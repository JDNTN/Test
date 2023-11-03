/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

import java.util.Date;
import lombok.Data;

/**
 *
 * @author dntn
 */
@Data
public class Autor {
    
    private Long id;
    private String name;
    private String country;
    private Date birthday;
    private String company;
}
