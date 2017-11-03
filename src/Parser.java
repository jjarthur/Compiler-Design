import java.util.Queue;

public class Parser {

    //Private variables
    private Queue<Token> tokenList;
    private SymbolTable symbolTable;
    private String scope, stream, errorList;
    private int errorCount = 0;

    //Public constructor
    public Parser(Queue<Token> tokenList, String stream){
        this.tokenList = tokenList;
        this.stream = stream;
        this.errorList = "\n";
        this.symbolTable = new SymbolTable();
        this.scope = SymbolTable.GLOBALS;
        run();
    }

    //Local methods
    private void run(){
//        printNodes();
        printTree();
        System.out.println(errorList);
//        System.out.println(symbolTable);

        if (errorCount == 0){
            System.out.println("No errors.");
        }
        else{
            System.out.println(errorCount + " errors found.");
        }
    }

    /**
     * Pre-conditions: None.
     * Post-conditions: Prints tokens in a tree-like format.
     */
    private void printTree() {
        System.out.println(program().toString(0));
    }

    /**
     * Pre-conditions: None.
     * Post-conditions: Prints tokens with format as described in
     *                  assignment specifications.
     */
    private void printNodes(){
        System.out.println(program().printNodeSpace());
    }

    /**
     * Pre-conditions: None.
     * Post-conditions: Consumes tokens until a semi-colon or end of
     *                  file is reached, and then returns UNDEF Node.
     */
    private TreeNode syntaxError(){
        Token current = tokenList.peek();
        errorList  += listingLine(current.getLn()) + "\nSyntax error - Line " + current.getLn() + "\nUnexpected token: " + current.value() + "\n\n";
        while(current.value() != TokId.TSEMI && current.value() != TokId.TEOF){
            tokenList.poll();
            current = tokenList.peek();
        }
        errorCount++;
        return new TreeNode(Node.NUNDEF);
    }

    public void semanticError(String error, int ln){
        errorList += listingLine(ln) + "\nSemantic error - Line " + ln + "\n" + error + "\n\n";
        errorCount++;
    }

    private String listingLine(int ln){
        String listing = ln + " ";
        int currentLn = 1;

        for (int i = 0; i < stream.length(); i++) {
            if (stream.charAt(i) == '\n') {
                currentLn++;
            }
            if (ln == currentLn) {
                if (stream.charAt(i) != '\n'){
                    listing += stream.charAt(i);
                }
            }
        }
        return listing;
    }

    /**
     * Pre-conditions: node must be a valid TreeNode with name identifier.
     * Post-conditions: Adds a name symbol table record for specified node.
     */
    private void newName(TreeNode node) {
        Token token = tokenList.peek();
        StRec symbol = new StRec(token.getStr(), token.getLn());

        if (symbolTable.lookup(symbol.getName(), scope) != null){ //If exists in symbol table
            semanticError(token.getStr() + " is already defined in this scope", token.getLn());
        }
        symbolTable.insert(symbol, scope);
        node.setName(symbolTable.lookup(symbol.getName(),scope));

        tokenList.poll();
    }

    private StRec getName() {
        Token token = tokenList.peek();
        StRec name = symbolTable.lookup(token.getStr(), scope);

        if (name == null){ //If name does not exist in symbol table
            semanticError(token.getStr() + " is not defined in this scope", token.getLn());
        }
        tokenList.poll();
        return name;
    }

    /**
     * Pre-conditions: node must be a valid TreeNode with type identifier.
     * Post-conditions: Adds a type symbol table record for specified node.
     */
    private void newType(TreeNode node) {
        Token token = tokenList.peek();
        StRec symbol = symbolTable.lookup(token.getStr(), SymbolTable.GLOBALS);
        if (symbol == null){ //If symbol does not exist in symbol table
            semanticError(token.getStr() + " is not defined in this scope", token.getLn());
        }
        node.setType(symbol);
        node.getName().setTypeName(symbol);
        //todo set the hashmap of this (this is custom types, ie symbol.setHashMap

        tokenList.poll();
    }

    /**
     * Pre-conditions: node must be a valid TreeNode with type identifier.
     * Post-conditions: Adds a type symbol table record for specified node.
     */
    private void newType(TreeNode node, String type) {
        StRec symbol = stringToStRec(type);
        node.setType(symbol);
        node.getName().setTypeName(symbol);
    }

    private StRec stringToStRec(String type){
        switch(type){
            case "integer":
                return StRec.INTTYPE;
            case "VOID":
                return StRec.VOIDTYPE;
            case "real":
                return StRec.REALTYPE;
            case "boolean":
                return StRec.BOOLTYPE;
            default:
//                StRec symbol = symbolTable.lookup(type, SymbolTable.GLOBALS);
//                if (symbol != null){
//                    return symbol;
//                }
                return null;
        }
    }

    private void checkParams(TreeNode arglist){
//        TreeNode plist = symbolTable.lookup(arglist.getName().getName(), SymbolTable.GLOBALS)

    }

    /*******************************************************************
     * These node methods are used to create the syntax analysis of CD *
     *******************************************************************/

    private TreeNode program(){
        TreeNode program = new TreeNode(Node.NPROG);
        if (tokenList.peek().value() == TokId.TCD){ //Checking for 'CD'
            tokenList.poll();

            newName(program);
            program.setLeft(globals());
            program.setMiddle(funcs());
            program.setRight(mainbody());
            return program;
        }
        return syntaxError();
    }

    private TreeNode globals(){
        Token current = tokenList.peek();
        TreeNode globals = new TreeNode(Node.NGLOB);

        if (current.value() == TokId.TCONS){ //Checking for 'constants'
            globals.setLeft(consts());
            current = tokenList.peek();
        }
        if (current.value() == TokId.TTYPS){ //Checking for 'types'
            globals.setMiddle(types());
            current = tokenList.peek();
        }
        if (current.value() == TokId.TARRS){ //Checking for 'arrays'
            globals.setRight(arrays());
        }
        return globals;
    }

    private TreeNode consts() {
        if (tokenList.peek().value() == TokId.TCONS){
            tokenList.poll();
            return initlist();
        }
        return null;
    }

    private TreeNode initlist() {
        return new TreeNode(Node.NILIST, init(), initlisttail());
    }

    private TreeNode initlisttail(){
        if (tokenList.peek().value() == TokId.TCOMA){
            tokenList.poll();
            return initlist();
        }
        return null;
    }

    private TreeNode init(){
        TreeNode init = new TreeNode(Node.NINIT);
        newName(init);
        if (tokenList.peek().value() == TokId.TISKW){
            tokenList.poll();
            init.setLeft(expr());
            return init;
        }
        return syntaxError();
    }

    private TreeNode types() {
        if (tokenList.peek().value() == TokId.TTYPS){
            tokenList.poll();
            return typelist();
        }
        return null;
    }

    private TreeNode arrays() {
        if (tokenList.peek().value() == TokId.TARRS){
            tokenList.poll();
            return arrdecls();
        }
        return null;
    }

    private TreeNode funcs() {
        if (tokenList.peek().value() == TokId.TFUNC){
            return new TreeNode(Node.NFUNCS, func(), funcs());
        }
        return null;
    }

    private TreeNode mainbody() {
        scope = SymbolTable.MAIN;
        TreeNode mainbody = new TreeNode(Node.NMAIN);

        if (tokenList.peek().value() == TokId.TMAIN){ //Checking for 'main'
            tokenList.poll();
            mainbody.setLeft(slist());

            if (tokenList.peek().value() == TokId.TBEGN){ //Checking for 'begin'
                tokenList.poll();
                mainbody.setRight(stats());

                if (tokenList.peek().value() == TokId.TENDK){ //Checking for 'end'
                    tokenList.poll();

                    if (tokenList.peek().value() == TokId.TCD){ //Checking for 'CD'
                        tokenList.poll();

                        StRec programName = symbolTable.lookup(tokenList.peek().getStr(), SymbolTable.GLOBALS);
                        if (programName == null){ //If EOF name is not the same as program name
                            semanticError("EOF name not equal to program name", tokenList.peek().getLn());
                        }
                        mainbody.setName(getName());
                        return mainbody;
                    }
                }
            }
        }
        return syntaxError();
    }

    private TreeNode slist() {
        return new TreeNode(Node.NSDLST, sdecl(), slisttail());
    }

    private TreeNode slisttail() {
        if (tokenList.peek().value() == TokId.TCOMA){
            tokenList.poll();
            return slist();
        }
        return null;
    }

    private TreeNode typelist() {
        return new TreeNode(Node.NTYPEL, type(), typelisttail());
    }

    private TreeNode typelisttail() {
        if (tokenList.peek().value() == TokId.TIDNT){
            return typelist();
        }
        return null;
    }

    private TreeNode type() {
        if (tokenList.peek().value() == TokId.TIDNT){
            TreeNode type = new TreeNode(Node.NATYPE);
            newName(type);

            if (tokenList.peek().value() == TokId.TISKW){
                tokenList.poll();
                return typetail(type);
            }
        }
        return syntaxError();
    }

    private TreeNode typetail(TreeNode type) {
        if (tokenList.peek().value() == TokId.TARRY){ //Checking for 'array'
            tokenList.poll();

            if (tokenList.peek().value() == TokId.TLBRK){ //Checking for '['
                tokenList.poll();
                type.setLeft(expr());

                if (tokenList.peek().value() == TokId.TRBRK){ //Checking for ']'
                    tokenList.poll();

                    if (tokenList.peek().value() == TokId.TOFKW){ //Checking for 'of'
                        tokenList.poll();
                        newType(type);
                        return type;
                    }
                }
            }
        }
        else{
            type.setValue(Node.NRTYPE);
            type.setLeft(fields());

            if (tokenList.peek().value() == TokId.TENDK){
                tokenList.poll();
                return type;
            }
        }
        return syntaxError();
    }

    private TreeNode fields() {
        return new TreeNode(Node.NFLIST, sdecl(), fieldstail());
    }

    private TreeNode fieldstail() {
        if (tokenList.peek().value() == TokId.TCOMA){
            tokenList.poll();
            return fields();
        }
        return null;
    }

    private TreeNode sdecl() {
        TreeNode sdecl = new TreeNode(Node.NSDECL);
        newName(sdecl);
        if (tokenList.peek().value() == TokId.TCOLN){
            tokenList.poll();
            String stype = stype(); //Determine the structure type
            newType(sdecl, stype);
            if (stype != null){ //Ensure it is a valid structure
                return sdecl;
            }
        }
        return syntaxError();
    }

    private TreeNode arrdecls() {
        return new TreeNode(Node.NALIST, arrdecl(), arrdeclstail());
    }

    private TreeNode arrdeclstail() {
        if (tokenList.peek().value() == TokId.TCOMA){
            tokenList.poll();
            return arrdecls();
        }
        return null;
    }

    private TreeNode arrdecl() {
        TreeNode arrdecl = new TreeNode(Node.NARRD);
        newName(arrdecl);
        if (tokenList.peek().value() == TokId.TCOLN){
            tokenList.poll();
            newType(arrdecl);
            return arrdecl;
        }
        return syntaxError();
    }

    private TreeNode func() {
        TreeNode func = new TreeNode(Node.NFUND);
        if (tokenList.peek().value() == TokId.TFUNC){ //Checking for 'func'
            tokenList.poll();

            newName(func);
            scope = func.getName().getName();
            symbolTable.addFunction(scope);

            if (tokenList.peek().value() == TokId.TLPAR) { //Checking for '('
                tokenList.poll();
                func.setLeft(plist());

                if (tokenList.peek().value() == TokId.TRPAR) { //Checking for ')'
                    tokenList.poll();

                    if (tokenList.peek().value() == TokId.TCOLN) { //Checking for ':'
                        tokenList.poll();
                        newType(func, rtype());
                        return funcbody(func);
                    }
                }
            }
        }
        return syntaxError();
    }

    private String rtype() {
        Token current = tokenList.peek();
        if (current.value() == TokId.TVOID){ //If return value is void
            tokenList.poll();
            return "VOID";
        }
        else{ //Check other types
            return stype();
        }
    }

    private TreeNode plist() {
        Token current = tokenList.peek();
        if (current.value() == TokId.TCNST || current.value() == TokId.TIDNT){
            return params();
        }
        return null;
    }

    private TreeNode params() {
        return new TreeNode(Node.NPLIST, param(), paramstail());
    }

    private TreeNode paramstail() {
        if (tokenList.peek().value() == TokId.TCOMA){
            tokenList.poll();
            return params();
        }
        return null;
    }

    private TreeNode param() {
        if (tokenList.peek().value() == TokId.TCNST){
            tokenList.poll();
            return new TreeNode(Node.NARRC, arrdecl());
        }
        TreeNode paramvar = paramvar();
        if (paramvar.getValue() == Node.NSDECL){
            TreeNode param = new TreeNode(Node.NSIMP);
            param.setName(paramvar.getName());
            param.setType(paramvar.getType());
            return param;
        }
        else if (paramvar.getValue() == Node.NARRD){
            TreeNode param = new TreeNode(Node.NARRP);
            param.setName(paramvar.getName());
            param.setType(paramvar.getType());
            return param;
        }
        return syntaxError();
    }

    private TreeNode paramvar() {
        TreeNode paramvar = new TreeNode(Node.NUNDEF);
        newName(paramvar);
        if (tokenList.peek().value() == TokId.TCOLN){
            tokenList.poll();
            return paramvartail(paramvar);
        }
        return syntaxError();
    }

    private TreeNode paramvartail(TreeNode paramvar) {
        if (tokenList.peek().value() == TokId.TIDNT){
            newType(paramvar);
            paramvar.setValue(Node.NARRD);
        }
        else{
            newType(paramvar, stype());
            paramvar.setValue(Node.NSDECL);
        }
        return paramvar;
    }

    private TreeNode funcbody(TreeNode func) {
        func.setMiddle(locals());
        if (tokenList.peek().value() == TokId.TBEGN){ //Checking for 'begin'
            tokenList.poll();
            func.setRight(stats());

            if (tokenList.peek().value() == TokId.TENDK){ //Checking for 'end'
                tokenList.poll();
                scope = SymbolTable.GLOBALS;
                return func;
            }
        }
        return syntaxError();
    }

    private TreeNode locals() {
        if (tokenList.peek().value() == TokId.TIDNT){
            return dlist();
        }
        return null;
    }

    private TreeNode dlist() {
        return new TreeNode(Node.NDLIST, decl(), dlisttail());
    }

    private TreeNode dlisttail() {
        if (tokenList.peek().value() == TokId.TCOMA){
            tokenList.poll();
            return dlist();
        }
        return null;
    }

    private TreeNode decl() {
        TreeNode decl = new TreeNode(Node.NUNDEF);
        newName(decl);
        Token current = tokenList.peek();
        if (current.value() == TokId.TCOLN){
            tokenList.poll();
            current = tokenList.peek();
            newType(decl, stype());
            if (current.value() == TokId.TINTG || current.value() == TokId.TREAL || current.value() == TokId.TBOOL){
                decl.setValue(Node.NSDECL);
            }
            else{
                decl.setValue(Node.NARRD);
            }
            return decl;
        }
        return syntaxError();
    }

    private String stype() {
        Token current = tokenList.peek();
        switch(current.value()){
            case TINTG:
                tokenList.poll();
                return "integer";
            case TREAL:
                tokenList.poll();
                return "real";
            case TBOOL:
                tokenList.poll();
                return "boolean";
            default:
                tokenList.poll();
//                StRec symbol = symbolTable.lookup(current.getStr(), SymbolTable.GLOBALS);
//                if (symbol != null){
//                    return symbol.getName();
//                }
                return null;
        }
    }

    private TreeNode stats() {
        TreeNode stats = new TreeNode(Node.NSTATS);
        Token current = tokenList.peek();
        if (current.value() == TokId.TFORK || current.value() == TokId.TIFKW){
            stats.setLeft(strstat());
        }
        else{
            stats.setLeft(stat());
            current = tokenList.peek();
            if (current.value() == TokId.TSEMI) {
                tokenList.poll();
            }
            else{
                return syntaxError();
            }
        }
        stats.setRight(statstail());
        return stats;
    }

    private TreeNode statstail() {
        Token current = tokenList.peek();
        if (current.value() == TokId.TENDK || current.value() == TokId.TUNTL || current.value() == TokId.TELSE){
            return null;
        }
        return stats();
    }

    private TreeNode strstat() {
        if (tokenList.peek().value() == TokId.TFORK){
            return forstat();
        }
        if (tokenList.peek().value() == TokId.TIFKW){
            return ifstat();
        }
        return syntaxError();
    }

    private TreeNode stat() {
        Token current = tokenList.peek();
        switch (current.value()) {
            case TREPT:
                return repstat();
            case TIDNT: //Could be asgnstat or callstat
                TreeNode stat = new TreeNode(Node.NUNDEF);
                stat.setName(getName());
                current = tokenList.peek();
                if (current.value() == TokId.TLPAR){ //If callstat
                    return callstat(stat);
                }
                else{ //Otherwise asgnstat
                    return stattail(stat);
                }
            case TINKW:
                return iostat();
            case TOUTP:
                return iostat();
            case TRETN:
                return returnstat();
            default:
                return syntaxError();
        }
    }

    private TreeNode stattail(TreeNode stat) {
        stat.setValue(Node.NSIMV);
        if (tokenList.peek().value() == TokId.TLBRK){
            return asgnstat(vararr(stat), false);
        }
        else{
            return asgnstat(stat, false);
        }
    }

    private TreeNode forstat() {
        TreeNode forstat = new TreeNode(Node.NFORL);
        if (tokenList.peek().value() == TokId.TFORK){ //Checking for 'for'
            tokenList.poll();

            if (tokenList.peek().value() == TokId.TLPAR){ //Checking for '('
                tokenList.poll();
                forstat.setLeft(asgnlist());

                if (tokenList.peek().value() == TokId.TSEMI){ //Checking for ';'
                    tokenList.poll();
                    forstat.setMiddle(bool());

                    if (tokenList.peek().value() == TokId.TRPAR){ //Checking for ')'
                        tokenList.poll();
                        forstat.setRight(stats());

                        if (tokenList.peek().value() == TokId.TENDK){ //Checking for 'end'
                            tokenList.poll();
                            return forstat;
                        }
                    }
                }
            }
        }
        return syntaxError();
    }

    private TreeNode repstat() {
        TreeNode repstat = new TreeNode(Node.NREPT);
        if (tokenList.peek().value() == TokId.TREPT){ //Checking for 'repeat'
            tokenList.poll();

            if (tokenList.peek().value() == TokId.TLPAR){ //Checking for '('
                tokenList.poll();
                repstat.setLeft(asgnlist());

                if (tokenList.peek().value() == TokId.TRPAR){ //Checking for ')'
                    tokenList.poll();
                    repstat.setMiddle(stats());

                    if (tokenList.peek().value() == TokId.TUNTL){ //Checking for 'until'
                        tokenList.poll();
                        repstat.setRight(bool());
                    }
                }
            }
        }
        return syntaxError();
    }

    private TreeNode asgnlist() {
        if (tokenList.peek().value() == TokId.TIDNT){
            return alist();
        }
        return null;
    }

    private TreeNode alist() {
        return new TreeNode(Node.NASGNS, asgnstat(new TreeNode(Node.NUNDEF),true), alisttail());
    }

    private TreeNode alisttail() {
        if (tokenList.peek().value() == TokId.TCOMA){
            tokenList.poll();
            return alist();
        }
        return null;
    }

    private TreeNode ifstat() {
        TreeNode ifstat = new TreeNode(Node.NIFTH);
        if (tokenList.peek().value() == TokId.TIFKW){ //Checking for 'is'
            tokenList.poll();

            if (tokenList.peek().value() == TokId.TLPAR){ //Checking for '('
                tokenList.poll();
                ifstat.setLeft(bool());

                if (tokenList.peek().value() == TokId.TRPAR){ //Checking for ')'
                    tokenList.poll();
                    ifstat.setMiddle(stats());
                    ifstat.setRight(elsestat());

                    if (tokenList.peek().value() == TokId.TENDK){ //Checking for 'end'
                        tokenList.poll();
                        return ifstat;
                    }
                }
            }
        }
        return syntaxError();
    }

    private TreeNode elsestat() {
        if (tokenList.peek().value() == TokId.TELSE){
            tokenList.poll();
            return new TreeNode(Node.NIFTE, stats());
        }
        return null;
    }

    private TreeNode asgnstat(TreeNode var, boolean consume) {
        TreeNode asgnstat = new TreeNode(Node.NASGN);
        if (consume){ //If we need to consume the identifier
            asgnstat.setLeft(var());
        }
        else{ //If identifier is already consumed
            asgnstat.setLeft(var);
        }
        if (tokenList.peek().value() == TokId.TASGN){
            tokenList.poll();
            asgnstat.setRight(bool());
            return asgnstat;
        }
        return syntaxError();
    }

    private TreeNode iostat() {
        if (tokenList.peek().value() == TokId.TINKW){  //Checking for 'In'
            tokenList.poll();

            if (tokenList.peek().value() == TokId.TINPT){  //Checking for '>>'
                tokenList.poll();
                return new TreeNode(Node.NINPUT, vlist());
            }
        }
        else if (tokenList.peek().value() == TokId.TOUTP){ //Checking for 'Out'
            tokenList.poll();

            if (tokenList.peek().value() == TokId.TASGN){ //Checking for '<<'
                tokenList.poll();
                return iostatcall(new TreeNode(Node.NOUTP));
            }
        }
        return syntaxError();
    }

    private TreeNode iostatcall(TreeNode iostat) {
        if (tokenList.peek().value() == TokId.TOUTL){
            tokenList.poll();
            iostat.setValue(Node.NOUTL);
            return iostat;
        }
        iostat.setLeft(prlist());
        return iostatcalltail(iostat);
    }

    private TreeNode iostatcalltail(TreeNode prlist) {
        if (tokenList.peek().value() == TokId.TASGN){
            tokenList.poll();

            if (tokenList.peek().value() == TokId.TOUTL){
                tokenList.poll();
                prlist.setValue(Node.NOUTL);
            }
            return syntaxError();
        }
        return prlist;
    }

    private TreeNode callstat(TreeNode stat) {
        if (tokenList.peek().value() == TokId.TLPAR){
            tokenList.poll();
            stat.setValue(Node.NCALL);
            stat.setLeft(callstattail());

            if (tokenList.peek().value() == TokId.TRPAR){
                tokenList.poll();

                checkParams(stat);
                return stat;
            }
        }
        return syntaxError();
    }

    private TreeNode callstattail() {
        Token current = tokenList.peek();
        if (current.value() == TokId.TNOTK || current.value() == TokId.TIDNT || current.value() == TokId.TILIT
                || current.value() == TokId.TFLIT || current.value() == TokId.TTRUE || current.value() == TokId.TFALS
                || current.value() == TokId.TLPAR){
            return elist();
        }
        return null;
    }

    private TreeNode returnstat() {
        if (tokenList.peek().value() == TokId.TRETN){
            tokenList.poll();
            return new TreeNode(Node.NRETN, returnstattail());
        }
        return null;
    }

    private TreeNode returnstattail() {
        Token current = tokenList.peek();
        if (current.value() == TokId.TIDNT || current.value() == TokId.TILIT || current.value() == TokId.TFLIT
                || current.value() == TokId.TTRUE || current.value() == TokId.TFALS || current.value() == TokId.TLPAR){
            return elist();
        }
        return null;
    }

    private TreeNode vlist() {
        return new TreeNode(Node.NVLIST, var(), vlisttail());
    }

    private TreeNode vlisttail() {
        if (tokenList.peek().value() == TokId.TCOMA){
            tokenList.poll();
            return vlist();
        }
        return null;
    }

    private TreeNode var() {
        TreeNode var = new TreeNode(Node.NSIMV);
        var.setName(getName());
        return vararr(var);
    }

    private TreeNode vararr(TreeNode var) {
        if (tokenList.peek().value() == TokId.TLBRK){
            tokenList.poll();
            var.setValue(Node.NAELT);
            var.setLeft(expr());

            if (tokenList.peek().value() == TokId.TRBRK){
                tokenList.poll();
                return vararrtail(var);
            }
        }
        else{
            return var;
        }
        return syntaxError();
    }

    private TreeNode vararrtail(TreeNode vararr) {
        if (tokenList.peek().value() == TokId.TDOTT){
            tokenList.poll();
            vararr.setValue(Node.NARRV);
            vararr.setRight(var());
        }
        return vararr;
    }

    private TreeNode elist() {
        return new TreeNode(Node.NEXPL, bool(), elisttail());
    }

    private TreeNode elisttail() {
        if (tokenList.peek().value() == TokId.TCOMA){
            tokenList.poll();
            return elist();
        }
        return null;
    }

    private TreeNode bool() {
        return booltail(rel());
    }

    private TreeNode booltail(TreeNode rel) {
        Token current = tokenList.peek();
        if (current.value() == TokId.TANDK || current.value() == TokId.TORKW || current.value() == TokId.TXORK){
            TreeNode logop = logop(rel);
            logop.setRight(rel());
            return booltail(logop);
        }
        return rel;
    }

    private TreeNode rel() {
        Token current = tokenList.peek();
        if (current.value() == TokId.TNOTK){
            return new TreeNode(Node.NNOT, rel());
        }
        return reltail(expr());
    }

    private TreeNode reltail(TreeNode expr) {
        Token current = tokenList.peek();
        if (current.value() == TokId.TDEQL || current.value() == TokId.TNEQL || current.value() == TokId.TGRTR
                || current.value() == TokId.TLEQL || current.value() == TokId.TLESS || current.value() == TokId.TGREQ){
            TreeNode relop = relop(expr);
            relop.setRight(expr());
            return relop;
        }
        return expr;
    }

    private TreeNode logop(TreeNode rel){
        Token current = tokenList.peek();
        if (current.value() == TokId.TANDK){ //Checking for 'and'
            TreeNode logop = new TreeNode(Node.NAND);
            logop.setLeft(rel);
            tokenList.poll();
            return logop;
        }
        if (current.value() == TokId.TORKW){ //Checking for 'or'
            TreeNode logop = new TreeNode(Node.NOR);
            logop.setLeft(rel);
            tokenList.poll();
            return logop;
        }
        if (current.value() == TokId.TXORK){ //Checking for 'xor'
            TreeNode logop = new TreeNode(Node.NXOR);
            logop.setLeft(rel);
            tokenList.poll();
            return logop;
        }
        return syntaxError();
    }

    private TreeNode relop(TreeNode expr){
        Token current = tokenList.peek();
        if (current.value() == TokId.TDEQL){ //Checking for '=='
            tokenList.poll();
            return new TreeNode(Node.NEQL, expr);
        }
        if (current.value() == TokId.TNEQL){ //Checking for '!='
            tokenList.poll();
            return new TreeNode(Node.NNEQ, expr);
        }
        if (current.value() == TokId.TGRTR){ //Checking for '>'
            tokenList.poll();
            return new TreeNode(Node.NGRT, expr);
        }
        if (current.value() == TokId.TLEQL){ //Checking for '<='
            tokenList.poll();
            return new TreeNode(Node.NLEQ, expr);
        }
        if (current.value() == TokId.TLESS){ //Checking for '<'
            tokenList.poll();
            return new TreeNode(Node.NLSS, expr);
        }
        if (current.value() == TokId.TGREQ){ //Checking for '>'
            tokenList.poll();
            return new TreeNode(Node.NGEQ, expr);
        }
        return expr;
    }

    private TreeNode expr() {
        return exprtail(term());
    }

    private TreeNode exprtail(TreeNode term){
        Token current = tokenList.peek();
        if (current.value() == TokId.TADDT){ //Checking for '+'
            tokenList.poll();
            return exprtail(new TreeNode(Node.NADD, term, term()));
        }
        if (current.value() == TokId.TSUBT){ //Checking for '-'
            tokenList.poll();
            return exprtail(new TreeNode(Node.NSUB, term, term()));
        }
        return term;
    }

    private TreeNode term(){
        return termtail(fact());
    }

    private TreeNode termtail(TreeNode fact){
        Token current = tokenList.peek();
        if (current.value() == TokId.TMULT){ //Checking for '*'
            tokenList.poll();
            return termtail(new TreeNode(Node.NMUL, fact, fact()));
        }
        if (current.value() == TokId.TDIVT){ //Checking for '/'
            tokenList.poll();
            return termtail(new TreeNode(Node.NDIV, fact, fact()));
        }
        if (current.value() == TokId.TPERC){ //Checking for '%'
            tokenList.poll();
            return termtail(new TreeNode(Node.NMOD, fact, fact()));
        }
        return fact;
    }

    private TreeNode fact(){
        return facttail(exponent());
    }

    private TreeNode facttail(TreeNode exponent){
        Token current = tokenList.peek();
        if (current.value() == TokId.TCART){
            tokenList.poll();
            return facttail(new TreeNode(Node.NPOW, exponent, fact()));
        }
        return exponent;
    }

    private TreeNode exponent(){
        Token current = tokenList.peek();
        TreeNode exponent;
        switch (current.value()){
            case TIDNT: //Checking for identifier
                return var();
            case TILIT: //Checking for integer
                exponent = new TreeNode(Node.NILIT);
                tokenList.poll();
                return exponent;
            case TFLIT: //Checking for float
                exponent = new TreeNode(Node.NFLIT);
                tokenList.poll();
                return new TreeNode(Node.NFLIT);
            case TFUNC: //Checking for 'func'
                exponent = new TreeNode(Node.NUNDEF);
                tokenList.poll();
                return fncall(exponent);
            case TTRUE: //Checking for 'true'
                exponent = new TreeNode(Node.NTRUE);
                tokenList.poll();
                return new TreeNode(Node.NTRUE);
            case TFALS: //Checking for 'false'
                exponent = new TreeNode(Node.NFALS);
                tokenList.poll();
                return new TreeNode(Node.NFALS);
            case TLPAR: //Checking for '('
                tokenList.poll();
                exponent = bool();
                if (tokenList.peek().value() == TokId.TRPAR){ //Consuming right parenthesis
                    tokenList.poll();
                    return exponent;
                }
            default:
                return syntaxError();
        }
    }

    private TreeNode fncall(TreeNode exponent){
        exponent.setName(getName());

        if (tokenList.peek().value() == TokId.TLPAR){
            tokenList.poll();
            TreeNode fncalltail = fncalltail();
            exponent.setValue(fncalltail.getValue());
            exponent.setLeft(fncalltail.getLeft());
            exponent.setRight(fncalltail.getRight());

            if (tokenList.peek().value() == TokId.TRPAR){
                tokenList.poll();
                return exponent;
            }
        }
        return syntaxError();
    }

    private TreeNode fncalltail() {
        Token current = tokenList.peek();
        if (current.value() == TokId.TNOTK || current.value() == TokId.TIDNT || current.value() == TokId.TILIT
                || current.value() == TokId.TFLIT || current.value() == TokId.TTRUE || current.value() == TokId.TFALS
                || current.value() == TokId.TLPAR){
            return elist();
        }
        return null;
    }

    private TreeNode prlist() {
        return new TreeNode(Node.NPRLST, printitem(), prlisttail());
    }

    private TreeNode prlisttail() {
        if (tokenList.peek().value() == TokId.TCOMA){
            
            tokenList.poll();
            return prlist();
        }
        return null;
    }

    private TreeNode printitem() {
        if (tokenList.peek().value() == TokId.TSTRG){
            TreeNode printitem = new TreeNode(Node.NSTRG);
            tokenList.poll();
            return printitem;
        }
        return expr();
    }
}
