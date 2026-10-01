package clients;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class StudentClient {

    private static final String BASE_URL =
            "http://localhost:9001";

    private final HttpClient httpClient =
            HttpClient.newHttpClient();

    public JsonNode getStudent(String maSV)
            throws IOException, InterruptedException {

        String url = BASE_URL + "/api/students/" + maSV;

        System.out.println(
                "[Registration] Calling Student Service: " + url
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
                "[Registration] Student Service status: "
                        + response.statusCode()
        );

        if (response.statusCode() == 200) {

            return Json.parse(response.body());

        } else if (response.statusCode() == 404) {

            return null;

        } else {

            throw new IOException(
                    "Student Service returned HTTP "
                            + response.statusCode()
            );
        }
    }
}