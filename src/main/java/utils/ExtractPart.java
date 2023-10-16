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
public class ExtractPart {

    private String restrictionClassVariable = null;
    private List<String> entities = new ArrayList<String>();

    public ExtractPart(List<String> extractParts, String template) {
        if (template.contains("predicateAdjectiveBaseForm")) {
            this.restrictionClassVariable = extractParts.get(0);
        } else {
            if (extractParts.size() == 2) {
                this.restrictionClassVariable = extractParts.get(0);
                this.entities.add(extractParts.get(1));
            }
            if (extractParts.size() == 1) {
                if(extractParts.get(0)!=null)
                  this.entities.add(extractParts.get(0));
            }
        }

    }

    public String getRestrictionClassVariable() {
        return restrictionClassVariable;
    }

    public List<String> getEntities() {
        return entities;
    }

    @Override
    public String toString() {
        return "ExtractPart{" + "restrictionClassVariable=" + restrictionClassVariable + ", entities=" + entities + '}';
    }

}
