package org.mvaAiApp.app;

import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * OpenAI API を利用して、ターミナル上で対話を行うアプリケーション。
 * ユーザーが入力したテーマに対して、AI が「菊池風磨構文」で回答を行う。
 */
public class AoaiMain {
    // AI に対して事前設定する内容
    private static final String SYSTEM_MESSAGE = """
            あなたは、事実に基づいたフィードバックに定評があります。
            返答は菊池風磨構文で返答します。
            その後に的確な回答をします。
            【質問者】コーヒーって知ってますか？？
            【あなた】コーヒーを忘れているようじゃダメか・・・、コーヒーは知っとかないと。
            　　　　　コーヒーは世界で最も飲まれている飲料で、カフェインを多く含み、茶色い液体です。
            　　　　　エスプレッソやドリップコーヒーなど様々な種類が存在しており、バリスタと呼ばれるコーヒーのスペシャリストが
            　　　　　コーヒー豆から抽出して提供したりします。
            """;

    public static void main(String[] args) {
        // 環境変数から OpenAI API キーを取得
        String apiKey = System.getenv("OPENAI_API_KEY");

        // API キーが設定されていない場合は手動入力
        if (apiKey == null || apiKey.isEmpty()) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("OpenAI API Key を入力してください:");
            apiKey = scanner.nextLine();
        }

        // ターミナル入力待機
        Scanner scanner = new Scanner(System.in);
        System.out.println("テーマを入力してください (終了するには 'exit' を入力):");

        while (true) {
            System.out.print("> ");
            String theme = scanner.nextLine();

            if ("exit".equalsIgnoreCase(theme)) {
                System.out.println("終了します...");
                break;
            }

            // OpenAI API を呼び出す
            String response = callOpenAI(apiKey, theme);

            // 結果を表示
            System.out.println("\n【回答】");
            System.out.println(response + "\n");
        }
        scanner.close();
    }

    /**
     * OpenAI API を呼び出し、ユーザーが入力したテーマに対する AI の回答を取得する
     *
     * @param apiKey OpenAI API の認証キー
     * @param theme ユーザーが入力したテーマ
     * @return AI からの回答
     */
    private static String callOpenAI(String apiKey, String theme) {
        // OkHttp クライアントのインスタンスを作成 (HTTP リクエストを送るため)
        OkHttpClient httpClient = new OkHttpClient();

        try {
            // OpenAI API に送るリクエストボディを作成
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gpt-4"); // GPT-4を使用するように指定
            requestBody.put("messages", List.of(
                    // AI に事前設定を渡す
                    new JSONObject().put("role", "system").put("content", SYSTEM_MESSAGE),
                    // ユーザーの質問
                    new JSONObject().put("role", "user").put("content", theme)
            ));
            requestBody.put("max_tokens", 150);

            // OpenAI API への HTTP リクエストを構築
            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions") // OpenAI APIエンドポイント
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .post(RequestBody.create(requestBody.toString(), MediaType.get("application/json")))
                    .build();
            // API を呼び出してレスポンスを取得
            Response response = httpClient.newCall(request).execute();
            // レスポンスを JSON オブジェクトとして解析し、AI の回答部分を取得
            return new JSONObject(response.body().string())
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

        } catch (IOException e) {
            return "エラー: " + e.getMessage();
        }
    }
}