package com.example.instabook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.instabook.Activity.CircularImageView;
import com.example.instabook.Activity.ForHashTag.HashTagHelper;
import com.example.instabook.Activity.ForHashTag.Hashtag;
import com.example.instabook.Activity.ForReview.ModiReviewActivity;
import com.example.instabook.Activity.ForReview.ReviewDelActivity;
import com.example.instabook.Activity.ForUser.UserBookData;
import com.example.instabook.Activity.MainActivity;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.ListView.HomeReviewItem;
import com.example.instabook.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import static com.example.instabook.Activity.ForReview.ReviewActivity.retroBaseApiService;

public class HomeReviewAdapter extends BaseAdapter implements HashTagHelper.OnHashTagClickListener{
    private static final String TAG = "BookListAdapter";
    int layout;
    Context context;
    LayoutInflater inflater;
    SaveSharedPreference sp;
    int useruid;
    int himg;
    String str;
    HomeReviewItem homeReviewItem;
    ArrayList<HomeReviewItem> items = new ArrayList<>();

    private HashTagHelper mTextHashTagHelper;

    public HomeReviewAdapter(FragmentActivity activity, int layout, ArrayList<HomeReviewItem> items) {
        this.context = activity;
        this.items = items;
        this.layout = layout;

        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public void onHashTagClicked(String hashTag) {
        //hashtag 클릭 시
    }

    static class ViewHolder {
        ImageButton MemuImageButton;
        ImageButton favButton;
        CircularImageView CImagetView;
        TextView NickTextView;
        TextView DateTextView;
        RatingBar ratingBar;
        TextView BnameTextView;
        TextView ReviewTextView;
        TextView TagTextView;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public HomeReviewItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int userUid = sp.getUserUid(context.getApplicationContext());
        useruid = userUid;
        ViewHolder hodler;
        final int pos = position;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_homereview, parent, false);
            hodler = new ViewHolder();
            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            hodler.CImagetView = (CircularImageView) convertView.findViewById(R.id.uf_icon);
            hodler.NickTextView = (TextView) convertView.findViewById(R.id.txt_nick);
            hodler.DateTextView = (TextView) convertView.findViewById(R.id.txt_date);
            hodler.MemuImageButton = (ImageButton) convertView.findViewById(R.id.btn_menu);
            hodler.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingbarSmall);
            hodler.BnameTextView = (TextView) convertView.findViewById(R.id.txt_bname);
            hodler.favButton = (ImageButton) convertView.findViewById(R.id.imgbtn_favorite);
            hodler.ReviewTextView = (TextView) convertView.findViewById(R.id.txt_review);
            hodler.TagTextView = (TextView) convertView.findViewById(R.id.txt_tag);

            convertView.setTag(hodler);
        } else {
            hodler = (ViewHolder)convertView.getTag();
        }

        //item 가져오기
        homeReviewItem = getItem(pos);

        //item 내용 세팅
        himg = setheart(homeReviewItem);

        //태그
        str = homeReviewItem.getTags();
        ArrayList<int[]> hashtagSpan = getSpans(str,'#');
        SpannableString commentsContent = new SpannableString(str);
        setSpanComment(commentsContent,hashtagSpan) ;
        hodler.TagTextView.setMovementMethod(LinkMovementMethod.getInstance());
        hodler.TagTextView.setText(commentsContent);


        //item 내용 setting
        hodler.CImagetView.setImageBitmap(homeReviewItem.getIconDrawable());
        hodler.NickTextView.setText(homeReviewItem.getnName());
        hodler.DateTextView.setText(homeReviewItem.getReDate());
        hodler.BnameTextView.setText(homeReviewItem.getbName());
        hodler.ReviewTextView.setText(homeReviewItem.getReview());
        hodler.ReviewTextView.setMovementMethod(new ScrollingMovementMethod());
        hodler.ratingBar.setNumStars(homeReviewItem.getRate());

        if(userUid == homeReviewItem.getuId()){
            hodler.favButton.setImageResource(R.drawable.ripple_effect);
        } else {
            hodler.favButton.setImageResource(himg);
            hodler.favButton.setTag(pos);
            hodler.favButton.setOnClickListener(jjimOnClickListener);
        }

        hodler.MemuImageButton.setTag(pos);
        hodler.MemuImageButton.setOnClickListener(this::menuOnClick);

        return convertView;
    }


    public ArrayList<int[]> getSpans(String body, char prefix) {
        ArrayList<int[]> spans = new ArrayList<int[]>();

        Pattern pattern = Pattern.compile(prefix + "\\w+");
        Matcher matcher = pattern.matcher(body);

        // Check all occurrences
        while (matcher.find()) {
            int[] currentSpan = new int[2];
            currentSpan[0] = matcher.start();
            currentSpan[1] = matcher.end();
            spans.add(currentSpan);
        }

        return  spans;
    }


    private void setSpanComment(SpannableString commentsContent, ArrayList<int[]> hashtagSpans) {
        for(int i = 0; i < hashtagSpans.size(); i++) {
            int[] span = hashtagSpans.get(i);
            int hashTagStart = span[0];
            int hashTagEnd = span[1];

            commentsContent.setSpan(new Hashtag(context), hashTagStart, hashTagEnd, 0);

        }

    }


    public void menuOnClick(View v) {
        //버튼이 눌렸을때 여기로옴
        PopupMenu popup = new PopupMenu(context, v);

        //유저 UID 가져오기
        //final int useruid = sp.getUserUid(context.getApplicationContext());

        int position = Integer.parseInt((v.getTag().toString()));
        HomeReviewItem item = items.get(position);

        final int uuid = item.getuId();
        final int rrid = item.getRuid();
        final String iisbn = item.getIsbn13();
        //Bitmap bitmap = item.getBitmap();
        String uurl = item.getUrl();
        final int rrate = item.getRate();
        final String rreview = item.getReview();
        final String bbname = item.getbName();
        final String tags = item.getTags();
        Log.d(TAG,"intent 전 : "+rrid+", "+tags);

        //비트맵 이미지 byte로 변환
        //ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //bitmap.compress(CompressFormat.JPEG,100,stream);
        //byte[] b = stream.toByteArray();


        //xml파일에 메뉴 정의한것을 가져오기위해서 전개자 선언
        MenuInflater inflater = popup.getMenuInflater();
        Menu menu = popup.getMenu();

        //실제 메뉴 정의한것을 가져오는 부분 menu 객체에 넣어줌
        inflater.inflate(R.menu.homefragment_menu, menu);

        //메뉴가 클릭했을때 처리하는 부분
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.modify:
                        if(uuid == useruid){
                            Intent in = new Intent(context.getApplicationContext(), ModiReviewActivity.class);
                            in.putExtra("isbn", iisbn);
                            in.putExtra("uid", uuid);
                            in.putExtra("rid",rrid);
                            in.putExtra("rate", rrate);
                            in.putExtra("review", rreview);
                            in.putExtra("title", bbname);
                            in.putExtra("tags",tags);
                            in.putExtra("url",uurl);
                            Log.d(TAG,"intent 후 수정 : "+rrid+", "+tags);
                            context.startActivity(in);
                        }
                        break;
                    case R.id.remove:
                        if(uuid == useruid){
                            Intent inten = new Intent(context.getApplicationContext(), ReviewDelActivity.class);
                            inten.putExtra("rrid",rrid);
                            inten.putExtra("bname",bbname);
                            context.startActivity(inten);
                        }
                        break;
                }
                return false;
            }
        });
        popup.show();
    }


    final Button.OnClickListener jjimOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = Integer.parseInt((v.getTag().toString()));
            HomeReviewItem reitem = items.get(position);

            String isbn =reitem.getIsbn13();
            int ubuid = reitem.getUbuid();

            HashMap<String, Object> map = new HashMap<>();
            map.put("uid",useruid);
            map.put("isbn",isbn);

            if (ubuid > 0){ //찜 목록에서 없애기
                Retrofit retro_djim = new Retrofit.Builder()
                        .baseUrl(retroBaseApiService.Base_URL)
                        .addConverterFactory(GsonConverterFactory.create()).build();
                retroBaseApiService = retro_djim.create(RetroBaseApiService.class);

                retroBaseApiService.delUBook(ubuid).enqueue(new Callback<UserBookData>() {
                    @Override
                    public void onResponse(Call<UserBookData> call, Response<UserBookData> response) {
                        notifyDataSetChanged();
                        Toast.makeText(context.getApplicationContext(), reitem.getbName()+"찜 도서 제거 성공", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(Call<UserBookData> call, Throwable t) {
                        Toast.makeText(context.getApplicationContext(), "찜 도서 제거 실패", Toast.LENGTH_SHORT).show();
                    }

                });
            } else { //찜목록에 넣기
                Retrofit retro_jjim = new Retrofit.Builder()
                        .baseUrl(retroBaseApiService.Base_URL)
                        .addConverterFactory(GsonConverterFactory.create()).build();
                retroBaseApiService = retro_jjim.create(RetroBaseApiService.class);

                retroBaseApiService.postUBook(map).enqueue(new Callback<UserBookData>() {
                    @Override
                    public void onResponse(Call<UserBookData> call, Response<UserBookData> response) {
                        notifyDataSetChanged();
                        Toast.makeText(context.getApplicationContext(), reitem.getbName()+"찜 도서 추가 성공", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(Call<UserBookData> call, Throwable t) {
                        Toast.makeText(context.getApplicationContext(), "찜 도서 추가 실패", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    };


    private int setheart(HomeReviewItem item){
        int himg;
        int ubid = item.getUbuid();
        if(ubid == 0){
            himg = R.drawable.favorite_border_black;
        } else {
            himg = R.drawable.favorite_black;
        }
        return himg;
    }
}
