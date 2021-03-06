package nova.workoutapp22;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import nova.workoutapp22.subSources.BasicInfo;

import static nova.workoutapp22.R.id.imageViewForAdd;
import static nova.workoutapp22.subSources.BasicInfo.CROP_FROM_IMAGE;
import static nova.workoutapp22.subSources.BasicInfo.PICK_FROM_ALBUM;
import static nova.workoutapp22.subSources.BasicInfo.PICK_FROM_CAMERA;
import static nova.workoutapp22.subSources.timeController.getTimeCutSec;

public class AddMemoActivity extends AppCompatActivity {

    EditText editTextMemo;
    TextView strDate;
    ImageView imgViewForAdd;
    int mIDForTransport;

    String strMemoMode;


    Uri imageUri;
    boolean editFlag = false;

    private Uri mImageCaptureUri;
    private Uri cropImageUri;

    private String absolutePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        editFlag = false;
        Toast.makeText(getApplicationContext(),"editFlag = "+editFlag, Toast.LENGTH_SHORT);

        clearMyPrefs();
        setContentView(R.layout.activity_add_memo);


        imgViewForAdd = (ImageView) findViewById(imageViewForAdd);
        editTextMemo = (EditText) findViewById(R.id.editTextMemo);
        strDate = (TextView) findViewById(R.id.textViewDate);
        strDate.setText(getTimeCutSec());

        Intent intent = getIntent();

        strMemoMode = intent.getStringExtra(BasicInfo.KEY_ADDMEMO_MODE);


        //메모를 걍클릭 함(모드뷰), or 롱클릭 -> 수정누름 (MODE_MODIFY)
        // 기존 내용을 먼저 그려주고, 사용자의 입력을 저장해준다
        if (strMemoMode.equals(BasicInfo.MODE_MODIFY) || strMemoMode.equals(BasicInfo.MODE_VIEW)) {

            processIntent(intent);

        } else { // 새로 메모를 하려는 경우. 화면을 새로 그려준다.

            Button saveMemoButton = (Button) findViewById(R.id.buttonSaveMemo);
            saveMemoButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {


                    saveAndSetResult();
                 //   Toast.makeText(getApplicationContext(), "입력 내용이 저장됩니다.",Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }


    }

    public void onAddMemoImageButtonClicked(View v){
        DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doTakePhotoAction();
            }
        };
        DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doTakeAlbumAction();
            }
        };

        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };

        new AlertDialog.Builder(this)
                .setTitle("업로드할 이미지 선택")
                .setPositiveButton("사진촬영", cameraListener)
                .setNeutralButton("앨범선택", albumListener)
                .setNegativeButton("취소", cancelListener)
                .show();

        editFlag = true;
//        Toast.makeText(getApplicationContext(), "edit Flag = "+editFlag, Toast.LENGTH_SHORT).show();


    }








    public void doTakePhotoAction() // 카메라 촬영 후 이미지 가져오기
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 임시로 사용할 파일의 경로를 생성
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    public void doTakeAlbumAction() // 앨범에서 이미지 가져오기
    {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case PICK_FROM_ALBUM: {
                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.
                // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.
                mImageCaptureUri = data.getData();
                Log.d("SmartWheel", mImageCaptureUri.getPath().toString());
            }

            case PICK_FROM_CAMERA: {


                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                // CROP할 이미지를 200*200 크기로 저장
                intent.putExtra("outputX", 200); // CROP한 이미지의 x축 크기
                intent.putExtra("outputY", 200); // CROP한 이미지의 y축 크기
                intent.putExtra("aspectX", 1); // CROP 박스의 X축 비율
                intent.putExtra("aspectY", 1); // CROP 박스의 Y축 비율
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_IMAGE); // CROP_FROM_CAMERA case문 이동
                break;
            }

            case CROP_FROM_IMAGE: {
                // 크롭이 된 이후의 이미지를 넘겨 받습니다.
                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
                // 임시 파일을 삭제합니다.
                if (resultCode != RESULT_OK) {
                    return;
                }

                final Bundle extras = data.getExtras();

                // CROP된 이미지를 저장하기 위한 FILE 경로
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/TempCrop/" + System.currentTimeMillis() + ".jpg";

                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data"); // CROP된 BITMAP
                    imgViewForAdd.setImageBitmap(photo); // 레이아웃의 이미지칸에 CROP된 BITMAP을 보여줌

                    storeCropImage(photo, filePath); // CROP된 이미지를 외부저장소, 앨범에 저장한다.
                    absolutePath = filePath;
                    break;
                }


            }
        }
    }

    private void storeCropImage(Bitmap bitmap, String filePath) {
        // tempCrop 폴더를 생성하여 이미지를 저장하는 방식이다.
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/tempCrop";
        File temp_crop = new File(dirPath);

        if(!temp_crop.exists()) // tempCrop 디렉터리에 폴더가 없다면 (새로 이미지를 저장할 경우에 속한다.)
            temp_crop.mkdir();

        File copyFile = new File(filePath);
        BufferedOutputStream out = null;

        try {

            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));


            //////////////////////uri from file을 이용, 자른 이미지의 uri를 얻어옴
            cropImageUri = Uri.fromFile(copyFile);
            saveState();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);


            // sendBroadcast를 통해 Crop된 사진을 앨범에 보이도록 갱신한다.
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(copyFile)));

            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }







    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if( event.getAction() == KeyEvent.ACTION_DOWN ){ //키 다운 액션 감지
            if( keyCode == KeyEvent.KEYCODE_BACK ){ //BackKey 다운일 경우만 처리

                if ( !(editTextMemo.getText().toString().equals("")) )
                    editFlag = true;



                if( editFlag ==false){ //메모부분이 비어있으면 저장하지 마라

                   // Toast.makeText(this, "editTextMemo.getText() = "+editTextMemo.getText(),Toast.LENGTH_SHORT).show();

                    Toast.makeText(this, "입력한 내용이 없어 저장되지 않습니다.",Toast.LENGTH_SHORT).show();
                    finish();
                }

                else{
                  //  Toast.makeText(this, "editTextMemo.getText() = "+editTextMemo.getText(),Toast.LENGTH_SHORT).show();
                    saveAndSetResult();


                    finish();

                }



                return true;
            }
        }
        return super.onKeyDown( keyCode, event );
    }



    public void saveAndSetResult() {
        clearMyPrefs();

      //  Toast.makeText(getApplicationContext(), "입력 내용이 저장됩니다.",Toast.LENGTH_SHORT).show();

        Intent intentForSave = new Intent();

        saveImageAndUri();

        intentForSave.putExtra("memo", editTextMemo.getText().toString());
        intentForSave.putExtra("mID", mIDForTransport);
        intentForSave.putExtra("imageUri", cropImageUri.toString());


        setResult(RESULT_OK, intentForSave);
    }



    public void saveImageAndUri(){

        FileOutputStream fOutStream = null;

        String resDrawableUri = "android.resource://"+getApplicationContext().getPackageName()+"/drawable/basicimage";


        Bitmap bitmap = null;
        try {

            // 처음 왔을 때도 널! 수정 하러 와서 안건드려도 널! 어떻게 구분하지?

            //처음 왔을 때 -> getintent해서 건진 uri가 널이다!
            if( (cropImageUri == null) && ( (Uri)getIntent().getExtras().get("imageUri")==null )  ){

                cropImageUri =  Uri.parse(resDrawableUri) ;
            }
            // 수정하러 와서 안건드렸을 때 -> 건져지는 uri가 있겠지!
            else if( (cropImageUri == null) && ( (Uri)getIntent().getExtras().get("imageUri")!=null) ){
                cropImageUri = (Uri)getIntent().getExtras().get("imageUri");
            }



            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), cropImageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }


        try{

            //기본 그림일 경우

            if(cropImageUri.equals(Uri.parse(resDrawableUri) ) )
            {
                fOutStream=new FileOutputStream(resDrawableUri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOutStream);


            }
            //기본 그림이 아니라 새로운 그림을 얹었을 경우
            else {
                fOutStream=new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+"/tempImage.jpg");
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOutStream);
            }

        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }


        // 임시 파일 삭제

        if(mImageCaptureUri!=null){
            File f = new File(mImageCaptureUri.getPath());
            if(f.exists())
            {
                f.delete();
            }
        }

    }


    ////////////////////// 아이템을 수정하여 담아주는 인텐트 ::
    /// //수정할 때 기존의 텍스트와 이미지를 담아준다.
    /// /ID를 같이 보내주어야 교체가 가능해진다.
    public void processIntent(Intent intent) {

        editTextMemo.setText(intent.getStringExtra(BasicInfo.KEY_MEMO_TEXT));
        imgViewForAdd.setImageURI( (Uri)intent.getExtras().get("imageUri") );

        strDate.setText(getTimeCutSec());

        mIDForTransport = intent.getIntExtra("mID", 1);


        Button button = (Button) findViewById(R.id.buttonSaveMemo);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                saveAndSetResult();

                finish();
            }
        });


        // 사진에 대한 코드를 추가할것!!! imgViewForAdd.setImageintent.getStringExtra("resId");


    }


    /////////////////////////////////////////생명주기 관련 파트////////
    ////////// onPause에서 입력하던 상태가 저장되며, onResume에서 입력하던 상태가 복원된다.


    @Override
    protected void onStart() {
        super.onStart();

        editTextMemo.setText(getIntent().getStringExtra("memo"));

    }

    @Override
    protected void onPause() {
        super.onPause();


        saveState();

        //Toast.makeText(this, "onPause called", Toast.LENGTH_SHORT).show();
        // saveStateWithGson();
    }

    @Override
    protected void onStop() {
        super.onStop();


        //Toast.makeText(this, "onStop called", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        restoreState();
        // Toast.makeText(this, "onPause called", Toast.LENGTH_SHORT).show();

        //clearMyPrefs();

    }

    protected void restoreState() {

        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);

        if ((pref != null) && (pref.contains("memo"))   ) {
            String name = pref.getString("memo", "");
            editTextMemo.setText(name);
        }

        if ((pref != null) && (pref.contains("imageUri"))) {
            String uriString = pref.getString("imageUri", "");

            imageUri = Uri.parse(uriString);

            imgViewForAdd.setImageURI(imageUri);
        }

    }

    protected void saveState() {


        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("memo", editTextMemo.getText().toString());

        if(cropImageUri!=null){
            editor.putString("imageUri", cropImageUri.toString());

        }
        editor.commit();

    }

    protected void clearMyPrefs() {
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
    ////////////////////////////////////////////////////////////////////////////////////

}
