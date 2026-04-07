package com.smarthealth.logic;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.Map;

/**
 * Client for interacting with the Google Gemini API for medical symptom analysis.
 */
public class GeminiClient {
    private final String apiKey;
    private final HttpClient httpClient;
    private final Gson gson;

    public GeminiClient() {
        // Loads .env file from the project root
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        this.apiKey = dotenv.get("GEMINI_API_KEY");
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    /**
     * Sends symptoms to Gemini API and returns the AI-generated diagnosis/advice.
     * @param symptoms User-provided symptoms
     * @return AI response or error message
     */
    public String analyzeSymptoms(String symptoms) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "Error: Gemini API Key not found. Please ensure GEMINI_API_KEY is set in your .env file.";
        }

        // Gemini API endpoint for content generation
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        // Construct the prompt
        String prompt = "You are a professional medical assistant integrated into a healthcare system. " +
                "Based on the following symptoms: \"" + symptoms + "\", please provide: " +
                "1. A list of possible conditions. " +
                "2. Recommended next steps or general advice. " +
                "3. A clear disclaimer that this is not professional medical advice. " +
                "Keep the response concise and formatted with bullet points.";

        // Build the request body using nested maps for JSON structure
        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> contentPart = Map.of("parts", Collections.singletonList(part));
        Map<String, Object> body = Map.of("contents", Collections.singletonList(contentPart));

        String jsonBody = gson.toJson(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                // Parse the response to extract the text content
                JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
                return jsonResponse.getAsJsonArray("candidates")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("content")
                        .getAsJsonArray("parts")
                        .get(0).getAsJsonObject()
                        .get("text").getAsString();
            } else {
                return "Error calling Gemini API: Status Code " + response.statusCode() + "\nRaw response: " + response.body();
            }
        } catch (Exception e) {
            return "Critical Error: An exception occurred while contacting the AI service: " + e.getMessage();
        }
    }
}
