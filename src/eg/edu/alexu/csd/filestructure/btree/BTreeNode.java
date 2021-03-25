package eg.edu.alexu.csd.filestructure.btree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BTreeNode<K extends Comparable<K>, V> implements IBTreeNode<K, V> {
	private List<K> keys = new ArrayList<K>();
	private List<V> values = new ArrayList<V>();
	private List<IBTreeNode<K,V>> children = new ArrayList<IBTreeNode<K,V>>();
	private int NumOfKeys;
	private boolean leaf = true;           //not sure to initialize it with true
	
	@Override
	public int getNumOfKeys() {
		NumOfKeys= keys.size();
		return NumOfKeys;
	}

	@Override
	public void setNumOfKeys(int numOfKeys) {       
		NumOfKeys= numOfKeys;                     //sure
	}

	@Override
	public boolean isLeaf() {
		return this.leaf;
	}

	@Override
	public void setLeaf(boolean isLeaf) {
		leaf= isLeaf;
	}

	@Override
	public List<K> getKeys() {
		return keys;
	}

	@Override
	public void setKeys(List<K> keys) {
		this.keys = keys;
	}

	@Override
	public List<V> getValues() {
		return values;
	}

	@Override
	public void setValues(List<V> values) {
		this.values = values;
	}

	@Override
	public List<IBTreeNode<K,V>> getChildren() {
		return children;
	}

	@Override
	public void setChildren(List<IBTreeNode<K,V>>  children) {
		this.children = children;
	}

}
