// Copyright (C) 2023 Baidu Inc. All rights reserved.

import com.baidu.acg.feed.common.domain.dto.ResultResponse;
import com.baidu.acg.feed.exception.ThirdPartyException;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 2020/8/12 20:19
 *
 * @author Cynric Shu
 */
@Slf4j
public class ResultCallAdaptorFactory extends CallAdapter.Factory {
    @Nullable
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (getRawType(returnType) != ResultResponse.class) {
            return null;
        }

        if (!(returnType instanceof ParameterizedType)) {
            throw new IllegalStateException("ResultResponse return type must be parameterized"
                    + " as ResultResponse<Foo> or ResultResponse<? extends Foo>");
        }

        return new ResultBodyCallAdaptor<>(returnType);
    }

    private static final class ResultBodyCallAdaptor<R>
            implements CallAdapter<ResultResponse<R>, ResultResponse<R>> {

        private final Type responseType;

        ResultBodyCallAdaptor(Type responseType) {
            this.responseType = responseType;
        }

        @Override
        public Type responseType() {
            return responseType;
        }

        @Override
        public ResultResponse<R> adapt(Call<ResultResponse<R>> call) {

            Response<ResultResponse<R>> response;
            try {
                response = call.execute();
            } catch (IOException e) {
                // http 请求由于IO原因没有成功，比如建立连接失败，读写超时等等
                String errorMsg = "failed to call " + call.request().url().toString()
                        + " due to IOException: " + e.toString();
                throw new ThirdPartyException(errorMsg);
            }

            // 请求成功，并且返回了 200-300 之间的状态码
            if (response.isSuccessful()) {
                return response.body();
            } else {
                // 返回了诸如 400，500 的状态码
                String errMsg = "http status code is not 2xx";
                log.error("{}, response: {}, body: {}", errMsg, response.toString(), response.errorBody());
                throw new ThirdPartyException(errMsg);
            }
        }
    }
}