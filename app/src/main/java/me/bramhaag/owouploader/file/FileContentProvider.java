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

import android.webkit.MimeTypeMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class FileContentProvider implements ContentProvider {

    private final File file;

    public FileContentProvider(File file) {
        this.file = file;
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public String getContentType() {
        return Optional.of(MimeTypeMap.getFileExtensionFromUrl(file.getName()))
                .map(extension -> MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension))
                .orElse(DEFAULT_CONTENT_TYPE);
    }

    @Override
    public long getSize() {
        return file.length();
    }

    @Override
    public InputStream getContent() throws IOException {
        return new FileInputStream(file);
    }
}
