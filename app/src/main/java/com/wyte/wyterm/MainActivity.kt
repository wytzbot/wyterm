package com.wyte.wyterm

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.billingclient.api.*
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : Activity() {
    companion object { private const val PRO_PRODUCT_ID = "wyterm_pro" }

    private lateinit var output: TextView
    private lateinit var outputScroll: ScrollView
    private lateinit var input: EditText
    private lateinit var billingClient: BillingClient
    private var proOwned = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildUi()
        printLine("WyTerm 1.1\nFree local terminal. No ads. No AI.\n")
        setupBilling()
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(11,11,11))
            setPadding(16, 12, 16, 8)
        }
        val titleRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        val title = TextView(this).apply { text = "WyTerm"; textSize = 20f; setTextColor(Color.WHITE); gravity = Gravity.CENTER_VERTICAL }
        val pro = Button(this).apply { text = "PRO $5"; textSize = 11f; setOnClickListener { showProDialog() } }
        titleRow.addView(title, LinearLayout.LayoutParams(0, 52, 1f))
        titleRow.addView(pro, LinearLayout.LayoutParams(90, 52))
        root.addView(titleRow)

        output = TextView(this).apply {
            textSize = 14f; setTextColor(Color.LTGRAY); typeface = android.graphics.Typeface.MONOSPACE
            setPadding(4, 8, 4, 8); setTextIsSelectable(true)
        }
        outputScroll = ScrollView(this)
        outputScroll.addView(output, ViewGroup.LayoutParams(-1, -2))
        root.addView(outputScroll, LinearLayout.LayoutParams(-1, 0, 1f))

        val shortcuts = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        listOf("CTRL","TAB","ESC","↑","↓","/","~","&&").forEach { key ->
            val b = Button(this).apply { text = key; textSize = 11f; setOnClickListener { insertShortcut(key) } }
            shortcuts.addView(b, LinearLayout.LayoutParams(0, 48, 1f))
        }
        root.addView(shortcuts)

        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        input = EditText(this).apply {
            hint = "command"; singleLine = true; textSize = 15f; setTextColor(Color.WHITE); setHintTextColor(Color.GRAY)
            setBackgroundColor(Color.rgb(25,25,25)); setPadding(12,0,12,0)
            setOnEditorActionListener { _,_,_ -> runCommand(); true }
        }
        val run = Button(this).apply { text = "RUN"; setOnClickListener { runCommand() } }
        row.addView(input, LinearLayout.LayoutParams(0, 56, 1f)); row.addView(run, LinearLayout.LayoutParams(90, 56))
        root.addView(row)
        setContentView(root)
    }

    private fun setupBilling() {
        billingClient = BillingClient.newBuilder(this)
            .setListener { result, purchases ->
                if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    processPurchases(purchases)
                }
            }
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
            )
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) restorePurchases()
            }
            override fun onBillingServiceDisconnected() {}
        })
    }

    private fun restorePurchases() {
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build()
        ) { result, purchases ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) processPurchases(purchases)
        }
    }

    private fun processPurchases(purchases: List<Purchase>) {
        val owned = purchases.any { p -> p.products.contains(PRO_PRODUCT_ID) && p.purchaseState == Purchase.PurchaseState.PURCHASED }
        proOwned = owned
        purchases.filter { it.products.contains(PRO_PRODUCT_ID) && !it.isAcknowledged && it.purchaseState == Purchase.PurchaseState.PURCHASED }
            .forEach { purchase ->
                billingClient.acknowledgePurchase(
                    AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
                ) { /* acknowledged */ }
            }
        if (owned) printLine("\nWyTerm Pro active. Premium features unlocked.\n$ ")
    }

    private fun showProDialog() {
        if (proOwned) {
            Toast.makeText(this, "WyTerm Pro is already active.", Toast.LENGTH_SHORT).show()
            return
        }
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(40, 10, 40, 10) }
        box.addView(TextView(this).apply { text = "WyTerm Pro — one-time purchase\n\n• Advanced editor\n• Project workspace\n• Git GUI\n• Local server tools\n• SSH profiles\n• Extra developer tools\n\nThe terminal and command execution stay FREE.\n\nNo account/signup is required. Purchases can be restored through Google Play." })
        AlertDialog.Builder(this).setTitle("WyTerm Pro — $5").setView(box)
            .setNegativeButton("Cancel", null).setPositiveButton("Buy") { _, _ -> launchPurchase() }.show()
    }

    private fun launchPurchase() {
        billingClient.queryProductDetailsAsync(
            QueryProductDetailsParams.newBuilder().setProductList(
                listOf(QueryProductDetailsParams.Product.newBuilder().setProductId(PRO_PRODUCT_ID).setProductType(BillingClient.ProductType.INAPP).build())
            ).build()
        ) { result, queryResult ->
            val details = queryResult.productDetailsList
            if (result.responseCode != BillingClient.BillingResponseCode.OK || details.isEmpty()) {
                Toast.makeText(this, "Pro product is not available yet. Configure wyterm_pro in Play Console.", Toast.LENGTH_LONG).show()
                return@queryProductDetailsAsync
            }
            val product = details.first()
            val offer = product.oneTimePurchaseOfferDetailsList?.firstOrNull()
            if (offer == null) return@queryProductDetailsAsync
            val params = BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(product).setOfferToken(offer.offerToken).build()
            billingClient.launchBillingFlow(this, BillingFlowParams.newBuilder().setProductDetailsParamsList(listOf(params)).build())
        }
    }

    private fun insertShortcut(key: String) {
        val s = when(key) { "CTRL" -> "Ctrl+"; "TAB" -> "\t"; "ESC" -> "\u001b"; "&&" -> " && "; else -> key }
        input.append(s); input.requestFocus()
    }

    private fun runCommand() {
        val command = input.text.toString().trim(); if (command.isEmpty()) return
        printLine("\n$ $command\n"); input.setText("")
        if (command == "help") { printLine("help, pwd, ls, whoami, date, clear, echo TEXT\n$ "); return }
        if (command == "clear") { output.text = ""; printLine("$ "); return }
        execute(command)
    }

    private fun execute(command: String) {
        Thread {
            try {
                val p = ProcessBuilder("sh", "-c", command).redirectErrorStream(true).start()
                val reader = BufferedReader(InputStreamReader(p.inputStream)); val result = StringBuilder(); var line: String?
                while (reader.readLine().also { line = it } != null) result.append(line).append('\n')
                p.waitFor()
                runOnUiThread { printLine(result.toString() + "$ ") }
            } catch (e: Exception) { runOnUiThread { printLine("Error: ${e.message}\n$ ") } }
        }.start()
    }

    private fun printLine(text: String) {
        output.append(text)
        outputScroll.post { outputScroll.fullScroll(View.FOCUS_DOWN) }
    }

    override fun onResume() { super.onResume(); if (::billingClient.isInitialized && billingClient.isReady) restorePurchases() }
    override fun onDestroy() { if (::billingClient.isInitialized) billingClient.endConnection(); super.onDestroy() }
}
