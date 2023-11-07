/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;

import DAO.GenreDAO;
import Helpers.DateTimeHelper;
import Models.Genre;
import java.sql.SQLException;
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
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author dntn
 */
public class GenreController extends BaseController {

    private Combobox cbxSearchStatus;
    private Listbox lbxGenres;

    private Window wdwActionsGenres;
    private Textbox txtGenre;
    private Combobox cbxStatus;

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

    public void getChieldWindowComponents() {
        txtGenre = (Textbox) wdwActionsGenres.getFellow("txtGenre");
        cbxStatus = (Combobox) wdwActionsGenres.getFellow("cbxStatus");
        btnSave = (Button) wdwActionsGenres.getFellow("btnSave");
        btnCancel = (Button) wdwActionsGenres.getFellow("btnCancel");

        btnSave.removeEventListener(Events.ON_CLICK, onClick$btnSave);
        btnSave.addEventListener(Events.ON_CLICK, onClick$btnSave);

        btnCancel.removeEventListener(Events.ON_CLICK, onClick$btnCancel);
        btnCancel.addEventListener(Events.ON_CLICK, onClick$btnCancel);
    }

    public void onChanging$txtSearchGenre(InputEvent evt) {
        if (evt.getValue() != null && !evt.getValue().isBlank()) {
            params.put("genre", evt.getValue());
            loadTable();
            return;
        }
        if (params.containsKey("genre")) {
            params.remove("genre");
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
        wdwActionsGenres.setTitle("Save Genre");
        wdwActionsGenres.setVisible(true);
    }

    public void onClick$mnuEdit() {
        if (lbxGenres.getSelectedItem() != null) {
            clean();
            Genre genre = (Genre) lbxGenres.getSelectedItem().getValue();
            wdwActionsGenres.setTitle("Edit Genre");
            txtGenre.setValue(genre.getGenre());
            cbxStatus.setSelectedIndex(genre.getStatus() - 1);
            wdwActionsGenres.setVisible(true);
            return;
        }
        Messagebox.show("No se puede Editar un genero sin seleccionar");
    }

    public void onClick$mnuDel() {
        if (lbxGenres.getSelectedItem() != null) {
            Genre genre = (Genre) lbxGenres.getSelectedItem().getValue();
            Messagebox.show("¿Está seguro de eliminar el Genero: " + genre.getGenre() + "?", "Confirmacion",
                    Messagebox.YES + Messagebox.NO, Messagebox.QUESTION, event -> {
                        try {
                            int res = Integer.parseInt(String.valueOf(event.getData()));
                            if (res == Messagebox.YES) {
                                GenreDAO.getInstance().delete(getConnection(), genre.getId());
                                loadTable();
                                Messagebox.show("Genero: " + genre.getGenre() + " Eliminado con exito");
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
        if (wdwActionsGenres.getTitle().contains("Save")) {
            GenreDAO.getInstance().insert(getConnection(), txtGenre.getValue(), cbxStatus.getSelectedIndex() + 1);
            Messagebox.show("Se guardó el genero: " + txtGenre.getValue());
        } else {
            Genre genre = (Genre) lbxGenres.getSelectedItem().getValue();
            GenreDAO.getInstance().update(getConnection(), txtGenre.getValue(), cbxStatus.getSelectedIndex() + 1, genre.getId());
            Messagebox.show("Se guardó el genero: " + txtGenre.getValue());
        }
        loadTable();
        wdwActionsGenres.setVisible(false);
    };

    public EventListener<Event> onClick$btnCancel = (evt) -> {
        wdwActionsGenres.setVisible(false);
        clean();
    };

    public void clean() {
        txtGenre.setValue("");
        cbxStatus.setSelectedIndex(-1);
    }

    public void valid() {
        if (txtGenre.getValue() == null || txtGenre.getValue().isBlank()) {
            txtGenre.setErrorMessage("Debe llenar el campo");
        }
        if (cbxStatus.getSelectedIndex() < 0) {
            cbxStatus.setErrorMessage("Debe llenar el campo");
        }
    }

    private List<Genre> getListOfGenres() throws SQLException, NamingException {
        if (params.isEmpty()) {
            return GenreDAO.getInstance().getGenres(getConnection());
        }
        return GenreDAO.getInstance().getGenresWithParams(getConnection(), params);
    }

    public void loadTable() {
        //Llamar a la DB
        try {
            lbxGenres.getItems().clear();
            Stream<Genre> streamGenres = getListOfGenres().stream();
            streamGenres.forEach(genre -> {
                Listitem item = new Listitem();
                item.setValue(genre);
                item.setParent(lbxGenres);

                Listcell cell = new Listcell();
                cell.setLabel(genre.getGenre());
                cell.setParent(item);

                cell = new Listcell();
                cell.setLabel(genre.getStatus() == 1 ? "Alta" : "Baja");
                cell.setParent(item);

                cell = new Listcell();
                cell.setLabel(DateTimeHelper.getDateTimeFormat(genre.getCreated()));
                cell.setParent(item);

                cell = new Listcell();
                cell.setLabel(genre.getUpdated() != null ? DateTimeHelper.getDateTimeFormat(genre.getUpdated()) : "");
                cell.setParent(item);

            });
        } catch (SQLException ex) {
            Logger.getLogger(CrudController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(CrudController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
