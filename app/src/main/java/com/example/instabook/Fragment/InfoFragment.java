package com.example.instabook.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instabook.Activity.ForHome.HomeData;
import com.example.instabook.Activity.ForMyBook.MyBookActivity;
import com.example.instabook.Activity.Pre.ResponseGet;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.Adapter.InfoReviewAdapter;
import com.example.instabook.ListView.HomeReviewItem;
import com.example.instabook.R;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;



public class InfoFragment extends Fragment {

    RetroBaseApiService retroBaseApiService;
    private ImageView info_pimg, info_editname;
    private TextView info_nickname, info_id, info_count;
    private FrameLayout info_fr_pimg,info_fr_editname;
    private Uri mImageCaptureUri;
    private String absoultePath;
    Button mybook;
    View rootView;

    private static final int PICK_FROM_ALBUM = 0;
    private static final int CROP_FROM_iMAGE = 1;

    List<HomeData> infoDataList;
    ArrayList<HomeReviewItem> items;
    HomeReviewItem item;

    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //유저 아이디, UID, 닉네임 가져오기
        final String userid = SaveSharedPreference.getUserName(getActivity());
        final int useruid = SaveSharedPreference.getUserUid(getActivity());
        final String usernickname = SaveSharedPreference.getUserNickname(getActivity());


        info_pimg = getView().findViewById(R.id.info_pimg);
        info_fr_pimg = getView().findViewById(R.id.info_fr_pimg);
        info_nickname = getView().findViewById(R.id.info_nickname);
        info_id = getView().findViewById(R.id.info_id);
        info_editname = getView().findViewById(R.id.info_editname);
        info_fr_editname = getView().findViewById(R.id.info_fr_editname);
        info_count = getView().findViewById(R.id.info_count);
        mybook = getView().findViewById(R.id.mybook);


        //리뷰수 표시하기
        Retrofit retro_cnt = new Retrofit.Builder()
                .baseUrl(retroBaseApiService.Base_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retroBaseApiService = retro_cnt.create(RetroBaseApiService.class);

        retroBaseApiService.getReviewcnt(useruid).enqueue(new Callback<List<ResponseGet>>() {
            @Override
            public void onResponse(Call<List<ResponseGet>> call, Response<List<ResponseGet>> response) {
                List<ResponseGet> get_cnt = response.body();
                int rv_cnt = get_cnt.get(0).getReviewCnt();
                info_count.setText("리뷰수: "+rv_cnt+"회");
            }

            @Override
            public void onFailure(Call<List<ResponseGet>> call, Throwable t) {

            }
        });



        //상단 프로필 이미지 불러오기
        String string_profile = SaveSharedPreference.getUserImage(getActivity());
        if(string_profile == null){
            //이미지뷰에 표시
            info_pimg.setImageResource(R.drawable.default_img);
            //이미지 동그랗게 보이기
            info_pimg.setBackground(new ShapeDrawable(new OvalShape()));
            info_pimg.setClipToOutline(true);
        }else {
            Bitmap bitmap_profile = StringToBitMap(string_profile);

            //이미지뷰에 표시
            info_pimg.setImageBitmap(bitmap_profile);
            //이미지 동그랗게 보이기
            info_pimg.setBackground(new ShapeDrawable(new OvalShape()));
            info_pimg.setClipToOutline(true);
        }


        //닉네임, 아이디 표시해주기
        info_id.setText("ID: " + userid);
        info_nickname.setText(usernickname);


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //찜리스트 화면으로 이동
                    case R.id.mybook:
                        Intent Success = new Intent(getActivity(), MyBookActivity.class);
                        startActivity(Success);
                        break;
                    case R.id.info_fr_editname:
                        EditText et = new EditText(getContext());  //닉네임 변경 시 사용
                        DialogInterface.OnClickListener edit = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String edit_name = et.getText().toString().trim();   //변경할 닉네임

                                if(edit_name.length() <= 0){
                                    Toast.makeText(getActivity(), "변경할 닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                                } else if(edit_name.length() > 10){
                                    Toast.makeText(getActivity(), "10글자 이내로만 변경 가능합니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    conNickName(edit_name, userid);
                                    dialog.dismiss();
                                }
                            }
                        };
                        DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };

                        new AlertDialog.Builder(getActivity())
                                .setTitle("변경할 닉네임 입력")
                                .setView(et)
                                .setNeutralButton("변경", edit)
                                .setNegativeButton("취소", cancel)
                                .show();

                        break;
                    //프로필이미지 변경
                    case R.id.info_fr_pimg:
                        //앨범 선택
                        DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //앨범에서 이미지 선택

                                int permission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
                                if(permission == PackageManager.PERMISSION_DENIED){
                                    // 권한 없어서 요청
                                    checkSelfPermission();
                                }else{
                                    // 권한 있음
                                    doTakeAlbumAction();
                                }


                            }
                        };

                        //취소
                        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };

                        //기본 이미지
                        DialogInterface.OnClickListener basicListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Retrofit retro_delimg = new Retrofit.Builder()
                                        .baseUrl(retroBaseApiService.Base_URL)
                                        .addConverterFactory(GsonConverterFactory.create()).build();
                                retroBaseApiService = retro_delimg.create(RetroBaseApiService.class);

                                retroBaseApiService.delImage(useruid).enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                        //서버에서 받아온 기본 이미지 비트맵으로 변환
                                        assert response.body() != null;
                                        InputStream is = response.body().byteStream();
                                        Bitmap bitmap_profile = BitmapFactory.decodeStream(is);

                                        //비트맵을 문자열로 변환하여 sharedpreference에 저장
                                        String string_profile = bitMapToString(bitmap_profile);
                                        SaveSharedPreference.setUserImage(getActivity(), string_profile);

                                        //기본 이미지로 변경
                                        info_pimg.setImageBitmap(bitmap_profile);

                                        //프래그먼트 새로고침
                                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                                        ft.detach(InfoFragment.this).attach(InfoFragment.this).commit();
                                    }
                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Toast.makeText(getActivity(), "페이지를 다시 로드해주세요.", Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }
                        };
                        new AlertDialog.Builder(getActivity())
                                .setTitle("프로필 이미지 변경")
                                .setPositiveButton("앨범선택", albumListener)
                                .setNeutralButton("기본 이미지", basicListener)
                                .setNegativeButton("취소", cancelListener)
                                .show();
                        break;
                }
            }
        };

        info_fr_pimg.setOnClickListener(listener);
        info_fr_editname.setOnClickListener(listener);
        mybook.setOnClickListener(listener);








        //유저 UID로 유저 정보, 리뷰 정보, 도서 정보 가져오기
        retroBaseApiService.getHreq(useruid).enqueue(new Callback<List<HomeData>>() {
            @Override
            public void onResponse(Call<List<HomeData>> call, Response<List<HomeData>> response) {
                infoDataList = response.body();
                items = new ArrayList<>();


                for(int l = 0; l < infoDataList.size(); l++){

                    int uid = infoDataList.get(l).getUserUID();
                    String review = infoDataList.get(l).getReview();
                    String redate = infoDataList.get(l).getReviewDate();
                    String isbn = infoDataList.get(l).getISBN13();
                    int rate = infoDataList.get(l).getRate();
                    String bname = infoDataList.get(l).getBookName();
                    String nname = infoDataList.get(l).getNickName();

                    Date date = null;
                    try {
                        date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(redate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                    String redate_2 = sdf.format(date);

                    //uid로 이미지 가져오기

                    Bitmap bitmap_profile = StringToBitMap(string_profile);

                    //리스트뷰에 추가
                    item = new HomeReviewItem(bitmap_profile, uid, review, redate_2, isbn, rate, bname, nname);
                    items.add(item);
                    //Toast.makeText(getActivity(), response.code() + "", Toast.LENGTH_SHORT).show();


                    InfoReviewAdapter infoAdapter = new InfoReviewAdapter(getActivity(), R.layout.listview_inforeview, items);
                    ListView listView = (ListView) getView().findViewById(R.id.info_listview);
                    listView.setAdapter(infoAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<HomeData>> call, Throwable t) {
                Toast.makeText(getActivity(), "리뷰 정보 없음.", Toast.LENGTH_SHORT).show();
            }
        });




    }

    private void checkSelfPermission() {

        String temp = "";

        //파일 쓰기 권한 확인
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.WRITE_EXTERNAL_STORAGE + " ";
        }
        //파일 읽기 권한 확인
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.READ_EXTERNAL_STORAGE + " ";
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            temp += Manifest.permission.CAMERA + " ";
        }
        if (!TextUtils.isEmpty(temp)) {
            // 권한 요청
            ActivityCompat.requestPermissions(getActivity(), temp.trim().split(" "),1);
        }else{
            // 모두 허용 상태
            //Toast.makeText(getActivity(), "권한을 모두 허용", Toast.LENGTH_SHORT).show();
            doTakeAlbumAction();
        }
    }





    //프로필 이미지 문자열에서 비트맵으로 변환하기
    private Bitmap StringToBitMap(String encodedString){
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch (Exception e){
            e.getMessage();
            return null;
        }
    }

    //프로필 이미지를 문자열로 Sharedpreference에 저장
    private String bitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte [] b = baos.toByteArray();
        String temp = Base64.encodeToString(b,Base64.DEFAULT);
        return temp;
    }

    // 앨범에서 이미지 가져오기
    private void doTakeAlbumAction() {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        //유저 UID 가져오기
        final int useruid = SaveSharedPreference.getUserUid(getActivity());

        if(resultCode != RESULT_OK)
            return;
        switch(requestCode) {
            case PICK_FROM_ALBUM: {
                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행
                mImageCaptureUri = data.getData();
                assert mImageCaptureUri != null;
                Log.d("InstaBook",mImageCaptureUri.getPath().toString());

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                // CROP할 이미지를 200*200 크기로 저장
                intent.putExtra("outputX", 200); // CROP한 이미지의 x축 크기
                intent.putExtra("outputY", 200); // CROP한 이미지의 y축 크기
                intent.putExtra("aspectX", 1); // CROP 박스의 X축 비율
                intent.putExtra("aspectY", 1); // CROP 박스의 Y축 비율
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_iMAGE); // CROP_FROM_CAMERA case문 이동
                break;
            }
            case CROP_FROM_iMAGE: {
                // 크롭이 된 이후의 이미지를 넘겨 받음
                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에 임시 파일을 삭제

                if(resultCode != RESULT_OK) {
                    return;
                }

                Log.d("code", "resultCode : "+ resultCode);
                final Bundle extras = data.getExtras();

                //CROP 된 이미지를 저장하기 위한 FILE 경로
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+
                        "/InstaBook/"+System.currentTimeMillis()+".jpg";

                String storage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() +"/InstaBook/";


                if(extras != null) {
                    Bitmap photo = extras.getParcelable("data"); // CROP된 BITMAP

                    //jpg로 확장자 변경, userid로 파일명 변경 후 서버에 업로드까지
                    storeCropImage(photo, useruid, storage); // CROP된 이미지를 외부저장소, 앨범에 저장한다.
                    absoultePath = filePath;

                    break;
                }

                // 임시 파일 삭제
                File f = new File(mImageCaptureUri.getPath());
                Log.d("임시파일 경로", "임시파일 경로 : "+ mImageCaptureUri.getPath().toString());
                if(f.exists()) {
                    f.delete();
                }
            }
        }
    }



    private void storeCropImage(Bitmap bitmap, int useruid, String storage){
        /*
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/InstaBook";
        File directory_InstaBook = new File(dirPath);

        if(!directory_InstaBook.exists()){ // InstaBook 디렉터리에 폴더가 없다면 (새로 이미지를 저장할 경우에 속한다.)
            directory_InstaBook.mkdir();
        }
         */

        File dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/InstaBook");
        if (!dirPath.mkdirs()) {
            Log.e("FILE", "Directory not created");
        }else{
            Log.e("FILE", "Create directory successfully");
        }

            //파일명을 UserUID로 변경
            String fileName = useruid + ".jpg"; // 파일이름 : userUID.jpg
            File photo_fin = new File(storage, fileName);

            BufferedOutputStream out = null;

            try {
                photo_fin.createNewFile();
                out = new BufferedOutputStream(new FileOutputStream(photo_fin));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
                getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.fromFile(photo_fin)));
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }


            //서버에 업로드
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), photo_fin);
            MultipartBody.Part body = MultipartBody.Part.createFormData("upload", photo_fin.getName(), reqFile);
            //RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload");


            Retrofit retro_name = new Retrofit.Builder()
                    .baseUrl(retroBaseApiService.Base_URL)
                    .addConverterFactory(GsonConverterFactory.create()).build();
            retroBaseApiService = retro_name.create(RetroBaseApiService.class);

            retroBaseApiService.postImage(body).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200) {

                        //비트맵을 문자열로 변환하여 sharedpreference에 저장
                        String string_profile = bitMapToString(bitmap);
                        SaveSharedPreference.setUserImage(getActivity(), string_profile);


                        // 레이아웃의 이미지칸에 CROP된 BITMAP을 보여줌
                        info_pimg.setImageBitmap(bitmap);

                        //이미지 동그랗게 보이기
                        info_pimg.setBackground(new ShapeDrawable(new OvalShape()));
                        info_pimg.setClipToOutline(true);


                        //프래그먼트 새로고침
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.detach(InfoFragment.this).attach(InfoFragment.this).commit();

                    }
                    //Toast.makeText(getActivity(), response.code() + "", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getActivity(), "실패", Toast.LENGTH_SHORT).show();
                }
            });

    }




    //변경할 닉네임 중복 확인
    private void conNickName(String name, String id){

        Retrofit retro_name = new Retrofit.Builder()
                .baseUrl(retroBaseApiService.Base_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retroBaseApiService = retro_name.create(RetroBaseApiService.class);

        retroBaseApiService.getEditname(name).enqueue(new Callback<List<ResponseGet>>() {

            @Override
            public void onResponse(Call<List<ResponseGet>> call, Response<List<ResponseGet>> response) {
                Toast.makeText(getActivity(), "중복된 닉네임 입니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<ResponseGet>> call, Throwable t) {
                changeNickName(name, id);
            }
        });
    }

    //닉네임 변경
    private void changeNickName(String name, String id){
        Retrofit retro_name = new Retrofit.Builder()
                .baseUrl(retroBaseApiService.Base_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retroBaseApiService = retro_name.create(RetroBaseApiService.class);

        retroBaseApiService.putName(id, name).enqueue(new Callback<ResponseGet>() {
            @Override
            public void onResponse(Call<ResponseGet> call, Response<ResponseGet> response) {
                info_nickname.setText(name);
                SaveSharedPreference.setUserNickName(getActivity(), name);
                Toast.makeText(getActivity(), "변경이 완료되었습니다.", Toast.LENGTH_SHORT).show();

                //프래그먼트 새로고침
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(InfoFragment.this).attach(InfoFragment.this).commit();

            }

            @Override
            public void onFailure(Call<ResponseGet> call, Throwable t) {
                Toast.makeText(getActivity(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_info, container, false);

        // Inflate the layout for this fragment
        return rootView;


    }
}
