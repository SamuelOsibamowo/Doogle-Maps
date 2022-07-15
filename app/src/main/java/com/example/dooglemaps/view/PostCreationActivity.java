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
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
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
import com.example.dooglemaps.viewModel.Post;
import com.example.dooglemaps.viewModel.Report;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class PostCreationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, GoogleMap.OnMarkerDragListener{

    private static final String TAG = "PostCreationAct";
    public static final String DATABASE_POST_PATH = "posts";
    private static final int IMAGE_CONSTRAINT = 10;
    public static final int PICK_PHOTO_CODE = 1046;

    private EditText etPetDescription;
    private Button btnChoosePic;
    private TextView tvPostBack, tvPostSubmit;
    private Uri imageUri;
    private Bitmap chosenImage;
    private Spinner spinAnimal;
    private String animalFromSpinner = "Dog";



    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private FirebaseUser user;
    private LatLng curMarkerLoc;
    private LatLng changedMarkerLoc;


    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private BitmapDescriptor pawPinDescriptor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_creation);


        curMarkerLoc = (LatLng) getIntent().getExtras().get("latlng");
        changedMarkerLoc = curMarkerLoc;


        spinAnimal = findViewById(R.id.spinAnimal);
        tvPostBack = findViewById(R.id.tvPostGoBack);
        tvPostSubmit = findViewById(R.id.tvPostSubmit);
        btnChoosePic = findViewById(R.id.btnChoosePic);
        etPetDescription = findViewById(R.id.etPetDescription);

        user = FirebaseAuth.getInstance().getCurrentUser();
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference(DATABASE_POST_PATH);
        storageReference = FirebaseStorage.getInstance().getReference();

        String[] animals = getResources().getStringArray(R.array.animals);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, animals);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinAnimal.setAdapter(adapter);
        spinAnimal.setOnItemSelectedListener(this);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        pawPinDescriptor = bitmapDescriptor(this, R.drawable.blue_paw_pin, IMAGE_CONSTRAINT);
        reference = FirebaseDatabase.getInstance().getReference().child(DATABASE_POST_PATH);


        // init the map fragment
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.postMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    loadMap(googleMap);
                }
            });
        }

        // Closes the dialog fragment
        tvPostBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvPostSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Capturing Information
                String description = etPetDescription.getText().toString();
                if (!description.isEmpty() && chosenImage != null) {
                    uploadToFirebase(imageUri, description);
                }
                finish();
            }
        });

        // Calls an intent that goes to the phones photo gallery screen
        btnChoosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(v);
            }
        });

    }


    // Helper method that is called when the onMapReady is called (created mainly for cleanliness)
    private void loadMap(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMarkerDragListener(this);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(curMarkerLoc)
                .icon(pawPinDescriptor)
                .draggable(true );

        map.addMarker(markerOptions);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curMarkerLoc, 16));

    }

    private void uploadToFirebase(Uri imageUri, String description) {
        StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String postId = reference.push().getKey();
                        Post post = new Post(uri.toString(), description, postId, animalFromSpinner, user.getUid(), changedMarkerLoc.latitude, changedMarkerLoc.longitude);
                        reference.child(user.getUid()).child(postId).setValue(post);
                    }
                });
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            imageUri = data.getData();
            // Load the image located at photoUri into selectedImage
            chosenImage = loadFromUri(imageUri);
            // Load the selected image into a preview
            btnChoosePic.setText("Picture uploaded!");
        }
    }

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

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        changedMarkerLoc = marker.getPosition();

    }
}