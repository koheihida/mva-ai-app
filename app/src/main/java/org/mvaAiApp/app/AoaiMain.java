package org.mvaAiApp.app;

import org.mvaAiApp.app.service.AoaiService;
import org.mvaAiApp.app.utils.ConfigUtils;
import java.util.Scanner;

/**
 * OpenAI API を利用して、ターミナル上で対話を行うアプリケーション。
 * ユーザーが入力したテーマに対して、AI が「菊池風磨構文」で回答を行う。
 */
public class AoaiMain {
        public static void main(String[] args) {
            // 環境変数から API キーを取得
            String apiKey = ConfigUtils.getApiKey();

            // `AoaiService` のインスタンスを作成
            AoaiService aoaiService = new AoaiService(apiKey);

            // ターミナルで入力待機
            Scanner scanner = new Scanner(System.in);
            System.out.println("テーマを入力してください (終了するには 'exit' を入力):");

            while (true) {
                System.out.print("> ");
                String theme = scanner.nextLine();

                if ("exit".equalsIgnoreCase(theme)) {
                    System.out.println("終了します...");
                    break;
                }

                // AI に問い合わせて結果を取得
                String response = aoaiService.callOpenAI(theme);

                // 結果を表示
                System.out.println("\n【AIの回答】");
                System.out.println(response + "\n");
            }

            scanner.close();
        }
    }
}