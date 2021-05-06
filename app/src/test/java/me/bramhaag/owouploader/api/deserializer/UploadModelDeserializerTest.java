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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.bramhaag.owouploader.api.model.UploadModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UploadModelDeserializerTest {

    private Gson gson;

    @BeforeEach
    void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapter(UploadModel.class, new UploadModelDeserializer())
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    @Test
    void deserialize_valid() {
        //language=JSON
        var input = "{\n"
                + "  \"success\": true,\n"
                + "  \"files\": [\n"
                + "    {\n"
                + "      \"success\": true,\n"
                + "      \"hash\": \"9f76df5b93647c39fe5ec62dde46ed6b\",\n"
                + "      \"name\": \"balloon cat.jpg\",\n"
                + "      \"url\": \"4f23PwD.jpg\",\n"
                + "      \"size\": 468048\n"
                + "    }\n"
                + "  ]\n"
                + "}";

        var result = gson.fromJson(input, UploadModel.class);
        assertEquals("9f76df5b93647c39fe5ec62dde46ed6b", result.getHash());
        assertEquals("balloon cat.jpg", result.getName());
        assertEquals("4f23PwD.jpg", result.getUrl());
        assertEquals(468048, result.getSize());
    }

    @Test
    void deserialize_invalid() {
        //language=JSON
        var input = "{\n"
                + "  \"success\": false\n"
                + "}";

        var result = gson.fromJson(input, UploadModel.class);
        assertNull(result);
    }
}