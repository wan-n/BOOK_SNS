package com.example.instabook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
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
import com.example.instabook.Activity.ForHome.UserBookUIDData;
import com.example.instabook.Activity.ForReview.ModiReviewActivity;
import com.example.instabook.Activity.ForReview.ReviewDelActivity;
import com.example.instabook.Activity.ForUser.NotiBookActivity;
import com.example.instabook.Activity.ForUser.NotiBookDelActivity;
import com.example.instabook.Activity.ForUser.UserBookData;
import com.example.instabook.Activity.MainActivity;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.ListView.HomeReviewItem;
import com.example.instabook.R;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.PendingIntent.getActivity;
import static com.example.instabook.Activity.ForReview.ReviewActivity.retroBaseApiService;

public class HomeReviewAdapter extends BaseAdapter {
    private static final String TAG = "BookListAdapter";
    SaveSharedPreference sp;
    int layout;
    Context context;
    LayoutInflater inflater;
    ImageButton MemuImageButton;
    ImageButton favButton;
    ArrayList<HomeReviewItem> items;
    String iisbn;
    int uuid;
    int ubuid;
    int rrate;
    int useruid2;
    String rreview;
    String bbname;

    UserBookUIDData uBookData;
    HomeReviewItem homeReviewItem;

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
        //유저 UID 가져오기
        final int useruid = sp.getUserUid(context.getApplicationContext());
        useruid2 = useruid;
        final int pos = position;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_homereview, parent, false);
        }

        homeReviewItem = getItem(pos);

        MemuImageButton = (ImageButton) convertView.findViewById(R.id.btn_menu);
        MemuImageButton.setOnClickListener(this::menuOnClick);
        favButton = (ImageButton) convertView.findViewById(R.id.imgbtn_favorite);

        CircularImageView CImagetView = (CircularImageView) convertView.findViewById(R.id.uf_icon);
        TextView NickTextView = (TextView) convertView.findViewById(R.id.txt_nick);
        TextView DateTextView = (TextView) convertView.findViewById(R.id.txt_date);
        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.ratingbarSmall);
        TextView BnameTextView = (TextView) convertView.findViewById(R.id.txt_bname);
        TextView ReviewTextView = (TextView) convertView.findViewById(R.id.txt_review);
        TextView TagTextView = (TextView) convertView.findViewById(R.id.txt_tag);

        CImagetView.setImageBitmap(homeReviewItem.getIconDrawable());
        NickTextView.setText(homeReviewItem.getnName());
        DateTextView.setText(homeReviewItem.getReDate());
        BnameTextView.setText(homeReviewItem.getbName());
        ReviewTextView.setText(homeReviewItem.getReview());
        ReviewTextView.setMovementMethod(new ScrollingMovementMethod());
        ratingBar.setNumStars(homeReviewItem.getRate());

        iisbn = homeReviewItem.getIsbn13();
        uuid = homeReviewItem.getuId();
        rrate = homeReviewItem.getRate();
        rreview = homeReviewItem.getReview();
        bbname = homeReviewItem.getbName();

        return convertView;
    }

    public void menuOnClick(View v) {
        //버튼이 눌렸을때 여기로옴
        PopupMenu popup = new PopupMenu(context, v);

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
                        Intent in = new Intent(context, ModiReviewActivity.class);
                        in.putExtra("isbn", iisbn);
                        in.putExtra("uid", uuid);
                        in.putExtra("rate", rrate);
                        in.putExtra("review", rreview);
                        in.putExtra("title", bbname);
                        context.startActivity(in);

                        break;
                    case R.id.remove:
                        Intent intent = new Intent(context, ReviewDelActivity.class);
                        intent.putExtra("isbn", iisbn);
                        intent.putExtra("uid", uuid);
                        context.startActivity(intent);

                        break;
                }
                return false;
            }
        });
        popup.show();
    }

}
