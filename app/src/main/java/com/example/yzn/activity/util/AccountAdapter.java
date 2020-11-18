package com.example.yzn.activity.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yzn.R;
import com.example.yzn.activity.ui.CircleImageView;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder>{
    private List<Account> accountList;


    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView textView_nickname;
        TextView textView_number;
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            circleImageView = (CircleImageView) view.findViewById(R.id.circleView);
            textView_nickname = (TextView) view.findViewById(R.id.textView_nickname);
            textView_number = (TextView) view.findViewById(R.id.textView_number);
            imageView = (ImageView) view.findViewById(R.id.imageView_state);
        }

    }

    public AccountAdapter(List<Account> accounts) {
        this.accountList = accounts;
    }

    @NonNull
    @Override
    public AccountAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account, parent, false);
        final AccountAdapter.ViewHolder holder = new AccountAdapter.ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AccountAdapter.ViewHolder holder, int position) {
        Account account = accountList.get(position);
        holder.circleImageView.setImageBitmap(account.getBitmap());
        holder.textView_number.setText(account.getPhone());
        holder.textView_nickname.setText(account.getNickname());
        if (account.getIsOnline()) {
            holder.imageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

}