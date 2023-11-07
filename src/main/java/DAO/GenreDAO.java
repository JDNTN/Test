/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Models.Genre;
import Models.QueryParam;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

/**
 *
 * @author dntn
 */
public class GenreDAO {

    private QueryRunner queryR;
    private static GenreDAO instance;

    private GenreDAO() {
        super();
        queryR = new QueryRunner();
    }

    public static GenreDAO getInstance() {
        if (instance == null) {
            instance = new GenreDAO();
        }
        return instance;
    }

    public List<Genre> getGenresToCbx(Connection cnx) {
        try {
            return queryR.query(cnx, "SELECT id, genre FROM GENRE WHERE status =1", new BeanListHandler<>(Genre.class));
        } catch (SQLException ex) {
            Logger.getLogger(GenreDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }

    public Genre getGenresById(Connection cnx, String id) {
        try {
            return queryR.query(cnx, "SELECT id, genre FROM GENRE WHERE id=" + id, new BeanHandler<>(Genre.class));
        } catch (SQLException ex) {
            Logger.getLogger(GenreDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new Genre(0L, "No Existe");
    }

    public List<Genre> getGenres(Connection cnx) {
        try {
            return queryR.query(cnx, "SELECT * FROM GENRE", new BeanListHandler<>(Genre.class));
        } catch (SQLException ex) {
            Logger.getLogger(GenreDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }

    public List<Genre> getGenresWithParams(Connection cnx, HashMap<String, String> params) {
        try {
            QueryParam queryParam = new QueryParam(params);
            return queryR.query(cnx, "SELECT * FROM GENRE WHERE " + queryParam.getQuery(),
                    new BeanListHandler<>(Genre.class), queryParam.getParams());
        } catch (SQLException ex) {
            Logger.getLogger(GenreDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }

    public void insert(Connection cnx, String genre, int estado) throws SQLException {
        queryR.insert(cnx, "INSERT INTO GENRE (genre, status) VALUES (?, ?)",
                new ScalarHandler<Long>(), genre,estado);
    }

    public void delete(Connection cnx, long idGenre) throws SQLException {
        queryR.update(cnx, "DELETE FROM GENRE WHERE id=?", idGenre);
    }

    public void update(Connection cnx, String genre, int estado, Long id) throws SQLException {
        queryR.update(cnx, "UPDATE GENRE SET genre=?, status=? WHERE id=?",genre, estado, id);
    }
}
