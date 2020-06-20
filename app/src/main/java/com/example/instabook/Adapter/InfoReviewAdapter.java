package com.example.instabook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.example.instabook.Activity.CircularImageView;
import com.example.instabook.Activity.ForHashTag.Hashtag;
import com.example.instabook.Activity.ForHome.UserBookUIDData;
import com.example.instabook.Activity.ForReview.ModiReviewActivity;
import com.example.instabook.Activity.ForReview.ReviewDelActivity;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.ListView.HomeReviewItem;
import com.example.instabook.R;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InfoReviewAdapter extends BaseAdapter {
    private static final String TAG = "InfoListAdapter";
    SaveSharedPreference sp;
    int layout;
    Context context;
    LayoutInflater inflater;
    ImageButton MemuImageButton;
    ImageButton favButton;
    ArrayList<HomeReviewItem> items;
    int useruid2;
    String str;

    UserBookUIDData uBookData;
    HomeReviewItem homeReviewItem;

    public InfoReviewAdapter(FragmentActivity activity, int layout, ArrayList<HomeReviewItem> items) {
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
            convertView = inflater.inflate(R.layout.listview_inforeview, parent, false);
        }

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


        homeReviewItem = getItem(pos);

        //태그
        str = homeReviewItem.getTags();
        ArrayList<int[]> hashtagSpan = getSpans(str,'#');
        SpannableString commentsContent = new SpannableString(str);
        setSpanComment(commentsContent,hashtagSpan) ;
        TagTextView.setMovementMethod(LinkMovementMethod.getInstance());
        TagTextView.setText(commentsContent);


        CImagetView.setImageBitmap(homeReviewItem.getIconDrawable());
        NickTextView.setText(homeReviewItem.getnName());
        DateTextView.setText(homeReviewItem.getReDate());
        BnameTextView.setText(homeReviewItem.getbName());
        ReviewTextView.setText(homeReviewItem.getReview());
        ReviewTextView.setMovementMethod(new ScrollingMovementMethod());
        ratingBar.setNumStars(homeReviewItem.getRate());

        MemuImageButton.setTag(pos);
        MemuImageButton.setOnClickListener(this::menuOnClick);



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

        int position = Integer.parseInt((v.getTag().toString()));
        HomeReviewItem item = items.get(position);

        final String iisbn = item.getIsbn13();
        final int rrid = item.getRuid();
        final int uuid = item.getuId();
        final int rrate = item.getRate();
        final String rreview = item.getReview();
        final String bbname = item.getbName();
        final String btag = item.getTags();



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
                        in.putExtra("rid",rrid);
                        in.putExtra("rate", rrate);
                        in.putExtra("review", rreview);
                        in.putExtra("title", bbname);
                        in.putExtra("tags", btag);
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
