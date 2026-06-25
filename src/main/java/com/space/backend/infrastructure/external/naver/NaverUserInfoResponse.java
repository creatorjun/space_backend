package com.space.backend.infrastructure.external.naver;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NaverUserInfoResponse {
    private String resultcode;
    private String message;
    private Response response;

    @Getter
    @NoArgsConstructor
    public static class Response {
        private String id;
        private String name;
        private String email;
        private String mobile;
        @JsonProperty("profile_image") private String profileImage;
    }
}
