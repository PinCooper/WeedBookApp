package com.example.weedbookapp;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.DatePickerDialog;
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
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class UpdatePageFragment extends Fragment {

    private String nameWeed, nameWeedLat, dateAdded, imageID, latitude, longitude, area, address, culture, coating, chemical, date, efficiency, comment, userUID, id, currentPhotoPath;
    private TextView nameWeedInputUpdate, nameWeedLatInputUpdate, latitudeInputUpdate, longitudeInputUpdate, areaInputUpdate, addressInputUpdate, dateInputUpdate, commentInputUpdate;
    private Spinner cultureSpinnerUpdate, coatingSpinnerUpdate, chemicalSpinnerUpdate, efficiencySpinnerUpdate;
    int numCulture, numCoating, numChemical, numEfficiency;
    private ArrayList<String> culturesList, coatingsList, chemicalsList, efficiencyList;
    private ArrayAdapter<String> culturesAdapter, coatingsAdapter, chemicalsAdapter, efficiencyAdapter;
    private String[] culturesUpdate = {"-", "Горох", "Горошек", "Гречиха", "Зимний овёс", "Зимний рапс", "Картофель", "Капуста", "Кормовая свёкла", "Кукурузу",
            "Лук", "Лён", "Марковка", "Огурцы", "Озимая пшеница", "Озимая рожь", "Озимый ячмень", "Помидоры", "Подсолнухи", "Просо", "Рис",
            "Сахарная свекла", "Свёкла обыкновенная", "Сорго", "Соя", "Чеснок", "Яровая пшеница", "Яровая рожь", "Яровой овёс", "Яровой рапс",
            "Яровой ячмень"};
    private String[] coatingsUpdate = {"-", "10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%", "100%"};
    private String[] chemicalsUpdate = {"-", "Агрокиллер", "Глифос", "Граунд", "Лазурит", "Линтур", "Миура", "Отличник", "Раундап", "Торнадо",
            "Ураган форте", "Чистогряд"};
    private String[] efficiencyUpdate = {"-", "Эффективно", "Малоэффективно", "Не эффективно"};
    private ImageView imageWeedUpdate;
    private ImageButton updatePhotoButton, updateImageButton, gpsButtonUpdate;
    private Button updateWeedButton;
    private DatabaseReference reference;
    private StorageReference mStorageRef;
    private Uri uploadUri;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Calendar calendar;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UpdatePageFragment() {
        // Required empty public constructor
    }

    public UpdatePageFragment(String nameWeed, String nameWeedLat, String dateAdded, String imageID, String latitude, String longitude, String area, String address, String culture, int numCulture, String coating, int numCoating, String chemical, int numChemical,
                              String date, String efficiency, int numEfficiency, String comment, String userUID, String id) {

        this.nameWeed = nameWeed;
        this.nameWeedLat = nameWeedLat;
        this.dateAdded = dateAdded;
        this.imageID = imageID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.area = area;
        this.address = address;
        this.culture = culture;
        this.numCulture = numCulture;
        this.coating = coating;
        this.numCoating = numCoating;
        this.chemical = chemical;
        this.numChemical = numChemical;
        this.dateAdded = dateAdded;
        this.date = date;
        this.efficiency = efficiency;
        this.numEfficiency = numEfficiency;
        this.comment = comment;
        this.userUID = userUID;
        this.id = id;

    }

    public static UpdatePageFragment newInstance(String param1, String param2) {
        UpdatePageFragment fragment = new UpdatePageFragment();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_update_page, container, false);
        nameWeedInputUpdate = (TextView) root.findViewById(R.id.nameWeedInputUpdate);
        nameWeedLatInputUpdate = (TextView) root.findViewById(R.id.nameWeedLatInputUpdate);
        latitudeInputUpdate = (TextView) root.findViewById(R.id.latitudeInputUpdate);
        longitudeInputUpdate = (TextView) root.findViewById(R.id.longitudeInputUpdate);
        areaInputUpdate = (TextView) root.findViewById(R.id.areaInputUpdate);
        addressInputUpdate = (TextView) root.findViewById(R.id.addressInputUpdate);
        dateInputUpdate = (TextView) root.findViewById(R.id.dateInputUpdate);
        commentInputUpdate = (TextView) root.findViewById(R.id.commentInputUpdate);
        cultureSpinnerUpdate = (Spinner) root.findViewById(R.id.cultureSpinnerUpdate);
        coatingSpinnerUpdate = (Spinner) root.findViewById(R.id.coatingSpinnerUpdate);
        chemicalSpinnerUpdate = (Spinner) root.findViewById(R.id.chemicalSpinnerUpdate);
        efficiencySpinnerUpdate = (Spinner) root.findViewById(R.id.efficiencySpinnerUpdate);
        imageWeedUpdate = (ImageView) root.findViewById(R.id.imageWeedUpdate);
        updateImageButton = (ImageButton) root.findViewById(R.id.updateImageButton);
        updatePhotoButton = (ImageButton) root.findViewById(R.id.updatePhotoButton);
        updateWeedButton = (Button) root.findViewById(R.id.updateWeedButton);
        gpsButtonUpdate = (ImageButton) root.findViewById(R.id.gpsButtonUpdate);
        reference = FirebaseDatabase.getInstance().getReference("Weed");
        mStorageRef = FirebaseStorage.getInstance().getReference("ImageDB");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        calendar = Calendar.getInstance();

        nameWeedInputUpdate.setText(nameWeed);
        nameWeedLatInputUpdate.setText(nameWeedLat);
        latitudeInputUpdate.setText(latitude);
        longitudeInputUpdate.setText(longitude);
        areaInputUpdate.setText(area);
        addressInputUpdate.setText(address);
        dateInputUpdate.setText(date);
        commentInputUpdate.setText(comment);

        culturesList = new ArrayList<>(Arrays.asList(culturesUpdate));
        culturesAdapter = new ArrayAdapter<>(getActivity(), R.layout.style_spinner, culturesList);
        cultureSpinnerUpdate.setAdapter(culturesAdapter);
        cultureSpinnerUpdate.setSelection(numCulture);

        coatingsList = new ArrayList<>(Arrays.asList(coatingsUpdate));
        coatingsAdapter = new ArrayAdapter<>(getActivity(), R.layout.style_spinner, coatingsList);
        coatingSpinnerUpdate.setAdapter(coatingsAdapter);
        coatingSpinnerUpdate.setSelection(numCoating);

        chemicalsList = new ArrayList<>(Arrays.asList(chemicalsUpdate));
        chemicalsAdapter = new ArrayAdapter<>(getActivity(), R.layout.style_spinner, chemicalsList);
        chemicalSpinnerUpdate.setAdapter(chemicalsAdapter);
        chemicalSpinnerUpdate.setSelection(numChemical);

        efficiencyList = new ArrayList<>(Arrays.asList(efficiencyUpdate));
        efficiencyAdapter = new ArrayAdapter<>(getActivity(), R.layout.style_spinner, efficiencyList);
        efficiencySpinnerUpdate.setAdapter(efficiencyAdapter);
        efficiencySpinnerUpdate.setSelection(numEfficiency);

        Picasso.get().load(imageID).into(imageWeedUpdate);

        updatePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        updateImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage();
            }
        });

        updateWeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDatabase();
            }
        });

        gpsButtonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });

        dateIndicate();

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

        dateInputUpdate.setOnClickListener(new View.OnClickListener() {
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
        dateInputUpdate.setText(sdf.format(calendar.getTime()));
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

                            addressInputUpdate.setText(
                                    addresses.get(0).getAddressLine(0)
                            );

                            latitudeInputUpdate.setText(
                                    String.valueOf(addresses.get(0).getLatitude())
                            );

                            longitudeInputUpdate.setText(
                                    String.valueOf(addresses.get(0).getLongitude())
                            );

                            areaInputUpdate.setText(
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
                imageWeedUpdate.setImageURI(data.getData());
            }
        }else if(requestCode == 2){
            if(resultCode == RESULT_OK) {
                rotateImage(setReducedPhotoSize());
            }
        }
    }

    private void updateDatabase(){
        isNameWeedChanged();
        isNameWeedLatChanged();
        isLatitudeChanged();
        isLongitudeChanged();
        isAreaChanged();
        isAddressChanged();
        isCultureChanged();
        isCoatingChanged();
        isChemicalChanged();
        isDateChanged();
        isEfficiencyChanged();
        isCommentChanged();
        isImageChanged();
        Toast.makeText(getActivity(), "Данные обновлены", Toast.LENGTH_LONG).show();
    }

    private void isNameWeedChanged(){
        if (!nameWeed.equals(nameWeedInputUpdate.getText().toString())){
            reference.child(id).child("nameWeed").setValue(nameWeedInputUpdate.getText().toString());
        }
    }

    private void isNameWeedLatChanged(){
        if (!nameWeedLat.equals(nameWeedInputUpdate.getText().toString())){
            reference.child(id).child("nameWeedLat").setValue(nameWeedLatInputUpdate.getText().toString());
        }
    }

    private void isLatitudeChanged(){
        if (!latitude.equals(latitudeInputUpdate.getText().toString())){
            reference.child(id).child("latitude").setValue(latitudeInputUpdate.getText().toString());
        }
    }

    private void isLongitudeChanged(){
        if (!longitude.equals(longitudeInputUpdate.getText().toString())){
            reference.child(id).child("longitude").setValue(longitudeInputUpdate.getText().toString());
        }
    }

    private void isAreaChanged(){
        if (!area.equals(areaInputUpdate.getText().toString())){
            reference.child(id).child("area").setValue(areaInputUpdate.getText().toString());
        }
    }

    private void isAddressChanged(){
        if (!address.equals(addressInputUpdate.getText().toString())){
            reference.child(id).child("address").setValue(addressInputUpdate.getText().toString());
        }
    }

    private void isCultureChanged(){
        if (!culture.equals(cultureSpinnerUpdate.getSelectedItem().toString())){
            reference.child(id).child("culture").setValue(cultureSpinnerUpdate.getSelectedItem().toString());
            reference.child(id).child("numCulture").setValue(cultureSpinnerUpdate.getSelectedItemPosition());
        }
    }

    private void isCoatingChanged(){
        if (!coating.equals(coatingSpinnerUpdate.getSelectedItem().toString())){
            reference.child(id).child("coating").setValue(coatingSpinnerUpdate.getSelectedItem().toString());
            reference.child(id).child("numCoating").setValue(coatingSpinnerUpdate.getSelectedItemPosition());
        }
    }

    private void isChemicalChanged(){
        if (!chemical.equals(chemicalSpinnerUpdate.getSelectedItem().toString())){
            reference.child(id).child("chemical").setValue(chemicalSpinnerUpdate.getSelectedItem().toString());
            reference.child(id).child("numChemical").setValue(chemicalSpinnerUpdate.getSelectedItemPosition());
        }
    }

    private void isDateChanged(){
        if (!date.equals(dateInputUpdate.getText().toString())){
            reference.child(id).child("date").setValue(dateInputUpdate.getText().toString());
        }
    }

    private void isEfficiencyChanged(){
        if (!efficiency.equals(efficiencySpinnerUpdate.getSelectedItem().toString())){
            reference.child(id).child("efficiency").setValue(efficiencySpinnerUpdate.getSelectedItem().toString());
            reference.child(id).child("numEfficiency").setValue(efficiencySpinnerUpdate.getSelectedItemPosition());
        }
    }

    private void isCommentChanged(){
        if (!comment.equals(commentInputUpdate.getText().toString())){
            reference.child(id).child("comment").setValue(commentInputUpdate.getText().toString());
        }
    }

    private void isImageChanged(){
        if (!imageWeedUpdate.equals(imageID)){
            FirebaseStorage.getInstance().getReferenceFromUrl(imageID).delete();
            Bitmap bitMap = ((BitmapDrawable) imageWeedUpdate.getDrawable()).getBitmap();
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
                    reference.child(id).child("imageID").setValue(uploadUri.toString());
                }
            });
        }
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
        int targetImageViewWidth = imageWeedUpdate.getWidth();
        int targetImageViewHeight = imageWeedUpdate.getHeight();

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
            imageWeedUpdate.setImageBitmap(getResizedBitmap(rotatedBitmap, 960, 1280));
        }
        else{
            imageWeedUpdate.setImageBitmap(getResizedBitmap(rotatedBitmap, 1280, 960));
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
}