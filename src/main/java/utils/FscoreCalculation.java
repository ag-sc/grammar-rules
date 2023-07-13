/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import static java.lang.System.exit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;

/**
 *
 * @author elahi
 */
public class FscoreCalculation {

    private Integer tp = 0;
    private Integer fp = 0;
    private Integer fn = 0;
    private float precision = 0;
    private float recall = 0;
    private float fscore = 0;
    private float precision_av = 0;
    private float recall_av = 0;
    private float fscore_av = 0;

    public FscoreCalculation(float Tp, float Fp, float Fn) {
        this.precision = getPrecision(Tp, Fp, Fn);
        this.recall = getRecall(Tp, Fp, Fn);
        this.fscore = getFscore(Tp, Fp, Fn);

    }

    public FscoreCalculation(float Tp, float Fp, float Fn, boolean flag) {
        this.precision_av = getPrecision(Tp, Fp, Fn);
        this.recall_av = getRecall(Tp, Fp, Fn);
        this.fscore_av = getFscore(Tp, Fp, Fn);
    }

    public FscoreCalculation(Set<String> qald, Set<String> queGGR) {
        List<String> qaldResults = new ArrayList<String>(qald);
        List<String> queGGResults = new ArrayList<String>(queGGR);
        tp = this.getTp(qaldResults, queGGResults);
        fp = this.getFp(qaldResults, queGGResults);
        fn = this.getFn(qaldResults, queGGResults);
        /*if (qaldResults.toString().contains("Gamma Ray")) {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!::" );
            System.out.println("qaldResults::" + qaldResults);
            System.out.println("queGGResults::" + queGGResults);
            System.out.println("Tp::" + tp);
            System.out.println("Fp::" + fp);
            System.out.println("Fn::" + fn);
        }*/
        this.precision = getPrecision(tp, fp, fn);
        this.recall = getRecall(tp, fp, fn);
        this.fscore = getFscore(tp, fp, fn);
        //System.out.println("precision::" + precision + " recall::" + recall + " fScore::" + fscore);
    }


    /*
    True  Positive:  Number of results in QueGG SPARQL query that are also in QALD  query results
    Both in QALD and QueGG resuts
     */
    private Integer getTp(List<String> qaldResults, List<String> queGGResults) {
        Set<String> existBothQaldQueGG = new HashSet<String>();
        qaldResults = this.validList(qaldResults);
        queGGResults = this.validList(queGGResults);
        existBothQaldQueGG = new HashSet<String>(qaldResults);
        existBothQaldQueGG.retainAll(queGGResults);
        System.out.println("qaldResults::"+qaldResults);
        System.out.println("queGGResults::"+queGGResults);
        System.out.println("existBothQaldQueGG::"+existBothQaldQueGG);
        System.out.println("existBothQaldQueGG::"+existBothQaldQueGG);
        if (existBothQaldQueGG.isEmpty()) {
            return 0;
        } else {
            return existBothQaldQueGG.size();
        }
    }

    /*
    False Positive:  Number of results in QueGG SPARQL query that are not  in QALD  query results
    Exist in QueGG but NOT in QALD
     */
    private Integer getFp(List<String> qaldResults, List<String> queGGResults) {
        Integer fp = 0;
        qaldResults = this.validList(qaldResults);
        queGGResults = this.validList(queGGResults);

        for (String queGGElement : queGGResults) {
            if (queGGElement.isEmpty()) {
                continue;
            } else if (qaldResults.contains(queGGElement)) {
                continue;
            } else {
                fp = fp + 1;
            }
        }
        if (queGGResults.isEmpty()) {
            return 0;
        } else {
            return fp;
        }

    }

    /*alse Negative:  Number of results in QALD  SPARQL query that are not  in QueGG query results
    Exist QALD but NOT in QueGG
     */
    private Integer getFn(List<String> qaldResults, List<String> queGGResults) {
        Integer fn = 0;
        qaldResults = this.validList(qaldResults);
        queGGResults = this.validList(queGGResults);

        for (String qaldElement : qaldResults) {
            if (queGGResults.contains(qaldElement)) {
                continue;
            } else {
                //existQaldNotQueGG.add(qaldElement);
                fn = fn + 1;
            }
        }
        return fn;

    }

    private float getPrecision(float Tp, float Fp, float Fn) {
        if ((Tp + Fp) > 0) {
            return (Tp / (Tp + Fp));
        }
        return 0;
    }

    private float getRecall(float Tp, float Fp, float Fn) {
        if ((Tp + Fn) > 0) {
            return (Tp / (Tp + Fn));
        }
        return 0;
    }

    private float getPrecision_av(float Tp, float Fp, float Fn) {
        if ((Tp + Fp) > 0) {
            return (Tp / (Tp + Fp));
        }
        return 0;
    }

    private float getRecall_av(float Tp, float Fp, float Fn) {
        if ((Tp + Fn) > 0) {
            return (Tp / (Tp + Fn));
        }
        return 0;
    }

    private float getFscore_av(float Tp, float Fp, float Fn) {
        if ((Tp + Fp) > 0) {
            return (Tp / (Tp + Fp));
        }
        return 0;
    }

    private float getFscore(float Tp, float Fp, float Fn) {
        precision = getPrecision(Tp, Fp, Fn);
        recall = getRecall(Tp, Fp, Fn);
        if ((precision * recall) > 0) {
            return (2 * (precision * recall) / (precision + recall));
        } else {
            return 0;
        }
    }

    public float getPrecision() {
        return precision;
    }

    public float getRecall() {
        return recall;
    }

    public float getFscore() {
        return fscore;
    }

    public Integer getTp() {
        return tp;
    }

    public Integer getFp() {
        return fp;
    }

    public Integer getFn() {
        return fn;
    }

    public static void test1() {

        Set<String> qaldResults = new HashSet<String>();
        qaldResults.add("http://dbpedia.org/resource/Walter_Koenig");
        qaldResults.add("http://dbpedia.org/resource/DeForest_Kelley");
        qaldResults.add("http://dbpedia.org/resource/Leonard_Nimoy");
        qaldResults.add("http://dbpedia.org/resource/George_Takei");
        qaldResults.add("http://dbpedia.org/resource/Nichelle_Nichols");
        qaldResults.add("http://dbpedia.org/resource/William_Shatner");
        qaldResults.add("http://dbpedia.org/resource/James_Doohan");
        Set<String> queGGResults = new HashSet<String>();
        queGGResults.add("http://dbpedia.org/resource/Star_Trek_V:_The_Final_Frontier");
        queGGResults.add("http://dbpedia.org/resource/The_Captains_(film)");
        queGGResults.add("http://dbpedia.org/resource/Groom_Lake_(film)");

        System.out.println("test1::");
        System.out.println("qaldResults::" + qaldResults);
        System.out.println("queGGResults::" + queGGResults);
        FscoreCalculation fscore = new FscoreCalculation(qaldResults, queGGResults);
        System.out.println("precision::" + fscore.getPrecision() + " recall::" + fscore.getRecall() + " Fscore::" + fscore.getFscore());

    }

    public static void test2() {

        Set<String> qaldResults = new HashSet<String>();
        qaldResults.add("1330");
        Set<String> queGGResults = new HashSet<String>();
        queGGResults.add("0");
        System.out.println("test2::");
        System.out.println("qaldResults::" + qaldResults);
        System.out.println("queGGResults::" + queGGResults);
        FscoreCalculation fscore = new FscoreCalculation(qaldResults, queGGResults);

    }

    public static void test3() {

        Set<String> qaldResults = new HashSet<String>();
        qaldResults.add("http://dbpedia.org/resource/Murderecords");
        Set<String> queGGResults = new HashSet<String>();
        System.out.println("test2::");
        System.out.println("qaldResults::" + qaldResults);
        System.out.println("queGGResults::" + queGGResults);
        FscoreCalculation fscore = new FscoreCalculation(qaldResults, queGGResults);
    }

    public static void test4() {

        Set<String> qaldResults = new HashSet<String>();
        qaldResults.add("Gamma Ray 1996–1997");
        Set<String> queGGResults = new HashSet<String>();
        System.out.println("test2::");
        System.out.println("qaldResults::" + qaldResults);
        System.out.println("queGGResults::" + queGGResults);
        FscoreCalculation fscore = new FscoreCalculation(qaldResults, queGGResults);

    }

    public static void main(String[] args) {

        test1(); //passed
        test2(); //passed
        test3(); //passed
        test4(); //passed

    }

    private List<String> validList(List<String> list) {
        List<String> result = new ArrayList<String>();
        for (String element : list) {
            if (element.isEmpty()) {
                continue;
            } else {
                result.add(element);
            }
        }
        return result;
    }

}
