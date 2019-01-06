package com.sporksoft.kiddom.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import androidx.navigation.Navigation
import com.sporksoft.kiddom.R
import com.sporksoft.kiddom.ui.adapter.FeedAdapter
import com.sporksoft.kiddom.ui.listener.PaginatedScrollListener
import com.sporksoft.kiddom.web.WebServiceManager
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.fragment_feed.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.app.SearchManager
import android.app.Activity
import android.content.Context
import android.support.design.widget.Snackbar
import android.support.v7.widget.SearchView
import android.text.TextUtils
import com.sporksoft.kiddom.models.SearchResult


class FeedFragment : Fragment() {
    private val LOGTAG = FeedFragment::class.java.simpleName

    private lateinit var layout: View
    private lateinit var adapter: FeedAdapter

    private var queryStr: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        fetchFeedItems(true, queryStr)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        swipeRefreshLayout.setOnRefreshListener { fetchFeedItems(true, queryStr) }

        adapter = FeedAdapter(onClickListener = View.OnClickListener {
            Log.d(LOGTAG, "Clicked item " + it.tag)
            val item = adapter.items.get(it.tag as Int)
            val bundle: Bundle = Bundle()
            bundle.putParcelable(ARG_DESCRIPTION, item.result.detailedDescription)
            Navigation.findNavController(it).navigate(R.id.detailsFragment, bundle)
        })
        recyclerView.adapter = adapter

        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(HorizontalDividerItemDecoration.Builder(activity)
                .colorResId(R.color.grey_light)
                .sizeResId(R.dimen.divider_height)
                .marginResId(R.dimen.divider_left_margin, R.dimen.divider_right_margin).build())

        //recyclerView.addOnScrollListener(object: PaginatedScrollListener(layoutManager) {
        //    override fun onLoadMore(page: Int, totalItemsCount: Int) {
        //        // Triggered only when new data needs to be appended to the list
        //        if (totalItemsCount <= adapter.itemCount) {
        //            fetchFeedItems(page, false, queryStr)
        //        }
        //    }
        //})
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.fragment_feed, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val activity = context as Activity
        val searchManager = activity.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        val searchView = searchItem?.getActionView() as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()))
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                queryStr = query;
                fetchFeedItems(true, query)
                return false
            }

        })

        return super.onCreateOptionsMenu(menu, inflater)
    }

    fun fetchFeedItems(reset: Boolean = false, query: String = "") {
        swipeRefreshLayout.isRefreshing = true

        val request: Call<SearchResult> = WebServiceManager.instanceOf()!!.search(query)
        request.enqueue(object : Callback<SearchResult> {
            override fun onFailure(call: Call<SearchResult>?, t: Throwable?) {
                swipeRefreshLayout.isRefreshing = false
                Log.e(LOGTAG, t.toString())
                Snackbar.make(recyclerView, t.toString(), Snackbar.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<SearchResult>?, response: Response<SearchResult>?) {
                swipeRefreshLayout.isRefreshing = false
                if (response != null) {
                    val result = response.body()
                    if (response.isSuccessful && result != null && result.itemListElement != null) {
                        val items = result.itemListElement
                        if (reset) {
                            adapter.reset(items)
                        } else {
                            adapter.addPage(items)
                        }
                    } else {
                        val str = response.errorBody().toString()
                        Log.e(LOGTAG, str)
                        Snackbar.make(recyclerView, str, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        })

    }
}
