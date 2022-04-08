package hu.petrik.nadasdibarbara_restapi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

public class InsertActivity extends AppCompatActivity {
    private EditText editNev, editOrszag, editLakossag;
    private AppCompatButton btnfelvetel, btnuvissza;

    private String url = "https://retoolapi.dev/G1VM4r/varosok";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        init();
        btnuvissza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InsertActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnfelvetel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                varosHozzadasa();
            }
        });

    }
    public void init(){
        editNev = findViewById(R.id.editNev);
        editOrszag = findViewById(R.id.editOrszag);
        editLakossag = findViewById(R.id.editLakossag);
        btnfelvetel = findViewById(R.id.btnfelvetel);
        btnuvissza = findViewById(R.id.btnuvissza);
    }

    private boolean validacio(String nev, String orszag, String lakossagString) {
        if (nev.isEmpty()) {
            Toast.makeText(this, "Név megadása kötelező", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (orszag.isEmpty()) {
            Toast.makeText(this, "Ország megadása kötelező", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (lakossagString.isEmpty()) {
            Toast.makeText(this, "Lakosság megadása kötelező", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void alaphelyzet() {
        editNev.setText("");
        editOrszag.setText("");
        editLakossag.setText("");
    }

    private void varosHozzadasa() {
        String nev = editNev.getText().toString().trim();
        String orszag = editOrszag.getText().toString().trim();
        String lakossagString = editLakossag.getText().toString().trim();
        if (nev.isEmpty() || orszag.isEmpty() || lakossagString.isEmpty()) {
            btnfelvetel.setEnabled(false);
        }
        if (!validacio(nev, orszag, lakossagString)) {
            Toast.makeText(InsertActivity.this, "Sikertelen felvétel", Toast.LENGTH_SHORT).show();
            return;
        }
        int lakossag = Integer.parseInt(lakossagString);
        Varos city = new Varos(0, nev, orszag, lakossag);
        Toast.makeText(InsertActivity.this, "Sikeres felvétel", Toast.LENGTH_SHORT).show();
        Gson jsonConverter = new Gson();
        RequestTask task = new RequestTask(url, "POST", jsonConverter.toJson(city));
        task.execute();
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
            response = RequestHandler.post(requestUrl, requestParams);
        } catch (IOException e) {
            runOnUiThread(() -> Toast.makeText(InsertActivity.this, e.toString(), Toast.LENGTH_SHORT).show());
        }
        return response;
    }

    @Override
    protected void onPostExecute(Response response) {
        super.onPostExecute(response);
        Gson converter = new Gson();
        if (response.getResponseCode() >= 400){
            Toast.makeText(InsertActivity.this, "Hiba történt a kérés feldolgozása során", Toast.LENGTH_SHORT).show();
        }
        else {
            Varos varos = converter.fromJson(response.getContent(), Varos.class);
            ListResultActivity.varosList.add(0, varos);
            alaphelyzet();
        }
    }
}
}