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

package me.bramhaag.owouploader.db.converters;

import androidx.room.TypeConverter;
import java.net.URI;

/**
 * Type converters for {@link URI}s.
 */
public class URIConverter {

    @TypeConverter
    public static URI toURI(String value) {
        return value == null ? null : URI.create(value);
    }

    @TypeConverter
    public static String toString(URI uri) {
        return uri == null ? null : uri.toString();
    }

}
