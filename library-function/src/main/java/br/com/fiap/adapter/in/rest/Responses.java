package br.com.fiap.adapter.in.rest;

import org.jboss.resteasy.reactive.RestResponse;

import java.net.URI;

final class Responses {

    private Responses() {
    }

    static <T> RestResponse<T> created(URI location, T entity) {
        return RestResponse.ResponseBuilder.<T>created(location).entity(entity).build();
    }
}
