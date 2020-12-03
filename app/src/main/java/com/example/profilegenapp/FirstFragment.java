package com.example.profilegenapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class FirstFragment extends Fragment {

    Button sendProfileButton;
    Button returnFromProfileButton;
    Button profileResetButton;
    TextView profileReturnMessage;
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
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fname = view.findViewById(R.id.fnameInput);
        lname = view.findViewById(R.id.lnameInput);
        instrument = view.findViewById(R.id.instrumentInput);
        email = view.findViewById(R.id.emailInput);
        sendProfileButton = view.findViewById(R.id.sendProfileButton);
        returnFromProfileButton = view.findViewById(R.id.returnFromProfileButton);
        profileResetButton = view.findViewById(R.id.profileResetButton);
        profileReturnMessage = view.findViewById(R.id.profileReturnMessage);

        sendProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postData("https://profilegen.sites.tjhsst.edu/profile_mobile_post");
            }
        });
        //return to starting MainActivity
        returnFromProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        profileResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fname.setText("");
                lname.setText("");
                instrument.setText("");
                email.setText("");
                profileReturnMessage.setText("");
            }
        });

    }


    public void postData(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JSONObject sendData = new JSONObject();
        if (isValid(fname) && isValid(lname) && isValid(instrument) && !TextUtils.isEmpty(email.getText())) { //email can contain numbers
            try {
                sendData.put("firstname", fname.getText().toString().trim());
                sendData.put("lastname", lname.getText().toString().trim());
                sendData.put("instrument", instrument.getText().toString().trim());
                sendData.put("email", email.getText().toString().trim());
                sendData.put("processType", "input");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println(sendData);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, sendData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                profileReturnMessage.setText(response.getString("message"));
                                Toast.makeText(getActivity(), "Sent!", Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            profileReturnMessage.setText("Volley Error in receiving from ProfileGen");
                            Toast.makeText(getActivity(), "Volley Error", Toast.LENGTH_LONG).show();
                        }
                    }
            );
            requestQueue.add(jsonObjectRequest);
        } else {
            Toast.makeText(getActivity(), "Please fill out all inputs correctly before sending", Toast.LENGTH_LONG).show();
        }

    }

    public boolean isValid(EditText e) {
        return !TextUtils.isEmpty(e.getText()) && e.getText().toString().matches("^[a-zA-Z ]+$");
    }

}