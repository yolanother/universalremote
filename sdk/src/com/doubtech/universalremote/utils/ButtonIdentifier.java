package com.doubtech.universalremote.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

public class ButtonIdentifier {

    public static final String IGNORED_WORDS_STRING = "^(KEY|BUTTON|BTN|BUT|KP|CODE)";
    private static final String NBR_LEAD = "(num|number|nr|digit|CODE )?\\s*";
    private static final String NPS_LEAD = IGNORED_WORDS_STRING + "?\\s*";
    private static final String NPS_TAIL = "$";

    public static final String[] NAME_PATTERN_STRINGS = new String[] {
        "UNKNOWN",
        NPS_LEAD + "(vol(ume)?\\s*(up|[+^]|plus|inc))|([+]\\s*vol(ume)?)" + NPS_TAIL,
        NPS_LEAD + "(vol(ume)?\\s*(down|-|minus|dec|dn|dwn))|([+]\\s*vol(ume)?)" + NPS_TAIL,
        NPS_LEAD + "pow(er)?\\s*(toggle|on/off)?" + NPS_TAIL,
        NPS_LEAD + "pow(er)?\\s*on|on,\\s*power\\s*on/off" + NPS_TAIL,
        NPS_LEAD + "pow(er)?\\s*off|off,\\s*power\\s*on/off" + NPS_TAIL,
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
        NPS_LEAD + "(ok|menu select|dpad\\s*center)" + NPS_TAIL,
        NPS_LEAD + "(enter)" + NPS_TAIL,
        NPS_LEAD + "(return)" + NPS_TAIL,
        NPS_LEAD + "(help)" + NPS_TAIL,
        NPS_LEAD + "(guide)" + NPS_TAIL,
        NPS_LEAD + "(home)" + NPS_TAIL,
        NPS_LEAD + "(exit)" + NPS_TAIL,
        NPS_LEAD + "((media\\s*)?play)" + NPS_TAIL,
        NPS_LEAD + "((media\\s*)?pause)" + NPS_TAIL,
        NPS_LEAD + "(media\\s*)?(ffwd|fast forward|FF|FFW|FFWRD|forwards|forward|fastforward|fastfwd|fforward|ffd)" + NPS_TAIL,
        NPS_LEAD + "(media\\s*)?(rew|rewind|FFRW|FFWBack|fast rewind|fastrewind|fastbackward|fastbackwards|fastrew|fastrwd)" + NPS_TAIL,
        NPS_LEAD + "((media\\s*)?stop)" + NPS_TAIL,
        NPS_LEAD + "((pg|page)\\s*(up|[+]|plus|inc))|([+]\\s*(pg|page))" + NPS_TAIL,
        NPS_LEAD + "((pg|page)\\s*(down|-|minus|dec|dn|dwn))|([+]\\s*(pg|page))" + NPS_TAIL,
        NPS_LEAD + "(media\\s*)?(rec|record)" + NPS_TAIL,
        NPS_LEAD + "((dpad\\s*)?up( arrow)?|menu up|menu up, adjust up|adjust up)" + NPS_TAIL,
        NPS_LEAD + "((dpad\\s*)?(down|dwn|dn)( arrow)?)|menu down|menu down, adjust down|adjust down" + NPS_TAIL,
        NPS_LEAD + "((dpad\\s*)?left( arrow)?)|menu left" + NPS_TAIL,
        NPS_LEAD + "((dpad\\s*)?right( arrow)?)|menu right" + NPS_TAIL,
        NPS_LEAD + "(red)" + NPS_TAIL,
        NPS_LEAD + "(blue)" + NPS_TAIL,
        NPS_LEAD + "(green)" + NPS_TAIL,
        NPS_LEAD + "(yellow)" + NPS_TAIL,
        NPS_LEAD + "(back|bck)" + NPS_TAIL,
        NPS_LEAD + "(backward|bwd)" + NPS_TAIL,
        NPS_LEAD + "(mute|muting)" + NPS_TAIL,
        NPS_LEAD + "(media\\s*)?(next|nxt|skip\\s*forward)" + NPS_TAIL,
        NPS_LEAD + "(media\\s*)?(previous|prev|prv|skip\\s*back(ward)?)" + NPS_TAIL,
        NPS_LEAD + "(source|input|tv/video)" + NPS_TAIL,
        NPS_LEAD + "(menu)" + NPS_TAIL,
        NPS_LEAD + "(sap,\\s*cc|cc|closed\\s*caption(ing|s)|captions)" + NPS_TAIL,
        NPS_LEAD + "(dot|[.])" + NPS_TAIL,
        NPS_LEAD + "(A)" + NPS_TAIL,
        NPS_LEAD + "(B)" + NPS_TAIL,
        NPS_LEAD + "(C)" + NPS_TAIL,
        NPS_LEAD + "(D)" + NPS_TAIL,
        NPS_LEAD + "(E)" + NPS_TAIL,
        NPS_LEAD + "(F)" + NPS_TAIL,
        NPS_LEAD + "(G)" + NPS_TAIL,
        NPS_LEAD + "(H)" + NPS_TAIL,
        NPS_LEAD + "(I)" + NPS_TAIL,
        NPS_LEAD + "(J)" + NPS_TAIL,
        NPS_LEAD + "(K)" + NPS_TAIL,
        NPS_LEAD + "(L)" + NPS_TAIL,
        NPS_LEAD + "(M)" + NPS_TAIL,
        NPS_LEAD + "(N)" + NPS_TAIL,
        NPS_LEAD + "(O)" + NPS_TAIL,
        NPS_LEAD + "(P)" + NPS_TAIL,
        NPS_LEAD + "(Q)" + NPS_TAIL,
        NPS_LEAD + "(R)" + NPS_TAIL,
        NPS_LEAD + "(S)" + NPS_TAIL,
        NPS_LEAD + "(T)" + NPS_TAIL,
        NPS_LEAD + "(U)" + NPS_TAIL,
        NPS_LEAD + "(V)" + NPS_TAIL,
        NPS_LEAD + "(W)" + NPS_TAIL,
        NPS_LEAD + "(X)" + NPS_TAIL,
        NPS_LEAD + "(Y)" + NPS_TAIL,
        NPS_LEAD + "(Z)" + NPS_TAIL,
        NPS_LEAD + "(SEARCH)" + NPS_TAIL,
    };

    public static final Pattern[] NAME_PATTERNS = new Pattern[NAME_PATTERN_STRINGS.length];

    private static final HashMap<String, Integer> sKnownButtons = new HashMap<String, Integer>();
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

    public static boolean isNumber(int buttonIdentifier) {
        return buttonIdentifier >= ButtonIds.BUTTON_0 && buttonIdentifier <= ButtonIds.BUTTON_9;
    }

    public static boolean isArrow(int buttonIdentifier) {
        return buttonIdentifier == ButtonIds.BUTTON_UP || buttonIdentifier == ButtonIds.BUTTON_LEFT || buttonIdentifier == ButtonIds.BUTTON_RIGHT || buttonIdentifier == ButtonIds.BUTTON_DOWN;
    }
}
