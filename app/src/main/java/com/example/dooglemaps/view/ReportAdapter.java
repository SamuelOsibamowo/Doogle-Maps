package com.example.dooglemaps.view;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dooglemaps.R;
import com.example.dooglemaps.viewModel.Post;
import com.example.dooglemaps.viewModel.Report;
import com.example.dooglemaps.viewModel.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.MyViewHolder>{


    private static final String TAG = "ReportAdapter";
    Context context;
    ArrayList<Report> reports;

    public ReportAdapter(Context context, ArrayList<Report> reports) {
        this.context = context;
        this.reports = reports;
    }

    @NonNull
    @Override
    public ReportAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_report, parent, false );
        return new ReportAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportAdapter.MyViewHolder holder, int position) {
        Report report = reports.get(position);
        Log.i(TAG, "Image URL: " + report.getImageUrl() + " Post Description: " + report.getDescription());
        holder.bind(report);
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView ivReportedPet;
        TextView tvReportDescription;
        TextView tvUsername;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            tvReportDescription= itemView.findViewById(R.id.tvReportDescription);
            ivReportedPet = itemView.findViewById(R.id.ivReportedPet);
            tvUsername = itemView.findViewById(R.id.tvUsername);
        }

        public void bind(Report report) {
            String description = "<b>" + "Info: " + "</b>" + report.getDescription();
            String userId = report.getUserId();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userId);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    tvUsername.setText(user.getUsername());
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tvReportDescription.setText(Html.fromHtml(description));
            Glide.with(context)
                    .load(report.getImageUrl())
                    .centerCrop()
                    .into(ivReportedPet);
        }

        @Override
        public void onClick(View v) {
            // Goes to the detail activity for the specified instagram post
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION){
                Report report = reports.get(position);
                Intent intent = new Intent(context, ReportDetailedActivity.class);
                intent.putExtra(Report.class.getSimpleName(), Parcels.wrap(report));
                context.startActivity(intent);

            }

        }
    }
}
