package com.blue.webcontent;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

public class MainJavaActivity extends AppCompatActivity {

    private BaseQuickAdapter<Merchant, BaseViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_java);

        adapter = new BaseQuickAdapter<Merchant, BaseViewHolder>() {
            @Override
            protected void convert(@NotNull BaseViewHolder baseViewHolder, Merchant merchant) {
                baseViewHolder.setText(R.id.tvName,merchant.getName());
                baseViewHolder.setText(R.id.tvPhone,merchant.getPhone());
                ImageView imageView = (ImageView) (baseViewHolder.getView(R.id.imageView));
                Glide.with(MainJavaActivity.this).load(merchant.getImg()).into(imageView);
            }
        };
    }
}