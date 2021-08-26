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

package me.bramhaag.owouploader.adapter.viewholder.item;

import java.time.Instant;
import me.bramhaag.owouploader.db.entity.HistoryItem;

public class AssociatedItem implements HistoryItem {

    private HistoryItem item;

    public AssociatedItem(HistoryItem item) {
        this.item = item;
    }

    public HistoryItem getItem() {
        return item;
    }

    public void setItem(HistoryItem item) {
        this.item = item;
    }

    @Override
    public String key() {
        return item.key();
    }

    @Override
    public Instant createdAt() {
        return item.createdAt();
    }
}
