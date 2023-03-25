package tzu.hsuan.tseng.tooowlforate

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tzu.hsuan.tseng.tooowlforate.databinding.ActivityMainBinding
import tzu.hsuan.tseng.tooowlforate.databinding.ListItemActivityBinding


class MainActivity : Activity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }
        initView()
    }

    private fun initView() {
        binding.rvActivity.adapter = ActivityAdapter(getActivityList())
        binding.rvActivity.layoutManager = LinearLayoutManager(this)
        binding.rvActivity.isNestedScrollingEnabled = false
        binding.rvActivity.adapter = ActivityAdapter(getActivityList())
        //TODO 沒反應
    }

    private fun getActivityList(): Array<ActivityInfo> {
        val pm = this.packageManager
        val info =
            pm.getPackageInfo(applicationContext.packageName, PackageManager.GET_ACTIVITIES)
        return info.activities
    }

    class ActivityAdapter(private val itemList: Array<ActivityInfo>) :
        RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

        class ActivityViewHolder(val binding: ListItemActivityBinding): RecyclerView.ViewHolder(binding.root) {

            fun setData(activityInfo: ActivityInfo, position: Int){
                binding.tvActivity.text = activityInfo.name.substringAfter(activityInfo.packageName)
                binding.root.setOnClickListener {
                    val c = Class.forName(activityInfo.name)
                    val intent = Intent(binding.root.context, c)
                    startActivity(binding.root.context, intent, null)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
            ListItemActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ActivityViewHolder(
                ListItemActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

        override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
            holder.setData(itemList[position], position)
        }

        override fun getItemCount(): Int {
            return itemList.size
        }

    }
}