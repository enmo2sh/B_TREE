package eg.edu.alexu.csd.filestructure.btree;

import javax.management.RuntimeErrorException;
import java.util.ArrayList;
import java.util.List;

public class BTree <K extends Comparable<K>, V> implements IBTree {
	private IBTreeNode root;
	private int  minimumDegree;
	private int maxKeys;
	private int minKeys;
	private int flag;
	private List<K> keys = new ArrayList<K>();
	private List<V> values = new ArrayList<V>();
	private List<IBTreeNode<K,V>> children = new ArrayList<IBTreeNode<K,V>>();
	private List<K> keys2 = new ArrayList<K>();
	private List<V> values2 = new ArrayList<V>();
	private List<IBTreeNode<K,V>> children2 = new ArrayList<IBTreeNode<K,V>>();
	private IBTreeNode parent;
	BTree(int minimumDegree){
		if(minimumDegree<2)
			throw new RuntimeErrorException(null);
		this.minimumDegree=  minimumDegree;
		root=null;
		maxKeys = 2*minimumDegree - 1;
		minKeys=minimumDegree-1;
	}

	public void print(IBTreeNode<K, V> node, int j){
		if(node.getNumOfKeys()>0) {
			//System.out.println(j + ":" + node.getKeys());
			j++;
			for(int i=0; i<node.getChildren().size(); i++)
				print(node.getChildren().get(i), j);
		}

	}

	@Override
	public int getMinimumDegree() {
		return this.minimumDegree;
	}

	@Override
	public IBTreeNode getRoot() {
		if (root==null)
			return null;
		return root;
	}

	@Override
	public void insert(Comparable key, Object value) {
		if(key ==null || value == null)
			throw new RuntimeErrorException(null);

		if(search(key)==null){
			if (root==null) {
				root = new BTreeNode();
				keys.add((K) key);
				values.add((V) value);
				root.setKeys(keys);
				root.setValues(values);
				root.setNumOfKeys(1);
			}
			else{
				if(root.getNumOfKeys() == maxKeys)      //Full Node
					InsertFull(root, key, value);
				else
					InsertNotFull(root, key, value);
			}
		}
		//System.out.println("****************************");
		//search for that key

	}

	private void InsertFull(IBTreeNode<K, V> root2, Comparable key, Object value){
		root2 = Split(root2, null, 0);
		if(key.compareTo(getRoot().getKeys().get(0))<0)
			InsertNotFull(root2.getChildren().get(0), key, value);
		else
			InsertNotFull(root2.getChildren().get(1), key, value);
	}

	private void InsertNotFull(IBTreeNode<K, V> node, Comparable key, Object value){
		clearAdded();
		keys2 = node.getKeys();
		values2 = node.getValues();
		int no = node.getNumOfKeys();
		int i = no-1;
		while (i >= 0 && key.compareTo(node.getKeys().get(i))<0)
			i--;
		if(node.isLeaf()){
			keys2.add(i+1, (K) key);
			values2.add(i+1, (V) value);
			node.setKeys(keys2);
			node.setValues(values2);
			node.setNumOfKeys(no+1);
		}
		else {
			IBTreeNode prev = node;
			IBTreeNode child = node.getChildren().get(i+1);
			if(child.getNumOfKeys() == maxKeys) {
				Split(child, prev, i + 1);
				if(key.compareTo(node.getKeys().get(i+1))<0)
					child = node.getChildren().get(i+1);
				else
					child = node.getChildren().get(i+2);
			}
			InsertNotFull(child, key, value);
		}
	}

	private IBTreeNode Split(IBTreeNode node, IBTreeNode prev, int order){
		flag = 0;
		IBTreeNode left = new BTreeNode();
		IBTreeNode right = new BTreeNode();
		int no = node.getNumOfKeys();
		int mid = no/2;
		K midK = (K) node.getKeys().get(mid);
		V midV = (V) node.getValues().get(mid);
		getLRC(left, right, node, mid);
		clearAdded();
		if(prev == null){
			root = new BTreeNode();
			keys2.add(midK);
			values2.add(midV);
			children2.add(left);
			children2.add(right);
			root.setKeys(keys2);
			root.setValues(values2);
			root.setChildren(children2);
			root.setLeaf(false);
			root.setNumOfKeys(1);
			return root;
		}
		else {
			keys2 = prev.getKeys();
			values2 = prev.getValues();
			children2 = prev.getChildren();
			keys2.add(order, midK);
			values2.add(order, midV);
			children2.remove(order);
			children2.add(order, left);
			children2.add(order+1, right);
			prev.setKeys(keys2);
			prev.setValues(values2);
			prev.setChildren(children2);
			prev.setLeaf(false);
			prev.setNumOfKeys(keys2.size());
			return prev;
		}
	}

	private void getLRC(IBTreeNode left, IBTreeNode right, IBTreeNode node, int mid){
		keys = node.getKeys();
		values = node.getValues();
		children = node.getChildren();
		clearAdded();
		if(children.size()>0&&children.get(0).getNumOfKeys()>0)
			flag = 1;

		for(int i=0; i<mid; i++){
			keys2.add(keys.get(i));
			values2.add(values.get(i));
			if(flag==1)
				children2.add(children.get(i));
		}
		if(flag==1)
			children2.add(children.get(mid));

		left.setKeys(keys2);
		left.setValues(values2);
		left.setChildren(children2);
		left.setNumOfKeys(keys2.size());
		left.setLeaf(node.isLeaf());
		left = nullC(left);

		clearAdded();
		for(int i = mid+1; i<node.getNumOfKeys(); i++){
			keys2.add(keys.get(i));
			values2.add(values.get(i));
			if(flag==1)
				children2.add(children.get(i));
		}
		if(flag==1)
			children2.add(children.get(node.getNumOfKeys()));

		right.setKeys(keys2);
		right.setValues(values2);
		right.setChildren(children2);
		right.setNumOfKeys(keys2.size());
		right.setLeaf(node.isLeaf());
		right = nullC(right);
	}

	private IBTreeNode nullC(IBTreeNode node){
		List<IBTreeNode<K,V>> C = new ArrayList<>();
		C = node.getChildren();
		int max = node.getNumOfKeys() + 1;
		for(int i = C.size(); i<max; i++)
			C.add(new BTreeNode<>());
		node.setChildren(C);
		return node;
	}

	private void clearAdded(){
		keys2 = new ArrayList<>();
		values2 = new ArrayList<>();
		children2 = new ArrayList<>();
	}

	@Override
	public Object search(Comparable key) {
		// TODO Auto-generated method stub
		if(key==null){
			throw new RuntimeErrorException(null);
		}
		if(root==null){
			return null;
		}
		else{
			IBTreeNode temp=root;
			while (!temp.isLeaf()){
				if(temp.getKeys().contains(key)){
					return temp.getValues().get(temp.getKeys().indexOf(key));
				}
				else {
					List keys =temp.getKeys();
					for (int i=0;i<keys.size();i++){
						if((key.compareTo(keys.get(i))<0)){
							temp=(IBTreeNode) temp.getChildren().get(i);
							break;
						}
						else if ((i==keys.size()-1)){
							temp=(IBTreeNode) temp.getChildren().get(i+1);
						}
					}
				}
			}
			if(temp.getKeys().contains(key)){
				return temp.getValues().get(temp.getKeys().indexOf(key));
			}
			else{
				return null;
			}
		}
	}
	@Override
	public boolean delete(Comparable key) {
		boolean res=true;
		if(key==null ) {
			res=false;
			throw new RuntimeErrorException(null);
		}
		if(root==null || search(key)==null)
			res=false;
		if(res){
			if(getRoot().getKeys().contains(key))
				Delete(getRoot(), key);
			else
				Case3(key,getRoot());
		}
		return res;
	}

	private void Case3(Comparable key,IBTreeNode Node){
		if(Node!=getRoot() && Node.getKeys().size()==minKeys){
		    //System.out.println("Case3"+"   "+Node.getKeys());
			Node=NeedsKey(key,Node);
			}
		int idx=0;
		while(idx< Node.getKeys().size() && key.compareTo(Node.getKeys().get(idx))>0)
			idx++;
		parent=Node;
		Node=(IBTreeNode)Node.getChildren().get(idx);
		if(Node.getKeys().contains(key))
			Delete(Node,key);
		else
		    Case3(key, Node);
	}

	private void Delete(IBTreeNode T,Comparable key){
		int index=T.getKeys().indexOf(key);
		if(T.isLeaf()){
			if(T.getKeys().size()>minKeys) {
				//System.out.println("CAse 1.a");
				remove(T, key);
			}
			else{
				//System.out.println("CAse 1.b");
				if(T==getRoot())
					remove(T,key);
				else {
                    NeedsKey(key, T);
                    remove(T, key);
                }
			}
		}
		else if(!T.isLeaf()){
			//System.out.println("CAse2");
			IBTreeNode leftChild=(IBTreeNode)T.getChildren().get(index);
			IBTreeNode rightChild=(IBTreeNode)T.getChildren().get(index+1);
			if(leftChild.getKeys().size()>minKeys){
				Object key1=leftChild.getKeys().get(leftChild.getKeys().size()-1);
				Object value1=leftChild.getValues().get(leftChild.getValues().size()-1);
				T.getKeys().set(index,key1);
				T.getValues().set(index,value1);
				Delete(leftChild,(Comparable)key1);
			}
			else if(rightChild.getKeys().size()>minKeys){
				Object key1=rightChild.getKeys().get(0);
				Object value1=rightChild.getValues().get(0);
				T.getKeys().set(index,key1);
				T.getValues().set(index,value1);
				Delete(rightChild,(Comparable)key1);
			}
			else{
			   // System.out.println("merge");
				merge2(index,leftChild,rightChild,T);
				remove(leftChild,key);
				if(T==getRoot() && T.getKeys().size()==0)
					root=leftChild;
			}
		}
	}

	private void remove(IBTreeNode Node,Comparable key){
	    if(Node.getKeys().contains(key)) {
            Node.getValues().remove(Node.getKeys().indexOf(key));
            Node.getKeys().remove(key);
        }
	}

	private IBTreeNode NeedsKey(Comparable key,IBTreeNode temp){
		int index=parent.getChildren().indexOf(temp);
		IBTreeNode left ;
		if (index>0)
			left= (IBTreeNode) parent.getChildren().get(index - 1);
		else // no left sibling
			left=null;
		IBTreeNode right ;
		if(index<parent.getChildren().size()-1)
			right=(IBTreeNode)parent.getChildren().get(index+1);
		else // no right sibling
			right=null;
		if( left !=null && left.getKeys().size()>0 && left.getKeys().size() > minKeys) { //check if there is left child
				//System.out.println("Left said yes!!!");
				borrowFromPrev(temp,left,index);

		}
		else if(right!=null && right.getKeys().size()>0 && right.getKeys().size()>minKeys){// check if there is right sibling
				//System.out.println("Right Said Yes!!");
				borrowFromNext(temp,right,index);
		}
		else {
            //System.out.println("merge");
            boolean flag=false;
		    if(temp.getKeys().contains(key))
			    remove(temp,key);
		    else
		        flag=true;
			merge1(index, temp,left, right,key);
            if(parent==getRoot() && parent.getKeys().size()==0){
                if (right==null)
                    root=left;
                else
                    root=temp;
            }
            if(flag && right==null)
                temp = left;
		}
		return temp;
	}

	private void borrowFromPrev(IBTreeNode temp,IBTreeNode left,int index){
        Object key1 = left.getKeys().get(left.getKeys().size() - 1);
        Object value1 = left.getValues().get(left.getKeys().size() - 1);
        Object key2 = parent.getKeys().get(index - 1);
        Object value2 = parent.getValues().get(index - 1);
        parent.getKeys().set(index - 1, key1);
        parent.getValues().set(index - 1, value1);
        temp.getKeys().add(0, key2);
        temp.getValues().add(0, value2);
        if(!left.isLeaf()) {
            IBTreeNode lastChild = (IBTreeNode) left.getChildren().get(left.getChildren().size() - 1);
            temp.getChildren().add(0, lastChild);
            left.getChildren().remove(lastChild);
        }
        remove(left, (Comparable) key1);
    }

    private void borrowFromNext(IBTreeNode temp,IBTreeNode right,int index){
        Object key1=right.getKeys().get(0);
        Object value1=right.getValues().get(0);
        Object key2;
        Object value2;
        key2 = parent.getKeys().get(index);
        value2 = parent.getValues().get(index);
        parent.getKeys().set(index,key1);
        parent.getValues().set(index,value1);
        temp.getKeys().add(key2);
        temp.getValues().add(value2);
        if(!right.isLeaf()) {
            IBTreeNode firstChild = (IBTreeNode) right.getChildren().get(0);
            temp.getChildren().add(firstChild);
            right.getChildren().remove(firstChild);
        }
        remove(right,(Comparable)key1);
    }

	private void merge1(int index,IBTreeNode temp,IBTreeNode left,IBTreeNode right,Comparable key){
		if(right==null) {
		    //System.out.println("merge with left");
            merge2(index - 1, left, temp, parent);
        }
		else {
            //System.out.println("merge with right");
            merge2(index, temp, right, parent);
        }
	}

	private void merge2(int index,IBTreeNode Node1,IBTreeNode Node2,IBTreeNode parent){
		Node1.getKeys().add(parent.getKeys().get(index));
		Node1.getValues().add(parent.getValues().get(index));
		while(Node2.getKeys().size()>0){
			Node1.getKeys().add(Node2.getKeys().get(0));
			Node1.getValues().add(Node2.getValues().get(0));
			Node2.getKeys().remove(0);
			Node2.getValues().remove(0);
		}
		while(Node2.getChildren().size()>0){
			Node1.getChildren().add(Node2.getChildren().get(0));
			Node2.getChildren().remove(0);
		}
		parent.getChildren().remove(Node2);
		remove(parent,(Comparable)parent.getKeys().get(index));
	}

}