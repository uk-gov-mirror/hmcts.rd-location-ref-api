package uk.gov.hmcts.reform.lrdapi.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static uk.gov.hmcts.reform.lrdapi.LrdAuthorizationEnabledIntegrationTest.LRD_SERVICE_NAME;

public class S2sWireMockStubs {

    private S2sWireMockStubs() {
    }

    public static void registerDefaults(WireMockServer server) {
        server.stubFor(get(urlEqualTo("/details"))
                           .willReturn(aResponse()
                                           .withStatus(200)
                                           .withHeader("Content-Type", "application/json")
                                           .withBody(LRD_SERVICE_NAME)));
    }
}
