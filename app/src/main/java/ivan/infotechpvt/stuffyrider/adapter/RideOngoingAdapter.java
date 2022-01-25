package ivan.infotechpvt.stuffyrider.adapter;

import android.annotation.SuppressLint;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import ivan.infotechpvt.stuffyrider.Bookride;
import ivan.infotechpvt.stuffyrider.R;
import ivan.infotechpvt.stuffyrider.model.OngoingrideModel;
import ivan.infotechpvt.stuffyrider.model.TimeSlotModel;

public class RideOngoingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<OngoingrideModel> ongoingrideModelArrayList;
    Context ctx;
    public String ridebasecharge = "";


    public RideOngoingAdapter(Context ctx, ArrayList<OngoingrideModel> ongoingrideModelArrayList) {
        inflater = LayoutInflater.from(ctx);
        this.ongoingrideModelArrayList = ongoingrideModelArrayList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                View view = inflater.inflate(R.layout.item_customeview, parent, false);
                return new CustomerViewHolder(view);
            case 2:
                View view2 = inflater.inflate(R.layout.rv_ongoingride, parent, false);
                return new OngoingViewHolder(view2);
            case 3:
                View view3 = inflater.inflate(R.layout.rv_pendingride, parent, false);
                return new PendingViewHolder(view3);
            default:
                View view4 = inflater.inflate(R.layout.item_customeview, parent, false);
                return new CustomerViewHolder(view4);
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 1:
                CustomerViewHolder mCustomerViewHolder = (CustomerViewHolder) holder;
                customerViewBindViewholder(mCustomerViewHolder, position);
                break;
            case 2:
                OngoingViewHolder mOngoingViewHolder = (OngoingViewHolder) holder;
                ongoingViewBindViewHolder(mOngoingViewHolder, position);
                break;
            case 3:
                PendingViewHolder mPendingViewHolder = (PendingViewHolder) holder;
                pendingBindViewHolder(mPendingViewHolder, position);
                break;
        }
    }

    private void customerViewBindViewholder(CustomerViewHolder holder, int position) {
        holder.tv_customer_name.setText("Name - " + ongoingrideModelArrayList.get(position).getCustomer_name());
        String phonenumber = ongoingrideModelArrayList.get(position).getCustomer_mobile();

        holder.tv_customer_phone.setText("Mobile - " + phonenumber + "");


        holder.btnStopall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);//Here I have to use v.getContext() istead of just cont.
                alertDialog.setTitle("Stop All Started Ride For This Customer?");
                alertDialog.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                ((Bookride) ctx).stopallride(ongoingrideModelArrayList.get(position));


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
    }

    private void ongoingViewBindViewHolder(@NonNull OngoingViewHolder holder, int position) {
        OngoingrideModel mOngoingrideModel = ongoingrideModelArrayList.get(position);
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("kk:mm:ss");
        try {
            Date datestart = formatter.parse(mOngoingrideModel.getStatr_time());
            Date mDateEnd = c.getTime();
            long difference = mDateEnd.getTime() - datestart.getTime();
            int days = (int) (difference / (1000 * 60 * 60 * 24));
            int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
            int sec = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        float extra_per_min_cost = Float.parseFloat(mOngoingrideModel.getExtra_per_min_cost());


        if (Long.parseLong(mOngoingrideModel.getTime_spend()) > 0) {
            int prevtime = Integer.parseInt(mOngoingrideModel.getTime_spend());
            float basetime = Float.parseFloat(mOngoingrideModel.getMin_ride_time()) * 60;
            if (prevtime > basetime) {
                float blanctime = prevtime - basetime;
//                int price = (int) blanctime / 60*extra_per_min_cost;
//                int price = (int) ((blanctime/60)*extra_per_min_cost);
                int paidtime = (int) (blanctime/60);
                float price = paidtime * extra_per_min_cost;
                mOngoingrideModel.setMin_ride_price(String.valueOf((Float.parseFloat(mOngoingrideModel.getMin_ride_price()) + price)));
            }
        }



        holder.tvTimer.setTag(Long.parseLong(mOngoingrideModel.getTime_spend()));
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ((Activity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        float basetime = Float.parseFloat(mOngoingrideModel.getMin_ride_time());
                        float sec = basetime * 60;
                        long counter = (long) holder.tvTimer.getTag();
                        counter++;
                        Log.v("counter", counter + "");
                        holder.tvTimer.setTag(counter);
                        long hours = counter / 3600;
                        long minutes = (counter % 3600) / 60;
                        long seconds = counter % 60;
                        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

                        holder.tvTimer.setText(timeString);

                        if (counter > 0 && counter > sec && counter % 60 == 0) {
                            mOngoingrideModel.setMin_ride_price(String.valueOf((Float.parseFloat(mOngoingrideModel.getMin_ride_price()) + extra_per_min_cost)));
                            holder.tvBaseprice.setText("$" + mOngoingrideModel.getMin_ride_price());

                        }
                    }
                });
            }
        }, 1, 1000);


        holder.tvRidename.setText(mOngoingrideModel.getRide_name());
        holder.tvStartTime.setText(mOngoingrideModel.getStatr_time());
        holder.tvNowTime.setText(formatter.format(c.getTime()));
        holder.tvBaseprice.setText("$" + mOngoingrideModel.getMin_ride_price());

        Glide.with(ctx)
                .load("https://dev6.ivantechnology.in/stuffyridersenterprises/adminpanel/public/uploads/ride/" + ongoingrideModelArrayList.get(position).getRide_img())
                .placeholder(R.drawable.image2)
                .into(holder.rideImg);


        Drawable unwrappedDrawable = holder.ll_bg.getBackground();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        if (!ongoingrideModelArrayList.get(position).getColor_code().equals("null")) {
            DrawableCompat.setTint(wrappedDrawable, Color.parseColor(ongoingrideModelArrayList.get(position).getColor_code()));
        } else {
            DrawableCompat.setTint(wrappedDrawable, Color.parseColor("#FFC423"));
        }


        if (ongoingrideModelArrayList.get(position).isRideStop()) {

            holder.notAvailable.setVisibility(View.VISIBLE);
            holder.btnLanded.setVisibility(View.VISIBLE);
            holder.btnStop.setVisibility(View.GONE);
            holder.ll_ongoingDetails.setVisibility(View.GONE);
            holder.btnEmergency.setVisibility(View.INVISIBLE);
            holder.tvStatus.setText("Landed");

        } else {


            holder.notAvailable.setVisibility(View.GONE);
            holder.btnLanded.setVisibility(View.GONE);
            holder.btnStop.setVisibility(View.VISIBLE);
            holder.btnEmergency.setVisibility(View.VISIBLE);
            holder.ll_ongoingDetails.setVisibility(View.VISIBLE);
            holder.tvStatus.setText("Ongoing");
        }


        holder.btnEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ((Bookride) ctx).alertemergency(ongoingrideModelArrayList.get(position), position);


            }

        });


        holder.btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);//Here I have to use v.getContext() istead of just cont.
                alertDialog.setTitle("Stop Ride?");
                alertDialog.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                ((Bookride) ctx).stopride(ongoingrideModelArrayList.get(position), position);


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

    }

    // @RequiresApi(api = Build.VERSION_CODES.O)
    public void pendingBindViewHolder(@NonNull PendingViewHolder holder, int position) {

       /* Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("kk:mm:ss");
        try {
            Date datestart = formatter.parse(ongoingrideModelArrayList.get(position).getStatr_time());
            Date mDateEnd = c.getTime();
            long difference = mDateEnd.getTime() - datestart.getTime();
            int days = (int) (difference / (1000 * 60 * 60 * 24));
            int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
            int sec = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
*/


        holder.tvTimer.setTag(0);
        if (holder.mTimer != null) {
            holder.mTimer.cancel();
            holder.mTimer = new Timer();
        }
        holder.mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ((Activity) ctx).runOnUiThread(new Runnable() {
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


        holder.tvRidename.setText(ongoingrideModelArrayList.get(position).getRide_name());

        Glide.with(ctx)
                .load("https://dev6.ivantechnology.in/stuffyridersenterprises/adminpanel/public/uploads/ride/"
                        + ongoingrideModelArrayList.get(position).getRide_img())
                .placeholder(R.drawable.image2)
                .into(holder.rideImg);

        Drawable unwrappedDrawable = holder.ll_bg.getBackground();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        if (!ongoingrideModelArrayList.get(position).getColor_code().equals("null")) {
            DrawableCompat.setTint(wrappedDrawable, Color.parseColor(ongoingrideModelArrayList.get(position).getColor_code()));
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

                                ((Bookride) ctx).cancelridepending(ongoingrideModelArrayList.get(position));

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


        holder.btnOngoingEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((Bookride) ctx).alertemergency(ongoingrideModelArrayList.get(position), position);


            }
        });


//
        String baseprice = "";
        holder.recy_time_slot.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false));
        /*List<TimeSlotModel> timeslotlist = new ArrayList<>();
        try {
            JSONArray list = new JSONArray(ongoingrideModelArrayList.get(position).getRide_time_slot() != null ? ongoingrideModelArrayList.get(position).getRide_time_slot() : "[]");
            for (int i = 0; i < list.length(); i++) {
                JSONObject value = list.getJSONObject(i);
                baseprice = value.getString("ride_base_charge");
                timeslotlist.add(new TimeSlotModel(value.getString("ride_base_time"), value.getString("ride_base_charge")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        TimeSlotAdapter mTimeSlotAdapter = new TimeSlotAdapter(ctx, ongoingrideModelArrayList.get(position).getTimeSlot());
        holder.recy_time_slot.setAdapter(mTimeSlotAdapter);
        Log.v("SelectedPos", mTimeSlotAdapter.SelectedPos + "");

        if (ongoingrideModelArrayList.get(position).isRideOngoning()) {
            holder.btnEnd.setVisibility(View.VISIBLE);
            holder.btnStart.setVisibility(View.GONE);
            holder.ll_start.setVisibility(View.VISIBLE);
            holder.recy_time_slot.setVisibility(View.GONE);
            holder.btnOngoingEmergency.setVisibility(View.VISIBLE);
            holder.btnEmergency.setVisibility(View.GONE);
            holder.tvStatus.setText("Ongoing Ride");
//            LocalTime localTime = LocalTime.now();
//            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("k:mm:ss");
            String timeStart = new SimpleDateFormat("k:mm:ss", Locale.getDefault()).format(new Date());
            holder.tvStartTime.setText(timeStart);
            for (int i = 0; i < ongoingrideModelArrayList.get(position).getTimeSlot().size(); i++) {
                TimeSlotModel model = ongoingrideModelArrayList.get(position).getTimeSlot().get(i);
                if (model.isSelected()) {
                    holder.tvBaseprice.setText("$" + model.getRide_base_charge());
                    break;
                } else {
                    holder.tvBaseprice.setText("");
                }
            }
        } else {
            if (ongoingrideModelArrayList.get(position).isRideStop()) {
                holder.btnLanded.setVisibility(View.VISIBLE);
                holder.notAvailable.setVisibility(View.VISIBLE);
                holder.btnEnd.setVisibility(View.GONE);
                holder.ll_start.setVisibility(View.VISIBLE);
                holder.ll_start.setBackgroundColor(Color.RED);
                holder.tvNowTime.setVisibility(View.GONE);
                holder.tvEndtime.setVisibility(View.VISIBLE);
                holder.tvEndtime.setText(ongoingrideModelArrayList.get(position).getEnd_time());
                holder.tvEnd.setText("Ride End");
                holder.tvTimer.setVisibility(View.GONE);
                holder.tvStartTime.setText(ongoingrideModelArrayList.get(position).getStatr_time());

                holder.tvStoptime.setVisibility(View.VISIBLE);
                int timespent = Integer.parseInt(ongoingrideModelArrayList.get(position).getTime_spend());
                int hours = timespent / 3600;
                int minutes = (timespent % 3600) / 60;
                int seconds = timespent % 60;
                String totaltime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                holder.tvStoptime.setText(totaltime);

                holder.tvBaseprice.setText("$"+ongoingrideModelArrayList.get(position).getTotal_ride_cost());

                holder.btnEmergency.setVisibility(View.INVISIBLE);
                holder.btnOngoingEmergency.setVisibility(View.GONE);
                holder.btnStart.setVisibility(View.GONE);
                holder.recy_time_slot.setVisibility(View.GONE);
                holder.tvStatus.setText("Landed");

            } else if (ongoingrideModelArrayList.get(position).getStatus().equals("C")) {
                holder.btnLanded.setVisibility(View.VISIBLE);
                holder.notAvailable.setVisibility(View.VISIBLE);
                holder.btnEnd.setVisibility(View.GONE);
                holder.btnEmergency.setVisibility(View.INVISIBLE);
                holder.btnOngoingEmergency.setVisibility(View.GONE);
                holder.ll_start.setVisibility(View.VISIBLE);
                holder.ll_start.setBackgroundColor(Color.RED);
                holder.tvNowTime.setVisibility(View.GONE);
                holder.tvEndtime.setVisibility(View.VISIBLE);
                holder.tvEndtime.setText(ongoingrideModelArrayList.get(position).getEnd_time());
                holder.tvEnd.setText("Ride End");
                holder.tvTimer.setVisibility(View.GONE);
                holder.tvStartTime.setText(ongoingrideModelArrayList.get(position).getStatr_time());


                holder.tvStoptime.setVisibility(View.VISIBLE);
                int timespent = Integer.parseInt(ongoingrideModelArrayList.get(position).getTime_spend());
                int hours = timespent / 3600;
                int minutes = (timespent % 3600) / 60;
                int seconds = timespent % 60;
                String totaltime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                holder.tvStoptime.setText(totaltime);

                holder.tvBaseprice.setText("$"+ongoingrideModelArrayList.get(position).getTotal_ride_cost());

                holder.btnStart.setVisibility(View.GONE);
                holder.recy_time_slot.setVisibility(View.GONE);
                holder.tvStatus.setText("Landed");

            } else {
                holder.btnLanded.setVisibility(View.GONE);
                holder.notAvailable.setVisibility(View.GONE);
                holder.btnEnd.setVisibility(View.GONE);
                holder.ll_start.setVisibility(View.GONE);
                holder.btnStart.setVisibility(View.VISIBLE);
                holder.btnEmergency.setVisibility(View.VISIBLE);
                holder.btnOngoingEmergency.setVisibility(View.GONE);
                holder.recy_time_slot.setVisibility(View.VISIBLE);
                holder.tvStatus.setText("Pending Ride");
            }

        }


        holder.btnStart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                if (mTimeSlotAdapter.SelectedPos > -1) {

                    ((Bookride) ctx).startride(ongoingrideModelArrayList.get(position), position);

                } else {

                    Toast.makeText(ctx, "Please Select a time slot", Toast.LENGTH_SHORT).show();

                }


            }
        });


        holder.btnEnd.setOnClickListener(view -> {


            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);//Here I have to use v.getContext() istead of just cont.
            alertDialog.setTitle("Stop Ride?");
            alertDialog.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            ((Bookride) ctx).stopridepending(ongoingrideModelArrayList.get(position), position);
//                            LocalTime localTime = LocalTime.now();
//                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
                            String endTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
                            holder.tvNowTime.setText(endTime);

                        }
                    });

            alertDialog.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            alertDialog.show();


        });

    }


    @Override
    public int getItemCount() {
        return ongoingrideModelArrayList == null ? 0 : ongoingrideModelArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (ongoingrideModelArrayList.get(position).getId() == null) {
            return 1;
        } else if (ongoingrideModelArrayList.get(position).getStatus().equals("A")) {
            return 2;
        } else {
            return 3;
        }
    }

    static class CustomerViewHolder extends RecyclerView.ViewHolder {

        TextView tv_customer_phone, tv_customer_name;
        LinearLayout btnStopall;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_customer_name = (TextView) itemView.findViewById(R.id.tv_customer_name);
            tv_customer_phone = (TextView) itemView.findViewById(R.id.tv_customer_phone);
            btnStopall = (LinearLayout) itemView.findViewById(R.id.btnStopall);
        }
    }

    static class OngoingViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ll_bg, btnStop, notAvailable, btnLanded, ll_ongoingDetails;
        ImageView rideImg;
        TextView tvRidename, tvStartTime, tvTimer, tvBaseprice, tvStatus;
        TextClock tvNowTime;
        ImageView btnEmergency;


        public OngoingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRidename = (TextView) itemView.findViewById(R.id.tvRidename);
            tvStartTime = (TextView) itemView.findViewById(R.id.tvStartTime);
            tvNowTime = (TextClock) itemView.findViewById(R.id.tvNowTime);
            tvTimer = (TextView) itemView.findViewById(R.id.tvTimer);
            tvBaseprice = (TextView) itemView.findViewById(R.id.tvBaseprice);
            tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);
            rideImg = (ImageView) itemView.findViewById(R.id.rideImg);
            ll_bg = (LinearLayout) itemView.findViewById(R.id.ll_bg);
            btnStop = (LinearLayout) itemView.findViewById(R.id.btnStop);
            notAvailable = (LinearLayout) itemView.findViewById(R.id.notAvailable);
            btnLanded = (LinearLayout) itemView.findViewById(R.id.btnLanded);
            ll_ongoingDetails = (LinearLayout) itemView.findViewById(R.id.ll_ongoingDetails);
            btnEmergency = (ImageView) itemView.findViewById(R.id.btnEmergency);
            tvNowTime.setFormat12Hour("k:mm:ss");
            tvNowTime.setFormat24Hour(null);
//            tvNowTime.setTimeZone("Canada/Pacific");



        }
    }

    static class PendingViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ll_bg, btnStart, btnEnd, ll_start, btnLanded, ll_ongoingDetails, notAvailable;
        ImageView rideImg;
        TextView tvRidename, tvStatus, tvStartTime, tvTimer, tvBaseprice, tvEnd, tvStoptime, tvEndtime;
        ImageView btnEmergency, btnOngoingEmergency;
        TextClock tvNowTime;
        RecyclerView recy_time_slot;
        Timer mTimer = new Timer();

        public PendingViewHolder(@NonNull View itemView) {
            super(itemView);

            tvRidename = (TextView) itemView.findViewById(R.id.tvRidename);
            tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);
            tvStartTime = (TextView) itemView.findViewById(R.id.tvStartTime);
            tvNowTime = (TextClock) itemView.findViewById(R.id.tvNowTime);
            tvEnd = (TextView) itemView.findViewById(R.id.tvEnd);
            tvStoptime = (TextView) itemView.findViewById(R.id.tvStoptime);
            tvTimer = (TextView) itemView.findViewById(R.id.tvTimer);
            tvBaseprice = (TextView) itemView.findViewById(R.id.tvBaseprice);
            tvEndtime = (TextView)itemView.findViewById(R.id.tvEndtime);
            rideImg = (ImageView) itemView.findViewById(R.id.rideImg);
            ll_bg = (LinearLayout) itemView.findViewById(R.id.ll_bg);
            btnStart = (LinearLayout) itemView.findViewById(R.id.btnStart);
            btnEnd = (LinearLayout) itemView.findViewById(R.id.btnEnd);
            btnLanded = (LinearLayout) itemView.findViewById(R.id.btnLanded);
            notAvailable = (LinearLayout) itemView.findViewById(R.id.notAvailable);
            btnEmergency = (ImageView) itemView.findViewById(R.id.btnEmergency);
            btnOngoingEmergency = (ImageView) itemView.findViewById(R.id.btnOngoingEmergency);
            recy_time_slot = itemView.findViewById(R.id.recy_time_slot);
            ll_start = (LinearLayout) itemView.findViewById(R.id.ll_start);
            tvNowTime.setFormat12Hour("k:mm:ss");
            tvNowTime.setFormat24Hour(null);
//            tvNowTime.setTimeZone("Canada/Pacific");


        }
    }
}
