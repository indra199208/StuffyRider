package ivan.infotechpvt.stuffyrider.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ivan.infotechpvt.stuffyrider.Bookride;
import ivan.infotechpvt.stuffyrider.Dashboard;

import ivan.infotechpvt.stuffyrider.R;
import ivan.infotechpvt.stuffyrider.model.TimeSlotModel;

import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.MyViewHolder> {

    private Context mContext;
    private List<TimeSlotModel> timeslotlist;
    public int  SelectedPos =-1;

    public TimeSlotAdapter(Context mContext, List<TimeSlotModel> timeslotlist) {
        this.mContext = mContext;
        this.timeslotlist = timeslotlist;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_timeslot, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TimeSlotModel model = timeslotlist.get(position);
      holder.tv_time_slot.setText(String.format("$%s/%smin",model.getRide_base_charge(),model.getRide_base_time()));
      if(SelectedPos==position){
          // true
          holder.ll_Timeslotbg.setBackgroundResource(R.drawable.border_shape9);
          model.setSelected(true);

      }else{
          //false
          holder.ll_Timeslotbg.setBackgroundResource(R.drawable.border_shape10);
          model.setSelected(false);
      }

      holder.itemView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              SelectedPos = holder.getAdapterPosition();
              if(mContext instanceof Dashboard){
              ((Dashboard)mContext).selectedRideVal(model.getRide_base_time(),model.getRide_base_charge());
              notifyDataSetChanged();
              }else if (mContext instanceof Bookride){
                  ((Bookride)mContext).selectedRideVal(model.getRide_base_time(),model.getRide_base_charge());
                  notifyDataSetChanged();
              }
          }
      });
    }

    @Override
    public int getItemCount() {
        return timeslotlist.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
    TextView tv_time_slot;
    LinearLayout ll_Timeslotbg;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_time_slot = itemView.findViewById(R.id.tv_time_slot);
            ll_Timeslotbg = itemView.findViewById(R.id.ll_Timeslotbg);
        }
    }
}
