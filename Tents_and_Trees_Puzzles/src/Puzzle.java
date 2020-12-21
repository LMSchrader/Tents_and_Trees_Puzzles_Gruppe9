import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Puzzle {
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

	// source: https://riptutorial.com/csv/example/27605/reading-and-writing-in-java
	private List<String[]> readFromCsvFile(String separator, String fileName) {
		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
			List<String[]> list = new ArrayList<>();
			String line = "";
			while ((line = reader.readLine()) != null) {
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
	
	public void setGrassForSquaresWithNoAvailableTree() {
		for (int i = 1; i < puzzle.length - 1; i++) {
			for (int j = 1; j < puzzle[i].length - 1; j++) {
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
				if (i < puzzle.length) {
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
				if (j < puzzle[i].length) {
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

	public void markZeroes() {
		List<Integer> indexOfZeroVert = new ArrayList<>();

		for (int i = 0; i < puzzle[0].length; i++) {
			if(puzzle[0][i].equals("0")) {
				indexOfZeroVert.add(Integer.valueOf(i));
			}
		}

		List<Integer> indexOfZeroHor = new ArrayList<>();
		for (int i = 0; i < puzzle.length; i++) {
			if(puzzle[i][0].equals("0")) {
				indexOfZeroHor.add(Integer.valueOf(i));
			}
		}
		int k = 0;
		if(indexOfZeroHor.size() != 0) {
			for(int a = 1; a < puzzle[0].length; a++) {
				if (!puzzle[indexOfZeroHor.get(k)][a].equals("t")) {
					puzzle[indexOfZeroHor.get(k)][a] = "g";
				}
			}
		}
		if(indexOfZeroVert.size() != 0) {
			for (int a = 1; a < puzzle.length; a++) {
				if (!puzzle[a][indexOfZeroVert.get(k)].equals("t")) {
					puzzle[a][indexOfZeroVert.get(k)] = "g";
				}
			}
		}
		printPuzzle();
	}
}


