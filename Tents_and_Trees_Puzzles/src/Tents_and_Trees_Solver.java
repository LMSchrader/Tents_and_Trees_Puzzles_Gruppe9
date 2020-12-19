import java.util.Stack;

public class Tents_and_Trees_Solver {
	private Puzzle p;
	
	private Stack<Node> currentPath = new Stack<>(); // stack for backtrack
	private Node currentNode;
	
	
	public Tents_and_Trees_Solver(String fileName) {
		p = new Puzzle(fileName);
		csp();
	}
	
	//  constraint satisfaction procedure
	private void csp() {
//		define variables and constraints
//		preprocessing % e.g. arc-consistency
//		WHILE there are uninstantiated variables
//		select variable v % selection
//		IF domain of v is empty
//		THEN solve conflict % (backtrack)
//		ELSE select consistent value from % selection
//		domain of v                           % assignment
//		constraint propagation % propagation
//		ENDIF
//		END WHILE
	}
}
