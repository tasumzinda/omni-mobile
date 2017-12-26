package com.stewardbank.omnichannel.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.kyanogen.signatureview.SignatureView;
import com.stewardbank.omnichannel.R;
import com.stewardbank.omnichannel.activity.utils.PermissionsChecker;
import com.stewardbank.omnichannel.business.domain.Customer;
import com.stewardbank.omnichannel.business.util.AppUtil;

import java.io.*;

public class SignatureActivity extends BaseActivity implements View.OnClickListener{

    private SignatureView signatureView;
    private String idNumber;
    @BindView(R.id.btn_save)
    Button save;
    @BindView(R.id.btn_clear)
    Button clear;
    private static final String[] PERMISSIONS_READ_STORAGE = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    PermissionsChecker checker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);//see xml layout
        ButterKnife.bind(this);
        signatureView = (SignatureView) findViewById(R.id.signature_view);
        signatureView.setPenColor(Color.RED);
        Intent intent = getIntent();
        idNumber = intent.getStringExtra("idNumber");
        save.setOnClickListener(this);
        clear.setOnClickListener(this);
        checker = new PermissionsChecker(this);
        setSupportActionBar(getToolbar("Step 2:: Capture Signature"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == clear.getId()){
            signatureView.clearCanvas();
        }

        if(view.getId() == save.getId()){
            saveSignature();
        }
    }

    public void saveSignature(){
        if(checker.lacksPermissions(PERMISSIONS_READ_STORAGE)){
            startPermissionsActivity(PERMISSIONS_READ_STORAGE);
        }else{
            File directory = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File file = new File(directory, idNumber+".png");
            FileOutputStream out = null;
            Bitmap bitmap = signatureView.getSignatureBitmap();
            try {
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){

                    }
                }
                out = new FileOutputStream(file);
                if(bitmap!=null){
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                }else{
                    AppUtil.createShortNotification(this, "Not found");
                    throw new FileNotFoundException();

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.flush();
                        out.close();

                        if(bitmap!=null){
                            Toast.makeText(getApplicationContext(),
                                    "Image saved successfully at "+ file.getPath(),Toast.LENGTH_LONG).show();
                            Log.d("Path", file.getPath());
                            Customer current = Customer.findByIdNumber(idNumber);
                            if(current != null){
                                current.signatureFilePath = file.getPath();
                                current.save();
                                Customer newCurrent = Customer.findByIdNumber(idNumber);
                                Log.d("Customer", AppUtil.createGson().toJson(newCurrent));
                                Intent intent = new Intent(getApplicationContext(), HeadshotPickerActivity.class);
                                intent.putExtra("idNumber", idNumber);
                                startActivity(intent);
                                finish();
                            }

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


}
