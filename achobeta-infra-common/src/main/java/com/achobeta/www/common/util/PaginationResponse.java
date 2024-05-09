package com.achobeta.www.common.util;

import lombok.Builder;
import lombok.extern.java.Log;

/**
 * <span>
 *     Pagination model as a return value
 * </span>
 * 
 * @author jettcc in 2024/1/27
 * @version 1.0
 */
@Builder
public record PaginationResponse(
        Long page,
        Long size,
        Long total
) {}
