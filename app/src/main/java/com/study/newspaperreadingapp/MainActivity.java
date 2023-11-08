package com.study.newspaperreadingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter adapters;
    private ArrayList<String> titles;
    private ArrayList<String> links;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView=findViewById(R.id.listViewTitle);
        titles=new ArrayList<>();
        links=new ArrayList<>();

        adapters=new ArrayAdapter(this, android.R.layout.simple_list_item_1,titles);

        listView.setAdapter(adapters);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MainActivity.this,DetailsActivity.class);
                intent.putExtra("link",links.get(position));
                startActivity(intent);
            }
        });

        new ReadRSS().execute("https://vnexpress.net/rss/giao-duc.rss");
    }

    private class ReadRSS extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            URL url= null;
            StringBuilder content=new StringBuilder();
            try {
                url = new URL(strings[0]);
                InputStreamReader inputStreamReader=new InputStreamReader(url.openConnection().getInputStream());
                BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
                String line="";
                while((line=bufferedReader.readLine())!=null){
                    content.append(line);
                }
                bufferedReader.close();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return content.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            XMLDOMParser parser=new XMLDOMParser();

            Document document=parser.getDocument(s);
            // Lấy ra danh sách các elements có tên item
            NodeList nodeList=document.getElementsByTagName("item");

            for(int i=0;i<nodeList.getLength();i++){
                Element e= (Element) nodeList.item(i);
                titles.add(parser.getValue(e,"title"));
                links.add(parser.getValue(e,"link"));
            }

            adapters.notifyDataSetChanged(); // Cập nhật dữ liệu thay đổi

//            Toast.makeText(MainActivity.this, title, Toast.LENGTH_LONG).show();
        }
    }
}