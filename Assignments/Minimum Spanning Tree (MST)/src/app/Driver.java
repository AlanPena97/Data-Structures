package app;


import structures.Graph;
import structures.PartialTree;

import java.io.IOException;
import java.util.ArrayList;
public class Driver{
	public static void main(String[] args){
        Graph g = null;
        try {
            g = new Graph("test3.txt");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        PartialTreeList mst = new PartialTreeList();
        mst = mst.initialize(g);
        System.out.println(mst.execute(mst));
    }
}
