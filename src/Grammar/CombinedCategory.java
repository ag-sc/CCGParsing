package Grammar;

import java.io.Serializable;
import java.util.Objects;

public class CombinedCategory implements CCGCategory, Serializable {

    CCGCategory right;

    CCGCategory left;

    public enum Operator {

        slash, backslash
    };

    Operator op;

    public void setLeft(CCGCategory cat) {
        left = cat;
    }

    public void setRight(CCGCategory cat) {
        right = cat;
    }

    public void setOperator(Operator operator) {
        op = operator;
    }

    public CCGCategory getRight() {
        return right;
    }

    public CCGCategory getLeft() {
        return left;
    }

    public Operator getOp() {
        return op;
    }

    public void setOp(Operator op) {
        this.op = op;
    }

    public String toString() {
        if (op.equals(Operator.slash)) {
            return "("+left.toString() + "/" + right.toString()+")";
        } else {
            return "("+left.toString() + "\\" + right.toString()+")";
        }
    }

    public CCGCategory apply(CCGCategory cat) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.right);
        hash = 97 * hash + Objects.hashCode(this.left);
        hash = 97 * hash + Objects.hashCode(this.op);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CombinedCategory other = (CombinedCategory) obj;
        if (!Objects.equals(this.right, other.right)) {
            return false;
        }
        if (!Objects.equals(this.left, other.left)) {
            return false;
        }
        if (this.op != other.op) {
            return false;
        }
        return true;
    }

}
