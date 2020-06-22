package com.example.instabook.Fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
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

import com.example.instabook.Activity.Dialog.NicknameDialog;
import com.example.instabook.Activity.Dialog.ProfileDialog;
import com.example.instabook.Activity.ForHome.HomeData;
import com.example.instabook.Activity.ForMyBook.MyBookActivity;
import com.example.instabook.Activity.Pre.ResponseGet;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.Adapter.InfoReviewAdapter;
import com.example.instabook.ListView.HomeReviewItem;
import com.example.instabook.R;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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

import static android.content.ContentValues.TAG;
import static com.example.instabook.Activity.ForReview.ReviewActivity.retroBaseApiService;


public class InfoFragment extends Fragment implements View.OnClickListener {

    RetroBaseApiService retroBaseApiService;
    private ImageView info_pimg, info_editname;
    private TextView info_nickname, info_id, info_count;
    private FrameLayout info_fr_pimg,info_fr_editname;
    private Uri mImageCaptureUri;
    private File tempFile;

    Bitmap bm;
    Button mybook;
    View rootView;
    private ProfileDialog profileDialog;

    private InfoReviewAdapter infoAdapter;

    private static final int PICK_FROM_ALBUM = 0;

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

        //커스텀다이얼로그 온클릭 연결
        info_fr_pimg.setOnClickListener(this);
        info_fr_editname.setOnClickListener(this);


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
        retroBaseApiService.getImage(useruid).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                InputStream is = response.body().byteStream();
                Bitmap userbitmap = BitmapFactory.decodeStream(is);

                // 레이아웃의 이미지칸에 CROP된 BITMAP을 보여줌
                info_pimg.setImageBitmap(userbitmap);

                //이미지 동그랗게 보이기
                info_pimg.setBackground(new ShapeDrawable(new OvalShape()));
                info_pimg.setClipToOutline(true);

                //Toast.makeText(getActivity(), "이미지 불러오기 성공", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "이미지 불러오기 실패", Toast.LENGTH_SHORT).show();
            }
        });


        //닉네임, 아이디 표시해주기
        info_id.setText("ID: " + userid);
        info_nickname.setText(usernickname);



        //찜리스트 화면으로 이동
        mybook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyBookActivity.class);
                startActivity(intent);
            }
        });


        //유저 UID로 유저 정보, 리뷰 정보, 도서 정보 가져오기
        retroBaseApiService.getHreq(useruid).enqueue(new Callback<List<HomeData>>() {
            @Override
            public void onResponse(Call<List<HomeData>> call, Response<List<HomeData>> response) {
                infoDataList = response.body();
                items = new ArrayList<>();


                for(int l = 0; l < infoDataList.size(); l++){

                    int uid = infoDataList.get(l).getUserUID();
                    int ruid = infoDataList.get(l).getReviewUID();
                    String review = infoDataList.get(l).getReview();
                    String redate = infoDataList.get(l).getReviewDate();
                    String isbn = infoDataList.get(l).getISBN13();
                    String url = infoDataList.get(l).getBookImageUrl();
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

                    //이미지 불러오기
                    retroBaseApiService.getImage(useruid).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            InputStream is = response.body().byteStream();
                            Bitmap img_bit = BitmapFactory.decodeStream(is);

                            //Toast.makeText(getActivity(), "이미지 불러오기 성공", Toast.LENGTH_SHORT).show();

                            //ReviewUID로 Tag리스트 가져오기
                            retroBaseApiService.getReviewtag(ruid).enqueue(new Callback<List<HomeData>>() {
                                @Override
                                public void onResponse(Call<List<HomeData>> call, Response<List<HomeData>> response) {
                                    Log.d(TAG, "친구 있고 리뷰 있고 태그 있다!");
                                    List<HomeData> taglist = response.body();

                                    String tags = "";
                                    for (int w = 0; w < taglist.size(); w++) {
                                        tags += "#" + taglist.get(w).getTag() + " ";
                                    }
                                    Log.d(TAG, "태그 리스트 : " + tags);


                                    if(url == null){
                                        //기본 이미지 비트맵으로 변환
                                        Bitmap bmm = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.default_img);
                                        int height = bmm.getHeight();
                                        int width = bmm.getWidth();

                                        Bitmap resized = null;
                                        while(height>70){
                                            resized = Bitmap.createScaledBitmap(bmm,(width*70)/height,70,true);
                                            height = resized.getHeight();
                                            width = resized.getWidth();
                                        }
                                        bm = resized;

                                        //리스트뷰에 추가
                                        item = new HomeReviewItem(img_bit, uid, ruid, bm,
                                                review, redate_2, isbn, rate, bname, nname, tags);
                                        items.add(item);

                                        infoAdapter = new InfoReviewAdapter(getActivity(), R.layout.listview_inforeview, items);
                                        ListView listView = (ListView) getView().findViewById(R.id.info_listview);
                                        listView.setAdapter(infoAdapter);
                                    } else {
                                        Thread bthread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    URL urll = new URL(url);
                                                    HttpURLConnection conn = (HttpURLConnection) urll.openConnection();
                                                    conn.connect();
                                                    InputStream bis = conn.getInputStream();
                                                    Bitmap bmm = BitmapFactory.decodeStream(bis);
                                                    int height = bmm.getHeight();
                                                    int width = bmm.getWidth();

                                                    Bitmap resized = null;
                                                    if(height>width){
                                                        while(height>70){
                                                            resized = Bitmap.createScaledBitmap(bmm,(width*70)/height,70,true);
                                                            height = resized.getHeight();
                                                            width = resized.getWidth();
                                                        }
                                                    } else {
                                                        while(width>70){
                                                            resized = Bitmap.createScaledBitmap(bmm,70,(height*70)/width,true);
                                                            height = resized.getHeight();
                                                            width = resized.getWidth();
                                                        }
                                                    }

                                                    bm = resized;
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }); bthread.start();
                                        try {
                                            bthread.join();
                                            //리스트뷰에 추가
                                            item = new HomeReviewItem(img_bit, uid, ruid, bm,
                                                    review, redate_2, isbn, rate, bname, nname, tags);
                                            items.add(item);

                                            infoAdapter = new InfoReviewAdapter(getActivity(), R.layout.listview_inforeview, items);
                                            ListView listView = (ListView) getView().findViewById(R.id.info_listview);
                                            listView.setAdapter(infoAdapter);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<HomeData>> call, Throwable t) {
                                    String tags = "";
                                    //리스트뷰에 추가, 태그가 없을경우
                                    Log.d(TAG, "태그 리스트 : " + "태그 없음");

                                    if(url == null){
                                        //기본 이미지 비트맵으로 변환
                                        Bitmap bmm = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.default_img);
                                        int height = bmm.getHeight();
                                        int width = bmm.getWidth();

                                        Bitmap resized = null;
                                        while(height>70){
                                            resized = Bitmap.createScaledBitmap(bmm,(width*70)/height,70,true);
                                            height = resized.getHeight();
                                            width = resized.getWidth();
                                        }
                                        bm = resized;

                                        //리스트뷰에 추가
                                        item = new HomeReviewItem(img_bit, uid, ruid, bm,
                                                review, redate_2, isbn, rate, bname, nname, tags);
                                        items.add(item);

                                        infoAdapter = new InfoReviewAdapter(getActivity(), R.layout.listview_inforeview, items);
                                        ListView listView = (ListView) getView().findViewById(R.id.info_listview);
                                        listView.setAdapter(infoAdapter);
                                    } else {
                                        Thread bthread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    URL urll = new URL(url);
                                                    HttpURLConnection conn = (HttpURLConnection) urll.openConnection();
                                                    conn.connect();
                                                    InputStream bis = conn.getInputStream();
                                                    Bitmap bmm = BitmapFactory.decodeStream(bis);
                                                    int height = bmm.getHeight();
                                                    int width = bmm.getWidth();

                                                    Bitmap resized = null;
                                                    if(height>width){
                                                        while(height>70){
                                                            resized = Bitmap.createScaledBitmap(bmm,(width*70)/height,70,true);
                                                            height = resized.getHeight();
                                                            width = resized.getWidth();
                                                        }
                                                    } else {
                                                        while(width>70){
                                                            resized = Bitmap.createScaledBitmap(bmm,70,(height*70)/width,true);
                                                            height = resized.getHeight();
                                                            width = resized.getWidth();
                                                        }
                                                    }

                                                    bm = resized;
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }); bthread.start();
                                        try {
                                            bthread.join();
                                            //리스트뷰에 추가
                                            item = new HomeReviewItem(img_bit, uid, ruid, bm,
                                                    review, redate_2, isbn, rate, bname, nname, tags);
                                            items.add(item);

                                            infoAdapter = new InfoReviewAdapter(getActivity(), R.layout.listview_inforeview, items);
                                            ListView listView = (ListView) getView().findViewById(R.id.info_listview);
                                            listView.setAdapter(infoAdapter);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    //Toast.makeText(getActivity(), response.code() + "", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getActivity(), "이미지 불러오기 실패", Toast.LENGTH_SHORT).show();
                        }
                    });



                }
            }

            @Override
            public void onFailure(Call<List<HomeData>> call, Throwable t) {
                // Toast.makeText(getActivity(), "리뷰 정보 없음.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void checkSelfPermission() {

        String temp = ""; //파일 읽기 권한 확인
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.READ_EXTERNAL_STORAGE + " ";
        }
        //파일 쓰기 권한 확인
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.WRITE_EXTERNAL_STORAGE + " ";
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



        switch(requestCode) {
            case PICK_FROM_ALBUM: {
                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행
                if(data == null){  //앨범에서 뒤로가기 시
                    Log.d("ALBUM", "앨범 : "+"이미지 선택 안함");
                    break;
                }
                mImageCaptureUri = data.getData();
                assert mImageCaptureUri != null;



                Cursor cursor = null;

                try{

                    String[] proj = {MediaStore.Images.Media.DATA};

                    assert mImageCaptureUri != null;
                    cursor = getActivity().getContentResolver().query(mImageCaptureUri, proj, null, null, null);

                    assert cursor != null;
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                    cursor.moveToFirst();

                    tempFile = new File(cursor.getString(column_index));


                }finally {
                    if(cursor != null){
                        cursor.close();
                    }
                }
                Log.d("InstaBook", "이미지경로 : "+mImageCaptureUri);
                cropImage(mImageCaptureUri);
                Log.d("CODE", "REQUESTCODE : "+ requestCode+", crop code : "+ Crop.REQUEST_CROP);

                break;
            }

            case Crop.REQUEST_CROP: {
                //크롭된 이미지 받기

                if(tempFile.length() <=0){  //크롭 도중 뒤로가기시 종료
                    Log.d("cropfile", "crop : " + "크롭 취소");
                    break;
                }

                Log.d("cropfile", "cropfile 경로 : " + tempFile);

                //서버, 이미지뷰에 업로드
                storeCropImage(tempFile);
            }
        }
    }


    private void cropImage(Uri photoUri){

        //갤러리에서 선택한 경우 tempFile이 없으므로 새로 생성해준다.
        try{
            tempFile = createImageFile();

            Log.d(TAG, "빈파일 경로 : " + tempFile);
            //크롭 후 저장할 Uri
            Uri savingUri = FileProvider.getUriForFile(getContext(), "com.example.instabook.provider", tempFile);

            Crop.of(photoUri, savingUri).asSquare().start(getContext(), InfoFragment.this, Crop.REQUEST_CROP);
        }catch (IOException e){
            Log.e(TAG, "이미지 처리 오류! 다시 시도해주세요.");
            Toast.makeText(getActivity(), "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }



    }


    private File createImageFile() throws IOException {


        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "instabook_"+timeStamp;

        // 이미지 파일 이름
        //String imageFileName = Integer.toString(useruid).trim();
        Log.d("NAME", "파일명 : " + imageFileName);

        // 이미지가 저장될 폴더 이름
        //File storageDir = new File(Environment.getExternalStorageDirectory() + "/InstaBook/");
        //if (!storageDir.exists()) storageDir.mkdirs();
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/InstaBook");
        if(!storageDir.mkdirs()){
            Log.e("FILE", "Directory not created");
        }else{
            Log.d("FILE", "Create directory successfully");
        }

        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() +"/InstaBook/"));

        Log.d("image", "빈파일 : " + image);


        return image;
    }



    private void storeCropImage(File cropfile){

        //빈파일인 경우 업로드 안함
        if(cropfile.length() <=0){
            return;
        }

        //유저 UID 가져오기
        final int useruid = SaveSharedPreference.getUserUid(getActivity());


        //서버에 업로드
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), cropfile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("upload", useruid+".jpg", reqFile);
        //RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload");


        Retrofit retro_name = new Retrofit.Builder()
                .baseUrl(retroBaseApiService.Base_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        retroBaseApiService = retro_name.create(RetroBaseApiService.class);

        retroBaseApiService.postImage(body, useruid).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                retroBaseApiService.getImage(useruid).enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        assert response.body() != null;
                        InputStream is = response.body().byteStream();
                        Bitmap bitmap_prof = BitmapFactory.decodeStream(is);


                        // 레이아웃의 이미지칸에 CROP된 BITMAP을 보여줌
                        info_pimg.setImageBitmap(bitmap_prof);

                        //이미지 동그랗게 보이기
                        info_pimg.setBackground(new ShapeDrawable(new OvalShape()));
                        info_pimg.setClipToOutline(true);


                        tempFile = null;


                        //프래그먼트 새로고침
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.detach(InfoFragment.this).attach(InfoFragment.this).commit();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getActivity(), "실패", Toast.LENGTH_SHORT).show();
                    }
                });
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


    //클릭 이벤트
    @Override
    public void onClick(View v) {
        switch (v.getId()){

            //닉네임 변경
            case R.id.info_fr_editname:
                NicknameDialog ndialog = new NicknameDialog(getContext());
                ndialog.setDialogListener(new NicknameDialog.NicknameDialogListener() {

                    @Override
                    public void onPositiveClicked(String nickname) {
                        Log.d("NICKNAME", "닉네임 : "+nickname);
                        String userid = SaveSharedPreference.getUserName(getActivity());

                        if(nickname.length() <= 0){
                            Toast.makeText(getActivity(), "변경할 닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        } else if(nickname.length() > 10){
                            Toast.makeText(getActivity(), "10글자 이내로만 변경 가능합니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            conNickName(nickname, userid);
                        }
                    }

                    @Override
                    public void onNegativeClicked() {

                    }
                });

                ndialog.show();
                break;

            //프로필 사진 변경
            case R.id.info_fr_pimg:

                ProfileDialog pdialog = new ProfileDialog(getContext());
                pdialog.setDialogListener(new ProfileDialog.ProfileDialogListener() {
                    @Override
                    public void onPositiveClicked() {  //앨범
                        int permission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
                        if(permission == PackageManager.PERMISSION_DENIED){
                            // 권한 없어서 요청
                            checkSelfPermission();
                        }else {
                            // 권한 있음
                            doTakeAlbumAction();
                        }
                    }

                    @Override
                    public void onNegativeClicked() {  //취소

                    }

                    @Override
                    public void onNeutralClicked() {   //기본

                        int useruid = SaveSharedPreference.getUserUid(getActivity());


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
                                Bitmap bitmap = BitmapFactory.decodeStream(is);

                                //기본 이미지로 변경
                                info_pimg.setImageBitmap(bitmap);

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
                });
                pdialog.show();
                break;
        }

    }
}