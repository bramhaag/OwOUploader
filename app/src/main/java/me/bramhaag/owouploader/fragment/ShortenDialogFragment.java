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

package me.bramhaag.owouploader.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.tabs.TabLayout;
import java.net.URI;
import java.util.Date;
import me.bramhaag.owouploader.R;
import me.bramhaag.owouploader.adapter.HistoryAdapter;
import me.bramhaag.owouploader.api.OwOAPI;
import me.bramhaag.owouploader.api.callback.ResultCallback;
import me.bramhaag.owouploader.components.ShortenHistoryItem;
import me.bramhaag.owouploader.databinding.ActivityMainBinding;
import me.bramhaag.owouploader.databinding.DialogShortenBinding;
import me.bramhaag.owouploader.databinding.FragmentHistoryBinding;
import me.bramhaag.owouploader.util.TextChangedListener;

/**
 * {@link DialogFragment} that handles shortening urls.
 */
public class ShortenDialogFragment extends DialogFragment {

    private DialogShortenBinding binding;

    private final OwOAPI api;
    private final TabLayout.Tab tab;
    private HistoryAdapter adapter;

    public ShortenDialogFragment(OwOAPI api, TabLayout.Tab tab) {
        this.api = api;
        this.tab = tab;
    }

    public void setAdapter(HistoryAdapter adapter) {
        this.adapter = adapter;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DialogShortenBinding.inflate(getLayoutInflater());
        var view = binding.getRoot();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Shorten URL")
                .setView(view)
                .setPositiveButton("Shorten", null)
                .setNegativeButton("Cancel", null);

        return builder.create();
    }


    @Override
    public void onStart() {
        super.onStart();

        var dialog = (AlertDialog) requireDialog();

        var shortenButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        var cancelButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

        shortenButton.setEnabled(false);
        shortenButton.setOnClickListener(view -> {
            var call = api.shortenUrl(binding.shortenDialogInput.getText().toString(), null, new ResultCallback<>() {
                @Override
                public void onStart() {
                    tab.select();
                    binding.shortenDialogInput.setEnabled(false);
                    binding.shortenProgressBar.setVisibility(View.VISIBLE);
                    binding.shortenProgressText.setVisibility(View.VISIBLE);
                    shortenButton.setEnabled(false);
                }

                @Override
                public void onError(@NonNull Throwable throwable) {
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Shorten error: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onComplete(@NonNull String result) {
                    dialog.dismiss();
                    Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                    adapter.addItem(new ShortenHistoryItem(URI.create(binding.shortenDialogInput.getText().toString()),
                            URI.create(result), new Date()));
                }
            }, false);

            cancelButton.setOnClickListener(v -> {
                call.cancel();
                dialog.dismiss();
            });

            dialog.setOnCancelListener(v -> {
                call.cancel();
                dialog.dismiss();
            });
        });

        binding.shortenDialogInput.addTextChangedListener(
                (TextChangedListener) input -> shortenButton.setEnabled(input.length() != 0));
    }
}
