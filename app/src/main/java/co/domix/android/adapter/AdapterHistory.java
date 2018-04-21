package co.domix.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import co.domix.android.DomixApplication;
import co.domix.android.R;
import co.domix.android.model.Order;

/**
 * Created by unicorn on 1/28/2018.
 */

public class AdapterHistory extends RecyclerView.Adapter<AdapterHistory.AdapterViewHolder> {

    ArrayList<Order> orders;
    Activity activity;
    Context ctx;

    public AdapterHistory(ArrayList<Order> orders, Context ctx) {
        this.orders = orders;
        this.activity = activity;
        this.ctx = ctx;
    }

    @Override
    public AdapterHistory.AdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_order, parent, false);
        AdapterViewHolder holder = new AdapterViewHolder(v, ctx, orders);
        return holder;
    }

    @Override
    public void onBindViewHolder(AdapterViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.textViewFrom.setText(order.getX_name_from());
        holder.textViewTo.setText(order.getX_name_to());
        holder.textViewTimeAgo.setText(String.valueOf(order.getX_date()) + " " + order.getX_time());
        holder.textViewMoney.setText(String.valueOf(order.getX_money_to_pay()));

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class AdapterViewHolder extends RecyclerView.ViewHolder {
        TextView textViewFrom, textViewTo, textViewTimeAgo, textViewMoney;
        FrameLayout frameLayout;
        Context ctx;
        ArrayList<Order> orders = new ArrayList<Order>();
        public int position;

        public AdapterViewHolder(View itemView, Context ctx, ArrayList<Order> orders) {
            super(itemView);
//            itemView.setOnClickListener(this);
            this.orders = orders;
            this.ctx = ctx;
            frameLayout = (FrameLayout) itemView.findViewById(R.id.framelayoutCardView);
            textViewFrom = (TextView) itemView.findViewById(R.id.from);
            textViewTo = (TextView) itemView.findViewById(R.id.to);
            textViewTimeAgo = (TextView) itemView.findViewById(R.id.historyDateDelivery);
            textViewMoney = (TextView) itemView.findViewById(R.id.historyMoneyToPay);
        }

//        @Override
//        public void onClick(View v) {
//            position = getAdapterPosition();
//            Order order = this.orders.get(position);
//            Intent intent = new Intent(this.ctx, OrderDetail.class);
//            intent.putExtra("positiond", position);
//            intent.putExtra("idd", order.getX_id());
//            intent.putExtra("uidAuthord", order.getA_id());
//            intent.putExtra("agod", order.getRelativeTimeStamp());
//            intent.putExtra("cityd", order.getA_city());
//            intent.putExtra("fromd", order.getX_name_from());
//            intent.putExtra("tod", order.getX_name_to());
//            intent.putExtra("latFromd", order.getX_latitude_from());
//            intent.putExtra("latTod", order.getX_latitudeTo());
//            intent.putExtra("lonFromd", order.getX_longitudeFrom());
//            intent.putExtra("lonTod", order.getX_longitudeTo());
//            intent.putExtra("headerd", order.getX_title());
//            intent.putExtra("descriptiond", order.getX_description());
//            intent.putExtra("authord", order.getA_email());
//            intent.putExtra("moneyd", order.getX_money_to_pay().toString());
//            this.ctx.startActivity(intent);
//        }
    }
}
