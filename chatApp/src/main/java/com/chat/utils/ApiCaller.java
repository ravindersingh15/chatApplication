package com.chat.utils;

import javax.ws.rs.client.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class ApiCaller {
    private static Client client;
    private static String baseUrl;
    private static final String TOKEN_FILE = "token"; // In same dir as config

    public static void init(Client httpClient, String baseApiUrl) {
        System.out.println("Initializing API Caller with base URL: " + baseApiUrl);
        client = httpClient;
        baseUrl = baseApiUrl;
    }

    public static String callApi(String path, String method, Map<String, String> headers, String payload) {
        WebTarget target = client.target(baseUrl).path(path);
        Invocation.Builder requestBuilder = target.request();

        // Default headers
        if (headers == null) headers = new HashMap<>();
        headers.putIfAbsent(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        // Auto Authorization (unless login or register)
        if (!path.equals("/auth/login") && !path.equals("/auth/register")) {
            String token = readToken();
            if (token != null) {
                headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            }
        }

        // Apply headers
        headers.forEach(requestBuilder::header);

        // Call API
        Response response;
        switch (method.toUpperCase()) {
            case "POST":   response = requestBuilder.post(Entity.json(payload)); break;
            case "PUT":    response = requestBuilder.put(Entity.json(payload)); break;
            case "DELETE": response = requestBuilder.method("DELETE", Entity.json(payload)); break;
            case "GET":    response = requestBuilder.get(); break;
            default:       throw new IllegalArgumentException("Unsupported method: " + method);
        }

        String responseBody = response.readEntity(String.class);

        // Save token if needed
        if ((path.equals("/auth/login") || path.equals("/auth/register")) && response.getStatus() == 200) {
            String token = extractTokenFromJson(responseBody);
            if (token != null) saveToken(token);
        }

        if (response.getStatus() >= 200 && response.getStatus() < 300) {
            return responseBody;
        } else if (response.getStatus() == 401) {
            System.err.println("Unauthorized access");
            return null;
        } else {
            return null;
        }
    }

    private static String extractTokenFromJson(String json) {
        try {
            int start = json.indexOf("\"token\":\"") + 9;
            int end = json.indexOf("\"", start);
            return (start >= 9 && end > start) ? json.substring(start, end) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private static void saveToken(String token) {
        try {
            Path path = Paths.get(TOKEN_FILE);

            // Only create file during save
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            Files.writeString(path, token, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("something went wrong!!");
        }
    }

    private static String readToken() {
        try {
            Path path = Paths.get(TOKEN_FILE);
            if (!Files.exists(path)) return null;
            return Files.readString(path).trim();
        } catch (IOException e) {
            System.err.println("something went wrong!!");
            return null;
        }
    }
}
