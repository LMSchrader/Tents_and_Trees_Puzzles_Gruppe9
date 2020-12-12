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

}
