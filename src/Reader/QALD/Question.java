/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Reader.QALD;

import java.util.ArrayList;

/**
 *
 * @author sherzod
 */
public class Question {
    private String questionText;
    private String queryText;
    private ArrayList<String> answers;
    private int id=-1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Question(String questionText, String queryText) {
        this.questionText = questionText;
        this.queryText = queryText;
    }
    
    public Question(String questionText, String queryText, int id) {
        this.questionText = questionText;
        this.queryText = queryText;
        this.id = id;
    }
    
    public Question(String questionText, String queryText, ArrayList<String> answers) {
        this.questionText = questionText;
        this.queryText = queryText;
        this.answers = answers;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getQueryText() {
        return queryText;
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<String> answers) {
        this.answers = answers;
    }
    
    

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    @Override
    public String toString() {
        return "Question{" + "questionText=" + questionText + ", queryText=" + queryText + '}';
    }
}
