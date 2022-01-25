package ivan.infotechpvt.stuffyrider.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import ivan.infotechpvt.stuffyrider.Bookride;

import ivan.infotechpvt.stuffyrider.R;
import ivan.infotechpvt.stuffyrider.model.OngoingrideModel;
import ivan.infotechpvt.stuffyrider.model.TimeSlotModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RidePendingAdapter extends RecyclerView.Adapter<RidePendingAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<OngoingrideModel> pendingingrideModelArrayList;
    Context ctx;
    public int SelectedPos = -1;
    String timeStart, endTime;


    public RidePendingAdapter(Context ctx, ArrayList<OngoingrideModel> pendingingrideModelArrayList) {
        inflater = LayoutInflater.from(ctx);
        this.pendingingrideModelArrayList = pendingingrideModelArrayList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public RidePendingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.rv_pendingride, parent, false);
        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull RidePendingAdapter.MyViewHolder holder, int position) {

        holder.tvRidename.setText(pendingingrideModelArrayList.get(position).getRide_name());

        Glide.with(ctx)
                .load("https://dev6.ivantechnology.in/stuffyridersenterprises/adminpanel/public/uploads/ride/"
                        + pendingingrideModelArrayList.get(position).getRide_img())
                .placeholder(R.drawable.image2)
                .into(holder.rideImg);


        if (pendingingrideModelArrayList.get(position).getColor_code().equals("#FB7FA5")) {

            holder.ll_bg.setBackgroundResource(R.drawable.border_shape8);

        } else if (pendingingrideModelArrayList.get(position).getColor_code().equals("#FFC423")) {

            holder.ll_bg.setBackgroundResource(R.drawable.border_shape14);

        } else if (pendingingrideModelArrayList.get(position).getColor_code().equals("#1122C1")) {

            holder.ll_bg.setBackgroundResource(R.drawable.border_shape15);

        } else if (pendingingrideModelArrayList.get(position).getColor_code().equals("#009688")) {

            holder.ll_bg.setBackgroundResource(R.drawable.border_shape16);

        } else {

            holder.ll_bg.setBackgroundResource(R.drawable.border_shape8);

        }


        holder.btnEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);//Here I have to use v.getContext() istead of just cont.
                alertDialog.setTitle("Cancel Ride?");
                alertDialog.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                ((Bookride) ctx).cancelridepending(pendingingrideModelArrayList.get(position));

//

                            }
                        });

                alertDialog.show();
            }

        });


//

        holder.recy_time_slot.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false));
        List<TimeSlotModel> timeslotlist = new ArrayList<>();
        try {
            JSONArray list = new JSONArray(pendingingrideModelArrayList.get(position).getRide_time_slot()!=null?pendingingrideModelArrayList.get(position).getRide_time_slot():"[]");
            for (int i = 0; i < list.length(); i++) {
                JSONObject value = list.getJSONObject(i);
                timeslotlist.add(new TimeSlotModel(value.getString("ride_base_time"), value.getString("ride_base_charge")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TimeSlotAdapter mTimeSlotAdapter = new TimeSlotAdapter(ctx, timeslotlist);
        holder.recy_time_slot.setAdapter(mTimeSlotAdapter);
        Log.v("SelectedPos", mTimeSlotAdapter.SelectedPos + "");

        if(pendingingrideModelArrayList.get(position).isRideOngoning()){
            holder.btnEnd.setVisibility(View.VISIBLE);
            holder.btnStart.setVisibility(View.GONE);
            holder.ll_start.setVisibility(View.VISIBLE);
            holder.recy_time_slot.setVisibility(View.GONE);
            holder.tvStatus.setText("Ongoing Ride");
            LocalTime localTime = LocalTime.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
            timeStart = "" + localTime.format(dateTimeFormatter);
            holder.tvStartTime.setText(timeStart);
            holder.tvBaseprice.setText("$" + pendingingrideModelArrayList.get(position).getMin_ride_price());
        }else{
            holder.btnEnd.setVisibility(View.GONE);
            holder.btnStart.setVisibility(View.VISIBLE);
            holder.ll_start.setVisibility(View.GONE);
            holder.recy_time_slot.setVisibility(View.VISIBLE);
            holder.tvStatus.setText("Pending Ride");
          /*  LocalTime localTime = LocalTime.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
            timeStart = "" + localTime.format(dateTimeFormatter);*/
           // holder.tvStartTime.setText(timeStart);
           // holder.tvBaseprice.setText("$" + ongoingrideModelArrayList.get(position).getMin_ride_price());
        }

        holder.btnStart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {


                if (mTimeSlotAdapter.SelectedPos > -1) {

                    ((Bookride) ctx).startride(pendingingrideModelArrayList.get(position),position);

                } else {

                    Toast.makeText(ctx, "Please Select a time slot", Toast.LENGTH_SHORT).show();

                }


            }
        });


        holder.btnEnd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                ((Bookride) ctx).stopridepending(pendingingrideModelArrayList.get(position),position);
                LocalTime localTime = LocalTime.now();
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
                endTime = "" + localTime.format(dateTimeFormatter);
                holder.tvNowTime.setText(endTime);
//                Date mDatestart = new Date();
//                mDatestart.setTime(Long.parseLong(timeStart));
//                Date mDateEnd = new Date();
//                mDateEnd.setTime(Long.parseLong(endTime));
//                long difference = mDateEnd.getTime() - mDatestart.getTime();
//                int days = (int) (difference / (1000 * 60 * 60 * 24));
//                int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
//                int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
//                String diff = hours + ":" + min;
//                holder.tvTimer.setText(diff);



            }
        });

    }

    @Override
    public int getItemCount() {
        return pendingingrideModelArrayList == null ? 0 : pendingingrideModelArrayList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ll_bg, btnStart, btnEnd, ll_start;
        ImageView rideImg;
        TextView tvRidename, tvStatus, tvStartTime, tvNowTime, tvTimer, tvBaseprice;
        ImageView btnEmergency;
        RecyclerView recy_time_slot;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvRidename = (TextView) itemView.findViewById(R.id.tvRidename);
            tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);
            tvStartTime = (TextView) itemView.findViewById(R.id.tvStartTime);
            tvNowTime = (TextView) itemView.findViewById(R.id.tvNowTime);
            tvTimer = (TextView) itemView.findViewById(R.id.tvTimer);
            tvBaseprice = (TextView) itemView.findViewById(R.id.tvBaseprice);
            rideImg = (ImageView) itemView.findViewById(R.id.rideImg);
            ll_bg = (LinearLayout) itemView.findViewById(R.id.ll_bg);
            btnStart = (LinearLayout) itemView.findViewById(R.id.btnStart);
            btnEnd = (LinearLayout) itemView.findViewById(R.id.btnEnd);
            btnEmergency = (ImageView) itemView.findViewById(R.id.btnEmergency);
            recy_time_slot = itemView.findViewById(R.id.recy_time_slot);
            ll_start = (LinearLayout) itemView.findViewById(R.id.ll_start);

        }
    }
}
