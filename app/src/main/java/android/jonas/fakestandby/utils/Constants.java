package android.jonas.fakestandby.utils;

public class Constants {

    public static final class Preferences {
        public static final String IS_SERVICE_RUNNING = "is_accessibility_service_running_now"; // Key of the preference that stores weather the accessibility service is currently running
        public static final String PREFERENCE_NAME = "default_preference"; // Name of the default preference to use.
        public static final String FIRST_OPEN = "is_app_opened_for_first_time"; // Key of the preference that stores weather the app is opened for the first time
        public static final String IS_OVERLAY_SHOWING = "is_overlay_showing"; // Key of the preference that stores weather the overlay is currently active
        public static final String START_OVERLAY_ON_SERVICE_START = "start_overlay_on_service_start"; // Stores true, if the service for the overlay should immediately start the overlay after start
    }

    public static final class Intent {
        public static final class Extra {
            public static final class OverlayAction {
                public static final String KEY = "overlay_action"; // The key for the extra of any intent that is used to control the overlay.
                public static final byte SHOW = 1; // Value of the extra if one wants to show the overlay.
                public static final byte HIDE = 0; // Value of the extra if one wants to hide the overlay.
                public static final byte HIDE_IMMEDIATELY = 2; // Value of the extra if one wants to hide the overlay without blending.
                public static final byte NOTHING = -1; // Value of the extra if nothing should happen. Just to have a default.
                public static final byte DEFAULT = NOTHING;
                public static final byte SHOW_NOTIFICATION = 3;
                public static final byte HIDE_NOTIFICATION = 4;
            }
        }
    }

    public static final class Notification {
        public static final int ID = 647832961; // Just some random ID for the notification that is displayed when the device does not support QuickTiles.
        public static final String CHANNEL_ID = "default"; // Just some random ID for the notification channel.
    }

    public static final class Overlay {
        public static String getStateName(byte state) {
            switch (state) {
                case State.INITIALIZING: return "INITIALIZING";
                case State.INITIALIZED: return "INITIALIZED";
                case State.ADDED: return "ADDED";
                case State.SHOWING: return "SHOWING";
                case State.VISIBLE: return "VISIBLE";
                case State.DRAGGING: return "DRAGGING";
                case State.FALLING: return "FALLING";
                case State.HIDING: return "HIDING";
                case State.HIDDEN: return "HIDDEN";
                case State.REMOVED: return "REMOVED";
                default: return "UNSET";
            }
        }

        public static final class State {
            public static final byte UNSET = -1; // Set to as default and never entered again after the overlay was initialized successfully.
            public static final byte INITIALIZING = 1; // Set to while initializing the view component for the overlay.
            public static final byte INITIALIZED = 2; // Set to when the overlay is ready.
            public static final byte ADDED = 3; // Set to after the overlay view component was added to the screen.
            public static final byte SHOWING = 4; // Set to while the view is blending from transparent to black.
            public static final byte VISIBLE = 5; // Set to when the overlay is visible but the user is not interacting with it.
            public static final byte DRAGGING = 6; // Set to as soon as the user taps on the touch screen and while he is dragging.
            public static final byte FALLING = 7; // Set to when the user releases the touch screen but did not swipe upwards to hide the overlay. Remains while the falling down animation is performed.
            public static final byte HIDING = 8; // Set to when the user releases the touch screen and did swipe upwards to hide the overlay. Remains while the hiding animation is performed
            public static final byte HIDDEN = 9; // Set to when the overlay is invisible to the user but still there.
            public static final byte REMOVED = 10; // Set to when the overlay view component was removed from screen.
                                                    // From here it jumps back to "ADDED" as soon as the service gets an intent to show the overlay again.
        }
    }

}
