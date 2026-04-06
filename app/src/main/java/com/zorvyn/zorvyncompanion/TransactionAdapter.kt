package com.zorvyn.zorvyncompanion

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zorvyn.zorvyncompanion.databinding.TransactionItemBinding
import java.text.NumberFormat
import java.util.Locale

class TransactionAdapter(
    private val onTransactionClick: (Transaction) -> Unit,
    private val onTransactionLongClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = TransactionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TransactionViewHolder(private val binding: TransactionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            binding.tvCategory.text = transaction.category
            binding.tvDate.text = transaction.date
            
            val formatter = NumberFormat.getCurrencyInstance(Locale.US)
            val amount = transaction.amount
            
            if (transaction.type == TransactionType.INCOME) {
                binding.tvAmount.text = "+${formatter.format(amount)}"
                binding.tvAmount.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorIncome))
                binding.ivCategory.setImageResource(R.drawable.ic_income)
            } else {
                binding.tvAmount.text = "-${formatter.format(amount)}"
                binding.tvAmount.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorExpense))
                binding.ivCategory.setImageResource(R.drawable.ic_expense)
            }

            itemView.setOnClickListener { onTransactionClick(transaction) }
            itemView.setOnLongClickListener {
                onTransactionLongClick(transaction)
                true
            }
        }
    }

    class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction) = oldItem == newItem
    }
}
