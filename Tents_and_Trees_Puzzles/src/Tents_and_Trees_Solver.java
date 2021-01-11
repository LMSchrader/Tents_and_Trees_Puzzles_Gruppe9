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
		puzzle.printPuzzle();
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
		return selectTreeWithSmallestDomain();
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
		for (Tree tree: trees) {
			if (tree.getDomain().size() == 1) {
				return tree;
			}
			int constraintCount = 0;
			for (Tree otherTree: trees) {
				if (otherTree == tree) {
					continue;
				}
				List<int[]> domain = new ArrayList<int[]>(tree.getDomain());
				List<int[]> otherDomain = new ArrayList<int[]>(otherTree.getDomain());
				domain.retainAll(otherDomain);
				constraintCount += domain.size();
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
		return selectFirstTent(tree);
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
