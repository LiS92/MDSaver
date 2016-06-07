package com.vk.sdk.api.model;

/**
 *
 * @author aNNiMON
 */
class TextUtils {

    static boolean isEmpty(CharSequence text) {
        return (text == null) || (text.length() == 0);
    }
    
    static String formatDuration(int duration) {
        final StringBuilder sb = new StringBuilder();
        final int hours = (duration / 3600);
        if (hours != 0) sb.append(formatNumber(hours)).append(':');
        final int minutes = duration % 3600 / 60;
        final int seconds = duration % 60;
        sb.append(formatNumber(minutes)).append(':').append(formatNumber(seconds));
        return sb.toString();
    }
    
    private static String formatNumber(int number) {
        return String.format("%02d", number);
    }
}
