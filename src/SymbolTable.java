import java.util.HashMap;

public class SymbolTable {

    //Scopes
    public static final String GLOBALS = "_GLOBALS";
    public static final String MAIN = "_MAIN";
    public static final String LITERALS = "_LITERALS";

    //Scope HashMaps
    public static HashMap<String, StRec> globals;
    public static HashMap<String, StRec> main;
    public static HashMap<String, StRec> literals;
    public static HashMap<String, HashMap<String, StRec>> functions;

    static {
        globals = new HashMap<>();
        main = new HashMap<>();
        literals = new HashMap<>();
        functions = new HashMap<>();
    }

    public void addFunction(String lexeme){
        functions.put(lexeme, new HashMap<>());
    }

    public StRec lookup(String lexeme, String scope){
        switch(scope){
            case GLOBALS:
                return globals.get(lexeme);
            case MAIN:
                if (main.get(lexeme) != null){
                    return main.get(lexeme);
                }
                break;
            case LITERALS:
                return literals.get(lexeme);
            default:
                //Functions
                HashMap<String, StRec> hashMap = functions.get(scope);
                if (hashMap.get(lexeme) == null){
                    return globals.get(lexeme);
                }
                return hashMap.get(lexeme);
        }

        return globals.get(lexeme);
    }

    public void insert(StRec symbol, String scope){
        String lexeme = symbol.getName();

        switch(scope){
            case GLOBALS:
                globals.put(lexeme, symbol);
                break;
            case MAIN:
                main.put(lexeme, symbol);
                break;
            case LITERALS:
                globals.put(lexeme, symbol);
                break;
            default:
                functions.get(scope).put(lexeme,symbol);
                break;
        }
    }

    public String toString(){
        String output = "\n\n---GLOBALS---\n";
        for (String key: globals.keySet()){
            String value = globals.get(key).toString();
            //String value = globals.get(key).getDeclPlace().getType().toString();
            output += value + "\n";
        }

        output += "\n---MAIN---\n";
        for (String key: main.keySet()){
            String value = main.get(key).toString();
            //String value = globals.get(key).getDeclPlace().getType().toString();
            output += value + "\n";
        }

        //output += "\n---LITERALS---\n";
        for (String key: literals.keySet()){
            String value = literals.get(key).toString();
            //String value = globals.get(key).getDeclPlace().getType().toString();
            output += value + "\n";
        }

        output += "\n---FUNCTIONS---\n";
        for (String key: functions.keySet()){
            output += key + "\n";

            for (String functionKey: functions.get(key).keySet()){
                String value = functions.get(key).get(functionKey).toString();
                output += "   " + value + "\n";
            }
        }

        return output;
    }
}
