package com.example.instabook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instabook.Activity.ForTag.BookInfoActivity;
import com.example.instabook.Activity.ForHashTag.HashTagHelper;
import com.example.instabook.Activity.ForHashTag.Hashtag;
import com.example.instabook.Activity.ForTag.SearchTagActivity;
import com.example.instabook.Activity.ForUser.UserBookData;
import com.example.instabook.Activity.MainActivity;
import com.example.instabook.Activity.Pre.RetroBaseApiService;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.ListView.RecmdBookItem;
import com.example.instabook.ListView.TagBookItem;
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

public class TagBookAdapter extends BaseAdapter implements HashTagHelper.OnHashTagClickListener{
    private static final String TAG = "TagBookAdapter";
    int layout;
    Context context;
    LayoutInflater inflater;
    SaveSharedPreference sp;
    TagBookItem tagBookItem;
    ArrayList<TagBookItem> items = new ArrayList<>();
    int useruid;
    int himge;
    String str;

    public TagBookAdapter(SearchTagActivity activity, int layout, ArrayList<TagBookItem> items) {
        this.context = activity;
        this.items = items;
        this.layout = layout;

        inflater = LayoutInflater.from(this.context);
    }

    static class ViewHolder {
        ImageView bookImageView;
        TextView titleTextView;
        TextView authorTextView;
        TextView pubTextView;
        TextView tagTextView;
        ImageButton jjimImageButton;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public TagBookItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int uid = sp.getUserUid(context.getApplicationContext());
        useruid = uid;
        ViewHolder hodler;
        final int pos = position;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_tagbook, parent, false);
            hodler = new ViewHolder();

            hodler.bookImageView = convertView.findViewById(R.id.img_book);
            hodler.titleTextView = convertView.findViewById(R.id.text_title);
            hodler.authorTextView = convertView.findViewById(R.id.text_author);
            hodler.pubTextView = convertView.findViewById(R.id.text_pub);
            hodler.tagTextView = convertView.findViewById(R.id.text_tag);
            hodler.jjimImageButton = convertView.findViewById(R.id.imgbtn_favorite);

            convertView.setTag(hodler);
        } else {
            hodler = (ViewHolder)convertView.getTag();
        }

        //item 가져오기
        tagBookItem = getItem(pos);

        himge = setheart(tagBookItem);

        //태그
        str = tagBookItem.getTag();
        ArrayList<int[]> hashtagSpan = getSpans(str,'#');
        SpannableString commentsContent = new SpannableString(str);
        setSpanComment(commentsContent,hashtagSpan) ;
        hodler.tagTextView.setMovementMethod(LinkMovementMethod.getInstance());
        hodler.tagTextView.setText(commentsContent);

        //item 내용 setting
        String url = tagBookItem.getUrl();

        Glide.with(convertView).load(url).override(70,70).error(R.drawable.default_img).into(hodler.bookImageView);
        //hodler.bookImageView.setImageBitmap(tagBookItem.getBp());
        hodler.titleTextView.setText(tagBookItem.getBname());
        hodler.authorTextView.setText(tagBookItem.getAuthor());
        hodler.jjimImageButton.setImageResource(himge);


        //도서 정보 화면으로 이동 온클릭 이벤트
        hodler.bookImageView.setTag(pos);
        hodler.bookImageView.setOnClickListener(moreOnClickListener);


        //찜 버튼 클릭
        hodler.jjimImageButton.setTag(pos);
        hodler.jjimImageButton.setOnClickListener(jjimOnClickListener);


        return convertView;
    }

    final Button.OnClickListener moreOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = Integer.parseInt((v.getTag().toString()));
            TagBookItem tagitem = items.get(position);
            //String pubdate2 = tagitem.getPub();

            Bitmap bm = tagitem.getBp();
            String title = tagitem.getBname();
            String author = tagitem.getAuthor();
            String isbn = tagitem.getIsbn();
            String pub = tagitem.getPub();
            String pubdate = tagitem.getPubdate();
            String tag = tagitem.getTag();
            int ubuid = tagitem.getUbuid();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG,100,stream);
            byte[] b = stream.toByteArray();


            Intent intent = new Intent(context.getApplicationContext(), BookInfoActivity.class);
            intent.putExtra("bm",b);
            intent.putExtra("title",title);
            intent.putExtra("author",author);
            intent.putExtra("isbn",isbn);
            intent.putExtra("pub",pub);
            intent.putExtra("pubdate",pubdate);
            intent.putExtra("tag",tag);
            intent.putExtra("ubuid",ubuid);
            context.startActivity(intent);
        }
    };


    final Button.OnClickListener jjimOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = Integer.parseInt((v.getTag().toString()));
            TagBookItem tagitem = items.get(position);

            String isbn =tagitem.getIsbn();
            int ubuid = tagitem.getUbuid();

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
                        Toast.makeText(context.getApplicationContext(), tagitem.getBname()+"찜 도서 제거 성공", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(context.getApplicationContext(), tagitem.getBname()+"찜 도서 추가 성공", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(Call<UserBookData> call, Throwable t) {
                        Toast.makeText(context.getApplicationContext(), "찜 도서 추가 실패", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    };

    private int setheart(TagBookItem item){
        int himg;
        int bid = item.getUbuid();
        if(bid == 0){
            himg = R.drawable.favorite_border_black;
        } else {
            himg = R.drawable.favorite_black;
        }
        return himg;
    }


    @Override
    public void onHashTagClicked(String hashTag) {

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
}
