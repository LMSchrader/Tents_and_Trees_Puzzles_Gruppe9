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
	private List<int[]> tentPosistions = new ArrayList<>();
	
	public Puzzle(String fileName) {
		setPuzzle(fileName);
	}
	
	private Puzzle(String[][] puzzle) {
		this.puzzle = puzzle;
	}
	
	private Puzzle(String[][] puzzle, List<int[]> tentPositions) {
		this.puzzle = puzzle;
		this.tentPosistions = tentPositions;
	}
	
	public List<int[]> getTentPosistions() {
		return tentPosistions;
	}
	
	public void addTentPosition(int[] tentPos) {
		tentPosistions.add(tentPos);
	}
	
	private List<int[]> cloneTentPositions() {
		List<int[]> clonedTentPositions = new ArrayList<>();
		for (int[] entry : this.tentPosistions) {
			clonedTentPositions.add(entry);
		}
		return clonedTentPositions;
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
	
	public void printPuzzle(Node n) {
		int[] treePos = n.getUpdatedTree().getPosition();
		int[] tentPos = n.getUpdatedTree().getCurrentTentPosition();
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
						if((treePos[0]==i && treePos[1]==j)||(tentPos[0]==i && tentPos[1]==j)) {
							System.out.print("*");
						} else {
							System.out.print(" ");
						}
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
	
	public int numberOfTentsThatShouldBeInColumn(int column) {
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
	
	public String[][] getPuzzle() {
		return puzzle;
	}
	
	private boolean isLegalRowIndex(int row) {
		return row < getRows() && row > 0;
	}
	
	private boolean isLegalColumnIndex(int column) {
		return column < getColumns() && column > 0;
	}
	
	public Puzzle clone() {
		String[][] p = new String[getRows()][getColumns()];
		for (int i = 0; i < getRows(); i++) {
			for (int j = 0; j < getColumns(); j++) {
				p[i][j] = puzzle[i][j];
			}
		}
		List<int[]> clonedTentPositions = cloneTentPositions();
		return new Puzzle(p,clonedTentPositions);
	}
	
	public void markZeroes() {
		for (int i = 0; i < getColumns(); i++) {
			if(puzzle[0][i].equals("0")) {
				for(int a = 1; a < getRows(); a++) {
					if (puzzle[a][Integer.valueOf(i)].equals("")) {
						puzzle[a][Integer.valueOf(i)] = "g";
					}
				}
			}
		}
		
		for (int i = 0; i < getRows(); i++) {
			if(puzzle[i][0].equals("0")) {
				for(int a = 1; a < getColumns(); a++) {
					if (puzzle[Integer.valueOf(i)][a].equals("")) {
						puzzle[Integer.valueOf(i)][a] = "g";
					}
				}
			}
		}
	}
	
	public void setGrassForSquaresWithNoAvailableTree() {
		for (int i = 1; i < getRows(); i++) {
			for (int j = 1; j < getColumns(); j++) {
				if (!puzzle[i][j].equals("")) {
						continue;
				}
				boolean treeAbove = false;
				if (i > 1) {
					if (puzzle[i-1][j].equals("t")) {
						treeAbove = true;
					}
				}
				boolean treeBelow = false;
				if (i < getRows() - 1) {
					if (puzzle[i+1][j].equals("t")) {
						treeBelow = true;
					}
				}
				boolean treeToTheLeft = false;
				if (j > 1) {
					if (puzzle[i][j-1].equals("t")) {
						treeToTheLeft = true;
					}
				}
				boolean treeToTheRight = false;
				if (j < getColumns() - 1) {
					if (puzzle[i][j+1].equals("t")) {
						treeToTheRight = true;
					}
				}
				if (treeToTheLeft || treeToTheRight || treeAbove || treeBelow) {
					continue;
				}
				puzzle[i][j] = "g";
			}
		}
	}
	
	public void fillEmptyFieldsWithGrass() {
		for (int i = 1; i < getRows(); i++) {
			fillUnknownFieldsofRowWithX(i,"g");
		}
	}
}
