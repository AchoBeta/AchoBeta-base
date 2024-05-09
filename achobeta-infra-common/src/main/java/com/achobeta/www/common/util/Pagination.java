package com.achobeta.www.common.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <span>
 *     Pagination model
 * </span>
 * 
 * @author jettcc in 2024/1/27
 * @version 1.0
 */

public record Pagination(Long page, Long size){

    public <T> Page<T> get() {
        return new Page<>(this.page, this.size);
    }

    public static final Pagination DEFAULT = new Pagination(1L, 10L);
}
