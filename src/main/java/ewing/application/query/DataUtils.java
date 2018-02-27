package ewing.application.query;

import org.springframework.beans.BeanUtils;

import java.util.Collection;

public class DataUtils {

    private DataUtils() {
    }

    public static <T, C extends Collection<T>> T getMaxOne(C collection) {
        if (collection == null || collection.size() < 1) {
            return null;
        } else if (collection.size() == 1) {
            return collection.iterator().next();
        } else {
            throw new RuntimeException("Elements more than one.");
        }
    }

    public static <T> T copyProperties(Object source, T target) {
        BeanUtils.copyProperties(source, target);
        return target;
    }

    public static <E, C extends Collection<E>, T> T getMaxOneAndCopy(C collection, T target) {
        if (collection == null || target == null) {
            return null;
        }
        return copyProperties(getMaxOne(collection), target);
    }

}
