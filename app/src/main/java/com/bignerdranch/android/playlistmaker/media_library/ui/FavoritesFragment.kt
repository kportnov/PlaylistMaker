package com.bignerdranch.android.playlistmaker.media_library.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.android.playlistmaker.R
import com.bignerdranch.android.playlistmaker.databinding.FragmentFavoritesBinding
import com.bignerdranch.android.playlistmaker.main.ui.MainActivity
import com.bignerdranch.android.playlistmaker.media_library.presentation.FavoritesViewModel
import com.bignerdranch.android.playlistmaker.media_library.ui.models.FavoritesState
import com.bignerdranch.android.playlistmaker.search.domain.models.Track
import com.bignerdranch.android.playlistmaker.search.ui.SearchTrackAdapter
import com.bignerdranch.android.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private val viewModel: FavoritesViewModel by viewModel()
    private lateinit var adapter: SearchTrackAdapter
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SearchTrackAdapter { track ->
            (activity as MainActivity).animateBottomNavigationView()
            onTrackClickDebounce(track)
        }

        onTrackClickDebounce = debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            viewModel.addToHistory(track)
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_playerFragment)
        }

        binding.recyclerSearch.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.recyclerSearch.adapter = adapter

        viewModel.getFavorites()

        viewModel.observeLiveData().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun showEmpty(messageId: Int) {
        context?.let {
            binding.recyclerSearch.visibility = View.GONE
            binding.upperSpace.visibility = View.VISIBLE
            binding.error.apply {
                imageViewError.visibility = View.VISIBLE
                textViewMessage.visibility = View.VISIBLE
                imageViewError.setImageDrawable(
                    AppCompatResources.getDrawable(
                        it,
                        R.drawable.img_nothing_found
                    )
                )
                textViewMessage.text = getString(messageId)
            }
        }
    }

    private fun showContent(tracks: List<Track>) {

        binding.apply {
            upperSpace.visibility = View.GONE
            recyclerSearch.visibility = View.VISIBLE
            binding.error.apply {
                imageViewError.visibility = View.GONE
                textViewMessage.visibility = View.GONE
            }
        }
        adapter.trackList.clear()
        adapter.trackList.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    fun render(state: FavoritesState) {
        when (state) {
            is FavoritesState.Content -> showContent(state.tracks)
            is FavoritesState.Empty -> showEmpty(state.messageId)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavorites()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 500L

        fun newInstance() =
            FavoritesFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}