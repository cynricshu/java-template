// Copyright (C) 2023 Baidu Inc. All rights reserved.

import com.baidu.acg.feed.common.domain.dto.ResultResponse;
import com.baidu.acg.feed.exception.ThirdPartyException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 2020/10/10 17:05
 *
 * @author Cynric Shu
 */
public class ResultConverterFactory extends Converter.Factory {

    private final ObjectMapper mapper;

    private ResultConverterFactory(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public static ResultConverterFactory create(ObjectMapper mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper == null");
        }
        return new ResultConverterFactory(mapper);
    }

    @Override
    public Converter<ResponseBody, ResultResponse<?>> responseBodyConverter(
            Type type, Annotation[] annotations, Retrofit retrofit) {

        // here we only handle the parameterizedType of ResultResponse<T>
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();

            if (!(rawType instanceof Class)) {
                throw new IllegalArgumentException();
            }

            if (rawType == ResultResponse.class) {
                JavaType javaType = mapper.getTypeFactory().constructType(type);
                ObjectReader reader = mapper.readerFor(javaType);
                return new JacksonResponseBodyConverter<>(reader, type);
            }
        }

        return null;
    }

    /**
     * Almost copied from {@link retrofit2.converter.jackson.JacksonResponseBodyConverter}
     */
    class JacksonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private final ObjectReader objectReader;
        private final Type type;

        JacksonResponseBodyConverter(ObjectReader objectReader, Type type) {
            this.objectReader = objectReader;
            this.type = type;
        }

        @Override
        public T convert(ResponseBody value) throws ThirdPartyException {
            String valueString;
            try {
                valueString = value.string();
            } catch (IOException e) {
                throw new ThirdPartyException("fail to parse response body to string, type=" + type);
            }

            try {
                return objectReader.readValue(valueString);
            } catch (IOException e) {
                // 如果反序列化失败，说明下游服务返回的结果并不是我们期望的格式，此时应抛出明确的错误信息
                throw new ThirdPartyException("unexpected response body = " + valueString +
                        " which cannot be deserialized to type " + type.getTypeName());
            }
        }
    }
}