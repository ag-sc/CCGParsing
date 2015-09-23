/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import java.util.List;

/**
 *
 * @author sherzod
 */
public class TestQueryData {

    public static void main(String[] args) {
        
        
        QueryDataProcessor proc = new QueryDataProcessor(true);
        long start = System.currentTimeMillis();
        List<Element> a = proc.getObject("http://dbpedia.org/resource/Pope_John_Paul_II", "http://dbpedia.org/property/name");
//        for(Element e :a){
//            System.out.println(e.getPredicate()+" "+e.getObject());
//        }
        long end = System.currentTimeMillis();
        
         System.out.println((end-start));
        
        
//        List<String> nouns = dbManagement.getPOSTags("noun");
//        
//        List<String> v = dbManagement.getPOSTags("verb");
//        
//        List<String> adj = dbManagement.getPOSTags("adjective");
//        
//        List<String> adv = dbManagement.getPOSTags("adverb");
//        
//        List<String> pre = dbManagement.getPOSTags("preposition");
//        
//        String s = "video game";
//        
//        if(nouns.contains(s)){
//            System.out.println("n");
//        }
//        if(v.contains(s)){
//            System.out.println("v");
//        }
//        if(adj.contains(s)){
//            System.out.println("adj");
//        }
//        if(adv.contains(s)){
//            System.out.println("adv");
//        }
//        if(pre.contains(s)){
//            System.out.println("pre");
//        }
        
       
    }
    
    
}
