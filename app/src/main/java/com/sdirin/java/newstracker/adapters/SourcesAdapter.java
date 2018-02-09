package com.sdirin.java.newstracker.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.sdirin.java.newstracker.R;
import com.sdirin.java.newstracker.data.model.Source;
import com.sdirin.java.newstracker.presenters.SourcesPresenter;

/**
 * Created by User on 07.02.2018.
 */

public class SourcesAdapter extends RecyclerViewCursorAdapter<SourcesAdapter.SourcesViewHolder> {

    private SourcesPresenter presenter;

    public SourcesAdapter(SourcesPresenter presenter) {
        super(null);
        this.presenter = presenter;
        canDelete = false;
    }

    @Override
    public void onBindViewHolder(SourcesViewHolder holder, Cursor cursor) {
        final Source source = Source.fromCursor(cursor);
        holder.name.setText(source.getName());
        holder.description.setText(source.getDescription());
        holder.category.setText(source.getCategory());
        holder.language.setText(source.getLanguage());
        holder.country.setText(source.getCountry());
        holder.selected.setChecked(presenter.selectedSources.has(source.getId()));
        holder.selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    presenter.selectedSources.setSource(source);
                } else {
                    presenter.selectedSources.removeSource(source);
                }
            }
        });
    }

    @Override
    void onBindRemoveViewHolder(SourcesViewHolder holder, Cursor mCursor) {

    }

    @Override
    void remove(int dbId) {
        presenter.removeSource(dbId);
    }

    @Override
    public SourcesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View item = LayoutInflater.from(context).inflate(R.layout.sources_list_item,parent,false);
        return new SourcesViewHolder(item);
    }

    public class SourcesViewHolder extends RecyclerView.ViewHolder{

        public TextView name;
        public TextView description;
        public TextView category;
        public TextView language;
        public TextView country;
        public CheckBox selected;

        public SourcesViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
            description = itemView.findViewById(R.id.tv_description);
            category = itemView.findViewById(R.id.tv_category);
            language = itemView.findViewById(R.id.tv_language);
            country = itemView.findViewById(R.id.tv_country);
            selected = itemView.findViewById(R.id.cb_selected);
        }
    }
}
