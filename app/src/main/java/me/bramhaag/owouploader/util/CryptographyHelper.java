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

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import androidx.annotation.NonNull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStore.SecretKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * Cryptography helper that we'll use to encrypt/decrypt user keys.
 */
public class CryptographyHelper {

    private static final int TAG_LENGTH_BITS = 128;
    private static final String TRANSFORMATION = String.format("%s/%s/%s",
            KeyProperties.KEY_ALGORITHM_AES,
            KeyProperties.BLOCK_MODE_GCM,
            KeyProperties.ENCRYPTION_PADDING_NONE
    );
    private static final String KEY_STORE_NAME = "AndroidKeyStore";
    private static final String DELIMITER = "_";

    private static final KeyStore keyStore;
    private static final SecretKey secretKey;

    private static final Cipher cipher;

    /**
     * Key alias that we'll use for both the key store and shared preferences.
     */
    public static final String KEY_ALIAS = "owo_api_key";

    static {
        try {
            keyStore = getAndroidKeyStore();
            secretKey = getSecretKey();
            cipher = Cipher.getInstance(TRANSFORMATION);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private CryptographyHelper() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    @NonNull
    private static SecretKey getSecretKey()
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        try {
            var secretKeyEntry = (SecretKeyEntry) keyStore.getEntry(KEY_ALIAS, null);
            return secretKeyEntry.getSecretKey();
        } catch (NullPointerException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
            // non-existent, generate
            return generateKey();
        }
    }

    @NonNull
    private static KeyStore getAndroidKeyStore()
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        var keyStore = KeyStore.getInstance(KEY_STORE_NAME);
        keyStore.load(null);

        return keyStore;
    }

    @NonNull
    private static SecretKey generateKey()
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

    /**
     * Encrypt a given input into a nicely packed string that contains our cipher's IV and the encrypted content,
     * concatenated and encoded using Base 64.
     *
     * @param input Input to encrypt.
     * @return Encrypted string.
     * @throws InvalidKeyException       When the secret key used is invalid.
     * @throws BadPaddingException       When the padding used is invalid.
     * @throws IllegalBlockSizeException When the block is illegally sized.
     * @see #decrypt(String) For a method to decrypt the encrypted input.
     */
    @NonNull
    public static String encrypt(@NonNull String input)
            throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        var iv = cipher.getIV();
        var encrypted = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));

        return Base64.encodeToString(iv, Base64.NO_WRAP) + DELIMITER
                + Base64.encodeToString(encrypted, Base64.NO_WRAP);
    }

    /**
     * Decrypt any input given. Beware that this will NOT parse any kind of encrypted string and will require the format
     * applied by the encryption method.
     *
     * @param input Text that we want to decrypt.
     * @return Decrypted text with the very nice information we want to use.
     * @throws InvalidKeyException                When the secret key used is invalid.
     * @throws InvalidAlgorithmParameterException When the algorithm used is invalid.
     * @throws BadPaddingException                When the padding is invalid.
     * @throws IllegalBlockSizeException          When the block is illegally sized.
     * @see #encrypt(String) To get a valid encrypted string.
     */
    @NonNull
    public static String decrypt(@NonNull String input) throws InvalidKeyException, InvalidAlgorithmParameterException,
            BadPaddingException, IllegalBlockSizeException {
        var parts = input.split(DELIMITER);

        var iv = Base64.decode(parts[0], Base64.NO_WRAP);
        var encrypted = Base64.decode(parts[1], Base64.NO_WRAP);

        cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BITS, iv, 0, iv.length));
        return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
    }
}
