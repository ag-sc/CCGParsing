package SemanticParser;

import Grammar.Grammar;
import Instance.Instance;
import LambdaCalculus.Expression;
import Lemmatizer.Lemmatizer;
import Model.Model;
import java.util.List;

public interface SemanticParser {

    public Model train(List<Instance> instances);

    public Expression predict(Instance i, Model m, int index);

}
