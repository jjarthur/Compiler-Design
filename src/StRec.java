import com.sun.org.apache.xalan.internal.xsltc.compiler.util.VoidType;

public class StRec {

    private TokId type; //Token type
    private String name; //Name of id (lexeme)
    private String idType; //How this variable name is originally declared
    private int value; //For an integer constant
    private double fvalue; //For a floating point constant
    private int dline; //Line where declared
    private boolean decl; //Set when declared
    private StRec typeName; //Link between this identifier/literal and its type
    private TreeNode declPlace; //Where the decl syntax sub-tree is for a func (for param type checks)
    private HashTable hashTable; //Complete hash table for a list of fields in a particular struct
    private int base; //Base register number (code gen)
    private int offset; //Allocated offset (code gen)

    public StRec(String s){
        this(s,0,-1);
    }

    //Special constructor for global simple types
    public StRec(String s, boolean self, StRec other){
        this(s,0,-1);
        decl = true;
        if (self) typeName = this; else typeName = other;
    }

    public StRec(String s, int ln){
        this(s,0,ln);
    }

    public StRec(String s, int v, int ln){
        type = TokId.TIDNT;
        name = s;
        idType = "IUNDEF";
        if (Character.isDigit(name.charAt(0))) type = TokId.TILIT;
        if(name.charAt(0) == '"') type = TokId.TSTRG;
        value = v;
        fvalue = 0.0;
        dline = ln;
        decl = false;
        typeName = VOIDTYPE; //Do we also use this for struct id within array type decl?
        declPlace = null;
        hashTable = null;

        base = -1;
        offset = -1;
    }

    //Global static StRec's for type checking simple types and boolean values
    public static final StRec VOIDTYPE = new StRec("void",true,null);
    public static final StRec INTTYPE = new StRec("intg", false, VOIDTYPE);
    public static final StRec REALTYPE = new StRec("real",false, VOIDTYPE);
    public static final StRec BOOLTYPE = new StRec("bool", false, VOIDTYPE);
    public static final StRec TRUE = new StRec("bool", true, VOIDTYPE); //VOIDTYPE?
    public static final StRec FALSE = new StRec("bool", false, VOIDTYPE); //VOIDTYPE?

    public String getName(){
        return name;
    }
}