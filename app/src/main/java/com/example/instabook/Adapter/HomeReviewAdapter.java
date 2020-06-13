package com.example.instabook.Adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.instabook.Activity.ForUser.NotiBookActivity;
import com.example.instabook.Activity.ForUser.UserBookData;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.ListView.HomeReviewItem;
import com.example.instabook.R;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.instabook.Activity.ForReview.ReviewActivity.retroBaseApiService;

public class HomeReviewAdapter extends BaseAdapter {
    private static final String TAG = "BookListAdapter";

    int layout;
    Context context;
    LayoutInflater inflater;
    ArrayList<HomeReviewItem> items;

    public HomeReviewAdapter(FragmentActivity activity, int layout, ArrayList<HomeReviewItem> items) {
        this.context = activity;
        this.items = items;
        this.layout = layout;

        inflater = LayoutInflater.from(this.context);
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
        final int pos = position;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_homereview, parent, false);
        }

        CircularImageView CImagetView = (CircularImageView) convertView.findViewById(R.id.uf_icon);
        TextView NickTextView = (TextView) convertView.findViewById(R.id.txt_nick);
        TextView DateTextView = (TextView) convertView.findViewById(R.id.txt_date);
        ImageButton MemuImageButton = (ImageButton) convertView.findViewById(R.id.btn_menu);
        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.ratingbarSmall);
        TextView BnameTextView = (TextView) convertView.findViewById(R.id.txt_bname);
        TextView ReviewTextView = (TextView) convertView.findViewById(R.id.txt_review);
        TextView TagTextView = (TextView) convertView.findViewById(R.id.txt_tag);

        HomeReviewItem homeReviewItem = getItem(pos);

        ImageButton favButton = (ImageButton)convertView.findViewById(R.id.imgbtn_favorite);
        if(homeReviewItem.getUserBookUID() == -1){
            favButton.setImageResource(R.drawable.favorite_border_black);
        } else {
            favButton.setImageResource(R.drawable.favorite_black);
        }
        CImagetView.setImageBitmap(homeReviewItem.getIconDrawable());
        NickTextView.setText(homeReviewItem.getnName());
        DateTextView.setText(homeReviewItem.getReDate());
        BnameTextView.setText(homeReviewItem.getbName());
        ReviewTextView.setText(homeReviewItem.getReview());
        ratingBar.setNumStars(homeReviewItem.getRate());

        favButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (homeReviewItem.getUserBookUID() == -1) {
                    favButton.setImageResource(R.drawable.favorite_black);

                    //빈 하트 클릭으로 소유도서 생성
                    HashMap<String, Object> map = new HashMap<>();

                    map.put("ISBN13", homeReviewItem.getIsbn13());
                    map.put("UserUID", homeReviewItem.getuId());

                    Retrofit retro_mubook = new Retrofit.Builder()
                            .baseUrl(retroBaseApiService.Base_URL)
                            .addConverterFactory(GsonConverterFactory.create()).build();
                    retroBaseApiService = retro_mubook.create(RetroBaseApiService.class);
                    //유저 소유 도서 정보 만들기
                    retroBaseApiService.postUBook(map).enqueue(new Callback<UserBookData>() {
                        @Override
                        public void onResponse(Call<UserBookData> call, Response<UserBookData> response) {
                            Intent intent = new Intent(context.getApplicationContext(), NotiBookActivity.class);
                            context.startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<UserBookData> call, Throwable t) {
                            Toast.makeText(context.getApplicationContext(), "찜하기 생성 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (homeReviewItem.getUserBookUID() > -1) {
                    favButton.setImageResource(R.drawable.favorite_border_black);
                    //꽉찬 하트 클릭으로 소유 도서 삭제
                    Retrofit retro_rmbook = new Retrofit.Builder()
                            .baseUrl(retroBaseApiService.Base_URL)
                            .addConverterFactory(GsonConverterFactory.create()).build();
                    retroBaseApiService = retro_rmbook.create(RetroBaseApiService.class);
                    //유저 소유 도서 정보 만들기
                    retroBaseApiService.delUBook(homeReviewItem.getIsbn13(),homeReviewItem.getuId()).enqueue(new Callback<UserBookData>() {
                        @Override
                        public void onResponse(Call<UserBookData> call, Response<UserBookData> response) {
                            Intent intent = new Intent(context.getApplicationContext(), NotiBookFActivity.class);
                            context.startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<UserBookData> call, Throwable t) {
                            Toast.makeText(context.getApplicationContext(), "찜하기 제거 실패", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });


        return convertView;
    }

    public void menuOnClick(View v){
        //버튼이 눌렸을때 여기로옴
        PopupMenu popup = new PopupMenu(context.getApplicationContext(), v);

        //xml파일에 메뉴 정의한것을 가져오기위해서 전개자 선언
        MenuInflater inflater = popup.getMenuInflater();
        Menu menu = popup.getMenu();

        //실제 메뉴 정의한것을 가져오는 부분 menu 객체에 넣어줌
            inflater.inflate(R.menu.homefragment_menu, menu);

        //메뉴가 클릭했을때 처리하는 부분
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.modify:
                        //Intent intent = new Intent(context.getApplicationContext(), );
                        //context.startActivity(intent);
                        break;
                    case  R.id.remove:
                        //Intent intent = new Intent(context.getApplicationContext(), );
                        //context.startActivity(intent);
                        break;
                }
                return false;
            }
        });
        popup.show();
    }
}
