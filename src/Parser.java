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
            //root.setMiddle(funcs());
            //root.setRight(mainbody());

        }
        else{
            //no CD, error
        }
        return program;
    }

    private TreeNode globals(){
        Token current = tokenList.get(0);
        TreeNode node = new TreeNode(Node.NGLOB);

        if (current.value() == TokId.TCONS){
            node.setLeft(consts());
        }
        if (current.value() == TokId.TTYPS){
            //node.setMiddle(types());
        }
        if (current.value() == TokId.TARRS){
            //node.setLeft(arrays());
        }
        return node; //What if node is empty?
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
        return null;
    }

    private TreeNode mainbody() {
        return null;
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
        return null;
    }

    private TreeNode elisttail() {
        return null;
    }

    private TreeNode bool() {
        return null;
    }

    private TreeNode booltail() {
        return null;
    }

    private TreeNode rel() {
        return null;
    }

    private TreeNode reltail() {
        return null;
    }

    private TreeNode logop(){
        return exprtail(term());
    }

    private TreeNode relop() {
        return null;
    }

    private TreeNode expr() {
        return null;
    }

    //TODO: term() ??
    private TreeNode exprtail(TreeNode term){
        Token current = tokenList.get(0);
        if (current.value() == TokId.TADDT){
            tokenList.remove(0);
            return new TreeNode(Node.NADD, term, term());
        }
        if (current.value() == TokId.TADDT){
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
                TreeNode node = bool();
                //TODO: check for right parenthesis
                return node;
        }
        //TODO: no 'is' keyword, throw error
        //return null for now
        return null;
    }

    private TreeNode fncall(){
        Token current = tokenList.get(0);
        id();
        if (current.value() == TokId.TLPAR){
            TreeNode node = fncalltail();
            //TODO: check for right parenthesis
            return node;
        }
        //TODO: error?
        return null;
    }

    private TreeNode fncalltail() {
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
