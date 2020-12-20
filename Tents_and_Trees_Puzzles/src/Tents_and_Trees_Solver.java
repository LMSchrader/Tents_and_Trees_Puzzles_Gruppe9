import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Tents_and_Trees_Solver {
	private String[][] puzzle;
	
	private Stack<Node> currentPath = new Stack<>(); // stack for backtrack
	private Node currentNode;
	
	
	public Tents_and_Trees_Solver(String fileName) {
		setPuzzle(fileName);
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
	
	
	public void setPuzzle(String fileName) {
		List<String[]> p = readFromCsvFile(",", fileName);
		
		this.puzzle = new String[p.size()][p.get(0).length];
		
		for (int i = 0; i < p.size(); i++) {
			this.puzzle[i] = p.get(i);
		}
	}
	
	// source: https://riptutorial.com/csv/example/27605/reading-and-writing-in-java
	private List<String[]> readFromCsvFile(String separator, String fileName) {
	    try (BufferedReader reader = new BufferedReader(new FileReader(fileName))){
	        List<String[]> list = new ArrayList<>();
	        String line = "";
	        while((line = reader.readLine()) != null){
	            String[] array = line.split(separator, -1);
	            list.add(array);
	        }
	        return list;
	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    }  
	}
	
	public void printPuzzle() {
		for (int i = 0; i < puzzle.length; i++) {
			for (int j = 0; j < puzzle[i].length; j++) {
				System.out.print(puzzle[i][j] + "\t");
			}
			System.out.println("\n\n");
		}
	}

}
