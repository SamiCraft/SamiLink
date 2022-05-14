package com.samifying.link.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InfoModel {

    private Long id;
    private String name;
    private Integer serverCount;
    private Long totalAccountLinks;
}
