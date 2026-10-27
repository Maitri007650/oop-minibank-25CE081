
package service;

import model.annotation.Id;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class Repository<T> {

    private final HashMap<String, T> items = new HashMap<>();

    public T save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        try {
            Class<?> type = entity.getClass();

            while (type != null) {
                for (Field field : type.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Id.class)) {
                        field.setAccessible(true);
                        String id = String.valueOf(field.get(entity));
                        items.put(id, entity);
                        return entity;
                    }
                }
                type = type.getSuperclass();
            }

            throw new IllegalArgumentException(
                    "No field marked with @Id found");

        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not read entity ID", e);
        }
    }

    @SafeVarargs
    public final void saveAll(T... entities) {
        for (T entity : entities) {
            save(entity);
        }
    }

    public T findById(String id) {
        return items.get(id);
    }

    public ArrayList<T> findAll() {
        return new ArrayList<>(items.values());
    }

    public boolean delete(String id) {
        return items.remove(id) != null;
    }

    public static <N extends Number> double average(N[] values) {
        if (values == null || values.length == 0) {
            return 0.0;
        }

        double total = 0;

        for (N value : values) {
            total += value.doubleValue();
        }

        return total / values.length;
    }
}
