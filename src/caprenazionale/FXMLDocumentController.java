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

package caprenazionale;

import capre.Capra;
import capre.CapreRepository;
import capre.LoadingException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Date;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

/**
 *
 * @author Alessandro Bugatti <alessandro.bugatti@gmail.com>
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private BorderPane root;
    @FXML
    private MenuItem mnuSuccessivo;
    @FXML
    private MenuItem mnuPrecedente;
    @FXML
    private Label lblCodice;
    @FXML
    private ChoiceBox<String> choStatoRiproduttivo;
    @FXML
    private ChoiceBox<String> choStatoProduttivo;
    @FXML
    private TextField txtNLattazioni;
    @FXML
    private DatePicker dateNascita;
    @FXML
    private DatePicker dateParto;
    
    private CapreRepository capreRepository;
    @FXML
    private TextField txtLatte;
    @FXML
    private TextField txtGiorni;
    @FXML
    private Button btnSuccessivo;
    @FXML
    private Button btnPrecedente;
    @FXML
    private Button btnEdit;
    @FXML
    private MenuItem mnuSalva;
    @FXML
    private MenuItem mnuEsporta;
    @FXML
    private MenuItem mnuClose;
    @FXML
    private MenuItem mnuMerge;
    @FXML
    private MenuItem mnuMergeParallelo;

    private void salvaStatoCapra() {
        Capra corrente = capreRepository.getElementoCorrente();
        corrente.setStatoRiproduttivo(choStatoRiproduttivo.getSelectionModel().getSelectedIndex());
        corrente.setStatoProduttivo(choStatoProduttivo.getSelectionModel().getSelectedIndex());
        corrente.setNLattazioni(Integer.parseInt(txtNLattazioni.getText()));
        corrente.setKgLatte(Float.parseFloat(txtLatte.getText()));
        corrente.setGiorni(Integer.parseInt(txtGiorni.getText()));
        Instant instant = Instant.from(dateNascita.getValue().atStartOfDay(ZoneId.systemDefault()));
        corrente.setNascita(Date.from(instant));
        instant = Instant.from(dateParto.getValue().atStartOfDay(ZoneId.systemDefault()));
        corrente.setUltimoParto(Date.from(instant));
    }

    @FXML
    private void SalvaFile(ActionEvent event) throws IOException, FileNotFoundException {
        Stage stage = (Stage) root.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.setInitialDirectory(new File("."));
        File file = fileChooser.showSaveDialog(stage);
        if (file != null){
            capreRepository.saveToFile(file.getAbsolutePath());
        }
    }

    @FXML
    private void esportaXML(ActionEvent event) {
        Stage stage = (Stage) root.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.setInitialDirectory(new File("."));
        File file = fileChooser.showSaveDialog(stage);
        if (file != null){
            try {
                capreRepository.esportaXML(file.getAbsolutePath());
            } catch (ParserConfigurationException | TransformerException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
       
    }

    @FXML
    private void chiudi(ActionEvent event) {
        root.getScene().getWindow().fireEvent(
        new WindowEvent(
            root.getScene().getWindow(),
            WindowEvent.WINDOW_CLOSE_REQUEST
    )
);
    }

    @FXML
    private void mergeSort(ActionEvent event) {
        capreRepository.merge();
        capreRepository.firstPosition();
        disabilitaMovimento(false);
        mnuPrecedente.setDisable(true);
        btnPrecedente.setDisable(true);
        btnEdit.setDisable(false);
        mnuSalva.setDisable(false);
        mnuEsporta.setDisable(false);
        aggiorna(); 
    }

    @FXML
    private void mergeSortParallelo(ActionEvent event) {
        capreRepository.parallelMerge();
        capreRepository.firstPosition();
        disabilitaMovimento(false);
        mnuPrecedente.setDisable(true);
        btnPrecedente.setDisable(true);
        btnEdit.setDisable(false);
        mnuSalva.setDisable(false);
        mnuEsporta.setDisable(false);
        aggiorna(); 
    }
    
    
    private enum STATO_EDITING {MODIFICA, SALVA};
    private STATO_EDITING stato_editing = STATO_EDITING.MODIFICA;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        choStatoRiproduttivo.setItems(FXCollections.observableArrayList(Capra.STATO_RIPRODUTTIVO));
        choStatoProduttivo.setItems(FXCollections.observableArrayList(Capra.STATO_PRODUTTIVO));
        System.out.println(root.getBottom());
        disabilitaPannelloCapra(true); 
        disabilitaMovimento(true);
    }    
    
    public void disabilitaMovimento(boolean stato)
    {
        mnuPrecedente.setDisable(stato);
        mnuSuccessivo.setDisable(stato);
        btnPrecedente.setDisable(stato);
        btnSuccessivo.setDisable(stato);
        mnuSalva.setDisable(stato);
        mnuEsporta.setDisable(stato);
        mnuMerge.setDisable(stato);
        mnuMergeParallelo.setDisable(stato);
    }
    
    public void disabilitaPannelloCapra(boolean stato)
    {
        choStatoRiproduttivo.setDisable(stato);
        choStatoProduttivo.setDisable(stato);
        txtNLattazioni.setDisable(stato);
        txtLatte.setDisable(stato);
        txtGiorni.setDisable(stato);
        dateNascita.setDisable(stato);
        dateParto.setDisable(stato);
        btnEdit.setDisable(stato);      
    }
        
    @FXML
    private void CaricaFile(ActionEvent event) {
        Stage stage = (Stage) root.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.setInitialDirectory(new File("."));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null){
            capreRepository = (capre.CapreRepository) root.getUserData();
            try {
                capreRepository.loadFile(file.getAbsolutePath());
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (LoadingException ex) {
                ExceptionDialog(ex);
            }
        }
        else 
            return;
        if (capreRepository.numCapre() == 0)
        {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Errore di caricamento");
            alert.setHeaderText("Errore nel caricamento del file " + file.getName());
            alert.setContentText("Non sembrano essere presenti righe valide!");
            alert.showAndWait();
            disabilitaPannelloCapra(true);
            disabilitaMovimento(true);
            return;
        }
        disabilitaMovimento(false);
        mnuPrecedente.setDisable(true);
        btnPrecedente.setDisable(true);
        btnEdit.setDisable(false);
        mnuSalva.setDisable(false);
        mnuEsporta.setDisable(false);
        aggiorna();    
    }
    
    private void ExceptionDialog(LoadingException ex)
    {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Eccezione");
        alert.setHeaderText("Problema nel caricamento del file dei dati");
        String problema; 
        problema = "Nel file sono presenti " + ex.size() + " righe corrotte";
        alert.setContentText(problema);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        for (int i = 0; i < ex.size(); i++)
            pw.print("Riga " + ex.get(i).getValue() + 
                    ": " + ex.get(i).getKey() + "\n");
        String exceptionText = sw.toString();

        Label label = new Label("Informazioni dettagliate");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    private void aggiorna()
    {
        lblCodice.setText("Codice capra: " + capreRepository.getElementoCorrente().getCodice());
        choStatoRiproduttivo.setValue(capreRepository.getElementoCorrente().getStatoRiproduttivo());
        choStatoProduttivo.setValue(capreRepository.getElementoCorrente().getStatoProduttivo());
        txtNLattazioni.setText(Integer.toString(capreRepository.getElementoCorrente().getNLattazioni()));
        txtLatte.setText(Float.toString(capreRepository.getElementoCorrente().getKgLatte()));
        txtGiorni.setText(Integer.toString(capreRepository.getElementoCorrente().getGiorni()));
        dateNascita.setValue(capreRepository.getElementoCorrente().getNascita().
                toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        dateParto.setValue(capreRepository.getElementoCorrente().getUltimoParto().
                toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        
    }
    @FXML
    private void vaiAlSuccessivo(ActionEvent event) {
        capreRepository.moveToElementoSuccessivo();
        if (!capreRepository.hasNext())
        {
            btnSuccessivo.setDisable(true);
            mnuSuccessivo.setDisable(true);
        }
        btnPrecedente.setDisable(false);
        mnuPrecedente.setDisable(false);
        aggiorna();
    }

    @FXML
    private void vaiAlPrecedente(ActionEvent event) {
        capreRepository.moveToElementoPrecedente();
        if (!capreRepository.hasPrevious())
        {
            btnPrecedente.setDisable(true);
            mnuPrecedente.setDisable(true);
        }
        btnSuccessivo.setDisable(false);
        mnuSuccessivo.setDisable(false);
        aggiorna();        
    }

    @FXML
    private void edita(ActionEvent event) {
        if (stato_editing == STATO_EDITING.MODIFICA)
        {
            disabilitaPannelloCapra(false);
            btnEdit.setText("Salva");
            stato_editing = STATO_EDITING.SALVA;
        }
        else
        {
            salvaStatoCapra();
            disabilitaPannelloCapra(true);
            btnEdit.setDisable(false);
            btnEdit.setText("Modifica");
            mnuSalva.setDisable(false);
            mnuEsporta.setDisable(false);
            stato_editing = STATO_EDITING.MODIFICA;
        }
    }
}
