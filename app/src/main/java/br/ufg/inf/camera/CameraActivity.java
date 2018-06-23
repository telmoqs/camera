package br.ufg.inf.camera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.ufg.inf.camera.model.Vestuario;

public class CameraActivity extends AppCompatActivity {

    private DatePicker picker;
    private Vestuario vestuario = new Vestuario();

    private static final int REQUEST_CAMERA = 1;

    private CameraView mCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mCameraView = findViewById(R.id.camera);

        checkCamera();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.start();
    }

    @Override
    protected void onPause() {
        mCameraView.stop();
        super.onPause();
    }

    private void checkCamera() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED){

        }else{
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CAMERA){
            if(grantResults.length > 0){
                if(grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    //TODO ABRO A CAMERA
                }else{
                    showError(R.string.error_permission_not_granted);
                }
            } else{
                showError(R.string.permission_request_canceled);
            }
        }
    }

    public void showError(int idStringDescription){
        new MaterialDialog.Builder(this)
                .title(R.string.title_error)
                .content(idStringDescription)
                .positiveText(R.string.label_ok)
                .show();
    }

    public void btnSalvarFotoOnClick(View view) {
        Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );

        adicionarDetalhes(view);

        CameraKitImage imagemCapturada = (CameraKitImage) intent.clone();
        saveImage(imagemCapturada);

    }

    private String saveImage(CameraKitImage cameraKitImage) {
        File file = null;
        OutputStream fOut = null;
        try {
            file = createImageFile();
            fOut =  new FileOutputStream(file);
            fOut.write(cameraKitImage.getJpeg());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fOut.close();
            } catch (IOException e) {
                Log.d("CAMERA_ERROR", e.getMessage());
            }
        }

        return file.getAbsolutePath();
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".mysecret",         /* suffix */
                storageDir      /* directory */
        );

        return imageFile;
    }

    private void shareImage(String path) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");


        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
        startActivity(share);
    }

    public void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (
                    InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void adicionarDetalhes(View view) {

        hideKeyboard();

        EditText fieldBoneChapeu = findViewById(R.id.field_boneChapeu);
        vestuario.setBoneChapeu(fieldBoneChapeu.getText().toString());

        EditText fieldCamisaBlusa = findViewById(R.id.field_camisaBlusa);
        vestuario.setCamisaBlusa(fieldCamisaBlusa.getText().toString());

        EditText fieldShortCalca = findViewById(R.id.field_shortCalca);
        vestuario.setShortCalca(fieldShortCalca.getText().toString());

        EditText fieldCalcado = findViewById(R.id.field_calcado);
        vestuario.setCalcado(fieldCalcado.getText().toString());

        picker= findViewById(R.id.datePicker1);
        vestuario.setData(picker.getDayOfMonth() + "/" + (picker.getMonth() + 1) + "/" + picker.getYear());
    }
}
