import java.util.ArrayList;
import java.util.List;

public class Tree {
	private int[] position; 
	private List<int[]> domain = new ArrayList<>(); // hint for backtrack: if we detect a 'dead-end', delete currentTentPosition in domain
	private int[] currentTentPosition = null;

	public Tree(int[] pos) {
		position = pos;
	}

	public int[] getPosition() {
		return position;
	}

	public void deleteFromDomain(int[] tentPos) {
		domain.remove(tentPos);
	}
	
	public List<int[]> getDomain() {
		return domain;
	}

	public int[] getCurrentTentPosition() {
		return currentTentPosition;
	}

	public void setCurrentTentPosition(int[] currentTentPosition) {
		this.currentTentPosition = currentTentPosition;
	}
}
