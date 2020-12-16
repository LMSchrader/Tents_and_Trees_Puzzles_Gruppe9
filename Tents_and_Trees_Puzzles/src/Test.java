import java.io.File;

public class Test {
	private static Puzzle puzzle;

	public static void main(String[] args) {
		puzzle = new Puzzle("src" + File.separatorChar + "resources" + File.separatorChar + "tents_trees_0.csv");

		preprocessing3();
	}

	/**
	 * Wenn in einer Zeile oder Spalte nur noch so viele moegliche Felder gibt, um Zelte aufstellen zu koennen, 
	 * wie Zelte in der Zeile oder Spalte stehen koennen, kann direkt ein Zelt gesetzt werden. 
	 */
	public static void preprocessing3() {
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
	}
	
}
