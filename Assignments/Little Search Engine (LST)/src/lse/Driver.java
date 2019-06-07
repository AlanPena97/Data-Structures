package lse;

import java.io.*;
import java.util.*;

//import javax.print.attribute.DocAttributeSet;

public class Driver {
	static Scanner sc = new Scanner(System.in);
	
	static String getOption() 
	{
		System.out.print("getKeyWord(): ");
		String response = sc.next();
		return response;
	}
	
	public static void main(String args[]) throws FileNotFoundException
	{
		/*Scanner sc = new Scanner(new File("AliceCh1.txt"));
		LittleSearchEngine s = new LittleSearchEngine();
		 while(sc.hasNext()) {
			String temp = s.getKeyword(sc.next());
			System.out.println(temp);
		 }*/
		ArrayList<Occurrence> trial = new ArrayList<>();
		Occurrence temp = new Occurrence("AliceCh1.txt", 12);
		trial.add(temp);
		Occurrence temp1 = new Occurrence("AliceCh1.txt", 55);
		trial.add(temp1);
		Occurrence temp2 = new Occurrence("AliceCh1.txt", 7);
		trial.add(temp2);
		Occurrence temp3 = new Occurrence("AliceCh1.txt", 5);
		trial.add(temp3);
		Occurrence temp4 = new Occurrence("AliceCh1.txt", 3);
		trial.add(temp4);
		Occurrence temp5 = new Occurrence("AliceCh1.txt", 2);
		trial.add(temp5);
		Occurrence temp6 = new Occurrence("AliceCh1.txt", 6);
		trial.add(temp6);
		Occurrence temp7 = new Occurrence("AliceCh1.txt", 4);
		trial.add(temp7);
		
		LittleSearchEngine lse = new LittleSearchEngine();
		
		try{
			lse.makeIndex("docs.txt", "noisewords.txt");
		} catch (FileNotFoundException e){
			System.out.println("Hello World");
		}		
		
		HashMap<String, ArrayList<Occurrence>> hashM = lse.keywordsIndex;
		for(String w : hashM.keySet()) {
			System.out.println("Key: " + w);
			ArrayList<Occurrence> temp10 = hashM.get(w);
			for(int j = 0; j < temp10.size(); j++) {
				//System.out.println("Key: " + w);
				System.out.println(temp10.get(j));
			}
		}
		System.out.println("I am here");
		System.out.println(lse.top5search("red", "orange"));
	}
}