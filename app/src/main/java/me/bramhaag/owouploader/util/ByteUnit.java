/*
 * OwO Uploader
 * Copyright (C) 2021
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.bramhaag.owouploader.util;

import java.util.Arrays;
import java.util.TreeMap;
import java.util.stream.Collectors;

public enum ByteUnit {
    BYTE("B", 1),
    KILO_BYTE("kB", 1e3),
    MEGA_BYTE("MB", 1e6),
    GIGA_BYTE("GB", 1e9);

    private static final TreeMap<Double, ByteUnit> UNIT_PREFIX = new TreeMap<>(
            Arrays.stream(values()).collect(Collectors.toMap(v -> v.prefix, v -> v)));

    private final String unit;
    private final double prefix;

    ByteUnit(String unit, double prefix) {
        this.unit = unit;
        this.prefix = prefix;
    }

    public double to(double value, ByteUnit unit) {
        return (value * this.prefix) / unit.getPrefix();
    }

    public double getPrefix() {
        return prefix;
    }

    public String getUnit() {
        return unit;
    }

    public static ByteUnit getPreferredUnit(double value) {
        var entry = UNIT_PREFIX.floorEntry(value);
        return entry == null ? BYTE : entry.getValue();
    }
}
