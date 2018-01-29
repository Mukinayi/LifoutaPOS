package com.example.exact_it_dev.lifoutapos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.exact_it_dev.lifoutapos.R;

import java.util.ArrayList;

/**
 * Created by EXACT-IT-DEV on 1/29/2018.
 */

public class CustomAdapter extends ArrayAdapter<DataModel> implements View.OnClickListener{

    private ArrayList<DataModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtAmount;
        TextView txtTransaction;
        TextView txtCommission;
        TextView txtTranstype;
    }

    public CustomAdapter(ArrayList<DataModel> data, Context context) {
        super(context, R.layout.listrow, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        DataModel dataModel=(DataModel)object;
        switch (v.getId())
        {
            case R.id.textView2:
                Toast.makeText(getContext(),"Vous avez cliqué sur la liste",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listrow, parent, false);
            viewHolder.txtAmount = (TextView) convertView.findViewById(R.id.txtTotAmount);
            viewHolder.txtCommission = (TextView) convertView.findViewById(R.id.txtCommission);
            viewHolder.txtTransaction = (TextView) convertView.findViewById(R.id.txtViewTranscount);
            viewHolder.txtTranstype = (TextView) convertView.findViewById(R.id.txtViewTranstype);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        //Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        //result.startAnimation(animation);
        //lastPosition = position;


        viewHolder.txtTranstype.setText(dataModel.getTxtTranstype());
        viewHolder.txtTransaction.setText(dataModel.getTxtTransaction());
        viewHolder.txtAmount.setText(dataModel.getTxtAmount());
        viewHolder.txtCommission.setText(dataModel.getTxtCommission());
        // Return the completed view to render on screen
        return convertView;
    }
}
