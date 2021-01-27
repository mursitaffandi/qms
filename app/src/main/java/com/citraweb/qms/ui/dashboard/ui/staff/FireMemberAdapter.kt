package com.citraweb.qms.ui.dashboard.ui.staff

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.citraweb.qms.data.user.User

import com.citraweb.qms.databinding.ItemMemberBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import timber.log.Timber


class FireMemberAdapter(options: FirestoreRecyclerOptions<User?>, private val callback: OnItemClick) :
    FirestoreRecyclerAdapter<User, FireMemberAdapter.UserHolder>(options) {
    override fun onBindViewHolder(holder: UserHolder, position: Int, model: User) {
        holder.binding.tvMemberName.text = model.name
        holder.binding.tvMemberNumberorder.text = model.ticket.toString()
        holder.binding.root.setOnClickListener {
            callback.click(model, snapshots.getSnapshot(position).id)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val binding = ItemMemberBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return UserHolder(binding)
    }

    override fun getItemCount(): Int {
        callback.size(super.getItemCount())
        return super.getItemCount()
    }
    override fun onError(e: FirebaseFirestoreException) {
        Timber.tag("FirestoreException").e(e);
        super.onError(e)
    }

    inner class UserHolder(val binding: ItemMemberBinding)
        :RecyclerView.ViewHolder(binding.root)

    interface OnItemClick{
        fun click(S: User, idSnapshot: String)
        fun size(itemCount: Int)
    }
}


