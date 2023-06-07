package net.maps.navigation.gps.location.sondermap.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import net.maps.navigation.gps.location.sondermap.R;
import java.util.ArrayList;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {

    private int selectedPosition = 0;
    private Context context;
    private ArrayList<String> states;

    public TopicAdapter(Context context, ArrayList<String> configBeans) {
        this.context = context;
        this.states = configBeans;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView topic;
        private RelativeLayout relativeLayout1;

        public ViewHolder(View itemView) {
            super(itemView);
            topic = itemView.findViewById(R.id.topic);
            relativeLayout1 = itemView.findViewById(R.id.relativeLayout1);


        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic,parent,false);
        return new ViewHolder(view);
    }
    @SuppressLint({"RecyclerView", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,  int position) {
      //  RandomBean configBean = randomBeans.get(position);
        // 设置itemView的状态为选中或未选中
        if (selectedPosition == position) {
            holder.relativeLayout1.setBackgroundResource(R.drawable.sharp_chosen);
            holder.topic.setTextColor(Color.parseColor("#30BFC9"));
        }else {
            holder.relativeLayout1.setBackgroundResource(R.color.white);
            holder.topic.setTextColor(Color.parseColor("#222222"));
        }

        String s = states.get(position);
        holder.topic.setText(s);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 设置选中位置为当前位置
                int previousSelectedPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(previousSelectedPosition);
                notifyItemChanged(selectedPosition);
            }
        });
    }


    @Override
    public int getItemCount() {
        return states.size();
    }


    public void setSelectedPosition(int position) {
        int previousSelectedPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousSelectedPosition);
        notifyItemChanged(selectedPosition);
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setList(ArrayList<String> list){
        this.states=list;
        notifyDataSetChanged();
    }
}
