package com.achobeta.www.service.tasks;

import com.achobeta.www.service.entity.ClassInfo;
import com.achobeta.www.service.mapper.ClassInfoMapper;
import com.achobeta.www.service.tasks.dto.SchoolInfoRespDTO;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @author jett
 * @since 2024/1/25
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ImportSchoolInfoScheduledTask {

    private static class ClassInfoService extends ServiceImpl<ClassInfoMapper, ClassInfo> {
        static final ClassInfoService INSTANCE = new ClassInfoService();
    }

    @Value("${ulearn.url}")
    private String U_LEARN_TOKEN_URL = "";
    @Value("${ulearn.info.url}")
    private String U_LEARN_INFO_URL = "";

    private final OkHttpClient okHttpClient;
    private static final Set<Integer> FILTER_COLLEGE_ID = Set.of(
            // 人力资源处、党委教师工作处、国际学院
            17681, 17677, 17778
    );


    @Scheduled(cron = "0 30 2 1 * ?")
//    @Scheduled(cron = "*/5 * * * * ?") // debug
    public void importSchoolClassData() {
        log.info("start import school class data.");
        if (U_LEARN_TOKEN_URL.isBlank()) throw new RuntimeException("U_LEARN_TOKEN_STRING is blank, plz check it.");
        var tokenReq = new Request.Builder().url(U_LEARN_TOKEN_URL).build();
        try (var tokenResp = okHttpClient.newCall(tokenReq).execute()) {
            var uLearnToken = Optional.ofNullable(tokenResp.body()).map(body -> {
                try {
                    return body.string();
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }).orElseThrow(() -> new RuntimeException("api get token is blank, plz check it."));

            var schoolClassInfoReq = new Request.Builder().url(U_LEARN_INFO_URL)
                    .addHeader(HttpHeaders.AUTHORIZATION, uLearnToken)
                    .build();
            try (var schoolClassInfoResp = okHttpClient.newCall(schoolClassInfoReq).execute()) {
                var schoolClassInfo = Optional.ofNullable(schoolClassInfoResp.body()).map(body -> {
                    try {
                        return body.string();
                    } catch (IOException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }).orElseThrow(() -> new RuntimeException("get school class info is blank, plz check it."));
                var classInfo = JSONObject.parseObject(schoolClassInfo, SchoolInfoRespDTO.class);
                List<ClassInfo> clasz = classInfo.getList().stream()
                        .filter(Objects::nonNull)
                        .filter(s -> !FILTER_COLLEGE_ID.contains(s.getCollege()))
                        .filter(s -> s.getClassName().matches("^\\d{4}.*$"))
                        .toList();
                ClassInfoService.INSTANCE.saveOrUpdateBatch(clasz);
            log.info("import school class data success, size: {}", clasz.size());
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
