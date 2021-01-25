import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

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
//		puzzle.printPuzzle();
		preprocessing();
//		puzzle.printPuzzle();
		currentNode = createFirstNode(); // define variables 
		
		this.updatedPuzzle = this.puzzle.clone();
		
		while (currentNode.hasUninstantiatedTrees()) {
			Tree t = selectTree(); // select variable
			int[] tentPos = selectTent(t); // select consistent value
			if (tentPos == null) { // if domain is empty
				backtrack();
//				System.out.println("BACKTRACK");
//				updatedPuzzle.printPuzzle();
			} else {
				update(t, tentPos);	
//				System.out.println("UPDATE");
//				updatedPuzzle.printPuzzle(currentNode);
				currentPath.push(currentNode.clone());
				constraintPropagation(); // propagation
//				System.out.println("PROPAGATION");
//				updatedPuzzle.printPuzzle();
				// create a new node based on the old currentNode to build path (currentPath)
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
		puzzle.setGrassForSquaresWithNoAvailableTree();
		//preprocessing3();
	}
	
	private Tree selectTree() {
		return selectMostConstrainingTree();
	}
	
	private Tree selectRandomTree() {
		List<Tree> trees = currentNode.getUninstantiatedTrees();
		Tree tree = trees.get((int)(Math.random() * ((trees.size() - 1) + 1)));
		return tree;
	}
	
	private Tree selectTreeWithSmallestDomain() {
		List<Tree> trees = currentNode.getUninstantiatedTrees();
		int smallestDomain = Integer.MAX_VALUE;
		Tree treeWithSmallestDomain = null;
		for (Tree tree: trees) {
			int domainSize = tree.getDomain().size();
			if (domainSize < smallestDomain)
				smallestDomain = domainSize;
				treeWithSmallestDomain = tree;
		}
		return treeWithSmallestDomain;
	}
	
	
	private Tree selectMostConstrainingTree() {
		List<Tree> trees = currentNode.getUninstantiatedTrees();
		int maxConstraints = 0;
		Tree mostConstrainingTree = null;
		for (Tree tree: trees) { // tree that could be selected
			if (tree.getDomain().size() == 1) {
				return tree;
			}
			
			int constraintCount = 0; // counter for object tree
			
			for (int[] pos : tree.getDomain()) {
				
				for (Tree otherTree: trees) {
					if (otherTree == tree) {
						continue;
					}
					
					for (int[] otherPos : otherTree.getDomain()) {
						if ((otherPos[0] > pos[0]-2 &&  otherPos[0] < tree.getPosition()[0]+2 && otherPos[1] > pos[1]-2 &&  otherPos[1] < tree.getPosition()[1]+2) || // if the tent position otherPos is adjacent to pos
								 (otherPos[1] == pos[1] && updatedPuzzle.countNumberOfXInColumn(pos[1], "^") == updatedPuzzle.numberOfTentsThatShouldBeInColumn(pos[1])-1) || //if a tent on position pos would fill the column in which otherPos is located
								 (otherPos[0] == pos[0] && updatedPuzzle.countNumberOfXInRow(pos[0], "^") == updatedPuzzle.numberOfTentsThatShouldBeInRow(pos[0])-1) // if a tent on position pos would fill the row in which otherPos is located
								 ) {
							constraintCount++;
							continue;
						}
					}
				}
			}
			if (constraintCount >= maxConstraints) {
				maxConstraints = constraintCount;
				mostConstrainingTree = tree;
			}
		}
		return mostConstrainingTree;
	}
	
	private Tree selectFirstTree() {
		List<Tree> trees = currentNode.getUninstantiatedTrees();
		return trees.get(0);
	}
	
	private int[] selectTent(Tree tree) {
		return selectRandomTent(tree);
	}
	
	private int[] selectRandomTent(Tree tree) {
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
	
	private int[] selectFirstTent(Tree tree) {
		int domainSize = tree.getDomain().size();
		int[] tent;
		for (int i = 0; i < domainSize; i++) {
			tent = tree.getDomain().get(0);
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
		
		createPuzzleFromNode(this.currentNode);
	}
	
	private void update(Tree t, int[] tentPos) {
		currentNode.update(t, tentPos);
		updatePuzzle();	
		constraints(updatedPuzzle);
	}
	
	private void constraintPropagation() {
		if(forwardChecking()) {
			backtrack();
		}
	}
	
	private boolean forwardChecking() {
		Tree tree = currentNode.getUpdatedTree();
		List<Tree> uninstantiatedNeighbours = getUninstantiatedNeighbours(tree);
		for (int i = 0; i<uninstantiatedNeighbours.size(); i++) {
			if (deleteInconsistentValuesForwardChecking(uninstantiatedNeighbours.get(i))) {
				if(uninstantiatedNeighbours.get(i).getDomain().isEmpty()) {
					return true;
				}
			}
		}
		return false;
		
	}
	
	private List<Tree> getUninstantiatedNeighbours(Tree tree) {
		List<Tree> uninstantiatedNeighbours = new ArrayList<>();
		List<Tree> allUninstantiatedTrees = currentNode.getUninstantiatedTrees();
		
		for (int i = 0; i<allUninstantiatedTrees.size(); i++) {
			Tree t = allUninstantiatedTrees.get(i);
			if (t.getPosition()[0] > tree.getPosition()[0]-3 &&  t.getPosition()[0] < tree.getPosition()[0]+3) {
				uninstantiatedNeighbours.add(t);
			} else if (t.getPosition()[1] > tree.getPosition()[1]-3 &&  t.getPosition()[1] < tree.getPosition()[1]+3) {
				uninstantiatedNeighbours.add(t);
			}
		}
		return uninstantiatedNeighbours;
		
	}
	
	private boolean deleteInconsistentValuesForwardChecking(Tree neighbouringTree) {
		List<int[]> neighbouringTreeDomain = neighbouringTree.getDomain();
		List<int[]> toBeDeleted = new ArrayList<>();
		String[][] puzzle = updatedPuzzle.getPuzzle();
		
		for (int i = 0; i < neighbouringTreeDomain.size(); i++) {
			int[] domainElementPos = neighbouringTreeDomain.get(i);
			if (!puzzle[domainElementPos[0]][domainElementPos[1]].equals("")) {
				toBeDeleted.add(domainElementPos);
			}
		}
			
		if (!toBeDeleted.isEmpty()) {
			for (int[] toBeDeletedElement : toBeDeleted) {
				neighbouringTree.deleteFromDomain(toBeDeletedElement);
			}
			return true;
		}
		return false;
	}
	
	private void constraints(Puzzle puzzle) {
		setGrassAroundASquareWithATent(puzzle);
		fillRowsAndCollumnsWithGrassIfAllTentsHaveBeenSet(puzzle);
		setGrassOnAllEmptyFieldsIfFieldIsNotInAnyDomainOfUninstantiatedTrees(puzzle);
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
	
	// constraint:  Each tree must be attached to exactly one tent.
	private void setGrassOnAllEmptyFieldsIfFieldIsNotInAnyDomainOfUninstantiatedTrees(Puzzle puzzle) {
		for (int i = 1; i < puzzle.getRows(); i++) {
			for (int j = 1; j < puzzle.getColumns(); j++) {
				setGrassOnEmptyFieldIfFieldIsNotInAnyDomainOfUninstantiatedTrees(puzzle, new int[] {i,j});
			}
		}
	}
	
	private void setGrassOnEmptyFieldIfFieldIsNotInAnyDomainOfUninstantiatedTrees(Puzzle puzzle, int[] domainPos) {
		if (puzzle.getPuzzle() [domainPos[0]] [domainPos[1]].equals("")) {
			List<Tree> allUninstantiatedTrees = currentNode.getUninstantiatedTrees();
			for (Tree t : allUninstantiatedTrees) {
				if (Arrays.equals(t.getPosition(),new int[] {domainPos[0], domainPos[1]-1}) ||
					Arrays.equals(t.getPosition(),new int[] {domainPos[0], domainPos[1]+1}) ||
					Arrays.equals(t.getPosition(),new int[] {domainPos[0]-1, domainPos[1]}) ||
					Arrays.equals(t.getPosition(),new int[] {domainPos[0]+1, domainPos[1]})) {
						for (int[] domainElement : t.getDomain()) {
							if (Arrays.equals(domainPos, domainElement)) {
								return;
							}
						}
				}
			}
			updatedPuzzle.getPuzzle() [domainPos[0]] [domainPos[1]] = "g";
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
