package andoird.jonas.fakestandby.utils;

public class Constants {

    public static final class Preferences {
        public static final String IS_ACTIVE_NOW = "overlay_is_active_now";
        public static final String PREFERENCE_NAME = "default_preference";
    }

    public static final class Intent {
        public static final class Extra {
            public static final class OverlayAction {
                public static final String KEY = "overlay_action";
                public static final byte SHOW = 0;
                public static final byte HIDE = 1;
                public static final byte NOTHING = -1;
                public static final byte DEFAULT = NOTHING;
            }
        }
    }

    public static final class Notification {
        public static final int ID = 647832961;
    }

}
