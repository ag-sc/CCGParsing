/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Evaluator;

import Database.QueryDBpedia;
import LambdaCalculus.Expression;
import Reader.QALD.SPARQLParser;
import java.util.ArrayList;

/**
 *
 * @author sherzod
 */
public class QALD_Evaluator implements Evaluator {

    private double F1 = 0;
    private String instanceQuery = "";

    public double getF1() {
        return F1;
    }

    public void setF1(double F1) {
        this.F1 = F1;
    }

    public String getInstanceQuery() {
        return instanceQuery;
    }

    public void setInstanceQuery(String instanceQuery) {
        this.instanceQuery = instanceQuery;
    }

    @Override
    public Boolean betterOrEqualTo(Expression instance, Expression expected) {

        try {
            SPARQLParser parser = new SPARQLParser();

            QueryDBpedia queryProcessor = new QueryDBpedia();

            String expectedQuery = parser.convertLogicalFormToQuery(expected);

            ArrayList<String> expectedAnswers = queryProcessor.getAnswers(expectedQuery, parser.getReturnVariable());

            if (expectedAnswers.isEmpty()) {
                return false;
            }

            String instanceQuery = "";
            if (instance != null) {
                instanceQuery = parser.convertLogicalFormToQuery(instance);
            }
            
            if (instanceQuery.equals("")) {
                return false;
            }

            ArrayList<String> instanceAnswers = queryProcessor.getAnswers(instanceQuery, parser.getReturnVariable());

            double F1 = calculateF1Score(instanceAnswers, expectedAnswers);

            setF1(F1);
            setInstanceQuery(instanceQuery);

            if (F1 >= 0.75) {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

        return false;
    }

    public boolean isValidSPARQL(Expression expression) {

        try {
            SPARQLParser parser = new SPARQLParser();

            QueryDBpedia queryProcessor = new QueryDBpedia();

            String instanceQuery = "";
            if (expression != null) {
                instanceQuery = parser.convertLogicalFormToQuery(expression);
            }

            if (instanceQuery.equals("")) {
                return false;
            }

            ArrayList<String> instanceAnswers = queryProcessor.getAnswers(instanceQuery, parser.getReturnVariable());

            if (!instanceAnswers.isEmpty()) {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

        return false;
    }

    private double calculateF1Score(ArrayList<String> candidateAnswers, ArrayList<String> answers) {
        double precision = 0;
        double recall = 0;
        double f1 = 0;

        int positiveCount = 0;

        for (String a : candidateAnswers) {
            if (answers.contains(a)) {
                positiveCount++;
            }
        }

        recall = (double) positiveCount / (double) answers.size();

        precision = (double) positiveCount / (double) candidateAnswers.size();

        if (Double.isNaN(recall)) {
            recall = 0;
        }
        if (Double.isNaN(precision)) {
            precision = 0;
        }

        f1 = (2 * precision * recall) / (precision + recall);

        if (Double.isNaN(f1)) {
            f1 = 0;
        }

//        if (f1 < 1 && f1 > 0) {
//            return Double.parseDouble(String.format("%.2f", f1 + ""));
//        }
        return f1;

    }

}
