/**
 * Class: Tokenizer.java
 * Description: This class is responsible for lexical analysis of CD source files.
 * 				It separates each token and prints them to the console.
 */

public class Tokenizer {

	//Private variables
	private String stream;
	private int index, row, col;

	//Public constructor
	public Tokenizer(String stream){
		this.stream = stream;
		this.index = 0;
		this.row = 0;
		this.col = 0;

		run();
	}

	//Private methods

	/**
	 * Pre-conditions: None.
	 * Post-conditions: Controls the overall flow of the lexical analysis
	 * 					and prints tokens to console.
	 */
	private void run(){
		int lineLength = 0;
		while (index < stream.length()){ //For each character in the stream
			Token token = getToken();
			if (token != null){ //If valid token
				if (token.value() == TokId.TUNDF){ //If an TUNDF token
					lineLength = 0; //New line
					System.out.println("\nError: Undefined token " + token.shortString()); //Print to console
				}
				else if(lineLength > 60){ //Checking if the length for the current line will exceed 60 characters
					System.out.println();
					lineLength = 0; //New line
					lineLength += token.shortString().length();
					System.out.print(token.shortString()); //Print to console
				}
				else{
					lineLength += token.shortString().length();
					System.out.print(token.shortString()); //Print to console
				}
			}
			index++;
		}
		Token token = new Token(TokId.TEOF, row, col, null);
		if (lineLength > 60){ //Checking if the length for the current line will exceed 60 characters
			System.out.println();
		}
		System.out.println(token.shortString()); //Print to console
	}

	/**
	 * Pre-conditions: current should be valid char.
	 * Post-conditions: Returns the char type string of current.
	 */
	private String getCharType(char current){
		String charType;

		if (Character.isLetter(current)){ //If current is a letter
			charType = "LETTER";
		}
		else if (Character.isDigit(current)){ //If current is a digit
			charType = "DIGIT";
		}
		else if (isOperator(current)){ //If current is an operator
			charType = "OPERATOR";
		}
		else if (Character.isWhitespace(current)){ //If current is a whitespace
			charType = "WHITESPACE";
		}
		else {
			charType = "SPECIAL"; //If current is a special
		}
		return charType;
	}

	/**
	 * Pre-conditions: stream should not be empty.
	 * Post-conditions: Returns a token if found, otherwise returns null.
	 */
	private Token getToken(){
		TokId tid = TokId.TUNDF;
		String tokenStr = "";
		char current = stream.charAt(index);

		switch(getCharType(current)){
			case "LETTER": { //If first character is a letter
				tid = TokId.TIDNT;
				do { //While letter or digit, and index is less than total stream length
					tokenStr += current;
					index++;
					if (index < stream.length()){
						current = stream.charAt(index);
					}
				} while((getCharType(current).equals("LETTER") || getCharType(current).equals("DIGIT")) && index < stream.length());
				index--;
				break;
			}
			case "DIGIT": { //If first character is a digit
				tid = TokId.TILIT;

				if (current == '0') { //Checking for leading 0's
					if (index+1 < stream.length() && getCharType(stream.charAt(index+1)).equals("DIGIT")){
						tid = TokId.TUNDF;
						while (getCharType(current).equals("DIGIT")) { //For every digit after the leading 0's
							tokenStr += current;
							index++;
							if (index < stream.length()) {
								current = stream.charAt(index);
							}
							else{
								break;
							}
						}
						index--;
						break;
					}
				}

				while((getCharType(current).equals("DIGIT") || current == '.') && index < stream.length()){
					tokenStr += current;
					index++;

					if (index < stream.length()){
						current = stream.charAt(index);
					}
					if (current == '.'){ //If potential floating point number
						index++;
						if (index < stream.length()){
							current = stream.charAt(index);
						}
						if (getCharType(current).equals("DIGIT")){ //If floating point number
							tid = TokId.TFLIT;
							tokenStr += "."; //Append the dot
							do {
								tokenStr += current;
								index++;
								if (index < stream.length()){
									current = stream.charAt(index);
								}
							} while(getCharType(current).equals("DIGIT"));
						}
						else{
							index--;
						}
					}
				}
				index--;
				break;
			}
			case "OPERATOR": { //If first character is punctuation
				switch(current){
					case '[':
						tid = TokId.TLBRK;
						tokenStr = null;
						break;
					case ']':
						tid = TokId.TRBRK;
						tokenStr = null;
						break;
					case '(':
						tid = TokId.TLPAR;
						tokenStr = null;
						break;
					case ')':
						tid = TokId.TRPAR;
						tokenStr = null;
						break;
					case ';':
						tid = TokId.TSEMI;
						tokenStr = null;
						break;
					case ',':
						tid = TokId.TCOMA;
						tokenStr = null;
						break;
					case ':':
						tid = TokId.TCOLN;
						tokenStr = null;
						break;
					case '.':
						tid = TokId.TDOTT;
						tokenStr = null;
						break;
					case '<':
						tid = TokId.TLESS;
						tokenStr = null;
						index++;
						if (index < stream.length()){
							current = stream.charAt(index);
						}
						if (current == '<'){ //If assignment operator
							tid = TokId.TASGN;
						}
						else if (current == '='){ //If less than or equal operator
							tid = TokId.TLEQL;
						}
						else{
							index--;
						}
						break;
					case '>':
						tid = TokId.TGRTR;
						tokenStr = null;
						index++;
						if (index < stream.length()){
							current = stream.charAt(index);
						}
						if (current == '>'){ //If input operator
							tid = TokId.TINPT;
						}
						else if (current == '='){ //If greater than or equal operator
							tid = TokId.TGREQ;
						}
						else{
							index--;
						}
						break;
					case '=':
						tokenStr += current;
						index++;
						if (index < stream.length() && stream.charAt(index) == '='){ //If equality operator
							return new Token(TokId.TDEQL, row, col, null);
						}
						else{
							tid = TokId.TUNDF;
							//index++;
						}
						break;
					case '!':
						tokenStr += current;
						index++;
						if (index < stream.length()){
							current = stream.charAt(index);
						}
						if (current == '='){ //If equality operator
							tid = TokId.TNEQL;
							tokenStr = null;
						}
						else{
							index--;
						}
						break;
					case '+':
						tid = TokId.TADDT;
						tokenStr = null;
						break;
					case '-':
						tid = TokId.TSUBT;
						tokenStr = null;
						break;
					case '*':
						tid = TokId.TMULT;
						tokenStr = null;
						break;
					case '/':
						tid = TokId.TDIVT;
						tokenStr += current;
						index++;
						//Checking if token is an in-line comment
						if (index < stream.length()){
							current = stream.charAt(index);
							index++;
							if (current == '-' && index < stream.length()){
								current = stream.charAt(index);
								index++;
								if (current == '-' && index < stream.length()){ //If in-line comment
									current = stream.charAt(index);
									while (current != '\n' && index < stream.length()){ //Until the end of the line
										index++;
										current = stream.charAt(index);
									}
									return null;
								}
								else if (current == '*' && index < stream.length()){ //If multi-line comment
									current = stream.charAt(index);
									while (index < stream.length()-2){ //Whilst still within the comment
										if (current == '*' && stream.charAt(index+1) == '-' && stream.charAt(index+2) == '/'){ //If end of multi-line comment token is found
											index += 2;
											return null;
										}
										index++;
										if (index < stream.length()){
											current = stream.charAt(index);
										}
										else{
											System.out.println("\nError: Missing '*-/'");
											return new Token(TokId.TUNDF, row, col, null);
										}
									}
									System.out.println("\nError: Missing '*-/'");
									return new Token(TokId.TUNDF, row, col, null);
								}
								else{
									index -= 3;
									tokenStr = null;
								}
							}
							else{
								index -= 2;
								tokenStr = null;
							}
						}
						else{
							tokenStr = null;
						}
						break;
					case '%':
						tid = TokId.TPERC;
						tokenStr = null;
						break;
					case '^':
						tid = TokId.TCART;
						tokenStr = null;
						break;
					case '"':
						tid = TokId.TSTRG;
						index++;
						current = stream.charAt(index);
						while (current != '"' && index < stream.length()){ //Until the next quotation marks
							tokenStr += current;
							index++;
							if (index < stream.length()){
								current = stream.charAt(index);
								if (current == '\n'){
									System.out.println("\nError: Missing '\"'");
									return new Token(TokId.TUNDF, row, col, tokenStr.substring(0,tokenStr.length()-1));
								}
							}
							else{
								System.out.println("\nError: Missing '\"'");
								return new Token(TokId.TUNDF, row, col, tokenStr);
							}
						}
						break;
				}
				break;
			}
			case "SPECIAL": { //If first character is special
				while (getCharType(current).equals("SPECIAL")){
					tokenStr += current;
					index++;
					if (index < stream.length()){
						current = stream.charAt(index);
					}
				}
				index--;
				break;
			}
			case "WHITESPACE": { //If first character is whitespace
				return null;
			}
		}

		return new Token(tid, row, col, tokenStr);
	}

	/**
	 * Pre-conditions: current is a valid char.
	 * Post-conditions: Returns true if current is an operator,
	 * 					otherwise returns false.
	 */
	private boolean isOperator(char current){
		char[] operators = { '[', ']', '(', ')', ';', ',', ':', '.', '<', '>', '=', '!', '+', '-','*', '/', '%', '^', '"' };
		for (int i = 0; i < operators.length; i++){
			if (current == operators[i]){
				return true;
			}
		}
		return false;
	}
}