/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

/**
 *
 * @author sherzod
 */
public class Element {
    private String subject;
    private String predicate;
    private String object;

    public Element(String subject, String predicate, String object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "Element{" + "subject=" + subject + ", predicate=" + predicate + ", object=" + object + '}';
    }
}
