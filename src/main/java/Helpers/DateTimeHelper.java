package Helpers;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author dntn
 */
public class DateTimeHelper {

    private static DateTimeFormatter formatterDT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static SimpleDateFormat formatterD = new SimpleDateFormat("dd/MM/yyyy");

    public static String getDateTimeFormat(LocalDateTime date) {
        return date.format(formatterDT);
    }

    public static String getDateFormat(Date date) {
        return formatterD.format(date);
    }

    public static Date toDate(String date) throws ParseException {
        return formatterD.parse(date);
    }
}
