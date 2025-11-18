package com.laboratorioBases.gasolineras.ingesta;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Descarga ficheros Excel por HTTP.
 * (No lo usamos actualmente por el problema de certificados SSL,
 *  pero se deja preparado para el futuro.)
 */
public class DescargadorExcel {

    private final HttpClient httpClient;

    public DescargadorExcel() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public byte[] descargar(String url) throws Exception {
        System.out.println("Descargando: " + url);

        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).build();
        HttpResponse<byte[]> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        System.out.println("Descargado (" + response.body().length + " bytes)");
        return response.body();
    }
}
