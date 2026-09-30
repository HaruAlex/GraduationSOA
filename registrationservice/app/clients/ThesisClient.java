package clients;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ThesisClient {

    private static final String BASE_URL =
            "http://localhost:9002";

    private final HttpClient httpClient =
            HttpClient.newHttpClient();

    public JsonNode getThesis(String maDT)
            throws IOException, InterruptedException {

        String url = BASE_URL + "/api/theses/" + maDT;

        System.out.println(
                "[Registration] Calling Thesis Service: " + url
        );

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        System.out.println(
                "[Registration] Thesis Service status: "
                        + response.statusCode()
        );

        if (response.statusCode() == 200) {

            return Json.parse(response.body());

        } else if (response.statusCode() == 404) {

            return null;

        } else {

            throw new IOException(
                    "Thesis Service returned HTTP "
                            + response.statusCode()
            );
        }
    }
}