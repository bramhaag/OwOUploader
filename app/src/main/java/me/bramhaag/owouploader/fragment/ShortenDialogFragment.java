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
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;
import com.google.android.material.tabs.TabLayout;
import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.net.URI;
import javax.inject.Inject;
import me.bramhaag.owouploader.adapter.HistoryAdapter;
import me.bramhaag.owouploader.api.OwOAPI;
import me.bramhaag.owouploader.api.callback.ResultCallback;
import me.bramhaag.owouploader.databinding.DialogShortenBinding;
import me.bramhaag.owouploader.db.HistoryDatabase;
import me.bramhaag.owouploader.db.entity.ShortenItem;
import me.bramhaag.owouploader.util.ApiUtil;
import me.bramhaag.owouploader.util.TextChangedListener;

/**
 * {@link DialogFragment} that handles shortening urls.
 */
@AndroidEntryPoint
public class ShortenDialogFragment extends DialogFragment {

    private DialogShortenBinding binding;

    private final OwOAPI api;
    private final HistoryDatabase database;

    private TabLayout.Tab tab;
    private HistoryAdapter adapter;

    @Inject
    public ShortenDialogFragment(OwOAPI api, HistoryDatabase database) {
        this.api = api;
        this.database = database;
    }

    public void setAdapter(HistoryAdapter adapter) {
        this.adapter = adapter;
    }

    public void setTab(TabLayout.Tab tab) {
        this.tab = tab;
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

        var preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        var dialog = (AlertDialog) requireDialog();

        var shortenButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        var cancelButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

        shortenButton.setEnabled(false);
        shortenButton.setOnClickListener(view -> {
            var call = api.shortenUrl(
                    binding.shortenDialogInput.getText().toString(),
                    preferences.getString("shorten_url", ""),
                    new ResultCallback<>() {
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
                            Toast.makeText(getContext(), "Shorten error: " + throwable.getMessage(), Toast.LENGTH_LONG)
                                    .show();
                        }

                        @Override
                        public void onComplete(@NonNull String result) {
                            dialog.dismiss();
                            Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();

                            var destUrl = URI.create(binding.shortenDialogInput.getText().toString());
                            var newUrl = URI.create(result);

                            var newItem = new ShortenItem(ApiUtil.getKey(newUrl), destUrl, newUrl);

                            adapter.addItemFirst(newItem);
                            database.shortenItemDao()
                                    .insert(newItem)
                                    .subscribeOn(Schedulers.io())
                                    .subscribe();
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
