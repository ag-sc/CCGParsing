package Parser;

import Factory.Factory;
import Grammar.CCGGrammar;
import Grammar.CCGLexEntry;
import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        
        List<String> tokens = new ArrayList<String>();
        
        tokens.add("NP");
        tokens.add("((S\\NP)/NP)");
        tokens.add("NP");
        
        CYKParser cyk = new CYKParser();
        
    }

}
