package com.blue.webcontent;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainJavaActivity extends AppCompatActivity {

    private BaseQuickAdapter<Merchant, BaseViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_java);

        adapter = new BaseQuickAdapter<Merchant, BaseViewHolder>(R.layout.adapter_merchant) {
            @Override
            protected void convert(@NotNull BaseViewHolder baseViewHolder, Merchant merchant) {
                baseViewHolder.setText(R.id.tvName,merchant.getName());
                baseViewHolder.setText(R.id.tvPhone,merchant.getPhone());
                ImageView imageView = (ImageView) (baseViewHolder.getView(R.id.imageView));
                Glide.with(MainJavaActivity.this).load(merchant.getImg()).into(imageView);
            }
        };
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        getMerchant();
    }

    private void getMerchant() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect("http://m.liebiao.com/beijing/jiadianweixiu/")
                            .userAgent("Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko)")
                            .timeout(5000).get();
                    Element body = document.body();
                    Elements list = body.getElementsByClass("main");
                    Element merchantElement = null;
                    if (list.size() > 0) {
                        for (Element element : list.get(0).getAllElements()) {
                            if ("post-list".equals(element.className())) {
                                merchantElement = element;
                                break;
                            }
                        }
                    }
                    if (merchantElement != null) {
                        List<Element> merchantList = new ArrayList<>();
                        Elements allElements = merchantElement.getAllElements();
                        for (Element element : allElements) {
                            if ("post-card border-onepx".equals(element.className())) {
                                merchantList.add(element);
                            }
                        }
                        List<Merchant> merchants = new ArrayList<>();
                        for (Element element : merchantList) {
                            merchants.add(getMerchantItem(element));
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.setList(merchants);
                            }
                        });
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Merchant getMerchantItem(Element element) {
        Merchant merchant = new Merchant();
        Elements cardElement = element.getElementsByClass("post-card-link");
        if (cardElement.size() > 0) {
            Elements linkElement = cardElement.get(0).getElementsByClass("post-link");
            Elements infoElement = cardElement.get(0).getElementsByClass("post-overview").get(0).getElementsByClass("post-button-wrapper");
            Element info1 = null;
            for (Element element1 : linkElement.get(0).getAllElements()) {
                if ("post-img".equals(element1.className())) {
                    info1 = element1;
                    break;
                }
            }

            merchant.setImg(info1.attr("src"));
            merchant.setName(info1.attr("alt"));
            Element info2 = null;
            for (Element element1:infoElement.get(0).select("div").get(0).getAllElements()){
                if ("tel-btn-wrapper".equals(element1.className())) {
                    for (Element element2 : element1.getAllElements()) {
                        if ("tel-btn tel".equals(element2.className())) {
                            info2 = element2;
                            break;
                        }
                    }
                    break;
                }
            }
            merchant.setPhone(info2.attr("href"));
        }
        return merchant;
    }
}