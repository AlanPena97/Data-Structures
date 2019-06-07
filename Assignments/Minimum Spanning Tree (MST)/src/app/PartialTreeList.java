package app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import structures.Arc;
import structures.Graph;
import structures.MinHeap;
import structures.PartialTree;
import structures.Vertex;

/**
 * Stores partial trees in a circular linked list
 * 
 */
public class PartialTreeList implements Iterable<PartialTree> {
    
	/**
	 * Inner class - to build the partial tree circular linked list 
	 * 
	 */
	public static class Node {
		/**
		 * Partial tree
		 */
		public PartialTree tree;
		
		/**
		 * Next node in linked list
		 */
		public Node next;
		
		/**
		 * Initializes this node by setting the tree part to the given tree,
		 * and setting next part to null
		 * 
		 * @param tree Partial tree
		 */
		public Node(PartialTree tree) {
			this.tree = tree;
			next = null;
		}
	}

	/**
	 * Pointer to last node of the circular linked list
	 */
	private Node end;
	
	/**
	 * Number of nodes in the CLL
	 */
	private int size;
	
	/**
	 * Initializes this list to empty
	 */
    public PartialTreeList() {
    	end = null;
    	size = 0;
    }

    /**
     * Adds a new tree to the end of the list
     * 
     * @param tree Tree to be added to the end of the list
     */
    public void append(PartialTree tree) {
    	Node ptr = new Node(tree);
    	if (end == null) {
    		ptr.next = ptr;
    	} else {
    		ptr.next = end.next;
    		end.next = ptr;
    	}
    	end = ptr;
    	size++;
    }

    /**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
		/* COMPLETE THIS METHOD */
		PartialTreeList finalResult = new PartialTreeList();
		for(int i = 0; i < graph.vertices.length; i++) {
			PartialTree c = new PartialTree(graph.vertices[i]);
			
			for(Vertex.Neighbor z = graph.vertices[i].neighbors; z != null; z = z.next){
				Arc e = new Arc(graph.vertices[i], z.vertex, z.weight);
				MinHeap<Arc> P = c.getArcs();
				P.insert(e);
			}
			finalResult.append(c);
		}
		return finalResult;
	}
	
	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * for that graph
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<Arc> execute(PartialTreeList ptlist) {
		/* COMPLETE THIS METHOD */
		ArrayList<Arc> finalRes = new ArrayList<>();
		while(ptlist.size() > 1){
			PartialTree p = ptlist.remove();
			MinHeap<Arc> q = p.getArcs();
			Arc first = null;
			Vertex secondV = null;
			while(true){
			first = q.deleteMin();
			secondV = first.getv2();
				if(!p.getRoot().equals(secondV.getRoot()) ) {
					break;
				}
			}
			finalRes.add(first);
			PartialTree t = ptlist.removeTreeContaining(secondV);
			p.merge(t);
			ptlist.append(p);
		}
		return finalRes;
	}
	/**
     * Removes the tree in this list that contains a given vertex.
     * 
     * @param vertex Vertex whose tree is to be removed
     * @return The tree that is removed
     * @throws NoSuchElementException If there is no matching tree
     */
    public PartialTree removeTreeContaining(Vertex vertex) 
    throws NoSuchElementException {
    		/* COMPLETE THIS METHOD */
    		if(end == null) {
    			throw new NoSuchElementException();
    		}
    		Node pointer = end.next; 
    		Node previous = end;
    		PartialTree finalResult = null;
    		if(pointer == previous && pointer.tree.getRoot().equals(vertex.getRoot())) {
    			finalResult = pointer.tree;
    			end = null;
    			size--;
    			return finalResult;
    		}
    		do {
    			if(pointer.tree.getRoot().equals(vertex.getRoot())){
    				if(pointer == end) {
    					end = previous;
    				}
    				finalResult = pointer.tree;
    				break;
    			}
    			previous = pointer;
    			pointer = pointer.next;
    		}while(pointer != end.next);
    		if(finalResult == null) {
    			throw new NoSuchElementException();
    		}
    		previous.next = pointer.next;
    		size--;
    		return finalResult;
     }
    /**
     * Removes the tree that is at the front of the list.
     * 
     * @return The tree that is removed from the front
     * @throws NoSuchElementException If the list is empty
     */
    public PartialTree remove() 
    throws NoSuchElementException {
    			
    	if (end == null) {
    		throw new NoSuchElementException("list is empty");
    	}
    	PartialTree ret = end.next.tree;
    	if (end.next == end) {
    		end = null;
    	} else {
    		end.next = end.next.next;
    	}
    	size--;
    	return ret;
    		
    }

    /**
     * Gives the number of trees in this list
     * 
     * @return Number of trees
     */
    public int size() {
    	return size;
    }
    
    /**
     * Returns an Iterator that can be used to step through the trees in this list.
     * The iterator does NOT support remove.
     * 
     * @return Iterator for this list
     */
    public Iterator<PartialTree> iterator() {
    	return new PartialTreeListIterator(this);
    }
    
    private class PartialTreeListIterator implements Iterator<PartialTree> {
    	
    	private PartialTreeList.Node ptr;
    	private int rest;
    	
    	public PartialTreeListIterator(PartialTreeList target) {
    		rest = target.size;
    		ptr = rest > 0 ? target.end.next : null;
    	}
    	
    	public PartialTree next() 
    	throws NoSuchElementException {
    		if (rest <= 0) {
    			throw new NoSuchElementException();
    		}
    		PartialTree ret = ptr.tree;
    		ptr = ptr.next;
    		rest--;
    		return ret;
    	}
    	
    	public boolean hasNext() {
    		return rest != 0;
    	}
    	
    	public void remove() 
    	throws UnsupportedOperationException {
    		throw new UnsupportedOperationException();
    	}
    	
    }
}
