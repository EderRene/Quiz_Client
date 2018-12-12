/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bll;

import java.util.Objects;

/**
 *
 * @author schueler
 */
public class Antwort {
    private String quizId;
    private int frageId;
    private int id;
    private String antwort;
    private Boolean right=false;

    public Boolean getRight() {
        return right;
    }

    public void setRight(Boolean right) {
        this.right = right;
    }

    public Antwort(String quizId, int frageId, int id, String antwort) {
        this.quizId = quizId;
        this.frageId = frageId;
        this.id = id;
        this.antwort = antwort;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.quizId);
        hash = 59 * hash + this.frageId;
        hash = 59 * hash + this.id;
        hash = 59 * hash + Objects.hashCode(this.antwort);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Antwort other = (Antwort) obj;
        if (this.frageId != other.frageId) {
            return false;
        }
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.quizId, other.quizId)) {
            return false;
        }
        if (!Objects.equals(this.antwort, other.antwort)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return antwort;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public int getFrageId() {
        return frageId;
    }

    public void setFrageId(int frageId) {
        this.frageId = frageId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAntwort() {
        return antwort;
    }

    public void setAntwort(String antwort) {
        this.antwort = antwort;
    }
    
    
}
