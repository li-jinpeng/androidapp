package com.example.androidapp.fragment.chat;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.androidapp.R;
import com.example.androidapp.activity.ChatActivity;
import com.example.androidapp.entity.chat.Dialog;
import com.example.androidapp.entity.chat.Message;
import com.example.androidapp.entity.chat.User;
import com.example.androidapp.repository.ChatHistory;
import com.example.androidapp.util.BasicInfo;
import com.example.androidapp.util.DateUtil3;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;

import java.security.SecureRandom;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 聊天列表界面（其实就是主界面聊天子页，不过分成两个fragment）
 */
public class ChatFragment extends Fragment implements DateFormatter.Formatter {


    private DialogsList dialogsList;
    private DialogsListAdapter dialogsAdapter;
    private ImageLoader imageLoader;
    private int currentMessageId;
    private int messageId;
    private List<Integer> messageIdList;
    private String userAccount;
    private ArrayList<Dialog> dialogs;
    private ArrayList<ChatHistory> tmpChatHistoryList;
    private Unbinder unbinder;
/*
    private static Handler mHandler = new Handler(Looper.getMainLooper());

    private Runnable mTimeCounterRunnable = new Runnable() {
        @Override
        public void run() {
           // refresh();
            mHandler.postDelayed(this, 2 * 1000);
        }
    };

    BroadcastReceiver myBroadcastReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           // refresh();
        }
    };
*/
    public ChatFragment() { }


    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chat, container, false);

        unbinder = ButterKnife.bind(this, root);

        dialogsList = root.findViewById(R.id.dialogsList);

        imageLoader = (imageView, url, payload) -> {
            Picasso.get().load(url).placeholder(R.drawable.ic_avatarholder).into(imageView);
        };

        tmpChatHistoryList = new ArrayList<>();
        //userAccount = BasicInfo.ACCOUNT;

        dialogsAdapter = new DialogsListAdapter<>(imageLoader);
        //dialogs = new ArrayList<>();

        dialogsAdapter.setItems(getDialogs());

        dialogsAdapter.setDatesFormatter(this);


        dialogsAdapter.setOnDialogClickListener(new DialogsListAdapter.OnDialogClickListener() {
            @Override
            public void onDialogClick(IDialog dialog) {

                /*User contact = (User) dialog.getUsers().get(0);
                ((Dialog) dialog).getLastMessage().setRead();
                BasicInfo.subFromBadgeChat(dialog.getUnreadCount());

                ArrayList<Message> msgs = BasicInfo.CHAT_HISTORY.get(contact.getAccount());
                for (Message m : msgs) {
                    m.setRead();
                }*/

                //String s = contact.getName();
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                /*intent.putExtra("user", userAccount);
                intent.putExtra("contact", contact.getAccount());
                intent.putExtra("real_name", contact.getName());
                intent.putExtra("contact_id", contact.getUserId());
                intent.putExtra("contact_type", contact.getType());*/
                startActivity(intent);
            }
        });
        dialogsList.setAdapter(dialogsAdapter);
        return root;
    }


    @Override
    public void onStart() {
        super.onStart();
        //refresh();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        //getActivity().unregisterReceiver(myBroadcastReceive);
    }

    @Override
    public String format(Date date) {
        try {
            String s = DateUtil3.formatDate(date);
            return s;
        } catch (ParseException e) {
            return "";
        }
    }

//硬编码聊天内容
    public static ArrayList<Dialog> getDialogs() {
        ArrayList<Dialog> chats = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -(i * i));
            calendar.add(Calendar.MINUTE, -(i * i));

            chats.add(getDialog(i, calendar.getTime()));
        }

        return chats;
    }

    private static Dialog getDialog(int i, Date lastMessageCreatedAt) {
        ArrayList<User> users = getUsers();
        return new Dialog(
                getRandomId(),
                users.size() > 1 ? groupChatTitles.get(users.size() - 2) : users.get(0).getName(),
                users.size() > 1 ? groupChatImages.get(users.size() - 2) : getRandomAvatar(),
                users,
                getMessage(lastMessageCreatedAt),
                i < 3 ? 3 - i : 0);
    }

    private static ArrayList<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();
        int usersCount = 1 + rnd.nextInt(4);

        for (int i = 0; i < usersCount; i++) {
            users.add(getUser());
        }

        return users;
    }

    private static User getUser() {
        return new User(
                getRandomId(),
                getRandomName(),
                getRandomAvatar(),
                getRandomBoolean());
    }

    private static Message getMessage(final Date date) {
        return new Message(
                getRandomId(),
                getUser(),
                getRandomMessage(),
                date);
    }

    static SecureRandom rnd = new SecureRandom();

    static ArrayList<String> avatars = new ArrayList<String>() {
        {
            add("http://i.imgur.com/pv1tBmT.png");
            add("http://i.imgur.com/R3Jm1CL.png");
            add("http://i.imgur.com/ROz4Jgh.png");
            add("http://i.imgur.com/Qn9UesZ.png");
        }
    };

    static final ArrayList<String> groupChatImages = new ArrayList<String>() {
        {
            add("http://i.imgur.com/hRShCT3.png");
            add("http://i.imgur.com/zgTUcL3.png");
            add("http://i.imgur.com/mRqh5w1.png");
        }
    };

    static final ArrayList<String> groupChatTitles = new ArrayList<String>() {
        {
            add("Samuel, Michelle");
            add("Jordan, Jordan, Zoe");
            add("Julia, Angel, Kyle, Jordan");
        }
    };

    static final ArrayList<String> names = new ArrayList<String>() {
        {
            add("Samuel Reynolds");
            add("Kyle Hardman");
            add("Zoe Milton");
            add("Angel Ogden");
            add("Zoe Milton");
            add("Angelina Mackenzie");
            add("Kyle Oswald");
            add("Abigail Stevenson");
            add("Julia Goldman");
            add("Jordan Gill");
            add("Michelle Macey");
        }
    };

    static final ArrayList<String> messages = new ArrayList<String>() {
        {
            add("Hello!");
            add("This is my phone number - +1 (234) 567-89-01");
            add("Here is my e-mail - myemail@example.com");
            add("Hey! Check out this awesome link! www.github.com");
            add("Hello! No problem. I can today at 2 pm. And after we can go to the office.");
            add("At first, for some time, I was not able to answer him one word");
            add("At length one of them called out in a clear, polite, smooth dialect, not unlike in sound to the Italian");
            add("By the bye, Bob, said Hopkins");
            add("He made his passenger captain of one, with four of the men; and himself, his mate, and five more, went in the other; and they contrived their business very well, for they came up to the ship about midnight.");
            add("So saying he unbuckled his baldric with the bugle");
            add("Just then her head struck against the roof of the hall: in fact she was now more than nine feet high, and she at once took up the little golden key and hurried off to the garden door.");
        }
    };

    static final ArrayList<String> images = new ArrayList<String>() {
        {
            add("https://habrastorage.org/getpro/habr/post_images/e4b/067/b17/e4b067b17a3e414083f7420351db272b.jpg");
            add("https://cdn.pixabay.com/photo/2017/12/25/17/48/waters-3038803_1280.jpg");
        }
    };

    static String getRandomId() {
        return Long.toString(UUID.randomUUID().getLeastSignificantBits());
    }

    static String getRandomAvatar() {
        return avatars.get(rnd.nextInt(avatars.size()));
    }

    static String getRandomGroupChatImage() {
        return groupChatImages.get(rnd.nextInt(groupChatImages.size()));
    }

    static String getRandomGroupChatTitle() {
        return groupChatTitles.get(rnd.nextInt(groupChatTitles.size()));
    }

    static String getRandomName() {
        return names.get(rnd.nextInt(names.size()));
    }

    static String getRandomMessage() {
        return messages.get(rnd.nextInt(messages.size()));
    }

    static String getRandomImage() {
        return images.get(rnd.nextInt(images.size()));
    }

    static boolean getRandomBoolean() {
        return rnd.nextBoolean();
    }
}

