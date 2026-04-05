package com.chat.utils;

import org.apache.juli.logging.Log;

import javax.ws.rs.client.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class ApiCaller {
    private static Client client;
    private static String baseUrl = "http://localhost:8080/api";
    private static final String TOKEN_FILE = "token"; // In same dir as config
    private static String memoryToken = null;

    public static void init(Client httpClient, String baseApiUrl) {
        System.out.println("Initializing API Caller with base URL: " + baseApiUrl);
        client = httpClient;
        baseUrl = baseApiUrl;
    }

    public static String callApi(String path, String method, Map<String, String> headers, String payload) {
        if (client == null) {
            client = ClientBuilder.newClient();
        }
        WebTarget target = client.target(baseUrl).path(path);
        Invocation.Builder requestBuilder = target.request();

        // Default headers
        if (headers == null) headers = new HashMap<>();
        headers.putIfAbsent(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        if (!path.startsWith("/auth/login") && !path.startsWith("/auth/register")) {
            String token = readToken();
            if (token != null && !token.isEmpty()) {
                headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            }
        }

        // Apply headers
        headers.forEach(requestBuilder::header);

        // Call API
        Response response = null;
        try {
            switch (method.toUpperCase()) {
                case "POST":   response = requestBuilder.post(payload != null ? Entity.json(payload) : null); break;
                case "PUT":    response = requestBuilder.put(payload != null ? Entity.json(payload) : null); break;
                case "DELETE": response = requestBuilder.method("DELETE", payload != null ? Entity.json(payload) : null); break;
                case "GET":    response = requestBuilder.get(); break;
                default:       throw new IllegalArgumentException("Unsupported method: " + method);
            }
        } catch (Exception e) {
            System.err.println("API Call failed: " + e.getMessage());
            return null;
        }

        int status = response.getStatus();
        String responseBody = "";
        if (response.hasEntity()) {
            responseBody = response.readEntity(String.class);
        }

        if (status >= 200 && status < 300) {
            return responseBody;
        } else if (status == 401) {
            System.err.println("Unauthorized access - logging out");
            clearToken();
            return null;
        } else if (status == 403) {
            System.err.println("Access denied");
            return null;
        } else if (status >= 500) {
            System.err.println("Server error occurred");
            return null;
        } else if (status == 404) {
            // Ignore printing 404 errors as they are expected for empty lists/resources
            return null;
        } else {
            System.err.println("API Error " + status + ": " + responseBody);
            return null;
        }
    }

    public static void saveToken(String token) {
        memoryToken = token;
        try {
            Path path = Paths.get(TOKEN_FILE);
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            Files.writeString(path, token, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to save token to file");
        }
    }

    private static String readToken() {
        if (memoryToken != null) return memoryToken;
        try {
            Path path = Paths.get(TOKEN_FILE);
            if (!Files.exists(path)) return null;
            memoryToken = Files.readString(path).trim();
            return memoryToken;
        } catch (IOException e) {
            return null;
        }
    }
    
    public static void clearToken() {
        memoryToken = null;
        try {
            Files.deleteIfExists(Paths.get(TOKEN_FILE));
        } catch (IOException e) {
            // Ignore if file doesn't exist
        }
    }
}