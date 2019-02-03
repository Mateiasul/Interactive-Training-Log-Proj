package com.example.android.fireapp.testTab;


import android.content.Context;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.fireapp.R;

public class MySimpleArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public static class ViewHolder{
        TextView textView;
        ImageView imageView;
    }

    public MySimpleArrayAdapter(Context context, String[] values) {
        super(context, R.layout.to_delete_testlistview, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      /*  LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.to_delete_testlistview, parent, false);
*/
        //TextView textView = (TextView) rowView.findViewById(R.id.label);
        //ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        //textView.setText(values[position]);
        // Change the icon for Windows and iPhone




        View rowView = convertView;
        if (rowView == null)
        {
              LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
             rowView = inflater.inflate(R.layout.to_delete_testlistview, null, false);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.textView = rowView.findViewById(R.id.label);
            viewHolder.imageView = rowView.findViewById(R.id.icon);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        String s = values[position];
        holder.textView.setText(s);

        if (s.startsWith("Windows7") || s.startsWith("iPhone")
                || s.startsWith("Solaris")) {
            holder.imageView.setImageResource(R.drawable.baseline_dashboard_black_24dp);
        } else {
            holder.imageView.setImageResource(R.drawable.baseline_person_black_24dp);
        }

        return rowView;
    }
}