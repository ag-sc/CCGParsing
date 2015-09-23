package Grammar;

import java.util.List;

public interface Grammar {

    public void addLexicalEntries(List<CCGLexEntry> generateLexiconEntries);
    
    public Grammar merge(Grammar grammar);
}
