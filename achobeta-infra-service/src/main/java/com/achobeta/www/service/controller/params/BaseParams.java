package com.achobeta.www.service.controller.params;

import com.achobeta.www.common.util.Pagination;
import lombok.Getter;

/**
 * @author jett
 * @since 2024/1/30
 */
@Getter
public class BaseParams {
    Pagination pagination = Pagination.DEFAULT;
}
