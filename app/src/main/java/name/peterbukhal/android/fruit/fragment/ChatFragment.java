package name.peterbukhal.android.fruit.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import name.peterbukhal.android.fruit.R;
import name.peterbukhal.android.fruit.service.chat.model.Message;
import name.peterbukhal.android.fruit.widget.CircleTransform;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.m_chat, menu);
    }

    private void openProfile() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_content,
                        ProfileFragment.newInstance(),
                        ProfileFragment.FRAGMENT_TAG_PROFILE)
                .addToBackStack(ProfileFragment.FRAGMENT_TAG_PROFILE)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile: {
                openProfile();
            }
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    private RecyclerView mRvMessages;
    private EditText mEtInputMessage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View contentView = inflater.inflate(R.layout.f_chat, container, false);

        if (contentView != null) {
            mRvMessages = (RecyclerView) contentView.findViewById(R.id.messages);
            mRvMessages.setLayoutManager(new LinearLayoutManager(getActivity()));

            mEtInputMessage = (EditText) contentView.findViewById(R.id.inputMessage);

            ImageButton btSendMessage = (ImageButton) contentView.findViewById(R.id.submitMessage);
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
        return new Message(mNickname, message);
    }

    private List<Message> mMessages = new ArrayList<>();
    private MessagesAdapter mMessagesAdapter = new MessagesAdapter();

    private WebSocket mChat;
    private WebSocketListener mChatListener = new WebSocketListener() {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    final Message newMessage = newMessage(mNickname + " присоединился");

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

                    if (!msg1.getMessage().startsWith(mNickname)) {
                        addMessage(msg1);
                        confirmMessageRead(msg1);
                    }
                }

            });
        }

        @Override
        public void onClosed(WebSocket webSocket, final int code, final String reason) {
        }

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
            if (message.getStatus() == 0) {
                mChat.send(gson.toJson(message));
            }
        }
    }

    private void confirmMessageRead(Message message) {
        if (message.getStatus() < 2 && !message.getAuthor().equals(mNickname)) {
            message.setStatus(2);
            mChat.send(gson.toJson(message));
        }
    }

    private void addMessage(final Message message) {
        if (mMessages.contains(message)) {
            final int index = mMessages.indexOf(message);

            mMessages.set(index, message);
            mMessagesAdapter.notifyItemChanged(index);
        } else {
            mMessages.add(message);
            mMessagesAdapter.notifyItemInserted(mMessages.size() - 1);

            mRvMessages.scrollToPosition(mMessagesAdapter.getItemCount() - 1);
        }
    }

    private String mNickname;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRvMessages.setAdapter(mMessagesAdapter);

        mNickname = getActivity()
                .getSharedPreferences("profile", Context.MODE_PRIVATE)
                .getString("profile_nickname", "noname");

        connectToChat();
    }

    private void connectToChat() {
        try {
            mChat = new OkHttpClient()
                    .newWebSocket(new Request.Builder()
                            .url(getString(R.string.fruit_chat_service))
                            .build(), mChatListener);
        } catch (Exception e) {
            //
        }
    }

    private void disconnectFromChat() {
        try {
            mChat.send(gson.toJson(newMessage(mNickname + " вышел")));

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {
                    mChat.close(1000, "");
                }

            }, 1500);
        } catch (Exception e) {
            //
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        disconnectFromChat();
    }

    private class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        static final int TYPE_OUTGOING_MESSAGE = 0;
        static final int TYPE_INCOMING_MESSAGE = 1;
        static final int TYPE_SERVICE_MESSAGE = 2;

        @Override
        public int getItemViewType(int position) {
            if (mMessages.get(position).getMessage().endsWith("присоединился") ||
                    mMessages.get(position).getMessage().endsWith("вышел")) {
                return TYPE_SERVICE_MESSAGE;
            }

            return (mMessages.get(position).getAuthor().equals(mNickname)) ?
                    TYPE_OUTGOING_MESSAGE : TYPE_INCOMING_MESSAGE;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case TYPE_OUTGOING_MESSAGE: {
                    return new OutgoingMessageViewHolder(from(getActivity())
                            .inflate(R.layout.l_message_outgoing, parent, false));
                }
                case TYPE_INCOMING_MESSAGE: {
                    return new IncomingMessageViewHolder(from(getActivity())
                            .inflate(R.layout.l_message_incoming, parent, false));
                }
                case TYPE_SERVICE_MESSAGE: {
                    return new ServiceMessageViewHolder(from(getActivity())
                            .inflate(R.layout.l_message_service, parent, false));
                }
                default: {
                    throw new RuntimeException();
                }
            }
        }

        final SimpleDateFormat F = new SimpleDateFormat("HH:mm", Locale.getDefault());

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final Message message = mMessages.get(position);

            switch (holder.getItemViewType()) {
                case TYPE_OUTGOING_MESSAGE: {
                    final OutgoingMessageViewHolder outgoingHolder = (OutgoingMessageViewHolder) holder;

                    outgoingHolder.tvMessage.setText(message.getMessage());
                    outgoingHolder.tvDate.setText(F.format(new Date(message.getCreatedAt())));
                    outgoingHolder.tvAuthor.setText(R.string.you);

                    switch (message.getStatus()) {
                        case 0: {
                            outgoingHolder.tvStatus.setImageResource(R.drawable.ic_message_not_sent);
                        }
                        break;
                        case 1: {
                            outgoingHolder.tvStatus.setImageResource(R.drawable.ic_message_sent);
                        }
                        break;
                        case 2: {
                            outgoingHolder.tvStatus.setImageResource(R.drawable.ic_message_read);
                        }
                        break;
                    }
                }
                break;
                case TYPE_INCOMING_MESSAGE: {
                    final IncomingMessageViewHolder incomingHolder = (IncomingMessageViewHolder) holder;

                    Picasso.with(getActivity())
                            .load("asdasdasdasd"/*getString(R.string.fruit_default_avatar)*/)
                            .error(R.drawable.ic_profile)
                            .transform(new CircleTransform())
                            .into(incomingHolder.ivAvatar);

                    incomingHolder.tvMessage.setText(message.getMessage());
                    incomingHolder.tvDate.setText(F.format(new Date(message.getCreatedAt())));
                    incomingHolder.tvAuthor.setText(message.getAuthor());
                }
                break;
                case TYPE_SERVICE_MESSAGE: {
                    final ServiceMessageViewHolder serviceHolder = (ServiceMessageViewHolder) holder;

                    serviceHolder.tvMessage.setText(
                            F.format(new Date(message.getCreatedAt())) +
                                    " " + message.getMessage());
                }
                break;
            }
        }

        @Override
        public int getItemCount() {
            return mMessages.size();
        }

    }

    private static class IncomingMessageViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAvatar;
        TextView tvAuthor;
        TextView tvDate;
        TextView tvMessage;

        IncomingMessageViewHolder(View itemView) {
            super(itemView);

            ivAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            tvMessage = (TextView) itemView.findViewById(R.id.message);
            tvAuthor = (TextView) itemView.findViewById(R.id.author);
            tvDate = (TextView) itemView.findViewById(R.id.date);
        }

    }

    private static class OutgoingMessageViewHolder extends RecyclerView.ViewHolder {

        TextView tvAuthor;
        TextView tvDate;
        TextView tvMessage;
        ImageView tvStatus;

        OutgoingMessageViewHolder(View itemView) {
            super(itemView);

            tvMessage = (TextView) itemView.findViewById(R.id.message);
            tvAuthor = (TextView) itemView.findViewById(R.id.author);
            tvDate = (TextView) itemView.findViewById(R.id.date);
            tvStatus = (ImageView) itemView.findViewById(R.id.status);
        }

    }

    private static class ServiceMessageViewHolder extends RecyclerView.ViewHolder {

        TextView tvMessage;

        ServiceMessageViewHolder(View itemView) {
            super(itemView);

            tvMessage = (TextView) itemView.findViewById(R.id.message);
        }

    }

}