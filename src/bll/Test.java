/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bll;

import java.util.ArrayList;

/**
 *
 * @author schueler
 */
public class Test {
    private String tid;
    private String tname;
    private ArrayList<Question> questions=null;

    public Test(String tid, String tname) {
        this.tid = tid;
        this.tname = tname;
    }

    @Override
    public String toString() {
        return tname+" (Id=" + tid+ ')';
    }

    public String getTid() {
        return tid;
    }

    public String getTname() {
        return tname;
    }
    public void setQuestions(ArrayList<Question> al){
        questions=new ArrayList<Question>();
        this.questions.addAll(al);
    }
    
}
