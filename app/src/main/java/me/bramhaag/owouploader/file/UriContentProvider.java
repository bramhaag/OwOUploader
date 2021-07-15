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

package me.bramhaag.owouploader.file;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.OpenableColumns;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * {@link ContentProvider} for Uris.
 */
public class UriContentProvider implements ContentProvider {

    private final ContentResolver resolver;
    private final Uri uri;

    private final String name;
    private final long size;
    private final String contentType;

    /**
     * Create a new UriContentProvider.
     *
     * @param context the context
     * @param uri     the uri
     */
    public UriContentProvider(Context context, Uri uri) {
        this.resolver = context.getContentResolver();
        this.uri = uri;

        try (var cursor = resolver.query(uri, null, null, null, null)) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);

            cursor.moveToFirst();

            this.name = cursor.getString(nameIndex);
            this.size = cursor.getLong(sizeIndex);
        }

        var typeString = resolver.getType(uri);
        this.contentType = typeString == null ? DEFAULT_CONTENT_TYPE : typeString;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public InputStream getContent() throws FileNotFoundException {
        return resolver.openInputStream(uri);
    }
}
