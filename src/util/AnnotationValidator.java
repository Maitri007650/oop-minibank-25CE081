package util;

import model.annotation.Id;
import model.annotation.Positive;
import model.annotation.MaxLength;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class AnnotationValidator {

    public static String[] validate(Object obj) {
        ArrayList<String> errors = new ArrayList<>();

        Class<?> type = obj.getClass();

        while (type != null) {
            for (Field field : type.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(obj);

                    if (field.isAnnotationPresent(Positive.class)
                            && value instanceof Number) {
                        Positive rule = field.getAnnotation(Positive.class);

                        if (((Number) value).doubleValue() <= 0) {
                            errors.add(field.getName() + " "
                                    + rule.message());
                        }
                    }

                    if (field.isAnnotationPresent(MaxLength.class)
                            && value instanceof String) {
                        MaxLength rule = field.getAnnotation(MaxLength.class);

                        if (((String) value).length() > rule.value()) {
                            errors.add(field.getName()
                                    + " exceeds maximum length "
                                    + rule.value());
                        }
                    }

                } catch (IllegalAccessException e) {
                    errors.add("Could not validate " + field.getName());
                }
            }

            type = type.getSuperclass();
        }

        return errors.toArray(new String[0]);
    }
}