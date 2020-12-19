import java.util.ArrayList;
import java.util.List;

public class Tree {
	private int[] position; 
	private List<int[]> domain = new ArrayList<>(); // hint for backtrack: if we detect a 'dead-end', delete currentTentPosition in domain
	private int[] currentTentPosition = null;

	public Tree(int[] pos) {
		position = pos;
	}
}
