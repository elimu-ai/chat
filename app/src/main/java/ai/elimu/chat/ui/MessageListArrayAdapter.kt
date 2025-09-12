package ai.elimu.chat.ui

import ai.elimu.chat.R
import ai.elimu.chat.di.ServiceLocator
import ai.elimu.chat.model.Message
import ai.elimu.chat.util.Constants
import android.content.Context
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import java.io.File

class MessageListArrayAdapter(context: Context, messages: List<Message>) :
    ArrayAdapter<Message?>(context, R.layout.activity_chat_list_item, messages) {
    private val context: Context

    private val messages: List<Message>

    private val studentId: String? // Id of current Student using the Device

    internal class ViewHolder {
        var imageViewAvatar: ImageView? = null
        var textViewListItem: TextView? = null
    }

    init {
        Log.i(javaClass.getName(), "MessageListArrayAdapter")

        this.context = context
        this.messages = messages

        studentId = ServiceLocator.provideSharedPreference().getString(Constants.PREF_STUDENT_ID, null)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        Log.i(javaClass.getName(), "getView")

        val message = messages[position]

        var listItem: View?

        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        listItem = if (TextUtils.isEmpty(message.studentId) || message.studentId == studentId) {
            // Align message to the left of the screen
            layoutInflater.inflate(R.layout.activity_chat_list_item, parent, false)
        } else {
            // Align message to the right of the screen
            layoutInflater.inflate(R.layout.activity_chat_list_item_right, parent, false)
        }


        val viewHolder = ViewHolder()
        viewHolder.imageViewAvatar = listItem.findViewById<View?>(R.id.imageViewAvatar) as ImageView
        viewHolder.textViewListItem =
            listItem.findViewById<View?>(R.id.textViewListItem) as TextView

        viewHolder.textViewListItem!!.text = message.text

        if (!TextUtils.isEmpty(message.studentAvatar)) {
            val file = File(message.studentAvatar)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                viewHolder.imageViewAvatar!!.setImageBitmap(bitmap)
            }
        } else if ("00000000aaaaaaaa_2" == message.studentId) {
            // Penguin
            viewHolder.imageViewAvatar!!.setImageDrawable(context.getDrawable(R.drawable.penguin))
        }

        return listItem
    }
}