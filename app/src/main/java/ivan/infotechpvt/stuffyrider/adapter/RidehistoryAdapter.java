package ivan.infotechpvt.stuffyrider.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ivan.infotechpvt.stuffyrider.R;
import ivan.infotechpvt.stuffyrider.Ridehistory;
import ivan.infotechpvt.stuffyrider.model.RidehistoryModel;

import java.util.List;

public class RidehistoryAdapter extends RecyclerView.Adapter<RidehistoryAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private List<RidehistoryModel> ridehistoryModelArrayList;
    Context ctx;


    public RidehistoryAdapter(Context ctx, List<RidehistoryModel> ridehistoryModelArrayList) {
        inflater = LayoutInflater.from(ctx);
        this.ridehistoryModelArrayList = ridehistoryModelArrayList;
        this.ctx = ctx;

    }


    @NonNull
    @Override
    public RidehistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.rv_ridehistory, parent, false);
        RidehistoryAdapter.MyViewHolder holder = new RidehistoryAdapter.MyViewHolder(view);

        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RidehistoryAdapter.MyViewHolder holder, int position) {
        RidehistoryModel mDatabean = ridehistoryModelArrayList.get(position);
        holder.tvUsername.setText(mDatabean.getCustomer_name());
        holder.tvMobile.setText("Mobile No. " + mDatabean.getCustomer_mobile());
        holder.tvRidecost.setText("$" + mDatabean.getTotal_ride_cost());
        holder.tvTotalridetimeandNumber.setText(mDatabean.getTotal_ride_time() +
                " | " + mDatabean.getTotal_ride() + " Ride");
        holder.tvDate.setText(mDatabean.getBooking_date());


        if(mDatabean.getDetailslist().size()>0){
            holder.ll_ridehistoryDetails.setVisibility(View.VISIBLE);
            holder.rv_ridehistoryDetails.setLayoutManager(new LinearLayoutManager(ctx,RecyclerView.VERTICAL,false));
            holder.rv_ridehistoryDetails.setAdapter(new RidedetailsAdapter(ctx,mDatabean.getDetailslist()));
        }else{
            holder.ll_ridehistoryDetails.setVisibility(View.GONE);

        }


        holder.btnRideDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (ridehistoryModelArrayList.get(position).getStatus().equals("C")) {


                    ((Ridehistory) ctx).ridehistorydetails(mDatabean,holder.getAdapterPosition());



                } else {

                  //  holder.ll_ridehistoryDetails.setVisibility(View.GONE);

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);//Here I have to use v.getContext() istead of just cont.
                    alertDialog.setTitle("Ride On Progress! complete ride first");
                    alertDialog.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {


                                    dialog.dismiss();

                                }
                            });

                    alertDialog.show();


                }

            }
        });

    }


    @Override
    public int getItemCount() {
        return ridehistoryModelArrayList == null ? 0 : ridehistoryModelArrayList.size();

    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvUsername, tvMobile, tvRidecost, tvTotalridetimeandNumber, tvDate;
        LinearLayout btnRideDetails, ll_ridehistoryDetails;
        RecyclerView rv_ridehistoryDetails;
        Button btnpaid;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            tvMobile = (TextView) itemView.findViewById(R.id.tvMobile);
            tvRidecost = (TextView) itemView.findViewById(R.id.tvRidecost);
            tvTotalridetimeandNumber = (TextView) itemView.findViewById(R.id.tvTotalridetimeandNumber);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            btnRideDetails = (LinearLayout) itemView.findViewById(R.id.btnRideDetails);
            ll_ridehistoryDetails = (LinearLayout)itemView.findViewById(R.id.ll_ridehistoryDetails);
            rv_ridehistoryDetails = itemView.findViewById(R.id.rv_ridehistoryDetails);
            btnpaid = itemView.findViewById(R.id.btnpaid);
        }


    }
}
