package com.corrodinggames.rts.gameFramework.utility;

import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/SlickToAndroidKeycodes.class */
public class SlickToAndroidKeycodes {

    /* JADX INFO: renamed from: a */
    static HashMap slickToAndroid;

    /* JADX INFO: renamed from: b */
    static HashMap gdxToAndroidMap;

    /* JADX INFO: renamed from: c */
    static HashMap gdxToSlickMap;

    /* JADX INFO: renamed from: d */
    static HashMap<String, Integer> slickNameMap = createKeyNameMap(SlickCodes.class);

    /* JADX INFO: renamed from: e */
    static HashMap androidNameMap = createKeyNameMap(AndroidCodes.class);

    /* JADX INFO: renamed from: f */
    static HashMap gdxNameMap = createKeyNameMap(GdxCodes.class);

    /* JADX INFO: renamed from: g */
    static HashMap androidKeyCodeToNameMap;

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/SlickToAndroidKeycodes$AndroidCodes.class */
    public class AndroidCodes {
        public static final int KEYCODE_UNKNOWN = 0;
        public static final int KEYCODE_SOFT_LEFT = 1;
        public static final int KEYCODE_SOFT_RIGHT = 2;
        public static final int KEYCODE_HOME = 3;
        public static final int KEYCODE_BACK = 4;
        public static final int KEYCODE_CALL = 5;
        public static final int KEYCODE_ENDCALL = 6;
        public static final int KEYCODE_0 = 7;
        public static final int KEYCODE_1 = 8;
        public static final int KEYCODE_2 = 9;
        public static final int KEYCODE_3 = 10;
        public static final int KEYCODE_4 = 11;
        public static final int KEYCODE_5 = 12;
        public static final int KEYCODE_6 = 13;
        public static final int KEYCODE_7 = 14;
        public static final int KEYCODE_8 = 15;
        public static final int KEYCODE_9 = 16;
        public static final int KEYCODE_STAR = 17;
        public static final int KEYCODE_POUND = 18;
        public static final int KEYCODE_DPAD_UP = 19;
        public static final int KEYCODE_DPAD_DOWN = 20;
        public static final int KEYCODE_DPAD_LEFT = 21;
        public static final int KEYCODE_DPAD_RIGHT = 22;
        public static final int KEYCODE_DPAD_CENTER = 23;
        public static final int KEYCODE_UP = 19;
        public static final int KEYCODE_DOWN = 20;
        public static final int KEYCODE_LEFT = 21;
        public static final int KEYCODE_RIGHT = 22;
        public static final int KEYCODE_CENTER = 23;
        public static final int KEYCODE_VOLUME_UP = 24;
        public static final int KEYCODE_VOLUME_DOWN = 25;
        public static final int KEYCODE_POWER = 26;
        public static final int KEYCODE_CAMERA = 27;
        public static final int KEYCODE_CLEAR = 28;
        public static final int KEYCODE_A = 29;
        public static final int KEYCODE_B = 30;
        public static final int KEYCODE_C = 31;
        public static final int KEYCODE_D = 32;
        public static final int KEYCODE_E = 33;
        public static final int KEYCODE_F = 34;
        public static final int KEYCODE_G = 35;
        public static final int KEYCODE_H = 36;
        public static final int KEYCODE_I = 37;
        public static final int KEYCODE_J = 38;
        public static final int KEYCODE_K = 39;
        public static final int KEYCODE_L = 40;
        public static final int KEYCODE_M = 41;
        public static final int KEYCODE_N = 42;
        public static final int KEYCODE_O = 43;
        public static final int KEYCODE_P = 44;
        public static final int KEYCODE_Q = 45;
        public static final int KEYCODE_R = 46;
        public static final int KEYCODE_S = 47;
        public static final int KEYCODE_T = 48;
        public static final int KEYCODE_U = 49;
        public static final int KEYCODE_V = 50;
        public static final int KEYCODE_W = 51;
        public static final int KEYCODE_X = 52;
        public static final int KEYCODE_Y = 53;
        public static final int KEYCODE_Z = 54;
        public static final int KEYCODE_COMMA = 55;
        public static final int KEYCODE_PERIOD = 56;
        public static final int KEYCODE_LALT = 57;
        public static final int KEYCODE_RALT = 58;
        public static final int KEYCODE_LSHIFT = 59;
        public static final int KEYCODE_RSHIFT = 60;
        public static final int KEYCODE_ALT_LEFT = 57;
        public static final int KEYCODE_ALT_RIGHT = 58;
        public static final int KEYCODE_SHIFT_LEFT = 59;
        public static final int KEYCODE_SHIFT_RIGHT = 60;
        public static final int KEYCODE_TAB = 61;
        public static final int KEYCODE_SPACE = 62;
        public static final int KEYCODE_SYM = 63;
        public static final int KEYCODE_EXPLORER = 64;
        public static final int KEYCODE_ENVELOPE = 65;
        public static final int KEYCODE_ENTER = 66;
        public static final int KEYCODE_DEL = 67;
        public static final int KEYCODE_GRAVE = 68;
        public static final int KEYCODE_MINUS = 69;
        public static final int KEYCODE_EQUALS = 70;
        public static final int KEYCODE_LEFT_BRACKET = 71;
        public static final int KEYCODE_RIGHT_BRACKET = 72;
        public static final int KEYCODE_BACKSLASH = 73;
        public static final int KEYCODE_SEMICOLON = 74;
        public static final int KEYCODE_APOSTROPHE = 75;
        public static final int KEYCODE_SLASH = 76;
        public static final int KEYCODE_AT = 77;
        public static final int KEYCODE_NUM = 78;
        public static final int KEYCODE_HEADSETHOOK = 79;
        public static final int KEYCODE_FOCUS = 80;
        public static final int KEYCODE_PLUS = 81;
        public static final int KEYCODE_MENU = 82;
        public static final int KEYCODE_NOTIFICATION = 83;
        public static final int KEYCODE_SEARCH = 84;
        public static final int KEYCODE_MEDIA_PLAY_PAUSE = 85;
        public static final int KEYCODE_MEDIA_STOP = 86;
        public static final int KEYCODE_MEDIA_NEXT = 87;
        public static final int KEYCODE_MEDIA_PREVIOUS = 88;
        public static final int KEYCODE_MEDIA_REWIND = 89;
        public static final int KEYCODE_MEDIA_FAST_FORWARD = 90;
        public static final int KEYCODE_MUTE = 91;
        public static final int KEYCODE_PAGE_UP = 92;
        public static final int KEYCODE_PAGE_DOWN = 93;
        public static final int KEYCODE_PICTSYMBOLS = 94;
        public static final int KEYCODE_SWITCH_CHARSET = 95;
        public static final int KEYCODE_BUTTON_A = 96;
        public static final int KEYCODE_BUTTON_B = 97;
        public static final int KEYCODE_BUTTON_C = 98;
        public static final int KEYCODE_BUTTON_X = 99;
        public static final int KEYCODE_BUTTON_Y = 100;
        public static final int KEYCODE_BUTTON_Z = 101;
        public static final int KEYCODE_BUTTON_L1 = 102;
        public static final int KEYCODE_BUTTON_R1 = 103;
        public static final int KEYCODE_BUTTON_L2 = 104;
        public static final int KEYCODE_BUTTON_R2 = 105;
        public static final int KEYCODE_BUTTON_THUMBL = 106;
        public static final int KEYCODE_BUTTON_THUMBR = 107;
        public static final int KEYCODE_BUTTON_START = 108;
        public static final int KEYCODE_BUTTON_SELECT = 109;
        public static final int KEYCODE_BUTTON_MODE = 110;
        public static final int KEYCODE_ESCAPE = 111;
        public static final int KEYCODE_FORWARD_DEL = 112;
        public static final int KEYCODE_CTRL_LEFT = 113;
        public static final int KEYCODE_CTRL_RIGHT = 114;
        public static final int KEYCODE_LCONTROL = 113;
        public static final int KEYCODE_RCONTROL = 114;
        public static final int KEYCODE_CAPS_LOCK = 115;
        public static final int KEYCODE_SCROLL_LOCK = 116;
        public static final int KEYCODE_META_LEFT = 117;
        public static final int KEYCODE_META_RIGHT = 118;
        public static final int KEYCODE_FUNCTION = 119;
        public static final int KEYCODE_SYSRQ = 120;
        public static final int KEYCODE_BREAK = 121;
        public static final int KEYCODE_MOVE_HOME = 122;
        public static final int KEYCODE_MOVE_END = 123;
        public static final int KEYCODE_INSERT = 124;
        public static final int KEYCODE_FORWARD = 125;
        public static final int KEYCODE_MEDIA_PLAY = 126;
        public static final int KEYCODE_MEDIA_PAUSE = 127;
        public static final int KEYCODE_MEDIA_CLOSE = 128;
        public static final int KEYCODE_MEDIA_EJECT = 129;
        public static final int KEYCODE_MEDIA_RECORD = 130;
        public static final int KEYCODE_F1 = 131;
        public static final int KEYCODE_F2 = 132;
        public static final int KEYCODE_F3 = 133;
        public static final int KEYCODE_F4 = 134;
        public static final int KEYCODE_F5 = 135;
        public static final int KEYCODE_F6 = 136;
        public static final int KEYCODE_F7 = 137;
        public static final int KEYCODE_F8 = 138;
        public static final int KEYCODE_F9 = 139;
        public static final int KEYCODE_F10 = 140;
        public static final int KEYCODE_F11 = 141;
        public static final int KEYCODE_F12 = 142;
        public static final int KEYCODE_NUM_LOCK = 143;
        public static final int KEYCODE_NUMPAD_0 = 144;
        public static final int KEYCODE_NUMPAD_1 = 145;
        public static final int KEYCODE_NUMPAD_2 = 146;
        public static final int KEYCODE_NUMPAD_3 = 147;
        public static final int KEYCODE_NUMPAD_4 = 148;
        public static final int KEYCODE_NUMPAD_5 = 149;
        public static final int KEYCODE_NUMPAD_6 = 150;
        public static final int KEYCODE_NUMPAD_7 = 151;
        public static final int KEYCODE_NUMPAD_8 = 152;
        public static final int KEYCODE_NUMPAD_9 = 153;
        public static final int KEYCODE_NUMPAD0 = 144;
        public static final int KEYCODE_NUMPAD1 = 145;
        public static final int KEYCODE_NUMPAD2 = 146;
        public static final int KEYCODE_NUMPAD3 = 147;
        public static final int KEYCODE_NUMPAD4 = 148;
        public static final int KEYCODE_NUMPAD5 = 149;
        public static final int KEYCODE_NUMPAD6 = 150;
        public static final int KEYCODE_NUMPAD7 = 151;
        public static final int KEYCODE_NUMPAD8 = 152;
        public static final int KEYCODE_NUMPAD9 = 153;
        public static final int KEYCODE_NUMPAD_DIVIDE = 154;
        public static final int KEYCODE_NUMPAD_MULTIPLY = 155;
        public static final int KEYCODE_NUMPAD_SUBTRACT = 156;
        public static final int KEYCODE_NUMPAD_ADD = 157;
        public static final int KEYCODE_NUMPAD_DOT = 158;
        public static final int KEYCODE_NUMPAD_COMMA = 159;
        public static final int KEYCODE_NUMPAD_ENTER = 160;
        public static final int KEYCODE_NUMPAD_EQUALS = 161;
        public static final int KEYCODE_NUMPAD_LEFT_PAREN = 162;
        public static final int KEYCODE_NUMPAD_RIGHT_PAREN = 163;
        public static final int KEYCODE_VOLUME_MUTE = 164;
        public static final int KEYCODE_INFO = 165;
        public static final int KEYCODE_CHANNEL_UP = 166;
        public static final int KEYCODE_CHANNEL_DOWN = 167;
        public static final int KEYCODE_ZOOM_IN = 168;
        public static final int KEYCODE_ZOOM_OUT = 169;
        public static final int KEYCODE_TV = 170;
        public static final int KEYCODE_WINDOW = 171;
        public static final int KEYCODE_GUIDE = 172;
        public static final int KEYCODE_DVR = 173;
        public static final int KEYCODE_BOOKMARK = 174;
        public static final int KEYCODE_CAPTIONS = 175;
        public static final int KEYCODE_SETTINGS = 176;
        public static final int KEYCODE_TV_POWER = 177;
        public static final int KEYCODE_TV_INPUT = 178;
        public static final int KEYCODE_STB_POWER = 179;
        public static final int KEYCODE_STB_INPUT = 180;
        public static final int KEYCODE_AVR_POWER = 181;
        public static final int KEYCODE_AVR_INPUT = 182;
        public static final int KEYCODE_PROG_RED = 183;
        public static final int KEYCODE_PROG_GREEN = 184;
        public static final int KEYCODE_PROG_YELLOW = 185;
        public static final int KEYCODE_PROG_BLUE = 186;
        public static final int KEYCODE_APP_SWITCH = 187;
        public static final int KEYCODE_BUTTON_1 = 188;
        public static final int KEYCODE_BUTTON_2 = 189;
        public static final int KEYCODE_BUTTON_3 = 190;
        public static final int KEYCODE_BUTTON_4 = 191;
        public static final int KEYCODE_BUTTON_5 = 192;
        public static final int KEYCODE_BUTTON_6 = 193;
        public static final int KEYCODE_BUTTON_7 = 194;
        public static final int KEYCODE_BUTTON_8 = 195;
        public static final int KEYCODE_BUTTON_9 = 196;
        public static final int KEYCODE_BUTTON_10 = 197;
        public static final int KEYCODE_BUTTON_11 = 198;
        public static final int KEYCODE_BUTTON_12 = 199;
        public static final int KEYCODE_BUTTON_13 = 200;
        public static final int KEYCODE_BUTTON_14 = 201;
        public static final int KEYCODE_BUTTON_15 = 202;
        public static final int KEYCODE_BUTTON_16 = 203;
        public static final int KEYCODE_LANGUAGE_SWITCH = 204;
        public static final int KEYCODE_MANNER_MODE = 205;
        public static final int KEYCODE_3D_MODE = 206;
        public static final int KEYCODE_CONTACTS = 207;
        public static final int KEYCODE_CALENDAR = 208;
        public static final int KEYCODE_MUSIC = 209;
        public static final int KEYCODE_CALCULATOR = 210;
        public static final int KEYCODE_ZENKAKU_HANKAKU = 211;
        public static final int KEYCODE_EISU = 212;
        public static final int KEYCODE_MUHENKAN = 213;
        public static final int KEYCODE_HENKAN = 214;
        public static final int KEYCODE_KATAKANA_HIRAGANA = 215;
        public static final int KEYCODE_YEN = 216;
        public static final int KEYCODE_RO = 217;
        public static final int KEYCODE_KANA = 218;
        public static final int KEYCODE_ASSIST = 219;
        public static final int KEYCODE_BRIGHTNESS_DOWN = 220;
        public static final int KEYCODE_BRIGHTNESS_UP = 221;
        public static final int KEYCODE_MEDIA_AUDIO_TRACK = 222;
        public static final int KEYCODE_SLEEP = 223;
        public static final int KEYCODE_WAKEUP = 224;
        public static final int KEYCODE_PAIRING = 225;
        public static final int KEYCODE_MEDIA_TOP_MENU = 226;
        public static final int KEYCODE_11 = 227;
        public static final int KEYCODE_12 = 228;
        public static final int KEYCODE_LAST_CHANNEL = 229;
        public static final int KEYCODE_TV_DATA_SERVICE = 230;
        public static final int KEYCODE_VOICE_ASSIST = 231;
        public static final int KEYCODE_TV_RADIO_SERVICE = 232;
        public static final int KEYCODE_TV_TELETEXT = 233;
        public static final int KEYCODE_TV_NUMBER_ENTRY = 234;
        public static final int KEYCODE_TV_TERRESTRIAL_ANALOG = 235;
        public static final int KEYCODE_TV_TERRESTRIAL_DIGITAL = 236;
        public static final int KEYCODE_TV_SATELLITE = 237;
        public static final int KEYCODE_TV_SATELLITE_BS = 238;
        public static final int KEYCODE_TV_SATELLITE_CS = 239;
        public static final int KEYCODE_TV_SATELLITE_SERVICE = 240;
        public static final int KEYCODE_TV_NETWORK = 241;
        public static final int KEYCODE_TV_ANTENNA_CABLE = 242;
        public static final int KEYCODE_TV_INPUT_HDMI_1 = 243;
        public static final int KEYCODE_TV_INPUT_HDMI_2 = 244;
        public static final int KEYCODE_TV_INPUT_HDMI_3 = 245;
        public static final int KEYCODE_TV_INPUT_HDMI_4 = 246;
        public static final int KEYCODE_TV_INPUT_COMPOSITE_1 = 247;
        public static final int KEYCODE_TV_INPUT_COMPOSITE_2 = 248;
        public static final int KEYCODE_TV_INPUT_COMPONENT_1 = 249;
        public static final int KEYCODE_TV_INPUT_COMPONENT_2 = 250;
        public static final int KEYCODE_TV_INPUT_VGA_1 = 251;
        public static final int KEYCODE_TV_AUDIO_DESCRIPTION = 252;
        public static final int KEYCODE_TV_AUDIO_DESCRIPTION_MIX_UP = 253;
        public static final int KEYCODE_TV_AUDIO_DESCRIPTION_MIX_DOWN = 254;
        public static final int KEYCODE_TV_ZOOM_MODE = 255;
        public static final int KEYCODE_TV_CONTENTS_MENU = 256;
        public static final int KEYCODE_TV_MEDIA_CONTEXT_MENU = 257;
        public static final int KEYCODE_TV_TIMER_PROGRAMMING = 258;
        public static final int KEYCODE_HELP = 259;
        public static final int KEYCODE_NAVIGATE_PREVIOUS = 260;
        public static final int KEYCODE_NAVIGATE_NEXT = 261;
        public static final int KEYCODE_NAVIGATE_IN = 262;
        public static final int KEYCODE_NAVIGATE_OUT = 263;
        public static final int KEYCODE_STEM_PRIMARY = 264;
        public static final int KEYCODE_STEM_1 = 265;
        public static final int KEYCODE_STEM_2 = 266;
        public static final int KEYCODE_STEM_3 = 267;
        public static final int KEYCODE_MEDIA_SKIP_FORWARD = 272;
        public static final int KEYCODE_MEDIA_SKIP_BACKWARD = 273;
        public static final int KEYCODE_MEDIA_STEP_FORWARD = 274;
        public static final int KEYCODE_MEDIA_STEP_BACKWARD = 275;
        public static final int KEYCODE_SOFT_SLEEP = 276;
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/SlickToAndroidKeycodes$GdxCodes.class */
    public class GdxCodes {
        public static final int ANY_KEY = -1;
        public static final int NUM_0 = 7;
        public static final int NUM_1 = 8;
        public static final int NUM_2 = 9;
        public static final int NUM_3 = 10;
        public static final int NUM_4 = 11;
        public static final int NUM_5 = 12;
        public static final int NUM_6 = 13;
        public static final int NUM_7 = 14;
        public static final int NUM_8 = 15;
        public static final int NUM_9 = 16;
        public static final int A = 29;
        public static final int ALT_LEFT = 57;
        public static final int ALT_RIGHT = 58;
        public static final int APOSTROPHE = 75;
        public static final int AT = 77;
        public static final int B = 30;
        public static final int BACK = 4;
        public static final int BACKSLASH = 73;
        public static final int C = 31;
        public static final int CALL = 5;
        public static final int CAMERA = 27;
        public static final int CLEAR = 28;
        public static final int COMMA = 55;
        public static final int D = 32;
        public static final int DEL = 67;
        public static final int BACKSPACE = 67;
        public static final int FORWARD_DEL = 112;
        public static final int DPAD_CENTER = 23;
        public static final int DPAD_DOWN = 20;
        public static final int DPAD_LEFT = 21;
        public static final int DPAD_RIGHT = 22;
        public static final int DPAD_UP = 19;
        public static final int CENTER = 23;
        public static final int DOWN = 20;
        public static final int LEFT = 21;
        public static final int RIGHT = 22;
        public static final int UP = 19;
        public static final int E = 33;
        public static final int ENDCALL = 6;
        public static final int ENTER = 66;
        public static final int ENVELOPE = 65;
        public static final int EQUALS = 70;
        public static final int EXPLORER = 64;
        public static final int F = 34;
        public static final int FOCUS = 80;
        public static final int G = 35;
        public static final int GRAVE = 68;
        public static final int H = 36;
        public static final int HEADSETHOOK = 79;
        public static final int HOME = 3;
        public static final int I = 37;
        public static final int J = 38;
        public static final int K = 39;
        public static final int L = 40;
        public static final int LEFT_BRACKET = 71;
        public static final int M = 41;
        public static final int MEDIA_FAST_FORWARD = 90;
        public static final int MEDIA_NEXT = 87;
        public static final int MEDIA_PLAY_PAUSE = 85;
        public static final int MEDIA_PREVIOUS = 88;
        public static final int MEDIA_REWIND = 89;
        public static final int MEDIA_STOP = 86;
        public static final int MENU = 82;
        public static final int MINUS = 69;
        public static final int MUTE = 91;
        public static final int N = 42;
        public static final int NOTIFICATION = 83;
        public static final int NUM = 78;
        public static final int O = 43;
        public static final int P = 44;
        public static final int PERIOD = 56;
        public static final int PLUS = 81;
        public static final int POUND = 18;
        public static final int POWER = 26;
        public static final int Q = 45;
        public static final int R = 46;
        public static final int RIGHT_BRACKET = 72;
        public static final int S = 47;
        public static final int SEARCH = 84;
        public static final int SEMICOLON = 74;
        public static final int SHIFT_LEFT = 59;
        public static final int SHIFT_RIGHT = 60;
        public static final int SLASH = 76;
        public static final int SOFT_LEFT = 1;
        public static final int SOFT_RIGHT = 2;
        public static final int SPACE = 62;
        public static final int STAR = 17;
        public static final int SYM = 63;
        public static final int T = 48;
        public static final int TAB = 61;
        public static final int U = 49;
        public static final int UNKNOWN = 0;
        public static final int V = 50;
        public static final int VOLUME_DOWN = 25;
        public static final int VOLUME_UP = 24;
        public static final int W = 51;
        public static final int X = 52;
        public static final int Y = 53;
        public static final int Z = 54;
        public static final int META_ALT_LEFT_ON = 16;
        public static final int META_ALT_ON = 2;
        public static final int META_ALT_RIGHT_ON = 32;
        public static final int META_SHIFT_LEFT_ON = 64;
        public static final int META_SHIFT_ON = 1;
        public static final int META_SHIFT_RIGHT_ON = 128;
        public static final int META_SYM_ON = 4;
        public static final int CONTROL_LEFT = 129;
        public static final int CONTROL_RIGHT = 130;
        public static final int KEY_LCONTROL = 129;
        public static final int KEY_RCONTROL = 130;
        public static final int ESCAPE = 131;
        public static final int END = 132;
        public static final int INSERT = 133;
        public static final int PAGE_UP = 92;
        public static final int PAGE_DOWN = 93;
        public static final int PICTSYMBOLS = 94;
        public static final int SWITCH_CHARSET = 95;
        public static final int BUTTON_CIRCLE = 255;
        public static final int BUTTON_A = 96;
        public static final int BUTTON_B = 97;
        public static final int BUTTON_C = 98;
        public static final int BUTTON_X = 99;
        public static final int BUTTON_Y = 100;
        public static final int BUTTON_Z = 101;
        public static final int BUTTON_L1 = 102;
        public static final int BUTTON_R1 = 103;
        public static final int BUTTON_L2 = 104;
        public static final int BUTTON_R2 = 105;
        public static final int BUTTON_THUMBL = 106;
        public static final int BUTTON_THUMBR = 107;
        public static final int BUTTON_START = 108;
        public static final int BUTTON_SELECT = 109;
        public static final int BUTTON_MODE = 110;
        public static final int NUMPAD_0 = 144;
        public static final int NUMPAD_1 = 145;
        public static final int NUMPAD_2 = 146;
        public static final int NUMPAD_3 = 147;
        public static final int NUMPAD_4 = 148;
        public static final int NUMPAD_5 = 149;
        public static final int NUMPAD_6 = 150;
        public static final int NUMPAD_7 = 151;
        public static final int NUMPAD_8 = 152;
        public static final int NUMPAD_9 = 153;
        public static final int COLON = 243;
        public static final int F1 = 244;
        public static final int F2 = 245;
        public static final int F3 = 246;
        public static final int F4 = 247;
        public static final int F5 = 248;
        public static final int F6 = 249;
        public static final int F7 = 250;
        public static final int F8 = 251;
        public static final int F9 = 252;
        public static final int F10 = 253;
        public static final int F11 = 254;
        public static final int F12 = 255;
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/SlickToAndroidKeycodes$LibrocketKeys.class */
    public class LibrocketKeys {
        public static final int KI_UNKNOWN = 0;
        public static final int KI_SPACE = 1;
        public static final int KI_0 = 2;
        public static final int KI_1 = 3;
        public static final int KI_2 = 4;
        public static final int KI_3 = 5;
        public static final int KI_4 = 6;
        public static final int KI_5 = 7;
        public static final int KI_6 = 8;
        public static final int KI_7 = 9;
        public static final int KI_8 = 10;
        public static final int KI_9 = 11;
        public static final int KI_A = 12;
        public static final int KI_B = 13;
        public static final int KI_C = 14;
        public static final int KI_D = 15;
        public static final int KI_E = 16;
        public static final int KI_F = 17;
        public static final int KI_G = 18;
        public static final int KI_H = 19;
        public static final int KI_I = 20;
        public static final int KI_J = 21;
        public static final int KI_K = 22;
        public static final int KI_L = 23;
        public static final int KI_M = 24;
        public static final int KI_N = 25;
        public static final int KI_O = 26;
        public static final int KI_P = 27;
        public static final int KI_Q = 28;
        public static final int KI_R = 29;
        public static final int KI_S = 30;
        public static final int KI_T = 31;
        public static final int KI_U = 32;
        public static final int KI_V = 33;
        public static final int KI_W = 34;
        public static final int KI_X = 35;
        public static final int KI_Y = 36;
        public static final int KI_Z = 37;
        public static final int KI_OEM_1 = 38;
        public static final int KI_OEM_PLUS = 39;
        public static final int KI_OEM_COMMA = 40;
        public static final int KI_OEM_MINUS = 41;
        public static final int KI_OEM_PERIOD = 42;
        public static final int KI_OEM_2 = 43;
        public static final int KI_OEM_3 = 44;
        public static final int KI_OEM_4 = 45;
        public static final int KI_OEM_5 = 46;
        public static final int KI_OEM_6 = 47;
        public static final int KI_OEM_7 = 48;
        public static final int KI_OEM_8 = 49;
        public static final int KI_OEM_102 = 50;
        public static final int KI_NUMPAD0 = 51;
        public static final int KI_NUMPAD1 = 52;
        public static final int KI_NUMPAD2 = 53;
        public static final int KI_NUMPAD3 = 54;
        public static final int KI_NUMPAD4 = 55;
        public static final int KI_NUMPAD5 = 56;
        public static final int KI_NUMPAD6 = 57;
        public static final int KI_NUMPAD7 = 58;
        public static final int KI_NUMPAD8 = 59;
        public static final int KI_NUMPAD9 = 60;
        public static final int KI_NUMPADENTER = 61;
        public static final int KI_MULTIPLY = 62;
        public static final int KI_ADD = 63;
        public static final int KI_SEPARATOR = 64;
        public static final int KI_SUBTRACT = 65;
        public static final int KI_DECIMAL = 66;
        public static final int KI_DIVIDE = 67;
        public static final int KI_OEM_NEC_EQUAL = 68;
        public static final int KI_BACK = 69;
        public static final int KI_TAB = 70;
        public static final int KI_CLEAR = 71;
        public static final int KI_RETURN = 72;
        public static final int KI_PAUSE = 73;
        public static final int KI_CAPITAL = 74;
        public static final int KI_KANA = 75;
        public static final int KI_HANGUL = 76;
        public static final int KI_JUNJA = 77;
        public static final int KI_FINAL = 78;
        public static final int KI_HANJA = 79;
        public static final int KI_KANJI = 80;
        public static final int KI_ESCAPE = 81;
        public static final int KI_CONVERT = 82;
        public static final int KI_NONCONVERT = 83;
        public static final int KI_ACCEPT = 84;
        public static final int KI_MODECHANGE = 85;
        public static final int KI_PRIOR = 86;
        public static final int KI_NEXT = 87;
        public static final int KI_END = 88;
        public static final int KI_HOME = 89;
        public static final int KI_LEFT = 90;
        public static final int KI_UP = 91;
        public static final int KI_RIGHT = 92;
        public static final int KI_DOWN = 93;
        public static final int KI_SELECT = 94;
        public static final int KI_PRINT = 95;
        public static final int KI_EXECUTE = 96;
        public static final int KI_SNAPSHOT = 97;
        public static final int KI_INSERT = 98;
        public static final int KI_DELETE = 99;
        public static final int KI_HELP = 100;
        public static final int KI_LWIN = 101;
        public static final int KI_RWIN = 102;
        public static final int KI_APPS = 103;
        public static final int KI_POWER = 104;
        public static final int KI_SLEEP = 105;
        public static final int KI_WAKE = 106;
        public static final int KI_F1 = 107;
        public static final int KI_F2 = 108;
        public static final int KI_F3 = 109;
        public static final int KI_F4 = 110;
        public static final int KI_F5 = 111;
        public static final int KI_F6 = 112;
        public static final int KI_F7 = 113;
        public static final int KI_F8 = 114;
        public static final int KI_F9 = 115;
        public static final int KI_F10 = 116;
        public static final int KI_F11 = 117;
        public static final int KI_F12 = 118;
        public static final int KI_F13 = 119;
        public static final int KI_F14 = 120;
        public static final int KI_F15 = 121;
        public static final int KI_F16 = 122;
        public static final int KI_F17 = 123;
        public static final int KI_F18 = 124;
        public static final int KI_F19 = 125;
        public static final int KI_F20 = 126;
        public static final int KI_F21 = 127;
        public static final int KI_F22 = 128;
        public static final int KI_F23 = 129;
        public static final int KI_F24 = 130;
        public static final int KI_NUMLOCK = 131;
        public static final int KI_SCROLL = 132;
        public static final int KI_OEM_FJ_JISHO = 133;
        public static final int KI_OEM_FJ_MASSHOU = 134;
        public static final int KI_OEM_FJ_TOUROKU = 135;
        public static final int KI_OEM_FJ_LOYA = 136;
        public static final int KI_OEM_FJ_ROYA = 137;
        public static final int KI_LSHIFT = 138;
        public static final int KI_RSHIFT = 139;
        public static final int KI_LCONTROL = 140;
        public static final int KI_RCONTROL = 141;
        public static final int KI_LMENU = 142;
        public static final int KI_RMENU = 143;
        public static final int KI_BROWSER_BACK = 144;
        public static final int KI_BROWSER_FORWARD = 145;
        public static final int KI_BROWSER_REFRESH = 146;
        public static final int KI_BROWSER_STOP = 147;
        public static final int KI_BROWSER_SEARCH = 148;
        public static final int KI_BROWSER_FAVORITES = 149;
        public static final int KI_BROWSER_HOME = 150;
        public static final int KI_VOLUME_MUTE = 151;
        public static final int KI_VOLUME_DOWN = 152;
        public static final int KI_VOLUME_UP = 153;
        public static final int KI_MEDIA_NEXT_TRACK = 154;
        public static final int KI_MEDIA_PREV_TRACK = 155;
        public static final int KI_MEDIA_STOP = 156;
        public static final int KI_MEDIA_PLAY_PAUSE = 157;
        public static final int KI_LAUNCH_MAIL = 158;
        public static final int KI_LAUNCH_MEDIA_SELECT = 159;
        public static final int KI_LAUNCH_APP1 = 160;
        public static final int KI_LAUNCH_APP2 = 161;
        public static final int KI_OEM_AX = 162;
        public static final int KI_ICO_HELP = 163;
        public static final int KI_ICO_00 = 164;
        public static final int KI_PROCESSKEY = 165;
        public static final int KI_ICO_CLEAR = 166;
        public static final int KI_ATTN = 167;
        public static final int KI_CRSEL = 168;
        public static final int KI_EXSEL = 169;
        public static final int KI_EREOF = 170;
        public static final int KI_PLAY = 171;
        public static final int KI_ZOOM = 172;
        public static final int KI_PA1 = 173;
        public static final int KI_OEM_CLEAR = 174;
        public static final int KI_LMETA = 175;
        public static final int KI_RMETA = 176;
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/SlickToAndroidKeycodes$SlickCodes.class */
    public class SlickCodes {
        public static final int KEY_ESCAPE = 1;
        public static final int KEY_1 = 2;
        public static final int KEY_2 = 3;
        public static final int KEY_3 = 4;
        public static final int KEY_4 = 5;
        public static final int KEY_5 = 6;
        public static final int KEY_6 = 7;
        public static final int KEY_7 = 8;
        public static final int KEY_8 = 9;
        public static final int KEY_9 = 10;
        public static final int KEY_0 = 11;
        public static final int KEY_MINUS = 12;
        public static final int KEY_EQUALS = 13;
        public static final int KEY_BACK = 14;
        public static final int KEY_TAB = 15;
        public static final int KEY_Q = 16;
        public static final int KEY_W = 17;
        public static final int KEY_E = 18;
        public static final int KEY_R = 19;
        public static final int KEY_T = 20;
        public static final int KEY_Y = 21;
        public static final int KEY_U = 22;
        public static final int KEY_I = 23;
        public static final int KEY_O = 24;
        public static final int KEY_P = 25;
        public static final int KEY_LBRACKET = 26;
        public static final int KEY_RBRACKET = 27;
        public static final int KEY_LEFT_BRACKET = 26;
        public static final int KEY_RIGHT_BRACKET = 27;
        public static final int KEY_RETURN = 28;
        public static final int KEY_ENTER = 28;
        public static final int KEY_LCONTROL = 29;
        public static final int KEY_A = 30;
        public static final int KEY_S = 31;
        public static final int KEY_D = 32;
        public static final int KEY_F = 33;
        public static final int KEY_G = 34;
        public static final int KEY_H = 35;
        public static final int KEY_J = 36;
        public static final int KEY_K = 37;
        public static final int KEY_L = 38;
        public static final int KEY_SEMICOLON = 39;
        public static final int KEY_APOSTROPHE = 40;
        public static final int KEY_GRAVE = 41;
        public static final int KEY_LSHIFT = 42;
        public static final int KEY_BACKSLASH = 43;
        public static final int KEY_Z = 44;
        public static final int KEY_X = 45;
        public static final int KEY_C = 46;
        public static final int KEY_V = 47;
        public static final int KEY_B = 48;
        public static final int KEY_N = 49;
        public static final int KEY_M = 50;
        public static final int KEY_COMMA = 51;
        public static final int KEY_PERIOD = 52;
        public static final int KEY_SLASH = 53;
        public static final int KEY_RSHIFT = 54;
        public static final int KEY_MULTIPLY = 55;
        public static final int KEY_NUMPADMULTIPLY = 55;
        public static final int KEY_LMENU = 56;
        public static final int ALT_LEFT = 56;
        public static final int KEY_SPACE = 57;
        public static final int KEY_CAPITAL = 58;
        public static final int KEY_F1 = 59;
        public static final int KEY_F2 = 60;
        public static final int KEY_F3 = 61;
        public static final int KEY_F4 = 62;
        public static final int KEY_F5 = 63;
        public static final int KEY_F6 = 64;
        public static final int KEY_F7 = 65;
        public static final int KEY_F8 = 66;
        public static final int KEY_F9 = 67;
        public static final int KEY_F10 = 68;
        public static final int KEY_NUMLOCK = 69;
        public static final int KEY_SCROLL = 70;
        public static final int KEY_NUMPAD7 = 71;
        public static final int KEY_NUMPAD8 = 72;
        public static final int KEY_NUMPAD9 = 73;
        public static final int KEY_SUBTRACT = 74;
        public static final int KEY_NUMPADSUBTRACT = 74;
        public static final int KEY_NUMPAD4 = 75;
        public static final int KEY_NUMPAD5 = 76;
        public static final int KEY_NUMPAD6 = 77;
        public static final int KEY_ADD = 78;
        public static final int KEY_NUMPADADD = 78;
        public static final int KEY_NUMPAD1 = 79;
        public static final int KEY_NUMPAD2 = 80;
        public static final int KEY_NUMPAD3 = 81;
        public static final int KEY_NUMPAD0 = 82;
        public static final int KEY_DECIMAL = 83;
        public static final int KEY_NUMPADDECIMAL = 83;
        public static final int KEY_F11 = 87;
        public static final int KEY_F12 = 88;
        public static final int KEY_F13 = 100;
        public static final int KEY_F14 = 101;
        public static final int KEY_F15 = 102;
        public static final int KEY_KANA = 112;
        public static final int KEY_CONVERT = 121;
        public static final int KEY_NOCONVERT = 123;
        public static final int KEY_YEN = 125;
        public static final int KEY_NUMPADEQUALS = 141;
        public static final int KEY_CIRCUMFLEX = 144;
        public static final int KEY_AT = 145;
        public static final int KEY_COLON = 146;
        public static final int KEY_UNDERLINE = 147;
        public static final int KEY_KANJI = 148;
        public static final int KEY_STOP = 149;
        public static final int KEY_AX = 150;
        public static final int KEY_UNLABELED = 151;
        public static final int KEY_NUMPADENTER = 156;
        public static final int KEY_RCONTROL = 157;
        public static final int KEY_NUMPADCOMMA = 179;
        public static final int KEY_DIVIDE = 181;
        public static final int KEY_NUMPADDIVIDE = 181;
        public static final int KEY_SYSRQ = 183;
        public static final int KEY_RMENU = 184;
        public static final int ALT_RIGHT = 184;
        public static final int KEY_PAUSE = 197;
        public static final int KEY_BREAK = 197;
        public static final int KEY_HOME = 199;
        public static final int KEY_UP = 200;
        public static final int KEY_PRIOR = 201;
        public static final int KEY_PAGE_UP = 201;
        public static final int KEY_LEFT = 203;
        public static final int KEY_RIGHT = 205;
        public static final int KEY_END = 207;
        public static final int KEY_DOWN = 208;
        public static final int KEY_NEXT = 209;
        public static final int KEY_PAGE_DOWN = 209;
        public static final int KEY_INSERT = 210;
        public static final int KEY_DELETE = 211;
        public static final int KEY_LWIN = 219;
        public static final int KEY_RWIN = 220;
        public static final int KEY_APPS = 221;
        public static final int KEY_POWER = 222;
        public static final int KEY_SLEEP = 223;
        public static final int KEY_LALT = 56;
        public static final int KEY_RALT = 184;
    }

    /* JADX INFO: renamed from: a */
    static HashMap createKeyNameMap(Class cls) {
        HashMap map = new HashMap();
        for (Field field : cls.getFields()) {
            try {
                map.put(field.getName().replace("KEYCODE_", VariableScope.nullOrMissingString).replace("KEY_", VariableScope.nullOrMissingString).replace("NUMPAD_", "NUMPAD").replace("NUM_", "NUMPAD"), Integer.valueOf(field.getInt(null)));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e2) {
                throw new RuntimeException(e2);
            }
        }
        return map;
    }

    /* JADX INFO: renamed from: a */
    public static void initializeKeyCodeMaps() {
        slickToAndroid = buildKeyCodeConversionMap("slickToAndroidCodes", slickNameMap, androidNameMap);
        androidKeyCodeToNameMap = new HashMap();
        ArrayList arrayList = new ArrayList();
        for (String str : slickNameMap.keySet()) {
            int iIntValue = ((Integer) slickNameMap.get(str)).intValue();
            Integer num = (Integer) androidNameMap.get(str);
            if (num == null) {
                arrayList.add(str);
            }
            if (slickToAndroid.get(Integer.valueOf(iIntValue)) == null) {
            }
            String lowerCase = str.toLowerCase(Locale.ENGLISH);
            if (num != null) {
                androidKeyCodeToNameMap.put(num, lowerCase);
            }
        }
    }

    /* JADX INFO: renamed from: b */
    public static void initializeGdxToAndroidMap() {
        gdxToAndroidMap = buildKeyCodeConversionMap("gdxToAndroidCodes", gdxNameMap, androidNameMap);
    }

    /* JADX INFO: renamed from: c */
    public static void initializeGdxToSlickMap() {
        gdxToSlickMap = buildKeyCodeConversionMap("gdxToSlickCodes", gdxNameMap, slickNameMap);
    }

    /* JADX INFO: renamed from: a */

    public static HashMap buildKeyCodeConversionMap(String string, HashMap<String, Integer> hashMap2, HashMap hashMap3) {
        HashMap var3 = new HashMap();
        ArrayList<String> var4 = new ArrayList();

        for (String var6 : hashMap2.keySet()) {
            int var7 = (Integer) hashMap2.get(var6);
            Integer var8 = (Integer) hashMap3.get(var6);
            if (var8 == null) {
                var4.add(var6);
            }

            if (var3.get(var7) == null) {
                var3.put(var7, var8);
            }

            String var9 = var6.toLowerCase(Locale.ENGLISH);
            if (var8 != null) {
            }
        }

        if (var4.size() != 0) {
            String var10 = "";

            for (String var12 : var4) {
                int var13 = (Integer) hashMap2.get(var12);
                if (var3.get(var13) == null) {
                    var10 = var10 + var12 + ", ";
                }
            }

            GameEngine.log(string + ": Could not find keycode for: " + var10);
        }

        return var3;
    }

    static {
        initializeKeyCodeMaps();
        initializeGdxToAndroidMap();
        initializeGdxToSlickMap();
    }

    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/SlickToAndroidKeycodes$MissingKey.class */
    public static class MissingKey extends Exception {
        private static final long serialVersionUID = 1;

        public MissingKey(String str) {
            super(str);
        }
    }

    /* JADX INFO: renamed from: a */
    public static int getAndroidKeyCode(String str) throws MissingKey {
        String upperCase = str.toUpperCase();
        Integer num = (Integer) androidNameMap.get(upperCase);
        if (num == null) {
            throw new MissingKey("Could not find key:" + upperCase);
        }
        return num.intValue();
    }

    public static String a(int i) {
        String str = (String) androidKeyCodeToNameMap.get(Integer.valueOf(i));
        if (str == null) {
            return "unknown";
        }
        return str;
    }

    /* JADX INFO: renamed from: b */
    public static int convertSlickToAndroidKeyCode(int i) {
        Integer num = (Integer) slickToAndroid.get(Integer.valueOf(i));
        if (num == null) {
            return 0;
        }
        return num.intValue();
    }

    /* JADX INFO: renamed from: c */
    public static Integer convertSlickToGdxKeyCode(int i) {
        if (i == 14) {
            return 69;
        }
        if (i == 211) {
            return 99;
        }
        if (i == 28) {
            return 72;
        }
        if (i == 203) {
            return 90;
        }
        if (i == 205) {
            return 92;
        }
        if (i == 200) {
            return 91;
        }
        if (i == 208) {
            return 93;
        }
        if (i == 15) {
            return 70;
        }
        if (i == 42) {
            return 138;
        }
        if (i == 54) {
            return 139;
        }
        if (i == 29) {
            return 140;
        }
        return i == 157 ? 141 : null;
    }
}
