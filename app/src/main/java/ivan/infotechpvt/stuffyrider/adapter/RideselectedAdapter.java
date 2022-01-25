package ivan.infotechpvt.stuffyrider.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import ivan.infotechpvt.stuffyrider.Dashboard;
import ivan.infotechpvt.stuffyrider.R;
import ivan.infotechpvt.stuffyrider.model.RidelistModel;
import ivan.infotechpvt.stuffyrider.model.TimeSlotModel;

public class RideselectedAdapter extends RecyclerView.Adapter<RideselectedAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<RidelistModel> ridelistModelArrayList;
    Context ctx;
    public int SelectedPos = -1;
    String timeStart, endTime;


    public RideselectedAdapter(Context ctx, ArrayList<RidelistModel> ridelistModelArrayList) {
        inflater = LayoutInflater.from(ctx);
        this.ridelistModelArrayList = ridelistModelArrayList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public RideselectedAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.rv_selectedride, parent, false);
        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull RideselectedAdapter.MyViewHolder holder, int position) {

        holder.tvTimer.setTag(0);
        if (holder.mTimer != null) {
            holder.mTimer.cancel();
            holder.mTimer = new Timer();
        }
        holder.mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ((Activity)ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int counter = (int) holder.tvTimer.getTag();
                        counter++;
                        holder.tvTimer.setTag(counter);
                        int hours = counter / 3600;
                        int minutes = (counter % 3600) / 60;
                        int seconds = counter % 60;
                        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

                        holder.tvTimer.setText(timeString);
                    }
                });
            }
        }, 1, 1000);

        holder.tvRidename.setText(ridelistModelArrayList.get(position).getRide_name());

        Glide.with(ctx)
                .load("https://dev6.ivantechnology.in/stuffyridersenterprises/adminpanel/public/uploads/ride/"
                        + ridelistModelArrayList.get(position).getRide_img())
                .placeholder(R.drawable.image2)
                .into(holder.rideImg);


        Drawable unwrappedDrawable = holder.ll_bg.getBackground();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        if (!ridelistModelArrayList.get(position).getColor_code().equals("null")) {
            DrawableCompat.setTint(wrappedDrawable, Color.parseColor(ridelistModelArrayList.get(position).getColor_code()));
        } else {
            DrawableCompat.setTint(wrappedDrawable, Color.parseColor("#FFC423"));
        }


        holder.btnEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);//Here I have to use v.getContext() istead of just cont.
                alertDialog.setTitle("Cancel Ride?");
                alertDialog.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                ((Dashboard) ctx).cancelride(ridelistModelArrayList.get(position));

//

                            }
                        });

                alertDialog.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();


            }
        });


        holder.btnOngoingEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);
                alertDialog.setTitle("Emergency Cancel Ride?");
                View dialogView = LayoutInflater.from(ctx).inflate(R.layout.alert_emergency, null);
                alertDialog.setView(dialogView);
                EditText edt_reason = dialogView.findViewById(R.id.edt_reason);

                alertDialog.setPositiveButton("Yes Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ((Dashboard) ctx).cancelride(ridelistModelArrayList.get(position));
                            }
                        });

                alertDialog.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                alertDialog.show();


            }
        });


//recy
        String baseprice = "";
        holder.recy_time_slot.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false));
        /*List<TimeSlotModel> timeslotlist = new ArrayList<>();
        try {
            JSONArray list = new JSONArray(ridelistModelArrayList.get(position).getRide_time_slot() != null ? ridelistModelArrayList.get(position).getRide_time_slot() : "[]");

            for (int i = 0; i < list.length(); i++) {
                JSONObject value = list.getJSONObject(i);
                baseprice = value.getString("ride_base_charge");
                timeslotlist.add(new TimeSlotModel(value.getString("ride_base_time"), value.getString("ride_base_charge")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        TimeSlotAdapter mTimeSlotAdapter = new TimeSlotAdapter(ctx, ridelistModelArrayList.get(position).getTimeSlot());
        holder.recy_time_slot.setAdapter(mTimeSlotAdapter);
        Log.v("SelectedPos", mTimeSlotAdapter.SelectedPos + "");

        if (ridelistModelArrayList.get(position).isRideOngoning()) {
            holder.btnEnd.setVisibility(View.VISIBLE);
            holder.btnStart.setVisibility(View.GONE);
            holder.ll_start.setVisibility(View.VISIBLE);
            holder.recy_time_slot.setVisibility(View.GONE);
            holder.btnOngoingEmergency.setVisibility(View.VISIBLE);
            holder.btnEmergency.setVisibility(View.GONE);
            holder.tvStatus.setText("Ongoing Ride");
//            LocalTime localTime = LocalTime.now();
//            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("k:mm:ss");
//            timeStart = "" + localTime.format(dateTimeFormatter);
//            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT-5"));
//            Date currentLocalTime = cal.getTime();
//            @SuppressLint("SimpleDateFormat")
//            DateFormat date = new SimpleDateFormat("k:mm:ss");
//            date.setTimeZone(TimeZone.getTimeZone("GMT-5"));
            timeStart = new SimpleDateFormat("k:mm:ss", Locale.getDefault()).format(new Date());




            holder.tvStartTime.setText(timeStart);
//            holder.tvBaseprice.setText("$" + ridelistModelArrayList.get(position).getRide_time_slot());
            for(int i = 0;i<ridelistModelArrayList.get(position).getTimeSlot().size();i++){
                TimeSlotModel  model = ridelistModelArrayList.get(position).getTimeSlot().get(i);
                if(model.isSelected()){
                    holder.tvBaseprice.setText("$" + model.getRide_base_charge());
                    break;
                }else{
                    holder.tvBaseprice.setText("");
                }
            }
           /* if (mTimeSlotAdapter.SelectedPos > -1)
            else{
                holder.tvBaseprice.setText("");
            }*/
//            if(mTimeSlotAdapter.SelectedPos>-1)
//            holder.tvBaseprice.setText("$" +  timeslotlist.get(mTimeSlotAdapter.SelectedPos).getRide_base_charge());
//            else
//                holder.tvBaseprice.setText("");

        } else {
            holder.btnEnd.setVisibility(View.GONE);
            holder.btnStart.setVisibility(View.VISIBLE);
            holder.ll_start.setVisibility(View.GONE);
            holder.recy_time_slot.setVisibility(View.VISIBLE);
            holder.btnOngoingEmergency.setVisibility(View.GONE);
            holder.btnEmergency.setVisibility(View.VISIBLE);
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

                    ((Dashboard) ctx).startride(ridelistModelArrayList.get(position), position);


                } else {

                    Toast.makeText(ctx, "Please Select a time slot", Toast.LENGTH_SHORT).show();

                }


            }
        });

        holder.btnEnd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);//Here I have to use v.getContext() istead of just cont.
                alertDialog.setTitle("Stop Ride?");
                alertDialog.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                ((Dashboard) ctx).stopride(ridelistModelArrayList.get(position), position);
//                                LocalTime localTime = LocalTime.now();
//                                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
                                endTime = new SimpleDateFormat("k:mm:ss", Locale.getDefault()).format(new Date());
                                holder.tvNowTime.setText(endTime);

                            }
                        });

                alertDialog.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();


            }
        });

    }

    @Override
    public int getItemCount() {
        return ridelistModelArrayList == null ? 0 : ridelistModelArrayList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ll_bg, btnStart, btnEnd, ll_start, notAvailable, btnLanded;
        ImageView rideImg;
        TextView tvRidename, tvStatus, tvStartTime, tvTimer, tvBaseprice;
        TextClock tvNowTime;
        ImageView btnEmergency, btnOngoingEmergency;
        RecyclerView recy_time_slot;
        Timer mTimer = new Timer();


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRidename = (TextView) itemView.findViewById(R.id.tvRidename);
            tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);
            tvStartTime = (TextView) itemView.findViewById(R.id.tvStartTime);
            tvNowTime = (TextClock) itemView.findViewById(R.id.tvNowTime);
            tvTimer = (TextView) itemView.findViewById(R.id.tvTimer);
            tvBaseprice = (TextView) itemView.findViewById(R.id.tvBaseprice);
            rideImg = (ImageView) itemView.findViewById(R.id.rideImg);
            ll_bg = (LinearLayout) itemView.findViewById(R.id.ll_bg);
            notAvailable = (LinearLayout) itemView.findViewById(R.id.notAvailable);
            btnLanded = (LinearLayout) itemView.findViewById(R.id.btnLanded);
            btnStart = (LinearLayout) itemView.findViewById(R.id.btnStart);
            btnEnd = (LinearLayout) itemView.findViewById(R.id.btnEnd);
            btnEmergency = (ImageView) itemView.findViewById(R.id.btnEmergency);
            btnOngoingEmergency = (ImageView) itemView.findViewById(R.id.btnOngoingEmergency);
            recy_time_slot = itemView.findViewById(R.id.recy_time_slot);
            ll_start = (LinearLayout) itemView.findViewById(R.id.ll_start);
            tvNowTime.setFormat12Hour("k:mm:ss");
            tvNowTime.setFormat24Hour(null);

        }
    }
}
