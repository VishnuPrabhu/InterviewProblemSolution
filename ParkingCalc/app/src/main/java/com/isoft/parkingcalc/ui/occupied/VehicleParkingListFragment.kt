package com.isoft.parkingcalc.ui.occupied

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.isoft.parkingcalc.R
import androidx.recyclerview.widget.GridLayoutManager
import com.isoft.parkingcalc.ui.main.MainViewModel
import kotlinx.android.synthetic.main.fragment_parking_list.*

class VehicleParkingListFragment : Fragment() {

    companion object {
        fun newInstance() = VehicleParkingListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_parking_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = ViewModelProviders.of(this).get(VehicleParkingListViewModel::class.java)


        val sections = viewModel.getAllParkedVehicles()
        val viewManager = LinearLayoutManager(this.activity!!)
        val adapter = SectionAdapter(this.activity!!, sections)
        parking_list_view.apply {
            setHasFixedSize(true)
            // use a linear layout manager
            layoutManager = viewManager
            setAdapter(adapter)
        }
    }

}

class Section(var sectionTitle: String?, var allItemsInSection: MutableList<String>)

class ItemAdapter(internal var items: List<String>) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemAdapter.ViewHolder, position: Int) {
        val itemName = items[position]
        holder.bind(itemName)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemName: TextView

        init {
            itemName = itemView.findViewById(R.id.list_item_text_view)
        }

        fun bind(item: String) {
            itemName.text = item
        }
    }
}

class SectionAdapter(private val context: Context, private val sectionList: List<Section>) :
    RecyclerView.Adapter<SectionAdapter.ViewHolder>() {
    private var itemAdapter: ItemAdapter? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_section, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val section = sectionList[position]
        holder.bind(section)
    }

    override fun getItemCount(): Int {
        return sectionList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sectionName: TextView
        private val itemRecyclerView: RecyclerView

        init {
            sectionName = itemView.findViewById(R.id.section_item_text_view)
            itemRecyclerView = itemView.findViewById(R.id.item_recycler_view)
        }

        fun bind(section: Section) {
            sectionName.text = section.sectionTitle
            val gridLayoutManager = GridLayoutManager(context, 4)
            itemRecyclerView.layoutManager = gridLayoutManager
            itemAdapter = ItemAdapter(section.allItemsInSection!!)
            itemRecyclerView.adapter = itemAdapter
        }
    }

    fun moveItem(toSectionPosition: Int, fromSectionPosition: Int) {
        val toItemsInSection = sectionList[toSectionPosition].allItemsInSection
        val fromItemsInSection = sectionList[fromSectionPosition].allItemsInSection
        if (fromItemsInSection!!.size > 3) {
            toItemsInSection!!.add(fromItemsInSection[3])
            fromItemsInSection.removeAt(3)
            // update adapter for items in a section
            itemAdapter!!.notifyDataSetChanged()
            // update adapter for sections
            this.notifyDataSetChanged()
        }
    }
}
