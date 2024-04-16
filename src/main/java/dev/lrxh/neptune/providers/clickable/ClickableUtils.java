package dev.lrxh.neptune.providers.clickable;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ClickableUtils {

    public List<Object> returnMessage(String message, Replacement... replacements) {
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(message);

        for (Replacement replacement : replacements) {
            String placeholder = replacement.getPlaceholder();
            ArrayList<Object> tempObjects = new ArrayList<>();

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
                } else if (obj instanceof List) {
                    List<?> list = (List<?>) obj;
                    System.out.println("Added list: " + list);
                    tempObjects.addAll(list);
                } else {
                    tempObjects.add(obj);
                }
            }
            objects.clear();
            objects.addAll(tempObjects);
        }

        System.out.println(objects);
        return objects;
    }
}