package com.stylefeng.guns.api.film.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FilmActorsVO implements Serializable {
    private ActorVO director;
    private List<ActorVO> actors;
}
