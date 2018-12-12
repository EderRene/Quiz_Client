/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bll;

/**
 *
 * @author schueler
 */
public class Candidate {
    private String name;
    private String schooltyp;
    private int id;

    public Candidate(String name, String schooltyp,int id) {
        this.name = name;
        this.schooltyp = schooltyp;
        this.id=id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchooltyp() {
        return schooltyp;
    }

    public void setSchooltyp(String schooltyp) {
        this.schooltyp = schooltyp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
}
