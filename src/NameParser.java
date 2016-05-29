import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

/*
 * Partially based off of http://www.javapractices.com/topic/TopicAction.do?Id=87
 * Parse the census formated text document for names and holds an array of those names.
 */
public class NameParser {

	private final FileReader input;
	private static Vector<String> namesList;
	private BufferedReader bufRead;
	private String myLine = null;
	
	public NameParser(String string) throws IOException {
		  input = new FileReader(string);
		  this.process();
		  
	}

	/*
	 * Parses the text file.
	 * Adds the name to the namesList
	 */
	private void process() throws IOException {
		namesList = new Vector<String>(1);
		bufRead = new BufferedReader(input);
		while( (myLine = bufRead.readLine()) != null){
			String[] splitString = myLine.split(" ");
			String name = splitString[0];
			namesList.add(name);
		}
		
	}

	public String getRandomName(){
		int index = (int) (Math.random() * namesList.size());
		return namesList.elementAt(index);
	}
}
