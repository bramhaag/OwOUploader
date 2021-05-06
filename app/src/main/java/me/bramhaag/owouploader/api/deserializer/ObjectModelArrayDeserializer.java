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

package me.bramhaag.owouploader.api.deserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.bramhaag.owouploader.api.model.ObjectModel;

/**
 * Custom {@link com.google.gson.JsonDeserializer} to deserialize {@link ObjectModel}[]s.
 */
public class ObjectModelArrayDeserializer extends ResponseDeserializer<ObjectModel[]> {

    @Override
    ObjectModel[] deserialize(JsonObject jsonObject) {
        var data = jsonObject.get("data").getAsJsonArray();
        var result = new ObjectModel[data.size()];

        int i = 0;
        for (JsonElement object : data) {
            result[i++] = GSON.fromJson(object, ObjectModel.class);
        }

        return result;
    }
}

