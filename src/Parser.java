import java.util.ArrayList;

public class Parser {

    private ArrayList<Token> tokenList;

    public Parser(ArrayList<Token> tokenList){
        this.tokenList = tokenList;
    }

    private void id() {
        Token current = tokenList.get(0);
        if (current.value() == TokId.TIDNT) {
            tokenList.remove(0);
            //TODO: Create new node with id and add to tree
        }
        //TODO: error?
    }

    public TreeNode program(){
        TreeNode program = new TreeNode(Node.NPROG);
        if (tokenList.get(0).value() == TokId.TCD){ //CD
            tokenList.remove(0);

            id(); //Program name
            program.setLeft(globals());
            program.setMiddle(funcs());
            program.setRight(mainbody());

        }
        else{
            //no CD, error
        }
        return program;
    }

    private TreeNode globals(){
        Token current = tokenList.get(0);
        TreeNode globals = new TreeNode(Node.NGLOB);

        if (current.value() == TokId.TCONS){
            globals.setLeft(consts());
        }
        if (current.value() == TokId.TTYPS){
            globals.setMiddle(types());
        }
        if (current.value() == TokId.TARRS){
            globals.setLeft(arrays());
        }
        return globals; //What if globals is empty?
    }

    private TreeNode consts() {
        if (tokenList.get(0).value() == TokId.TCONS){
            tokenList.remove(0);
            return initlist();
        }
        return null;
    }

    private TreeNode initlist() {
        return new TreeNode(Node.NILIST, init(), initlisttail());
    }

    private TreeNode initlisttail(){
        if (tokenList.get(0).value() == TokId.TCOMA){
            tokenList.remove(0);
            return init();
        }
        return null;
    }

    private TreeNode init(){
        id();
        if (tokenList.get(0).value() == TokId.TISKW){
            tokenList.remove(0);
            return new TreeNode(Node.NINIT, expr());
        }
        //TODO: no 'is' keyword, throw error
        //return null for now
        return null;
    }

    private TreeNode types() {
        if (tokenList.get(0).value() == TokId.TTYPS){
            tokenList.remove(0);
            return typelist();
        }
        return null;
    }

    private TreeNode arrays() {
        if (tokenList.get(0).value() == TokId.TARRS){
            tokenList.remove(0);
            return arrdecls();
        }
        return null;
    }

    private TreeNode funcs() {
        if (tokenList.get(0).value() == TokId.TFUNC){
            return new TreeNode(Node.NFUNCS, func(), funcs());
        }
        return null;
    }

    private TreeNode mainbody() {
        TreeNode main = new TreeNode(Node.NMAIN);
        if (tokenList.get(0).value() == TokId.TMAIN){
            tokenList.remove(0);

        }
    }

    private TreeNode slist() {
        return null;
    }

    private TreeNode slisttail() {
        return null;
    }

    private TreeNode typelist() {
        return null;
    }

    private TreeNode typelisttail() {
        return null;
    }

    private TreeNode type() {
        return null;
    }

    private TreeNode typetail() {
        return null;
    }

    private TreeNode fields() {
        return null;
    }

    private TreeNode fieldstail() {
        return null;
    }

    private TreeNode sdecl() {
        return null;
    }

    private TreeNode arrdecls() {
        return null;
    }

    private TreeNode arrdeclstail() {
        return null;
    }

    private TreeNode arrdecl() {
        return null;
    }

    private TreeNode func() {
        return null;
    }

    private TreeNode rtype() {
        return null;
    }

    private TreeNode plist() {
        return null;
    }

    private TreeNode params() {
        return null;
    }

    private TreeNode paramstail() {
        return null;
    }

    private TreeNode param() {
        return null;
    }

    private TreeNode funcbody() {
        return null;
    }

    private TreeNode locals() {
        return null;
    }

    private TreeNode dlist() {
        return null;
    }

    private TreeNode dlisttail() {
        return null;
    }

    private TreeNode decl() {
        return null;
    }

    private TreeNode stype() {
        return null;
    }

    private TreeNode stats() {
        return null;
    }

    private TreeNode statstail() {
        return null;
    }

    private TreeNode strstat() {
        return null;
    }

    private TreeNode stat() {
        return null;
    }

    private TreeNode forstat() {
        return null;
    }

    private TreeNode repstat() {
        return null;
    }

    private TreeNode asgnlist() {
        return null;
    }

    private TreeNode alist() {
        return null;
    }

    private TreeNode alisttail() {
        return null;
    }

    private TreeNode ifstat() {
        return null;
    }

    private TreeNode elsestat() {
        return null;
    }

    private TreeNode asgnstat() {
        return null;
    }

    private TreeNode iostat() {
        return null;
    }

    private TreeNode iostatcall() {
        return null;
    }

    private TreeNode iostatcalltail() {
        return null;
    }

    private TreeNode callstat() {
        return null;
    }

    private TreeNode callstattail() {
        return null;
    }

    private TreeNode returnstat() {
        return null;
    }

    private TreeNode returnstattail() {
        return null;
    }

    private TreeNode vlist() {
        return null;
    }

    private TreeNode vlisttail() {
        return null;
    }

    private TreeNode var() {
        return null;
    }

    private TreeNode vararr() {
        return null;
    }

    private TreeNode vararrtail() {
        return null;
    }

    private TreeNode elist() {
        return new TreeNode(Node.NEXPL, bool(), elisttail());
    }

    private TreeNode elisttail() {
        if (tokenList.get(0).value() == TokId.TCOMA){
            tokenList.remove(0);
            return elist();
        }
        return null;
    }

    private TreeNode bool() {
        return new TreeNode(Node.NBOOL, rel(), booltail());
    }

    private TreeNode booltail() {
        Token current = tokenList.get(0);
        if (current.value() == TokId.TANDK || current.value() == TokId.TORKW || current.value() == TokId.TXORK){
            logop();
        }
        return null;
    }

    private TreeNode rel() {
        return null;
    }

    private TreeNode reltail() {
        return null;
    }

    //TODO: Symbol table stuff..
    //TODO: logop children?
    private TreeNode logop(){
        Token current = tokenList.get(0);
        if (current.value() == TokId.TANDK){
            return new TreeNode(Node.NAND);
        }
        if (current.value() == TokId.TORKW){
            return new TreeNode(Node.NOR);
        }
        if (current.value() == TokId.TXORK){
            return new TreeNode(Node.NXOR);
        }
        //TODO: error?
        return null;
    }

    //TODO: Symbol table stuff..
    //TODO: relop children?
    private TreeNode relop(){
        Token current = tokenList.get(0);
        if (current.value() == TokId.TDEQL){
            return new TreeNode(Node.NEQL);
        }
        if (current.value() == TokId.TNEQL){
            return new TreeNode(Node.NNEQ);
        }
        if (current.value() == TokId.TGRTR){
            return new TreeNode(Node.NGTR);
        }
        if (current.value() == TokId.TLEQL){
            return new TreeNode(Node.NLEQ);
        }
        if (current.value() == TokId.TLESS){
            return new TreeNode(Node.NLSS);
        }
        if (current.value() == TokId.TGREQ){
            return new TreeNode(Node.NGEQ);
        }
        //TODO: error?
        return null;
    }

    private TreeNode expr() {
        return exprtail(term());
    }

    //TODO: term() ??
    private TreeNode exprtail(TreeNode term){
        Token current = tokenList.get(0);
        if (current.value() == TokId.TADDT){
            tokenList.remove(0);
            return new TreeNode(Node.NADD, term, term());
        }
        if (current.value() == TokId.TSUBT){
            tokenList.remove(0);
            return new TreeNode(Node.NSUB, term, term());
        }
        return null;
    }

    private TreeNode term(){
        return termtail(fact());
    }

    //TODO: fact() ??
    private TreeNode termtail(TreeNode fact){
        Token current = tokenList.get(0);
        if (current.value() == TokId.TMULT){
            tokenList.remove(0);
            return new TreeNode(Node.NMUL, fact, fact());
        }
        if (current.value() == TokId.TDIVT){
            tokenList.remove(0);
            return new TreeNode(Node.NDIV, fact(), fact());
        }
        if (current.value() == TokId.TPERC){
            tokenList.remove(0);
            return new TreeNode(Node.NMOD, fact(), fact());
        }
        return null;
    }

    private TreeNode fact(){
        return facttail(exponent());
    }

    //TODO: exponent() ??
    private TreeNode facttail(TreeNode exponent){
        Token current = tokenList.get(0);
        if (current.value() == TokId.TCART){
            tokenList.remove(0);
            return new TreeNode(Node.NPOW, exponent, exponent());
        }
        return null;
    }

    //TODO: symbol table entries..
    //TODO: idexp
    private TreeNode exponent(){
        Token current = tokenList.get(0);
        switch (current.value()){
            case TILIT:
                return new TreeNode(Node.NILIT);
            case TFLIT:
                return new TreeNode(Node.NFLIT);
            case TFUNC:
                return fncall();
            case TTRUE:
                return new TreeNode(Node.NTRUE);
            case TFALS:
                return new TreeNode(Node.NFALS);
            case TLPAR:
                tokenList.remove(0);
                TreeNode exponent = bool();
                //TODO: check for right parenthesis
                return exponent;
        }
        //TODO: no 'is' keyword, throw error
        //return null for now
        return null;
    }

    private TreeNode fncall(){
        Token current = tokenList.get(0);
        id();
        if (current.value() == TokId.TLPAR){
            TreeNode fncall = fncalltail();
            //TODO: check for right parenthesis
            return fncall;
        }
        //TODO: error?
        return null;
    }

    private TreeNode fncalltail() {
        Token current = tokenList.get(0);
        if (current.value() == TokId.TTRUE || current.value() == TokId.TFALS || current.value() == TokId.TIDNT
                || current.value() == TokId.TILIT || current.value() == TokId.TLPAR || current.value() == TokId.TNOTK){
            return elist();
        }
        return null;
    }

    private TreeNode prlist() {
        return null;
    }

    private TreeNode printitemtail() {
        return null;
    }

    private TreeNode printitem() {
        return null;
    }
}
