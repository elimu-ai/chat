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

    static class ViewHolder {
        ImageView imageViewAvatar;
        TextView textViewListItem;
    }

    public MessageListArrayAdapter(Context context, List<Message> messages) {
        super(context, R.layout.activity_chat_list_item, messages);
        Log.i(getClass().getName(), "MessageListArrayAdapter");

        this.context = context;
        this.messages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i(getClass().getName(), "getView");

        View listItem = convertView;
        if (listItem == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItem = layoutInflater.inflate(R.layout.activity_chat_list_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.imageViewAvatar = (ImageView) listItem.findViewById(R.id.imageViewAvatar);
            viewHolder.textViewListItem = (TextView) listItem.findViewById(R.id.textViewListItem);
            listItem.setTag(viewHolder);
        }

        Message message = messages.get(position);

        ViewHolder viewHolder = (ViewHolder) listItem.getTag();
        viewHolder.textViewListItem.setText(message.getText());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String studentAvatar = sharedPreferences.getString(StudentUpdateReceiver.PREF_STUDENT_AVATAR, null);
        if (!TextUtils.isEmpty(studentAvatar)) {
            File file = new File(studentAvatar);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                viewHolder.imageViewAvatar.setImageBitmap(bitmap);
            }
        }

        return listItem;
    }
}
