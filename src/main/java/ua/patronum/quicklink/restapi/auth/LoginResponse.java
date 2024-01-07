package ua.patronum.quicklink.restapi.auth;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginResponse {

    public enum Error {
        OK,
        INVALID_USER_NAME,
        NAME_IS_EMPTY,
        INVALID_PASSWORD,
        INVALID_MAX_PASSWORD
    }

    private Error error;
    private String token;

    public static LoginResponse success(String authToken) {
        return builder().error(Error.OK).token(authToken).build();
    }

    public static LoginResponse failed(Error error) {
        return builder().error(error).build();
    }
}
