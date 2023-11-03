/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

import java.util.HashMap;
import java.util.Iterator;
import lombok.Data;

/**
 *
 * @author dntn
 */
@Data
public class QueryParam {

    private String query;
    private Object[] params;

    public QueryParam() {
    }

    public QueryParam(HashMap<String, String> paramsIn) {
        createParams(paramsIn);
    }

    public void createParams(HashMap<String, String> params) {
        Iterator<String> key = params.keySet().iterator();
        String strParams = "";
        if (key != null && key.hasNext()) {
            String strKey = key.next();
            strParams = compareIfParamIsText(strKey);
            while (key.hasNext()) {
                strKey = key.next();
                strParams = strParams + " AND " + compareIfParamIsText(strKey);
            }
        }
        this.query = strParams;
        setParamsValues(params);
    }

    private String compareIfParamIsText(String key) {
        if (key.equals("b.book") || key.equals("genre")) {
            return key + " LIKE CONCAT('%',?,'%')";
        }
        return key + "=?";
    }

    public void setParamsValues(HashMap<String, String> params) {
        this.params = params.values().toArray(new Object[params.size()]);
    }
}
