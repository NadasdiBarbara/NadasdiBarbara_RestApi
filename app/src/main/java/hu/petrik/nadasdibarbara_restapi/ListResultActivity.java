package hu.petrik.nadasdibarbara_restapi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListResultActivity extends AppCompatActivity {
    private AppCompatButton btnVissza;
    private ListView listVarosok;

    public static List<Varos> varosList = new ArrayList<>();

    private String url = "https://retoolapi.dev/G1VM4r/varosok";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_result);
        btnVissza = findViewById(R.id.btnVissza);
        listVarosok = findViewById(R.id.listVarosok);

        btnVissza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListResultActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        RequestTask cityTask = new RequestTask(url, "GET");
        cityTask.execute();
    }

    private class RequestTask extends AsyncTask<Void, Void, Response> {
        String requestUrl;
        String requestType;
        String requestParams;

        public RequestTask(String requestUrl, String requestType, String requestParams) {
            this.requestUrl = requestUrl;
            this.requestType = requestType;
            this.requestParams = requestParams;
        }

        public RequestTask(String requestUrl, String requestType) {
            this.requestUrl = requestUrl;
            this.requestType = requestType;
        }

        @Override
        protected Response doInBackground(Void... voids) {
            Response response = null;
            try {
                response = RequestHandler.get(requestUrl);

            } catch (IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(ListResultActivity.this, e.toString(), Toast.LENGTH_SHORT).show());
            }
            return response;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            Gson converter = new Gson();
            if (response == null | response.getResponseCode() >= 400) {
                Toast.makeText(ListResultActivity.this, "Hiba történt a kérésnek feldolgozása során!", Toast.LENGTH_SHORT).show();

            }
            else {
                Varos[] places =  converter.fromJson(response.getContent(), Varos[].class);
                varosList.clear();
                varosList.addAll(Arrays.asList(places));
                ArrayAdapter<Varos> cityArrayAdapter = new ArrayAdapter<>(ListResultActivity.this, R.layout.list_varosok, R.id.listItemVarosok, varosList);
                listVarosok.setAdapter(cityArrayAdapter);
            }
        }


    }


}