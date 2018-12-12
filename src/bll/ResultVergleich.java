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
public class ResultVergleich {

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.username);
        hash = 37 * hash + Objects.hashCode(this.ergebnis);
        hash = 37 * hash + Objects.hashCode(this.besser);
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
        final ResultVergleich other = (ResultVergleich) obj;
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        if (!Objects.equals(this.ergebnis, other.ergebnis)) {
            return false;
        }
        if (!Objects.equals(this.besser, other.besser)) {
            return false;
        }
        return true;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getErgebnis() {
        return ergebnis;
    }

    public void setErgebnis(Integer ergebnis) {
        this.ergebnis = ergebnis;
    }

    public Integer getBesser() {
        return besser;
    }

    public void setBesser(Integer besser) {
        this.besser = besser;
    }
    private String username;
    private Integer ergebnis;
    private Integer besser;

    public ResultVergleich(String username, Integer ergebnis, Integer besser) {
        this.username = username;
        this.ergebnis = ergebnis;
        this.besser = besser;
    }

    @Override
    public String toString() {
        return "ResultVergleich{" + "Username=" + username + ", Ergebnis=" + ergebnis + ", besser=" + besser + '}';
    }
    
}
