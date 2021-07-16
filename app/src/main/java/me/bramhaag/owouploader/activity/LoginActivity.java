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

package me.bramhaag.owouploader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.qualifiers.ActivityContext;
import dagger.hilt.android.scopes.ActivityRetainedScoped;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.UUID;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.inject.Inject;
import me.bramhaag.owouploader.DaggerOwOApplication_HiltComponents_SingletonC;
import me.bramhaag.owouploader.R;
import me.bramhaag.owouploader.api.OwOAPI;
import me.bramhaag.owouploader.api.callback.ResultCallback;
import me.bramhaag.owouploader.api.model.UserModel;
import me.bramhaag.owouploader.databinding.ActivityLoginBinding;
import me.bramhaag.owouploader.module.ApiModule;
import me.bramhaag.owouploader.util.CryptographyHelper;

/**
 * Login Activity.
 */
@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Inject
    OwOAPI api;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.loginButton.setOnClickListener(view -> {
            var input = binding.loginTokenInput;
            var inputKey = input.getEditText().getText().toString().trim();

            api.setApiKey(inputKey);
            api.getUser(new ResultCallback<>() {
                @Override
                public void onStart() {
                    // TODO: Add spinning loading icon to the TextInputLayout, or something along those lines.
                }

                @Override
                public void onError(@NonNull Throwable throwable) {
                    input.setError("The key provided is not valid!");
                }

                @Override
                public void onComplete(@NonNull UserModel result) {
                    try {
                        PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit()
                                .putString(
                                    CryptographyHelper.KEY_ALIAS,
                                    CryptographyHelper.getInstance().encrypt(inputKey)
                                ).apply();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                        finish();
                    }
                }
            });
        });
    }
}