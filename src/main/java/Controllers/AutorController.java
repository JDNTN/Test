/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;

import DAO.AutorDAO;
import Helpers.DateTimeHelper;
import Models.Autor;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.naming.NamingException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author danton
 */
public class AutorController extends BaseController {

    private Combobox cbxSearchStatus;
    private Listbox lbxAutor;

    private Window wdwActionsAutor;
    private Textbox txtAutor;
    private Datebox dbxBirthday;
    private Combobox cbxStatus;
    private Textbox txtCompany;

    private Button btnSave;
    private Button btnCancel;

    private HashMap<String, String> params = new HashMap<>();

    @Override
    public void doAfterCompose(Component t) {
        try {
            super.doAfterCompose(t);
            cbxSearchStatus.setSelectedIndex(0);
            getChieldWindowComponents();
            loadTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getChieldWindowComponents() {
        txtAutor = (Textbox) wdwActionsAutor.getFellow("txtAutor");
        dbxBirthday = (Datebox) wdwActionsAutor.getFellow("dbxBirthday");
        txtCompany = (Textbox) wdwActionsAutor.getFellow("txtCompany");
        cbxStatus = (Combobox) wdwActionsAutor.getFellow("cbxStatus");

        btnSave = (Button) wdwActionsAutor.getFellow("btnSave");
        btnCancel = (Button) wdwActionsAutor.getFellow("btnCancel");

        btnSave.removeEventListener(Events.ON_CLICK, onClick$btnSave);
        btnSave.addEventListener(Events.ON_CLICK, onClick$btnSave);

        btnCancel.removeEventListener(Events.ON_CLICK, onClick$btnCancel);
        btnCancel.addEventListener(Events.ON_CLICK, onClick$btnCancel);
    }

    public void onChanging$txtSearchAutor(InputEvent evt) {
        if (evt.getValue() != null && !evt.getValue().isBlank()) {
            params.put("autor", evt.getValue());
            loadTable();
            return;
        }
        if (params.containsKey("autor")) {
            params.remove("autor");
            loadTable();
        }
    }

    public void onChange$cbxSearchStatus() {
        if (cbxSearchStatus.getSelectedIndex() > 0) {
            params.put("status", cbxSearchStatus.getSelectedItem().getValue());
            loadTable();
            return;
        }
        if (params.containsKey("status")) {
            params.remove("status");
            loadTable();
        }
    }

    public void onClick$mnuAdd() {
        clean();
        wdwActionsAutor.setTitle("Save Autors");
        wdwActionsAutor.setVisible(true);
    }

    public void onClick$mnuEdit() {
        if (lbxAutor.getSelectedItem() != null) {
            clean();
            Autor autor = (Autor) lbxAutor.getSelectedItem().getValue();
            wdwActionsAutor.setTitle("Edit Genre");
            txtAutor.setValue(autor.getAutor());
            txtCompany.setValue(autor.getCompany());
            dbxBirthday.setValue(autor.getBirthday());
            cbxStatus.setSelectedIndex(autor.getStatus() - 1);
            wdwActionsAutor.setVisible(true);
            return;
        }
        Messagebox.show("No se puede Editar un genero sin seleccionar");
    }

    public void onClick$mnuDel() {
        if (lbxAutor.getSelectedItem() != null) {
            Autor autor = (Autor) lbxAutor.getSelectedItem().getValue();
            Messagebox.show("¿Está seguro de eliminar el Genero: " + autor.getAutor() + "?", "Confirmacion",
                    Messagebox.YES + Messagebox.NO, Messagebox.QUESTION, event -> {
                        try {
                            int res = Integer.parseInt(String.valueOf(event.getData()));
                            if (res == Messagebox.YES) {
                                AutorDAO.getInstance().delete(getConnection(), autor.getId());
                                loadTable();
                                Messagebox.show("Genero: " + autor.getAutor() + " Eliminado con exito");
                                return;
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(GenreController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (NamingException ex) {
                            Logger.getLogger(GenreController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        return;
                    });
            return;
        }
        Messagebox.show("No se puede eliminar un genero sin seleccionar");
    }

    public EventListener<Event> onClick$btnSave = (evt) -> {
        valid();
        if (wdwActionsAutor.getTitle().contains("Save")) {
            AutorDAO.getInstance().insert(getConnection(), txtAutor.getValue(), cbxStatus.getSelectedIndex() + 1, dbxBirthday.getValue(), txtCompany.getValue());
            Messagebox.show("Se guardó el genero: " + txtAutor.getValue());
        } else {
            Autor genre = (Autor) lbxAutor.getSelectedItem().getValue();
            AutorDAO.getInstance().update(getConnection(), txtAutor.getValue(), cbxStatus.getSelectedIndex() + 1, genre.getId(), dbxBirthday.getValue(), txtCompany.getValue());
            Messagebox.show("Se guardó el genero: " + txtAutor.getValue());
        }
        loadTable();
        wdwActionsAutor.setVisible(false);
    };

    public EventListener<Event> onClick$btnCancel = (evt) -> {
        wdwActionsAutor.setVisible(false);
        clean();
    };

    public void clean() {
        txtAutor.setValue("");
        dbxBirthday.setValue(new Date());
        txtCompany.setValue("");
        cbxStatus.setSelectedIndex(-1);
    }

    public void valid() {
        if (txtAutor.getValue() == null || txtAutor.getValue().isBlank()) {
            txtAutor.setErrorMessage("Debe llenar el campo");
        }
        if (txtCompany.getValue() == null || txtCompany.getValue().isBlank()) {
            txtCompany.setErrorMessage("Debe llenar el campo");
        }
        if (cbxStatus.getSelectedIndex() < 0) {
            cbxStatus.setErrorMessage("Debe llenar el campo");
        }
    }

    private List<Autor> getListOfAutors() throws SQLException, NamingException {
        if (params.isEmpty()) {
            return AutorDAO.getInstance().getAutors(getConnection());
        }
        return AutorDAO.getInstance().getAutorsWithParams(getConnection(), params);
    }

    public void loadTable() {
        //Llamar a la DB
        try {
            lbxAutor.getItems().clear();
            Stream<Autor> streamGenres = getListOfAutors().stream();
            streamGenres.forEach(autor -> {
                Listitem item = new Listitem();
                item.setValue(autor);
                item.setParent(lbxAutor);

                Listcell cell = new Listcell();
                cell.setLabel(autor.getAutor());
                cell.setParent(item);

                /*FECHA*/
                cell = new Listcell();
                cell.setLabel(DateTimeHelper.getDateFormat(autor.getBirthday()));
                cell.setParent(item);

                cell = new Listcell();
                cell.setLabel(autor.getCompany());
                cell.setParent(item);

                cell = new Listcell();
                cell.setLabel(autor.getStatus() == 1 ? "Alta" : "Baja");
                cell.setParent(item);

                cell = new Listcell();
                cell.setLabel(DateTimeHelper.getDateTimeFormat(autor.getCreated()));
                cell.setParent(item);

                cell = new Listcell();
                cell.setLabel(autor.getUpdated() != null ? DateTimeHelper.getDateTimeFormat(autor.getUpdated()) : "");
                cell.setParent(item);

            });
        } catch (SQLException ex) {
            Logger.getLogger(CrudController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(CrudController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
