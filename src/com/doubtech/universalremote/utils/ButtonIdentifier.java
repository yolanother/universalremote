package com.doubtech.universalremote.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Pattern;

import android.content.res.Resources;

import com.doubtech.universalremote.R;

public class ButtonIdentifier {

    public static final String IGNORED_WORDS_STRING = "^(KEY|BUTTON|BTN|BUT|KP)";
    private static final String NBR_LEAD = "(num|number|nr)?\\s*";
    private static final String NPS_LEAD = IGNORED_WORDS_STRING + "?\\s*";
    private static final String NPS_TAIL = "$";

    public static final String[] NAME_PATTERN_STRINGS = new String[] {
        "UNKNOWN",
        NPS_LEAD + "(vol(ume)?\\s*(up|[+^]|plus|inc))|([+]\\s*vol(ume)?)" + NPS_TAIL,
        NPS_LEAD + "(vol(ume)?\\s*(down|-|minus|dec|dn|dwn))|([+]\\s*vol(ume)?)" + NPS_TAIL,
        NPS_LEAD + "pow(er)?(\\s*toggle)?" + NPS_TAIL,
        NPS_LEAD + "pow(er)?\\s*on" + NPS_TAIL,
        NPS_LEAD + "pow(er)?\\s*off" + NPS_TAIL,
        NPS_LEAD + "ch(an(nel)?)?\\s*(up|[+\\^]|inc|plus)|[+]\\s*ch(an(nel)?)?" + NPS_TAIL,
        NPS_LEAD + "ch(an(nel)?)?\\s*(down|[-]|dn|dwn|dec|minus)|[-]\\s*ch(an(nel)?)?" + NPS_TAIL,
        NPS_LEAD + NBR_LEAD + "(0|zero)" + NPS_TAIL,
        NPS_LEAD + NBR_LEAD + "(1|one)" + NPS_TAIL,
        NPS_LEAD + NBR_LEAD + "(2|two)" + NPS_TAIL,
        NPS_LEAD + NBR_LEAD + "(3|three)" + NPS_TAIL,
        NPS_LEAD + NBR_LEAD + "(4|four)" + NPS_TAIL,
        NPS_LEAD + NBR_LEAD + "(5|five)" + NPS_TAIL,
        NPS_LEAD + NBR_LEAD + "(6|six)" + NPS_TAIL,
        NPS_LEAD + NBR_LEAD + "(7|seven)" + NPS_TAIL,
        NPS_LEAD + NBR_LEAD + "(8|eight)" + NPS_TAIL,
        NPS_LEAD + NBR_LEAD + "(9|nine)" + NPS_TAIL,
        NPS_LEAD + "(settings)" + NPS_TAIL,
        NPS_LEAD + "((br|bri|brgt|bright|brighness|brightness|brt)\\s*(up|[+]|plus|inc))|((up|[+]|plus|inc)\\s*(br|bri|brgt|brt|bright|brightness|brighness))" + NPS_TAIL,
        NPS_LEAD + "((br|bri|brgt|bright|brightness|brighness|brt)\\s*(dwn|down|-|minus|dec))|((dwn|down|-|minus|dec)\\s*(br|bri|brgt|brt|bright|brightness|brighness))" + NPS_TAIL,
        NPS_LEAD + "(cancel)" + NPS_TAIL,
        NPS_LEAD + "(ok)" + NPS_TAIL,
        NPS_LEAD + "(enter)" + NPS_TAIL,
        NPS_LEAD + "(return)" + NPS_TAIL,
        NPS_LEAD + "(help)" + NPS_TAIL,
        NPS_LEAD + "(guide)" + NPS_TAIL,
        NPS_LEAD + "(home)" + NPS_TAIL,
        NPS_LEAD + "(exit)" + NPS_TAIL,
        NPS_LEAD + "(play)" + NPS_TAIL,
        NPS_LEAD + "(pause)" + NPS_TAIL,
        NPS_LEAD + "(ffwd|fast forward|FF|FFW|FFWRD|forwards|forward|fastforward|fastfwd|fforward|ffd)" + NPS_TAIL,
        NPS_LEAD + "(rew|rewind|FFRW|FFWBack|fast rewind|fastrewind|fastbackward|fastbackwards|fastrew|fastrwd)" + NPS_TAIL,
        NPS_LEAD + "(stop)" + NPS_TAIL,
        NPS_LEAD + "((pg|page)\\s*(up|[+]|plus|inc))|([+]\\s*(pg|page))" + NPS_TAIL,
        NPS_LEAD + "((pg|page)\\s*(down|-|minus|dec|dn|dwn))|([+]\\s*(pg|page))" + NPS_TAIL,
        NPS_LEAD + "(rec|record)" + NPS_TAIL,
        NPS_LEAD + "(up( arrow)?)" + NPS_TAIL,
        NPS_LEAD + "(down|dwn|dn)( arrow)?" + NPS_TAIL,
        NPS_LEAD + "(left( arrow)?)" + NPS_TAIL,
        NPS_LEAD + "(right( arrow)?)" + NPS_TAIL,
        NPS_LEAD + "(red)" + NPS_TAIL,
        NPS_LEAD + "(blue)" + NPS_TAIL,
        NPS_LEAD + "(green)" + NPS_TAIL,
        NPS_LEAD + "(yellow)" + NPS_TAIL,
        NPS_LEAD + "(back|bck)" + NPS_TAIL,
        NPS_LEAD + "(backward|bwd)" + NPS_TAIL,
        NPS_LEAD + "(mute|muting)" + NPS_TAIL,
        NPS_LEAD + "(next|nxt|skip\\s*forward)" + NPS_TAIL,
        NPS_LEAD + "(previous|prev|prv|skip\\s*back(ward)?)" + NPS_TAIL,
        NPS_LEAD + "(source|input)" + NPS_TAIL,
        NPS_LEAD + "(menu)" + NPS_TAIL,
        NPS_LEAD + "(cc|closed\\s*caption(ing|s)|captions)" + NPS_TAIL,
        NPS_LEAD + "(dot|[.])" + NPS_TAIL,
    };

    public static final int[] NAME_LABEL_IDS = new int[NAME_PATTERN_STRINGS.length];
    public static final int[] NAME_ICON_IDS = new int[NAME_PATTERN_STRINGS.length];

    public static final Pattern[] NAME_PATTERNS = new Pattern[NAME_PATTERN_STRINGS.length];

    private static final HashMap<String, Integer> sKnownButtons = new HashMap<String, Integer>();
    private static final HashMap<String, String> sButtonLabels = new HashMap<String, String>();
    private static final HashSet<String> sUnknownButtons = new HashSet<String>();

    public static final Pattern ALL_MATCH_PATTERN;

    static {
        StringBuilder allMatchString = new StringBuilder();
        for (int i = 1; i < NAME_PATTERN_STRINGS.length; i++) {
            allMatchString.append("(");
            allMatchString.append(NAME_PATTERN_STRINGS[i]);
            allMatchString.append(")");
            if (i + 1 < NAME_PATTERN_STRINGS.length) {
                allMatchString.append("|");
            }
            NAME_PATTERNS[i] = Pattern.compile(NAME_PATTERN_STRINGS[i], Pattern.CASE_INSENSITIVE);
        }

        ALL_MATCH_PATTERN = Pattern.compile(allMatchString.toString(), Pattern.CASE_INSENSITIVE);

        for (int i = 0; i < 10; i++) {
            sKnownButtons.put(Integer.toString(i), ButtonIds.BUTTON_0 + i);
        }

        NAME_LABEL_IDS[ButtonIds.BUTTON_VOLUME_UP] = R.string.button_volume_up;
        NAME_LABEL_IDS[ButtonIds.BUTTON_VOLUME_DOWN] = R.string.button_volume_down;
        NAME_LABEL_IDS[ButtonIds.BUTTON_POWER_TOGGLE] = R.string.button_power_toggle;
        NAME_LABEL_IDS[ButtonIds.BUTTON_POWER_ON] = R.string.button_power_on;
        NAME_LABEL_IDS[ButtonIds.BUTTON_POWER_OFF] = R.string.button_power_off;
        NAME_LABEL_IDS[ButtonIds.BUTTON_CH_UP] = R.string.button_ch_up;
        NAME_LABEL_IDS[ButtonIds.BUTTON_CH_DOWN] = R.string.button_ch_down;
        NAME_LABEL_IDS[ButtonIds.BUTTON_0] = R.string.button_0;
        NAME_LABEL_IDS[ButtonIds.BUTTON_1] = R.string.button_1;
        NAME_LABEL_IDS[ButtonIds.BUTTON_2] = R.string.button_2;
        NAME_LABEL_IDS[ButtonIds.BUTTON_3] = R.string.button_3;
        NAME_LABEL_IDS[ButtonIds.BUTTON_4] = R.string.button_4;
        NAME_LABEL_IDS[ButtonIds.BUTTON_5] = R.string.button_5;
        NAME_LABEL_IDS[ButtonIds.BUTTON_6] = R.string.button_6;
        NAME_LABEL_IDS[ButtonIds.BUTTON_7] = R.string.button_7;
        NAME_LABEL_IDS[ButtonIds.BUTTON_8] = R.string.button_8;
        NAME_LABEL_IDS[ButtonIds.BUTTON_9] = R.string.button_9;
        NAME_LABEL_IDS[ButtonIds.BUTTON_MENU] = R.string.button_menu;
        NAME_LABEL_IDS[ButtonIds.BUTTON_SETTINGS] = R.string.button_settings;
        NAME_LABEL_IDS[ButtonIds.BUTTON_BRIGHTNESS_UP] = R.string.button_brightness_up;
        NAME_LABEL_IDS[ButtonIds.BUTTON_BRIGHTNESS_DOWN] = R.string.button_brightness_down;
        NAME_LABEL_IDS[ButtonIds.BUTTON_CANCEL] = R.string.button_cancel;
        NAME_LABEL_IDS[ButtonIds.BUTTON_OK] = R.string.button_ok;
        NAME_LABEL_IDS[ButtonIds.BUTTON_ENTER] = R.string.button_enter;
        NAME_LABEL_IDS[ButtonIds.BUTTON_RETURN] = R.string.button_return;
        NAME_LABEL_IDS[ButtonIds.BUTTON_HELP] = R.string.button_help;
        NAME_LABEL_IDS[ButtonIds.BUTTON_GUIDE] = R.string.button_guide;
        NAME_LABEL_IDS[ButtonIds.BUTTON_HOME] = R.string.button_home;
        NAME_LABEL_IDS[ButtonIds.BUTTON_EXIT] = R.string.button_exit;
        NAME_LABEL_IDS[ButtonIds.BUTTON_PLAY] = R.string.button_play;
        NAME_LABEL_IDS[ButtonIds.BUTTON_PAUSE] = R.string.button_pause;
        NAME_LABEL_IDS[ButtonIds.BUTTON_FFWD] = R.string.button_ffwd;
        NAME_LABEL_IDS[ButtonIds.BUTTON_REW] = R.string.button_rew;
        NAME_LABEL_IDS[ButtonIds.BUTTON_STOP] = R.string.button_stop;
        NAME_LABEL_IDS[ButtonIds.BUTTON_PAGE_UP] = R.string.button_page_up;
        NAME_LABEL_IDS[ButtonIds.BUTTON_PAGE_DOWN] = R.string.button_page_down;
        NAME_LABEL_IDS[ButtonIds.BUTTON_RECORD] = R.string.button_record;
        NAME_LABEL_IDS[ButtonIds.BUTTON_UP] = R.string.button_up;
        NAME_LABEL_IDS[ButtonIds.BUTTON_DOWN] = R.string.button_down;
        NAME_LABEL_IDS[ButtonIds.BUTTON_LEFT] = R.string.button_left;
        NAME_LABEL_IDS[ButtonIds.BUTTON_RIGHT] = R.string.button_right;
        NAME_LABEL_IDS[ButtonIds.BUTTON_RED] = R.string.button_red;
        NAME_LABEL_IDS[ButtonIds.BUTTON_GREEN] = R.string.button_green;
        NAME_LABEL_IDS[ButtonIds.BUTTON_BLUE] = R.string.button_blue;
        NAME_LABEL_IDS[ButtonIds.BUTTON_YELLOW] = R.string.button_yellow;
        NAME_LABEL_IDS[ButtonIds.BUTTON_BACK] = R.string.button_back;
        NAME_LABEL_IDS[ButtonIds.BUTTON_BACKWARD] = R.string.button_backward;
        NAME_LABEL_IDS[ButtonIds.BUTTON_MUTE] = R.string.button_mute;
        NAME_LABEL_IDS[ButtonIds.BUTTON_NEXT] = R.string.button_next;
        NAME_LABEL_IDS[ButtonIds.BUTTON_PREVIOUS] = R.string.button_previous;
        NAME_LABEL_IDS[ButtonIds.BUTTON_SOURCE] = R.string.button_source;
        NAME_LABEL_IDS[ButtonIds.BUTTON_CLOSED_CAPTIONS] = R.string.button_closed_captions;
        NAME_LABEL_IDS[ButtonIds.BUTTON_DOT] = R.string.button_dot;

        NAME_ICON_IDS[ButtonIds.BUTTON_VOLUME_UP] = R.drawable.button_volume_up;
        NAME_ICON_IDS[ButtonIds.BUTTON_VOLUME_DOWN] = R.drawable.button_volume_down;
        NAME_ICON_IDS[ButtonIds.BUTTON_POWER_TOGGLE] = R.drawable.button_power;
        NAME_ICON_IDS[ButtonIds.BUTTON_POWER_ON] = R.drawable.button_power_on;
        NAME_ICON_IDS[ButtonIds.BUTTON_POWER_OFF] = R.drawable.button_power_off;
        NAME_ICON_IDS[ButtonIds.BUTTON_CH_UP] = R.drawable.button_plus;
        NAME_ICON_IDS[ButtonIds.BUTTON_CH_DOWN] = R.drawable.button_minus;
        NAME_ICON_IDS[ButtonIds.BUTTON_MENU] = R.drawable.button_menu;
        //NAME_ICON_IDS[ButtonIds.BUTTON_SETTINGS] = R.drawable.button_settings;
        NAME_ICON_IDS[ButtonIds.BUTTON_BRIGHTNESS_UP] = R.drawable.button_brightness_up;
        NAME_ICON_IDS[ButtonIds.BUTTON_BRIGHTNESS_DOWN] = R.drawable.button_brightness_down;
        NAME_ICON_IDS[ButtonIds.BUTTON_CANCEL] = R.drawable.button_cancel;
        //NAME_ICON_IDS[ButtonIds.BUTTON_OK] = R.drawable.button_ok;
        //NAME_ICON_IDS[ButtonIds.BUTTON_ENTER] = R.drawable.button_enter;
        //NAME_ICON_IDS[ButtonIds.BUTTON_RETURN] = R.drawable.button_return;
        NAME_ICON_IDS[ButtonIds.BUTTON_HELP] = R.drawable.button_help;
        //NAME_ICON_IDS[ButtonIds.BUTTON_GUIDE] = R.drawable.button_guide;
        //NAME_ICON_IDS[ButtonIds.BUTTON_HOME] = R.drawable.button_home;
        NAME_ICON_IDS[ButtonIds.BUTTON_EXIT] = R.drawable.button_return;
        NAME_ICON_IDS[ButtonIds.BUTTON_PLAY] = R.drawable.button_play;
        NAME_ICON_IDS[ButtonIds.BUTTON_PAUSE] = R.drawable.button_pause;
        NAME_ICON_IDS[ButtonIds.BUTTON_FFWD] = R.drawable.button_ffwd;
        NAME_ICON_IDS[ButtonIds.BUTTON_REW] = R.drawable.button_rew;
        NAME_ICON_IDS[ButtonIds.BUTTON_STOP] = R.drawable.button_stop;
        NAME_ICON_IDS[ButtonIds.BUTTON_RECORD] = R.drawable.button_record;
        NAME_ICON_IDS[ButtonIds.BUTTON_UP] = R.drawable.button_up;
        NAME_ICON_IDS[ButtonIds.BUTTON_DOWN] = R.drawable.button_down;
        NAME_ICON_IDS[ButtonIds.BUTTON_LEFT] = R.drawable.button_left;
        NAME_ICON_IDS[ButtonIds.BUTTON_RIGHT] = R.drawable.button_right;
        NAME_ICON_IDS[ButtonIds.BUTTON_RED] = R.drawable.button_red;
        NAME_ICON_IDS[ButtonIds.BUTTON_GREEN] = R.drawable.button_green;
        NAME_ICON_IDS[ButtonIds.BUTTON_BLUE] = R.drawable.button_blue;
        NAME_ICON_IDS[ButtonIds.BUTTON_YELLOW] = R.drawable.button_yellow;
        NAME_ICON_IDS[ButtonIds.BUTTON_BACK] = R.drawable.button_back;
        NAME_ICON_IDS[ButtonIds.BUTTON_BACKWARD] = R.drawable.button_rew;
        NAME_ICON_IDS[ButtonIds.BUTTON_MUTE] = R.drawable.button_mute;
        NAME_ICON_IDS[ButtonIds.BUTTON_NEXT] = R.drawable.button_next;
        NAME_ICON_IDS[ButtonIds.BUTTON_PREVIOUS] = R.drawable.button_previous;
        NAME_ICON_IDS[ButtonIds.BUTTON_SOURCE] = R.drawable.button_input_source;
        NAME_ICON_IDS[ButtonIds.BUTTON_DOT] = R.drawable.button_dot;
    }

    public static Integer getKnownButton(String label) {
        Integer button = sKnownButtons.get(label);
        if (null == button && !sUnknownButtons.contains(label)) {
            for (int i = 1; i < NAME_PATTERNS.length; i++) {
                Pattern p = NAME_PATTERNS[i];
                if (null != p && p.matcher(label).find()) {
                    sKnownButtons.put(label, i);
                    return i;
                }
            }

            sUnknownButtons.add(label);
        }

        return null != button ? button : ButtonIds.BUTTON_UNKNOWN;
    }

    public static String getLabel(Resources res, String label) {
        Integer definedLabel = getKnownButton(label);
        if (null != definedLabel && 0 != definedLabel) {
            return getLabel(res, definedLabel);
        }

        String alteredLabel = sButtonLabels.get(label);
        if (null == alteredLabel) {
            alteredLabel = label.toUpperCase(Locale.getDefault()).replaceFirst(IGNORED_WORDS_STRING + "\\s*", "");
            if (!alteredLabel.equals(label)) {
                sButtonLabels.put(label, alteredLabel);
            }
        }

        return alteredLabel;
    }

    private static String getLabel(Resources res, int buttonIdentifier) {
        return res.getString(NAME_LABEL_IDS[buttonIdentifier]);
    }

    public static boolean isNumber(int buttonIdentifier) {
        return buttonIdentifier >= ButtonIds.BUTTON_0 && buttonIdentifier <= ButtonIds.BUTTON_9;
    }

    public static boolean isArrow(int buttonIdentifier) {
        return buttonIdentifier == ButtonIds.BUTTON_UP || buttonIdentifier == ButtonIds.BUTTON_LEFT || buttonIdentifier == ButtonIds.BUTTON_RIGHT || buttonIdentifier == ButtonIds.BUTTON_DOWN;
    }

    public static int getIconId(String label) {
        Integer buttonId = getKnownButton(label);
        if (null != buttonId) {
            return NAME_ICON_IDS[buttonId];
        }
        return 0;
    }
}
