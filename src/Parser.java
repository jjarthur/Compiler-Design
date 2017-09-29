import java.util.Queue;

public class Parser {

    private Queue<Token> tokenList;
    private HashTable symbolTable = new HashTable();

    public Parser(Queue<Token> tokenList){
        this.tokenList = tokenList;
        printNodes();
//        printTree();
    }

    public void printTree() {
        System.out.println(program().toString(0));
    }

    public void printNodes(){
        System.out.println(program().printNodeSpace());
    }

    private TreeNode syntaxError(){
        Token current = tokenList.peek();
        System.out.println("Syntax error on line " + current.getLn());
        while(current.value() != TokId.TSEMI && current.value() != TokId.TEOF){
            tokenList.poll();
            current = tokenList.peek();
        }
        return new TreeNode(Node.NUNDEF);
    }

    private void newName(TreeNode node) {
        Token token = tokenList.peek();
        //symbolTable.insertSymbol(token.getStr(), token.getLn());
        node.setName(new StRec(token.getStr(), token.getLn()));
        //System.out.println(tokenList.peek());
        tokenList.poll();
    }

    private void newType(TreeNode node) {
        Token token = tokenList.peek();
        //symbolTable.insertSymbol(token.getStr(), token.getLn());
        node.setType(new StRec(token.getStr(), token.getLn()));
        //System.out.println(tokenList.peek());
        tokenList.poll();
    }

    private void newType(TreeNode node, String type) {
        Token token = tokenList.peek();
        //symbolTable.insertSymbol(type, token.getLn());
        node.setType(new StRec(type, token.getLn()));
        //System.out.println(tokenList.peek());
    }

    private TreeNode program(){
        TreeNode program = new TreeNode(Node.NPROG);
        if (tokenList.peek().value() == TokId.TCD){ //CD
            //System.out.println(tokenList.peek());
            tokenList.poll();

            newName(program); //Program name
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

        if (current.value() == TokId.TCONS){
            globals.setLeft(consts());
            current = tokenList.peek();
        }
        if (current.value() == TokId.TTYPS){
            globals.setMiddle(types());
            current = tokenList.peek();
        }
        if (current.value() == TokId.TARRS){
            globals.setRight(arrays());
        }
        return globals;
    }

    private TreeNode consts() {
        if (tokenList.peek().value() == TokId.TCONS){
            //System.out.println(tokenList.peek());
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
            //System.out.println(tokenList.peek());
            tokenList.poll();
            return initlist();
        }
        return null;
    }

    private TreeNode init(){
        TreeNode init = new TreeNode(Node.NINIT);
        newName(init);
        if (tokenList.peek().value() == TokId.TISKW){
            //System.out.println(tokenList.peek());
            tokenList.poll();
            init.setLeft(expr());
            return init;
        }
        return syntaxError();
    }

    private TreeNode types() {
        if (tokenList.peek().value() == TokId.TTYPS){
            //System.out.println(tokenList.peek());
            tokenList.poll();
            return typelist();
        }
        return null;
    }

    private TreeNode arrays() {
        if (tokenList.peek().value() == TokId.TARRS){
            //System.out.println(tokenList.peek());
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
            //System.out.println(tokenList.peek());
            tokenList.poll();
            mainbody.setLeft(slist());

            //Checking for 'begin'
            if (tokenList.peek().value() == TokId.TBEGN){
                //System.out.println(tokenList.peek());
                tokenList.poll();
                mainbody.setRight(stats());

                //Checking for 'end'
                if (tokenList.peek().value() == TokId.TENDK){
                    //System.out.println(tokenList.peek());
                    tokenList.poll();

                    //Checking for 'CD'
                    if (tokenList.peek().value() == TokId.TCD){
                        //System.out.println(tokenList.peek());
                        tokenList.poll();
                        newName(mainbody);
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
            //System.out.println(tokenList.peek());
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
                //System.out.println(tokenList.peek());
                tokenList.poll();
                return typetail(type);
            }
        }
        return syntaxError();
    }

    private TreeNode typetail(TreeNode type) {
        if (tokenList.peek().value() == TokId.TARRY){
            //System.out.println(tokenList.peek());
            tokenList.poll();

            if (tokenList.peek().value() == TokId.TLBRK){
                //System.out.println(tokenList.peek());
                tokenList.poll();
                type.setLeft(expr());

                if (tokenList.peek().value() == TokId.TRBRK){
                    //System.out.println(tokenList.peek());
                    tokenList.poll();

                    if (tokenList.peek().value() == TokId.TOFKW){
                        //System.out.println(tokenList.peek());
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
                //System.out.println(tokenList.peek());
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
            //System.out.println(tokenList.peek());
            tokenList.poll();
            return fields();
        }
        return null;
    }

    private TreeNode sdecl() {
        TreeNode sdecl = new TreeNode(Node.NSDECL);
        newName(sdecl);
        if (tokenList.peek().value() == TokId.TCOLN){
            //System.out.println(tokenList.peek());
            tokenList.poll();
            String stype = stype();
            newType(sdecl, stype);
            if (stype != null){
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
            //System.out.println(tokenList.peek());
            tokenList.poll();
            return arrdecls();
        }
        return null;
    }

    private TreeNode arrdecl() {
        TreeNode arrdecl = new TreeNode(Node.NARRD);
        newName(arrdecl);
        if (tokenList.peek().value() == TokId.TCOLN){
            //System.out.println(tokenList.peek());
            tokenList.poll();
            newType(arrdecl);
            return arrdecl;
        }
        return syntaxError();
    }

    private TreeNode func() {
        TreeNode func = new TreeNode(Node.NFUND);
        if (tokenList.peek().value() == TokId.TFUNC){
            //System.out.println(tokenList.peek());
            tokenList.poll();
            newName(func);

            if (tokenList.peek().value() == TokId.TLPAR) {
                //System.out.println(tokenList.peek());
                tokenList.poll();
                func.setLeft(plist());

                if (tokenList.peek().value() == TokId.TRPAR) {
                    //System.out.println(tokenList.peek());
                    tokenList.poll();

                    if (tokenList.peek().value() == TokId.TCOLN) {
                        //System.out.println(tokenList.peek());
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
        if (current.value() == TokId.TVOID){
            //System.out.println(tokenList.peek());
            tokenList.poll();
            return "VOID";
        }
        else{
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
            //System.out.println(tokenList.peek());
            tokenList.poll();
            return params();
        }
        return null;
    }

    private TreeNode param() {
        if (tokenList.peek().value() == TokId.TCNST){
            //System.out.println(tokenList.peek());
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
            //System.out.println(tokenList.peek());
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
        if (tokenList.peek().value() == TokId.TBEGN){
            //System.out.println(tokenList.peek());
            tokenList.poll();
            func.setRight(stats());

            if (tokenList.peek().value() == TokId.TENDK){
                //System.out.println(tokenList.peek());
                tokenList.poll();
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
            //System.out.println(tokenList.peek());
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
            //System.out.println(tokenList.peek());
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
                //System.out.println(tokenList.peek());
                tokenList.poll();
                return "integer";
            case TREAL:
                //System.out.println(tokenList.peek());
                tokenList.poll();
                return "real";
            case TBOOL:
                //System.out.println(tokenList.peek());
                tokenList.poll();
                return "boolean";
            default:
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
                //System.out.println(tokenList.peek());
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
                newName(stat);
                current = tokenList.peek();
                if (current.value() == TokId.TLPAR){
                    return callstat(stat);
                }
                else{
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
        if (tokenList.peek().value() == TokId.TFORK){
            //System.out.println(tokenList.peek());
            tokenList.poll();

            if (tokenList.peek().value() == TokId.TLPAR){
                //System.out.println(tokenList.peek());
                tokenList.poll();
                forstat.setLeft(asgnlist());

                if (tokenList.peek().value() == TokId.TSEMI){
                    //System.out.println(tokenList.peek());
                    tokenList.poll();
                    forstat.setMiddle(bool());

                    if (tokenList.peek().value() == TokId.TRPAR){
                        //System.out.println(tokenList.peek());
                        tokenList.poll();
                        forstat.setRight(stats());

                        if (tokenList.peek().value() == TokId.TENDK){
                            //System.out.println(tokenList.peek());
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
        if (tokenList.peek().value() == TokId.TREPT){
            //System.out.println(tokenList.peek());
            tokenList.poll();

            if (tokenList.peek().value() == TokId.TLPAR){
                //System.out.println(tokenList.peek());
                tokenList.poll();
                repstat.setLeft(asgnlist());

                if (tokenList.peek().value() == TokId.TRPAR){
                    //System.out.println(tokenList.peek());
                    tokenList.poll();
                    repstat.setMiddle(stats());

                    if (tokenList.peek().value() == TokId.TUNTL){
                        //System.out.println(tokenList.peek());
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
            //System.out.println(tokenList.peek());
            tokenList.poll();
            return alist();
        }
        return null;
    }

    private TreeNode ifstat() {
        TreeNode ifstat = new TreeNode(Node.NIFTH);
        if (tokenList.peek().value() == TokId.TIFKW){
            //System.out.println(tokenList.peek());
            tokenList.poll();

            if (tokenList.peek().value() == TokId.TLPAR){
                //System.out.println(tokenList.peek());
                tokenList.poll();
                ifstat.setLeft(bool());

                if (tokenList.peek().value() == TokId.TRPAR){
                    //System.out.println(tokenList.peek());
                    tokenList.poll();
                    ifstat.setMiddle(stats());
                    ifstat.setRight(elsestat());

                    if (tokenList.peek().value() == TokId.TENDK){
                        //System.out.println(tokenList.peek());
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
            //System.out.println(tokenList.peek());
            tokenList.poll();
            return new TreeNode(Node.NIFTE, stats());
        }
        return null;
    }

    private TreeNode asgnstat(TreeNode var, boolean consume) {
        TreeNode asgnstat = new TreeNode(Node.NASGN);
        if (consume){
            asgnstat.setLeft(var());
        }
        else{
            asgnstat.setLeft(var);
        }
        if (tokenList.peek().value() == TokId.TASGN){
            //System.out.println(tokenList.peek());
            tokenList.poll();
            asgnstat.setRight(bool());
            return asgnstat;
        }
        return syntaxError();
    }

    private TreeNode iostat() {
        if (tokenList.peek().value() == TokId.TINKW){
            //System.out.println(tokenList.peek());
            tokenList.poll();

            if (tokenList.peek().value() == TokId.TINPT){
                //System.out.println(tokenList.peek());
                tokenList.poll();
                return new TreeNode(Node.NINPUT, vlist());
            }
        }
        else if (tokenList.peek().value() == TokId.TOUTP){
            //System.out.println(tokenList.peek());
            tokenList.poll();

            if (tokenList.peek().value() == TokId.TASGN){
                //System.out.println(tokenList.peek());
                tokenList.poll();
                return iostatcall(new TreeNode(Node.NOUTP));
            }
        }
        return syntaxError();
    }

    private TreeNode iostatcall(TreeNode iostat) {
        if (tokenList.peek().value() == TokId.TOUTL){
            //System.out.println(tokenList.peek());
            tokenList.poll();
            iostat.setValue(Node.NOUTL);
            return iostat;
        }
        iostat.setLeft(prlist());
        return iostatcalltail(iostat);
    }

    private TreeNode iostatcalltail(TreeNode prlist) {
        if (tokenList.peek().value() == TokId.TASGN){
            //System.out.println(tokenList.peek());
            tokenList.poll();

            if (tokenList.peek().value() == TokId.TOUTL){
                //System.out.println(tokenList.peek());
                tokenList.poll();
                prlist.setValue(Node.NOUTL);
            }
            return syntaxError();
        }
        return prlist;
    }

    private TreeNode callstat(TreeNode stat) {
        if (tokenList.peek().value() == TokId.TLPAR){
            //System.out.println(tokenList.peek());
            tokenList.poll();
            stat.setValue(Node.NCALL);
            stat.setLeft(callstattail());

            if (tokenList.peek().value() == TokId.TRPAR){
                //System.out.println(tokenList.peek());
                tokenList.poll();
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
            //System.out.println(tokenList.peek());
            tokenList.poll();
            return vlist();
        }
        return null;
    }

    private TreeNode var() {
        TreeNode var = new TreeNode(Node.NSIMV);
        newName(var);
        return vararr(var);
    }

    private TreeNode vararr(TreeNode var) {
        if (tokenList.peek().value() == TokId.TLBRK){
            //System.out.println(tokenList.peek());
            tokenList.poll();
            var.setValue(Node.NAELT);
            var.setLeft(expr());

            if (tokenList.peek().value() == TokId.TRBRK){
                //System.out.println(tokenList.peek());
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
            //System.out.println(tokenList.peek());
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
            //System.out.println(tokenList.peek());
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
        if (current.value() == TokId.TANDK){
            //System.out.println(tokenList.peek());
            TreeNode logop = new TreeNode(Node.NAND);
            logop.setLeft(rel);
            tokenList.poll();
            return logop;
        }
        if (current.value() == TokId.TORKW){
            //System.out.println(tokenList.peek());
            TreeNode logop = new TreeNode(Node.NOR);
            logop.setLeft(rel);
            tokenList.poll();
            return logop;
        }
        if (current.value() == TokId.TXORK){
            //System.out.println(tokenList.peek());
            TreeNode logop = new TreeNode(Node.NXOR);
            logop.setLeft(rel);
            tokenList.poll();
            return logop;
        }
        return syntaxError();
    }

    private TreeNode relop(TreeNode expr){
        Token current = tokenList.peek();
        if (current.value() == TokId.TDEQL){
            //System.out.println(tokenList.peek());
            tokenList.poll();
            return new TreeNode(Node.NEQL, expr);
        }
        if (current.value() == TokId.TNEQL){
            //System.out.println(tokenList.peek());
            tokenList.poll();
            return new TreeNode(Node.NNEQ, expr);
        }
        if (current.value() == TokId.TGRTR){
            //System.out.println(tokenList.peek());
            tokenList.poll();
            return new TreeNode(Node.NGRT, expr);
        }
        if (current.value() == TokId.TLEQL){
            //System.out.println(tokenList.peek());
            tokenList.poll();
            return new TreeNode(Node.NLEQ, expr);
        }
        if (current.value() == TokId.TLESS){
            //System.out.println(tokenList.peek());
            tokenList.poll();
            return new TreeNode(Node.NLSS, expr);
        }
        if (current.value() == TokId.TGREQ){
            //System.out.println(tokenList.peek());
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
        if (current.value() == TokId.TADDT){
            //System.out.println(tokenList.peek());
            tokenList.poll();
            return exprtail(new TreeNode(Node.NADD, term, term()));
        }
        if (current.value() == TokId.TSUBT){
            //System.out.println(tokenList.peek());
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
        if (current.value() == TokId.TMULT){
            //System.out.println(tokenList.peek());
            tokenList.poll();
            return termtail(new TreeNode(Node.NMUL, fact, fact()));
        }
        if (current.value() == TokId.TDIVT){
            //System.out.println(tokenList.peek());
            tokenList.poll();
            return termtail(new TreeNode(Node.NDIV, fact, fact()));
        }
        if (current.value() == TokId.TPERC){
            //System.out.println(tokenList.peek());
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
            //System.out.println(tokenList.peek());
            tokenList.poll();
            return facttail(new TreeNode(Node.NPOW, exponent, fact()));
        }
        return exponent;
    }

    private TreeNode exponent(){
        Token current = tokenList.peek();
        TreeNode exponent;
        switch (current.value()){
            case TIDNT:
                return var();
            case TILIT:
                //System.out.println(tokenList.peek());
                exponent = new TreeNode(Node.NILIT);
                newName(exponent);
                return exponent;
            case TFLIT:
                //System.out.println(tokenList.peek());
                exponent = new TreeNode(Node.NFLIT);
                newName(exponent);
                return new TreeNode(Node.NFLIT);
            case TFUNC:
                //System.out.println(tokenList.peek());
                exponent = new TreeNode(Node.NUNDEF);
                newName(exponent);
                return fncall(exponent);
            case TTRUE:
                //System.out.println(tokenList.peek());
                exponent = new TreeNode(Node.NTRUE);
                newName(exponent);
                return new TreeNode(Node.NTRUE);
            case TFALS:
                //System.out.println(tokenList.peek());
                exponent = new TreeNode(Node.NFALS);
                newName(exponent);
                return new TreeNode(Node.NFALS);
            case TLPAR:
                //System.out.println(tokenList.peek());
                tokenList.poll();
                exponent = bool();
                if (tokenList.peek().value() == TokId.TRPAR){
                    //System.out.println(tokenList.peek());
                    tokenList.poll();
                    return exponent;
                }
            default:
                return syntaxError();
        }
    }

    private TreeNode fncall(TreeNode exponent){
        newName(exponent);

        if (tokenList.peek().value() == TokId.TLPAR){
            //System.out.println(tokenList.peek());
            tokenList.poll();
            TreeNode fncalltail = fncalltail();
            exponent.setValue(fncalltail.getValue());
            exponent.setLeft(fncalltail.getLeft());
            exponent.setRight(fncalltail.getRight());

            if (tokenList.peek().value() == TokId.TRPAR){
                //System.out.println(tokenList.peek());
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
            //System.out.println(tokenList.peek());
            tokenList.poll();
            return prlist();
        }
        return null;
    }

    private TreeNode printitem() {
        if (tokenList.peek().value() == TokId.TSTRG){
            TreeNode printitem = new TreeNode(Node.NSTRG);
            newName(printitem);
            return printitem;
        }
        return expr();
    }
}
