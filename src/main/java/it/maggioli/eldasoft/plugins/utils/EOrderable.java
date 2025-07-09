package it.maggioli.eldasoft.plugins.utils;

import java.io.Serializable;
import java.util.Comparator;
import java.util.function.Function;

/**
 * The EOrderable enum is used to represent the order of sorting for a collection of objects.<br/>
 * It provides methods to obtain a comparator for ordering objects in ascending or descending order.<br/>
 * <br/>
 * <strong>Note:</strong><br/>
 * Null are sorted to the end.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public enum EOrderable implements Serializable {
    /**
     * The DESC variable is an enum of the EOrderable class.<br/>
     * It is used to represent an descending order.<br/>
     * <br/>
     * DESC is a static variable initialized with the result of the order_desc() and order_generic_desc() methods.<br/>
     * <br/>
     * The order_desc() method returns a Comparator object that compares Comparable objects in descending order. If the second object (o2) is null, it returns 0. If the first object (o1) is null, it returns -1. Otherwise, it compares the objects using the compareTo() method and returns the result.<br/>
     * <br/>
     * The order_generic_desc() method returns a Function object that takes a Function object as a parameter and returns a Comparator object. This Comparator object compares two objects of type T using the provided condition. If the first object (o2) is null, it returns 0. If the first object (o1) is null, it returns -1. Otherwise, it applies the condition to the objects and returns the result.<br/>
     * <br/>
     * Example usage:<br/>
     * <pre>
     * Comparator<Comparable> descComparator = EOrderable.DESC.getComparator();
     * Comparator<String> genericDescComp = EOrderable.DESC.genericComparator(EspletGaraOperatoreType::getPunteggioTecnico);
     * </pre>
     */
    DESC(order_desc(), order_generic_desc())
    ,
    /**
     * The ASC variable is an enum of the EOrderable class.<br/>
     * It is used to represent an ascending order.<br/>
     * <br/>
     * The ASC class is responsible for defining the order of elements in ascending order.<br/>
     * It provides two methods for ordering: order_asc() and order_generic_asc().<br/>
     * <br/>
     * The order_asc() method returns an ascending order based on the natural ordering of the elements.<br/>
     * <br/>
     * The order_generic_asc() method returns an ascending order based on a generic comparator.<br/>
     * <br/>
     * Example usage:<br/>
     * <pre>
     * Comparator<Comparable> descComparator = EOrderable.ASC.getComparator();
     * Comparator<String> genericDescComp = EOrderable.ASC.genericComparator(EspletGaraOperatoreType::getPunteggioTecnico);
     * </pre>
     */
    ASC(order_asc(), order_generic_asc());

    private final Comparator<Comparable> simpleComparator;
    private final Function fieldComparator;

    EOrderable(final Comparator<Comparable> simpleComparator, final Function fieldComparator) {
        this.simpleComparator = simpleComparator;
        this.fieldComparator = fieldComparator;
    }

    public Comparator<Comparable> getSimpleComparator() {
        return simpleComparator;
    }

    /**
     * Returns a comparator based on a given getter function.
     *
     * @param <T> the type of objects being compared
     * @param getter the function to retrieve the value to compare
     * @return a comparator that compares objects based on the values extracted by the getter function
     */
    public <T> Comparator<T> fieldComparator(final Function<T, Comparable> getter) {
        return (Comparator<T>) fieldComparator.apply(getter);
    }

    /**
     * Creates a comparator that orders objects in descending order.
     *
     * @return the comparator that orders objects in descending order.
     */
    private static Comparator<Comparable> order_desc() {
        return (o1, o2) -> {
            if (o1 == null) return 0;
            else if (o2 == null) return -1;
            else return o2.compareTo(o1);
        };
    }

    /**
     * Returns a comparator that orders objects in ascending order.
     *
     * @return a comparator that orders objects in ascending order
     */
    private static Comparator<Comparable> order_asc() {
        return (o1, o2) -> {
            if (o1 == null) return 0;
            else if (o2 == null) return -1;
            else return o1.compareTo(o2);
        };
    }

    /**
     * Returns a function that generates a comparator for ordering objects in descending order based on a specified criteria.
     *
     * @param <T> the type of the objects being compared
     * @return a function that generates a comparator
     */
    private static <T> Function<Function<T, Comparable>, Comparator<T>> order_generic_desc() {
        return (getter) -> (Comparator<T>) (o1, o2) -> {
            if (o1 == null) return 0;
            else if (o2== null) return -1;
            else if (getter.apply(o1) == null) return 0;
            else if (getter.apply(o2) == null) return -1;
            else return getter.apply(o2).compareTo(getter.apply(o1));
        };
    }

    /**
     * Creates a Comparator to order objects in ascending order based on a generic property.
     * The Comparator is created using a getter function that extracts the property value from each object.
     *
     * @param <T> the type of the object
     * @return a Function that accepts a getter function and returns a Comparator
     */
    private static <T> Function<Function<T, Comparable>, Comparator<T>> order_generic_asc() {
        return (getter) -> (Comparator<T>) (o1, o2) -> {
            if (o1 == null) return 0;
            else if (o2 == null) return -1;
            else if (getter.apply(o1) == null) return 0;
            else if (getter.apply(o2) == null) return -1;
            else return getter.apply(o1).compareTo(getter.apply(o2));
        };
    }

}
