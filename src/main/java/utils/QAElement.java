/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import parser.GrammarRule;

/**
 *
 * @author elahi
 */
public class QAElement {

    private List<String> sortedQuestions = new ArrayList<String>();
    private List<String> bindingSparqls = new ArrayList<String>();
    private String questionSparql = null;
    private String complexSentence = null;

    public QAElement(List<String> sortQuestions, List<String> bindingSparqls, String questionSparql) {
        this.sortedQuestions =sortQuestions;
        this.bindingSparqls = bindingSparqls;
        this.questionSparql = questionSparql;
    }

    public QAElement(List<String> sortQuestions, List<String> bindingSparqls, String questionSparql, String complexSentence) {
        this.sortedQuestions =sortQuestions;
        this.bindingSparqls = bindingSparqls;
        this.complexSentence = complexSentence;
    }

    public List<String> getQuestion() {
        return sortedQuestions;
    }

    public String getComplexSentence() {
        return complexSentence;
    }

    public List<String> getBindingSparqls() {
        return bindingSparqls;
    }

    public String getQuestionSparql() {
        return questionSparql;
    }

    @Override
    public String toString() {
        return "QAElement{" + "question=" + sortedQuestions + ", sparql1=" + bindingSparqls+'}';
    }

}
