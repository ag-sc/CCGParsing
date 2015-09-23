package LambdaCalculus;


import java.util.ArrayList;

import java.util.List;
import java.util.Objects;

public class MultiConnector extends PredicateLogicalForm {

    public MultiConnector() {
        this.Conn =  Connector.and;
        this.predicates = new ArrayList<>();
    }

    @Override
    public Expression reduce() {
        MultiConnector m = new MultiConnector();
        for(Expression e : getPredicates()){
            m.add(e.reduce());
        }
        return m;
    }

    @Override
    public Expression substitute(LambdaCalculus.Variable var, Expression exp) {
        
        MultiConnector m = new MultiConnector();
        
        for(int i=0; i< predicates.size(); i++){
            Expression p = predicates.get(i);
            
            m.add(p.substitute(var, exp));
            
        }
        
        return m.reduce();
    }
    
    public void add(Expression p){
        this.predicates.add(p);
    }

    public enum Connector {
        and, or
    };

    List<Expression> predicates;

    Connector Conn;

    public List<LambdaCalculus.Expression> getPredicates() {
        return predicates;
    }

    public void setPredicates(List<LambdaCalculus.Expression> predicates) {
        this.predicates = predicates;
    }

    public Connector getConn() {
        return Conn;
    }

    public void setConn(Connector Conn) {
        this.Conn = Conn;
    }

    @Override
    public String toString() {
        
        String s="";
        for(Expression p : getPredicates()){
            if(getPredicates().indexOf(p) != getPredicates().size()-1){
                s+=p.toString()+" "+getConn().toString()+" ";
            }
            else{
                s+=p.toString();
            }
            
        }
        s="[" +s.trim()+"]";
        return s;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.predicates);
        hash = 23 * hash + Objects.hashCode(this.Conn);
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
        final MultiConnector other = (MultiConnector) obj;
        if (!Objects.equals(this.predicates, other.predicates)) {
            return false;
        }
        if (this.Conn != other.Conn) {
            return false;
        }
        return true;
    }
    
}
