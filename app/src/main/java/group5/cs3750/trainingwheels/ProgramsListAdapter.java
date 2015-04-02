package group5.cs3750.trainingwheels;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ProgramsListAdapter extends BaseAdapter {
    private Context context;

    private LayoutInflater inflater;

    private String[] files;

    public ProgramsListAdapter(Context context, String[] files) {
        this.context = context;

        this.files = files;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return files.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.main_open_program_list_item, null);

        ((TextView) convertView.findViewById(R.id.list_fileName)).setText(files[position]);

        return convertView;
    }
}