package com.normal.offline.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.normal.offline.R;
import com.normal.offline.db.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Adapter extends RecyclerView.Adapter<Adapter.VHolder> {


    private List<User> list;

    public Adapter(List<User> list) {
        this.list = list;
    }

    public void update(List<User> userList) {

        this.list = new ArrayList<>();
        this.list.addAll(userList);
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public VHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_item, viewGroup, false);

        return new VHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VHolder vHolder, int i) {

        User user = list.get(i);

        vHolder.name.setText(String.format("%s\n%s\n%s", user.getFirstName(), user.getLastName(), user.getEmail()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addItem(List<User> userList) {
        this.list.addAll(userList);
        notifyDataSetChanged();
    }

    static class VHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name)
        TextView name;

        VHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
