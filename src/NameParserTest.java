import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public class NameParserTest {

	@Test
	public void randomNameTest() throws IOException {
		NameParser test = new NameParser("src/txt/test.txt");
		String name = test.getRandomName();
		assertTrue(name.equals("BETTY") || name.equals("CARL") || name.equals("DAN"));
	}
	
	@Test(expected=FileNotFoundException.class)
	public void notFoundTest() throws IOException {
		NameParser test = new NameParser("src/txt/corn.txt");
	}

}
