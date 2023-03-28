// Copyright (C) 2023 Baidu Inc. All rights reserved.

import java.util.List;

import com.baidu.acg.feed.common.domain.dto.ResultResponse;
import com.baidu.acg.feed.product.client.bo.ProductDailyStatistic;
import com.baidu.acg.feed.product.client.bo.ProductRecReq;
import com.baidu.acg.feed.product.client.bo.ProductRecResult;
import com.baidu.acg.feed.product.client.bo.ProductStatReq;

import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 2020/8/3 22:30
 *
 * @author Cynric Shu
 */
public interface ProductClient {
    String urlPrefix = "/api/v1/feed/product/toc";

    @POST(urlPrefix + "/recommend")
    ResultResponse<List<ProductRecResult>> productRec(@Body ProductRecReq productRecReq);

    @POST(urlPrefix + "/statistic")
    ResultResponse<List<ProductDailyStatistic>> productStatistic(@Body ProductStatReq productStatReq);
}