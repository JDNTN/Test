/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;

import DAO.AutorDAO;
import Helpers.DateTimeHelper;
import DAO.BookDAO;
import DAO.GenreDAO;
import Models.Autor;
import Models.Book;
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
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 *
 * @author dntn
 */
public class CrudController extends BaseController {

    // Principal Window
    private Combobox cbxSearchGenre;
    private Combobox cbxSearchStatus;
    private Listbox lbxBooks;
    private Paging pgLbxBooks;
    private Window wdwBook;

    //Chield Window
    private Textbox txtBook;
    private Combobox txtAutor;
    private Textbox txtISBN;
    private Textbox txtNoPages;
    private Combobox cbxGenre;
    private Combobox cbxStatus;
    private Button btnSave;
    private Button btnCancel;

    //Params
    private HashMap<String, String> params = new HashMap<>();

    //Methods
    @Override
    public void doAfterCompose(Component t) {
        try {
            super.doAfterCompose(t);
            cbxSearchStatus.setSelectedIndex(0);
            loadGenres(cbxSearchGenre);
            cbxSearchGenre.setSelectedIndex(0);
            getChieldWindowComponents();
            loadTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onOK$wdwBook() {
        Events.echoEvent(Events.ON_CLICK, btnSave, null);
    }

    public void getChieldWindowComponents() {
        txtBook = (Textbox) wdwBook.getFellow("txtBook");
        txtAutor = (Combobox) wdwBook.getFellow("txtAutor");
        txtISBN = (Textbox) wdwBook.getFellow("txtISBN");
        txtNoPages = (Textbox) wdwBook.getFellow("txtNoPages");
        cbxGenre = (Combobox) wdwBook.getFellow("cbxGenre");
        cbxStatus = (Combobox) wdwBook.getFellow("cbxStatus");
        btnSave = (Button) wdwBook.getFellow("btnSave");
        btnCancel = (Button) wdwBook.getFellow("btnCancel");

        btnSave.removeEventListener(Events.ON_CLICK, onClick$btnSave);
        btnSave.addEventListener(Events.ON_CLICK, onClick$btnSave);

        btnCancel.removeEventListener(Events.ON_CLICK, onClick$btnCancel);
        btnCancel.addEventListener(Events.ON_CLICK, onClick$btnCancel);
    }

    public void onClick$mnuAdd() {
        clean();
        loadGenres(cbxGenre);
        loadAutors();
        wdwBook.setTitle("Save Book");
        wdwBook.setVisible(true);
    }

    public void onClick$mnuDel() {
        if (lbxBooks.getSelectedItem() != null) {
            Book book = (Book) lbxBooks.getSelectedItem().getValue();
            Messagebox.show("¿Está seguro de eliminar el Genero: " + book.getBook() + "?", "Confirmacion",
                    Messagebox.YES + Messagebox.NO, Messagebox.QUESTION, event -> {
                        try {
                            int res = Integer.parseInt(String.valueOf(event.getData()));
                            if (res == Messagebox.YES) {
                                BookDAO.getInstance().delete(getConnection(), book.getId());
                                Messagebox.show("Libro: " + book.getBook() + " Eliminado con exito");
                                loadTable();
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(CrudController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (NamingException ex) {
                            Logger.getLogger(CrudController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        return;
                    });
            Messagebox.show("No se puede eliminar un libro sin seleccionar");
        }
    }

    public void onClick$mnuEdit() {
        if (lbxBooks.getSelectedItem() != null) {
            clean();
            loadGenres(cbxGenre);
            loadAutors();
            Book book = (Book) lbxBooks.getSelectedItem().getValue();
            wdwBook.setTitle("Edit Book");
            //txtBook.setValue(book.getBook());
            txtAutor.setValue(book.getAutor().toString());
            txtISBN.setValue(book.getISBN().toString());
            cbxGenre.getItems().stream()
                    .filter(comboitem -> ((Genre) comboitem.getValue()) != null)
                    .filter(comboitem -> ((Genre) comboitem.getValue()).getId() == book.getGenre())
                    .findFirst()
                    .ifPresent(cbxGenre::setSelectedItem);
            cbxStatus.setSelectedIndex(book.getStatus() - 1);
            txtNoPages.setValue(book.getNo_pages().toString());
            wdwBook.setVisible(true);
            return;
        }
        Messagebox.show("No se puede Editar un libro sin seleccionar uno");
    }

    public void onChange$cbxSearchStatus() {
        if (cbxSearchStatus.getSelectedIndex() > 0) {
            params.put("b.status", cbxSearchStatus.getSelectedItem().getValue());
            loadTable();
            return;
        }
        if (params.containsKey("b.status")) {
            params.remove("b.status");
            loadTable();
        }
    }

    public void onChange$cbxSearchGenre() {
        if (cbxSearchGenre.getSelectedIndex() > 0) {
            Genre genre = cbxSearchGenre.getSelectedItem().getValue();
            params.put("b.genre", genre.getId().toString());
            loadTable();
            return;
        }
        if (params.containsKey("b.genre")) {
            params.remove("b.genre");
            loadTable();
        }
    }

    public void onChanging$txtSeachName(InputEvent evt) {
        if (evt.getValue() != null && !evt.getValue().isBlank()) {
            params.put("b.book", evt.getValue());
            loadTable();
            return;
        }
        if (params.containsKey("b.book")) {
            params.remove("b.book");
            loadTable();
        }
    }

    public EventListener<Event> onClick$btnSave = (evt) -> {
        valid();
        if (wdwBook.getTitle().contains("Save")) {
            Genre gen = (Genre) cbxGenre.getSelectedItem().getValue();
            Autor aut = (Autor) txtAutor.getSelectedItem().getValue();
            BookDAO.getInstance().insert(getConnection(), txtBook.getValue(), aut.getId(), Integer.parseInt(txtISBN.getValue()), Integer.parseInt(txtNoPages.getValue()),
                    gen.getId(), cbxStatus.getSelectedIndex());
            Messagebox.show("Se guardó el libró: " + txtBook.getValue());
        } else {
            Genre gen = (Genre) cbxGenre.getSelectedItem().getValue();
            Book book = (Book) lbxBooks.getSelectedItem().getValue();
            BookDAO.getInstance().update(getConnection(), txtBook.getValue(), txtAutor.getValue(), Integer.parseInt(txtISBN.getValue()), Integer.parseInt(txtNoPages.getValue()),
                    gen.getId(), cbxStatus.getSelectedIndex() + 1, book.getId());
            Messagebox.show("Se editó el libró: " + txtBook.getValue());
        }
        loadTable();
        wdwBook.setVisible(false);
    };

    public EventListener<Event> onClick$btnCancel = (evt) -> {
        wdwBook.setVisible(false);
        clean();
    };

    private List<Book> getListOfBooks() throws SQLException, NamingException {
        if (params.isEmpty()) {
            return BookDAO.getInstance().getBooks(getConnection());
        }
        return BookDAO.getInstance().getBooksWithParams(getConnection(), params);
    }

    public void loadTable() {
        //Llamar a la DB
        try {
            lbxBooks.getItems().clear();
            Stream<Book> streamBooks = getListOfBooks().stream();
            streamBooks.forEach(book -> {
                Listitem item = new Listitem();
                item.setValue(book);
                item.setParent(lbxBooks);

                Listcell cell = new Listcell();
                cell.setLabel(book.getBook());
                cell.setParent(item);

                try {
                    cell = new Listcell();
                    cell.setLabel(AutorDAO.getInstance().getAutorById(getConnection(),
                            book.getAutor().toString()).getAutor());
                    cell.setParent(item);
                } catch (SQLException ex) {
                    Logger.getLogger(CrudController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NamingException ex) {
                    Logger.getLogger(CrudController.class.getName()).log(Level.SEVERE, null, ex);
                }

                try {
                    cell = new Listcell();
                    cell.setLabel(GenreDAO.getInstance().getGenresById(getConnection(),
                            book.getGenre().toString()).getGenre());
                    cell.setParent(item);
                } catch (SQLException ex) {
                    Logger.getLogger(CrudController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NamingException ex) {
                    Logger.getLogger(CrudController.class.getName()).log(Level.SEVERE, null, ex);
                }

                cell = new Listcell();
                cell.setLabel(book.getISBN().toString());
                cell.setParent(item);

                cell = new Listcell();
                cell.setLabel(book.getNo_pages().toString());
                cell.setParent(item);

                cell = new Listcell();
                cell.setLabel(book.getStatus() == 1 ? "Disponible" : "No Disponible");
                cell.setParent(item);

                cell = new Listcell();
                cell.setLabel(DateTimeHelper.getDateTimeFormat(book.getCreated()));
                cell.setParent(item);

                cell = new Listcell();
                cell.setLabel(book.getUpdated() != null ? DateTimeHelper.getDateTimeFormat(book.getUpdated()) : "");
                cell.setParent(item);

            });
        } catch (SQLException ex) {
            Logger.getLogger(CrudController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(CrudController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void valid() {
        if (txtBook.getValue() == null || txtBook.getValue().isBlank()) {
            txtBook.setErrorMessage("Debe especificar el nombre del libro");
        }
        if (txtAutor.getSelectedIndex() < 0) {
            txtAutor.setErrorMessage("Debe seleccionar un autor");
        }
        if (txtISBN.getValue() == null || txtISBN.getValue().isBlank()) {
            txtISBN.setErrorMessage("Debe especificar el isbn");
        }
        if (txtNoPages.getValue() == null || txtNoPages.getValue().isBlank()) {
            txtNoPages.setErrorMessage("Debe especificar el numero de paginas");
        }
        try {
            Integer.parseInt(txtISBN.getValue());
        } catch (Exception e) {
            txtISBN.setErrorMessage("El isbn es unicamente numerico");
        }
        try {
            Integer.parseInt(txtNoPages.getValue());
        } catch (Exception e) {
            txtNoPages.setErrorMessage("El numero de paginas es unicamente numerico");
        }
        if (cbxGenre.getSelectedIndex() < 0) {
            cbxGenre.setErrorMessage("Debe seleccionar un genero");
        }
        if (cbxStatus.getSelectedIndex() < 0) {
            cbxStatus.setErrorMessage("Debe seleccionar un genero");
        }
    }

    public void clean() {
        cbxGenre.setSelectedIndex(-1);
        txtBook.setValue("");
        txtAutor.setSelectedIndex(-1);
        txtISBN.setValue("");
        txtNoPages.setValue("");
        cbxGenre.setSelectedIndex(-1);
        cbxStatus.setSelectedIndex(-1);
        cbxGenre.setSelectedIndex(-1);
    }

    private void loadAutors() {
        txtAutor.getItems().clear();
        List<Autor> autorsDB;
        try {
            autorsDB = AutorDAO.getInstance().getAutorToCbx(getConnection());
            autorsDB.stream().forEach(autor -> {
                Comboitem item = new Comboitem();
                item.setLabel(autor.getAutor());
                item.setValue(autor);
                item.setParent(txtAutor);
            });
        } catch (SQLException ex) {
            Logger.getLogger(CrudController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(CrudController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadGenres(Combobox cbx) {
        cbx.getItems().clear();
        if ("cbxSearchGenre".equals(cbx.getId())) {
            Comboitem item = new Comboitem();
            item.setLabel("Todos");
            item.setParent(cbx);
        }
        List<Genre> genresDB;
        try {
            genresDB = GenreDAO.getInstance().getGenresToCbx(getConnection());
            genresDB.stream().forEach(genre -> {
                Comboitem item = new Comboitem();
                item.setLabel(genre.getGenre());
                item.setValue(genre);
                item.setParent(cbx);
            });
        } catch (SQLException ex) {
            Logger.getLogger(CrudController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(CrudController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
