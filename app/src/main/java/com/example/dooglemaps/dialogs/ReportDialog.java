package com.example.dooglemaps.dialogs;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.example.dooglemaps.R;
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

public class ReportDialog extends DialogFragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "ReportDialog";
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
    private String animalFromSpinner = "";


    private Uri imageUri;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private FirebaseUser user;


    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private BitmapDescriptor pawPinDescriptor;
    private LatLng curMarkerLoc;


    public ReportDialog(LatLng latLng) {
        curMarkerLoc = latLng;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_report, container, false);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinAnimal = view.findViewById(R.id.spinAnimal);
        tvGoBack = view.findViewById(R.id.tvGoBack);
        tvSubmit = view.findViewById(R.id.tvSubmit);
        btnTakePic = view.findViewById(R.id.btnTakePic);
        etAnimalDescription = view.findViewById(R.id.etAnimalDescription);

        user = FirebaseAuth.getInstance().getCurrentUser();
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference(DATABASE_REPORT_PATH);
        storageReference = FirebaseStorage.getInstance().getReference();

        String[] animals = getResources().getStringArray(R.array.animals);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, animals);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinAnimal.setAdapter(adapter);
        spinAnimal.setOnItemSelectedListener(this);

        tvGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Closing Dialog
                getDialog().dismiss();

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
                if (!description.isEmpty() && takenImage != null && !animalFromSpinner.isEmpty()) {
                    uploadToFirebase(imageUri, description);
                }
                getDialog().dismiss();
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        reference = FirebaseDatabase.getInstance().getReference().child(DATABASE_REPORT_PATH);
        pawPinDescriptor = bitmapDescriptor(getContext(), R.drawable.paw_pin, IMAGE_CONSTRAINT);
        // init the map fragment
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    loadMap(googleMap);
                }
            });
        }

    }

    // Helper method that is called when the onMapReady is called (created mainly for cleanliness)
    private void loadMap(GoogleMap googleMap) {
        map = googleMap;
        // TODO: Go to Location
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curMarkerLoc, 16));

    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        imageUri = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider.DoogleMaps", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
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
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

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
                        Report report = new Report(uri.toString(), description, reportId, animalFromSpinner, user.getUid(), curMarkerLoc.latitude, curMarkerLoc.longitude);
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
        ContentResolver cr = getContext().getContentResolver();
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
}
