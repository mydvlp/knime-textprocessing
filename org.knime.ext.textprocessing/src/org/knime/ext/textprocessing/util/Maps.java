/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright, 2003 - 2008
 * University of Konstanz, Germany
 * and KNIME GmbH, Konstanz, Germany
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.org
 * email: contact@knime.org
 * ---------------------------------------------------------------------
 *
 * History
 *   Jun 12, 2008 (Pierre-Francois Laquerre, University of Konstanz): created
 */
package org.knime.ext.textprocessing.util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Provides utility methods for maps.
 *
 * @author Pierre-Francois Laquerre, University of Konstanz
 */
public class Maps {

    /**
     * Returns the min(nbvalues, source.size()) top entries, ordered in
     * descending order of their value.
     *
     * @param <K> key type
     * @param <V> value type
     * @param source the map from which the values are to be extracted
     * @param n the maximum number of values to return
     *
     * The amount of items returned will be less than nbvalues if the source map
     * does not contain enough entries.
     *
     * @return a new map containing the top min(n, source.size()) entries.
     */
    @SuppressWarnings("unchecked")
    public static <K, V extends Comparable<V>> Map<K, V> getTopValues(final Map<K, V> source,
            final int n) {
        Map<K, V> topvalues = new LinkedHashMap<K, V>();
        int nbval = n > source.size() ? source.size() : n;

        Entry<K, V>[] entryset = new Entry[source.size()];
        source.entrySet().toArray(entryset);

        java.util.Arrays.sort(entryset, new ValueComparator());

        int i = source.size() - 1;
        int lowerbound = source.size() - nbval; // inclusive lower bound

        while (i >= lowerbound) {
            topvalues.put(entryset[i].getKey(), entryset[i].getValue());
            --i;
        }

        return topvalues;
    }

    /**
     * Compares entries based on their value.
     *
     * @author Pierre-Francois Laquerre, University of Konstanz
     */
    @SuppressWarnings("unchecked")
    private static class ValueComparator implements
            Comparator<Entry<?, ? extends Comparable>> {

        /**
         * {@inheritDoc}
         */
        public int compare(final Entry<?, ? extends Comparable> e1,
                final Entry<?, ? extends Comparable> e2) {
            return e1.getValue().compareTo(e2.getValue());
        }
    }

    /**
     * Only keeps elements in 'map' which matches a given condition.
     *
     * @param <M> the type of the map
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to modify
     * @param cond the condition that must be met for an element to be kept
     */
    public static <M extends Map<K,V>, K, V> void filter(
            final M map, final Condition<Entry<K,V>> cond) {
        Iterator<Entry<K, V>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry<K, V> e = it.next();

            if (!cond.test(e)) {
                it.remove();
            }
        }
    }
}