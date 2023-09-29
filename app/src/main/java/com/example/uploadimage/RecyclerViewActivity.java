package com.example.uploadimage;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class RecyclerViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;  // Declaring a RecyclerView variable called 'recyclerView'
    private ProgressDialog progressDialog;  // Declaring a ProgressDialog variable called 'progressDialog'
    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();  // Declaring an ArrayList variable called 'arrayList' to store HashMap objects
    private HashMap<String, String> hashMap;  // Declaring a HashMap variable called 'hashMap'

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        recyclerView = findViewById(R.id.recyclerView);  // Initializing 'recyclerView' with the RecyclerView from the layout with the id 'recyclerView'
        progressDialog = new ProgressDialog(this);  // Creating a new ProgressDialog instance

        progressDialog.setMessage("Please wait...");  // Setting the message for the progress dialog
        progressDialog.setCanceledOnTouchOutside(false);  // Setting the progress dialog to be non-cancelable
        progressDialog.show();  // Showing the progress dialog

        String URL = "https://thelightsurprise.com/user.php";  // URL for fetching user data
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        progressDialog.dismiss();  // Dismissing the progress dialog after receiving the response

                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject userObject = response.getJSONObject(i);
                                String path = userObject.getString("path");  // Extracting the "path" value from the JSON object

                                hashMap = new HashMap<>();
                                hashMap.put("path", path);  // Adding the "path" value to the HashMap
                                arrayList.add(hashMap);  // Adding the HashMap to the ArrayList
                            }

                            MyAdapter myAdapter = new MyAdapter();
                            recyclerView.setAdapter(myAdapter);  // Setting the adapter for the RecyclerView
                            recyclerView.setLayoutManager(new LinearLayoutManager(RecyclerViewActivity.this));  // Setting the layout manager for the RecyclerView
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();  // Dismissing the progress dialog on error
                        Toast.makeText(RecyclerViewActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();  // Displaying an error message
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(RecyclerViewActivity.this);  // Creating a new request queue
        requestQueue.add(jsonArrayRequest);  // Adding the JSON array request to the request queue
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.layout, parent, false);  // Inflating the layout for each item in the RecyclerView
            return new MyViewHolder(itemView);  // Returning a new instance of MyViewHolder
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            HashMap<String, String> hashMap = arrayList.get(position);
            String path = hashMap.get("path");  // Retrieving the "path" value from the HashMap

            String imageUrl = "https://thelightsurprise.com/" + path;  // Constructing the URL for the image
            Picasso.get().load(imageUrl).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(holder.path);  // Using Picasso library to load the image into the ImageView
        }

        @Override
        public int getItemCount() {
            return arrayList.size();  // Returning the size of the ArrayList
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView path;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                path = itemView.findViewById(R.id.path);  // Initializing 'path' with the ImageView from the layout with the id 'path'
            }
        }
    }
}