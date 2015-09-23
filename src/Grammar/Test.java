package Grammar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import Factory.Factory;

public class Test {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		FileWriter writer = new FileWriter("test.ccg");
		
		writer.write("mia:NP - mia\n");
		
		writer.write("vincent:NP - vincent\n");
		
		writer.write("loves:(S\\NP)/NP - lambda(x,lambda(y,loves(y,x))\n");
		
		Factory factory = new Factory();
                
                CombinedCategory c = (CombinedCategory) factory.construcCCGCategory("((S\\NP)/NP)");
		
		CCGGrammar grammar = factory.readGrammar(new File("test.ccg"));
		
		if(c!=null)
                    System.out.print(c);
		
		
		

	}

}
