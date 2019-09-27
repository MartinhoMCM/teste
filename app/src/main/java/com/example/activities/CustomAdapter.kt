package com.example.activities

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.activities.model.activities
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.DecimalFormat

class CustomAdapter(private  val mActivitiesList :List<activities> ):
    RecyclerView.Adapter<CustomAdapter.ViewHolder>()
{

    var viewGroup: ViewGroup? =null;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        viewGroup =parent;
        val v =LayoutInflater.from(parent.context).inflate(R.layout.item_activity, parent, false)

        return  ViewHolder(v)
    }

    override fun getItemCount(): Int {
      return mActivitiesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var activity = mActivitiesList.get(position)
        var content : String =""
        var date =activity.created_at
        var amount =activity.amount

        if (date != null) {

            var created_at =receiveCreatedAd(date)
            date =getTime(created_at)
        }

        holder.itemView.setOnClickListener(View.OnClickListener {

            if(activity.transaction_type.equals("PAYMENT"))
            OpenDialog(activity, date)
        })

       if(amount!=null) {

         //  amount =amount.substring(0, amount.indexOf('.'))
           var number : Float =amount.toFloat()
           amount = numberFormat(number)

       } //


        var status =activity.status
        when(status)
        {
            "RECHARGED" -> status ="RECARREGADO"
            "PAID"-> status ="PAGO"
            "WAITING"-> status ="ESPERANDO"
        }
        if(activity.transaction_type!=null)
        {
            if(activity.transaction_type.equals("PAYMENT"))
            {
                    var  from_User =""+activity.from.firstname +" "+activity.from.lastname
                    content ="Recebeste de $from_User"
                    holder.tvHead.setText(content)
                    holder.tvSubHead1.text =activity.description
                    holder.tvSubHead2.text =date
                    holder.tvSubheadamount.text="+Kz $amount"

                val urlPathFromUser : String? = activity.from.profile_photo

                if(urlPathFromUser!=null)
                {
                    Glide.with(viewGroup!!.context).load(urlPathFromUser).into(holder.tvPhoto)

                }
                else {

                    Glide.with(viewGroup!!.context).load(R.drawable.ic_user).into(holder.tvPhoto)
                }
            }
            else if(activity.transaction_type.equals("DEPOSIT"))
            {
                var deposit:String
                var description:String
                //Esta condicao foi adicionado porque no arquivo json o objecto company_bank_account =null
                if(activity.company_bankAccount!=null)
                {
                     deposit="Depositaste na conta do "+ activity.company_bankAccount!!.holder
                     description="" + activity.company_bankAccount!!.bank!!.name
                }
                else {
                    deposit="Depositaste na conta do desconhecido"
                    description =""
                }
                holder.tvHead.setText(deposit)
                holder.tvSubHead1.setText(description)
                holder.tvSubHead2.setText(status)
                holder.tvSubheadamount.setText("+Kz $amount")
                Glide.with(viewGroup!!.context).load(R.drawable.ic_deposit_edit).into(holder.tvPhoto)
            }
            else if(activity.transaction_type.equals("RECHARGE"))
            {
                var  recharge  ="Recarregaste o telefone "
                holder.tvHead.setText(recharge)
                holder.tvSubHead1.setText(activity.mobile_operator_name)
                holder.tvSubHead2.setText(date)
                holder.tvSubheadamount.setText(activity.amount_of_utts+" UTT")
                Glide.with(viewGroup!!.context).load(R.drawable.ic_recharge).into(holder.tvPhoto);
            }

            else
            {
                //mActivitiesList.drop()
            }

        }

    }

    fun numberFormat(number:Float):String
    {
        var numberFormat =DecimalFormat("#,##0.00")
        var amount = numberFormat.format(number)

        return  amount
    }


    fun receiveCreatedAd(str :String):String
    {
        var length =str.indexOf('.')
        var longdate=""

        for (x in 0.. length-3)
        {
            if(!(str.get(x).equals('-') || str.get(x).equals('T') || str.get(x).equals(':')))
            { longdate+=str.get(x)
            }
        }
        return  longdate
    }


    fun  getTime(time :String):String
    {
        val  timeYear =time.substring(0, 4)
        val timeMonth =time.substring(4, 6)
        val timeDay =time.substring(6,8)
        val hour =time.substring(8,10)
        val min =time.substring(10,12)

        val time ="$timeDay/$timeMonth/$timeYear $hour:$min"

        return time

    }


    @SuppressLint("InflateParams")
    fun OpenDialog(activities: activities, date:String)
    {
        var amount = numberFormat((activities.amount)!!.toFloat())
        var content =""

        if(activities.transaction_type.equals("PAYMENT"))
        {
            var fee :Float =(activities.fee)!!.toFloat()
            content ="Recebeste de " +activities.from.firstname +" " +activities.from.lastname
            //var date =activities.created_at

            val dialog =BottomSheetDialog(viewGroup!!.context)
            val view =LayoutInflater.from(viewGroup!!.context).inflate(R.layout.dialog_payment, null)
            var dialog_from =view.findViewById<TextView>(R.id.dialog_from)
            var dialog_amount =view.findViewById<TextView>(R.id.dialog_amount)
            var dialog_fee =view.findViewById<TextView>(R.id.dialog_fee)
            var dialog_date =view.findViewById<TextView>(R.id.dialog_date)
            var dialog_description =view.findViewById<TextView>(R.id.dialog_description)
            var dialog_paid =view.findViewById<TextView>(R.id.dialog_paid)
            var dialog_ok =view.findViewById<TextView>(R.id.dialog_ok)

            dialog_from.text =content
            dialog_amount.text=amount

           if(fee>0)
           {
               dialog_fee.text =""+fee
           }

            dialog_date.setText(date)

            dialog_description.text=activities.description

            if(activities.status == "PAID")
            {
                dialog_paid.setText("PAGO")
            }
            else {
                dialog_paid.text = activities.status
            }

            dialog_ok.setOnClickListener(View.OnClickListener {
                dialog.dismiss()
            })


            dialog.setContentView(view)
            dialog.show()
        }
        else if(activities.transaction_type.equals("RECHARGE"))
        {
            val dialog =BottomSheetDialog(viewGroup!!.context)
            val view =LayoutInflater.from(viewGroup!!.context).inflate(R.layout.dialog_payment, null)
            dialog.setContentView(view)
            dialog.show()

        }
        else
            if(activities.transaction_type.equals("DEPOSIT"))
            {
                val dialog =BottomSheetDialog(viewGroup!!.context)
                val view =LayoutInflater.from(viewGroup!!.context).inflate(R.layout.dialog_payment, null)
                dialog.setContentView(view)
                dialog.show()
            }
    }



    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var tvPhoto : ImageView =itemView.findViewById(R.id.photo)
        var tvHead : TextView =itemView.findViewById(R.id.head)
        var tvSubheadamount : TextView =itemView.findViewById(R.id.subhead_amount)
        var tvSubHead1 :TextView =itemView.findViewById(R.id.subhead1)
        var tvSubHead2 :TextView =itemView.findViewById(R.id.subhead2)


    }


}