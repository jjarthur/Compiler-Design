////
////	COMP3290/6290 CD A4
////
////	Syntax Tree Class - Builds a syntax tree node
////
////	M. Hannaford
////	17-Aug-2016
////
////

public class TreeNode {

    private static int count = 0;
    private static int index = 0;
    private static int lineLength = 0;
    private Node nodeValue;
    private int idx;
    private TreeNode left, middle, right;
    private StRec name, type;

    public TreeNode() {
        nodeValue = Node.NUNDEF;
        index++;
        idx = index;
        left = null;
        middle = null;
        right = null;

        name = null;
        type = null;
    }

    public TreeNode(Node value) {
        this();
        nodeValue = value;
    }

    public TreeNode(Node value, StRec st) {
        this(value);
        name = st;
    }

    public TreeNode(Node value, TreeNode l) {
        this(value);
        left = l;
    }

    public TreeNode(Node value, TreeNode l, TreeNode r) {
        this(value);
        left = l;
        right = r;
    }

    public TreeNode(Node value, TreeNode l, TreeNode m, TreeNode r) {
        this(value, l, r);
        middle = m;
    }

    public Node getValue() {
        return nodeValue;
    }

    public TreeNode getLeft() {
        return left;
    }

    public TreeNode getMiddle() {
        return middle;
    }

    public TreeNode getRight() {
        return right;
    }

    public StRec getName() {
        return name;
    }

    public StRec getType() {
        return type;
    }

    public void setValue(Node value) {
        nodeValue = value;
    }

    public void setLeft(TreeNode l) {
        left = l;
    }

    public void setMiddle(TreeNode m) {
        middle = m;
    }

    public void setRight(TreeNode r) {
        right = r;
    }

    public void setName(StRec st) {
        name = st;
        if (type != null){
            name.setTypeName(type);
        }
    }

    public void setType(StRec st) {
        type = st;
    }

    public static void resetIndex() {
        index = 0;
    }

    public String printNode(int level) {
        String output = "";
        String tab = "   ";
        for (int i = 0; i < level; i++) {
            output += tab;
        }
        return output += nodeValue;
    }

    public String toString(int level) {
        String output = "";
        output += printNode(level) + "\n";
        if (this.left != null) {
            output += left.toString(level + 1);
        }
        if (this.middle != null) {
            output += middle.toString(level + 1);
        }
        if (this.right != null) {
            output += right.toString(level + 1);
        }
        return output;
    }

    public String toString() {
        int level = 0;
        String output = "";
        output += printNode(level) + "\n";
        if (this.left != null) {
            output += left.toString(level + 1);
        }
        if (this.middle != null) {
            output += middle.toString(level + 1);
        }
        if (this.right != null) {
            output += right.toString(level + 1);
        }
        return output;
    }

    public String printNodeSpace() {
        String output = "";
        output += nodeValue;

        if (name != null) {
            output += " " + name.getName();
        }
        if (type != null) {
            output += " " + type.getName();
        }
        output += " ";

        while (output.length() % 7 != 0) output += " ";
        lineLength += output.length();
        if (lineLength > 70) {
            output += "\n";
            lineLength = 0; //New line
        }
        if (this.left != null) {
            output += left.printNodeSpace();
        }
        if (this.middle != null) {
            output += middle.printNodeSpace();
        }
        if (this.right != null) {
            output += right.printNodeSpace();
        }
        return output;
    }
}
