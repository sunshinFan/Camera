package com.example.api;


import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.concurrent.Semaphore;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// this class implements API to communicate with server
public class DiagReport {

    // json part
    @Expose@SerializedName("Gender")
    private String gender;          //性别
    @Expose@SerializedName("Name")
    private String name;            //姓名
    @Expose@SerializedName("Number")
    private String roomNomber;      //病人ID(住院号、病床号、或者房间号)

    private DiagResult diagResult;

    // image data should be in jpeg,png or other common formatW
    private byte[] imageData = null;

    private String url;

    public DiagReport(String url) {
        this.url = url;
    }

    public DiagReport() {
        this.url = "http://58.196.154.129:8080";
    }

    public DiagReport setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public DiagReport setName(String name) {
        this.name = name;
        return this;
    }

    public DiagReport setRoomNomber(String roomNomber) {
        this.roomNomber = roomNomber;
        return this;
    }

    public DiagReport setImageData(byte[] imageData) {
        this.imageData = imageData;
        return this;
    }

    public DiagReport setUrl(String url) {
        this.url = url;
        return this;
    }

    public DiagResult getDiagResult() {
        return diagResult;
    }

    public String getJson(){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }

    public boolean SendRequest(){
        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpeg");
        final OkHttpClient client = new OkHttpClient();

        // sending request;
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("info", this.getJson())
                .addFormDataPart("uploadfile", "logo-square.png",
                        RequestBody.create(MEDIA_TYPE_PNG, this.imageData))
                .build();
        Request request = new Request.Builder()
                .url(url+"/makeDemo")
                .post(requestBody)
                .build();
        Response response = null;
        byte []responseBody = null;
        try {
            response = client.newCall(request).execute();
            responseBody = response.body().bytes();
        } catch (IOException e) {
            response = null;
            e.printStackTrace();
            return false;
        }
        Log.i(CommonOperations.logTag,"Here1");

        // fetching result
        String respStr = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            respStr = new String(responseBody, StandardCharsets.UTF_8);
            Log.i(CommonOperations.logTag, "Response : " + respStr);
        } else {
            Log.i(CommonOperations.logTag,"Error Cannot convert");
        }
        diagResult = DiagResult.NewDiagReport(respStr,url);
        Log.i(CommonOperations.logTag,"diagResult constructed");
        diagResult.RetriveData();
        Log.i(CommonOperations.logTag,"Retrive finished");


        return true;
    }
}

class CommonOperations{
    static String logTag = "CommonOperations";
    final static OkHttpClient client = new OkHttpClient();

    static byte[] getFromUrl(String url){
        Semaphore available = new Semaphore(0, true);
        Request request = new Request.Builder()
                .url(url)
                .build();

        class MyCallback implements Callback{
            public byte []retval;
            Semaphore semaphore;

            public MyCallback(Semaphore semaphore) {
                this.semaphore = semaphore;
            }

            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.i(logTag,"request fail");
                semaphore.release();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                    Log.i(logTag,responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                this.retval = response.body().bytes();
                semaphore.release();
            }

        }

        MyCallback callback = new MyCallback(available);

        Log.i(CommonOperations.logTag, "Sending request");
        client.newCall(request).enqueue(callback);
        try {
            available.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        return callback.retval;
    }
}

class DiagResult{
    @Expose@SerializedName("OriginalName")
    String originalName = null;
    @Expose@SerializedName("PdfName")
    String pdfName = null;
    @Expose@SerializedName("SegName")
    String segName = null;
    @Expose@SerializedName("Desp")
    String desp[][] = null;

    private byte originalImage[];
    private byte segmentedImage[];

    public byte[] getDiagReportPdf() {
        return diagReportPdf;
    }

    public byte[] getOriginalImage() {
        return originalImage;
    }

    public byte[] getSegmentedImage() {
        return segmentedImage;
    }

    private byte diagReportPdf[];
    private String url;


    public static DiagResult NewDiagReport(String data, String url){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        DiagResult diagResult = gson.fromJson(data,DiagResult.class);
        diagResult.setUrl(url);
        return diagResult;
    }

    public void RetriveData(){
        // Note that segmented image is not writen to disk yet
//        Log.i(CommonOperations.logTag,"getting segment image");
//        segmentedImage = CommonOperations.getFromUrl(url+segName);
        Log.i(CommonOperations.logTag,"getting pdf");
        diagReportPdf = CommonOperations.getFromUrl(url + pdfName);
        Log.i(CommonOperations.logTag,"getting Original");
        originalImage = CommonOperations.getFromUrl(url+originalName);
        Log.i(CommonOperations.logTag,"getting retrive finished");
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
