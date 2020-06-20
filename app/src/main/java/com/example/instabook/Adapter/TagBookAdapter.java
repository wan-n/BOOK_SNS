package com.example.instabook.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.instabook.Activity.CircularImageView;
import com.example.instabook.Activity.ForBook.BookInfoActivity;
import com.example.instabook.Activity.ForHashTag.HashTagHelper;
import com.example.instabook.Activity.ForHashTag.Hashtag;
import com.example.instabook.Activity.ForReview.ReviewDelActivity;
import com.example.instabook.Activity.ForTag.SearchTagActivity;
import com.example.instabook.Activity.SaveSharedPreference;
import com.example.instabook.ListView.HomeReviewItem;
import com.example.instabook.ListView.TagBookItem;
import com.example.instabook.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagBookAdapter extends BaseAdapter implements HashTagHelper.OnHashTagClickListener{
    private static final String TAG = "TagBookAdapter";
    int layout;
    Context context;
    LayoutInflater inflater;
    SaveSharedPreference sp;
    TagBookItem tagBookItem;
    ArrayList<TagBookItem> items = new ArrayList<>();
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
        ImageButton moreImageButton;
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

        ViewHolder hodler;
        final int pos = position;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_tagbook, parent, false);
            hodler = new ViewHolder();

            hodler.bookImageView = (ImageView) convertView.findViewById(R.id.img_book);
            hodler.titleTextView = (TextView) convertView.findViewById(R.id.text_title);
            hodler.authorTextView = (TextView) convertView.findViewById(R.id.text_author);
            hodler.pubTextView = (TextView) convertView.findViewById(R.id.text_pub);
            hodler.tagTextView = (TextView) convertView.findViewById(R.id.text_tag);
            hodler.moreImageButton = (ImageButton) convertView.findViewById(R.id.pick_btn);

            convertView.setTag(hodler);
        } else {
            hodler = (ViewHolder)convertView.getTag();
        }

        //item 가져오기
        tagBookItem = getItem(pos);

        //태그
        str = tagBookItem.getTag();
        ArrayList<int[]> hashtagSpan = getSpans(str,'#');
        SpannableString commentsContent = new SpannableString(str);
        setSpanComment(commentsContent,hashtagSpan) ;
        hodler.tagTextView.setMovementMethod(LinkMovementMethod.getInstance());
        hodler.tagTextView.setText(commentsContent);

        //item 내용 setting
        hodler.bookImageView.setImageBitmap(tagBookItem.getBp());
        hodler.titleTextView.setText(tagBookItem.getBname());
        hodler.authorTextView.setText(tagBookItem.getAuthor());
        hodler.moreImageButton.setImageResource(R.drawable.more_black);

        hodler.moreImageButton.setTag(pos);
        hodler.moreImageButton.setOnClickListener(moreOnClickListener);


        return convertView;
    }

    final Button.OnClickListener moreOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = Integer.parseInt((v.getTag().toString()));
            TagBookItem tagitem = items.get(position);

            Bitmap bm = tagitem.getBp();
            String title = tagitem.getBname();
            String author = "저자 <"+tagitem.getAuthor()+">";
            String pub = "출판사 <"+tagitem.getPub()+">";
            String pubdate = "출판일 <"+tagitem.getPubdate()+">";
            String tag = tagitem.getTag();
            int ubuid = tagitem.getUbuid();

            Intent intent = new Intent(context.getApplicationContext(), BookInfoActivity.class);
            intent.putExtra("bm",bm);
            intent.putExtra("title",title);
            intent.putExtra("author",author);
            intent.putExtra("pub",pub);
            intent.putExtra("pubdate",pubdate);
            intent.putExtra("tag",tag);
            intent.putExtra("ubuid",ubuid);
            context.startActivity(intent);
        }
    };

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
