package com.lis92;

import java.io.IOException;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKList;
import java.awt.Desktop;
import java.io.File;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.json.JSONObject;

/**
 * Created by lis92 on 07.06.16.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        VkDialog.save("953545", 0, 14664);
        parse();
    }

    /**
     * Пакетный парсинг файлов в папке
     * @throws IOException
     */
    public static void parse() throws IOException {
        VKList<VKApiMessage> messages = new VKList<>();
        Files.list(Paths.get(Config.WORK_DIR))
                .filter(p -> p.toString().endsWith(".txt"))
                .sorted(new AlphanumComparator())
                .forEach(p -> messages.addAll(readMessages(p)));

        for (int year = 2010; year < 2016; year++) {
            LocalDateTime from = LocalDateTime.of(year, Month.JANUARY, 1, 0, 0);
            LocalDateTime to = from.plusYears(1);
            VKList<VKApiMessage> query = new VKList<>(messages.stream()
                    .filter(msg -> getDateTime(msg.date).isAfter(from)
                            && getDateTime(msg.date).isBefore(to))
                    .collect(Collectors.toList()));
            generate(query, String.valueOf(year));
        }
        /*VKList<VKApiMessage> query = new VKList<>(messages.stream()
                .filter(msg -> hasAttachment(msg.attachments, VKAttachments.TYPE_AUDIO))
                .collect(Collectors.toList()));
        generate(query, "audio");*/
    }

    private static boolean hasAttachment(VKAttachments attachments, String type) {
        return attachments.stream().anyMatch(a -> a.getType().equals(type));
    }

    /**
     * Парсинг одного конкретного файла
     * @param filename
     * @throws IOException
     */
    public static void parse(String filename) throws IOException {
        final String fullPath = Config.WORK_DIR + filename;
        VKList<VKApiMessage> messages = readMessages(Paths.get(fullPath));
        generate(messages, filename);
    }

    private static VKList<VKApiMessage> readMessages(Path fullPath) {
        try {
            final String content = new String(Files.readAllBytes(fullPath), "UTF-8");

            JSONObject jsonObject = new JSONObject(content);
            return new VKList<>(jsonObject, VKApiMessage.class);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new VKList<>();
    }

    private static void generate(VKList<VKApiMessage> messages, String outFilename) throws IOException {
        final String outPath = Config.WORK_DIR + outFilename + ".html";
        System.setOut(new PrintStream(new File(outPath)));
        generate(messages);
        try {
            Desktop.getDesktop().browse(new URL("file:///"+outPath).toURI());
        } catch (URISyntaxException ex) {
            throw new IOException(ex);
        }
    }

    private static void generate(VKList<VKApiMessage> messages) throws IOException {
        System.out.println("<html>");
        System.out.println("<head><link rel='stylesheet' type='text/css' href='styles/main.css'></head>");
        messages.stream().forEach(new MessageConsumer());
        System.out.println("</html>");
    }

    private static class MessageConsumer implements Consumer<VKApiMessage> {

        private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM uuuu", Locale.getDefault());
        private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());

        @Override
        public void accept(VKApiMessage message) {
            println(String.format("<span class='im_log_author'>%s</span>", message.out ? "Виктор" : "Сергей"));
            final LocalDateTime dateTime = getDateTime(message.date);
            String date = dateTime.format(DATE_FORMATTER) + "&nbsp;&nbsp;&nbsp;" + dateTime.format(TIME_FORMATTER);
            println(String.format("<span class='im_date_link'>%s</span>", date));
            if (!message.body.isEmpty()) {
                message.body = replaceHtml(message.body);
                if (message.emoji) {
                    Emoji.printEmoji(message.body);
                    println();
                } else println(message.body);
            }
            if (!message.fwd_messages.isEmpty()) {
                System.out.println("<div class='wall'>");
                message.fwd_messages.forEach(new MessageConsumer());
                System.out.println("</div>");
            }
            if (!message.attachments.isEmpty()) {
                System.out.println("<div class='hr'>");
                message.attachments.forEach((attachment) -> {
                    println(attachment.toHtml());
                });
                System.out.println("</div>");
            }
            println();
        }
    }

    private static String replaceHtml(String in) {
        return in.replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br/>");
    }

    private static LocalDateTime getDateTime(long timestamp) {
        return LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.ofHours(+3));
    }

    private static void println(Object text) {
        System.out.print(text);
        System.out.println("<br/>");
    }

    private static void println() {
        System.out.println("<br/>");
    }
}
