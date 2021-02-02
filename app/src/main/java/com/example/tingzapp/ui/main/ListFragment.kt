package com.example.tingzapp.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.e.msappsreview.ui.main.state.MainStateEvent
import com.example.tingzapp.ui.main.viewModel.setMovieList
import com.example.tingzapp.R
import com.example.tingzapp.model.MovieModel
import com.example.tingzapp.utils.TopSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_list.*

@AndroidEntryPoint
class ListFragment : BaseMainFragment(), RvAdapter.Interaction {

    private lateinit var rVadapter: RvAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        subscribeObservers()
        initStateEvent()
        initRV()
    }

    private fun initStateEvent() {
        viewModel.setStateEvent(
            MainStateEvent.GetMainMoviesEvent()
        )
    }

    private fun initRV() {
        recycler_movies_list.apply {
            layoutManager = LinearLayoutManager(this@ListFragment.context)
            val topSpaingItemDecoration = TopSpacingItemDecoration(30)
            removeItemDecoration(topSpaingItemDecoration)
            addItemDecoration(topSpaingItemDecoration)
            rVadapter = RvAdapter(
                requestManager = requestManager,
                interaction = this@ListFragment
            )
            adapter = rVadapter

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recycler_movies_list.adapter = null
    }


    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            if (dataState != null) {
                stateChangeListener.onDataStateChange(dataState)
                dataState.data?.let {
                    it.data?.let { event ->
                        event.getContentIfNotHandled()?.let {
                            Log.d(TAG, "ListFragment: data state: $it")
                            it.listFields.movies?.let { it1 -> viewModel.setMovieList(it1) }
                        }
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            Log.d(TAG, "subscribeObservers: ViewState : $viewState")
            if (viewState != null) {
                viewState.listFields.movies?.let {
                    rVadapter.submitList(
                        list = it
                    )
                }
            }
        })
    }

    override fun onItemSelected(position: Int, item: MovieModel) {
        if (findNavController().currentDestination!!.equals(R.id.ListFragment)) {
            findNavController().navigate(R.id.action_ListFragment_to_item_fragment)
        }
    }
}