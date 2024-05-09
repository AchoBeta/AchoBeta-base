package com.achobeta.www.service.controller;


import com.achobeta.www.common.util.SystemJsonResponse;
import com.achobeta.www.service.controller.params.QueryClassInfoParams;
import com.achobeta.www.service.server.SchoolInfoServer;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/school-info")
@RequiredArgsConstructor
public class SchoolInfoController {
    private final SchoolInfoServer server;

    @PostMapping("/class/query")
    public Mono<SystemJsonResponse> queryClass(@RequestBody @Validated QueryClassInfoParams params) {
        var resp = server.queryClassInfoByParams(params);
        return Mono.just(SystemJsonResponse.SYSTEM_SUCCESS(resp));
    }

    @GetMapping("/college/query/name")
    public Mono<SystemJsonResponse> queryAllCollegeName(@RequestParam("grade") Integer grade) {
        var resp = server.queryAllCollegeName(grade);
        return Mono.just(SystemJsonResponse.SYSTEM_SUCCESS(resp));
    }
}
