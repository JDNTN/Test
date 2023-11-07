/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Models.Book;
import Models.QueryParam;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

/**
 *
 * @author dntn
 */
public class BookDAO {

    private QueryRunner queryR;
    private static BookDAO bookDao;

    private BookDAO() {
        super();
        queryR = new QueryRunner();
    }

    public static BookDAO getInstance() {
        if (bookDao == null) {
            bookDao = new BookDAO();
        }
        return bookDao;
    }

    public List<Book> getBooks(Connection cnx) {
        try {
            return queryR.query(cnx, "SELECT b.id, b.book, b.autor, b.ISBN, b.no_pages, b.genre, b.status, b.created, b.updated "
                    + "FROM BOOK b LEFT JOIN GENRE g ON g.id = b.genre "
                    + "LEFT JOIN AUTOR a ON a.id = b.autor "
                    + "WHERE g.status = ? AND a.status = 1", new BeanListHandler<>(Book.class), 1);
        } catch (SQLException ex) {
            Logger.getLogger(GenreDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }

    public List<Book> getBooksWithParams(Connection cnx, HashMap<String, String> params) {
        try {
            QueryParam queryParams = new QueryParam(params);
            return queryR.query(cnx, "SELECT b.id, b.book, b.autor, b.ISBN, b.no_pages, b.genre, b.status, b.created, b.updated FROM BOOK b "
                    + "LEFT JOIN GENRE g ON g.id = b.genre "
                    + "LEFT JOIN AUTOR a ON a.id = b.autor "
                    + "WHERE " + queryParams.getQuery() + " AND g.status = 1 AND a.status = 1",
                    new BeanListHandler<>(Book.class), queryParams.getParams());
        } catch (SQLException ex) {
            Logger.getLogger(GenreDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }

    public void insert(Connection cnx, String libro, Long autor, int isbn, int paginas, long id_genero, int estado) throws SQLException {
        queryR.insert(cnx, "INSERT INTO BOOK (book, ISBN, autor, no_pages, status, genre) VALUES (?, ?, ?, ?, ?, ?)",
                new ScalarHandler<Long>(), libro, isbn, autor, paginas, estado, id_genero);
    }

    public void delete(Connection cnx, long id_libro) throws SQLException {
        queryR.update(cnx, "DELETE FROM BOOK WHERE id=?", id_libro);
    }

    public void update(Connection cnx, String libro, String autor, int isbn, int paginas, long id_genero, int estado, long id_libro) throws SQLException {
        queryR.update(cnx, "UPDATE BOOK SET genre=?, book=?, isbn=?, autor=?, no_pages=?, status=? WHERE id=?",
                id_genero, libro, isbn, autor, paginas, estado, id_libro);
    }
}
