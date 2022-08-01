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
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import dagger.hilt.android.AndroidEntryPoint;
import javax.inject.Inject;
import me.bramhaag.owouploader.databinding.FragmentFilterDialogBinding;

/**
 * {@link DialogFragment} that handles shortening urls.
 */
@AndroidEntryPoint
public class FilterDialogFragment extends DialogFragment {

    private FragmentFilterDialogBinding binding;

    @Inject
    public FilterDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = FragmentFilterDialogBinding.inflate(getLayoutInflater());
        var view = binding.getRoot();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Search filters")
                .setView(view)
                .setPositiveButton("Apply", null)
                .setNegativeButton("Cancel", null);

        return builder.create();
    }
}
