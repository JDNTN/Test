/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Models.Autor;
import Models.QueryParam;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
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
public class AutorDAO {

    private QueryRunner queryR;
    private static AutorDAO instance;

    private AutorDAO() {
        super();
        queryR = new QueryRunner();
    }

    public static AutorDAO getInstance() {
        if (instance == null) {
            instance = new AutorDAO();
        }
        return instance;
    }

    public List<Autor> getAutorToCbx(Connection cnx) {
        try {
            return queryR.query(cnx, "SELECT id, autor FROM AUTOR WHERE status = 1", new BeanListHandler<>(Autor.class));
        } catch (SQLException ex) {
            Logger.getLogger(AutorDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }

    public Autor getAutorById(Connection cnx, String id) {
        try {
            return queryR.query(cnx, "SELECT id, autor FROM AUTOR WHERE id=" + id, new BeanHandler<>(Autor.class));
        } catch (SQLException ex) {
            Logger.getLogger(AutorDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new Autor(0L, "No Existe");
    }

    public List<Autor> getAutors(Connection cnx) {
        try {
            return queryR.query(cnx, "SELECT * FROM AUTOR", new BeanListHandler<>(Autor.class));
        } catch (SQLException ex) {
            Logger.getLogger(AutorDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }

    public List<Autor> getAutorsWithParams(Connection cnx, HashMap<String, String> params) {
        try {
            QueryParam queryParam = new QueryParam(params);
            return queryR.query(cnx, "SELECT * FROM AUTOR WHERE " + queryParam.getQuery(),
                    new BeanListHandler<>(Autor.class), queryParam.getParams());
        } catch (SQLException ex) {
            Logger.getLogger(AutorDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }

    public void insert(Connection cnx, String autor, Integer status, Date birthday, String company) throws SQLException {
        queryR.insert(cnx, "INSERT INTO AUTOR (autor, status, birthday, company) VALUES (?, ?, ?, ?)",
                new ScalarHandler<Long>(), autor, status, birthday, company);
    }

    public void delete(Connection cnx, Long idAutor) throws SQLException {
        queryR.update(cnx, "DELETE FROM AUTOR WHERE id = ?", idAutor);
    }

    public void update(Connection cnx, String autor, Integer estado, Long id, Date birthday, String company) throws SQLException {
        queryR.update(cnx, "UPDATE AUTOR SET autor= ?, status= ?, birthday= ?, company= ? WHERE id=?",autor, estado, birthday, company, id);
    }
}
