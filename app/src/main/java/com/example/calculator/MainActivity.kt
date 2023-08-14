package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.math.BigDecimal

class MainActivity : AppCompatActivity() {

    private var tvInput: TextView? = null
    private var digitCount = 0
    private var isDecimal = false
    private var prevNum = BigDecimal.ZERO
    private var currNum = BigDecimal.ZERO
    private var isNewNum = false

    private var isPlus = false
    private var isMinus = false
    private var isMultiply = false
    private var isDivide = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvInput = findViewById(R.id.tvInput)
    }

    fun onDigit(view: View) {
        var currText = tvInput?.text.toString()
        if (currText == "0" || isNewNum) {
            currText = ""
            isNewNum = false
            digitCount = 0
        }
        if (digitCount > 8) {
            Toast.makeText(this, "Maximum digits reached", Toast.LENGTH_SHORT).show()
            return
        }

        digitCount++
        currText += ((view as Button).text)

        var num = BigDecimal(currText.replace(",", ""))
        tvInput?.text = toText(num)

        rewrite()

        findViewById<Button>(R.id.acBtn).text = "C"
    }

    fun onClear(view: View) {
        val btn = view as Button
        if(btn.text == "AC") {
            tvInput?.text = "0"
            isNewNum = true
            isDecimal = false
            currNum = BigDecimal.ZERO
            prevNum = BigDecimal.ZERO
            digitCount = 0
            resetAction()
        } else {
            tvInput?.text = "0"
            isNewNum = true
            isDecimal = false
            findViewById<Button>(R.id.acBtn).text = "AC"
        }
    }

    fun onDecimal(view: View) {
        if (isDecimal) {
            return
        }
        isDecimal = true
        var currText = tvInput?.text.toString()
        currText += "."
        tvInput?.text = currText
        findViewById<Button>(R.id.acBtn).text = "C"
    }

    fun toText(num: BigDecimal): String {
        // Check if the number is a whole number
        val isWholeNumber = num.remainder(BigDecimal.ONE) == BigDecimal.ZERO

        return if (isWholeNumber) {
            // Convert the number to a whole number and format it with commas
            String.format("%,d", num.toBigInteger())
        } else {
            // Split the number into its whole and fractional parts
            num.toString()
        }
    }

    fun onPercent(view: View) {
        var num = tvInput?.text.toString().replace(",", "").toBigDecimal()
        num = num.divide(BigDecimal(100))
        tvInput?.text = toText(num)
        rewrite()
    }

    fun rewrite() {
        digitCount = tvInput?.text.toString().replace(",", "").replace(".", "").length
        if (digitCount > 7) {
            tvInput?.textSize = 60f
        } else if (digitCount > 5) {
            tvInput?.textSize = 70f
        } else {
            tvInput?.textSize = 100f
        }
    }

    fun onSign(view: View) {
        var num = tvInput?.text.toString().replace(",", "").toBigDecimal()
        num = num.negate()
        tvInput?.text = toText(num)
        rewrite()
    }

    fun onAction(view: View) {
        currNum = tvInput?.text.toString().replace(",", "").toBigDecimal()
        calculate()
        tvInput?.text = toText(currNum)
        rewrite()
        resetAction()

        val btn = view as Button
        val action = btn.text.toString()
        when (action) {
            "+" -> {
                isPlus = true
            }

            "-" -> {
                isMinus = true
            }

            "×" -> {
                isMultiply = true
            }

            "÷" -> {
                isDivide = true
            }
        }
        prevNum = currNum
        currNum = BigDecimal.ZERO
        isNewNum = true
    }


    fun resetAction() {
        isPlus = false
        isMinus = false
        isMultiply = false
        isDivide = false
    }

    fun calculate() {
        if (isPlus) {
            currNum = currNum.add(prevNum)
            isPlus = false
        } else if (isMinus) {
            currNum = prevNum.subtract(currNum)
            isMinus = false
        } else if (isMultiply) {
            currNum = currNum.multiply(prevNum)
            isMultiply = false
        } else if (isDivide) {
            currNum = prevNum.divide(currNum)
            isDivide = false
        }
        prevNum = BigDecimal.ZERO
    }

}