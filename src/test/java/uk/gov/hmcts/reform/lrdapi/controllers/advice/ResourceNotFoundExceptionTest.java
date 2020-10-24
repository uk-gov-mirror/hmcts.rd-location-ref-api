package uk.gov.hmcts.reform.lrdapi.controllers.advice;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class ResourceNotFoundExceptionTest {

    @Test
    public void test_handle_resource_not_found_exception() {
        ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException("Resource not found");
        assertThat(resourceNotFoundException).isNotNull();
        assertEquals("Resource not found", resourceNotFoundException.getMessage());

    }
}
