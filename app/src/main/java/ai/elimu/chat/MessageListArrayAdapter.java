package org.literacyapp.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.literacyapp.chat.model.Message;
import org.literacyapp.chat.receiver.StudentUpdateReceiver;

import java.io.File;
import java.util.List;

public class MessageListArrayAdapter extends ArrayAdapter<Message> {

    private Context context;

    private List<Message> messages;

    private String studentId; // Id of current Student using the Device

    static class ViewHolder {
        ImageView imageViewAvatar;
        TextView textViewListItem;
    }

    public MessageListArrayAdapter(Context context, List<Message> messages) {
        super(context, R.layout.activity_chat_list_item, messages);
        Log.i(getClass().getName(), "MessageListArrayAdapter");

        this.context = context;
        this.messages = messages;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        studentId = sharedPreferences.getString(StudentUpdateReceiver.PREF_STUDENT_ID, null);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i(getClass().getName(), "getView");

        Message message = messages.get(position);

        View listItem = null;

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (TextUtils.isEmpty(message.getStudentId()) || message.getStudentId().equals(studentId)) {
            // Align message to the left of the screen
            listItem = layoutInflater.inflate(R.layout.activity_chat_list_item, parent, false);
        } else {
            // Align message to the right of the screen
            listItem = layoutInflater.inflate(R.layout.activity_chat_list_item_right, parent, false);
        }


        ViewHolder viewHolder = new ViewHolder();
        viewHolder.imageViewAvatar = (ImageView) listItem.findViewById(R.id.imageViewAvatar);
        viewHolder.textViewListItem = (TextView) listItem.findViewById(R.id.textViewListItem);

        viewHolder.textViewListItem.setText(message.getText());

        if (!TextUtils.isEmpty(message.getStudentAvatar())) {
            File file = new File(message.getStudentAvatar());
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                viewHolder.imageViewAvatar.setImageBitmap(bitmap);
            }
        } else if ("00000000aaaaaaaa_2".equals(message.getStudentId())) {
            // Penguin
            viewHolder.imageViewAvatar.setImageDrawable(context.getDrawable(R.drawable.penguin));
        }

        return listItem;
    }
}
