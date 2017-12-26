package com.stewardbank.omnichannel.activity;

import android.Manifest;
import android.app.ProgressDialog;
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
import com.stewardbank.omnichannel.business.rest.CustomerService;
import com.stewardbank.omnichannel.business.rest.ServiceGenerator;
import com.stewardbank.omnichannel.business.util.AppUtil;
import com.stewardbank.omnichannel.business.util.HttpStatus;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;

/**
 * @uthor Tasu Muzinda
 */
public class ProofOfResidencePickerActivity extends BaseActivity implements View.OnClickListener{

    ImageView imageView;
    TextView textView;
    View parentView;
    String imagePath;
    String idNumber;
    Button save;
    String userName;
    String password;
    ProgressDialog progressDialog;
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
        userName = AppUtil.getUsername(this);
        password = AppUtil.getPassword(this);
        progressDialog = new ProgressDialog(this);
        setSupportActionBar(getToolbar("Step 5:: Capture Proof Of Residence"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == save.getId()){
            save();
        }
    }

    public void save(){
        Customer current = Customer.findByIdNumber(idNumber);
        if(imageView.getVisibility() == View.VISIBLE){
            if(current != null){
                current.proofOfResidenceFilePath = imagePath;
                current.isComplete = 1;
                current.save();
                Customer newCurrent = Customer.findByIdNumber(idNumber);
                Log.d("Current", AppUtil.createGson().toJson(newCurrent));
                if(AppUtil.isNetworkAvailable(this)){
                    progressDialog.setTitle("Uploading data to server");
                    progressDialog.setCancelable(true);
                    progressDialog.show();
                    submitToServer(newCurrent);
                }else{
                    AppUtil.createShortNotification(this, "Network unavailable. Please check connectivity");
                    Intent intent = new Intent(this, CustomerActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }else{
            AppUtil.createShortNotification(this, "Please choose proof of residence first");
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
            } else {
                textView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                Snackbar.make(parentView, R.string.string_unable_to_load_image, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    public void postSignatureImage(final Customer customer){
        CustomerService customerService = ServiceGenerator.createService(CustomerService.class, userName, password);
        String path = customer.signatureFilePath;
        File file = new File(path);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        retrofit2.Call<okhttp3.ResponseBody> req = customerService.postSignatureImage(body, name, customer.idNumber);
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    postIdImage(customer);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void postIdImage(final Customer customer){
        CustomerService customerService = ServiceGenerator.createService(CustomerService.class, userName, password);
        String path = customer.idFilePath;
        File file = new File(path);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        retrofit2.Call<okhttp3.ResponseBody> req = customerService.postIdImage(body, name, customer.idNumber);
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    postHeadShotImage(customer);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void postHeadShotImage(final Customer customer){
        CustomerService customerService = ServiceGenerator.createService(CustomerService.class, userName, password);
        String path = customer.headshotFilePath;
        File file = new File(path);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        retrofit2.Call<okhttp3.ResponseBody> req = customerService.postHeadShotImage(body, name, customer.idNumber);
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    postProofOfResidenceImage(customer);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void postProofOfResidenceImage(final Customer customer){
        CustomerService customerService = ServiceGenerator.createService(CustomerService.class, userName, password);
        String path = customer.proofOfResidenceFilePath;
        File file = new File(path);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        retrofit2.Call<okhttp3.ResponseBody> req = customerService.postProofOfResidenceImage(body, name, customer.idNumber);
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.hide();
                customer.delete();
                Intent intent = new Intent(getApplicationContext(), CustomerActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                progressDialog.hide();
            }
        });
    }

    public void submitToServer(final Customer customer){
        CustomerService customerService = ServiceGenerator.createService(CustomerService.class, userName, password);
        Call<HttpStatus> call = customerService.addCustomer(customer);
        call.enqueue(new Callback<HttpStatus>() {
            @Override
            public void onResponse(Call<HttpStatus> call, Response<HttpStatus> response) {
                if(response.isSuccessful()){
                    if(response.body().getStatus() == 1){
                        postSignatureImage(customer);
                    }else{
                        AppUtil.createLongNotification(getApplicationContext(), "Record already exists");
                    }
                }
            }

            @Override
            public void onFailure(Call<HttpStatus> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });
    }
}
