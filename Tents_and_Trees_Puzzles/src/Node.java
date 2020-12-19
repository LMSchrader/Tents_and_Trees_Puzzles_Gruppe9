import java.util.HashMap;
import java.util.Map;

public class Node {
	private Map<int[], Tree> allTrees = new HashMap<>(); // key: tree position, value: tree
	private Tree updatedTree; // for backtrack
	
	// only first node
	public Node() {
		//TODO
	}
	
	public Node(Node priviousNode) {
		cloneAllTrees(priviousNode);
	}
	
	private void cloneAllTrees(Node n) {
		// TODO
	}
}
