package com.samifying.link.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GuildModel {
    private Long id;
    private String name;
    private Integer memberCount;
}
