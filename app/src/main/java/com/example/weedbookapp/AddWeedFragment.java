package com.example.weedbookapp;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.ACTION_GET_CONTENT;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddWeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddWeedFragment extends Fragment {

    private ImageButton addPhotoButton, takePictureButton, gpsButton;
    private ImageView imageWeed;
    private StorageReference mStorageRef;
    private DatabaseReference mDataBase;
    private FirebaseAuth auth;
    private Uri uploadUri;
    private ProgressBar progressBar;
    private Button addWeedButton;
    private EditText nameWeedInput, nameWeedLatInput, latitudeInput, longitudeInput, areaInput, addressInput, dateInput, commentInput;
    private Spinner cultureSpinner, coatingSpinner, chemicalSpinner, efficiencySpinner;
    private String[] cultures = {"-", "Горох", "Горошек", "Гречиха", "Зимний овёс", "Зимний рапс", "Картофель", "Капуста", "Кормовая свёкла", "Кукурузу",
            "Лук", "Лён", "Марковка", "Огурцы", "Озимая пшеница", "Озимая рожь", "Озимый ячмень", "Помидоры", "Подсолнухи", "Просо", "Рис",
            "Сахарная свекла", "Свёкла обыкновенная", "Сорго", "Соя", "Чеснок", "Яровая пшеница", "Яровая рожь", "Яровой овёс", "Яровой рапс",
            "Яровой ячмень"};
    private String[] coatings = {"-", "10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%", "100%"};
    private String[] chemicals = {"-", "Агрокиллер", "Глифос", "Граунд", "Лазурит", "Линтур", "Миура", "Отличник", "Раундап", "Торнадо",
            "Ураган форте", "Чистогряд"};
    private String[] efficiency = {"-", "Эффективно", "Малоэффективно", "Не эффективно"};
    private ArrayList<String> culturesList, coatingsList, chemicalsList, efficiencyList;
    private ArrayAdapter<String> culturesAdapter, coatingsAdapter, chemicalsAdapter, efficiencyAdapter;
    private String WEED_KEY = "Weed";
    private String currentPhotoPath;
    private Calendar calendar;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseUser user;
    int orientation;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddWeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddWeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddWeedFragment newInstance(String param1, String param2) {
        AddWeedFragment fragment = new AddWeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
/*            nameWeedInput.setText(savedInstanceState.getString("nameWeed"));
            nameWeedLatInput.setText(savedInstanceState.getString("nameWeedLat"));*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_weed, container, false);
        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        addPhotoButton = (ImageButton) root.findViewById(R.id.addPhotoButton);
        takePictureButton = (ImageButton) root.findViewById(R.id.takePictureButton);
        imageWeed = (ImageView) root.findViewById(R.id.weedImage);
        gpsButton = (ImageButton) root.findViewById(R.id.gpsButton);
        addWeedButton = (Button) root.findViewById(R.id.addWeedButton);
        addressInput = (EditText) root.findViewById(R.id.addressInput);
        nameWeedInput = (EditText) root.findViewById(R.id.nameWeedInput);
        nameWeedLatInput = (EditText) root.findViewById(R.id.nameWeedLatInput);
        latitudeInput = (EditText) root.findViewById(R.id.latitudeInput);
        longitudeInput = (EditText) root.findViewById(R.id.longitudeInput);
        areaInput = (EditText) root.findViewById(R.id.areaInput);
        dateInput = (EditText) root.findViewById(R.id.dateInput);
        commentInput = (EditText) root.findViewById(R.id.commentInput);
        cultureSpinner = (Spinner) root.findViewById(R.id.cultureSpinner);
        coatingSpinner = (Spinner) root.findViewById(R.id.coatingSpinner);
        chemicalSpinner = (Spinner) root.findViewById(R.id.chemicalSpinner);
        efficiencySpinner = (Spinner) root.findViewById(R.id.efficiencySpinner);
        mStorageRef = FirebaseStorage.getInstance().getReference("ImageDB");
        mDataBase = FirebaseDatabase.getInstance().getReference(WEED_KEY);
        user = FirebaseAuth.getInstance().getCurrentUser();
        auth = FirebaseAuth.getInstance();
        calendar = Calendar.getInstance();
        orientation = root.getResources().getConfiguration().orientation;

        culturesList = new ArrayList<>(Arrays.asList(cultures));
        culturesAdapter = new ArrayAdapter<>(getActivity(), R.layout.style_spinner, culturesList);
        cultureSpinner.setAdapter(culturesAdapter);

        coatingsList = new ArrayList<>(Arrays.asList(coatings));
        coatingsAdapter = new ArrayAdapter<>(getActivity(), R.layout.style_spinner, coatingsList);
        coatingSpinner.setAdapter(coatingsAdapter);

        chemicalsList = new ArrayList<>(Arrays.asList(chemicals));
        chemicalsAdapter = new ArrayAdapter<>(getActivity(), R.layout.style_spinner, chemicalsList);
        chemicalSpinner.setAdapter(chemicalsAdapter);

        efficiencyList = new ArrayList<>(Arrays.asList(efficiency));
        efficiencyAdapter = new ArrayAdapter<>(getActivity(), R.layout.style_spinner, efficiencyList);
        efficiencySpinner.setAdapter(efficiencyAdapter);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        goneImage();

        dateIndicate();

        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });


        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
                visibleImage();
            }
        });

        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage();
                visibleImage();
            }
        });

        addWeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageWeed.getDrawable() != null){
                    uploadImage();
                }else{
                    Toast.makeText(getActivity(), "Вы не добавили фотографию", Toast.LENGTH_LONG).show();
                }
            }
        });

        return root;
    }

    private void dateIndicate(){
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateCalendar();
            }
        };

        dateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateCalendar(){
        String Format = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(Format, Locale.US);
        dateInput.setText(sdf.format(calendar.getTime()));
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(
                                    location.getLatitude(), location.getLongitude(), 1
                            );

                            addressInput.setText(
                                    addresses.get(0).getAddressLine(0)
                            );

                            latitudeInput.setText(
                                    String.valueOf(addresses.get(0).getLatitude())
                            );

                            longitudeInput.setText(
                                    String.valueOf(addresses.get(0).getLongitude())
                            );

                            areaInput.setText(
                                    String.valueOf(addresses.get(0).getAdminArea())
                            );

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && data != null && data.getData() != null){
            if(resultCode == RESULT_OK) {
                Log.d("MyLog", "Image URI : " + data.getData());
                imageWeed.setImageURI(data.getData());
            }
        }else if(requestCode == 2){
            if(resultCode == RESULT_OK) {
                rotateImage(setReducedPhotoSize());
            }
        }
    }

    private void saveWeed(){
        String id = mDataBase.push().getKey();
        String nameWeed = nameWeedInput.getText().toString();
        String nameWeedLat = nameWeedLatInput.getText().toString();
        String latitude = latitudeInput.getText().toString();
        String longitude = longitudeInput.getText().toString();
        String area = areaInput.getText().toString();
        String address = addressInput.getText().toString();
        String date = dateInput.getText().toString();
        String comment = commentInput.getText().toString();
        String culture = cultureSpinner.getSelectedItem().toString();
        int numCulture = cultureSpinner.getSelectedItemPosition();
        String coating = coatingSpinner.getSelectedItem().toString();
        int numCoating = coatingSpinner.getSelectedItemPosition();
        String chemical = chemicalSpinner.getSelectedItem().toString();
        int numChemical = chemicalSpinner.getSelectedItemPosition();
        String efficiency = efficiencySpinner.getSelectedItem().toString();
        int numEfficiency = efficiencySpinner.getSelectedItemPosition();
        String userUID = auth.getUid().toString();
        String userEmail = user.getEmail().toString();
        Long dateAdded = new Date().getTime();
        Weeds newWeed = new Weeds(id, nameWeed, nameWeedLat, latitude, longitude, address, area, date, comment, culture, numCulture, coating, numCoating, chemical, numChemical, efficiency, numEfficiency, uploadUri.toString(), userUID, dateAdded, userEmail);
        if (!TextUtils.isEmpty(nameWeed) && !TextUtils.isEmpty(nameWeedLat) && !TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude) && !TextUtils.isEmpty(address)){
            if(id != null)mDataBase.child(id).setValue(newWeed);
            Toast.makeText(getActivity(), "Сорняк добавлен", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getActivity(), "Необходимо заполнить обязательные поля!", Toast.LENGTH_SHORT).show();
        }
    }

    public Bitmap getResizedBitmap (Bitmap photo, int widthPhoto, int heightPhoto){
        int width = photo.getWidth();
        int height = photo.getHeight();
        float scaleWidth = ((float) widthPhoto) / width;
        float scaleHeight = ((float) heightPhoto) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(photo, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    private void uploadImage(){
        Bitmap bitMap = ((BitmapDrawable) imageWeed.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        final StorageReference mRef = mStorageRef.child(System.currentTimeMillis() + "my_image");
        UploadTask up = mRef.putBytes(byteArray);
        Task<Uri> task = up.continueWithTask((task1) ->{
            return mRef.getDownloadUrl();
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                uploadUri = task.getResult();
                saveWeed();
            }
        });
    }

    private void getImage(){
        Intent intentChooser = new Intent();
        intentChooser.setType("image/*");
        intentChooser.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentChooser, 1);
    }

    private void takePhoto(){
        String fileName = "WeedPhoto";
        File storageDirectory = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File imageFile = File.createTempFile(fileName, ".jpg", storageDirectory);

            currentPhotoPath = imageFile.getAbsolutePath();

            Uri imageUri = FileProvider.getUriForFile(getActivity(), "com.example.weedbookapp.fileprovider", imageFile);

            Intent  takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(takePhotoIntent, 2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap setReducedPhotoSize(){
        int targetImageViewWidth = imageWeed.getWidth();
        int targetImageViewHeight = imageWeed.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        int cameraImageWidth = bmOptions.outWidth;
        int cameraImageHeight = bmOptions.outHeight;

        int scaleFactor = Math.min(cameraImageWidth/targetImageViewWidth, cameraImageHeight/cameraImageHeight);
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
    }

    private void rotateImage(Bitmap bitmap){
        ExifInterface exifInterface = null;
        try{
            exifInterface = new ExifInterface(currentPhotoPath);
        }catch (IOException e){
            e.printStackTrace();
        }
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();
        switch (orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            default:
        }
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (rotatedBitmap.getHeight() > rotatedBitmap.getWidth()) {
            imageWeed.setImageBitmap(getResizedBitmap(rotatedBitmap, 960, 1280));
        }
        else{
            imageWeed.setImageBitmap(getResizedBitmap(rotatedBitmap, 1280, 960));
        }
    }

    private void visibleImage(){
        imageWeed.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void goneImage(){
        imageWeed.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

}