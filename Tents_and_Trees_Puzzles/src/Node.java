import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {
	// TODO: es kann sein, dass int[] probleme bereiten wird
	private Map<int[], Tree> allTrees = new HashMap<>(); // key: tree position, value: tree
	private Tree updatedTree; // for backtrack
	
	// only first node
	public Node() {
		//TODO
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
		// TODO
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
}
