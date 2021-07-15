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

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import androidx.annotation.NonNull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStore.SecretKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;

public class CryptographyHelper {
    private static CryptographyHelper INSTANCE;

    private static final int TAG_LENGTH_BYTES = 16;
    private static final String TRANSFORMATION = String.format("%s/%s/%s",
            KeyProperties.KEY_ALGORITHM_AES,
            KeyProperties.BLOCK_MODE_GCM,
            KeyProperties.ENCRYPTION_PADDING_NONE
    );
    private static final String KEY_STORE_NAME = "AndroidKeyStore";
    private static final String CONCATENATOR = "_";

    private static final String KEY_ALIAS = "OwOApiKey";

    private final KeyStore keyStore;
    private final SecretKey secretKey;

    private final Cipher cipher;

    public CryptographyHelper()
            throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, NoSuchProviderException,
            InvalidAlgorithmParameterException, NoSuchPaddingException {
        this.keyStore = getAndroidKeyStore();
        this.secretKey = getSecretKey();
        this.cipher = Cipher.getInstance(TRANSFORMATION);
    }

    @NonNull
    private SecretKey getSecretKey()
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        SecretKey secretKey;
        try {
            var secretKeyEntry = (SecretKeyEntry) keyStore.getEntry(KEY_ALIAS, null);
            secretKey = secretKeyEntry.getSecretKey();
        } catch (NullPointerException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
            // non-existent, generate
            secretKey = generateKey();
        }
        return secretKey;
    }

    @NonNull
    private KeyStore getAndroidKeyStore()
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        var keyStore = KeyStore.getInstance(KEY_STORE_NAME);
        keyStore.load(null);
        return keyStore;
    }

    @NonNull
    private SecretKey generateKey()
            throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        var keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEY_STORE_NAME);
        var keyGenParameterSpec = new KeyGenParameterSpec
                .Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build();
        keyGenerator.init(keyGenParameterSpec);
        return keyGenerator.generateKey();
    }

    @NonNull
    public String encrypt(@NonNull String input)
            throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        var iv = cipher.getIV();
        var encrypted = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));

        // gotta encode to string
        return Base64.encodeToString(iv, Base64.NO_WRAP) + CONCATENATOR + Base64.encodeToString(encrypted, Base64.NO_WRAP);
    }

    @NonNull
    public String decrypt(@NonNull String input)
            throws InvalidKeyException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
        var parts = input.split(CONCATENATOR);

        var iv = Base64.decode(parts[0], Base64.NO_WRAP);
        var encrypted = Base64.decode(parts[1], Base64.NO_WRAP);

        cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BYTES * 8, iv, 0, iv.length));
        return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
    }

    @NonNull
    public static CryptographyHelper getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }

        try {
            return INSTANCE = new CryptographyHelper();
        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | IOException | NoSuchProviderException | InvalidAlgorithmParameterException | NoSuchPaddingException e) {
            e.printStackTrace();
            return null; // shouldn't happen?
        }
    }
}
