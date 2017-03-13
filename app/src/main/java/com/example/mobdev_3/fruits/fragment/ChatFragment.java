package com.example.mobdev_3.fruits.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mobdev_3.fruits.R;
import com.example.mobdev_3.fruits.service.chat.model.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import static android.view.LayoutInflater.from;

public final class ChatFragment extends Fragment {

    public static final String FRAGMENT_TAG_CHAT = "fragment_tag_chat";

    public static Fragment newInstance() {
        final Bundle arguments = new Bundle();

        final Fragment fragment = new ChatFragment();
        fragment.setArguments(arguments);

        return fragment;
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
                mChat.send(gson.toJson(newMessage(message)));
                mEtInputMessage.setText("");
            }
        }

    };

    private Gson gson = new GsonBuilder().create();

    private Message newMessage(String message) {
        return new Message(mUserName, new Date().toString(), message);
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
                    addMessage(newMessage("Соединение установлено"));

                    mChat.send(gson.toJson(newMessage("Пользователь " + mUserName + " присоединился к чату")));
                }
            });
        }

        @Override
        public void onMessage(WebSocket ws, final String message) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    addMessage(gson.fromJson(message, Message.class));
                }

            });
        }

        @Override
        public void onClosed(WebSocket webSocket, final int code, final String reason) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    addMessage(newMessage("Соединение разорвано\nкод :" + code + "\nпричина: " + reason));
                }

            });
        }

        @Override
        public void onFailure(WebSocket webSocket, final Throwable t, Response response) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {
                    addMessage(newMessage("Соединение не установлено\nпричина: " + t.getMessage()));

                    connectToChat();
                }
            }, 2000);
        }

    };

    private void addMessage(Message message) {
        mMessages.add(message);
        mMessagesAdapter.notifyDataSetChanged();

        mLvMessages.post(new Runnable() {
            @Override
            public void run() {
                mLvMessages.setSelection(mMessagesAdapter.getCount() - 1);
            }
        });
    }

    private String mUserName;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mUserName = UUID.randomUUID().toString();

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
        mChat.send(gson.toJson(newMessage("Пользователь " + mUserName + " вышел")));

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
                newHolder.tvMessage = (TextView) convertView.findViewById(R.id.message);
                newHolder.tvAuthor = (TextView) convertView.findViewById(R.id.author);
                newHolder.tvDate = (TextView) convertView.findViewById(R.id.date);

                convertView.setTag(newHolder);
            }

            final Message message = getItem(position);

            final ChatMessageViewHolder holder = (ChatMessageViewHolder) convertView.getTag();
            holder.tvMessage.setText(message.getMessage());
            holder.tvAuthor.setText(message.getAuthor());
            holder.tvDate.setText(message.getDate());

            if (message.getAuthor().equals(mUserName)) {
                holder.tvMessage.setGravity(Gravity.END);
                holder.tvAuthor.setVisibility(View.GONE);
                holder.tvDate.setGravity(Gravity.END);
            } else {
                holder.tvMessage.setGravity(Gravity.START);
                holder.tvAuthor.setGravity(Gravity.START);
                holder.tvAuthor.setVisibility(View.VISIBLE);
                holder.tvDate.setGravity(Gravity.START);
            }

            return convertView;
        }

    }

    private static class ChatMessageViewHolder {

        TextView tvAuthor;
        TextView tvDate;
        TextView tvMessage;
    }

}
