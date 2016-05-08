package com.example.ishow.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.ishow.Adapter.ConversationAdapter;
import com.example.ishow.Bean.Conversation;
import com.example.ishow.Bean.MsgEntry;
import com.example.ishow.Bean.UserEntry;
import com.example.ishow.R;
import com.example.ishow.UIActivity.ChatActivity;
import com.example.ishow.Utils.ChatManager;
import com.example.ishow.Utils.SharePrefrence;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by MRME on 2016-03-28.
 */
public class FragmentMsgList extends BaseFragment implements AdapterView.OnItemClickListener {
    @Bind(R.id.base_error)
    LinearLayout baseError;
    @Bind(R.id.listView)
    ListView listView;

    ChatManager manager;
    ConversationAdapter adapter;
    private ArrayList<Conversation> list;
    private UserEntry studentInfo;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = getView(R.layout.fragment_msglist);
        ButterKnife.bind(this, rootView);
        studentInfo = SharePrefrence.getInstance().getStudentInfo(getActivity());

        listView.setOnItemClickListener(this);
        return rootView;
    }

    public void getConversationListFromDb() {
       if(manager==null) manager = new ChatManager();
        list = (ArrayList<Conversation>) manager.getConversationsList(studentInfo.getUserid());
        if (list==null)
        {
            listView.setVisibility(View.GONE);
            baseError.setVisibility(View.VISIBLE);
        }else{
            listView.setVisibility(View.VISIBLE);
            baseError.setVisibility(View.GONE);
        }
    //   if(adapter==null){
           adapter = new ConversationAdapter(getActivity(),list,studentInfo);
           listView.setAdapter(adapter);
      // }else adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position = adapter.getCount()-1-position;
        Conversation entry = list.get(position);
        UserEntry chatEntry = new UserEntry();
        chatEntry.setUserid(entry.getFromUserid());
        chatEntry.setName(entry.getFromNick());
        chatEntry.setImg(entry.getFromImg());
        chatEntry.setMobile(entry.getFromMobile());
        Bundle bundle = new Bundle();
        bundle.putParcelable("userEntry",chatEntry);
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtras(bundle);
       // startActivity(intent);
        ActivityCompat.startActivity(getActivity(),intent, ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity()).toBundle());
    }

    @Override
    public void onResume() {
        getConversationListFromDb();
        super.onResume();
    }
}
