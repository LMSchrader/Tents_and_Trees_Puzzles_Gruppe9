import java.util.List;
import java.util.Random;
import java.util.Stack;

public class Tents_and_Trees_Solver {
	private Puzzle puzzle; // saves the unsolved puzzle with preprocessing
	private Puzzle solvedPuzzle; // saves the solved or partially solved puzzle ()
	
	private Stack<Node> currentPath = new Stack<>(); // stack for backtrack
	private Node currentNode;
	
	
	public Tents_and_Trees_Solver(String fileName) {
		puzzle = new Puzzle(fileName);
		csp();
	}
	
	//  constraint satisfaction procedure
	private void csp() {
		puzzle.printPuzzle();
		preprocessing();
		
//		TODO: define variables (in currentNode) and constraints
		currentNode = new Node();
//		TODO: preprocessing % e.g. arc-consistency
		while (currentNode.hasUninstantiatedTrees()) {
			// create a new node based on the old currentNode to build path (currentPath)
			currentPath.push(currentNode);
			Node newNode = new Node(currentNode);
			currentNode = newNode;
			
			Tree t = selectTree(); // select variable
			if (t.getDomain().isEmpty()) {
				backtrack();
				generateNewSolvedPuzzle();
			} else {
				int[] tentPos = selectTent(t);//select consistent value
				t.setCurrentTentPosition(tentPos);
				updateSolvedPuzzle();
				constraintPropagation(); // propagation
			}
		}
	}
	
	
	public void preprocessing() {
		markZeroes();
		setGrassForSquaresWithNoAvailableTree();
		//preprocessing3();
	}
	
	// preprocessing 1
	public void markZeroes() {
		String[][] p = puzzle.getPuzzle();

		for (int i = 0; i < puzzle.getColumns(); i++) {
			if(p[0][i].equals("0")) {
				for(int a = 1; a < puzzle.getRows(); a++) {
					if (!p[a][Integer.valueOf(i)].equals("t")) {
						p[a][Integer.valueOf(i)] = "g";
					}
				}
			}
		}
		
		for (int i = 0; i < puzzle.getRows(); i++) {
			if(p[i][0].equals("0")) {
				for(int a = 1; a < puzzle.getColumns(); a++) {
					if (!p[Integer.valueOf(i)][a].equals("t")) {
						p[Integer.valueOf(i)][a] = "g";
					}
				}
			}
		}
		
		puzzle.printPuzzle();
	}
	
	
	// preprocessing 2
	public void setGrassForSquaresWithNoAvailableTree() {
		String[][] p = puzzle.getPuzzle();
		for (int i = 1; i < puzzle.getRows(); i++) {
			for (int j = 1; j < puzzle.getColumns(); j++) {
				if (!p[i][j].equals("")) {
						continue;
				}
				boolean treeAbove = false;
				if (i > 1) {
					if (p[i-1][j].equals("t")) {
						treeAbove = true;
					}
				}
				boolean treeBelow = false;
				if (i < puzzle.getRows() - 1) {
					if (p[i+1][j].equals("t")) {
						treeBelow = true;
					}
				}
				boolean treeToTheLeft = false;
				if (j > 1) {
					if (p[i][j-1].equals("t")) {
						treeToTheLeft = true;
					}
				}
				boolean treeToTheRight = false;
				if (j < puzzle.getColumns() - 1) {
					if (p[i][j+1].equals("t")) {
						treeToTheRight = true;
					}
				}
				if (treeToTheLeft || treeToTheRight || treeAbove || treeBelow) {
					continue;
				}
				p[i][j] = "g";
			}
		}
		
		puzzle.printPuzzle();
	}

	
	//TODO: preprocessing 3 funktioniert nicht mit aktueller Datenstruktur
	//preprocessing 3
	public void preprocessing3() {
		for (int i = 1; i < puzzle.getRows(); i++) {
			int numberOfMinssingTents = puzzle.numberOfTentsThatShouldBeInRow(i) - puzzle.countNumberOfXInRow(i, "^");
			if (puzzle.countNumberOfXInRow(i, "") == numberOfMinssingTents) {
				puzzle.fillUnknownFieldsofRowWithX(i, "^");
			}
		}
		
		for (int i = 1; i < puzzle.getColumns(); i++) {
			int numberOfMinssingTents = puzzle.numberOfTentsThatShouldBeInCollumn(i) - puzzle.countNumberOfXInColumn(i, "^");
			if (puzzle.countNumberOfXInColumn(i, "") == numberOfMinssingTents) {
				puzzle.fillUnknownFieldsofColumnWithX(i, "^");
			}
		}
		
		puzzle.printPuzzle();
	}

	
	public Tree selectTree() {
		//TODO: other heuristics
		// random tree
		List<Tree> trees = currentNode.getUninstantiatedTrees();
		Tree tree = trees.get(new Random().nextInt((trees.size() - 1) + 1));
		return tree;
	}
	
	public int[] selectTent(Tree tree) {
		//TODO: other heuristics
		// random tent
		List<int[]> tentPos = tree.getDomain();
		return tentPos.get(new Random().nextInt((tentPos.size() - 1) + 1));
	}
	
	
	public void backtrack() {
		int[] treePos;
		int[] tentPos;
		
		do {
			treePos = currentNode.getUpdatedTree().getPosition();
			tentPos = currentNode.getUpdatedTree().getCurrentTentPosition();
			
			// set previous node as current node and delete tent position from the domain and 
			currentNode = currentPath.pop();
			currentNode.getTree(treePos).deleteFromDomain(tentPos);
		} while (currentNode.getTree(treePos).getDomain().isEmpty());
	}
	
	public void constraintPropagation() {
		//TODO
	}
	
	// after a new tent was set
	public void updateSolvedPuzzle() {
		//TODO
		//solvedPuzzle = ...
	}
	
	// after backtrack
	public void generateNewSolvedPuzzle() {
		//TODO
		//solvedPuzzle = ...
	}

}
