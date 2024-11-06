package chatbot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class OpenAIChatBot {
    private static final String API_ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "enter API key here"; 
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("ChatBot: Hello! How can I help you today? (Type 'exit' to end)");
        
        while (true) {
            System.out.print("You: ");
            String userInput = scanner.nextLine();
            
            if (userInput.toLowerCase().equals("exit")) {
                System.out.println("ChatBot: Goodbye!");
                break;
            }
            
            String response = getChatGPTResponse(userInput);
            System.out.println("ChatBot: " + response);
        }
        
        scanner.close();
    }
    
    private static String getChatGPTResponse(String userInput) {
        try {
            // Create API request
            URL url = new URL(API_ENDPOINT);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setDoOutput(true);
            
            // Prepare request body for OpenAI API
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gpt-3.5-turbo");
            
            JSONArray messagesArray = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", userInput);
            messagesArray.put(message);
            
            requestBody.put("messages", messagesArray);
            requestBody.put("max_tokens", 150);
            requestBody.put("temperature", 0.7);
            
            // Send request
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(requestBody.toString());
            writer.flush();
            
            // Get response
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            
            // Parse OpenAI response
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray choices = jsonResponse.getJSONArray("choices");
            JSONObject firstChoice = choices.getJSONObject(0);
            JSONObject messageResponse = firstChoice.getJSONObject("message");
            return messageResponse.getString("content");
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Sorry, I'm having trouble connecting to the API right now.";
        }
    }
}