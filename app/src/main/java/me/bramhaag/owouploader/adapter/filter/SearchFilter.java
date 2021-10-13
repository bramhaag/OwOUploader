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

package me.bramhaag.owouploader.adapter.filter;

import me.bramhaag.owouploader.adapter.item.AssociatedItem;
import me.bramhaag.owouploader.adapter.item.LoadingItem;
import me.bramhaag.owouploader.adapter.item.ProgressItem;
import me.bramhaag.owouploader.adapter.item.ViewHolderItem;
import me.bramhaag.owouploader.db.entity.ShortenItem;
import me.bramhaag.owouploader.db.entity.UploadItem;

public class SearchFilter implements Filter<ViewHolderItem, String> {

    @Override
    public boolean accept(ViewHolderItem item, final String constraint) {
        if (constraint == null || constraint.trim().isEmpty()) {
            return true;
        }

        if (item instanceof ProgressItem || item instanceof LoadingItem) {
            return true;
        }

        if (item instanceof AssociatedItem) {
            item = ((AssociatedItem) item).getItem();
        }

        if (item instanceof UploadItem) {
            return ((UploadItem) item).name.toLowerCase().contains(constraint)
                    || ((UploadItem) item).url.toString().contains(constraint);
        } else if (item instanceof ShortenItem) {
            return ((ShortenItem) item).url().contains(constraint);
        }

        return false;
    }
}
