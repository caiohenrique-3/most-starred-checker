package app;

import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class Main {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        String startDate;
        String endDate;
        String ghAPI = "https://api.github.com/search/repositories";
        String charset = StandardCharsets.UTF_8.name();

        System.out.println("Enter the start date (YYYY-MM-DD)");
        startDate = scanner.nextLine();

        System.out.println("Enter the end date (YYYY-MM-DD)");
        endDate = scanner.nextLine();

        System.out.println(
                "Searching most starred repositories " +
                        "between " + startDate + " and " + endDate);

        try {
            String query = String.format(
                    "?q=created:%s..%s&sort=stars&order=desc&per_page=5",
                    startDate + "T00:00:00Z", endDate + "T23:59:59Z");
            URLConnection connection = new URL(ghAPI + query).openConnection();
            if (connection instanceof HttpURLConnection){
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");

                int responseCode = httpConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK){
                    InputStream response = connection.getInputStream();
                    Scanner s = new Scanner(response).
                            useDelimiter("\\A");
                    String result = s.hasNext() ? s.next() : "";

                    JSONObject jsonResponse = new JSONObject(result);
                    JSONArray items = jsonResponse.getJSONArray("items");

                    for (int i = 0; i < items.length(); i++){
                        JSONObject repo = items.getJSONObject(i);
                        String repoUrl = repo.getString("html_url");
                        int stars = repo.getInt("stargazers_count");

                        System.out.println("Repo URL: " + repoUrl);
                        System.out.println("Stars: " + stars);
                        System.out.println();
                    }
                } else {
                    System.out.println(
                            "HTTP request failed " +
                                    "with response code: " + responseCode);
                }
            } else {
                System.out.println("Invalid URL connection");
            }
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }
}