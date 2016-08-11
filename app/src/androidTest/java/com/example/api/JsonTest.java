package com.example.api;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)

@SmallTest
public class JsonTest {
    private final String loglabel = "Gson";

    @Before
    public void createLogHistory() {

    }

    @Test
    public void GsonGenertateTest() {
        assertThat(true,is(true));
        assertThat(true,is(true));
        Log.i(loglabel,"testing1");
        DiagReport diagReport = new DiagReport();
        diagReport.setGender("男");
        diagReport.setName("张三");
        diagReport.setRoomNomber("12323985");
        Log.i(loglabel,diagReport.getJson());
    }

    @Test
    public void httpGetTest(){
        Log.i(loglabel,"testing2");
        byte[] retval = CommonOperations.getFromUrl("http://58.196.154.129:8080/templete/moban1239/js/deepDR.js");
        assertThat(retval,notNullValue());
        Log.i(loglabel,retval.toString());
    }

    @Test
    public void overallTest(){
        Log.i(loglabel,"Overall test");
        DiagReport diagReport = new DiagReport();
        byte []originalImage = CommonOperations.getFromUrl("http://58.196.154.129:8080/templete/tmp/C0011213.2.jpg");
        assertThat(originalImage,notNullValue());
        diagReport.setImageData(originalImage)
                .setRoomNomber("00-000-0123")
                .setGender("Not set")
                .setName("male");
        diagReport.SendRequest();
        String desp[][] = diagReport.getDiagResult().desp;
        for (int i = 0; i < desp.length;i++){
            for(int j = 0; j < desp[i].length; j++){
                Log.i(loglabel,desp[i][j]);
            }
        }
    }
}
