package ru.kheynov.icerockgithubviewer.presentation.screens.detail_info

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.core.MarkwonTheme
import ru.kheynov.icerockgithubviewer.R
import ru.kheynov.icerockgithubviewer.databinding.FragmentDetailInfoBinding

@AndroidEntryPoint
class DetailInfoFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentDetailInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDetailInfoBinding.inflate(
            inflater,
            container,
            false
        )

        binding.apply {
            forksCount.text = "30"
            starsCount.text = "3"
            watchersCount.text = "10"
            license.text = "Apache-2.0"
            link.text = "github.com/icerockdev/moko-resources"
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.log_out_button, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_action_logout -> {
                navController.navigate(R.id.action_detailFragment_to_authFragment)
                true
            }
            android.R.id.home -> {
                navController.navigate(R.id.action_detailFragment_to_repositoriesListFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "TODO"
            setHomeAsUpIndicator(R.drawable.ic_arrow_left)
            setDisplayHomeAsUpEnabled(true)
            show()
        }

        val markwon = Markwon.builder(context!!)
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureTheme(builder: MarkwonTheme.Builder) {
                    builder.codeTextColor(Color.GREEN)
                }
            }).build()


        val markdown = "# H1\n" +
                "## H2\n" +
                "### H3\n" +
                "#### H4\n" +
                "##### H5\n" +
                "###### H6\n" +
                "\n" +
                "Alternatively, for H1 and H2, an underline-ish style:\n" +
                "\n" +
                "Alt-H1\n" +
                "======\n" +
                "\n" +
                "Alt-H2\n" +
                "## H2\n" +
                "### H3\n" +
                "#### H4\n" +
                "##### H5\n" +
                "###### H6\n" +
                "\n" +
                "Alternatively, for H1 and H2, an underline-ish style:\n" +
                "\n" +
                "Alt-H1\n" +
                "======\n" +
                "\n" +
                "Alt-H2\n" +
                "## H2\n" +
                "### H3\n" +
                "#### H4\n" +
                "##### H5\n" +
                "###### H6\n" +
                "\n" +
                "Alternatively, for H1 and H2, an underline-ish style:\n" +
                "\n" +
                "Alt-H1\n" +
                "======\n" +
                "\n" +
                "Alt-H2\n" +
                "------"
        markwon.setMarkdown(binding.readmeTextView, markdown)
    }
}