package com.cryptotaskapplication.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cryptotaskapplication.R
import com.cryptotaskapplication.models.CryptoCurrency
import com.squareup.picasso.Picasso

@Suppress("NAME_SHADOWING", "UNREACHABLE_CODE", "CAST_NEVER_SUCCEEDS")
class MainAdapter (private val context: Context, private val dataList: List<CryptoCurrency>)  : RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.currency_item_layout, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val position = dataList[position]

        val currencyId = position.id
        val currencyName = position.name
        val currencySymbol = position.symbol
        val currencyPrice = position.quotes[0].price
        val currencyChange = position.quotes[0].percentChange24h

        val currencyUrl = "https://s2.coinmarketcap.com/static/img/coins/64x64/$currencyId.png"
        val currencyChartUrl = "https://s3.coinmarketcap.com/generated/sparklines/web/7d/usd/$currencyId.png"
        val formattedCurrencyPrice = String.format("%.2f", currencyPrice)
        val formattedCurrencyChange = String.format("%.2f", currencyChange)

        Picasso.get().load(currencyUrl).into(holder.currency)
        Picasso.get().load(currencyChartUrl).into(holder.currencyChart)

        Log.i("Exception", "Id is $currencyId")

        holder.currencyPrice.text = formattedCurrencyPrice
        holder.currencyName.text = currencyName
        holder.currencySymbol.text = currencySymbol

        if (currencyChange > 0){
            holder.currencyChange.setTextColor(context.getColor(R.color.green))
            holder.currencyChange.text = "+$formattedCurrencyChange%"
        } else {
            holder.currencyChange.setTextColor(context.getColor(R.color.red))
            holder.currencyChange.text = "$formattedCurrencyChange%"
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val currency: ImageView = itemView.findViewById(R.id.currencyImageView)
        val currencyChart: ImageView = itemView.findViewById(R.id.currencyChartImageView)
        val currencyName: TextView = itemView.findViewById(R.id.currencyNameTextView)
        val currencySymbol: TextView = itemView.findViewById(R.id.currencySymbolTextView)
        val currencyPrice: TextView = itemView.findViewById(R.id.currencyPriceTextView)
        val currencyChange: TextView = itemView.findViewById(R.id.currencyChangeTextView)
    }
}