/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author elahi
 */
public class Label {

    private String uri = null;
    private String label = null;

    public Label(String objectOfProperty) {
        this.findLabelUri(objectOfProperty);
    }

    public void findLabelUri(String objectOfProperty) {
        String[] lines = objectOfProperty.split("\n");
        Integer index = 1;
        for (String line : lines) {
            line = line.trim().strip().stripLeading().stripTrailing();
            if (index == 1) {
                uri = line;
                //System.out.println(index + " " + label);
            } else if (index == 4) {
                label =  StringModifier.makeLabel(line);;
                //System.out.println(index + " " + uri);
            }
            index = index + 1;
        }

    }

    public String getUri() {
        return uri;
    }

    public String getLabel() {
        return label;
    }

   

}
