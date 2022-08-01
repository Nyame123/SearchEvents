package com.bismark.searchevents.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bismark.searchevents.data.EventJsonDataSourceImp
import com.bismark.searchevents.data.SearchEventRepositoryImpl
import com.bismark.searchevents.databinding.FragmentSearchEventBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class SearchEventFragment : Fragment() {

    private var _binding: FragmentSearchEventBinding? = null
    private val binding get() = _binding!!

    private val searchAdapter: SearchEventAdapter = SearchEventAdapter()
    private val searchEventViewModel: SearchEventViewModel by lazy {
        SearchEventViewModel(repository = SearchEventRepositoryImpl(EventJsonDataSourceImp(requireContext())))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchEventBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapter()
        getAllEvents()
        initializeClickListeners()
    }

    private fun setUpAdapter() {
        with(binding){
            recyclerView.adapter = searchAdapter
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.addItemDecoration(DividerItemDecoration(context,LinearLayoutManager.VERTICAL))
            recyclerView.itemAnimator = DefaultItemAnimator()
        }
    }

    private fun getAllEvents() {
        lifecycleScope.launch {
            searchEventViewModel.getAllEvents()
                .collectLatest {
                    searchAdapter.submitList(it)
                }
        }
    }

    private fun initializeClickListeners() {
        with(binding) {
            searchBtn.setOnClickListener {
                lifecycleScope.launch {
                    searchEventViewModel.searchButtonClick(
                        search = searchEdt.text.toString(),
                        mode = SearchMode.from(filterSpinner.selectedItem.toString())
                    ).collectLatest {
                        searchAdapter.submitList(it)
                    }
                }
            }

            lifecycleScope.launch {
                searchEventViewModel.state.collectLatest { errorState ->
                    repeatOnLifecycle(Lifecycle.State.STARTED){
                        if (errorState is SearchUIState.Error){
                            Snackbar.make(binding.root,errorState.error.localizedMessage ?: "Error",Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
