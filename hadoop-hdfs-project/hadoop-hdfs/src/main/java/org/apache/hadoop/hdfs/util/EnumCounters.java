/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.util;

import java.util.HashMap;

import com.google.common.base.Preconditions;

/**
 * Counters for an enum type.
 * 
 * For example, suppose there is an enum type
 * <pre>
 * enum Fruit { APPLE, ORANGE, GRAPE }
 * </pre>
 * An {@link EnumCounters} object can be created for counting the numbers of
 * APPLE, ORANGLE and GRAPE.
 *
 * @param <E> the enum type
 */
public class EnumCounters<E extends Enum<E>> {
  /** An array of enum constants. */
  private final E[] enumConstants;
  /** The counter array, counters[i] corresponds to the enumConstants[i]. */
  private final long[] counters;

  /**
   * Construct counters for the given enum constants.
   * @param enumConstants an array of enum constants such that, 
   *                      for all i, enumConstants[i].ordinal() == i.
   */
  public EnumCounters(final E[] enumConstants) {
    for(int i = 0; i < enumConstants.length; i++) {
      Preconditions.checkArgument(enumConstants[i].ordinal() == i);
    }
    this.enumConstants = enumConstants;
    this.counters = new long[enumConstants.length];
  }
  
  /** @return the value of counter e. */
  public final long get(final E e) {
    return counters[e.ordinal()];
  }

  /** Negate all counters. */
  public final void negation() {
    for(int i = 0; i < counters.length; i++) {
      counters[i] = -counters[i];
    }
  }
  
  /** Set counter e to the given value. */
  public final void set(final E e, final long value) {
    counters[e.ordinal()] = value;
  }

  /** Add the given value to counter e. */
  public final void add(final E e, final long value) {
    counters[e.ordinal()] += value;
  }

  /** Add that counters to this counters. */
  public final void add(final EnumCounters<E> that) {
    for(int i = 0; i < counters.length; i++) {
      this.counters[i] += that.counters[i];
    }
  }

  /** Subtract the given value from counter e. */
  public final void subtract(final E e, final long value) {
    counters[e.ordinal()] -= value;
  }

  /** Subtract that counters from this counters. */
  public final void subtract(final EnumCounters<E> that) {
    for(int i = 0; i < counters.length; i++) {
      this.counters[i] -= that.counters[i];
    }
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    for(int i = 0; i < counters.length; i++) {
      final String name = enumConstants[i].name();
      b.append(name).append("=").append(counters[i]).append(", ");
    }
    return b.substring(0, b.length() - 2);
  }

  /**
   * A factory for creating counters.
   * 
   * @param <E> the enum type
   * @param <C> the counter type
   */
  public static interface Factory<E extends Enum<E>,
                                  C extends EnumCounters<E>> {
    /** Create a new counters instance. */
    public C newInstance(); 
  }

  /**
   * A key-value map which maps the keys to {@link EnumCounters}.
   * Note that null key is supported.
   *
   * @param <K> the key type
   * @param <E> the enum type
   * @param <C> the counter type
   */
  public static class Map<K, E extends Enum<E>, C extends EnumCounters<E>> {
    /** The factory for creating counters. */
    private final Factory<E, C> factory;
    /** Key-to-Counts map. */
    private final java.util.Map<K, C> counts = new HashMap<K, C>();
    
    /** Construct a map. */
    public Map(final Factory<E, C> factory) {
      this.factory = factory;
    }

    /** @return the counters for the given key. */
    public final C getCounts(final K key) {
      C c = counts.get(key);
      if (c == null) {
        c = factory.newInstance();
        counts.put(key, c); 
      }
      return c;
    }
    
    /** @return the sum of the values of all the counters. */
    public final C sum() {
      final C sum = factory.newInstance();
      for(C c : counts.values()) {
        sum.add(c);
      }
      return sum;
    }
    
    /** @return the sum of the values of all the counters for e. */
    public final long sum(final E e) {
      long sum = 0;
      for(C c : counts.values()) {
        sum += c.get(e);
      }
      return sum;
    }

    @Override
    public String toString() {
      return counts.toString();
    }
  }
}
