package com.bfh.qualifier;

import com.bfh.qualifier.config.Settings;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RunnerService {

    private final WebClient http;
    private final Settings settings;
    private final ObjectMapper mapper = new ObjectMapper();

    public RunnerService(WebClient http, Settings settings) {
        this.http = http;
        this.settings = settings;
    }

    public void run() {
        Map<String, Object> req = Map.of(
                "name", settings.getName(),
                "regNo", settings.getRegNo(),
                "email", settings.getEmail()
        );
        JsonNode first = http.post()
                .uri(settings.getEndpoints().getGenerateWebhook())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
        if (first == null) throw new IllegalStateException("empty response");
        String token = find(first, List.of("accessToken", "token", "jwt")).orElse(null);
        String webhook = find(first, List.of("webhook", "webhookUrl", "url")).orElse(null);
        String submitUrl = settings.isPreferReturnedWebhook() && StringUtils.hasText(webhook)
                ? webhook
                : settings.getEndpoints().getFallbackSubmit();
        if (!StringUtils.hasText(token)) throw new IllegalStateException("missing token");
        String sql = chooseSql(settings.getRegNo(), settings.getSql().getOdd(), settings.getSql().getEven());
        try { Files.writeString(Path.of("solution.sql"), sql); } catch (IOException ignored) {}
        Map<String, Object> body = Map.of("finalQuery", sql);
        String submit = http.post()
                .uri(submitUrl)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        System.out.println("generateWebhook response:");
        System.out.println(pretty(first));
        System.out.println("using submit url: " + submitUrl);
        System.out.println("submission response:");
        System.out.println(submit);
    }

    private String chooseSql(String regNo, String odd, String even) {
        int lastTwo = lastTwoDigits(regNo);
        boolean evenFlag = lastTwo % 2 == 0;
        String sql = evenFlag ? even : odd;
        if (!StringUtils.hasText(sql)) sql = "SELECT 1;";
        return sql.trim();
    }

    private int lastTwoDigits(String s) {
    String digits = s == null ? "" : s.replaceAll("\\D", "");
    if (digits.isEmpty()) return 0;
    String lastTwo = digits.length() <= 2 ? digits : digits.substring(digits.length() - 2);
    return Integer.parseInt(lastTwo);
    }


    private Optional<String> find(JsonNode node, List<String> keys) {
        if (node == null) return Optional.empty();
        if (node.isObject()) {
            var it = node.fields();
            while (it.hasNext()) {
                var e = it.next();
                if (keys.stream().anyMatch(k -> k.equalsIgnoreCase(e.getKey()))) {
                    JsonNode v = e.getValue();
                    if (v != null && !v.isNull()) return Optional.of(v.asText());
                }
                Optional<String> nested = find(e.getValue(), keys);
                if (nested.isPresent()) return nested;
            }
        } else if (node.isArray()) {
            for (JsonNode child : node) {
                Optional<String> nested = find(child, keys);
                if (nested.isPresent()) return nested;
            }
        }
        return Optional.empty();
    }

    private String pretty(JsonNode node) {
        try { return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node); }
        catch (Exception e) { return String.valueOf(node); }
    }
}
