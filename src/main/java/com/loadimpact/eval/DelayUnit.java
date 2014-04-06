package com.loadimpact.eval;

import com.loadimpact.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit for threshold delay.
 *
 * @author jens
 */
public enum DelayUnit {
    seconds, users;

    public final String label;

    DelayUnit() {
        label = StringUtils.toInitialCase(name());
    }

    public static List<String> names() {
        DelayUnit[] units = values();
        List<String> result = new ArrayList<String>(units.length);
        for (int i = 0; i < units.length; i++) {
            result.add(units[i].name());
        }
        return result;
    }
    
    public String getId() {
        return name();
    }
    
    public String getDisplayName() {
        return label;
    }
    
}
