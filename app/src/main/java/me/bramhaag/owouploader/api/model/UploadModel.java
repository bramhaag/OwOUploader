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

package me.bramhaag.owouploader.api.model;

import androidx.annotation.NonNull;

/**
 * Stores the result of a call to the {@code upload} endpoint.
 */
public class UploadModel {

    private String hash;
    private String name;
    private String url;
    private long size;

    public String getHash() {
        return hash;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public long getSize() {
        return size;
    }

    @Override
    @NonNull
    public String toString() {
        return "UploadModel{"
                + "hash='" + hash + '\''
                + ", name='" + name + '\''
                + ", url='" + url + '\''
                + ", size=" + size
                + '}';
    }
}
