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
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import java.net.URI;
import java.util.Date;
import me.bramhaag.owouploader.R;
import me.bramhaag.owouploader.adapter.HistoryAdapter;
import me.bramhaag.owouploader.api.OwOAPI;
import me.bramhaag.owouploader.api.callback.ResultCallback;
import me.bramhaag.owouploader.components.ShortenHistoryItem;

public class ShortenDialogFragment extends DialogFragment {

    private final Handler mainHandler;
    private final Context context;
    private final OwOAPI api;
    private final ShortenHistoryFragment shortenHistoryFragment;

    public ShortenDialogFragment(OwOAPI api, Context context, ShortenHistoryFragment shortenHistoryFragment) {
        this.api = api;
        this.context = context;
        this.mainHandler = new Handler(context.getMainLooper());
        this.shortenHistoryFragment = shortenHistoryFragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        var view = getLayoutInflater().inflate(R.layout.dialog_shorten, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Shorten URL")
                .setView(view)
                .setPositiveButton("Shorten", (dialog, which) -> {
                    EditText text = view.findViewById(R.id.shorten_dialog_input);
                    api.shortenUrl(text.getText().toString(), null, new ResultCallback<>() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onError(@NonNull Throwable throwable) {
                            runOnUiThread(() -> Toast
                                    .makeText(context, "Shorten error: " + throwable.getMessage(), Toast.LENGTH_LONG)
                                    .show());
                        }

                        @Override
                        public void onComplete(@NonNull String result) {
                            runOnUiThread(() -> {
                                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                                ((HistoryAdapter) shortenHistoryFragment.binding.recyclerView.getAdapter())
                                        .addItem(new ShortenHistoryItem(
                                                URI.create(text.getText().toString()), URI.create(result), new Date()));
                            });
                        }
                    }, false);
                })
                .setNegativeButton("Cancel", null);

        return builder.create();
    }

    private void runOnUiThread(Runnable runnable) {
        mainHandler.post(runnable);
    }

}
