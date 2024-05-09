package com.achobeta.www.service.controller.vo;

import com.achobeta.www.common.util.PaginationResponse;
import com.achobeta.www.service.controller.vo.view.QueryClassInfoView;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author jettcc in 2024/1/27
 * @version 1.0
 */
@Builder(toBuilder = true)
@Getter
@Setter
public class QueryClassInfoVO implements Serializable {

    List<QueryClassInfoView> views;

    PaginationResponse pagination;
}
