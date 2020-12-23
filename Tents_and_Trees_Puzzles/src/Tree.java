import java.util.ArrayList;
import java.util.List;

public class Tree {
	private final int[] position; 
	private List<int[]> domain; // hint for backtrack: if we detect a 'dead-end', delete currentTentPosition in domain
	private int[] currentTentPosition = null;

	public Tree(int[] pos, List<int[]> domain) {
		this.position = pos;
		this.domain = domain;
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
	
	public Tree clone() {
		List<int[]> d = new ArrayList<>();
		for (int[] p : domain ) {
			d.add(p);
		}
		Tree t = new Tree(position, d);
		t.setCurrentTentPosition(currentTentPosition);
		return t;
	}
}
