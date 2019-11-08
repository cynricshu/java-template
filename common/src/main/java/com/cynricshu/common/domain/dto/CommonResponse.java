package com.cynricshu.common.domain.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse {

    private boolean success = true;
    private int code = 0;
    private Message message = new Message("success");
    @JsonAlias({"request_id"})
    private String requestId;

    public CommonResponse(boolean success, int code, String message, String requestId) {
        this.success = success;
        this.code = code;
        this.message = new Message(message);
        this.requestId = requestId;
    }

    public void setMessage(String message) {
        this.message = new Message(message);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        @Builder.Default
        private String global = "success";
    }
}
