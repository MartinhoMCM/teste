package com.example.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.activities.adapter.CustomAdapter
import com.example.activities.model.activities
import com.example.activities.service.KambaClient
import com.example.activities.service.ServiceBuilder
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {


    val activityList : ArrayList<activities> = ArrayList();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rv_activity =findViewById(R.id.rv_activities) as RecyclerView
        rv_activity.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val adapter = CustomAdapter(activityList)
        rv_activity.adapter =adapter

        progressbar_id.visibility = View.VISIBLE


        sr_layout.setColorSchemeColors(resources.getColor(R.color.colorPrimary))

        sr_layout.setOnRefreshListener {
            loadItems()
        }


        loadItems()

    }

    fun loadItems(){
        sr_layout.isRefreshing=true
        loadActivities()


    }

    fun onItemsLoadComplete()
    {
        rv_activities.adapter!!.notifyDataSetChanged()
        sr_layout.isRefreshing=false
    }

    override fun onResume() {
        super.onResume()

        sr_layout.setOnRefreshListener {
            loadItems()
        }

    }

    private  fun loadActivities()
    {
        sr_layout.isRefreshing =false
      val activitiesService =  ServiceBuilder.buildService(KambaClient::class.java)

      val requestCall = activitiesService.getActivities();

       requestCall.enqueue(object : Callback<List<activities>> {

           override fun onResponse( call: Call<List<activities>>,  response: Response<List<activities>>)
           {
              if(response.isSuccessful)
              {
                  val activities = response.body()!!
                  var activities_op :ArrayList<activities> = ArrayList()

                  for ( activity in activities)
                  {
                       if((  activity.transaction_type.equals("PAYMENT") || activity.transaction_type.equals("RECHARGE")
                           || activity.transaction_type.equals("DEPOSIT"))) {
                           activities_op.add(activity)
                       }
                  }

                  rv_activities.adapter =
                      CustomAdapter(activities_op)
                  rv_activities.visibility=View.VISIBLE
                  progressbar_id.visibility = View.INVISIBLE


                  onItemsLoadComplete();

              }else if(response.code() == 401) {
                  Toast.makeText(this@MainActivity,
                      "Your session has expired. Please Login again.", Toast.LENGTH_LONG).show()
                  progressbar_id.visibility = View.INVISIBLE
              } else { // Application-level failure
                  // Your status code is in the range of 300's, 400's and 500's
                   Toast.makeText(this@MainActivity, "Failed to retrieve items", Toast.LENGTH_LONG).show()
                  progressbar_id.visibility = View.INVISIBLE
              }
               onItemsLoadComplete();

           }
           override fun onFailure(call: Call<List<activities>>, t: Throwable) {
               Log.e("Note ", t.toString())
               Toast.makeText(this@MainActivity, "Error Occurred" + t.toString(), Toast.LENGTH_LONG).show()
               onItemsLoadComplete();
               progressbar_id.visibility = View.INVISIBLE
           }

       })


    }
}
