package com.zorvyn.zorvyncompanion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zorvyn.zorvyncompanion.databinding.TransactionItemBinding
import com.zorvyn.zorvyncompanion.databinding.TransactionHeaderItemBinding
import java.text.NumberFormat
import java.util.Locale

sealed class TransactionListItem {
    data class Header(val date: String) : TransactionListItem()
    data class TransactionItem(val transaction: Transaction) : TransactionListItem()
}

class TransactionAdapter(
    private val onTransactionClick: (Transaction) -> Unit,
    private val onTransactionLongClick: (Transaction) -> Unit
) : ListAdapter<TransactionListItem, RecyclerView.ViewHolder>(TransactionDiffCallback()) {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_TRANSACTION = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TransactionListItem.Header -> TYPE_HEADER
            is TransactionListItem.TransactionItem -> TYPE_TRANSACTION
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> {
                val binding = TransactionHeaderItemBinding.inflate(inflater, parent, false)
                HeaderViewHolder(binding)
            }
            else -> {
                val binding = TransactionItemBinding.inflate(inflater, parent, false)
                TransactionViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is TransactionListItem.Header -> (holder as HeaderViewHolder).bind(item.date)
            is TransactionListItem.TransactionItem -> (holder as TransactionViewHolder).bind(item.transaction)
        }
    }

    inner class HeaderViewHolder(private val binding: TransactionHeaderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(date: String) {
            binding.tvHeaderDate.text = date.uppercase()
        }
    }

    inner class TransactionViewHolder(private val binding: TransactionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            binding.tvCategory.text = transaction.notes ?: transaction.category
            binding.tvSubCategory.text = transaction.category
            
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

    class TransactionDiffCallback : DiffUtil.ItemCallback<TransactionListItem>() {
        override fun areItemsTheSame(oldItem: TransactionListItem, newItem: TransactionListItem): Boolean {
            return if (oldItem is TransactionListItem.Header && newItem is TransactionListItem.Header) {
                oldItem.date == newItem.date
            } else if (oldItem is TransactionListItem.TransactionItem && newItem is TransactionListItem.TransactionItem) {
                oldItem.transaction.id == newItem.transaction.id
            } else {
                false
            }
        }

        override fun areContentsTheSame(oldItem: TransactionListItem, newItem: TransactionListItem): Boolean {
            return oldItem == newItem
        }
    }
}
