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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.bramhaag.owouploader.api.model.UserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserModelDeserializerTest {

    private Gson gson;

    @BeforeEach
    void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapter(UserModel.class, new UserModelDeserializer())
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    @Test
    void deserialize_valid() {
        //language=JSON
        var input = "{\n"
                + "  \"success\": true,\n"
                + "  \"errorcode\": null,\n"
                + "  \"user\": {\n"
                + "    \"user_id\": \"a588874e-b12e-4ece-9b6d-fde185252613\",\n"
                + "    \"username\": \"test-user\",\n"
                + "    \"email\": \"test@email.com\",\n"
                + "    \"is_admin\": true,\n"
                + "    \"is_blocked\": false\n"
                + "  }\n"
                + "}";

        var result = gson.fromJson(input, UserModel.class);
        assertEquals("a588874e-b12e-4ece-9b6d-fde185252613", result.getUserId());
        assertEquals("test-user", result.getUsername());
        assertEquals("test@email.com", result.getEmail());
        assertTrue(result.isAdmin());
        assertFalse(result.isBlocked());
    }

    @Test
    void deserialize_invalid() {
        //language=JSON
        var input = "{\n"
                + "  \"success\": false\n"
                + "}";

        var result = gson.fromJson(input, UserModel.class);
        assertNull(result);
    }
}