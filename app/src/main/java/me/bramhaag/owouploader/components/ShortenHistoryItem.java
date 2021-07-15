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
public class ShortenHistoryItem implements HistoryItem {

    private final URI originalUrl;
    private final URI shortenedUrl;
    private final Date date;

    /**
     * Instantiate a new {@link ShortenHistoryItem}.
     *
     * @param originalUrl  the original url
     * @param shortenedUrl the shortened url
     * @param date         the data
     */
    public ShortenHistoryItem(URI originalUrl, URI shortenedUrl, Date date) {
        this.originalUrl = originalUrl;
        this.shortenedUrl = shortenedUrl;
        this.date = date;
    }

    public URI getOriginalUrl() {
        return originalUrl;
    }

    public URI getShortenedUrl() {
        return shortenedUrl;
    }

    public Date getDate() {
        return date;
    }
}
