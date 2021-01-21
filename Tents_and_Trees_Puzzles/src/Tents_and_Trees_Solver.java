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
			int[] tentPos = selectTent(t);//select consistent value
			//int[] tentPos = selectTentWithMaxDifference(t);
			//int[] tentPos = selectTentMinNumber(t);
			if (tentPos == null) { // if domain is empty
				backtrack();
				createPuzzleFromNode(this.currentNode);
				System.out.println("BACKTRACK");
				updatedPuzzle.printPuzzle();
			} else {
				currentNode.update(t, tentPos);
				updatePuzzle();
				constraints(updatedPuzzle);
				System.out.println("UPDATE");
 				updatedPuzzle.printPuzzle();
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


	//find spot for tent with smallest numbers in row and col
	private int[] selectTentMinNumber(Tree tree) {

		String[][] p = puzzle.getPuzzle();

		int row = tree.getPosition()[0];
		int col = tree.getPosition()[1];

		int numberInRowAbove = 0;
		int numberInRow = Integer.parseInt(p[row][0]);
		int numberInRowBelow = 0;

		int numberInColLeft = 0;
		int numberInCol = Integer.parseInt(p[0][col]);
		int numberInColRight = 0;

		if(row > 1) {
			numberInRowAbove = Integer.parseInt(p[row - 1][0]);
		}
		if (row < puzzle.getRows() - 1) {
			numberInRowBelow = Integer.parseInt(p[row + 1][0]);
		}
		if(col > 1) {
			numberInColLeft = Integer.parseInt(p[0][col - 1]);
		}
		if(col < puzzle.getColumns() - 1) {
			numberInColRight = Integer.parseInt(p[0][col + 1]);
		}

		int[] indexes = getIndexesForDomain(tree);

		int resAbove = numberInRowAbove + numberInCol;
		int resRight = numberInRow + numberInColRight;
		int resBelow = numberInRowBelow + numberInCol;
		int resLeft = numberInRow + numberInColLeft;

		int[] minNumbers = new int[] {resAbove, resRight, resBelow, resLeft};

		int minSpot = resAbove;
		int index = 0;

		List<int[]> res = new ArrayList<>();

		for(int i = 0; i < indexes.length; i++) {
			res.add(new int[] {minNumbers[indexes[i]], indexes[i]});
		}

		for(int i = 0; i < res.size(); i++) {
			if (res.get(i)[0] < minSpot) {
				minSpot = res.get(i)[0];
				index = res.get(i)[1];
			}
		}

		switch(index) {
			case 0:
				if (updatedPuzzle.getPuzzle()[row - 1][col].equals("")) {
					return new int[]{row - 1, col};
				} else {
					tree.deleteFromDomain(new int[] {row - 1, col});
				}
				return null;
			case 1:
				if (updatedPuzzle.getPuzzle()[row][col + 1].equals("")) {
					return new int[]{row, col + 1};
				} else {
					tree.deleteFromDomain(new int[] {row, col + 1});
				}
				return null;
			case 2:
				if (updatedPuzzle.getPuzzle()[row + 1][col].equals("")) {
					return new int[]{row + 1, col};
				} else {
					tree.deleteFromDomain(new int[] {row + 1, col});
				}
				return null;
			case 3:
				if (updatedPuzzle.getPuzzle()[row][col - 1].equals("")) {
					return new int[]{row, col - 1};
				} else {
					tree.deleteFromDomain(new int[] {row, col - 1});
				}
				return null;
			default:
				return null;
		}
	}


	private int[] selectTentWithMaxDifference(Tree tree) {

		if(tree.getDomain().size() == 0) {
			return null;
		}

		int treeRow = tree.getPosition()[0];
		int treeCol = tree.getPosition()[1];
		String[][] p = puzzle.getPuzzle();

		int tentsInRowAbove = 0;
		int tentsInRow = 0;
		int tentsInRowBelow = 0;

		int tentsInColLeft = 0;
		int tentsInCol = 0;
		int tentsInColRight = 0;

		int numberInRowAbove = 0;
		int numberInRow = Integer.parseInt(p[treeRow][0]);
		int numberInRowBelow = 0;

		int numberInColLeft = 0;
		int numberInCol = Integer.parseInt(p[0][treeCol]);
		int numberInColRight = 0;

		for (int i = 0; i < puzzle.getColumns(); i++) {
			if (p[treeRow][i].equals("^")) {
				tentsInRow++;
			}
		}

		for (int i = 0; i < puzzle.getRows(); i++) {
			if (p[i][treeCol].equals("^")) {
				tentsInCol++;
			}
		}

		if(treeRow > 1) {
			numberInRowAbove = Integer.parseInt(p[treeRow - 1][0]);
			for(int i = 0; i < puzzle.getColumns() - 1; i++) {
				if (p[treeRow - 1][i].equals("^")) {
					tentsInRowAbove++;
				}
			}
		}
		if (treeRow < puzzle.getRows() - 1) {
			numberInRowBelow = Integer.parseInt(p[treeRow + 1][0]);
			for(int i = 0; i < puzzle.getColumns() - 1; i++) {
				if(p[treeRow + 1][i].equals("^")) {
					tentsInRowBelow++;
				}
			}
		}
		if(treeCol > 1) {
			numberInColLeft = Integer.parseInt(p[0][treeCol - 1]);
			for (int i = 0; i < puzzle.getRows() - 1; i++) {
				if (p[i][treeCol - 1].equals("^")) {
					tentsInColLeft++;
				}
			}
		}
		if(treeCol < puzzle.getColumns() - 1) {
			numberInColRight = Integer.parseInt(p[0][treeCol + 1]);
			for(int i = 0; i < puzzle.getRows() - 1; i++) {
				if(p[i][treeCol + 1].equals("^")) {
					tentsInColRight++;
				}
			}
		}

		int resAbove = numberInRowAbove + numberInCol - tentsInRowAbove - tentsInCol;
		int resRight = numberInRow + numberInColRight - tentsInRow - tentsInColRight;
		int resBelow = numberInRowBelow + numberInCol - tentsInRowBelow - tentsInCol;
		int resLeft = numberInRow + numberInColLeft - tentsInRow - tentsInColLeft;

		int[] differences = new int[] {resAbove, resRight, resBelow, resLeft};

		int[] indexes = getIndexesForDomain(tree);

		int maxDifference = 0;
		int index = 0;

		List<int[]> res = new ArrayList<>();

		for(int i = 0; i < indexes.length; i++) {
			res.add(new int[] {differences[indexes[i]], indexes[i]});
		}

		for(int i = 0; i < res.size(); i++) {
			if (res.get(i)[0] > maxDifference) {
				maxDifference = res.get(i)[0];
				index = res.get(i)[1];
			}
		}

		switch(index) {
			case 0:
				if (updatedPuzzle.getPuzzle()[treeRow - 1][treeCol].equals("")) {
					return new int[]{treeRow - 1, treeCol};
				} else {
					tree.deleteFromDomain(new int[] {treeRow - 1, treeCol});
				}
				return null;
			case 1:
				if (updatedPuzzle.getPuzzle()[treeRow][treeCol + 1].equals("")) {
					return new int[]{treeRow, treeCol + 1};
				} else {
					tree.deleteFromDomain(new int[] {treeRow, treeCol + 1});
				}
				return null;
			case 2:
				if (updatedPuzzle.getPuzzle()[treeRow + 1][treeCol].equals("")) {
					return new int[]{treeRow + 1, treeCol};
				} else {
					tree.deleteFromDomain(new int[] {treeRow + 1, treeCol});
				}
				return null;
			case 3:
				if (updatedPuzzle.getPuzzle()[treeRow][treeCol - 1].equals("")) {
					return new int[]{treeRow, treeCol - 1};
				} else {
					tree.deleteFromDomain(new int[] {treeRow, treeCol - 1});
				}
				return null;
			default:
				return null;
		}
	}


	//get index for every free spot around the tree
	private int[] getIndexesForDomain(Tree tree) {
		if(tree.getDomain().size() == 0) {
			return null;
		}
		int row = tree.getPosition()[0];
		int col = tree.getPosition()[1];
		List<Integer> indexes = new ArrayList<>();

		if (row > 1) {
			if (puzzle.getPuzzle()[row - 1][col].equals("")) {
				indexes.add(0);
			}
		}

		if (col < puzzle.getColumns() - 1) {
			if (puzzle.getPuzzle()[row][col + 1].equals("")) {
				indexes.add(1);
			}
		}

		if (row < puzzle.getRows() - 1) {
			if (puzzle.getPuzzle()[row + 1][col].equals("")) {
				indexes.add(2);
			}
		}

		if (col > 1) {
			if (puzzle.getPuzzle()[row][col - 1].equals("")) {
				indexes.add(3);
			}
		}

		int[] result = indexes.stream().mapToInt(i -> i).toArray();
		return result;
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
