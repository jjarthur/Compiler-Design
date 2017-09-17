import java.util.LinkedList;
import java.util.Queue;

public class Parser {

    private Queue<Token> tokenList;
    public Queue<TreeNode> debug = new LinkedList<>();

    public Parser(Queue<Token> tokenList){
        this.tokenList = tokenList;
        printTree();
    }

    public void printTree() {
        System.out.println(program().toString(0));
    }

    private void id() {
        Token current = tokenList.peek();
        if (current.value() == TokId.TIDNT) {
            System.out.println(tokenList.peek());
            tokenList.poll();
            //TODO: Create new node with id and add to tree
        }
        //TODO: error?
    }

    private TreeNode program(){
        TreeNode program = new TreeNode(Node.NPROG);
        if (tokenList.peek().value() == TokId.TCD){ //CD
            System.out.println(tokenList.peek());
            tokenList.poll();

            id(); //Program name
            program.setLeft(globals());
            program.setMiddle(funcs());
            program.setRight(mainbody());
            return program;
        }
        //TODO: error?
        return null;
    }

    private TreeNode globals(){
        Token current = tokenList.peek();
        TreeNode globals = new TreeNode(Node.NGLOB);

        if (current.value() == TokId.TCONS){
            globals.setLeft(consts());
            current = tokenList.peek();
        }
        if (current.value() == TokId.TTYPS){
            globals.setMiddle(types());
            current = tokenList.peek();
        }
        if (current.value() == TokId.TARRS){
            globals.setLeft(arrays());
        }
        return globals; //What if globals is empty?
    }

    private TreeNode consts() {
        if (tokenList.peek().value() == TokId.TCONS){
            System.out.println(tokenList.peek());
            tokenList.poll();
            return initlist();
        }
        return null;
    }

    //TODO: tidy debug
    private TreeNode initlist() {
        TreeNode initlist = new TreeNode(Node.NILIST, init(), initlisttail());
        debug.add(initlist);
        return initlist;
    }

    private TreeNode initlisttail(){
        if (tokenList.peek().value() == TokId.TCOMA){
            System.out.println(tokenList.peek());
            tokenList.poll();
            return initlist();
        }
        return null;
    }

    private TreeNode init(){
        id();
        if (tokenList.peek().value() == TokId.TISKW){
            System.out.println(tokenList.peek());
            tokenList.poll();
            return new TreeNode(Node.NINIT, expr());
        }
        //TODO: no 'is' keyword, throw error
        //return null for now
        return null;
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
        TreeNode mainbody = new TreeNode(Node.NMAIN);

        //Checking for 'main'
        if (tokenList.peek().value() == TokId.TMAIN){
            tokenList.poll();
            mainbody.setLeft(slist());

            //Checking for 'begin'
            if (tokenList.peek().value() == TokId.TBEGN){
                tokenList.poll();
                mainbody.setRight(stats());

                //Checking for 'end'
                if (tokenList.peek().value() == TokId.TENDK){
                    tokenList.poll();

                    //Checking for 'CD'
                    if (tokenList.peek().value() == TokId.TCD){
                        tokenList.poll();
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
        if (tokenList.peek().value() == TokId.TIDNT) {
            id();
            return typelist();
        }
        return null;
    }

    //TODO: incomplete
    private TreeNode type() {
        if (tokenList.peek().value() == TokId.TIDNT){
            id();

            if (tokenList.peek().value() == TokId.TISKW){
                tokenList.poll();
                return typetail(new TreeNode(Node.NUNDEF));
            }
        }
        return null;
    }

    //TODO: incomplete
    private TreeNode typetail(TreeNode type) {
        if (tokenList.peek().value() == TokId.TARRY){
            tokenList.poll();

            if (tokenList.peek().value() == TokId.TLBRK){
                tokenList.poll();
                type.setLeft(expr());

                if (tokenList.peek().value() == TokId.TRBRK){
                    tokenList.poll();

                    if (tokenList.peek().value() == TokId.TOFKW){
                        tokenList.poll();
                        id();
                        type.setValue(Node.NATYPE);
                        return type;
                    }
                }
            }
        }
        else{
            type.setLeft(fields());

            if (tokenList.peek().value() == TokId.TENDK){
                tokenList.poll();
                type.setValue(Node.NRTYPE);
                return type;
            }
        }
        //TODO: error ??
        return null;
    }

    private TreeNode fields() {
        return new TreeNode(Node.NFLIST, sdecl(), fieldstail());
    }

    private TreeNode fieldstail() {
        if (tokenList.peek().value() == TokId.TIDNT){
            id();
            return sdecl();
        }
        return null;
    }

    private TreeNode sdecl() {
        id();
        if (tokenList.peek().value() == TokId.TCOLN){
            tokenList.poll();
            return stype();
        }
        //TODO: error ??
        return null;
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

    //TODO: Symbol table stuff
    private TreeNode arrdecl() {
        id();
        if (tokenList.peek().value() == TokId.TCOLN){
            tokenList.poll();
            //typeid();
        }
        return null;
    }

    //TODO: Symbol table stuff
    private TreeNode func() {
        TreeNode func = new TreeNode(Node.NFUNCS);
        if (tokenList.peek().value() == TokId.TFUNC){
            tokenList.poll();
            id();

            if (tokenList.peek().value() == TokId.TLPAR) {
                tokenList.poll();
                func.setLeft(plist());

                if (tokenList.peek().value() == TokId.TRPAR) {
                    tokenList.poll();

                    if (tokenList.peek().value() == TokId.TCOLN) {
                        tokenList.poll();
                        return funcbody(func);
                    }
                }
            }
        }
        //TODO: error??
        return null;
    }

    //TODO: Symbol table stuff
    private TreeNode rtype() {
        if (tokenList.peek().value() == TokId.TVOID){
            //void
        }
        return stype();
    }

    private TreeNode plist() {
        Token current = tokenList.peek();
        if (current.value() == TokId.TCONS || current.value() == TokId.TIDNT){
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
        if (tokenList.peek().value() == TokId.TCONS){
            tokenList.poll();
            return new TreeNode(Node.NARRC, arrdecl());
        }
        if (paramvar().getValue() == Node.NSDECL){
            return new TreeNode(Node.NSIMP);
        }
        if (paramvar().getValue() == Node.NARRD){
            return new TreeNode(Node.NARRP);
        }
        //TODO: error ??
        return null;
    }

    private TreeNode paramvar() {
        if (tokenList.peek().value() == TokId.TCOMA){
            tokenList.poll();
            return paramvartail();
        }
        //TODO: error ??
        return null;
    }

    //TODO: Syntax table stuff..
    private TreeNode paramvartail() {
        if (tokenList.peek().value() == TokId.TIDNT){
            id();
            return new TreeNode(Node.NARRD);
        }
        stype();
        return new TreeNode(Node.NSDECL);
    }

    private TreeNode funcbody(TreeNode func) {
        func.setMiddle(locals());
        if (tokenList.peek().value() == TokId.TBEGN){
            tokenList.poll();
            func.setRight(stats());

            if (tokenList.peek().value() == TokId.TENDK){
                tokenList.poll();

                return func;
            }
        }
        //TODO: error ??
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
        if (tokenList.peek().value() == TokId.TFORK){
            return forstat();
        }
        if (tokenList.peek().value() == TokId.TIFKW){
            return ifstat();
        }
        return null;
    }

    //TODO: incomplete
    private TreeNode stat() {
        return null;
    }

    private TreeNode forstat() {
        TreeNode forstat = new TreeNode(Node.NFORL);
        if (tokenList.peek().value() == TokId.TFORK){
            tokenList.poll();

            if (tokenList.peek().value() == TokId.TLPAR){
                tokenList.poll();
                forstat.setLeft(asgnlist());

                if (tokenList.peek().value() == TokId.TSEMI){
                    tokenList.poll();
                    forstat.setMiddle(bool());

                    if (tokenList.peek().value() == TokId.TRPAR){
                        tokenList.poll();
                        forstat.setRight(stats());

                        if (tokenList.peek().value() == TokId.TENDK){
                            tokenList.poll();
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
        if (tokenList.peek().value() == TokId.TREPT){
            tokenList.poll();

            if (tokenList.peek().value() == TokId.TLPAR){
                tokenList.poll();
                repstat.setLeft(asgnlist());

                if (tokenList.peek().value() == TokId.TRPAR){
                    tokenList.poll();
                    repstat.setMiddle(stats());

                    if (tokenList.peek().value() == TokId.TUNTL){
                        tokenList.poll();
                        repstat.setRight(bool());
                    }
                }
            }
        }
        //TODO: error??
        return null;
    }

    private TreeNode asgnlist() {
        if (tokenList.peek().value() == TokId.TIDNT){
            return alist();
        }
        return null;
    }

    private TreeNode alist() {
        return new TreeNode(Node.NALIST, asgnstat(), alisttail());
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
        if (tokenList.peek().value() == TokId.TIFKW){
            tokenList.poll();

            if (tokenList.peek().value() == TokId.TLPAR){
                tokenList.poll();
                ifstat.setLeft(bool());

                if (tokenList.peek().value() == TokId.TRPAR){
                    tokenList.poll();
                    ifstat.setMiddle(stats());
                    ifstat.setRight(elsestat());

                    if (tokenList.peek().value() == TokId.TENDK){
                        tokenList.poll();
                    }
                }
            }
        }
        //TODO: error??
        return null;
    }

    private TreeNode elsestat() {
        if (tokenList.peek().value() == TokId.TELSE){
            tokenList.poll();
            return new TreeNode(Node.NIFTE, stats());
        }
        return null;
    }

    private TreeNode asgnstat() {
        TreeNode asgnstat = new TreeNode(Node.NASGN, var());
        if (tokenList.peek().value() == TokId.TASGN){
            tokenList.poll();
            asgnstat.setRight(bool());
            return asgnstat;
        }
        //TODO: error??
        return null;
    }

    private TreeNode iostat() {
        if (tokenList.peek().value() == TokId.TINKW){
            tokenList.poll();

            if (tokenList.peek().value() == TokId.TINPT){
                tokenList.poll();
                return new TreeNode(Node.NINPUT, vlist());
            }
        }
        else if (tokenList.peek().value() == TokId.TOUTP){
            tokenList.poll();

            if (tokenList.peek().value() == TokId.TASGN){
                tokenList.poll();
                return new TreeNode(Node.NOUTP, iostatcall());
            }
        }
        //TODO: error??
        return null;
    }

    private TreeNode iostatcall() {
        if (tokenList.peek().value() == TokId.TOUTL){
            tokenList.poll();
            return new TreeNode(Node.NOUTL);
        }
        return iostatcalltail();
    }

    private TreeNode iostatcalltail() {
        if (tokenList.peek().value() == TokId.TASGN){
            tokenList.poll();

            if (tokenList.peek().value() == TokId.TOUTL){
                tokenList.poll();
                return new TreeNode(Node.NOUTL);
            }
        }
        return null;
    }

    private TreeNode callstat() {
        id();
        if (tokenList.peek().value() == TokId.TLPAR){
            tokenList.poll();
            TreeNode callstat = new TreeNode(Node.NCALL, callstattail());

            if (tokenList.peek().value() == TokId.TRPAR){
                tokenList.poll();
                return callstat;
            }
        }
        //TODO: error??
        return null;
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
        id();
        return new TreeNode(Node.NSIMV, vararr());
    }

    private TreeNode vararr() {
        TreeNode vararr = new TreeNode(Node.NAELT);
        if (tokenList.peek().value() == TokId.TLBRK){
            tokenList.poll();
            vararr.setLeft(expr());

            if (tokenList.peek().value() == TokId.TRBRK){
                tokenList.poll();
                vararr.setRight(vararrtail());
            }
        }
        //TODO: error?? or epsilon...
        return null;
    }

    private TreeNode vararrtail() {
        if (tokenList.peek().value() == TokId.TDOTT){
            tokenList.poll();
            id();
            //TODO: return
        }
        return null;
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
        return new TreeNode(Node.NBOOL, rel(), booltail());
    }

    private TreeNode booltail() {
        Token current = tokenList.peek();
        if (current.value() == TokId.TANDK || current.value() == TokId.TORKW || current.value() == TokId.TXORK){
            logop();
        }
        return null;
    }

    //TODO: incomplete
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
            relop.setLeft(expr());
            return relop;
        }
        return expr;
    }

    //TODO: Symbol table stuff..
    //TODO: logop children?
    private TreeNode logop(){
        Token current = tokenList.peek();
        if (current.value() == TokId.TANDK){
            tokenList.poll();
            return new TreeNode(Node.NAND);
        }
        if (current.value() == TokId.TORKW){
            tokenList.poll();
            return new TreeNode(Node.NOR);
        }
        if (current.value() == TokId.TXORK){
            tokenList.poll();
            return new TreeNode(Node.NXOR);
        }
        //TODO: error?
        return null;
    }

    //TODO: Symbol table stuff..
    //TODO: relop children?
    private TreeNode relop(TreeNode expr){
        Token current = tokenList.peek();
        if (current.value() == TokId.TDEQL){
            tokenList.poll();
            return new TreeNode(Node.NEQL, expr);
        }
        if (current.value() == TokId.TNEQL){
            tokenList.poll();
            return new TreeNode(Node.NNEQ, expr);
        }
        if (current.value() == TokId.TGRTR){
            tokenList.poll();
            return new TreeNode(Node.NGRT, expr);
        }
        if (current.value() == TokId.TLEQL){
            tokenList.poll();
            return new TreeNode(Node.NLEQ, expr);
        }
        if (current.value() == TokId.TLESS){
            tokenList.poll();
            return new TreeNode(Node.NLSS, expr);
        }
        if (current.value() == TokId.TGREQ){
            tokenList.poll();
            return new TreeNode(Node.NGEQ, expr);
        }
        return expr;
    }

    private TreeNode expr() {
        return exprtail(term());
    }

    //TODO: term() ??
    private TreeNode exprtail(TreeNode term){
        Token current = tokenList.peek();
        if (current.value() == TokId.TADDT){
            tokenList.poll();
            return new TreeNode(Node.NADD, term, term());
        }
        if (current.value() == TokId.TSUBT){
            tokenList.poll();
            return new TreeNode(Node.NSUB, term, term());
        }
        return term;
    }

    private TreeNode term(){
        return termtail(fact());
    }

    //TODO: fact() ??
    private TreeNode termtail(TreeNode fact){
        Token current = tokenList.peek();
        if (current.value() == TokId.TMULT){
            tokenList.poll();
            return new TreeNode(Node.NMUL, fact, fact());
        }
        if (current.value() == TokId.TDIVT){
            tokenList.poll();
            return new TreeNode(Node.NDIV, fact(), fact());
        }
        if (current.value() == TokId.TPERC){
            tokenList.poll();
            return new TreeNode(Node.NMOD, fact(), fact());
        }
        return fact;
    }

    private TreeNode fact(){
        return facttail(exponent());
    }

    //TODO: exponent() ??
    private TreeNode facttail(TreeNode exponent){
        Token current = tokenList.peek();
        if (current.value() == TokId.TCART){
            System.out.println(tokenList.peek());
            tokenList.poll();
            return new TreeNode(Node.NPOW, exponent, fact());
        }
        return exponent;
    }

    //TODO: symbol table entries..
    //TODO: idexp
    private TreeNode exponent(){
        Token current = tokenList.peek();
        switch (current.value()){
            case TIDNT:
                id();
            case TILIT:
                System.out.println(tokenList.peek());
                tokenList.poll();
                return new TreeNode(Node.NILIT);
            case TFLIT:
                tokenList.poll();
                return new TreeNode(Node.NFLIT);
            case TFUNC:
                tokenList.poll();
                return fncall();
            case TTRUE:
                tokenList.poll();
                return new TreeNode(Node.NTRUE);
            case TFALS:
                tokenList.poll();
                return new TreeNode(Node.NFALS);
            case TLPAR:
                tokenList.poll();
                TreeNode exponent = bool();
                if (tokenList.peek().value() == TokId.TLPAR){
                    tokenList.poll();
                    return exponent;
                }
                //TODO: error ??
                return null;
            default:
                //TODO: error ??
                return null;
        }
    }

    private TreeNode fncall(){
        Token current = tokenList.peek();
        id();
        if (current.value() == TokId.TLPAR){
            tokenList.poll();
            TreeNode fncall = fncalltail();
            //TODO: check for right parenthesis
            return fncall;
        }
        //TODO: error?
        return null;
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

    //TODO: Symbol table stuff..
    private TreeNode printitem() {
        if (tokenList.peek().value() == TokId.TSTRG){
            //return string
        }
        return expr();
    }
}
