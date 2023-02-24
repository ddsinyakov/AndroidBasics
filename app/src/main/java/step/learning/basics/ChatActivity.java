package step.learning.basics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

public class ChatActivity extends AppCompatActivity {

    private final String CHAT_URL = "https://diorama-chat.ew.r.appspot.com/story";
    private String content;
    private LinearLayout chatContainer;
    private List<ChatMessage> chatMessages;
    private ChatMessage chatMessage;
    private EditText etAuthor;
    private EditText etMessage;
    private ScrollView svContainer;
    private final static String CHANNEL_ID = "chat_channel";
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatContainer = findViewById(R.id.chatContainer);
        etAuthor = findViewById(R.id.etUserName);
        etMessage = findViewById(R.id.etMessage);
        svContainer = findViewById(R.id.sv_container);

        chatMessages = new ArrayList<>();
        handler = new Handler();

        findViewById(R.id.chatButtonSend)
                .setOnClickListener(this::sendButtonClick);

        new Thread(this::updateChat).start();
        handler.postDelayed(this::createNotificationChannel, 2000);
    }

    private void updateChat() {
        new Thread(this::loadUrl).start();
        handler.postDelayed(this::updateChat, 2000);
    }

    private void loadUrl() {
        try (InputStream inputStream = new URL(CHAT_URL).openStream()) {
            StringBuilder sb = new StringBuilder();
            int sym;
            while ((sym = inputStream.read()) != -1) {
                sb.append((char) sym);
            }
            content = new String(
                    sb.toString().getBytes(StandardCharsets.ISO_8859_1),
                    StandardCharsets.UTF_8);

            new Thread(this::parseContent).start();
            // runOnUiThread( this::showChatMessages ) ;
        } catch (android.os.NetworkOnMainThreadException ex) {
            Log.d("loadUrl", "NetworkOnMainThreadException: " + ex.getMessage());
        } catch (MalformedURLException ex) {
            Log.d("loadUrl", "MalformedURLException: " + ex.getMessage());
        } catch (IOException ex) {
            Log.d("loadUrl", "IOException: " + ex.getMessage());
        }
    }

    private void parseContent() {
        try {
            JSONObject js = new JSONObject(content);
            JSONArray jMessages = js.getJSONArray("data");
            if ("success".equals(js.get("status"))) {
                for (int i = 0; i < jMessages.length(); ++i) {
                    ChatMessage tmp = new ChatMessage(jMessages.getJSONObject(i));
                    if (chatMessages.stream().noneMatch(
                            obj -> obj.getId() == tmp.getId())) {
                        chatMessages.add(tmp);
                    }
                }
                chatMessages.sort(Comparator.comparing(ChatMessage::getMoment));
                runOnUiThread(this::showChatMessages);
            } else {
                Log.d("parseContent",
                        "Server responses status: " + js.getString("status"));
            }
        } catch (JSONException ex) {
            Log.d("parseContent", ex.getMessage());
        }
    }

    private void showChatMessages() {

        Drawable otherBg = AppCompatResources.getDrawable(
                getApplicationContext(), R.drawable.rates_bg_even);

        Drawable myBg = AppCompatResources.getDrawable(
                getApplicationContext(), R.drawable.rates_bg_odd );

        LinearLayout.LayoutParams otherLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT );
        otherLayoutParams.setMargins(7,5,7,5);

        LinearLayout.LayoutParams myLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT );
        myLayoutParams.setMargins(7,5,7,5);
        myLayoutParams.gravity = Gravity.END;


        boolean needScrollDown = false;
        String author = etAuthor.getText().toString();

        for (ChatMessage chatMessage : this.chatMessages) {
            if (chatMessage.getView() != null)
                continue;

            needScrollDown = true;
            TextView tv = new TextView(this);
            String msg = chatMessage.getMoment() + ": " + chatMessage.getAuthor() + "\n" +
                    chatMessage.getTxt();
            tv.setText(msg);
            if (chatMessage.getAuthor().equals(author)) {
                tv.setBackground(myBg);
                tv.setLayoutParams(myLayoutParams);
            } else {
                tv.setBackground(otherBg);
                tv.setLayoutParams(otherLayoutParams);
            }

            chatContainer.addView(tv);
            chatMessage.setView(tv);
        }

        if (needScrollDown){
            svContainer.post(() -> svContainer.fullScroll(View.FOCUS_DOWN));
        }
    }

    private void sendButtonClick(View view) {
        String message = etMessage.getText().toString();
        String author = etAuthor.getText().toString();

        if (message.isEmpty()) {
            Toast.makeText(ChatActivity.this, "Message is empty", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (author.isEmpty()) {
            Toast.makeText(ChatActivity.this, "Author is empty", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        this.chatMessage = new ChatMessage();
        chatMessage.setAuthor(author);
        chatMessage.setTxt(message);
        new Thread(this::postChatMessage).start();
    }

    private void postChatMessage() {
        try {
            URL url = new URL(CHAT_URL);

            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "*/*");
            urlConnection.setChunkedStreamingMode(0);

            OutputStream body = urlConnection.getOutputStream();
            body.write(String.format("{\"author\":\"%s\", \"txt\":\"%s\"}",
                chatMessage.getAuthor(),
                chatMessage.getTxt())
                .getBytes());
            body.flush();
            body.close();

            int responseCode = urlConnection.getResponseCode();
            if (responseCode != 200) {
                Log.d("postChatMessage", "Response code: " + responseCode);
                return;
            }

            InputStream reader = urlConnection.getInputStream();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            byte[] chunk = new byte[4096];
            int len;
            while ((len = reader.read(chunk)) != -1) {
                bytes.write(chunk, 0, len);
            }

            Log.d("postChatMessage",
                    new String(bytes.toByteArray(), StandardCharsets.UTF_8));

            bytes.close();
            reader.close();

            etMessage.setText("");

            loadUrl();
        } catch (Exception ex) {
            Log.d("postChatMessage", ex.getMessage());
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.sym_def_app_icon)
                        .setContentTitle("Chat")
                        .setContentText("Message from chat")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Notification notification = builder.build();
        NotificationManagerCompat notificationManager =
            NotificationManagerCompat.from(ChatActivity.this);
        notificationManager.notify(1001, notification);
    }

    private static class ChatMessage {

        private View view;

        public View getView() { return view; }

        public void setView(View view) { this.view = view; }

        private UUID id;
        private String author;
        private String txt;
        private Date moment;
        private UUID idReply;
        private String replyPreview;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getTxt() {
            return txt;
        }

        public void setTxt(String txt) {
            this.txt = txt;
        }

        public Date getMoment() {
            return moment;
        }

        public void setMoment(Date moment) {
            this.moment = moment;
        }

        public UUID getIdReply() {
            return idReply;
        }

        public void setIdReply(UUID idReply) {
            this.idReply = idReply;
        }

        public String getReplyPreview() {
            return replyPreview;
        }

        public void setReplyPreview(String replyPreview) {
            this.replyPreview = replyPreview;
        }

        private static final SimpleDateFormat dateFormat =
                new SimpleDateFormat("MMM dd, yyyy KK:mm:ss a", Locale.US);

        public ChatMessage() {}

        public ChatMessage(JSONObject obj) throws JSONException {
            setId(UUID.fromString(obj.getString("id")));
            setAuthor(obj.getString("author"));
            setTxt(obj.getString("txt"));

            try {
                setMoment(dateFormat.parse(obj.getString("moment")));
            } catch (ParseException ex) {
                throw new JSONException(ex.getMessage());
            }

            if (obj.has("idReply"))
                setIdReply(UUID.fromString(obj.getString("idReply")));
            if (obj.has("replyPreview"))
                setReplyPreview(obj.getString("replyPreview"));
        }
    }
}