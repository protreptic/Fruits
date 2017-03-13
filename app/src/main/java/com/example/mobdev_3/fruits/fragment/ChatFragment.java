package com.example.mobdev_3.fruits.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mobdev_3.fruits.R;
import com.example.mobdev_3.fruits.service.chat.model.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import static android.support.v4.content.ContextCompat.getDrawable;
import static android.view.LayoutInflater.from;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public final class ChatFragment extends Fragment {

    public static final String FRAGMENT_TAG_CHAT = "fragment_tag_chat";

    public static Fragment newInstance() {
        final Bundle arguments = new Bundle();

        final Fragment fragment = new ChatFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_chat, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings: {

            } break;
        }

        return super.onOptionsItemSelected(item);
    }

    private ListView mLvMessages;
    private EditText mEtInputMessage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View contentView = inflater.inflate(R.layout.fragment_chat, container, false);

        if (contentView != null) {
            mLvMessages = (ListView) contentView.findViewById(R.id.messages);
            mLvMessages.setDivider(null);
            mLvMessages.setAdapter(mMessagesAdapter);

            mEtInputMessage = (EditText) contentView.findViewById(R.id.inputMessage);

            Button btSendMessage = (Button) contentView.findViewById(R.id.submitMessage);
            btSendMessage.setOnClickListener(mOnClickListener);
        }

        return contentView;
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            final String message = mEtInputMessage.getText().toString();

            if (!TextUtils.isEmpty(message)) {
                final Message newMessage = newMessage(message);

                addMessage(newMessage);

                mChat.send(gson.toJson(newMessage));
                mEtInputMessage.setText("");
            }
        }

    };

    private Gson gson = new GsonBuilder().create();

    private Message newMessage(final String message) {
        return new Message(mUserName, message);
    }

    private List<Message>   mMessages = new ArrayList<>();
    private MessagesAdapter mMessagesAdapter = new MessagesAdapter();

    private WebSocket mChat;
    private WebSocketListener mChatListener = new WebSocketListener() {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    final Message newMessage = newMessage(mUserName + " присоединился");

                    mChat.send(gson.toJson(newMessage));

                    sendAllPendingMessages();
                }
            });
        }

        @Override
        public void onMessage(WebSocket ws, final String message) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    final Message msg1 = gson.fromJson(message, Message.class);

                    if (!msg1.getMessage().startsWith(mUserName)) {
                        addMessage(msg1);
                        confirmMessageRead(msg1);
                    }
                }

            });
        }

        @Override
        public void onClosed(WebSocket webSocket, final int code, final String reason) {}

        @Override
        public void onFailure(WebSocket webSocket, final Throwable t, Response response) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {
                    connectToChat();
                }
            }, 5000);
        }

    };

    private void sendAllPendingMessages() {
        for (Message message : mMessages) {
            if (message.getStatus().equals("Не отправлено")) {
                mChat.send(gson.toJson(message));
            }
        }
    }

    private void confirmMessageRead(Message message) {
        if (message.getStatusRaw() < 2 && !message.getAuthor().equals(mUserName)) {
            message.setStatus(2);
            mChat.send(gson.toJson(message));
        }
    }

    private void addMessage(final Message message) {
        if (mMessages.contains(message)) {
            mMessages.set(mMessages.indexOf(message), message);
            mMessagesAdapter.notifyDataSetChanged();
        } else {
            mMessages.add(message);
            mMessagesAdapter.notifyDataSetChanged();

            mLvMessages.post(new Runnable() {
                @Override
                public void run() {
                    mLvMessages.setSelection(mMessagesAdapter.getCount() - 1);
                }
            });
        }
    }

    private Realm mRealm;
    private String mUserName;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRealm = Realm.getDefaultInstance();
        mUserName = UUID.randomUUID().toString().substring(0, 8);

        if (savedInstanceState == null) {
            connectToChat();
        }
    }

    private void connectToChat() {
        mChat = new OkHttpClient()
                .newWebSocket(new Request.Builder()
                        .url(getString(R.string.fruit_chat_service))
                        .build(), mChatListener);
    }

    private void disconnectFromChat() {
        mChat.send(gson.toJson(newMessage(mUserName + " вышел")));

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                mChat.close(1000, "");
            }

        }, 1500);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        disconnectFromChat();

        try {
            if (mRealm != null) {
                if (mRealm.isInTransaction()) {
                    mRealm.cancelTransaction();
                }

                mRealm.close();
            }
        } catch (Exception e) {
            //
        }
    }

    private class MessagesAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mMessages.size();
        }

        @Override
        public Message getItem(int position) {
            return mMessages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = from(getActivity())
                        .inflate(R.layout.layout_chat_message, parent, false);

                final ChatMessageViewHolder newHolder = new ChatMessageViewHolder();
                newHolder.background = convertView.findViewById(R.id.background);
                newHolder.tvMessage = (TextView) convertView.findViewById(R.id.message);
                newHolder.tvAuthor = (TextView) convertView.findViewById(R.id.author);
                newHolder.tvDate = (TextView) convertView.findViewById(R.id.date);
                newHolder.tvStatus = (TextView) convertView.findViewById(R.id.status);

                convertView.setTag(newHolder);
            }

            final Message message = getItem(position);

            final ChatMessageViewHolder holder = (ChatMessageViewHolder) convertView.getTag();
            holder.tvMessage.setText(message.getMessage());
            holder.tvDate.setText(message.getCreatedAt());
            holder.tvStatus.setText(message.getStatus() + "");

            final LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);

            if (message.getAuthor().equals(mUserName)) {
                layoutParams.setMargins(96, 16, 16, 16);

                holder.background.setBackground(getDrawable(getActivity(), R.drawable.bubble_white));
                holder.tvAuthor.setText(R.string.you);
                holder.tvStatus.setVisibility(View.VISIBLE);
            } else {
                layoutParams.setMargins(16, 16, 96, 16);

                holder.background.setBackground(getDrawable(getActivity(), R.drawable.bubble_yellow));
                holder.tvAuthor.setText(message.getAuthor());
                holder.tvStatus.setVisibility(View.GONE);
            }

            holder.background.setLayoutParams(layoutParams);

            return convertView;
        }

    }

    private static class ChatMessageViewHolder {

        View background;
        TextView tvAuthor;
        TextView tvDate;
        TextView tvMessage;
        TextView tvStatus;
    }

}
