package Parser;

import Derivation.Covering;
import Derivation.Derivation;
import Grammar.CCGLexEntry;
import Grammar.Grammar;
import Instance.Instance;
import Model.Model;
import java.util.List;

public interface Parser {
	
	public List<Derivation> parse(List<Covering> coverings);
	
}
