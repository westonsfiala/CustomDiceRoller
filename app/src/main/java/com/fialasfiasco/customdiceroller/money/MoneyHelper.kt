package com.fialasfiasco.customdiceroller.money

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.android.billingclient.api.*
import com.fialasfiasco.customdiceroller.MainActivity
import com.fialasfiasco.customdiceroller.R

class MoneyHelper(private val mContext: MainActivity)
{

    private val mBillingClient: BillingClient

    init {
        mBillingClient = BillingClient.newBuilder(mContext)
            .enablePendingPurchases()
            .setListener(PurchaseListener(mContext)).build()
    }

    // This method launches a dialog that has some buttons for choosing what level of support.
    fun getSupport() {
        mBillingClient.startConnection(object : BillingClientStateListener {
            // When we connect to the play store server, start launching the dialog
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {

                    // Ask the server about the in app purchases that I enabled.
                    val skuList = listOf(
                        "support_the_app_a",
                        "support_the_app_b",
                        "support_the_app_c")
                    val params = SkuDetailsParams.newBuilder()
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
                    mBillingClient.querySkuDetailsAsync(params.build()) { innerBillingResult, skuDetailsList ->
                        if(innerBillingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            launchDialog(skuDetailsList)
                        }
                        else {
                            Toast.makeText(mContext, "Something went wrong, try again.", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(mContext, "Something went wrong, try again.", Toast.LENGTH_LONG).show()
                }
            }
            override fun onBillingServiceDisconnected() { }
        })
    }

    private fun launchDialog(skuDetailsList : List<SkuDetails>) {
        val purchaseDialog = Dialog(mContext)
        purchaseDialog.setContentView(R.layout.dialog_purchase)
        val scrolledLayout = purchaseDialog.findViewById<LinearLayout>(R.id.scrolledLayout)
        for (skuDetail in skuDetailsList) {
            val supportItem = mContext.layoutInflater.inflate(R.layout.layout_purchase_support, scrolledLayout, false)
            val text = supportItem.findViewById<TextView>(R.id.supportText)
            val button = supportItem.findViewById<Button>(R.id.supportButton)

            text.text = skuDetail.title
            button.text = skuDetail.price

            button.setOnClickListener {
                val flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetail)
                    .build()
                val purchase = mBillingClient.launchBillingFlow(mContext, flowParams)

                if(purchase?.responseCode != BillingClient.BillingResponseCode.OK) {
                    Toast.makeText(mContext, "Something went wrong, try again.", Toast.LENGTH_LONG).show()
                }
            }
            scrolledLayout.addView(supportItem)
        }

        purchaseDialog.show()
    }

    inner class PurchaseListener(private val context: Context) : PurchasesUpdatedListener
    {
        override fun onPurchasesUpdated(
            billingResult: BillingResult?,
            purchases: MutableList<Purchase>?
        ) {
            if(billingResult?.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for(purchase in purchases) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        // Acknowledge the purchase if it hasn't already been acknowledged.
                        if (!purchase.isAcknowledged) {
                            val consumePurchaseParams = ConsumeParams.newBuilder()
                                .setPurchaseToken(purchase.purchaseToken)
                                .build()
                            mBillingClient.consumeAsync(consumePurchaseParams) { result, _ ->
                                if(result.responseCode == BillingClient.BillingResponseCode.OK) {
                                    Toast.makeText(
                                        context,
                                        "Thank you for supporting the App!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
