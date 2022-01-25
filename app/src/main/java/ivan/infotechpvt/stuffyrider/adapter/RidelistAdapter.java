package ivan.infotechpvt.stuffyrider.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import ivan.infotechpvt.stuffyrider.MainActivity;
import ivan.infotechpvt.stuffyrider.R;
import ivan.infotechpvt.stuffyrider.model.RidelistModel;

import java.util.ArrayList;

public class RidelistAdapter extends RecyclerView.Adapter<RidelistAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<RidelistModel> ridelistModelArrayList;
    Context ctx;

    public RidelistAdapter(Context ctx, ArrayList<RidelistModel> ridelistModelArrayList) {
        inflater = LayoutInflater.from(ctx);
        this.ridelistModelArrayList = ridelistModelArrayList;
        this.ctx = ctx;

    }


    @NonNull
    @Override
    public RidelistAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.rv_ride, parent, false);
        MyViewHolder holder = new MyViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RidelistAdapter.MyViewHolder holder, int position) {

        final RidelistModel ridelistModel = ridelistModelArrayList.get(position);
        if (ridelistModel.isSelected()) {
            holder.view.setBackgroundColor(Color.CYAN);
        } else {
            holder.view.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.tvRidename.setText(ridelistModelArrayList.get(position).getRide_name());
        Drawable unwrappedDrawable = holder.ll_backgroundBg.getBackground();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        if (!ridelistModelArrayList.get(position).getColor_code().equals("null")) {
            DrawableCompat.setTint(wrappedDrawable, Color.parseColor(ridelistModelArrayList.get(position).getColor_code()));
        } else {
            DrawableCompat.setTint(wrappedDrawable, Color.parseColor("#FFC423"));
        }


        Glide.with(ctx)
                .load("https://dev6.ivantechnology.in/stuffyridersenterprises/adminpanel/public/uploads/ride/"
                        + ridelistModelArrayList.get(position).getRide_img())
                .placeholder(R.drawable.image2)
                .into(holder.rideImg);
        holder.view.setBackgroundColor(ridelistModel.isSelected() ? Color.CYAN : Color.TRANSPARENT);

        if (ridelistModel.isSelected()) {
            holder.btnRent.setVisibility(View.GONE);
            holder.btnRentselected.setVisibility(View.VISIBLE);
        } else {
            if (ridelistModelArrayList.get(position).getAvailibility().equals("no")) {
                holder.ll_backgroundBg.setVisibility(View.VISIBLE);
                holder.notAvailable.setVisibility(View.VISIBLE);
                holder.btnRent.setVisibility(View.GONE);
                holder.btnRentselected.setVisibility(View.GONE);
                holder.btnRentnotavailable.setVisibility(View.VISIBLE);
            } else {
                //  holder.ll_backgroundBg.setVisibility(View.GONE);
                holder.notAvailable.setVisibility(View.GONE);
                holder.btnRent.setVisibility(View.VISIBLE);
                holder.btnRentselected.setVisibility(View.GONE);
                holder.btnRentnotavailable.setVisibility(View.GONE);
            }
        }


        holder.btnRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ridelistModel.isSelected())
                    ridelistModel.setSelected(false);
                else
                    ridelistModel.setSelected(true);

                ((MainActivity) ctx).bottomsheetopendirect(ridelistModelArrayList.get(position));
                notifyItemChanged(holder.getAdapterPosition());

            }
        });
        holder.btnRentselected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ridelistModel.isSelected()) {
                    ridelistModel.setSelected(false);
                    holder.view.setBackgroundColor(Color.TRANSPARENT);
                    holder.btnRentselected.setVisibility(View.GONE);
                    holder.btnRent.setVisibility(View.VISIBLE);
                } else
                    ridelistModel.setSelected(true);

                ((MainActivity) ctx).bottomsheetopen(ridelistModelArrayList.get(position));

            }
        });

    }

    @Override
    public int getItemCount() {
        return ridelistModelArrayList == null ? 0 : ridelistModelArrayList.size();

    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvRidename;
        LinearLayout btnRent, btnRentselected, notAvailable, btnRentnotavailable;
        ImageView rideImg;
        LinearLayout ll_backgroundBg;
        private View view;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            tvRidename = (TextView) itemView.findViewById(R.id.tvRidename);
            btnRent = (LinearLayout) itemView.findViewById(R.id.btnRent);
            btnRentselected = (LinearLayout) itemView.findViewById(R.id.btnRentselected);
            rideImg = (ImageView) itemView.findViewById(R.id.rideImg);
            ll_backgroundBg = (LinearLayout) itemView.findViewById(R.id.ll_backgroundBg);
            notAvailable = (LinearLayout) itemView.findViewById(R.id.notAvailable);
            btnRentnotavailable = (LinearLayout) itemView.findViewById(R.id.btnRentnotavailable);

        }
    }
}
