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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ByteUnitTest {

    @Test
    void to() {
        assertEquals(ByteUnit.GIGA_BYTE.getPrefix(), ByteUnit.GIGA_BYTE.to(1, ByteUnit.BYTE));
        assertEquals(ByteUnit.MEGA_BYTE.getPrefix(), ByteUnit.MEGA_BYTE.to(1, ByteUnit.BYTE));
        assertEquals(ByteUnit.KILO_BYTE.getPrefix(), ByteUnit.KILO_BYTE.to(1, ByteUnit.BYTE));
        assertEquals(ByteUnit.BYTE.getPrefix(), ByteUnit.BYTE.to(1, ByteUnit.BYTE));
    }

    @Test
    void getPreferredUnit() {
        for (double i = 0; i < 1e3; i++) {
            assertEquals(ByteUnit.BYTE, ByteUnit.getPreferredUnit(i));
        }

        for (double i = 1e3; i < 1e6; i += 10) {
            assertEquals(ByteUnit.KILO_BYTE, ByteUnit.getPreferredUnit(i));
        }

        for (double i = 1e6; i < 1e9; i += 100) {
            assertEquals(ByteUnit.MEGA_BYTE, ByteUnit.getPreferredUnit(i));
        }

        for (double i = 1e9; i < 1e12; i += 1000) {
            assertEquals(ByteUnit.GIGA_BYTE, ByteUnit.getPreferredUnit(i));
        }
    }
}