/*
*  @(#)TasklistResponse.java
*
*  Copyright (c) J-Tech Solucoes em Informatica.
*  All Rights Reserved.
*
*  This software is the confidential and proprietary information of J-Tech.
*  ("Confidential Information"). You shall not disclose such Confidential
*  Information and shall use it only in accordance with the terms of the
*  license agreement you entered into with J-Tech.
*
*/
package br.com.jtech.tasklist.adapters.input.protocols;

import br.com.jtech.tasklist.application.core.domains.TaskList;
import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskListEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;

/**
* class TasklistResponse 
* 
* user angelo.vicente 
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TasklistResponse implements Serializable {
    private String id;
    List<TasklistResponse> responses;

    public static TasklistResponse of(TaskList tasklist) {
        return TasklistResponse.builder()
                .id(tasklist.getId())
                .build();
    }

    public static TasklistResponse of(List<TaskListEntity> entities) {
        var list = entities.stream().map(TasklistResponse::of).toList();
        return TasklistResponse.builder()
                .responses(list)
                .build();
    }

    public static TasklistResponse of(TaskListEntity entity) {
        var response = new TasklistResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }
}