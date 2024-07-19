package com.google.mediapipe.examples.handlandmarker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(private val context: Context, private val messageList: ArrayList<Message>) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    // 뷰홀더 생성

    companion object {
        private const val VIEW_TYPE_SENDER = 1
        private const val VIEW_TYPE_RECEIVER = 2
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return if (message.senderName == "You") {
            VIEW_TYPE_SENDER
        } else {
            VIEW_TYPE_RECEIVER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = when (viewType) {
            VIEW_TYPE_SENDER -> LayoutInflater.from(context).inflate(R.layout.item_message_sender, parent, false)
            VIEW_TYPE_RECEIVER -> LayoutInflater.from(context).inflate(R.layout.item_message, parent, false)
            else -> throw IllegalArgumentException("Invalid view type")
        }
        return ViewHolder(view)
    }

    // 뷰홀더에 데이터 바인딩
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messageList[position]
        holder.bind(message)
    }

    // 아이템 개수 반환
    override fun getItemCount(): Int {
        return messageList.size
    }

    // 뷰홀더 내부의 뷰 선언
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.message_tv)
        private val senderTextView: TextView = itemView.findViewById(R.id.name_tv)
        private val timestampTextView: TextView = itemView.findViewById(R.id.time_tv)

        // 뷰와 데이터 바인딩
        fun bind(message: Message) {
            messageTextView.text = message.message
            senderTextView.text = message.senderName
            timestampTextView.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))
        }
    }
}