package org.krechko.processService.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SongDTO {
    private String artist;
    private String title;
}
