package LambdaCalculus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Atom extends PredicateLogicalForm {

    String predicate;

    List<Term> arguments;

    public Atom() {
        this.arguments = new ArrayList<Term>();

    }

    public Atom(String a) {
        this.predicate = a;
        this.arguments = new ArrayList<Term>();

    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public List<LambdaCalculus.Term> getArguments() {
        return arguments;
    }

    public void setArguments(List<LambdaCalculus.Term> arguments) {
        this.arguments = arguments;
    }

    @Override
    public Expression reduce() {
        Atom a = new Atom();
        a.setPredicate(predicate);
        for (Term t : arguments) {
            a.add(t.clone());
        }
        return a;
    }

    @Override
    public Expression substitute(LambdaCalculus.Variable var, Expression exp) {

        if (exp instanceof Term) {

            Atom a = new Atom();
            a.setPredicate(predicate);

            for (int i = 0; i < arguments.size(); i++) {
                Term term = arguments.get(i);

                if (term instanceof Variable) {
                    Variable v = (Variable) term;

                    if (v.value.equals(var.value)) {
                        Term substituted = (LambdaCalculus.Term) exp;
                        a.add(substituted.clone());
                    }
                    else{
                        a.add(term.clone());
                    }

                } else {
                    a.add(term.clone());
                }
            }

            return a;
        }
        return this;

    }

    public void add(Term t) {
        arguments.add(t);
    }

    public String toString() {

        String string = "Pred-" + predicate + "(";
        for (Term term : arguments) {
            string += term.toString() + " ";
        }
        string = string.trim() + ")";
        return string;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.predicate);
        hash = 23 * hash + Objects.hashCode(this.arguments);
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
        final Atom other = (Atom) obj;
        if (!Objects.equals(this.predicate, other.predicate)) {
            return false;
        }
        if (!Objects.equals(this.arguments, other.arguments)) {
            return false;
        }
        return true;
    }
}
