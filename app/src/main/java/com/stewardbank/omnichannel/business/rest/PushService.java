package com.stewardbank.omnichannel.business.rest;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.stewardbank.omnichannel.activity.CustomerActivity;
import com.stewardbank.omnichannel.business.domain.Customer;
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
public class PushService extends IntentService {

    public static final String NOTIFICATION = "com.stewardbank.omnichannel";
    private Context context = this;
    public static final String RESULT = "result";
    String userName;
    String password;

    public PushService(){
        super("PushService");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        userName = AppUtil.getUsername(context);
        password = AppUtil.getPassword(context);
        Log.d("Test", userName);
        int result = Activity.RESULT_OK;
        try{
            for(Customer item : Customer.findComplete()){
                submitToServer(item, userName, password);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            result = Activity.RESULT_CANCELED;
        }
        publishResults(result);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getApplicationContext();
        return super.onStartCommand(intent, flags, startId);
    }

    private void publishResults(int result) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }

    public void postIdImage(final Customer customer, final String userName, final String password){
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
                    postHeadShotImage(customer, userName, password);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void postHeadShotImage(final Customer customer, final String userName, final String password){
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
                    postProofOfResidenceImage(customer, userName, password);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void postProofOfResidenceImage(final Customer customer, String userName, String password){
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
                customer.delete();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void submitToServer(final Customer customer, final String userName, final String password){
        CustomerService customerService = ServiceGenerator.createService(CustomerService.class, userName, password);
        Call<HttpStatus> call = customerService.addCustomer(customer);
        call.enqueue(new Callback<HttpStatus>() {
            @Override
            public void onResponse(Call<HttpStatus> call, Response<HttpStatus> response) {
                if(response.isSuccessful()){
                    if(response.body().getStatus() == 1){
                        postSignatureImage(customer, userName, password);
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

    public void postSignatureImage(final Customer customer, final String userName, final String password){
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
                    postIdImage(customer, userName, password);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


}
