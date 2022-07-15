package com.example.dooglemaps.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dooglemaps.R;
import com.example.dooglemaps.fragments.ApiService;
import com.example.dooglemaps.notifications.Client;
import com.example.dooglemaps.notifications.Data;
import com.example.dooglemaps.notifications.MyResponse;
import com.example.dooglemaps.notifications.Sender;
import com.example.dooglemaps.notifications.Token;
import com.example.dooglemaps.viewModel.Post;
import com.example.dooglemaps.viewModel.Report;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportCreationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,GoogleMap.OnMarkerDragListener{

    private static final String TAG = "ReportCreationAct";
    private static final int IMAGE_CONSTRAINT = 10;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public static final String DATABASE_REPORT_PATH = "reports";


    private EditText etAnimalDescription;
    private Button btnTakePic;
    private TextView tvGoBack, tvSubmit;
    private Spinner spinAnimal;

    private Bitmap takenImage;
    private File photoFile;
    public String photoFileName = "photo.jpg";
    private String animalFromSpinner = "Dog";


    private Uri imageUri;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private FirebaseUser user;
    private ApiService apiService;



    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private BitmapDescriptor pawPinDescriptor;
    private LatLng changedMarkerLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_creation);


        changedMarkerLoc = (LatLng) getIntent().getExtras().get("latlng");
        apiService = Client.getClient("https://fcm.googleapis.com/").create(ApiService.class);



        spinAnimal = findViewById(R.id.spinAnimal);
        tvGoBack = findViewById(R.id.tvGoBack);
        tvSubmit = findViewById(R.id.tvSubmit);
        btnTakePic = findViewById(R.id.btnTakePic);
        etAnimalDescription = findViewById(R.id.etAnimalDescription);

        user = FirebaseAuth.getInstance().getCurrentUser();
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference(DATABASE_REPORT_PATH);
        storageReference = FirebaseStorage.getInstance().getReference();

        String[] animals = getResources().getStringArray(R.array.animals);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, animals);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinAnimal.setAdapter(adapter);
        spinAnimal.setOnItemSelectedListener(this);

        reference = FirebaseDatabase.getInstance().getReference().child(DATABASE_REPORT_PATH);
        pawPinDescriptor = bitmapDescriptor(this, R.drawable.paw_pin, IMAGE_CONSTRAINT);
        // init the map fragment
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.reportMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    loadMap(googleMap);
                }
            });
        }

        tvGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Closing Dialog
                finish();

            }
        });

        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Capturing Information
                String description = etAnimalDescription.getText().toString();
                if (!description.isEmpty() && takenImage != null) {
                    uploadToFirebase(imageUri, description);
                    matchingAlgorithm(description);
                }
                finish();
            }
        });
    }

    private void matchingAlgorithm(String description) {
        // Go through all of the posts that have been made
        DatabaseReference postReference = FirebaseDatabase.getInstance().getReference("posts");
        postReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot parentSnap: snapshot.getChildren()) {
                    for (DataSnapshot childSnap: parentSnap.getChildren()) {
                        // Use the levenshtein string checker to see how similar the descriptions, address, and, animal type
                        Post post = childSnap.getValue(Post.class);
                        String addressPost = "";
                        String addressReport = "";

                        Geocoder geocoder = new Geocoder(ReportCreationActivity.this, Locale.getDefault());
                        try {
                            List<Address> addressListReport = geocoder.getFromLocation(changedMarkerLoc.latitude, changedMarkerLoc.longitude,1);
                            List<Address> addressListPost = geocoder.getFromLocation(post.getLat(), post.getLng(),1);
                            if (addressListPost.size() > 0 && addressListReport.size() > 0) {
                                addressPost = addressListPost.get(0).getAddressLine(0);
                                addressReport = addressListReport.get(0).getAddressLine(0);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        int check1 = LevenshteinDistanceDP.compute_Levenshtein_distanceDP(animalFromSpinner, post.getAnimal());
                        int check2 = LevenshteinDistanceDP.compute_Levenshtein_distanceDP(description, post.getDescription());
                        int check3 = LevenshteinDistanceDP.compute_Levenshtein_distanceDP(addressReport, addressPost);

                        // Algorithm can allow minimal differences in address, lots of differences in descriptions, and no differences in animal type
                        if (check1 == 0 && check2 < 20 && check3 < 15) {
                            // If a post meets the threshold (send a notification) or (add it to recycler view that shows matching posts)
                            sendNotification(post.getUserId());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendNotification(String receiver) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnap: snapshot.getChildren()){
                    Token token = childSnap.getValue(Token.class);
                    Data data = new Data(user.getUid(), R.drawable.paw_logo, "Report matching your post found!", "New Report!", receiver);
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(ReportCreationActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {}
                            });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    // Helper method that is called when the onMapReady is called (created mainly for cleanliness)
    private void loadMap(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMarkerDragListener(this);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(changedMarkerLoc)
                .icon(pawPinDescriptor)
                .draggable(true );

        map.addMarker(markerOptions);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(changedMarkerLoc, 16));

    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        imageUri = FileProvider.getUriForFile(this, "com.codepath.fileprovider.DoogleMaps", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                btnTakePic.setText("Picture Uploaded");
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }


    private void uploadToFirebase(Uri uri, String description) {
        StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String reportId = reference.push().getKey();
                        Report report = new Report(uri.toString(), description, reportId, animalFromSpinner, user.getUid(), changedMarkerLoc.latitude, changedMarkerLoc.longitude);
                        reference.child(user.getUid()).child(reportId).setValue(report);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Photo failed to upload: ", e);
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }


    // These two focus on
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spinAnimal) {
            animalFromSpinner = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // Method that sets the size and constraints for the drawable/image that will become the markers new icon
    private BitmapDescriptor bitmapDescriptor(Context context, int imageResId, int imageConstraint) {
        Drawable drawable = ContextCompat.getDrawable(context, imageResId);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth() / imageConstraint, drawable.getIntrinsicHeight() / imageConstraint);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth() / imageConstraint, drawable.getIntrinsicHeight() / imageConstraint, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Log.i(TAG, "OnMarkerDragStart");

    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Log.i(TAG, "OnMarkerDrag");

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Log.i(TAG, "OnMarkerDragEnd");
        changedMarkerLoc = marker.getPosition();

    }
}