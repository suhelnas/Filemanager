package com.example.pc.filemanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.pc.filemanager.R.drawable.directoryicon;


public class ListContent extends ArrayAdapter {
ArrayList<File> name;
    Context context;
    private SparseBooleanArray mSelectedItemsIds;
    public ListContent(Context context, int resource, ArrayList<File> name) {
        super(context, resource,name);
        mSelectedItemsIds = new SparseBooleanArray();
        this.context=context;
        this.name=name;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.listrow, null);
        }

        TextView name= (TextView) v.findViewById(R.id.name);
        TextView numberoffiles= (TextView) v.findViewById(R.id.numberoffiles);
        ImageView directory= (ImageView) v.findViewById(R.id.directoryimage);
        TextView lastmodified= (TextView) v.findViewById(R.id.lastmodified);

File f= (File) getItem(position);
        name.setText(f.getName());
       if(f.isDirectory()) {
            numberoffiles.setText(f.listFiles().length+"");

           directory.setImageResource(R.drawable.directoryicon);

        }
if(f.isFile()){
    directory.setImageResource(0);
    numberoffiles.setText(getFileSize(f.length()));
    if(f.exists()){
        Glide.with(context).load(f.getAbsolutePath()).into(directory);


    }
}

return v;
    }
    public static String getFileSize(long size) {
        if (size <= 0)
            return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public void remove(File object) {
        name.remove(object);
        notifyDataSetChanged();
    }
    public List<File> getfile() {
        return name;
    }
    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

  /* public void rename(final File file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater1 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialog = inflater1.inflate(R.layout.rename, null);
        builder.setView(dialog);
        final EditText value = (EditText) dialog.findViewById(R.id.renametext);
        value.setText(file.getName());
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                File from = new File(file.getParent(), file.getName());
                File to = new File(file.getParent(), "/" + value.getText().toString().trim());
                boolean rename = from.renameTo(to);

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
        notifyDataSetChanged();
    }*/

}
