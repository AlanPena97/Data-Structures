package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	private void addChild(TagNode p, TagNode node) {
		TagNode headNode = new TagNode(null, null, p.firstChild);
		TagNode currNode = headNode;

		while (currNode.sibling != null)
			currNode = currNode.sibling;

		currNode.sibling = node;
		p.firstChild = headNode.sibling;
	}

	private TagNode build(TagNode p) {
		if (!this.sc.hasNext())
			return p;

		String line = this.sc.nextLine();
		if (line.charAt(0) == '<') {
			if (line.charAt(1) == '/')
				return p;

			String tagName = line.substring(1, line.length() - 1);
			addChild(p, build(new TagNode(tagName, null, null)));
		} else {
			addChild(p, new TagNode(line, null, null));
		}

		return build(p);
	}

	/**
	 * Builds the DOM tree from input HTML file, through scanner passed in to the
	 * constructor and stored in the sc field of this object.
	 * 
	 * The root of the tree that is built is referenced by the root field of this
	 * object.
	 */
	public void build() {
		TagNode tmpRoot = new TagNode("root", root, null);
		this.root = build(tmpRoot).firstChild;
	}
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		/** COMPLETE THIS METHOD **/
		
		rtag(oldTag, newTag, root);
	}
	
	private void rtag(String prevT, String currT, TagNode temp){
		if(temp == null){
			return;
		} else if (temp.tag.equals(prevT)){
			temp.tag = currT;
		}
		rtag(prevT, currT, temp.firstChild);
		rtag(prevT, currT, temp.sibling);
	}
	
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		/** COMPLETE THIS METHOD **/
		if(row<=0){
			return;
		}else{
			boldThis(row, root);
		}
	}
	
	private void boldThis(int r, TagNode n){
		if(n == null){
			return;
		}else if(n != null){
			TagNode rowt = n.firstChild;
			if (n.tag.equals("table")){
				for(int count = 1; count < r; count++ ) {
					if (n.firstChild.sibling != null){
						rowt = rowt.sibling;
						}
				}
				TagNode node2 = rowt.firstChild;
				if(node2!=null){
					while (node2 != null) {
						TagNode result = new TagNode("b", node2.firstChild, null);
						node2.firstChild = result;
						node2 = node2.sibling;
						continue;
					}
				}
			}
		}
		
		boldThis(r, n.firstChild);
		boldThis(r, n.sibling);

	}

	
	public void removeTag(String tag) {
		TagNode troot = new TagNode("root", root, null);
		this.root = remT(troot, tag).firstChild;
	}

	private TagNode remT(TagNode p, String rTag) {
		if (p == null)
			return null;

		if (p.tag.equals(rTag)) {
			if (p.tag.equals("ul") && rTag.equals("ul"))
				rtag("li", "p", p.firstChild);

			if (p.tag.equals("ol") && rTag.equals("ol"))
				rtag("li", "p", p.firstChild);

			TagNode childNodes = remT(p.firstChild, rTag);
			return insertItAfter(childNodes, remT(p.sibling, rTag));
		}

		p.firstChild = remT(p.firstChild, rTag);
		p.sibling = remT(p.sibling, rTag);

		return p;
	}

	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag  Tag to be added
	 */
	public void addTag(String word, String tag) {
		TagNode troot = new TagNode("root", root, null);
		addTagToNode(troot, word, tag);
		this.root = troot.firstChild;
	}

	private TagNode constructT(String t, String palabra, String theTag) {
		StringTokenizer st = new StringTokenizer(t, "\t ", true);
		String out = "";
		TagNode theHead = new TagNode(null, null, null);
		TagNode curr = theHead;

		while (st.hasMoreTokens()) {
			String tok = st.nextToken();
			tok = tok.toLowerCase();

			boolean beginsWith = tok.indexOf(palabra.toLowerCase()) == 0;
			if (beginsWith && tok.length() == palabra.length()
					|| beginsWith && tok.length() == palabra.length() + 1 && isItAPunc(tok.charAt(tok.length() - 1))) {

				if (out.length() > 0) {
					curr.sibling = new TagNode(out, null, null);
					curr = curr.sibling;
					out = "";
				}

				TagNode tnWrap = new TagNode(theTag, null, null);
				tnWrap.firstChild = new TagNode(tok, null, null);
				curr.sibling = tnWrap;
				curr = curr.sibling;

			} else {
				out += tok;
			}
		}

		if (out.length() > 0) {
			curr.sibling = new TagNode(out, null, null);
			curr = curr.sibling;
			out = "";
		}

		return theHead.sibling;
	}

	private TagNode insertItAfter(TagNode prev, TagNode next) {
		if (next == null && prev == null)
			return null;

		if (next == null)
			return prev;

		if (prev == null)
			return next;

		prev.sibling = insertItAfter(prev.sibling, next);
		return prev;
	}

	private boolean isItAPunc(char a) {
		return a == ',' || a == '!' || a == '.' || a == '?' || a == ';';
	}

	private boolean validTagChecker(String tag) {
		String[] validTags = { "html", "body", "p", "em", "b", "table", "tr", "td", "ol", "ul", "li" };

		for (String validTag : validTags)
			if (validTag.equals(tag))
				return true;

		return false;
	}

	private TagNode addTagToNode(TagNode parent, String word, String tag) {
		if (parent == null)
			return parent;

		if (parent.firstChild == null) {
			TagNode addedNode = constructT(parent.tag, word, tag);
			TagNode siblingNodes = addTagToNode(parent.sibling, word, tag);
			return insertItAfter(addedNode, siblingNodes);
		}

		parent.firstChild = addTagToNode(parent.firstChild, word, tag);
		parent.sibling = addTagToNode(parent.sibling, word, tag);

		return parent;
	}

	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
	/**
	 * Prints the DOM tree. 
	 *
	 */
	public void print() {
		print(root, 1);
	}
	
	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|---- ");
			} else {
				System.out.print("      ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
}