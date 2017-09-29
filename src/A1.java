import java.util.*;
import java.io.*;

public class A1 {
	private Queue<Token> tokenList;

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
	
	private void run(String stream){
		//System.out.println("-------------------- Scanner --------------------");
		Tokenizer tokenizer = new Tokenizer(stream);
		tokenList = tokenizer.run();
		System.out.println("\n-------------------- Parser --------------------");
		Parser parser = new Parser(tokenList);
	}

	public static void main(String[] args){
		A1 compiler = new A1();
		compiler.run(compiler.readInput(args[0]));
	}
}