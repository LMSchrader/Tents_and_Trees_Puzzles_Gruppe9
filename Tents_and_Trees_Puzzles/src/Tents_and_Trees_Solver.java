import java.util.*;
import java.util.Map.Entry;

public class Tents_and_Trees_Solver {
	private Puzzle puzzle;	
	private Puzzle updatedPuzzle;
	private Stack<Node> currentPath = new Stack<>(); // stack for backtrack
	private Node currentNode;
	
	public static int BACKTRACKCOUNT = 0;
	
	
	public Tents_and_Trees_Solver(String fileName) {
		this.puzzle = new Puzzle(fileName);
		csp();
	}
	
	//  constraint satisfaction procedure
	private void csp() {
		puzzle.printPuzzle();
		preprocessing();
		
		currentNode = createFirstNode(); // define variables 
		
		//	TODO: preprocessing % e.g. arc-consistency
		
		this.updatedPuzzle = this.puzzle.clone();
		
		while (currentNode.hasUninstantiatedTrees()) {
			Tree t = selectTree(); // select variable

			//int[] tentPos = selectTent(t);//select consistent value
			int[] tentPos = selectTentMaxNumber(t);
			if (tentPos == null) { // if domain is empty
				backtrack();
				createPuzzleFromNode(this.currentNode);
//				System.out.println("BACKTRACK");
//				updatedPuzzle.printPuzzle();
			} else {
				currentNode.update(t, tentPos);
				updatePuzzle();
				constraints(updatedPuzzle);
//				System.out.println("UPDATE");
// 				updatedPuzzle.printPuzzle();
				constraintPropagation(); // propagation
				
				// create a new node based on the old currentNode to build path (currentPath)
				currentPath.push(currentNode);
				currentNode = new Node(currentNode);
			}	
		}
		System.out.println("RESULT");
		updatedPuzzle.printPuzzle();
		System.out.println("BACKTRACKCOUNT:" + BACKTRACKCOUNT);
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
	
	private void preprocessing() {
		puzzle.markZeroes();
//		puzzle.printPuzzle();
		puzzle.setGrassForSquaresWithNoAvailableTree();
//		puzzle.printPuzzle();
		//preprocessing3();
		puzzle.printPuzzle();
	}
	
	private Tree selectTree() {
		//TODO: other heuristics
		// random tree
		List<Tree> trees = currentNode.getUninstantiatedTrees();
		Tree tree = trees.get((int)(Math.random() * ((trees.size() - 1) + 1)));
		return tree;
	}
	
	private int[] selectTent(Tree tree) {
		//TODO: other heuristics
		// random tent
		int domainSize = tree.getDomain().size();
		int[] tent;
		for (int i = 0; i < domainSize; i++) {
			tent = tree.getDomain().get((int)(Math.random() * ((tree.getDomain().size() - 1) + 1)));
			if (updatedPuzzle.getPuzzle()[tent[0]][tent[1]].equals("")) { // if value is consistent
				return tent;
			} else {
				tree.deleteFromDomain(tent);
			}
		}
		return null;
	}

	private int[] selectTentMaxNumber(Tree tree) {
		String[][] p = puzzle.getPuzzle();

		int row = tree.getPosition()[0];
		int column = tree.getPosition()[1];

		//if tree is in top left corner
		if(row == 1 && column == 1) {
			if(Integer.parseInt(p[row + 1][0]) > Integer.parseInt(p[0][column + 1])) {
				return new int[] {row + 1, column};
			} else {
				return new int[] {row, column + 1};
			}
		}

		//if tree is in top right corner
		if(row == 1 && column == puzzle.getColumns() - 1) {
			if(Integer.parseInt(p[row + 1][0]) > Integer.parseInt(p[0][column - 1])) {
				return new int[] {row + 1, column};
			} else {
				return new int[] {row, column - 1};
			}
		}

		//if tree is in bottom left corner
		if(row == puzzle.getRows() - 1 && column == 1) {
			if(Integer.parseInt(p[row - 1][0]) > Integer.parseInt(p[0][column + 1])) {
				return new int[] {row - 1, column};
			} else {
				return new int[] {row, column + 1};
			}
		}

		//if tree is in bottom right corner
		if(row == puzzle.getRows() - 1 && column == puzzle.getColumns() - 1) {
			if(Integer.parseInt(p[row - 1][0]) > Integer.parseInt(p[0][column - 1])) {
				return new int[] {row - 1, column};
			} else {
				return new int[] {row, column - 1};
			}
		}

		if(row == 1) {
			if(Integer.parseInt(p[0][column - 1]) > Integer.parseInt(p[0][column + 1])) {
				return Integer.parseInt(p[row + 1][0]) > Integer.parseInt(p[0][column - 1]) ? new int[] {row + 1, column} : new int[] {row, column - 1};
			} else {
				return Integer.parseInt(p[row + 1][0]) > Integer.parseInt(p[0][column + 1]) ? new int[] {row + 1, column} : new int[] {row, column + 1};
			}
		}

		if(row == puzzle.getRows() - 1) {
			if(Integer.parseInt(p[0][column - 1]) > Integer.parseInt(p[0][column + 1])) {
				return Integer.parseInt(p[row - 1][0]) > Integer.parseInt(p[0][column - 1]) ? new int[] {row - 1, column} : new int[] {row, column - 1};
			} else {
				return Integer.parseInt(p[row - 1][0]) > Integer.parseInt(p[0][column + 1]) ? new int[] {row - 1, column} : new int[] {row, column + 1};
			}
		}

		if(column == 1) {
			if(Integer.parseInt(p[row - 1][0]) > Integer.parseInt(p[row + 1][0])) {
				return Integer.parseInt(p[row - 1][0]) > Integer.parseInt(p[0][column + 1]) ? new int[] {row - 1, column} : new int[] {row, column + 1};
			} else {
				return Integer.parseInt(p[row + 1][0]) > Integer.parseInt(p[0][column + 1]) ? new int[] {row + 1, column} : new int[] {row, column + 1};
			}
		}

		if(column == puzzle.getColumns() - 1) {
			if(Integer.parseInt(p[row - 1][0]) > Integer.parseInt(p[row + 1][0])) {
				return Integer.parseInt(p[row - 1][0]) > Integer.parseInt(p[0][column - 1]) ? new int[] {row - 1, column} : new int[] {row, column - 1};
			} else {
				return Integer.parseInt(p[row + 1][0]) > Integer.parseInt(p[0][column - 1]) ? new int[] {row + 1, column} : new int[] {row, column - 1};
			}
		}


		if(Integer.parseInt(p[row - 1][0]) > Integer.parseInt(p[row + 1][0])) {
			if(Integer.parseInt(p[0][column - 1]) > Integer.parseInt(p[0][column + 1])) {
				return Integer.parseInt(p[row - 1][0]) > Integer.parseInt(p[0][column - 1]) ? new int[] {row - 1, column} : new int[] {row, column - 1};
			} else {
				return Integer.parseInt(p[row - 1][0]) < Integer.parseInt(p[0][column + 1]) ? new int[] {row - 1, column} : new int[] {row, column + 1};
			}
		} else {
			if(Integer.parseInt(p[0][column - 1]) > Integer.parseInt(p[0][column + 1])) {
				return Integer.parseInt(p[row + 1][0]) > Integer.parseInt(p[0][column - 1]) ? new int[] {row + 1, column} : new int[] {row, column - 1};
			} else {
				return Integer.parseInt(p[row + 1][0]) < Integer.parseInt(p[0][column + 1]) ? new int[] {row + 1, column} : new int[] {row, column + 1};
			}
		}
	}
	
	
	private void backtrack() {
		int[] treePos;
		
		do {
			// set previous node as current node and delete tent position from the domain of the updated tree
			currentNode = currentPath.pop();
			treePos = currentNode.getUpdatedTree().getPosition();
			currentNode.undoUpdate();
			BACKTRACKCOUNT++;
		} while (currentNode.getTree(treePos).getDomain().isEmpty());
	}
	
	private void constraintPropagation() {
		//TODO
	}
	
	private void constraints(Puzzle puzzle) {
		setGrassAroundASquareWithATent(puzzle);
		fillRowsAndCollumnsWithGrassIfAllTentsHaveBeenSet(puzzle);
	}
	
	// constraint:  No two tents may stand immediately next to each other, not even diagonally. 
	private void setGrassAroundASquareWithATent(Puzzle puzzle) {
		String[][] p = puzzle.getPuzzle();
		for (int i = 1; i < puzzle.getRows(); i++) {
			for (int j = 1; j < puzzle.getColumns(); j++) {
				if (!p[i][j].equals("")) {
					continue;
				}
				
				if (i > 1) {
					if (p[i-1][j].equals("^")) {
						p[i][j] = "g";
						continue;
					}
				}
				
				if (i < puzzle.getRows() - 1) {
					if (p[i+1][j].equals("^")) {
						p[i][j] = "g";
						continue;
					}
				}
				
				if (j > 1) {
					if (p[i][j-1].equals("^")) {
						p[i][j] = "g";
						continue;
					}
				}
				
				if (j < puzzle.getColumns() - 1) {
					if (p[i][j+1].equals("^")) {
						p[i][j] = "g";
						continue;
					}
				}
				
				if (i > 1 && j > 1) {
					if (p[i-1][j-1].equals("^")) {
						p[i][j] = "g";
						continue;
					}
				}
				
				if (i < puzzle.getRows() - 1 && j > 1) {
					if (p[i+1][j-1].equals("^")) {
						p[i][j] = "g";
						continue;
					}
				}
				
				if (i < puzzle.getRows() - 1 && j < puzzle.getColumns() - 1) {
					if (p[i+1][j+1].equals("^")) {
						p[i][j] = "g";
						continue;
					}
				}
				
				if (i > 1 && j < puzzle.getColumns() - 1) {
					if (p[i-1][j+1].equals("^")) {
						p[i][j] = "g";
						continue;
					}
				}
				
			}
		}
	}
	
	// constraint:  There are exactly as many tents in each row or column as the number on the side indicates. 
	private void fillRowsAndCollumnsWithGrassIfAllTentsHaveBeenSet(Puzzle puzzle) {
		for (int i = 1; i < puzzle.getRows(); i++) {
			if (puzzle.countNumberOfXInRow(i, "^") >= puzzle.numberOfTentsThatShouldBeInRow(i)) {
				puzzle.fillUnknownFieldsofRowWithX(i, "g");
			}
		}
		
		for (int j = 1; j < puzzle.getColumns(); j++) {
			if (puzzle.countNumberOfXInColumn(j, "^") >= puzzle.numberOfTentsThatShouldBeInColumn(j)) {
				puzzle.fillUnknownFieldsofColumnWithX(j, "g");
			}
		}
	}
	
	//TODO: preprocessing 3 funktioniert nicht mit aktueller Datenstruktur
	private void preprocessing3() {
		for (int i = 1; i < puzzle.getRows(); i++) {
			int numberOfMinssingTents = puzzle.numberOfTentsThatShouldBeInRow(i) - puzzle.countNumberOfXInRow(i, "^");
			if (puzzle.countNumberOfXInRow(i, "") == numberOfMinssingTents) {
				puzzle.fillUnknownFieldsofRowWithX(i, "^");
			}
		}
		
		for (int i = 1; i < puzzle.getColumns(); i++) {
			int numberOfMinssingTents = puzzle.numberOfTentsThatShouldBeInColumn(i) - puzzle.countNumberOfXInColumn(i, "^");
			if (puzzle.countNumberOfXInColumn(i, "") == numberOfMinssingTents) {
				puzzle.fillUnknownFieldsofColumnWithX(i, "^");
			}
		}
		
		puzzle.printPuzzle();
	}
	
	// after a new tent was set
	private void updatePuzzle() {
		int[] tentPos= currentNode.getUpdatedTree().getCurrentTentPosition();
		updatedPuzzle.getPuzzle()[tentPos[0]][tentPos[1]] = "^";
	}
	
	// within backtrack
//	private void undoUpdateSolvedPuzzle() {
//		int[] tentPos= currentNode.getUpdatedTree().getCurrentTentPosition();
//		puzzle.getPuzzle()[tentPos[0]][tentPos[1]] = "";
//	}
	
	// after backtrack
	private void createPuzzleFromNode(Node node) {
		this.updatedPuzzle = puzzle.clone();
		String[][] p = this.updatedPuzzle.getPuzzle();
		
		for (Entry<int[], Tree> s : node.getAllTrees().entrySet()) {
			int[] tentPos = s.getValue().getCurrentTentPosition();
			if (tentPos != null) {
				p[tentPos[0]][tentPos[1]] = "^";
			}
		}
		constraints(updatedPuzzle);
	}

}
