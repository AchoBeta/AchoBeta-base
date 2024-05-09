package com.achobeta.www.service.controller.vo.view;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author jettcc in 2024/1/27
 * @version 1.0
 */
@Builder(toBuilder = true)
@Getter
public class QueryClassInfoView implements Serializable {
    private Long id;
    private String className;
    private Integer grade;
    private String collegeName;
    private Integer studentNum;
    private Integer college;
}