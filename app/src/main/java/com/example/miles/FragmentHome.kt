package com.example.miles

import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.miles.data.Item
import com.example.miles.data.ItemListAdapter
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentHome.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentHome : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val viewModel: dataBaseViewModel by activityViewModels {
        dataBaseViewModelFactory(
            (activity?.application as LocationApp).database.itemDao()
        )
    }
    private val viewModelDB: dataBaseViewModel by activityViewModels {
        dataBaseViewModelFactory(
            (activity?.application as LocationApp).database.itemDao()
        )
    }
    var _item: Item? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ItemListAdapter { intent ->
            if (activity?.packageManager?.resolveActivity(intent, 0) != null) {
                startActivity(intent)
            }
        }
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = adapter

        //implementing swipe to delete in android kotlin
        val itemTouchHelper =
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    TODO("Not yet implemented")
                }

                override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                    val position: Int = viewHolder.adapterPosition
                    val itemId: Int =
                        viewHolder.itemView.findViewById<TextView>(R.id.textView8).text.toString()
                            .toInt()


                    when (direction) {
                        ItemTouchHelper.RIGHT -> {
                            viewModelDB.findItemById(itemId).observeOnce(viewLifecycleOwner) { item ->
                                if (item != null) {
                                    _item = item
                                    viewModelDB.deleteItem(item)
                                    Snackbar.make(
                                        recyclerView,
                                        "Record Deleted",
                                        Snackbar.LENGTH_SHORT
                                    )
                                        .setAction("Undo", View.OnClickListener {
                                            _item.let {
                                                viewModelDB.undoDelete(_item!!)
                                                _item = null
                                            }
                                        }).show()
                                }
                            }
                        }
                    }
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    RecyclerViewSwipeDecorator.Builder(
                        requireActivity(),
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    ).addBackgroundColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.red
                        )
                    )
                        .addActionIcon(R.drawable.ic_baseline_delete_24)
                        .create()
                        .decorate()
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            })

        itemTouchHelper.attachToRecyclerView(recyclerView)

        recyclerView.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        )
        val TAG = "fragmentHome"
        Log.v(TAG, "Verbose  message")


        viewModel.allItems.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }

    }

    private fun todeleteItem(item: Item) {
        viewModel.deleteItem(item)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentHome.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentHome().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

private fun <T> LiveData<T>.observeOnce(viewLifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(viewLifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}
