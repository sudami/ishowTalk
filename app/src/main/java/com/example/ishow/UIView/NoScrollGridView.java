package com.example.ishow.UIView;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ishow.R;
import com.example.ishow.UIActivity.CourseActivity;
import com.example.ishow.iShowConfig.iShowConfig;

public class NoScrollGridView extends GridView implements AdapterView.OnItemClickListener {


    public  LayoutInflater inflater;
    public String[] courseArray;//装在课程名字
    public GridAdapter adapter;
    public int[] resourceIds
            = {R.drawable.course_primary,
            R.drawable.course_middle,
            R.drawable.course_high,
            R.drawable.course_high3,
            R.drawable.course_video,
            R.drawable.course_video2,
            R.drawable.course_morning,
            R.drawable.course_teacher,
            R.drawable.course_other};//装在图片资源id
    private Context context;

    public NoScrollGridView(Context context) {

        super(context);


    }

    public NoScrollGridView(Context context, AttributeSet attrs) {

        super(context, attrs);


    }

    public NoScrollGridView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int h = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, h);

       // initData(context);
    }

    /**
     * 初始化gridview的数据
     */
    public void initData( Context context) {
        this.context = context;
        courseArray = getResources().getStringArray(R.array.course_name_array);
        adapter = new GridAdapter(context);
        this.setAdapter(adapter);
        this.setOnItemClickListener(this);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent =new Intent(context,CourseActivity.class);
        switch (position){
            case 0:

                intent.putExtra("courseId", iShowConfig.COURSEID_BY_PRIMARY);
                intent.putExtra("courseName",getResources().getString(R.string.course_name_primary));
                break;
            case 1:
                intent.putExtra("courseName",getResources().getString(R.string.course_name_middle));
                intent.putExtra("courseId", iShowConfig.COURSEID_BY_MIDDLE);
                break;
            case 2:
                intent.putExtra("courseName",getResources().getString(R.string.course_name_high2));
                intent.putExtra("courseId", iShowConfig.COURSEID_BY_HIGH);
                break;
            case 3:
                intent.putExtra("courseName",getResources().getString(R.string.course_name_high3));
                intent.putExtra("courseId", iShowConfig.COURSEID_BY_HIGH20);
                break;
            case 4:
                intent.putExtra("courseName",getResources().getString(R.string.course_name_video));
                intent.putExtra("courseId", iShowConfig.COURSEID_BY_VIDEO);
                break;
            case 5:
                intent.putExtra("courseName",getResources().getString(R.string.course_name_video2));
                intent.putExtra("courseId", iShowConfig.COURSEID_BY_VIDEO20);
                break;
            case 6:
                intent.putExtra("courseName",getResources().getString(R.string.course_name_morning));
                intent.putExtra("courseId", iShowConfig.COURSEID_BY_MORNING);
                break;
            case 7:
                intent.putExtra("courseName",getResources().getString(R.string.course_name_teacher));
                intent.putExtra("courseId", iShowConfig.COURSEID_BY_TRANNING);
                break;
            case 8:
                intent.putExtra("courseName",getResources().getString(R.string.course_name_other));
                intent.putExtra("courseId", iShowConfig.COURSEID_BY_OTHER);
                break;
        }

        NoScrollGridViewHolder holder = (NoScrollGridViewHolder)view.getTag();
        View shareEmlemt=holder.courseName;
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, shareEmlemt, "transitionName");
        ActivityCompat.startActivity((Activity)context,intent,options.toBundle());
    }
    class GridAdapter extends  BaseAdapter{
        private Context context;

        public GridAdapter(Context context){

            this.context = context;
        }
        public int getCount() {
            return courseArray.length;
        }

        @Override
        public Object getItem(int position) {
            return courseArray[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NoScrollGridViewHolder holder=null;
            if (convertView==null){
                inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.fragment_course_griditem,null);
                holder = new NoScrollGridViewHolder();
                holder.courseID = (ImageView) convertView.findViewById(R.id.fragment_course_image);
                holder.courseName = (TextView) convertView.findViewById(R.id.fragment_course_name);
                holder.lineV = (TextView)convertView.findViewById(R.id.fragment_course_split_line_v);
                holder.lineH = (TextView)convertView.findViewById(R.id.fragment_course_split_line_h);

                convertView.setTag(holder);
            }else{
                holder = (NoScrollGridViewHolder) convertView.getTag();
            }
            holder.courseID.setImageResource(resourceIds[position]);
            holder.courseName.setText(courseArray[position]);
            if (position%3==2){
                holder.lineV.setVisibility(View.GONE);
            }else{
                holder.lineV.setVisibility(View.VISIBLE);
            }

            if (position>=6){
                holder.lineH.setVisibility(View.GONE);
            }else{
                holder.lineH.setVisibility(View.VISIBLE);
            }
            return convertView;
        }

    }
    public class NoScrollGridViewHolder{
        public ImageView courseID;
        public TextView courseName;
        public TextView lineV;
        public TextView lineH;
    }


}


