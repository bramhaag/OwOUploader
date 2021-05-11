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
public class UploadHistoryItem implements HistoryItem {

    private final String name;
    private final URI url;
    private final Date date;

    /**
     * Instantiate a new {@link UploadHistoryItem}.
     *
     * @param name the file's name
     * @param url  the file's url
     * @param date the upload date
     */
    public UploadHistoryItem(String name, URI url, Date date) {
        this.name = name;
        this.url = url;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public URI getUrl() {
        return url;
    }

    public Date getDate() {
        return date;
    }
}
