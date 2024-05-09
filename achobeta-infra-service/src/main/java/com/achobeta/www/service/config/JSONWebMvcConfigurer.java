package com.achobeta.www.service.config;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring6.http.codec.Fastjson2Decoder;
import com.alibaba.fastjson2.support.spring6.http.codec.Fastjson2Encoder;
import com.alibaba.fastjson2.support.spring6.http.converter.FastJsonHttpMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.AbstractJackson2Decoder;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.MimeType;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * <span>
 * use fastjson2 to replace the original serialization and deserialization tools
 * </span>
 *
 * @author jettcc in 2023/10/1
 * @version 1.0
 */
@Configuration
public class JSONWebMvcConfigurer implements WebFluxConfigurer {



    private static FastJsonHttpMessageConverter fastJsonHttpMessageConverter;

    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig config = new FastJsonConfig();
        config.setWriterFeatures(
                JSONWriter.Feature.WriteNullListAsEmpty,
                //json格式化
                JSONWriter.Feature.PrettyFormat,
                //输出map中value为null的数据
                JSONWriter.Feature.WriteMapNullValue,
                //输出boolean 为 false
                JSONWriter.Feature.WriteNullBooleanAsFalse,
                //输出list 为 []
                JSONWriter.Feature.WriteNullListAsEmpty,
                //输出number 为 0
                JSONWriter.Feature.WriteNullNumberAsZero,
                //输出字符串 为 ""
                JSONWriter.Feature.WriteNullStringAsEmpty,
                //对map进行排序
                JSONWriter.Feature.MapSortField
        );
        fastJsonHttpMessageConverter.setFastJsonConfig(config);
        HttpMessageConverter<?> converter = fastJsonHttpMessageConverter;
        return new HttpMessageConverters(converter);
    }

    private final ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        //自定义解码器
        configurer.defaultCodecs().jackson2JsonDecoder(new CustomJsonDecoder(objectMapper));
        //自定义编码器 编码器的道理和解码器基本类似，可以自行尝试
        //configurer.defaultCodecs().jackson2JsonEncoder(null);
    }


    static class CustomJsonDecoder extends AbstractJackson2Decoder {

        public CustomJsonDecoder(ObjectMapper mapper, MimeType... mimeTypes) {
            super(mapper, mimeTypes);
        }

        @Override
        public Object decode(DataBuffer dataBuffer, ResolvableType targetType, MimeType mimeType, Map<String, Object> hints) throws DecodingException {
            Object read = null;
            boolean flag = false;
            try {
                //如果是json格式 以及是否是自己的业务包
                if (mimeType.toString().contains(MediaType.APPLICATION_JSON_VALUE) &&
                        targetType.getType().getTypeName().startsWith("com.zhoule")) {
                    read = fastJsonHttpMessageConverter.read(targetType.getType(), null, new CustomHttpInputMessage(dataBuffer.asInputStream()));
                } else {
                    //不是想转换的数据直接返回父类调用
                    return super.decode(dataBuffer, targetType, mimeType, hints);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (flag) {
                    DataBufferUtils.release(dataBuffer);
                }
            }
            return read;
        }
    }

    //定义一个fastJson相关的HttpInputMessage
    static class CustomHttpInputMessage implements HttpInputMessage {
        private final InputStream inputStream;

        public CustomHttpInputMessage(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @NotNull
        @Override
        public InputStream getBody() throws IOException {
            return inputStream;
        }

        @NotNull
        @Override
        public HttpHeaders getHeaders() {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            return httpHeaders;
        }
    }

}
