import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

public class Tents_and_Trees_Solver {
	private Puzzle puzzle;	
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
		
		currentNode = createFirstNode(); // define variables 
		// TODO: define constraints
		
		//	TODO: preprocessing % e.g. arc-consistency
		while (currentNode.hasUninstantiatedTrees()) {
			Tree t = selectTree(); // select variable
			if (t.getDomain().isEmpty()) {
				backtrack();
			} else {
				int[] tentPos = selectTent(t); //select consistent value
				currentNode.update(t, tentPos);
				updateSolvedPuzzle();
				constraintPropagation(); // propagation
				
				// create a new node based on the old currentNode to build path (currentPath)
				currentPath.push(currentNode);
				currentNode = new Node(currentNode);
			}	
		}
		puzzle.printPuzzle();
	}
	
	
	private void preprocessing() {
		markZeroes();
		setGrassForSquaresWithNoAvailableTree();
		//preprocessing3();
	}
	
	// preprocessing 1
	private void markZeroes() {
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
	private void setGrassForSquaresWithNoAvailableTree() {
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
	private void preprocessing3() {
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

	
	private Tree selectTree() {
		//TODO: other heuristics
		// random tree
		List<Tree> trees = currentNode.getUninstantiatedTrees();
		Tree tree = trees.get(new Random().nextInt((trees.size() - 1) + 1));
		return tree;
	}
	
	private int[] selectTent(Tree tree) {
		//TODO: other heuristics
		// random tent
		List<int[]> tentPos = tree.getDomain();
		return tentPos.get(new Random().nextInt((tentPos.size() - 1) + 1));
	}
	
	
	private void backtrack() {
		int[] treePos;
		
		do {
			// set previous node as current node and delete tent position from the domain of the updated tree
			currentNode = currentPath.pop();
			treePos = currentNode.getUpdatedTree().getPosition();
			undoUpdateSolvedPuzzle();
			currentNode.undoUpdate();
		} while (currentNode.getTree(treePos).getDomain().isEmpty());
	}
	
	private void constraintPropagation() {
		//TODO
	}
	
	// after a new tent was set
	private void updateSolvedPuzzle() {
		int[] tentPos= currentNode.getUpdatedTree().getCurrentTentPosition();
		puzzle.getPuzzle()[tentPos[0]][tentPos[1]] = "^";
	}
	
	// within backtrack
	private void undoUpdateSolvedPuzzle() {
		int[] tentPos= currentNode.getUpdatedTree().getCurrentTentPosition();
		puzzle.getPuzzle()[tentPos[0]][tentPos[1]] = "";
	}
	
	private Node createFirstNode() {
		Map<int[], Tree> allTrees = new HashMap<>();
		String[][] p = puzzle.getPuzzle();
		
		for (int r = 1; r < puzzle.getRows(); r++) {
			for (int c = 1; c < puzzle.getColumns(); c++) {
				String s = p[r][c];
				if (s.equals("t")) {
					int[] pos = new int[] {r, c};
					List<int[]> domain = defineDomain(pos); 
					Tree t = new Tree(pos, domain);
					
					allTrees.put(pos, t);
				
				}

			}
		}
		
		return new Node(allTrees);
	}
	
	private List<int[]> defineDomain(int[] posTree) {
		List<int[]> domain = new ArrayList<>();
		String[][] p = puzzle.getPuzzle();
		int r = posTree[0];
		int c = posTree[1];
		
		if (r > 1) {
			if (p[r-1][c].equals("")) {
				domain.add(new int[] {r-1, c});
			}
		}
		if (r < puzzle.getRows() - 1) {
			if (p[r+1][c].equals("")) {
				domain.add(new int[] {r+1, c});
			}
		}
		if (c > 1) {
			if (p[r][c-1].equals("")) {
				domain.add(new int[] {r, c-1});
			}
		}
		if (c < puzzle.getColumns() - 1) {
			if (p[r][c+1].equals("")) {
				domain.add(new int[] {r, c+1});
			}
		}
		
		return domain;
	}

}
