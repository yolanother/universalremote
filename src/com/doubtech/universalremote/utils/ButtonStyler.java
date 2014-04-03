package com.doubtech.universalremote.utils;

import java.util.HashMap;
import java.util.Locale;

import com.doubtech.universalremote.R;

import android.content.res.Resources;
import android.util.Log;

public class ButtonStyler {
    public static final int[] NAME_LABEL_IDS = new int[ButtonIdentifier.NAME_PATTERN_STRINGS.length];
    public static final int[] NAME_ICON_IDS = new int[ButtonIdentifier.NAME_PATTERN_STRINGS.length];

    private static final HashMap<String, String> sButtonLabels = new HashMap<String, String>();

    static {

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
        NAME_LABEL_IDS[ButtonIds.BUTTON_SEARCH] = R.string.button_search;

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
        NAME_ICON_IDS[ButtonIds.BUTTON_HOME] = R.drawable.button_home;
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
        NAME_ICON_IDS[ButtonIds.BUTTON_SEARCH] = R.drawable.ic_action_search;
    }

    public static String getLabel(Resources res, String label) {
        Integer definedLabel = ButtonIdentifier.getKnownButton(label);
        if (null != definedLabel && 0 != definedLabel) {
            String l = getLabel(res, definedLabel);
            if (null != l) return l;
        }

        String alteredLabel = sButtonLabels.get(label);
        if (null == alteredLabel) {
            alteredLabel = label.toUpperCase(Locale.getDefault()).replaceFirst(ButtonIdentifier.IGNORED_WORDS_STRING + "\\s*", "");
            if (!alteredLabel.equals(label)) {
                sButtonLabels.put(label, alteredLabel);
            }
        }

        return alteredLabel;
    }

    private static String getLabel(Resources res, int buttonIdentifier) {
        int id = NAME_LABEL_IDS[buttonIdentifier];
        return 0 != id ? res.getString(id) : null;
    }

    public static int getIconId(String label) {
        if (null == label) {
            return 0;
        }
        Integer buttonId = ButtonIdentifier.getKnownButton(label);
        return getIconId(buttonId);
    }

    private static int getIconId(Integer buttonId) {
        if (null != buttonId && buttonId < NAME_ICON_IDS.length) {
            return NAME_ICON_IDS[buttonId];
        }
        return 0;
    }
}
