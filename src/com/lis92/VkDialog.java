package com.lis92; /**
 * Created by lis92 on 07.06.16.
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Бекап сообщений Вконтакте.
 */
public final class VkDialog {
    public static void save(String userId, int from, int to) throws IOException {
        VkApi vkApi = VkApi.with(Config.APP_ID, Config.ACCESS_TOKEN);

        final int count = 200;
        int offset = from;
        while (offset < to) {
            System.out.println(offset);
            String text;
            while (true) {
                text = vkApi.getHistory(userId, offset, count, true);
                if (!text.contains("Слишком много запросов")) break;
                System.out.println("Подождите 1 секундочку");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
            write(offset, text);
            offset += count;
        }
    }

    private static void write(int offset, String text) throws IOException {
        File file = new File(Config.WORK_DIR + offset + ".txt");
        try (OutputStream os = new FileOutputStream(file)) {
            OutputStreamWriter writer = new OutputStreamWriter(os, "UTF-8");
            writer.write(text);
            writer.flush();
        }
    }
}