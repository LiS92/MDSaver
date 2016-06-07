package com.lis92;

import java.util.Arrays;

/**
 * Класс вывода сообщений со смайлами.
 */
public final class Emoji {

    private static final char[] emojiChars = {
        '\u203c', '\u2049', '\u2139', '\u2194', '\u2195', '\u2196', '\u2197', '\u2198', '\u2199', '\u21a9', '\u21aa', 
        '\u231a', '\u231b', '\u23e9', '\u23ea', '\u23eb', '\u23ec', '\u23f0', '\u23f3', '\u24c2', '\u25aa', '\u25ab',
        '\u25b6', '\u25c0', '\u25fb', '\u25fc', '\u25fd', '\u25fe', '\u2600', '\u2601', '\u260e', '\u2611', '\u2614',
        '\u2615', '\u261d', '\u263a', '\u2648', '\u2649', '\u264a', '\u264b', '\u264c', '\u264d', '\u264e', '\u264f',
        '\u2650', '\u2651', '\u2652', '\u2653', '\u2660', '\u2663', '\u2665', '\u2666', '\u2668', '\u267b', '\u267f',
        '\u2693', '\u26a0', '\u26a1', '\u26aa', '\u26ab', '\u26bd', '\u26be', '\u26c4', '\u26c5', '\u26ce', '\u26d4',
        '\u26ea', '\u26f2', '\u26f3', '\u26f5', '\u26fa', '\u26fd', '\u2702', '\u2705', '\u2708', '\u2709', '\u270a',
        '\u270b', '\u270c', '\u270f', '\u2712', '\u2714', '\u2716', '\u2728', '\u2733', '\u2734', '\u2744', '\u2747',
        '\u274c', '\u274e', '\u2753', '\u2754', '\u2755', '\u2757', '\u2764', '\u2795', '\u2796', '\u2797', '\u27a1', 
        '\u27b0', '\u27bf', '\u2934', '\u2935', '\u2b05', '\u2b06', '\u2b07', '\u2b1b', '\u2b1c', '\u2b50', '\u2b55',
        '\u3030', '\u303d', '\u3297', '\u3299'
    };

    public static CharSequence printEmoji(final CharSequence message) {
        if (message == null || message.length() == 0) {
            return message;
        }
        
        long n = 0;
        for (int i = 0; i < message.length(); ++i) {
            final char ch = message.charAt(i);
            if (ch == '\ud83c' || ch == '\ud83d' || (n != 0 && (0xFFFFFFFF00000000L & n) == 0 && ch >= '\udde6' && ch <= '\uddfa')) {
                n = (n << 16 | ch);
            } else if (n > 0 && ('\uf000' & ch) == '\ud000') {
                Emoji.printImage(n << 16 | ch);
                n = 0;
            } else if (ch == '\u20e3') {
                if (i > 0) {
                    // Смайлы с цифрами 0..9 и #, на сайте для них нет картинок.
                    final char prev = message.charAt(i - 1);
                    if ((prev >= '0' && prev <= '9') || prev == '#') {
                        System.out.print("<"+prev+">");
                        n = 0;
                    }
                }
            } else if (Arrays.binarySearch(emojiChars, ch) != -1) {
                Emoji.printImage((long)ch);
            } else {
                System.out.print(ch);
            }
        }

        return message;
    }
    
    private static void printImage(long val) {
        String emoji = String.format("%8X", val).trim();
        System.out.print(" <img src='http://vk.com/images/emoji/" + emoji + ".png' /> ");
    }
}
