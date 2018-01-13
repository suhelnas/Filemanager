package com.example.pc.filemanager;


import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class OverflowMenuHandler {
    Context context;
    File to = null;

    public OverflowMenuHandler(Context context) {
        this.context = context;
    }

    public void share(ArrayList<File> file) {
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        ArrayList<Uri> files = new ArrayList<Uri>();
        String name = file.get(0).getName();
        String extension = name.substring(name.lastIndexOf('.') + 1);
        String mimeType = mimeTypeMap.getMimeTypeFromExtension(extension.toLowerCase());
        intent.setType(mimeType);
        for (File f : file) {
            Uri uri = Uri.fromFile(f);
            files.add(uri);
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        context.startActivity(Intent.createChooser(intent, "Share via"));
    }

    public File rename(final File file) {

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
                to = new File(file.getParent(), "/" + value.getText().toString().trim());
                boolean rename = from.renameTo(to);

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
        return to;

    }
}