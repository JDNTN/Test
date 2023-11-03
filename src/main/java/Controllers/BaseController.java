/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;

/**
 *
 * @author dntn
 */
public class BaseController extends GenericForwardComposer<Component> {

    @Override
    public void doAfterCompose(Component t) {
        try {
            super.doAfterCompose(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException, NamingException {
        Context contexto = new InitialContext();
        DataSource dts = (DataSource) contexto.lookup("java:/comp/env/jdbc/Test");
        return dts.getConnection();
    }
}
