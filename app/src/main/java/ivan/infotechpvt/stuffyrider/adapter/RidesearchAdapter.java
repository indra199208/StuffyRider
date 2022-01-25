package ivan.infotechpvt.stuffyrider.adapter;

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

public class RidesearchAdapter extends RecyclerView.Adapter<RidesearchAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private List<RidehistoryModel> ridehistoryModelArrayList;
    Context ctx;


    public RidesearchAdapter(Context ctx, List<RidehistoryModel> ridehistoryModelArrayList) {
        inflater = LayoutInflater.from(ctx);
        this.ridehistoryModelArrayList = ridehistoryModelArrayList;
        this.ctx = ctx;

    }


    @NonNull
    @Override
    public RidesearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.rv_ridehistory, parent, false);
        RidesearchAdapter.MyViewHolder holder = new RidesearchAdapter.MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RidesearchAdapter.MyViewHolder holder, int position) {
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


        if (mDatabean.getPayment_status().equals("unpaid")){

            holder.btnpaid.setVisibility(View.VISIBLE);

        }else {

            holder.btnpaid.setVisibility(View.GONE);

        }



        holder.btnpaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ((Ridehistory) ctx).paid2(ridehistoryModelArrayList.get(position));

            }
        });

        holder.btnRideDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (ridehistoryModelArrayList.get(position).getStatus().equals("C")) {

//                    Intent intent = new Intent(ctx, Ridehistorydetails.class);
//                    intent.putExtra("booking_id", ridehistoryModelArrayList.get(position).getId());
//                    intent.putExtra("customerName", ridehistoryModelArrayList.get(position).getCustomer_name());
//                    intent.putExtra("mobileNumber", ridehistoryModelArrayList.get(position).getCustomer_mobile());
//                    intent.putExtra("totalrideCost", ridehistoryModelArrayList.get(position).getTotal_ride_cost());
//                    intent.putExtra("tvTotalridetimeandNumber", ridehistoryModelArrayList.get(position).getTotal_ride_time() +
//                            " | " + ridehistoryModelArrayList.get(position).getTotal_ride() + " Ride");
//
//
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    ctx.startActivity(intent);
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
