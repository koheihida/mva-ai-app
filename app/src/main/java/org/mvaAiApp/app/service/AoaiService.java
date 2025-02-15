package org.mvaAiApp.app.service;

import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;
import java.util.List;

/**
 * OpenAI API にリクエストを送信し、レスポンスを取得するサービスクラス。
 * ユーザーが入力したテーマに基づいて AI の回答を取得する。
 */
public class AoaiService {
    private static final String SYSTEM_MESSAGE = """
            あなたは、事実に基づいたフィードバックに定評があります。
            返答は菊池風磨構文で返答します。
            その後に的確な回答をします。
            """;

    private final String apiKey;
    private final OkHttpClient httpClient;

    /**
     * コンストラクタ: OpenAI API のキーを受け取り、HTTP クライアントを初期化する。
     *
     * @param apiKey OpenAI API の認証キー
     */
    public AoaiService(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = new OkHttpClient();
    }

    /**
     * OpenAI API に問い合わせ、指定したテーマについて AI の回答を取得する。
     *
     * @param theme ユーザーの入力 (質問内容)
     * @return AI の応答
     */
    public String callOpenAI(String theme) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gpt-4");
            requestBody.put("messages", List.of(
                    new JSONObject().put("role", "system").put("content", SYSTEM_MESSAGE),
                    new JSONObject().put("role", "user").put("content", theme)
            ));
            requestBody.put("max_tokens", 150);

            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .post(RequestBody.create(requestBody.toString(), MediaType.get("application/json")))
                    .build();

            Response response = httpClient.newCall(request).execute();
            return new JSONObject(response.body().string())
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

        } catch (IOException e) {
            return "エラー: OpenAI API へのリクエストに失敗しました - " + e.getMessage();
        }
    }
}