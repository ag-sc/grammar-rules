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
public class Tuple {
    private Boolean flag=false;
    private List<String> results=new ArrayList<String>();
    
    public Tuple(Boolean flag,List<String> results){
       this.flag=flag;
       this.results=results; 
    }

    public Boolean getFlag() {
        return flag;
    }

    public List<String> getResults() {
        return results;
    }
    
}
