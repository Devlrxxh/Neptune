package dev.lrxh.neptune.providers.clickable;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ClickableUtils {

    public List<Object> returnMessage(String message, Replacement... replacements) {
        List<Object> objects = new ArrayList<>();
        objects.add(message);

        for (Replacement replacement : replacements) {
            String placeholder = replacement.getPlaceholder();
            List<Object> tempObjects = new ArrayList<>();

            for (Object obj : objects) {
                if (obj instanceof String) {
                    String string = (String) obj;
                    if (string.contains(placeholder)) {
                        int index = string.indexOf(placeholder);

                        String before = string.substring(0, index);
                        String after = string.substring(index + placeholder.length());

                        tempObjects.add(before);
                        tempObjects.add(replacement.getReplacement());
                        if (!after.isEmpty()) {
                            tempObjects.add(after);
                        }
                    } else {
                        tempObjects.add(obj);
                    }
                } else {
                    tempObjects.add(obj);
                }
            }
            objects.clear();
            objects.addAll(tempObjects);
        }

        return objects;
    }
}
