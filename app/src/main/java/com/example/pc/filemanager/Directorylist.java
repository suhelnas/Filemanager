package com.example.pc.filemanager;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Directorylist extends Activity {
    ListView listView;
    TextView path;

    String directoryname;
    ArrayList<File> file;
    int check = 0;
    File f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directorylist);
        path = (TextView) findViewById(R.id.path);
        listView = (ListView) findViewById(R.id.listview);
        directoryname = getIntent().getStringExtra("directory");
        path.setText(directoryname);
        File convert = new File(directoryname);
        File[] values = convert.listFiles();
        file = new ArrayList<File>(Arrays.asList(values));

        final ListContent adapter = new ListContent(Directorylist.this, R.layout.listrow, file);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                int checkeditemcount = listView.getCheckedItemCount();
                f = (File) listView.getItemAtPosition(i);
                if (f.isDirectory()) {
                    check = 1;
                }
                actionMode.setTitle(checkeditemcount + " Selected");
                adapter.toggleSelection(i);
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode.getMenuInflater().inflate(R.menu.overflow, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {

                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                SparseBooleanArray selected = adapter.getSelectedIds();
                com.example.pc.filemanager.OverflowMenuHandler overflowMenuHandler = new com.example.pc.filemanager.OverflowMenuHandler(Directorylist.this);
                switch (menuItem.getItemId()) {
                    case R.id.delete:
                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                File selecteditem = (File) adapter
                                        .getItem(selected.keyAt(i));
                                selecteditem.delete();
                                adapter.remove(selecteditem);
                            }
                        }
                        actionMode.finish();
                        return true;
                    case R.id.share:
                        if (check == 1)
                            Toast.makeText(Directorylist.this, "Select Files not Directory", Toast.LENGTH_SHORT).show();
                        else {
                            ArrayList<File> send = new ArrayList<File>();
                            for (int i = (selected.size() - 1); i >= 0; i--) {
                                if (selected.valueAt(i)) {
                                    send.add((File) adapter.getItem(selected.keyAt(i)));
                                }
                            }
                            overflowMenuHandler.share(send);

                        }
                        return true;
                    case R.id.rename:
                        if (selected.size() > 1)
                            Toast.makeText(Directorylist.this, "select one", Toast.LENGTH_SHORT).show();
                        else {
                            File f = (File) adapter.getItem(selected.keyAt(0));
                            File rename = overflowMenuHandler.rename(f);

                        }
                        return true;
                    case R.id.copy:
                        ArrayList<File> items = new ArrayList<File>();
                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                items.add((File) adapter.getItem(selected.keyAt(i)));
                            }
                        }
                        actionMode.finish();
                        return true;
                    case R.id.paste:


                        return true;

                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                adapter.removeSelection();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                f = (File) listView.getItemAtPosition(i);
                boolean check1 = f.isDirectory();
                boolean check2 = f.isFile();
                if (check1) {
                    Intent intent = new Intent(Directorylist.this, Directorylist.class);
                    intent.putExtra("directory", f.getAbsolutePath());
                    startActivity(intent);
                }

                if (check2) {
                    if (f.canRead()) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        if (Build.VERSION.SDK_INT <= 23) {
                            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                            String name = f.getName();
                            String extension = name.substring(name.lastIndexOf('.') + 1);
                            String mimeType = mimeTypeMap.getMimeTypeFromExtension(extension.toLowerCase());
                            intent.setDataAndType(Uri.fromFile(f), mimeType);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        } else {

                            String file = f.getName();
                            File imagePath = new File(f.getParent(), "/");
                            File newFile = new File(imagePath, file);
                            Uri uri = FileProvider.getUriForFile(Directorylist.this, "com.example.pc.filemanager", newFile);
                            intent.setDataAndType(uri, MimeTypeMap.getSingleton().getMimeTypeFromExtension(file));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);


                        }
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(Directorylist.this, "No handler for this type of file.", Toast.LENGTH_LONG).show();
                        }

                    } else
                        Toast.makeText(Directorylist.this, "No application found which can open the file", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

  /*  @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.fileoperation, menu);
    }

    /**
     * This will be invoked when a menu item is selected
     */
  /*  @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        final File file = (File) listView.getItemAtPosition(index);
        AlertDialog.Builder builder = new AlertDialog.Builder(Directorylist.this);
        switch (item.getItemId()) {
            case R.id.share :
                if(file.isFile()) {
                    MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                    String name = file.getName();
                    String extension = name.substring(name.lastIndexOf('.') + 1);
                    String mimeType = mimeTypeMap.getMimeTypeFromExtension(extension.toLowerCase());
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType(mimeType);
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(file.getAbsolutePath())));
                    startActivity(Intent.createChooser(intent, "Share via"));
                }
                break;
            case R.id.delete:
                builder.setTitle("delete " + file.getName());
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (file.isDirectory()) {
                            String[] children = file.list();
                            for (int a = 0; a < children.length; a++) {
                                new File(file, children[a]).delete();
                            }
                        } else {
                            boolean deleted = file.delete();
                        }
                        Toast.makeText(Directorylist.this, "Deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
                break;
               case R.id.move:
                   Toast.makeText(this, "move", Toast.LENGTH_SHORT).show();
                   break;
                case R.id.copy:
                    break;
                case R.id.rename:

                    LayoutInflater inflater1 = (LayoutInflater) Directorylist.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                    break;
                case R.id.details:
                    break;

            }
            return true;

    }*/

    /*  @Override
       public boolean onCreateOptionsMenu(Menu menu) {
           getMenuInflater().inflate(R.menu.overflow, menu);
           return true;
       }
       @Override
       public boolean onPrepareOptionsMenu(Menu menu){
          menu.setGroupVisible(R.id.filoperation,false);
   return true;
       }
       @Override
       public boolean onOptionsItemSelected(MenuItem item) {
           switch (item.getItemId()) {
               case R.id.newfolder:
                   AlertDialog.Builder builder = new AlertDialog.Builder(Directorylist.this);
                   builder.setTitle("New Folder");
                   LayoutInflater inflater1 = (LayoutInflater) Directorylist.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                   View dialog = inflater1.inflate(R.layout.newfolder, null);
                   builder.setView(dialog);
                   final EditText newfolder= (EditText) dialog.findViewById(R.id.newfolder);
                   newfolder.setText("NewFolder");
                   builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {
                           File folder=new File(path.getText().toString()+File.separator+newfolder.getText().toString().trim());
                           boolean created= folder.mkdirs();



                       }
                   });
                   builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {

                       }
                   });
                   builder.show();

                   return true;
               case R.id.sortbyname:
                   Collections.sort(file,new SortFileByName());
                   return true;
               default:
                   return super.onOptionsItemSelected(item);
           }
       }*/
    @Override
    public void onRestart() {
        super.onRestart();
        this.onCreate(null);
    }
}
