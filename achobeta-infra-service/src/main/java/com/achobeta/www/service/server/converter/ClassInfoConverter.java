package com.achobeta.www.service.server.converter;

import com.achobeta.www.service.controller.vo.view.QueryClassInfoView;
import com.achobeta.www.service.entity.ClassInfo;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author jettcc in 2024/1/27
 * @version 1.0
 */

@Mapper
public interface ClassInfoConverter {
    ClassInfoConverter INSTANCE = Mappers.getMapper(ClassInfoConverter.class);

    QueryClassInfoView entityToView(ClassInfo entity);
}
