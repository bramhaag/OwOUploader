/*
 * OwO Uploader
 * Copyright (C) 2022
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

package me.bramhaag.owouploader.fragment;

import android.os.Bundle;
import android.widget.Toast;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import io.sentry.Sentry;
import java.security.InvalidKeyException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import me.bramhaag.owouploader.R;
import me.bramhaag.owouploader.util.CryptographyHelper;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        var keyPreference = getPreference("owo_api_key", EditTextPreference.class);
        keyPreference.setOnBindEditTextListener(editText -> {
            editText.setText("");
            editText.setHint(R.string.unchanged);
        });

        keyPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            try {
                savePreference("owo_api_key", CryptographyHelper.encrypt((String) newValue));
            } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                Sentry.captureException(e);

                Toast.makeText(
                        getContext(),
                        "Something went wrong while encrypting your key: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }

            return false;
        });

        EditTextPreference uploadUrlPreference = getPreference("upload_url", EditTextPreference.class);
        uploadUrlPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            savePreference("upload_url", fixUrl((String) newValue));
            return false;
        });

        EditTextPreference shortenUrlPreference = getPreference("shorten_url", EditTextPreference.class);
        shortenUrlPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            savePreference("shorten_url", fixUrl((String) newValue));
            return false;
        });
    }

    <T extends Preference> T getPreference(String key, Class<T> clazz) {
        T preference = findPreference(key);

        if (preference == null) {
            throw new IllegalStateException("Cannot find " + key + " preference");
        }

        return (T) preference;
    }

    String fixUrl(String value) {
        if (!value.startsWith("http://") && !value.startsWith("https://")) {
            value = "https://" + value;
        }

        if (!value.endsWith("/")) {
            value += "/";
        }

        return value;
    }

    void savePreference(String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(this.getContext())
                .edit()
                .putString(key, value)
                .apply();
    }
}
