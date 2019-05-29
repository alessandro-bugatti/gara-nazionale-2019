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
 *  Classe per gestire le eventuali eccezioni generate dalla
 * lettura delle righe presenti nel file con le informazioni sulle
 * capre
 *  @author Alessandro Bugatti
 *  @version 0.1
 *  @date  Creazione 11-mag-2019
 *  @date  Ultima modifica 11-mag-2019
 */

package capre;

import java.util.ArrayList;
import javafx.util.Pair;

public class LoadingException extends Exception{
    ArrayList<Pair<Exception, Integer>> exceptions;

    /**
     * Costruttore che inizializza l'ArrayList
     */
    public LoadingException()
    {
        exceptions = new ArrayList<>();
    }
    
    /**
     * Aggiunge un'eccezione alla lista delle eccezioni
     * @param e Eccezione da memorizzare
     * @param riga Riga nel file di testo delle capre che ha 
     * generato l'eccezione perchè malformata
     */
    public void addException(Exception e, int riga)
    {
        exceptions.add(new Pair(e, riga));
    }
    
    /**
     * Ritorna una coppia eccezione-numero di riga che l'ha generata,
     * per permettere di individuare quale riga/righe del file 
     * di testo sono problematiche
     * @param i
     * @return Coppia eccezione - numero di riga
     */
    public Pair<Exception, Integer> get(int i)
    {
        return exceptions.get(i);
    }
    
    /**
     * Ritorna il numero di eccezioni presenti nella lista
     * @return numero di eccezioni presenti nella lista
     */
    public int size()
    {
        return exceptions.size();
    }
}
