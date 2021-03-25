package eg.edu.alexu.csd.filestructure.btree;

import java.util.ArrayList;
import java.util.List;

public class main {

	public static <K extends Comparable<K>, V> void main(String[] args) {
		IBTreeNode<K,V> t = new BTreeNode();
	    List keys = new ArrayList();
	    keys.add(1);
	    keys.add(2);
	    keys.add(4);
	    t.setKeys(keys);
	    List<IBTreeNode<K,V>> children = new ArrayList<IBTreeNode<K,V>>();
	    BTreeNode cht = new BTreeNode();
	    List chkeys = new ArrayList();
	    chkeys.add(3);
	    chkeys.add(2);
	    chkeys.add(4);
	    cht.setKeys(chkeys);
	    children.add(cht);
	    t.setChildren(children);
	    IBTreeNode child = t.getChildren().get(0);
	    BTree  i = new BTree (9);
	    System.out.println(i.getRoot());

	}

}
