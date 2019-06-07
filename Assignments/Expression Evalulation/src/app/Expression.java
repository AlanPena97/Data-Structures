package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays){
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	StringTokenizer y = new StringTokenizer(expr, delims, true);
    	ArrayList<String> var = new ArrayList<>();
    	
    	while(y.hasMoreTokens()) {
    		var.add(y.nextToken());
    		}
    	
    	for(int i = 1; i < var.size(); i++) {
    		if(var.get(i).contains("[")){
    			if(arrayChecker(arrays, var.get(i-1)) == -1){
    				arrays.add(new Array(var.get(i-1)));
    			}
    		}else if(Pattern.matches("[a-zA-Z]+",var.get(i-1)) && arrayChecker(arrays, var.get(i-1)) == -1){
    			if(variableChecker(vars, var.get(i-1)) == -1){
    				vars.add(new Variable(var.get(i-1)));
    			}
    		}
    	}
    	if(Pattern.matches("[a-zA-Z]+",var.get(var.size()-1)) && arrayChecker(arrays, var.get(var.size()-1)) == -1){
    		if(variableChecker(vars, var.get(var.size()-1)) == -1){
    			vars.add(new Variable(var.get(var.size()-1)));
        	}
    	}
    }

 
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer strng = new StringTokenizer(sc.nextLine().trim());
            int nOfTokens = strng.countTokens();
            String x = strng.nextToken();
            Variable y = new Variable(x);
            Array holder = new Array(x);
            int z = vars.indexOf(y);
            int w = arrays.indexOf(holder);
            if (z == -1 && w == -1) {
            	continue;
            }
            int numHolder = Integer.parseInt(strng.nextToken());
            if (nOfTokens == 2) { 
                vars.get(z).value = numHolder;
            } else { 
            	holder = arrays.get(w);
            	holder.values = new int[numHolder];
                
                while (strng.hasMoreTokens()) {
                    x = strng.nextToken();
                    StringTokenizer stt = new StringTokenizer(x," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    holder.values[index] = val;              
                }
            }
        }
    }
   
    
    private static int arrayChecker(ArrayList<Array> a, String nombre){
    	for (int i = 0; i < a.size(); i++) {
    		if(a.get(i).name.equals(nombre)) {
    			return i;
    		}
    	}
    	return -1;
    }
    
    private static boolean ranCheck(String t, String first){
    	if((t.equals("*") || t.equals("/")) && (first.equals("+") || first.equals("-"))) {
    		return false;
    	}if(first.equals("(")) {
    		return false;
    	}if(first.equals("[")){
    		return false;
    	}
    	return true;
    	}
  
    
    private static boolean numberChecker(String input){
        try
        {
           Integer.parseInt(input);
           return true;
        }
        catch(Exception e)
        {
           return false;
        }
     }
    private static float valueGetter(ArrayList<Array> a, float v, String nombre) {
    	int index1= (int) v;
    	for(int i = 0; i < a.size(); i++){
    		if(a.get(i).name.equals(nombre)){
    			Array arrs = a.get(i);
    			return (float)arrs.values[index1];
    		}
    	}
    	return -1;
    }
    private static int variableChecker(ArrayList<Variable> v, String nombre){
    	for(int i = 0; i < v.size(); i++) {
    		if(v.get(i).name.equals(nombre)) {
    			return i;
    		}
    	}
    	return -1;
    		
    }
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	
    	Stack<Float> valor = new Stack<>();
    	Stack<String> op = new Stack<>();
    	StringTokenizer x = new StringTokenizer(expr, delims, true);
    	while(x.hasMoreTokens()) {
    		String current = x.nextToken();
    		if(current.equals(" ") || current.equals("\t")){
    		}else if(numberChecker(current)){
    			valor.push(Float.parseFloat(current));
    			continue;
    		}else if(variableChecker(vars, current) != -1){
    			valor.push((float)vars.get(variableChecker(vars,current)).value);
    		}else if(current.equals("(")){
    			op.push("(");
    		}else if(current.equals(")")){
    			while(op.peek() != "("){
    				float w2 = valor.pop();
    				float w1 = valor.pop();
    				String sign = op.pop();
    				switch(sign){
    					case "+": 
    						valor.push(w1 + w2);
    						continue;
    					case "-":
    						valor.push(w1 - w2);
    						continue;
    					case "*":
    						valor.push(w1 * w2);
    						continue;
    					case "/":
    						valor.push(w1 / w2);
    						continue;
    				}
    			}
    			op.pop();
    		}else if(current.equals("[")){
    			op.push("[");
    		}else if(arrayChecker(arrays, current) != -1){
    			op.push(current);
    		}else if(current.equals("]")) {
    			while(op.peek() != "["){
    				float y2 = valor.pop();
    				float y1 = valor.pop();
    				String s = op.pop();
    				switch(s){
    					case "+": 
    						valor.push(y1 + y2);
    						continue;
    					case "-":
    						valor.push(y1 - y2);
    						continue;
    					case "*":
    						valor.push(y1 * y2);
    						continue;
    					case "/":
    						valor.push(y1 / y2);
    						continue;
    				}
    			}
    			op.pop();
    			String arrNombre = op.pop();
    			float arrIndex = valor.pop();
    			valor.push(valueGetter(arrays, arrIndex, arrNombre));
    		}else if(current.equals("+") || current.equals("-") || current.equals("*") || current.equals("/")){//deals with operators and pemdas
    			while(op.isEmpty() != true && ranCheck(current, op.peek())){
    				float x2 = valor.pop();
    				float x1 = valor.pop();
    				String signa = op.pop();
    				switch(signa){
					case "+": 
						valor.push(x1 + x2);
						continue;
					case "-":
						valor.push(x1 - x2);
						continue;
					case "*":
						valor.push(x1 * x2);
						continue;
					case "/":
						valor.push(x1 / x2);
						continue;
				}
			}
    			op.push(current);
    		}
    	}
    	while(op.isEmpty() != true) {
    		float p2 = valor.pop();
			float p1 = valor.pop();
			String signal = op.pop();
			switch(signal){
			case "+": 
				valor.push(p1 + p2);
				continue;
			case "-":
				valor.push(p1 - p2);
				continue;
			case "*":
				valor.push(p1 * p2);
				continue;
			case "/":
				valor.push(p1 / p2);
				continue;
			}
    	}
    	return valor.pop();
    }
}