package com.example.profilegenapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class SecondFragment extends Fragment {

    Button searchButton;
    Button returnFromSearchButton;
    Button searchResetButton;
    TextView searchReturnMessage;
    EditText fname;
    EditText lname;
    EditText instrument;
    EditText email;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fname = view.findViewById(R.id.fnameSearch);
        lname = view.findViewById(R.id.lnameSearch);
        instrument = view.findViewById(R.id.instrumentSearch);
        email = view.findViewById(R.id.emailSearch);
        searchButton = view.findViewById(R.id.searchButton);
        returnFromSearchButton = view.findViewById(R.id.returnFromSearchButton);
        searchResetButton = view.findViewById(R.id.searchResetButton);
        searchReturnMessage = view.findViewById(R.id.searchReturnMessage);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData("https://profilegen.sites.tjhsst.edu/profile_mobile_get");
            }
        });

        returnFromSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        searchResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fname.setText("");
                lname.setText("");
                instrument.setText("");
                email.setText("");
                searchReturnMessage.setText("");
            }
        });
    }

    public void getData(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        url += "?firstname=" + fname.getText().toString().trim() + "&lastname=" + lname.getText().toString().trim() +
                "&instrument=" + instrument.getText().toString().trim() + "&email=" + email.getText().toString().trim() +
                "&processType=search";
        System.out.println(url);
        searchReturnMessage.setMovementMethod(new ScrollingMovementMethod());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //loop through response entries and print them
                            if (response.getString("message").equals("success")) {
                                response.remove("message");
                                String returnMessage = ""; int resultCount = 0;
                                Iterator<String> keys = response.keys();
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    returnMessage += response.get(key) + "\n";
                                    resultCount += 1;
                                }
                                System.out.println(returnMessage);
                                System.out.println(response);
                                searchReturnMessage.setText(returnMessage);
                                Toast.makeText(getActivity(), resultCount + " result(s) found", Toast.LENGTH_LONG).show();
                            } else {
                                searchReturnMessage.setText(response.getString("message"));
                                Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        searchReturnMessage.setText("Volley Error in receiving from ProfileGen");
                        Toast.makeText(getActivity(), "Volley Error", Toast.LENGTH_LONG).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

}
