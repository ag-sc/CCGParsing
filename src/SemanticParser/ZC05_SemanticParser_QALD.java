package SemanticParser;

import Database.DBManagement;
import Database.QueryDBpedia;
import Decoder.*;
import Derivation.CCGDerivation;
import Derivation.Covering;
import Derivation.Derivation;
import Evaluator.Evaluator;
import Evaluator.QALD_Evaluator;
import Factory.Factory;
import Feature.CCGFeature;
import Feature.Feature;
import Feature.FeatureExtractor;
import Feature.SemanticFeatureExtractor;
import Grammar.AtomicCategory;
import Grammar.CCGGrammar;
import Grammar.CCGLexEntry;
import Grammar.CombinedCategory;
import Grammar.Grammar;
import Instance.Instance;
import Instance.SemanticParsingInstance;
import LambdaCalculus.AggregationQuantifier;
import LambdaCalculus.Atom;
import LambdaCalculus.Constant;
import LambdaCalculus.CountingQuantifier;
import LambdaCalculus.ExistQuantifier;
import LambdaCalculus.Expression;
import LambdaCalculus.LambdaAbstraction;
import LambdaCalculus.MultiConnector;
import LambdaCalculus.Term;
import LambdaCalculus.Variable;
import Lemmatizer.Lemmatizer;
import Lemmatizer.StanfordLemmatizer;
import Model.CCGModelOnSQL;
import Model.Model;
import Parser.Parser;
import Parser.SyntacticParser;
import Reader.QALD.QALD;
import Reader.QALD.Question;
import Reader.QALD.SPARQLParser;
import Stemmer.Stemmer;
import Trainer.Perceptron;
import Trainer.Trainer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.lang.model.element.NestingKind;

public class ZC05_SemanticParser_QALD implements SemanticParser {

    Trainer trainer;

    CCGGrammar grammar;

    Decoder decoder;

    Model model;

    Factory factory;

    FeatureExtractor extractor;

    Evaluator evaluator;

    Parser syntacticParser;

    private int iterationNumber = 1;

    private int stackSize = 1000;

    private int nGramSize = 3;

    private DBManagement dbProcessor;

    public static void main(String[] args) {

        String model_file = "";
        String input_file;
        String arg;
        String key, value;
        String mode = null;

//        justTest();
        testTraining();
//        testPredict();
//        saveMatollModelOnQALD("testModel.model");
//        testMatollOnQALD("trainedModel_Object_MATOLL.model");
        if (args.length < 3) {
            System.out.print("Usage: ZC05_SemanticParser --mode=train/predict <INPUT_FILE> <MODEL_FILE>\n");

            return;
        } else {
            model_file = args[args.length - 1];

            input_file = args[args.length - 2];

            for (int i = 0; i < args.length - 2; i++) {
                arg = args[i];

                Pattern p = Pattern.compile("^--(\\w+)=(\\w+|\\d+)$");

                Matcher matcher;

                matcher = p.matcher(arg);

                if (matcher.matches()) {
                    key = matcher.group(1);
                    value = matcher.group(2);

                    if (key.equals("mode")) {

                        if (key.equals("mode")) {
                            if (value.equals("train") || value.equals("predict")) {
                                mode = value;

                            } else {
                                System.out.print("Usage: ZC05_SemanticParser --mode=train/predict <INPUT_FILE> <MODEL_FILE>\n");

                                return;
                            }
                        }
                    } else {
                        System.out.print("Usage: ZC05_SemanticParser --mode=train/predict <INPUT_FILE> <MODEL_FILE>\n");

                        return;
                    }
                }

            }

        }

        if (mode.equals("train")) {
            System.out.println("Reading in training data from " + input_file);

            ZC05_SemanticParser_QALD zc05 = new ZC05_SemanticParser_QALD();
            Factory f = new Factory();

            //read training data
            List<Instance> instances = zc05.getInstances(f, input_file, true);

            //training process
            System.out.println("Starting training process... " + instances.size() + " instances .");

            CCGModelOnSQL trainedModel = (CCGModelOnSQL) zc05.train(instances);

//            System.out.print("Writing out model to " + model_file);
//
//            f.writeModel(trainedModel, model_file, "object");
//
//            model_file = model_file.substring(0, model_file.indexOf(".model"));
//            f.writeModel(trainedModel, model_file + "_AsString.model", "string");
//
//            System.out.println(" -- DONE. ");
        } else {

            if (mode.equals("predict")) {

                Factory f = new Factory();
                //load model
                System.out.print("Reading in model from: " + model_file + "\n");

//                model = f.readModel(new File(model_file));
                ZC05_SemanticParser_QALD zc05 = new ZC05_SemanticParser_QALD();
                zc05.dbProcessor = new DBManagement("root", "", "semantic_parsing");
                Lemmatizer lemmatizer = new StanfordLemmatizer();
                zc05.model = new CCGModelOnSQL(zc05.dbProcessor, lemmatizer);

                QALD_Evaluator evaluator = new QALD_Evaluator();

                //read training data
                List<Instance> instances = zc05.getInstances(f, input_file, true);

                int c = 0;
                int p = 0;
                for (Instance i : instances) {

                    Expression predicted = zc05.predict(i, zc05.model, -1);

                    if (predicted != null) {
                        p++;
                    }
                    SemanticParsingInstance instance = (SemanticParsingInstance) i;

                    if (instance.getForm() != null) {
                        boolean isEqual = evaluator.betterOrEqualTo(predicted, instance.getForm());
                        if (isEqual) {
                            c++;
//                    System.out.println(instance.getSentence());

                        } else {
                            System.out.println(instance.getSentence());

                            if (predicted != null) {
                                //System.out.println("\n" + predicted.toString() + " <==> " + instance.getForm().toString());
                            } else {
                                //System.out.println("\n NULL <==> " + instance.getForm().toString());
                            }
                        }
                    }
                }
                //predict and output
                //System.out.print("Output written to " + input_file + ".tagged\n");

                double prec = c / (double) p;
                double rec = c / (double) instances.size();
                double harmonic_mean = (2 * prec * rec) / (prec + rec);
                System.out.println("Recall: " + String.format("%.2f", rec) + "  " + c + "/" + instances.size());
                System.out.println("Precision: " + String.format("%.2f", prec) + "  " + c + "/" + p);

                System.out.println("F1: " + String.format("%.2f", harmonic_mean));
            }
        }
    }

    public static void testTraining() {

        ZC05_SemanticParser_QALD zc05 = new ZC05_SemanticParser_QALD();
        zc05.factory = new Factory();

        String input_file = "src/Reader/Files/qald-5_train.xml";
        String model_file = "src/Reader/Files/testModel.model";

        System.out.println("Reading in training data from " + input_file);

//        Factory f = new Factory();
        //read training data
        List<Instance> instances = zc05.getInstances(zc05.factory, input_file, true);

        //training process
        System.out.println("Starting training process... " + instances.size() + " instances .");

        CCGModelOnSQL trainedModel = (CCGModelOnSQL) zc05.train(instances);
//        DBManagement db = new DBManagement();

    }

    public List<Instance> getInstances(Factory factory, String fileName, boolean isTraining) {
        //ArrayList<String> sentences = readFile(fileName);
        List<Instance> instances = new ArrayList<Instance>();

        SPARQLParser parser = new SPARQLParser();

        ArrayList<Question> questions = QALD.getQuestions(fileName);

        ArrayList<String> namedEntities = null;
        int count = 0;

        if (isTraining) {
            namedEntities = factory.readFile(new File("src/Reader/Files/entityList_QALD.model"));
        }

        for (Question q : questions) {

            //replace query with below things for SPARQLQuery parser
            //it only parses if COUNT statement is like below
            //Jena library
            if (q.getQueryText().contains("SELECT COUNT(DISTINCT ?uri)")) {
                String query = q.getQueryText();
                query = query.replace("SELECT COUNT(DISTINCT ?uri)", "SELECT (COUNT(DISTINCT ?uri) as ?ss)");
                q.setQueryText(query);
            }
            String mark = q.getQuestionText().substring(q.getQuestionText().length() - 1);
            if (mark.equals("?") || mark.equals(".")) {
                q.setQuestionText(q.getQuestionText().substring(0, q.getQuestionText().length() - 1));
            }

            //replace 's with (space+'s)
            if (q.getQuestionText().contains("'s")) {
                q.setQuestionText(q.getQuestionText().replace("'s", " 's"));
            }

            //if training then replace named entities with underscores instead of space
            if (isTraining) {
                for (String e : namedEntities) {
                    String questionText = q.getQuestionText().toLowerCase();
                    String[] entity = e.split("\t");
                    if (questionText.contains(entity[0])) {
                        String replaced = entity[0].replace(" ", "_");
                        questionText = questionText.replace(entity[0], replaced);
                        q.setQuestionText(questionText);
                    }
                }
            }

            //convert SPARQL into logical form
            try {
                Expression f = parser.convertQueryToLogicalForm(q.getQueryText());

                SemanticParsingInstance instance = new SemanticParsingInstance(q.getQuestionText(), f);
                instance.setGeobaseForm(q.getQueryText());
                instances.add(instance);
                count++;

            } catch (Exception e) {
            }

        }

        System.out.println(count + "/" + questions.size());

        return instances;
    }

    public void logCoverings(List<Covering> coverings) {
        Logging.Logger.logText("\n\nCoverings:\n");

        for (Covering c : coverings) {
            Logging.Logger.logText(c.toString() + "\n");
        }
    }

    public void logDerivatioms(List<Derivation> derivations) {
        Logging.Logger.logText("\n\nDerivations:\n");

        for (Derivation c : derivations) {
            Logging.Logger.logText(c.toString() + "\n");
        }
    }

    @Override
    public Model train(List<Instance> instances) {

        SemanticParsingInstance myInstance;

        Lemmatizer lemmatizer = new StanfordLemmatizer();
        dbProcessor = new DBManagement("sherzod", "123asd", "qa");
        stackSize = 20000;
        iterationNumber = 1;
        nGramSize = 3;

        factory = new Factory();
        extractor = new SemanticFeatureExtractor();
        grammar = new CCGGrammar();
        evaluator = new QALD_Evaluator();
        syntacticParser = new SyntacticParser();

        model = new CCGModelOnSQL(dbProcessor, lemmatizer);
        decoder = new StackDecoderOld(model, stackSize, nGramSize);

        boolean addDBpediaLexicon = false;
        CCGGrammar definedGrammar = induceDefinedGrammar(addDBpediaLexicon);

        System.out.println("Inducing grammar from instances...");
        CCGGrammar inducedGrammar = induceGrammar(instances, definedGrammar);

        //add all lexicon again
        addDBpediaLexicon = false;
//        definedGrammar = induceDefinedGrammar(addDBpediaLexicon);

        CCGModelOnSQL inducedModel = null;
        try {
            inducedModel = new CCGModelOnSQL(dbProcessor, lemmatizer);
            if (inducedModel.modelExists()) {
                //            inducedModel.deleteAllInducedFeatures();
                inducedModel.udpateAllFeaturesAsUsed("false");
                inducedModel.updateDomainIndependentFeaturesScore(10.1);
                inducedModel.updateAllInducedFeaturesScore(0);

//                inducedModel.udpateAllFeatureScore(10.1);
                //delete all previous features
//            inducedModel.deleteAllFeatures();
            }

        } catch (Exception ex) {
            Logger.getLogger(ZC05_SemanticParser_QALD.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<CCGLexEntry> entries = definedGrammar.getAllEntries();

        if (!inducedModel.modelExists()) {
            //load manually defined grammar entries to the model as features and set score 10.1
            System.out.println("Adding defined features to the database...");
            inducedModel.addNewFeatures(extractor.extractFeatures(entries));
            inducedModel.udpateAllFeaturesAsUsed("false");
            inducedModel.udpateAllFeatureScore(10.1);

            inducedModel.udpateAllFeaturesAsUsed("false");

            inducedModel.updateAllFeatures(extractor.extractFeatures(entries), 10.1);
            entries = inducedGrammar.getAllEntries();

            //add all entries from inducedGrammar and combine with existing ones
            System.out.println("Adding induced features to the database...");
            inducedModel.addNewFeatures(extractor.extractFeatures(entries));

        }

        entries.clear();

        //Model.updateAllFeatures(inducedModel.getAllEntries(), 0.0);
        model = inducedModel;

        System.out.println("Starting the training process...");
        //remove all logs
        Logging.Logger.removeLogs();

        for (int i = 1; i <= iterationNumber; i++) {

            //set all features isUsed=false;
            inducedModel.udpateAllFeaturesAsUsed("false");
            decoder.setUseInstanceBasedFeatures(true);

            System.out.println("Iteration #" + i);

            long startTime = System.currentTimeMillis();
            System.out.println("Step1 parsing ... ");

            int unparsedCount = 0;
            int parsedCount = 0;

            String allLogs = "", unparsedInstances = "";
            for (Instance instance : instances) {

                myInstance = (SemanticParsingInstance) instance;

                Logging.Logger.setFilePATH("src/Reader/Files/logs/log" + instances.indexOf(instance) + "_Coverings.txt");
                Logging.Logger.logText("Iteration #" + i);
                Logging.Logger.logText(myInstance.toString());

                long parseStartTime = System.currentTimeMillis();

                String[] tokens = myInstance.getSentence().toLowerCase().split(" ");

                if (tokens.length <= 10) {
                    
                    System.out.println("Parsing : " + myInstance.getSentence() + " Index: " + instances.indexOf(myInstance));
                    allLogs+="\nParsing : " + myInstance.getSentence() + " Index: " + instances.indexOf(myInstance);
                    
                    List<Covering> coverings = decoder.decode(tokens, instances.indexOf(instance));
                    logCoverings(coverings);

                    Logging.Logger.setFilePATH("src/Reader/Files/logs/log" + instances.indexOf(instance) + "_Derivations.txt");
                    Logging.Logger.logText("Iteration #" + i);

                    List<Derivation> derivations = syntacticParser.parse(coverings);
                    logDerivatioms(derivations);

                    myInstance.setDerivation(derivations);
                }

                if (myInstance.getDerivation()!=null) {
                    System.out.println("Number of Derivations : " + myInstance.getDerivation().size());
                    allLogs+="\nNumber of Derivations : " + myInstance.getDerivation().size();
                    parsedCount++;
                } else {
                    System.out.println("Number of Derivations : 0");
                    allLogs+="\nNumber of Derivations : 0";
                    unparsedCount++;
                    unparsedInstances+=myInstance.getSentence()+"\n";
                }

                long parseEndtTime = System.currentTimeMillis();

                System.out.println("Parse time : " + (parseEndtTime - parseStartTime) / 1000 + " sec.");

            }
            Logging.Logger.setFilePATH("src/Reader/Files/logs.txt");
            Logging.Logger.logText("\nIteration #" + i);
            Logging.Logger.logText(allLogs);
            
            Logging.Logger.setFilePATH("src/Reader/Files/unParsed.txt");
            Logging.Logger.logText("\nIteration #" + i);
            Logging.Logger.logText(unparsedInstances);
            

            System.out.println("Unparsed : " + unparsedCount);
            System.out.println("Parsed : " + parsedCount);
            System.out.println(" -- DONE");

            System.out.println("Step1 updating weights ... ");
            trainer = new Perceptron(evaluator, model, extractor, false);

            instances = trainer.train(instances);
            System.out.println(" -- DONE");

            decoder.setUseInstanceBasedFeatures(false);

            trainer = new Perceptron(evaluator, model, extractor, true);

            System.out.println("Step2 parsing ... ");

            for (Instance instance : instances) {

                myInstance = (SemanticParsingInstance) instance;

                String[] tokens = myInstance.getSentence().toLowerCase().split(" ");

                if (myInstance.isParsed()) {

                    myInstance.setDerivation(null);
                    List<Covering> coverings = decoder.decode(tokens, instances.indexOf(instance));
                    List<Derivation> derivations = syntacticParser.parse(coverings);
                    myInstance.setDerivation(derivations);
                }
            }
            System.out.println(" -- DONE");

            System.out.println("Step2 updating weights ... ");
            instances = trainer.train(instances);

            long endTime = System.currentTimeMillis();

            double avg = (double) ((endTime - startTime) / 1000) / (double) instances.size();
            System.out.println("Average runtime per instance: " + avg);

        }

        System.out.println("Normalizing scores ... ");
        inducedModel.normalizeScores();

        return model;

    }

    @Override
    public Expression predict(Instance i, Model trainedModel, int index) {

        SemanticParsingInstance instance = (SemanticParsingInstance) i;
        decoder.setUseInstanceBasedFeatures(false);
        decoder.setStackSize(1000);

//        CCGModel model = (CCGModel) trainedModel;
        //Set<CCGFeature> mmatchingFeatures = model.getEntriesforLexeme("florida", true);
        try {
            String[] tokens = instance.getSentence().split(" ");
            List<Covering> coverings = decoder.decode(tokens, -1);
            List<Derivation> derivations = syntacticParser.parse(coverings);
            instance.setDerivation(derivations);

        } catch (Exception e) {
            Logger.getLogger("Problem with CCG parsing");
        }
        CCGDerivation max = null;

        QALD_Evaluator evaluator = new QALD_Evaluator();

        if (instance.getDerivation() != null) {
            for (Derivation m : instance.getDerivation()) {
                CCGDerivation d = (CCGDerivation) m;

                //check logical form
                Expression expression = d.getExpression();
                SPARQLParser parser = new SPARQLParser();

                QueryDBpedia queryProcessor = new QueryDBpedia();

                String instanceQuery = "";

                if (expression != null) {
                    instanceQuery = parser.convertLogicalFormToQuery(expression);
                }

                //if SPARQL query doesn't match Question type
                if (instanceQuery.startsWith("ASK")) {
                    if (instance.getSentence().startsWith("Is")) {
                        if (evaluator.isValidSPARQL(d.getExpression())) {
                            max = d;
                            break;
                        }
                    } else if (instance.getSentence().startsWith("Does")) {
                        if (evaluator.isValidSPARQL(d.getExpression())) {
                            max = d;
                            break;
                        }
                    }
                } else if (evaluator.isValidSPARQL(d.getExpression())) {
                    max = d;
                    break;
                }
            }
        }

        if (max == null) {
            return null;
        }

//        if(max!=null){
////           List<CCGLexEntry> entries = max.getEntries();
////           for(CCGLexEntry e : entries){
////               System.out.println(e.getLemma()+"\t"+e.getSyntax());
////           }
//        }
        return max.getExpression();
    }

    //extracts small parts of the given Expression
    public List<CCGLexEntry> extractLexicalEntries(Expression e) {
        List<CCGLexEntry> entries = new ArrayList<CCGLexEntry>();

        if (e instanceof LambdaCalculus.LambdaAbstraction) {
            LambdaCalculus.LambdaAbstraction abs = (LambdaAbstraction) e;

            e = abs.getBody();
        }

        //create Predicate, Constant, Adjective entries
        if (e instanceof Atom) {
            for (CCGLexEntry entry : createPredicateEntries((Atom) e)) {
                entries.add(entry);
            }

            for (CCGLexEntry entry : createAdjectiveEntries((Atom) e)) {
                entries.add(entry);
            }

            for (CCGLexEntry entry : createConstantEntries((Atom) e)) {
                entries.add(entry);
            }
        }

        if (e instanceof MultiConnector) {
            for (CCGLexEntry entry : createMultiConnectorEntries((MultiConnector) e)) {
                entries.add(entry);
            }
        }

        if (e instanceof CountingQuantifier) {
            CountingQuantifier c = (CountingQuantifier) e;
            if (c.getCondition() instanceof MultiConnector) {
                for (CCGLexEntry entry : createMultiConnectorEntries((MultiConnector) c.getCondition())) {
                    entries.add(entry);
                }
            }

            if (c.getCondition() instanceof Atom) {
                for (CCGLexEntry entry : createPredicateEntries((Atom) c.getCondition())) {
                    entries.add(entry);
                }
                for (CCGLexEntry entry : createAdjectiveEntries((Atom) c.getCondition())) {
                    entries.add(entry);
                }
                for (CCGLexEntry entry : createConstantEntries((Atom) c.getCondition())) {
                    entries.add(entry);
                }
            }
        }

        if (e instanceof ExistQuantifier) {
            ExistQuantifier c = (ExistQuantifier) e;
            if (c.getCondition() instanceof MultiConnector) {
                for (CCGLexEntry entry : createMultiConnectorEntries((MultiConnector) c.getCondition())) {
                    entries.add(entry);
                }
            }

            if (c.getCondition() instanceof Atom) {
                for (CCGLexEntry entry : createPredicateEntries((Atom) c.getCondition())) {
                    entries.add(entry);
                }
                for (CCGLexEntry entry : createAdjectiveEntries((Atom) c.getCondition())) {
                    entries.add(entry);
                }
                for (CCGLexEntry entry : createConstantEntries((Atom) c.getCondition())) {
                    entries.add(entry);
                }
            }
        }

        if (e instanceof AggregationQuantifier) {
            AggregationQuantifier a = (AggregationQuantifier) e;

            if (a.getCondition() instanceof MultiConnector) {
                for (CCGLexEntry entry : createMultiConnectorEntries((MultiConnector) a.getCondition())) {
                    entries.add(entry);
                }
            }

            if (a.getCondition() instanceof Atom) {
                for (CCGLexEntry entry : createPredicateEntries((Atom) a.getCondition())) {
                    entries.add(entry);
                }
                for (CCGLexEntry entry : createAdjectiveEntries((Atom) a.getCondition())) {
                    entries.add(entry);
                }
                for (CCGLexEntry entry : createConstantEntries((Atom) a.getCondition())) {
                    entries.add(entry);
                }
            }

            for (CCGLexEntry entry : createAggregationEntries(a)) {
                entries.add(entry);
            }

        }

        return entries;
    }

    public List<CCGLexEntry> createAggregationEntries(AggregationQuantifier a) {
        List<CCGLexEntry> entries = new ArrayList<CCGLexEntry>();

        String aggregationExpression = "";

        if (a.getAggregationExpression() instanceof Atom) {
            Atom exp = (Atom) a.getAggregationExpression();

            aggregationExpression += "Pred-" + exp.getPredicate() + "(";

            for (Term t : exp.getArguments()) {
                if (t.toString().equals(a.getAggregationVariable().toString())) {
                    aggregationExpression += t.toString() + " ";
                } else {
                    aggregationExpression += "Var-x ";
                }
            }
            aggregationExpression = aggregationExpression.trim() + ")";

        }
        String s = a.getQuant().toString() + " " + a.getAggregationVariable().toString() + " ";
        s += "({Var-g}@Var-x)" + " (" + aggregationExpression + ")";

        Expression ex1 = factory.constructLambdaCalculusExpression("lambda Var-g(lambda Var-x(" + s + "))");
        CCGLexEntry e1 = new CCGLexEntry("", factory.construcCCGCategory("(NP/N)"), ex1);

        entries.add(e1);

        return entries;

    }

    public List<CCGLexEntry> createMultiConnectorEntries(MultiConnector m) {
        List<CCGLexEntry> entries = new ArrayList<CCGLexEntry>();

        for (Expression e : m.getPredicates()) {

            for (CCGLexEntry entry : createPredicateEntries((Atom) e)) {
                entries.add(entry);
            }

            for (CCGLexEntry entry : createAdjectiveEntries((Atom) e)) {
                entries.add(entry);
            }

            for (CCGLexEntry entry : createConstantEntries((Atom) e)) {
                entries.add(entry);
            }
        }

        return entries;
    }

    public List<CCGLexEntry> createPredicateEntries(Atom a) {
        List<CCGLexEntry> entries = new ArrayList<CCGLexEntry>();

        String label = a.getPredicate();

        if (!label.contains("http://www.w3.org/1999/02/22-rdf-syntax-ns")) {
            //?x ?y P(y,x)
            Expression ex1 = factory.constructLambdaCalculusExpression("lambda Var-x(lambda Var-y(Pred-" + label + "(Var-y Var-x)))");
            //?x ?y P(x,y)
            Expression ex2 = factory.constructLambdaCalculusExpression("lambda Var-x(lambda Var-y(Pred-" + label + "(Var-x Var-y)))");

            //?g ?x ?y P(y,x) and g(y)
            Expression ex3 = factory.constructLambdaCalculusExpression("lambda Var-g(lambda Var-x(lambda Var-y(Pred-" + label + "(Var-y Var-x) and {Var-g}@Var-y)))");

            //?x ?g ?y P(y,x) and g(y)
            Expression ex4 = factory.constructLambdaCalculusExpression("lambda Var-x(lambda Var-g(lambda Var-y(Pred-" + label + "(Var-y Var-x) and {Var-g}@Var-y)))");

            CCGLexEntry e1 = new CCGLexEntry("", factory.construcCCGCategory("((S\\NP)/NP)"), ex1);
            CCGLexEntry e2 = new CCGLexEntry("", factory.construcCCGCategory("((S\\NP)/NP)"), ex2);
            CCGLexEntry e3 = new CCGLexEntry("", factory.construcCCGCategory("((S\\NP)/NP)"), ex3);
            CCGLexEntry e4 = new CCGLexEntry("", factory.construcCCGCategory("(N/NP)"), ex2);
            CCGLexEntry e7 = new CCGLexEntry("", factory.construcCCGCategory("(N/NP)"), ex1);
            CCGLexEntry e5 = new CCGLexEntry("", factory.construcCCGCategory("(N/NP)"), ex3);
            CCGLexEntry e6 = new CCGLexEntry("", factory.construcCCGCategory("((N\\N)/NP)"), ex4);

            entries.add(e1);
            entries.add(e2);
            entries.add(e4);
            entries.add(e3);
            entries.add(e7);
            entries.add(e5);
            entries.add(e6);
        } else {
            //?x rdf:type(x, C)
            if (a.getArguments().size() > 1) {
                if (a.getArguments().get(1) instanceof Constant) {

                    Constant c = (Constant) a.getArguments().get(1);
                    //?x rdf:type(x, C)
                    Expression ex1 = factory.constructLambdaCalculusExpression("lambda Var-x(Pred-" + a.getPredicate() + "(Var-x " + c.toString() + "))");

                    CCGLexEntry entry = new CCGLexEntry("", factory.construcCCGCategory("N"), ex1);

                    entries.add(entry);
                }
            }
        }

        return entries;
    }

    public List<CCGLexEntry> createAdjectiveEntries(Atom a) {
        List<CCGLexEntry> entries = new ArrayList<CCGLexEntry>();

        //?x P(x, Cons-C)
        if (!a.getPredicate().contains("http://www.w3.org/1999/02/22-rdf-syntax-ns")) {
            if (a.getArguments().size() > 1) {
                if (a.getArguments().get(1) instanceof Constant) {

                    Constant c = (Constant) a.getArguments().get(1);
                    try {
                        //?g ?y P(y,C) and g(y)
                        Expression ex1 = factory.constructLambdaCalculusExpression("lambda Var-g(lambda Var-y(Pred-" + a.getPredicate() + "(Var-y " + c.toString() + ") and {Var-g}@Var-y))");

                        CCGLexEntry entry = new CCGLexEntry("", factory.construcCCGCategory("(N/N)"), ex1);

                        entries.add(entry);
                    } catch (Exception e) {
                    }
                }
            }
        }

        return entries;
    }

    public List<CCGLexEntry> createConstantEntries(Atom a) {
        List<CCGLexEntry> entries = new ArrayList<CCGLexEntry>();

        if (!a.getPredicate().contains("http://www.w3.org/1999/02/22-rdf-syntax-ns")) {
            for (Term t : a.getArguments()) {
                if (t instanceof Constant) {
                    Constant c = (Constant) t;
                    CCGLexEntry entry = new CCGLexEntry("", factory.construcCCGCategory("NP"), c);

                    entries.add(entry);
                }
            }
        }

        return entries;
    }

    private CCGGrammar induceDefinedGrammar(boolean addDBpediaLexicon) {
        CCGGrammar inducedGrammar = new CCGGrammar();

        /*
         * add lexicon from DBpedia ontology
         */
//        induceDBpediaCategories();
//        induceDBpediaPredicates();
        List<CCGLexEntry> lexEntries = new ArrayList<CCGLexEntry>();

        ArrayList<String> words = factory.readFile(new File("src/Reader/Files/manualDefinedWords_QALD_ZC05.model"));//manual
        for (String s : words) {

            if (!s.substring(0, 2).equals("//")) {

                try {
                    String[] entries = s.split("\t");

                    Expression l = factory.constructLambdaCalculusExpression(entries[2]);

                    //String z = l.logicalFormAsString();
                    CCGLexEntry e = new CCGLexEntry(entries[0], factory.construcCCGCategory(entries[1]), l);
                    lexEntries.add(e);
                } catch (Exception e) {
                    System.out.println("Problem with defined grammar :  " + s);
                }
            }
        }

        if (addDBpediaLexicon) {

            words = factory.readFile(new File("src/Reader/Files/categories.txt"));
            for (String s : words) {

                if (!s.substring(0, 2).equals("//")) {
                    try {
                        String[] w = s.split("\t");
                        Constant c = new Constant(w[0]);
                        String p = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
                        //?x rdf:type(x, C)
                        Expression ex1 = factory.constructLambdaCalculusExpression("lambda Var-x(Pred-" + p + "(Var-x " + c.toString() + "))");

                        CCGLexEntry e1 = new CCGLexEntry(w[1].toLowerCase(), factory.construcCCGCategory("N"), factory.constructLambdaCalculusExpression("lambda Var-x(Pred-" + p + "(Var-x Cons-" + w[0] + "))"));

                        w[0] = "http://www.w3.org/2002/07/owl#Thing";

                        CCGLexEntry e2 = new CCGLexEntry(w[1].toLowerCase(), factory.construcCCGCategory("N"), factory.constructLambdaCalculusExpression("lambda Var-x(Pred-" + p + "(Var-x Cons-" + w[0] + "))"));

                        lexEntries.add(e1);
                        lexEntries.add(e2);

                    } catch (Exception e) {
                        System.out.println("Problem with defined grammar :  " + s);
                    }
                }

            }
            words = factory.readFile(new File("src/Reader/Files/predicates.txt"));
            for (String s : words) {

                if (!s.substring(0, 2).equals("//")) {
                    try {
                        String[] w = s.split("\t");

                        String label = w[0];
                        //?x ?y P(y,x)
                        Expression ex1 = factory.constructLambdaCalculusExpression("lambda Var-x(lambda Var-y(Pred-" + label + "(Var-y Var-x)))");
                        //?x ?y P(x,y)
                        Expression ex2 = factory.constructLambdaCalculusExpression("lambda Var-x(lambda Var-y(Pred-" + label + "(Var-x Var-y)))");

                        //?g ?x ?y P(y,x) and g(y)
                        Expression ex3 = factory.constructLambdaCalculusExpression("lambda Var-g(lambda Var-x(lambda Var-y(Pred-" + label + "(Var-y Var-x) and {Var-g}@Var-y)))");

                        //?x ?g ?y P(y,x) and g(y)
                        Expression ex4 = factory.constructLambdaCalculusExpression("lambda Var-x(lambda Var-g(lambda Var-y(Pred-" + label + "(Var-y Var-x) and {Var-g}@Var-y)))");

                        CCGLexEntry e1 = new CCGLexEntry(w[1].toLowerCase(), factory.construcCCGCategory("((S\\NP)/NP)"), ex1);
                        CCGLexEntry e2 = new CCGLexEntry(w[1].toLowerCase(), factory.construcCCGCategory("((S\\NP)/NP)"), ex2);
                        CCGLexEntry e3 = new CCGLexEntry(w[1].toLowerCase(), factory.construcCCGCategory("((S\\NP)/NP)"), ex3);
                        CCGLexEntry e4 = new CCGLexEntry(w[1].toLowerCase(), factory.construcCCGCategory("(N/NP)"), ex2);
                        CCGLexEntry e5 = new CCGLexEntry(w[1].toLowerCase(), factory.construcCCGCategory("(N/NP)"), ex3);
                        CCGLexEntry e6 = new CCGLexEntry(w[1].toLowerCase(), factory.construcCCGCategory("((N\\N)/NP)"), ex4);
                        CCGLexEntry e7 = new CCGLexEntry(w[1].toLowerCase(), factory.construcCCGCategory("(N/NP)"), ex1);

                        lexEntries.add(e1);
                        lexEntries.add(e2);
                        lexEntries.add(e3);
                        lexEntries.add(e4);
                        lexEntries.add(e5);
                        lexEntries.add(e6);
                        lexEntries.add(e7);

                    } catch (Exception e) {
                        System.out.println("Problem with defined grammar :  " + s);
                    }
                }

            }
        }

        words = factory.readFile(new File("src/Reader/Files/entityList_QALD.model"));//entityListSubset.model
        for (String s : words) {

            String[] entries = s.split("\t");

            Constant l = new Constant(entries[2]);

            CCGLexEntry e = new CCGLexEntry(entries[0].toLowerCase().replace(" ", "_"), factory.construcCCGCategory(entries[1]), l);
            lexEntries.add(e);
        }

        inducedGrammar.addLexicalEntries(lexEntries);

        return inducedGrammar;
    }

    private void induceDBpediaPredicates() {

        DBManagement dbManagement = new DBManagement("root", "", "semantic_parsing");

        List<String> predicates = dbManagement.getDBpediaObjectProperties();
        String fileName = "src/Reader/Files/seedList_Predicates.model";
        List<String> s = new ArrayList<>();
        int count = 0;

        for (String p : predicates) {

            String label = dbManagement.getRDFSLabel(p);

            if (!label.equals("")) {

                //?x ?y P(y,x)
                Expression ex1 = factory.constructLambdaCalculusExpression("lambda Var-x(lambda Var-y(Pred-" + label + "(Var-y Var-x)))");
                //?x ?y P(x,y)
                Expression ex2 = factory.constructLambdaCalculusExpression("lambda Var-x(lambda Var-y(Pred-" + label + "(Var-x Var-y)))");

                //?g ?x ?y P(y,x) and g(y)
                Expression ex3 = factory.constructLambdaCalculusExpression("lambda Var-g(lambda Var-x(lambda Var-y(Pred-" + label + "(Var-y Var-x) and {Var-g}@Var-y)))");

                //?x ?g ?y P(y,x) and g(y)
                Expression ex4 = factory.constructLambdaCalculusExpression("lambda Var-x(lambda Var-g(lambda Var-y(Pred-" + label + "(Var-y Var-x) and {Var-g}@Var-y)))");

                CCGLexEntry e1 = new CCGLexEntry(label, factory.construcCCGCategory("((S\\NP)/NP)"), ex1);
                CCGLexEntry e2 = new CCGLexEntry(label, factory.construcCCGCategory("((S\\NP)/NP)"), ex2);
                CCGLexEntry e3 = new CCGLexEntry(label, factory.construcCCGCategory("(N/NP)"), ex2);
                CCGLexEntry e4 = new CCGLexEntry(label, factory.construcCCGCategory("(N/NP)"), ex3);
                CCGLexEntry e5 = new CCGLexEntry(label, factory.construcCCGCategory("((N\\N)/NP)"), ex4);

                if (!s.contains(e1.toString())) {
                    s.add(e1.toString());
                }
                if (!s.contains(e2.toString())) {
                    s.add(e2.toString());
                }
                if (!s.contains(e3.toString())) {
                    s.add(e3.toString());
                }
                if (!s.contains(e4.toString())) {
                    s.add(e4.toString());
                }
                if (!s.contains(e5.toString())) {
                    s.add(e5.toString());
                }
            }
        }
        factory.writeListToFile(fileName, s);

    }

    private void induceDBpediaCategories() {

        List<String> categories = dbProcessor.getDBpediaCategories();
        String fileName = "src/Reader/Files/seedList_Categories.model";
        List<String> s = new ArrayList<>();
        int count = 0;

        //add categories with CCG Category N
        for (String c : categories) {

            //get the label
            String label = dbProcessor.getRDFSLabel(c);

            if (!label.equals("")) {

                // ?x rdf:type(?x, dbo:Actor)
                Expression ex1 = factory.constructLambdaCalculusExpression("lambda Var-x(Pred-rdf:type(Var-x Cons-" + c + "))");
                Expression ex2 = factory.constructLambdaCalculusExpression("lambda Var-x(Pred-rdf:type(Var-x Cons-owl:Thing))");

                CCGLexEntry e1 = new CCGLexEntry(label, factory.construcCCGCategory("N"), ex1);
                CCGLexEntry e2 = new CCGLexEntry(label, factory.construcCCGCategory("N"), ex2);
                if (!s.contains(e1.toString())) {
                    s.add(e1.toString());
                    count++;
                }
                if (!s.contains(e2.toString())) {
                    s.add(e2.toString());
                    count++;
                }
            }
        }
        factory.writeListToFile(fileName, s);
    }

    private CCGGrammar induceGrammar(List<Instance> instances, CCGGrammar grammar) {

        CCGGrammar inducedGrammar = new CCGGrammar();

        for (Instance i : instances) {
            SemanticParsingInstance instance = (SemanticParsingInstance) i;

            Expression form = instance.getForm();
            try {
                List<CCGLexEntry> lexEntries = extractLexicalEntries(form);

                List<String> lexUnits = extractLexicalUnits(instance.getSentence().toLowerCase(), 3, grammar);

                for (String u : lexUnits) {

                    for (CCGLexEntry e : lexEntries) {
                        CCGLexEntry newOne = e.clone();
                        newOne.setLemma(u);

                        Set<CCGLexEntry> entries = grammar.getEntriesforLexeme(newOne.getLemma());
                        if (entries == null) {
                            newOne.setInstanceId(instances.indexOf(i));
                            inducedGrammar.addLexicalEntry(newOne, instances.indexOf(i));
                        }

                    }
                }

            } catch (Exception eeee) {
                System.err.println("Problem with inducing grammar!!! " + instance.getSentence());
            }
        }

        return inducedGrammar;
    }

    private List<String> extractLexicalUnits(String sentence, int ngram_size, CCGGrammar definedGrammar) {
        List<String> lexUnits = new ArrayList<>();
        String[] unigrams = sentence.split(" ");

        for (int i = 0; i < unigrams.length; i++) {
            //lexUnits.add(unigrams[i]);

            for (int n = ngram_size; n > 0; n--) {

                if (i + n <= unigrams.length) {

                    String ngram = "";
                    ArrayList<String> list = new ArrayList<>();

                    for (int k = i; k < i + n; k++) {
                        ngram += unigrams[k] + " ";
                        list.add(unigrams[k]);
                    }

                    ngram = ngram.trim();

                    Set<CCGLexEntry> entries = definedGrammar.getEntriesforLexeme(ngram);
                    if (entries == null) {

                        boolean includesDefinedWord = false;
                        for (String l : list) {
                            entries = definedGrammar.getEntriesforLexeme(l);

                            if (entries != null) {
                                includesDefinedWord = true;
                                break;
                            }
                        }

                        if (!includesDefinedWord) {
                            lexUnits.add(ngram);
                        }

                    } else {
                        i = i + n - 1;
                        break;
                    }
                }
            }
        }
        return lexUnits;
    }

    //to extract resource for Test scenarios from given HashMap
    private List<String> extractResources(String sentence, int ngram_size, HashMap<String, List<String>> resources) {
        List<String> lexUnits = new ArrayList<>();
        sentence = sentence.substring(0, sentence.length() - 1);
        String[] unigrams = sentence.split(" ");

        Factory f = new Factory();

        ArrayList<String> stopwords = f.readFile(new File("src/Reader/Files/stopwords.txt"));

        Pattern p = Pattern.compile("^([A-Z])*");

        Matcher matcher;

        for (int i = 0; i < unigrams.length; i++) {
            //lexUnits.add(unigrams[i]);

            for (int n = ngram_size; n > 0; n--) {

                if (i + n <= unigrams.length) {

                    String ngram = "";

                    for (int k = i; k < i + n; k++) {
                        ngram += unigrams[k] + " ";
                    }

                    ngram = ngram.trim();

                    int count = 0;
                    matcher = p.matcher(ngram);
                    while (matcher.find()) {
                        count += matcher.group(0).length();
                    }

                    if (count > 0) {
                        if (!stopwords.contains(ngram)) {
                            if (resources.containsKey(ngram)) {
                                List<String> matched = resources.get(ngram);

                                //get the most frequent entity
                                String maxOne = "";
                                int maxFreq = 0;
                                for (String m : matched) {
                                    String entity = m.split("\t")[0];
                                    int freq = Integer.parseInt(m.split("\t")[1]);

                                    if (freq > maxFreq) {
                                        maxFreq = freq;
                                        maxOne = entity;
                                    }
//                                    if (freq > 10) {
//                                        lexUnits.add(ngram + "\t" + entity + "\t" + freq);
//                                    }
                                }
                                //add the most freq.
                                lexUnits.add(ngram + "\t" + maxOne + "\t" + maxFreq);

                                i = i + n - 1;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return lexUnits;
    }

}
