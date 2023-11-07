/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 *
 * @author dntn
 */
@Data
public class Autor {
    
    private Long id;
    private String autor;
    private Date birthday;
    private String company;
    private Integer status;
    private LocalDateTime created;
    private LocalDateTime updated;

    public Autor(Long id, String name) {
        this.id = id;
        this.autor = name;
    }

    public Autor() {
    }
}
