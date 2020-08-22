package com.isaacrf.android_base_app.features.beer_master_detail.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.isaacrf.android_base_app.R
import com.isaacrf.android_base_app.features.beer_master_detail.models.Beer
import com.isaacrf.android_base_app.features.beer_master_detail.viewmodels.BeerListViewModel
import com.isaacrf.android_base_app.shared.helpers.Status
import com.isaacrf.android_base_app.shared.ui.MainActivity
import kotlinx.android.synthetic.main.beer_list.*

class BeerListFragment: Fragment() {

    /**
     * ViewModel controls business logic and data representation. A saved state factory is created
     * to provide state retain across activity life cycle
     */
    private val beerListViewModel: BeerListViewModel by activityViewModels()
    private lateinit var layoutManager: LinearLayoutManager
    private var persistingView: View? = null
    private var beerListItemViewAdapter: BeerListItemViewAdapter? = null
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var isViewTwoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        beerListViewModel.getBeers().observe(this) {
            //Observe live data changes and update UI accordingly
            beerListViewModel.getBeers().observe(this) {
                when (it.status) {
                    Status.LOADING -> {
                        Log.d("GET BEERS", "LOADING...")
                        txtError.visibility = View.GONE
                        pbRepoListLoading.visibility = View.VISIBLE
                    }
                    Status.SUCCESS -> {
                        Log.d("GET BEERS", "SUCCESS")
                        if (beerListItemViewAdapter != null) {
                            beerListItemViewAdapter?.notifyDataSetChanged()
                        } else {
                            setupRecyclerView(recycler_beerlist, it.data!!)
                        }
                        pbRepoListLoading.visibility = View.GONE
                    }
                    Status.ERROR -> {
                        Log.d("GET BEERS", "ERROR")
                        pbRepoListLoading.visibility = View.GONE
                        txtError.visibility = View.VISIBLE
                        txtError.text = it.message
                    }
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (persistingView == null) {
            persistingView = inflater.inflate(R.layout.beer_list, container, false)
        }

        if (persistingView?.findViewById<FrameLayout>(R.id.beer_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            isViewTwoPane = true
        }

        return persistingView
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, items: List<Beer>) {
        layoutManager = LinearLayoutManager(activity)
        val dividerItemDecoration = DividerItemDecoration(
            recycler_beerlist.context,
            layoutManager.orientation
        )
        recycler_beerlist.addItemDecoration(dividerItemDecoration)
        recycler_beerlist.layoutManager = layoutManager

        beerListItemViewAdapter = BeerListItemViewAdapter(
            activity as MainActivity,
            items,
            isViewTwoPane
        )

        recyclerView.adapter = beerListItemViewAdapter

    }
}