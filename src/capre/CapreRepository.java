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

/** Classe per la gestione di un "gregge" di capre
 *  Espone un iteratore per la navigazione avanti/indietro,
 *  metodi per il caricamento/salvataggio da file,
 *  per l'ordinamento usando il MergeSort sequenziale e parallelo
 * 
 *  @author Alessandro Bugatti
 *  @version 0.1
 *  @date  Creazione 11-mag-2019
 *  @date  Ultima modifica 11-mag-2019
 */

package capre;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ForkJoinPool;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import parallel.ParallelMergeSort;

public class CapreRepository {
    /* Lista delle capre, implementata come ArrayList */
    private List<Capra> capre;
    private ListIterator iteratoreElementoCorrente;
    private Capra elementoCorrente;
    /* 
    * Flag per rendere corretto il cambio di direzione
    *  nello spostamento avanti/indietro tra le capre
    */
    private boolean avanti = true;
    
    /**
     * Costruttore che istanzia un ArrayList
     */
    public CapreRepository()
    {
        capre = new ArrayList<>();
    }
    
    /**
     * Carica un file con il formato indicato nella documentazione
     * del testo della gara
     * @param filename Stringa che rappresenta il percorso assoluto
     * del file che deve essere caricato
     * @throws FileNotFoundException
     * @throws IOException
     * @throws LoadingException
     */
    public void loadFile(String filename) throws FileNotFoundException, IOException, LoadingException
    {
        BufferedReader buf = new BufferedReader(new FileReader(filename));
        String riga;
        LoadingException exs = new LoadingException();
        int numero_riga = 1;
        capre.clear();
        while ((riga = buf.readLine()) != null)
        {
            if (!riga.isEmpty())
            {
                Capra c = null;
                try {
                    c = new Capra(riga);
                } catch (NumberFormatException | ParseException ex) {
                    exs.addException(ex,numero_riga);
                }
                if (c != null)
                    capre.add(c);
            }
            numero_riga++;
        }
        firstPosition();
        if (exs.size() != 0)
            throw exs;
       }
    
    /**
     * Salva tutte le capre contenute nel "gregge" all'interno di filename
     * nell'ordine nel quale compaiono nella lista e nello stesso
     * formato utilizzato da loadFile per il caricamento
     * @param filename Stringa che rappresenta il percorso assoluto
     * del file dove saranno salvati i dati delle capre 
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void saveToFile(String filename) throws FileNotFoundException, IOException
    {
        BufferedWriter buf = new BufferedWriter(new FileWriter(filename));
        for (Capra c: capre)
            buf.write(c.toString());
        buf.close();
    }
    
    /**
     *
     * @return Ritorna il numero di capre del "gregge"
     */
    public int numCapre()
    {
        return capre.size();
    }
    
    /**
     * 
     * @return Ritorna la capra che al momento è indicata dall'iteratore
     */
    public Capra getElementoCorrente()
    {
        return elementoCorrente;
    }
    
    /**
     * Mette l'iteratore sulla prima capra della lista
     */
    public void firstPosition()
    {
        if (!capre.isEmpty())
        {
            iteratoreElementoCorrente = capre.listIterator();
            elementoCorrente = (Capra) iteratoreElementoCorrente.next();
        }
    }
    
    /**
     * Verifica se esiste un'altra capra dopo quella corrente
     * @return Ritorna true se esiste almeno un'altra capra dopo
     * quella corrente, false altrimenti
     */
    public boolean hasNext()
    {
        return iteratoreElementoCorrente.hasNext();
    }
    
    /** 
     * Sposta l'iteratore all'elemento successivo
     * @return Ritorna la capra corrente dopo lo spostamento
     */
    public Capra moveToElementoSuccessivo()
    {
        if (iteratoreElementoCorrente.hasNext())
        {
            if (!avanti){
                iteratoreElementoCorrente.next();
                avanti = true;
            }
            return elementoCorrente = (Capra) iteratoreElementoCorrente.next();
        }
        else 
            return null;
    }
    
    /**
     * Verifica se esiste un'altra capra prima di quella corrente
     * @return Ritorna true se esiste almeno un'altra capra prima di
     * quella corrente, false altrimenti
     */
    public boolean hasPrevious()
    {
        return iteratoreElementoCorrente.hasPrevious();
    }
    
    /** 
     * Sposta l'iteratore all'elemento precedente
     * @return Ritorna la capra corrente dopo lo spostamento
     */
    public Capra moveToElementoPrecedente()
    {
        if (iteratoreElementoCorrente.hasPrevious())
        {
            if (avanti){
                iteratoreElementoCorrente.previous();
                avanti = false;
            }
            return elementoCorrente = (Capra) iteratoreElementoCorrente.previous();
        }else 
            return null;
    }
    
    /**
     * Esporta il gregge in un file XML
     * @param filename Percorso del file dove verranno salvati
     * i dati del gregge in formato XML come indicato nel testo
     * di gara
     * @throws TransformerConfigurationException
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    public void esportaXML(String filename) throws TransformerConfigurationException, ParserConfigurationException, TransformerException
    {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
 
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
 
            Document document = documentBuilder.newDocument();
 
            // root element
            Element root = document.createElement("capre");
            document.appendChild(root);
 
            for (Capra c: capre)
            {
                Element capra = document.createElement("capra");
                root.appendChild(capra);
 
                Attr attr = document.createAttribute("codice");
                attr.setValue(c.getCodice());
                capra.setAttributeNode(attr);
                
                Element el = document.createElement("stato_riproduttivo");
                el.appendChild(document.createTextNode(c.getStatoRiproduttivo()));
                capra.appendChild(el);
                el = document.createElement("stato_produttivo");
                el.appendChild(document.createTextNode(c.getStatoProduttivo()));
                capra.appendChild(el);
                el = document.createElement("numero_lattazioni");
                el.appendChild(document.createTextNode(Integer.toString(c.getNLattazioni())));
                capra.appendChild(el);
                el = document.createElement("kg_latte");
                el.appendChild(document.createTextNode(Float.toString(c.getKgLatte())));
                capra.appendChild(el);
                el = document.createElement("ultima_lattazione");
                el.appendChild(document.createTextNode(Integer.toString(c.getGiorni())));
                capra.appendChild(el);
                el = document.createElement("data_nascita");
                el.appendChild(document.createTextNode(new SimpleDateFormat("dd/MM/YYYY").format(c.getNascita())));
                capra.appendChild(el);
                el = document.createElement("data_ultimo_parto");
                el.appendChild(document.createTextNode(new SimpleDateFormat("dd/MM/YYYY").format(c.getUltimoParto())));
                capra.appendChild(el);
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(filename));
            transformer.transform(domSource, streamResult);
    }
    
    /**
     * Ordina il "gregge" di capre utilizzando l'algoritmo
     * di mergesort con chiave di ordinamento la data di nascita,
     * come definita nel metodo compareTo della classe Capra
     * L'algoritmo è definito nel testo di gara
     */
    public void merge()
    {
        mergesortR(0, capre.size() - 1);
    }
    
    /* Algoritmo ricorsivo di mergesort*/
    private void mergesortR (int left, int right){
        if (left < right){
            int center  = (left + right) / 2;
            mergesortR(left, center);
            mergesortR(center + 1, right);
            merge(left, center, right);
        }
    }
    
    /* Parte del merge dell'algoritmo di ordinamento*/
    private void merge (int left, int center, int right){
        int i = left;
        int j = center + 1;
        int k = 0;
        Capra b[] = new Capra[right - left + 1];
        while(i <= center && j <= right)
        {
            if (capre.get(i).compareTo(capre.get(j)) <= 0)
            {
                b[k] = capre.get(i);
                i++;
            }         
            else{
                b[k] = capre.get(j);
                j++;
            }
          k++;
        }

        while (i <= center){
            b[k] = capre.get(i);
            i++;
            k++;
        }

       while (j <= right){
            b[k] = capre.get(j);
            j++;
            k++;
        }

        for (k = left; k <= right; k++)
            capre.set(k,b[k-left]);
    }
    
    /**
     * Algoritmo di ordinamento che utilizza i thread
     * per parallelizzare l'esecuzione.
     * L'algoritmo è definito nella classe ParallelMergeSort
     */
    public void parallelMerge()
    {
        final ForkJoinPool forkJoinPool = 
        new ForkJoinPool(Runtime.getRuntime().availableProcessors() - 1);
        Capra capreArray[] = new Capra[capre.size()];
        capreArray= capre.toArray(capreArray);
        forkJoinPool.invoke(new ParallelMergeSort(capreArray, 0, capre.size() - 1));
        capre = new ArrayList<>(Arrays.asList(capreArray));
    }
   
}
