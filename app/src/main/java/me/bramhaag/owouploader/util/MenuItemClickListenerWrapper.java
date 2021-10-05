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

import android.view.MenuItem;
import androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener;
import java.util.HashMap;
import java.util.function.Consumer;

public class MenuItemClickListenerWrapper implements OnMenuItemClickListener {

    private final HashMap<Integer, Consumer<MenuItem>> handlers = new HashMap<>();

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        var handler = this.handlers.get(item.getItemId());

        if (handler == null) {
            return false;
        }

        handler.accept(item);
        return true;
    }

    public MenuItemClickListenerWrapper on(int id, Consumer<MenuItem> consumer) {
        this.handlers.put(id, consumer);

        return this;
    }

    public MenuItemClickListenerWrapper on(int id, Runnable runnable) {
        return on(id, ignored -> runnable.run());
    }

}
