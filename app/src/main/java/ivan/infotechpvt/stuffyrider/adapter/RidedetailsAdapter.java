package ivan.infotechpvt.stuffyrider.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ivan.infotechpvt.stuffyrider.R;
import ivan.infotechpvt.stuffyrider.model.RidedetailsModel;

import java.util.List;

public class RidedetailsAdapter extends RecyclerView.Adapter<RidedetailsAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private List<RidedetailsModel> ridedetailsModelArrayList;
    Context ctx;

    public RidedetailsAdapter(Context ctx, List<RidedetailsModel> ridedetailsModelArrayList) {
        inflater = LayoutInflater.from(ctx);
        this.ridedetailsModelArrayList = ridedetailsModelArrayList;
        this.ctx = ctx;

    }


    @NonNull
    @Override
    public RidedetailsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.rv_ridehistorydetails, parent, false);
        RidedetailsAdapter.MyViewHolder holder = new RidedetailsAdapter.MyViewHolder(view);


        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RidedetailsAdapter.MyViewHolder holder, int position) {

        holder.tvRidename.setText(ridedetailsModelArrayList.get(position).getRide_name());
        holder.tvStart.setText(ridedetailsModelArrayList.get(position).getStatr_time());
        holder.tvEnd.setText(ridedetailsModelArrayList.get(position).getEnd_time());
        holder.tvTotaltime.setText(ridedetailsModelArrayList.get(position).getTotal_ridetime());
        if (ridedetailsModelArrayList.get(position).getStatus().equals("I")) {
            holder.tvBaseprice.setText("$0");
        } else {
            holder.tvBaseprice.setText("$" + ridedetailsModelArrayList.get(position).getTotal_ride_cost());
        }


    }

    @Override
    public int getItemCount() {
        return ridedetailsModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvRidename, tvStart, tvEnd, tvTotaltime, tvBaseprice, tvAddtime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRidename = (TextView) itemView.findViewById(R.id.tvRidename);
            tvStart = (TextView) itemView.findViewById(R.id.tvStart);
            tvEnd = (TextView) itemView.findViewById(R.id.tvEnd);
            tvTotaltime = (TextView) itemView.findViewById(R.id.tvTotaltime);
            tvBaseprice = (TextView) itemView.findViewById(R.id.tvBaseprice);




        }
    }

}
