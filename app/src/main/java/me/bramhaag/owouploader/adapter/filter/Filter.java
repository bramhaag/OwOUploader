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

@FunctionalInterface
public interface Filter<T, U> {

    boolean accept(T value, U constraint);

    default Filter<T, U> and(Filter<T, U> filter) {
        return (v, c) -> Filter.this.accept(v, c) && filter.accept(v, c);
    }

    default Filter<T, U> or(Filter<T, U> filter) {
        return (v, c) -> Filter.this.accept(v, c) || filter.accept(v, c);
    }
}
