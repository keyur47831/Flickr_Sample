package com.code4sharing.flickrsample.parser;

import android.support.v7.appcompat.BuildConfig;

import com.code4sharing.flickrsample.model.FlickrDataModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
/**
 * Created by keyur on 22-08-2015.
 */
@Config(sdk = 18,
        manifest = "src/main/AndroidManifest.xml",
        resourceDir = "../../../app/build/intermediates/res/" + BuildConfig.FLAVOR + "/" + BuildConfig.BUILD_TYPE)
@RunWith(RobolectricTestRunner.class)
public class FlickrJsonParserTest {
        @Test
        public void testParseFactJsonDataNullChecks() throws Exception
        {
                FlickrJsonParser factParser = new FlickrJsonParser();
                List<FlickrDataModel> factList = null;
                factList = new ArrayList<> ();
               factParser.parseFactJsonData("", factList);
                assertThat (factList.isEmpty ());
        }
    @Test
    public void testParseFactJsonDataWithDataSetHavingProperValues() throws Exception
    {
        final String DataSet1 = "src/test/assets/data1.json";

        FlickrJsonParser factParser = new FlickrJsonParser();
        List<FlickrDataModel> factList = new ArrayList<>();

        String content;

        content = getContent (DataSet1);
        factParser.parseFactJsonData (content,factList);
        assertThat(factList.size()).isEqualTo(2);

    }
    private String getContent(String filePath)
    {
        File jsonDataFile = new File (filePath);
        assertThat(jsonDataFile).isNotNull ();


        String content = null;
        try
        {
            content = new Scanner (jsonDataFile).useDelimiter("\\Z").next();
        }
        catch (FileNotFoundException e)
        {
            fail("Dataset is not found");
        }
        assertThat(content).isNotNull ();
        return content;
    }
}
