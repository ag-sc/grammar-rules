/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author elahi
 */
public class QAElement {
    private List<String> question = new ArrayList<String>();
    private String sparql = null;
    
    public QAElement(List<String> question,String sparql){
       this.question = question;
       this.sparql = sparql;
    }

    public List<String> getQuestion() {
        return question;
    }

    public String getSparql() {
        return sparql;
    }

    @Override
    public String toString() {
        return "QAElement{" + "question=" + question + ", sparql=" + sparql + '}';
    }
    
}
