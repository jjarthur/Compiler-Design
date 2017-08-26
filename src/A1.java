/**
 * Class: A1.java
 * Description: This class contains the main method controlling the compiler for CD.
 */

import java.util.*;
import java.io.*;

public class A1 {

	/**
	 * Pre-conditions: input is a valid filename.
	 * Post-conditions: Reads an input file from the user and
	 * 					turns it into a string.
	 */
	public String readInput(String input){
		Scanner scanner = new Scanner(System.in);
		boolean success = false;
		String stream = "";
		File file = new File(input);
		FileReader fr = null;
		
		do {
			try {
				fr = new FileReader(file);
				char[] chars = new char[(int) file.length()];
				fr.read(chars);
				stream = new String(chars);
				fr.close();

				success = true;
			} catch (IOException e) {
				System.out.println("File not found, please try again.");
				input = scanner.next();
				file = new File(input);
			}
			finally {
				try {
					if(fr != null) {
						fr.close();
					}
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		while (!success);
		
		return stream;
	}
	
	public void run(String stream){
		new Tokenizer(stream);
	}

	public static void main(String[] args){
		A1 compiler = new A1();
		compiler.run(compiler.readInput(args[0]));
	}
}