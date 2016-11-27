package com.lastfighting.association.association;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.lastfighting.association.association.Http.Http;
import com.lastfighting.association.association.Http.RetData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReceiveNotice extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_notice);

        final ListView mNoticeListView = (ListView) findViewById(R.id.NoticeList);
        final List<String> list = new ArrayList<String>();
        final Runnable updateThread = new Runnable()
        {

            @Override
            public void run()
            {
                // 建立数据源
                String[] mItems =  list.toArray(new String[list.size()]);
                // 建立Adapter并且绑定数据源
                ArrayAdapter<String> adapter=new ArrayAdapter<String>(ReceiveNotice.this,android.R.layout.simple_list_item_1, mItems);
                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                //绑定 Adapter到控件
                mNoticeListView .setAdapter(adapter);
            }

        };
        Runnable oneRunnable = new Runnable() {
            @Override
            public void run() {
                RetData ret = new RetData();
                StringBuffer result = Http.Post(Config.GetNoitceUrl,null,ret);
                try {
                    JSONObject redata = new JSONObject(new String(result));
                    JSONArray list_array = redata.getJSONArray("errmsg");

                    for (int i=0; i<list_array.length(); i++) {
                        list.add( list_array.getString(i) );
                    }
                    ReceiveNotice.this.runOnUiThread(updateThread);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(oneRunnable).start();
    }

}
