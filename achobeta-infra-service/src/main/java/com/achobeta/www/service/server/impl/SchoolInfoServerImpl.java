package com.achobeta.www.service.server.impl;

import com.achobeta.www.common.util.PaginationResponse;
import com.achobeta.www.service.controller.params.QueryClassInfoParams;
import com.achobeta.www.service.controller.vo.QueryClassInfoVO;
import com.achobeta.www.service.entity.ClassInfo;
import com.achobeta.www.service.mapper.ClassInfoMapper;
import com.achobeta.www.service.server.SchoolInfoServer;
import com.achobeta.www.service.server.converter.ClassInfoConverter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <span>
 * school info server implement
 * </span>
 *
 * @author jettcc in 2024/1/27
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SchoolInfoServerImpl implements SchoolInfoServer {
    private final ClassInfoMapper classInfoMapper;

    @Override
    public QueryClassInfoVO queryClassInfoByParams(QueryClassInfoParams params) {
        Page<ClassInfo> pagination =
                params.getPagination().get();
        var qw = new QueryWrapper<ClassInfo>();
        qw.lambda()
                .eq(Optional.ofNullable(params.getId()).isPresent(), ClassInfo::getId, params.getId())
                .eq(Optional.ofNullable(params.getGrade()).isPresent(), ClassInfo::getGrade, params.getGrade())
                .eq(Optional.ofNullable(params.getCollegeId()).isPresent(), ClassInfo::getCollege, params.getCollegeId())
                .like(Optional.ofNullable(params.getClassName()).isPresent(), ClassInfo::getClassName, params.getClassName())
                .like(Optional.ofNullable(params.getCollegeName()).isPresent(), ClassInfo::getCollegeName, params.getCollegeName());
        classInfoMapper.selectPage(pagination, qw);
        return QueryClassInfoVO.builder()
                .views(pagination.getRecords().stream().map(
                        ClassInfoConverter.INSTANCE::entityToView
                ).toList())
                .pagination(
                        PaginationResponse.builder()
                                .total(pagination.getTotal())
                                .page(pagination.getCurrent())
                                .size(pagination.getSize())
                                .build()
                )
                .build();
    }

    @Override
    public Map<Integer, String> queryAllCollegeName(Integer grade) {
        QueryWrapper<ClassInfo> qw = new QueryWrapper<>();
        qw.select("distinct c_college as college, c_college_name as collegeName")
                .orderByAsc("c_college")
                .lambda()
                .eq(ClassInfo::getGrade, grade);
        List<ClassInfo> classInfos = classInfoMapper.selectList(qw);
        return classInfos.stream().collect(Collectors.toMap(ClassInfo::getCollege, ClassInfo::getCollegeName));
    }
}
