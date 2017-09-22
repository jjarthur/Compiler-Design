// COMP3290 CD Compiler
//
//	Token class	- constructs a token on behalf of the scanner for it to be sent to the parser.
//			- IDNTs/ILITs/FLITs/Strings do not have their symbol table reference set in this class,
//			    this is best done within the parser as it makes things easier in later phases,
//			    when we are dealing with things like variable scoping.
//
//			The class contains the usual constructor, get and set methods required of
//			  such a class.
//			Method checkKeyWords(String) is a skeleton for changing <id>s into relevant
//        Keywords as required.
//			Method toString() returns a dump of the Token contents useful for debugging.
//			Method shortString() returns a String that may be useful for Part 1 output.
//
//    Rules of Use: The text for this class has been extracted from a working CD16 scanner.
//			Code released via Blackboard may not be passed on to anyone outside this
//			  semester's COMP3290 class.
//			You may not complain or expect any consideration if the code does not work
//			  the way you expect it to.
//			It is supplied as an assistance and may be used in your project if you wish.
//
//	M.Hannaford	28-Jul-2016
//
//
public class Token {

    private TokId tid;		// token identifier
    private int line;		// line number on listing
    private int pos;		// character position within line
    private String str;		// actual lexeme character string from scanner
//	private StRec symbol;		// symbol table entry - used in Pt3 for the Parser, not needed in Pt1

    public Token(TokId t, int ln, int p, String s) {
        tid = t;
        line = ln;
        pos = p;
        str = s;
        if (tid == TokId.TIDNT) {		// Identifier token could be a reserved keyword in CD16
            TokId v = checkKeywords(s);			// (match is case-insensitive)
            if (v != TokId.TIDNT) { tid = v; str = null; }	// if keyword, alter token type
        }
//		symbol = null;	// initially null, got from Parser SymTab lookup if TIDNT/TILIT/TFLIT/TSTRG
    }

    public TokId value() { return tid; }

    public int getLn() { return line; }

    public int getPos() { return pos; }

    public String getStr() { return str; }

//	public StRec getSymbol() { return symbol; }			// ready for Part 3

//	public void setSymbol(StRec x) {symbol = x; }			// ready for Part 3


    private TokId checkKeywords(String s) {
        s = s.toLowerCase();		// change to lower case before checking
        if ( s.equals("cd") ) return TokId.TCD;
        if ( s.equals("constants") ) return TokId.TCONS;
        if ( s.equals("types") ) return TokId.TTYPS;
        if ( s.equals("is") ) return TokId.TISKW;
        if ( s.equals("arrays") ) return TokId.TARRS;
        if ( s.equals("main") ) return TokId.TMAIN;
        if ( s.equals("begin") ) return TokId.TBEGN;
        if ( s.equals("end") ) return TokId.TENDK;
        if ( s.equals("of") ) return TokId.TOFKW;
        if ( s.equals("array") ) return TokId.TARRY;
        if ( s.equals("func") ) return TokId.TFUNC;
        if ( s.equals("void") ) return TokId.TVOID;
        if ( s.equals("const") ) return TokId.TCNST;
        if ( s.equals("integer") ) return TokId.TINTG;
        if ( s.equals("real") ) return TokId.TREAL;
        if ( s.equals("boolean") ) return TokId.TBOOL;
        if ( s.equals("for") ) return TokId.TFORK;
        if ( s.equals("repeat") ) return TokId.TREPT;
        if ( s.equals("until") ) return TokId.TUNTL;
        if ( s.equals("in") ) return TokId.TINKW;
        if ( s.equals("out") ) return TokId.TOUTP;
        if ( s.equals("if") ) return TokId.TIFKW;
        if ( s.equals("else") ) return TokId.TELSE;
        if ( s.equals("line") ) return TokId.TOUTL;
        if ( s.equals("return") ) return TokId.TRETN;
        if ( s.equals("not") ) return TokId.TNOTK;
        if ( s.equals("and") ) return TokId.TANDK;
        if ( s.equals("or") ) return TokId.TORKW;
        if ( s.equals("xor") ) return TokId.TXORK;
        if ( s.equals("true") ) return TokId.TTRUE;
        if ( s.equals("false") ) return TokId.TFALS;

        return TokId.TIDNT;		// not a Keyword, therefore an <id>
    }

    public String toString() {		// toString method is only meant to be used for debug printing
        String s = tid.toString();
        while (s.length() % 6 != 0) s = s + " ";
        s = s +" " + line;
        if (str == null) return s;
        if (tid != TokId.TUNDF)
            s += " " + str;
        else {
            s += " ";
            for (int i=0; i<str.length(); i++) { // output non-printables as ascii codes
                char ch = str.charAt(i);
                int j = (int)ch;
                if (j <= 31 || j >= 127) s += "\\" +j; else s += ch;
            }
        }
        return s;
    }

    public String shortString() {		// provides a String that may be useful for Part 1 printed output
        String s = tid.name() + " ";
        if (str == null){
            while (s.length() % 6 != 0){
                s += " ";
            }
            return s;
        }
        if (tid != TokId.TUNDF) {
            if (tid == TokId.TSTRG)
                s += "\"" + str + "\" ";
            else
                s += str + " ";
            int j = (6 - s.length()%6) % 6;
            for (int i=0; i<j; i++)
                s += " ";
            return s;
        }
        s = "\n" + s;
        for (int i=0; i<str.length(); i++) { // output non-printables as ascii codes
            char ch = str.charAt(i);
            int j = (int)ch;
            if (j <= 31 || j >= 127) s += "\\" +j; else s += ch;
        }
        s += "\n";

        return s;
    }

}
