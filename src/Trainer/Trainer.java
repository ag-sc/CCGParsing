package Trainer;

import Grammar.Grammar;
import Instance.Instance;
import Model.Model;
import java.util.List;

public interface Trainer {

	public List<Instance> train(List<Instance> training);	
}
