package uk.gov.hmcts.reform.lrdapi.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class TestConfigProperties {


    @Value("${oauth2.client.secret}")
    public String clientSecret;

    @Value("${idam.api.url}")
    public String idamApiUrl;

    @Value("${idam.auth.redirectUrl}")
    public String oauthRedirectUrl;

    @Value("${idam.auth.clientId:rd-location-ref-api}")
    public String clientId;

}
