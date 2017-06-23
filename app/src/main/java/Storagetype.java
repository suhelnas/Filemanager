package com.example.pc.filemanager;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;

public class Storagetype extends Activity {
    private static final int PERMISSION_REQUEST_CODE = 1;
TextView internalspace,externalsapce;
    Button b1, b2;
    File internal,external;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storagetype);
        b1 = (Button) findViewById(R.id.internal);
        b2 = (Button) findViewById(R.id.external);
        internalspace = (TextView) findViewById(R.id.internalspace);
        externalsapce= (TextView) findViewById(R.id.externalspace);
        if (Build.VERSION.SDK_INT > 22) {
            if (checkPermission()) {
            }
            else {
                requestPermission();
            }

        }
        partition();

    }
public void partition(){
    if(Build.VERSION.SDK_INT<=22){
      internal=Environment.getExternalStorageDirectory();
        String st=System.getenv("SECONDARY_STORAGE");
        external=new File(st);

    }
    else {
        File[] f = ContextCompat.getExternalFilesDirs(Storagetype.this, null);
        if (f.length > 1) {
            internal = f[0];
            external = f[1];

        } else
            internal = f[0];
    }
    long inttotal=internal.getTotalSpace();
    long intused=internal.getUsableSpace();
    internalspace.setText(getFileSize(intused)+" available of total "+getFileSize(inttotal));
    if(external!=null) {
        long exttotal = external.getTotalSpace();
        long extused = external.getUsableSpace();
        externalsapce.setText(getFileSize(extused) + " available of total " + getFileSize(exttotal));
    }
}
    public void internal(View view) {

        Intent intent = new Intent(Storagetype.this, Directorylist.class);
        intent.putExtra("directory",getRoot(internal.getAbsolutePath()));
        startActivity(intent);

    }
    public static String getFileSize(long size) {
        if (size <= 0)
            return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public void external(View view) {
if(external!=null){
        Intent intent = new Intent(Storagetype.this, Directorylist.class);
                intent.putExtra("directory",getRoot(external.getAbsolutePath()));
                startActivity(intent);

            }
        else
            Toast.makeText(Storagetype.this, "Sd card is not available", Toast.LENGTH_SHORT).show();

    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(Storagetype.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private void requestPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(Storagetype.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(Storagetype.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(Storagetype.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    public void Share(View view) {
        ApplicationInfo app = getApplicationContext().getApplicationInfo();
        String filePath = app.sourceDir;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/vnd.android.package-archive");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
        startActivity(Intent.createChooser(intent, "Share via"));
    }
    public String getRoot(String st){
        for(int i=0;i<st.length();i++){
            if(st.charAt(i)=='/'&&st.charAt(i+1)=='A')
                st=st.substring(0,i);
        }
        return  st;
    }
}
