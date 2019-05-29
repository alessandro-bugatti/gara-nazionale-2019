/*
  Copyright (C) 2019 Alessandro Bugatti (alessandro.bugatti@istruzione.it)

  This program is free software; you can redistribute it and/or
  modify it under the terms of the GNU General Public License
  as published by the Free Software Foundation; either version 2
  of the License, or (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

/** 
 *  @author Alessandro Bugatti
 *  @version 0.1
 *  @date  Creazione 11-mag-2019
 *  @date  Ultima modifica 11-mag-2019
 */

package capre;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;


public class Capra implements Comparable<Capra>{
    
    /**
     * Array contenente le varie etichette dello stato 
     * riproduttivo
     */
    public static final ArrayList<String> STATO_RIPRODUTTIVO =
    new ArrayList<>(Arrays.asList("Inattiva","Da fecondare",
            "Fecondata", "Gravida","Asciutta", "Puerpera"));

    /**
     * Array contenente le varie etichette dello stato 
     * produttivo
     */
    public static final ArrayList<String> STATO_PRODUTTIVO =
    new ArrayList<>(Arrays.asList("Asciutta","In lattazione"));
    private String codice;
    private int statoRiproduttivo;
    private int statoProduttivo;
    private int nLattazioni;
    private float kgLatte;
    private int giorni;
    private Date nascita;
    private Date ultimoParto;
   
    /**
     * Costruttore che inizializza lo stato di una capra
     * prendendo come parametro una stringa con il formato
     * descritto nel testo di gara
     * @param codifica Stringa contenente lo stato di una capra
     * @throws NumberFormatException
     * @throws ParseException
     */
    public Capra(String codifica) throws NumberFormatException, ParseException{

        this.codice = codifica.substring(0, 8);
        this.statoRiproduttivo = Integer.parseInt(codifica.substring(8,10));
        this.statoProduttivo = Integer.parseInt(codifica.substring(10,12));
        this.nLattazioni = Integer.parseInt(codifica.substring(12,14));
        this.kgLatte = Float.parseFloat(codifica.substring(14,19));
        this.giorni = Integer.parseInt(codifica.substring(19,22));
        this.nascita = new SimpleDateFormat("dd/MM/yy").
                parse(codifica.substring(22,30)); 
        this.ultimoParto = new SimpleDateFormat("dd/MM/yy").
                parse(codifica.substring(30,38));
        }
    
    
    
    /**
     * Ritorna il codice 
     *
     * @return Codice
     */
    public String getCodice() {
        return codice;
    }

    /**
     * Imposta il codice
     *
     * @param codice Nuovo valore del codice 
     */
    public void setCodice(String codice) {
        this.codice = codice;
    }

    /**
     * Ritorna lo stato riproduttivo
     * @return Stato riproduttivo
     */
    public String getStatoRiproduttivo() {
        return STATO_RIPRODUTTIVO.get(statoRiproduttivo);
    }

    /**
     * Imposta lo stato riproduttivo
     * @param statoRiproduttivo è un codice numerico che corrisponde
     * a un'etichetta linguistica come descritto nel testo di gara
     */
    public void setStatoRiproduttivo(int statoRiproduttivo) {
        this.statoRiproduttivo = statoRiproduttivo;
    }
    
    /**
     * Ritorna lo stato produttivo
     * @return Stato produttivo
     */
    public String getStatoProduttivo() {
        return STATO_PRODUTTIVO.get(statoProduttivo);
    }

    /**
     * Imposta lo stato produttivo
     * @param statoProduttivo  è un codice numerico che corrisponde
     * a un'etichetta linguistica come descritto nel testo di gara
     */
    public void setStatoProduttivo(int statoProduttivo) {
        this.statoProduttivo = statoProduttivo;
    }

    /**
     * Ritorna il numero di lattazioni
     * @return Numero di lattazioni
     */
    public int getNLattazioni() {
        return nLattazioni;
    }

    /**
     * Imposta il numero di lattazioni
     * @param nLattazioni
     */
    public void setNLattazioni(int nLattazioni) {
        this.nLattazioni = nLattazioni;
    }

    /**
     * Ritorna i Kg di latte
     * @return Kg di latte
     */
    public float getKgLatte() {
        return kgLatte;
    }

    /**
     * Imposta i Kg di latte
     * @param kgLatte
     */
    public void setKgLatte(float kgLatte) {
        this.kgLatte = kgLatte;
    }

    /**
     * Ritorna i giorni passati dall'ultima lattazione
     * @return giorni passati dall'ultima lattazione
     */
    public int getGiorni() {
        return giorni;
    }

    /**
     * Imposta i giorni passati dall'ultima lattazione
     * @param giorni
     */
    public void setGiorni(int giorni) {
        this.giorni = giorni;
    }

    /**
     * Ritorna la data di nascita
     * @return Data di nascita
     */
    public Date getNascita() {
        return nascita;
    }

    /**
     * Imposta la data di nascita
     * @param nascita
     */
    public void setNascita(Date nascita) {
        this.nascita = nascita;
    }

    /**
     * Ritorna la data dell'ultimo parto
     * @return Data dell'ultimo parto
     */
    public Date getUltimoParto() {
        return ultimoParto;
    }

    /**
     * Imposta la data dell'ultimo parto
     * @param ultimoParto
     */
    public void setUltimoParto(Date ultimoParto) {
        this.ultimoParto = ultimoParto;
    }

    /**
     * Ritorna lo stato in una stringa che segue il formato
     * descritto nel testo di gara, uguale a quello 
     * già presente nel file in lettura
     * @return Data dell'ultimo parto
     */
    @Override
    public String toString() {
        String s;
        s = String.format(Locale.US,"%-8s%02d%02d%02d%05.2f%03d", 
                codice, 
                statoRiproduttivo,
                statoProduttivo,
                nLattazioni,
                kgLatte,
                giorni);
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/YY");
        s += f.format(nascita);
        s += f.format(ultimoParto);
        s += "\n";
        return s;
    }

    /**
     * Ridefinizione sulla data di nascita
     * @param c la capra di cui si confronta la data di nascita 
     * @return 
     */
    @Override
    public int compareTo(Capra c) {
        return this.nascita.compareTo(c.nascita);
    }
    
}
