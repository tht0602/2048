package tzu.hsuan.tseng.tooowlforate

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import tzu.hsuan.tseng.tooowlforate.databinding.ActivityMainBinding
import tzu.hsuan.tseng.tooowlforate.databinding.ListItemActivityBinding


class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ActivityAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }
        initView()
    }

    private fun initView() {
        adapter = ActivityAdapter(getActivityList())
        binding.rvActivity.adapter = adapter
        //TODO 沒反應
    }

    private fun getActivityList(): Array<ActivityInfo> {
        val pm = this.packageManager
        val info =
            pm.getPackageInfo(applicationContext.packageName, PackageManager.GET_ACTIVITIES)
        return info.activities
    }

    class ActivityAdapter(private val itemList: Array<ActivityInfo>) :
        RecyclerView.Adapter<ActivityAdapter.RecyclerViewHolder>() {

        class RecyclerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            private val binding = ListItemActivityBinding.bind(itemView)
            private val context: Context = itemView.context

            fun setData(activityInfo: ActivityInfo, position: Int){
                binding.tvActivity.text = activityInfo.name
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
            val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
            val rootView: View = layoutInflater.inflate(R.layout.list_item_activity, parent, false)
            return RecyclerViewHolder(rootView)
        }

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            holder.setData(itemList[position], position)
        }

        override fun getItemCount(): Int {
            return itemList.size
        }

    }
}