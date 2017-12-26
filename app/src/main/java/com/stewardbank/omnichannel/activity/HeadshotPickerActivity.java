package com.stewardbank.omnichannel.activity;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.stewardbank.omnichannel.R;
import com.stewardbank.omnichannel.activity.utils.PermissionsChecker;
import com.stewardbank.omnichannel.business.domain.Customer;
import com.stewardbank.omnichannel.business.util.AppUtil;

import java.io.File;

public class HeadshotPickerActivity extends BaseActivity implements View.OnClickListener {

    ImageView imageView;
    TextView textView;
    View parentView;
    String imagePath;
    String idNumber;
    Button save;
    private static final String[] PERMISSIONS_READ_STORAGE = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    PermissionsChecker checker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picker);
        textView = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);
        parentView = (View) findViewById(R.id.parentView);
        save = (Button) findViewById(R.id.btn_save);
        save.setOnClickListener(this);
        checker = new PermissionsChecker(this);
        Intent intent = getIntent();
        idNumber = intent.getStringExtra("idNumber");
        setSupportActionBar(getToolbar("Step 3:: Capture Head Shot"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == save.getId()){
            if(imageView.getVisibility() == View.VISIBLE){
                Intent intent = new Intent(this, IDPickerActivity.class);
                intent.putExtra("idNumber", idNumber);
                startActivity(intent);
                finish();
            }else{
                AppUtil.createShortNotification(this, "Please select head shot image before proceeding");
            }

        }
    }

    public void showImagePopup(View view) {
        if (checker.lacksPermissions(PERMISSIONS_READ_STORAGE)) {
            startPermissionsActivity(PERMISSIONS_READ_STORAGE);
        } else {
            // File System.
            final Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_PICK);

            // Chooser of file system options.
            final Intent chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.string_choose_image));
            startActivityForResult(chooserIntent, 1010);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1010) {
            if (data == null) {
                Snackbar.make(parentView, R.string.string_unable_to_pick_image, Snackbar.LENGTH_INDEFINITE).show();
                return;
            }
            Uri selectedImageUri = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imagePath = cursor.getString(columnIndex);
                Log.d("Image", imagePath);

                Picasso.with(getApplicationContext()).load(new File(imagePath))
                        .into(imageView);

                Snackbar.make(parentView, R.string.string_reselect, Snackbar.LENGTH_LONG).show();
                cursor.close();

                textView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                Customer current = Customer.findByIdNumber(idNumber);
                if(current != null){
                    current.headshotFilePath = imagePath;
                    current.save();
                    Customer newCurrent = Customer.findByIdNumber(idNumber);
                    Log.d("Current", AppUtil.createGson().toJson(newCurrent));
                }
            } else {
                textView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                Snackbar.make(parentView, R.string.string_unable_to_load_image, Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
