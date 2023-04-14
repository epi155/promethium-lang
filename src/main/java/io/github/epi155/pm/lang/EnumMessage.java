package io.github.epi155.pm.lang;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

enum EnumMessage implements CustMsg {
    NIL_ARG,
    ILL_ARG,
    NIL_BLD,
    ERR_BLD,
    OVR_BLD,
    DBL_SET,

    ;

    private final String code;
    private final String pattern;
    private final int status;

    EnumMessage() {
        String label = name().toLowerCase().replace('_', '-');
        ResourceBundle bundle = ResourceBundle.getBundle("pm-messages");
        this.code = bundle.getString(label + ".code");
        this.pattern = bundle.getString(label + ".pattern");
        this.status = getInt(bundle, label + ".status", 500);
    }

    private int getInt(ResourceBundle bundle, String key, int defaultValue) {
        try {
            return Integer.decode(bundle.getString(key));
        } catch (MissingResourceException | NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message(Object[] objects) {
        return PmFormatter.format(pattern, objects);
    }

    @Override
    public int statusCode() {
        return status;
    }
}
