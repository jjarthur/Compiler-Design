import java.util.ArrayList;

public class Parser {

    private ArrayList<Token> tokenList;

    public Parser(ArrayList<Token> tokenList){
        this.tokenList = tokenList;
        program();
    }

    private void id() {
        Token current = tokenList.get(0);
        if (current.value() == TokId.TIDNT) {
            tokenList.remove(0);
            //TODO: Create new node with id and add to tree
        }
        //TODO: error?
    }

    private TreeNode program(){
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
        TreeNode mainbody = new TreeNode(Node.NMAIN);

        //Checking for 'main'
        if (tokenList.get(0).value() == TokId.TMAIN){
            tokenList.remove(0);
            mainbody.setLeft(slist());

            //Checking for 'begin'
            if (tokenList.get(0).value() == TokId.TBEGN){
                tokenList.remove(0);
                mainbody.setRight(stats());

                //Checking for 'end'
                if (tokenList.get(0).value() == TokId.TENDK){
                    tokenList.remove(0);

                    //Checking for 'CD'
                    if (tokenList.get(0).value() == TokId.TCD){
                        tokenList.remove(0);
                        id();
                        return mainbody;
                    }
                }
            }
        }
        //TODO: error??
        return null;
    }

    private TreeNode slist() {
        return new TreeNode(Node.NSDLST, sdecl(), slisttail());
    }

    private TreeNode slisttail() {
        if (tokenList.get(0).value() == TokId.TCOMA){
            tokenList.remove(0);
            return slist();
        }
        return null;
    }

    private TreeNode typelist() {
        return new TreeNode(Node.NTYPEL, type(), typelisttail());
    }

    private TreeNode typelisttail() {
        if (tokenList.get(0).value() == TokId.TIDNT) {
            id();
            return typelist();
        }
        return null;
    }

    //TODO: incomplete
    private TreeNode type() {
        return null;
    }

    //TODO: incomplete
    private TreeNode typetail() {
        return null;
    }

    private TreeNode fields() {
        return new TreeNode(Node.NFLIST, sdecl(), fieldstail());
    }

    private TreeNode fieldstail() {
        if (tokenList.get(0).value() == TokId.TIDNT){
            id();
            return sdecl();
        }
        return null;
    }

    //TODO: incomplete
    private TreeNode sdecl() {
        return null;
    }

    private TreeNode arrdecls() {
        return new TreeNode(Node.NALIST, arrdecl(), arrdeclstail());
    }

    private TreeNode arrdeclstail() {
        if (tokenList.get(0).value() == TokId.TCOMA){
            tokenList.remove(0);
            return arrdecls();
        }
        return null;
    }

    //TODO: syntax table stuff
    private TreeNode arrdecl() {
        id();
        if (tokenList.get(0).value() == TokId.TCOLN){
            tokenList.remove(0);
            //typeid();
        }
        return null;
    }

    private TreeNode func() {
        TreeNode func = new TreeNode(Node.NFUNCS);
        if (tokenList.get(0).value() == TokId.TFUNC){
            tokenList.remove(0);
            id();

            if (tokenList.get(0).value() == TokId.TLBRK) {
                tokenList.remove(0);
                func.setLeft(plist());

                if (tokenList.get(0).value() == TokId.TRBRK) {
                    tokenList.remove(0);

                    if (tokenList.get(0).value() == TokId.TCOLN) {
                        tokenList.remove(0);
                        func.setMiddle(rtype());
                        func.setRight(funcbody());
                    }
                }
            }
        }
        //TODO: error??
        return null;
    }

    //TODO: syntax table stuff
    private TreeNode rtype() {
        if (tokenList.get(0).value() == TokId.TVOID){
            //void
        }
        return stype();
    }

    //TODO: fix params
    private TreeNode plist() {
        return null;
    }

    //TODO: fix params
    private TreeNode params() {
        return null;
    }

    //TODO: fix params
    private TreeNode paramstail() {
        return null;
    }

    //TODO: fix params
    private TreeNode param() {
        return null;
    }

    //TODO: incomplete
    private TreeNode funcbody() {
        return null;
    }

    //TODO: fix params
    private TreeNode locals() {
        return null;
    }

    //TODO: fix params
    private TreeNode dlist() {
        return null;
    }

    //TODO: fix params
    private TreeNode dlisttail() {
        return null;
    }

    //TODO: fix params
    private TreeNode decl() {
        return null;
    }

    //TODO: incomplete
    private TreeNode stype() {
        return null;
    }

    //TODO: incomplete
    private TreeNode stats() {
        return null;
    }

    //TODO: incomplete
    private TreeNode statstail() {
        return null;
    }

    private TreeNode strstat() {
        if (tokenList.get(0).value() == TokId.TFORK){
            return forstat();
        }
        if (tokenList.get(0).value() == TokId.TIFKW){
            return ifstat();
        }
        return null;
    }

    //TODO: incomplete
    private TreeNode stat() {
        return null;
    }

    private TreeNode forstat() {
        TreeNode forstat = new TreeNode(Node.NFOR);
        if (tokenList.get(0).value() == TokId.TFORK){
            tokenList.remove(0);

            if (tokenList.get(0).value() == TokId.TLBRK){
                tokenList.remove(0);
                forstat.setLeft(asgnlist());

                if (tokenList.get(0).value() == TokId.TSEMI){
                    tokenList.remove(0);
                    forstat.setMiddle(bool());

                    if (tokenList.get(0).value() == TokId.TRBRK){
                        tokenList.remove(0);
                        forstat.setRight(stats());

                        if (tokenList.get(0).value() == TokId.TENDK){
                            tokenList.remove(0);
                        }
                    }
                }
            }
        }
        //TODO: error??
        return null;
    }

    private TreeNode repstat() {
        TreeNode repstat = new TreeNode(Node.NREPT);
        if (tokenList.get(0).value() == TokId.TREPT){
            tokenList.remove(0);

            if (tokenList.get(0).value() == TokId.TLBRK){
                tokenList.remove(0);
                repstat.setLeft(asgnlist());

                if (tokenList.get(0).value() == TokId.TRBRK){
                    tokenList.remove(0);
                    repstat.setMiddle(stats());

                    if (tokenList.get(0).value() == TokId.TUNTL){
                        tokenList.remove(0);
                        repstat.setRight(bool());
                    }
                }
            }
        }
        //TODO: error??
        return null;
    }

    private TreeNode asgnlist() {
        if (tokenList.get(0).value() == TokId.TIDNT){
            return alist();
        }
        return null;
    }

    private TreeNode alist() {
        return new TreeNode(Node.NALIST, asgnstat(), alisttail());
    }

    private TreeNode alisttail() {
        if (tokenList.get(0).value() == TokId.TCOMA){
            tokenList.remove(0);
            return alist();
        }
        return null;
    }

    private TreeNode ifstat() {
        TreeNode ifstat = new TreeNode(Node.NIFTH);
        if (tokenList.get(0).value() == TokId.TIFKW){
            tokenList.remove(0);

            if (tokenList.get(0).value() == TokId.TLBRK){
                tokenList.remove(0);
                ifstat.setLeft(bool());

                if (tokenList.get(0).value() == TokId.TRBRK){
                    tokenList.remove(0);
                    ifstat.setMiddle(stats());
                    ifstat.setRight(elsestat());

                    if (tokenList.get(0).value() == TokId.TENDK){
                        tokenList.remove(0);
                    }
                }
            }
        }
        //TODO: error??
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
            tokenList.remove(0);
            return new TreeNode(Node.NAND);
        }
        if (current.value() == TokId.TORKW){
            tokenList.remove(0);
            return new TreeNode(Node.NOR);
        }
        if (current.value() == TokId.TXORK){
            tokenList.remove(0);
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
            tokenList.remove(0);
            return new TreeNode(Node.NEQL);
        }
        if (current.value() == TokId.TNEQL){
            tokenList.remove(0);
            return new TreeNode(Node.NNEQ);
        }
        if (current.value() == TokId.TGRTR){
            tokenList.remove(0);
            return new TreeNode(Node.NGTR);
        }
        if (current.value() == TokId.TLEQL){
            tokenList.remove(0);
            return new TreeNode(Node.NLEQ);
        }
        if (current.value() == TokId.TLESS){
            tokenList.remove(0);
            return new TreeNode(Node.NLSS);
        }
        if (current.value() == TokId.TGREQ){
            tokenList.remove(0);
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
            tokenList.remove(0);
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
