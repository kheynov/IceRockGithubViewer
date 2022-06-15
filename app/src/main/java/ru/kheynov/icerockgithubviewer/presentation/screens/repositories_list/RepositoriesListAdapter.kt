package ru.kheynov.icerockgithubviewer.presentation.screens.repositories_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.kheynov.icerockgithubviewer.data.entities.Repo
import ru.kheynov.icerockgithubviewer.databinding.RepositoriesListItemBinding


class RepositoriesListAdapter(private val onItemClick: (Int) -> Unit) :
    RecyclerView.Adapter<RepositoriesListAdapter.ViewHolder>() {

    var items: List<Repo> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val itemViewBinding = RepositoriesListItemBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false,
        )
        return ViewHolder(itemViewBinding)
    }

    class ViewHolder(private val itemViewBinding: RepositoriesListItemBinding) : RecyclerView
    .ViewHolder(itemViewBinding.root) {
        fun bind(repo: Repo) {
            itemViewBinding.repositoriesListItemTitle.text = repo.name

            if (repo.description.isNullOrEmpty()) itemViewBinding.repositoriesListItemDescription
                .visibility = View.GONE
            else itemViewBinding.repositoriesListItemDescription.text = repo.description

            itemViewBinding.repositoriesListItemLanguage.text = repo.language
            itemViewBinding.repositoriesListItemLanguage.setTextColor(repo.getColor())
        }
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val repoItem = items[position]
        viewHolder.bind(repoItem)
        viewHolder.itemView.setOnClickListener { onItemClick(position) }
    }

    override fun getItemCount() = items.size
}