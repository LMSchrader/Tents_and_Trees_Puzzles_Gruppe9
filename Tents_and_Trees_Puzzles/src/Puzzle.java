import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Puzzle {
	// representation:
	// tree: "t"
	// tent: "^"
	// gras: "g"
	// unknown: ""
	private String[][] puzzle;
	
	public Puzzle(String fileName) {
		setPuzzle(fileName);
	}
	
	public void setPuzzle(String fileName) {
		List<String[]> p = readFromCsvFile(",", fileName);
		
		this.puzzle = new String[p.size()][p.get(0).length];
		
		for (int i = 0; i < p.size(); i++) {
			this.puzzle[i] = p.get(i);
		}
	}
	
	public void setPuzzle(String[][] puzzle) {
		this.puzzle = puzzle;
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
				if (puzzle[i][j].equals("")) {
					System.out.print("  ");
				} else {
					try {
						Integer a = Integer.parseInt(puzzle[i][j]);
						if(a > 9) {
							System.out.print(puzzle[i][j]);
						} else {
							System.out.print(" " + puzzle[i][j]);
						}
					} catch(Exception E) {
						System.out.print(" ");
						System.out.print(puzzle[i][j]);
					}
					
				}
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public int countNumberOfXInRow(int row, String x) {
		if (isLegalRowIndex(row)) {
			int count = 0;
			for (int i = 1; i < getColumns(); i++) {
				if (x.equals(this.puzzle[row][i])) {
					count++;
				}
			}
			return count;
		} else {
			throw new IndexOutOfBoundsException("Index " + row + " is out of bounds!");
		}
	}
	
	public int countNumberOfXInColumn(int column, String x) {
		if (isLegalColumnIndex(column)) {
			int count = 0;
			for (int i = 1; i < getRows(); i++) {
				if (x.equals(this.puzzle[i][column])) {
					count++;
				}
			}
			return count;
		} else {
			throw new IndexOutOfBoundsException("Index " + column + " is out of bounds!");
		}
	}
	
	public int numberOfTentsThatShouldBeInRow(int row) {
		if (isLegalRowIndex(row)) {
			return Integer.parseInt(this.puzzle[row][0]);
		} else {
			throw new IndexOutOfBoundsException("Index " + row + " is out of bounds!");
		}
	}
	
	public int numberOfTentsThatShouldBeInCollumn(int column) {
		if (isLegalColumnIndex(column)) {
			return Integer.parseInt(this.puzzle[0][column]);
		} else {
			throw new IndexOutOfBoundsException("Index " + column + " is out of bounds!");
		}
	}
	
	public void fillUnknownFieldsofRowWithX(int row, String x) {
		if (isLegalRowIndex(row)) {
			for (int i = 1; i < getColumns(); i++) {
				if (this.puzzle[row][i].equals("")) {
					this.puzzle[row][i] = x;
				}
			}
		} else {
			throw new IndexOutOfBoundsException("Index " + row + " is out of bounds!");
		}
	}
	
	public void fillUnknownFieldsofColumnWithX(int column, String x) {
		if (isLegalColumnIndex(column)) {
			for (int i = 1; i < getRows(); i++) {
				if (this.puzzle[i][column].equals("")) {
					this.puzzle[i][column] = x; 
				}
			}
		} else {
			throw new IndexOutOfBoundsException("Index " + column + " is out of bounds!");
		}
	}
	
	public int getRows() {
		return this.puzzle.length;
	}
	
	public int getColumns() {
		return this.puzzle[0].length;
	}
	
	private boolean isLegalRowIndex(int row) {
		return row < getRows() && row > 0;
	}
	
	private boolean isLegalColumnIndex(int column) {
		return column < getColumns() && column > 0;
	}

	public String[][] getPuzzle() {
		return puzzle;
	}
}
