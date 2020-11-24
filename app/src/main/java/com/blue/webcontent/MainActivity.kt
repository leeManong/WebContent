package com.blue.webcontent

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class MainActivity : AppCompatActivity() {

    private lateinit var adapter:BaseQuickAdapter<Merchant,BaseViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = object :BaseQuickAdapter<Merchant,BaseViewHolder>(R.layout.adapter_merchant){
            override fun convert(holder: BaseViewHolder, item: Merchant) {
                holder.setText(R.id.tvName,item.name)
                holder.setText(R.id.tvPhone,item.phone)
                Glide.with(this@MainActivity).load(item.img).into(holder.getView(R.id.imageView))
            }
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        getPhone()
    }

    private fun getPhone() {
        Thread {
            val document = Jsoup.connect("http://m.liebiao.com/beijing/jiadianweixiu/").userAgent("Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko)").timeout(5000).get()
            val body = document.body()
            val list = body.getElementsByClass("main")
            var merchantElement: Element? = null
            if (list.isNotEmpty()) {
                for (item in list[0].allElements.withIndex()) {
                    if (item.value.className() == "post-list") {
                        merchantElement = item.value
                        break
                    }
                }
            }
            val merchantList = merchantElement?.allElements?.filter {
                it.className() == "post-card border-onepx"
            }
            val merchants = arrayListOf<Merchant>()
            merchantList?.forEach {
                merchants.add(getMerchantItem(it))
            }
            Log.d("dsa", "das")
            runOnUiThread {
                adapter.setList(merchants)
            }
        }.start()
    }

    private fun getMerchantItem(element: Element): Merchant { 
        val merchant = Merchant()
        val cardElement = element.getElementsByClass("post-card-link")
        if (cardElement.isNotEmpty()) {
            val linkElement = cardElement[0].getElementsByClass("post-link")
            val infoElement = cardElement[0].getElementsByClass("post-overview").first()?.getElementsByClass("post-button-wrapper")
            val info1 = linkElement.first()?.allElements?.filter {
                it.className() == "post-img"
            }?.first()
            info1?.let {
                merchant.img = info1.attr("src")
                merchant.name = info1.attr("alt")
            }
            val info2 = infoElement?.first()?.select("div")?.first()?.allElements?.find {
                it.className() == "tel-btn-wrapper"
            }?.allElements?.filter {
                it.className() == "tel-btn tel"
            }?.first()
            info2?.let {
                merchant.phone = info2.attr("href")
            }
        }
        return merchant
    }
}