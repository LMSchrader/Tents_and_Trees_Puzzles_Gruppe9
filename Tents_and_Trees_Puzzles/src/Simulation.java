import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Simulation {
	
	/*  Die zu testenden Kombinationen von Select Heuristiken muessen per Hand eingefuegt werden
	 *  Dazu muessen in Tents_and_Trees_Solver die entsprechenden Strategien in selectTree() und selectTent() aufgerufen werden, 
	 *  sowie in der folgenden main Methode die ensprechenden Bezeichner fuer die Strategien in solved.writeBacktrackcountToCsvFile ersetzt werden.
	 */
	public static void main(String[] args) {
		List<String> fileNames = new ArrayList<>();
		for(int i = 0; i < 10; i++) {
			fileNames.add("tents_trees_" + i + ".csv");
		}
		
		for(String fileName : fileNames) {
			for(int i = 0; i<1; i++) {
				Tents_and_Trees_Solver solved = new Tents_and_Trees_Solver("src" + File.separatorChar + "resources" + File.separatorChar + fileName);
				solved.writeBacktrackcountToCsvFile(" ", "evaluation", fileName, "MostConstrained", "LeastConstraining", true);
			}
		}
	}

}
