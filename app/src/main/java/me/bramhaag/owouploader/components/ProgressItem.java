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

package me.bramhaag.owouploader.components;

import java.net.URI;
import java.util.Date;

/**
 * Data class for shorten history items.
 */
public class ProgressItem implements HistoryItem {

    private final String name;
    private long uploaded;
    private final long size;

    public ProgressItem(String name, long uploaded, long size) {
        this.name = name;
        this.uploaded = uploaded;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public long getUploaded() {
        return uploaded;
    }

    public void setUploaded(long uploaded) {
        this.uploaded = uploaded;
    }

    public long getSize() {
        return size;
    }
}
