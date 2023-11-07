/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author dntn
 */
@Data
public class Genre {
    
    private Long id;
    private String genre;
    private Integer status;
    private LocalDateTime created;
    private LocalDateTime updated;

    public Genre() {
    }

    public Genre(Long id, String name) {
        this.id = id;
        this.genre = name;
    }
}
