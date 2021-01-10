import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {
	private Map<int[], Tree> allTrees; // key: tree position, value: tree
	private Tree updatedTree; // for backtrack
	
	// only first node
	public Node(Map<int[], Tree> allTrees) {
		this.allTrees = allTrees;
	}
	
	public Node(Node priviousNode) {
		cloneAllTrees(priviousNode);
	}
	
	
	public boolean hasUninstantiatedTrees() {
		for (Map.Entry<int[], Tree> entry : allTrees.entrySet()) {
			if (entry.getValue().getCurrentTentPosition() == null) {
				return true;
			}
		}
		return false;
	}
	
	public List<Tree> getUninstantiatedTrees() {
		List<Tree> treeList = new ArrayList<>();
		for (Map.Entry<int[], Tree> entry : allTrees.entrySet()) {
			if (entry.getValue().getCurrentTentPosition() == null) {
				treeList.add(entry.getValue());
			}
		}
		return treeList;
	}
	
	private void cloneAllTrees(Node n) {
		allTrees = new HashMap<>();
		for (Map.Entry<int[], Tree> entry : n.getAllTrees().entrySet()) {
			allTrees.put(entry.getKey(), entry.getValue().clone());
		}
	}
	
	public Node clone() {
		Node n = new Node(this);
		n.setUpdatedTree(n.getTree(this.getUpdatedTree().getPosition()));
		return n;
	}

	public Map<int[], Tree> getAllTrees() {
		return allTrees;
	}
	
	public Tree getTree(int[] pos) {
		return allTrees.get(pos);
	}

	public Tree getUpdatedTree() {
		return updatedTree;
	}
	
	public void update(Tree tree, int[] tentPos) {
		this.updatedTree = tree;
		tree.setCurrentTentPosition(tentPos);
		tree.deleteFromDomain(tentPos);
	}
	
	public void undoUpdate() {
		this.updatedTree.setCurrentTentPosition(null);
		this.updatedTree = null;
	}
	
	private void setUpdatedTree(Tree tree) {
		this.updatedTree = tree;
	}
}
