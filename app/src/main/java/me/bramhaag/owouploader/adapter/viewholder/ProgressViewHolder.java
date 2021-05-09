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

package me.bramhaag.owouploader.adapter.viewholder;


import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Locale;
import me.bramhaag.owouploader.R;
import me.bramhaag.owouploader.components.ProgressItem;
import me.bramhaag.owouploader.util.ByteUnit;

/**
 * {@link RecyclerView.ViewHolder} for upload history.
 */

public class ProgressViewHolder extends HistoryViewHolder<ProgressItem> {

    private final TextView title;
    private final ProgressBar progressBar;
    private final TextView progressText;

    /**
     * Instantiate a new ViewHolder.
     *
     * @param view the view
     */
    public ProgressViewHolder(View view) {
        super(view);
        title = view.findViewById(R.id.progress_item_title);
        progressBar = view.findViewById(R.id.progress_item_progress);
        progressText = view.findViewById(R.id.progress_item_description);
    }

    @Override
    void initializeView() {
        setTitle(getItem().getName());
        setProgress(getItem().getUploaded(), getItem().getSize());
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setProgress(long uploadedBytes, long totalBytes) {
        var unit = ByteUnit.getPreferredUnit(totalBytes);

        System.out.println("uploadedBytes = " + uploadedBytes);
        System.out.println("totalBytes = " + totalBytes);
        System.out.println("unit = " + unit);

        var uploaded = ByteUnit.BYTE.to(uploadedBytes, unit);
        var total = ByteUnit.BYTE.to(totalBytes, unit);

        System.out.println("uploaded = " + uploaded);
        System.out.println("total = " + total);

        var progress = uploaded / total;

        progressBar.setProgress((int) (progress * 100));
        progressText.setText(String.format(Locale.getDefault(), "%.1f / %.1f %s", uploaded, total, unit.getUnit()));
    }
}
