package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {

    private var tvInput: TextView? = null // TextView displaying the input
    private var digitCount = 0 // Number of digits in the input to determine the text size
    private var isDecimal = false // Whether the input contains a decimal point
    // variables to store values
    private var prevNum = BigDecimal.ZERO
    private var currNum = BigDecimal.ZERO

    private var isNewNum = false // Whether the input is a new number

    // variables to store the action to be performed
    private var isPlus = false
    private var isMinus = false
    private var isMultiply = false
    private var isDivide = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvInput = findViewById(R.id.tvInput)
    }

    fun onDigit(view: View) { // Function to handle digit input
        var currText = tvInput?.text.toString()
        if (currText == "0" || isNewNum) { // If the input is 0 or a new number, replace it with the digit
            currText = ""
            isNewNum = false
            digitCount = 0
        }
        if (digitCount > 8) { // limit the number of digits to 8
            Toast.makeText(this, "Maximum digits reached", Toast.LENGTH_SHORT).show()
            return
        }

        digitCount++
        currText += ((view as Button).text) // append the digit to the input

        var num = BigDecimal(currText.replace(",", "")) // convert the input to a BigDecimal
        tvInput?.text = toText(num) // format the input with commas

        rewrite() // adjust the text size

        findViewById<Button>(R.id.acBtn).text = "C" // change the AC button to C
    }

    fun onClear(view: View) { // Function to handle the AC/C button
        val btn = view as Button
        if(btn.text == "AC") { // If the button is AC, reset everything
            tvInput?.text = "0"
            isNewNum = true
            isDecimal = false
            currNum = BigDecimal.ZERO
            prevNum = BigDecimal.ZERO
            digitCount = 0
            resetActions()
        } else { // If the button is C, clear the input
            tvInput?.text = "0"
            isNewNum = true
            isDecimal = false
            findViewById<Button>(R.id.acBtn).text = "AC"
        }
    }

    fun onDecimal(view: View) { // Function to handle the decimal point button
        if (isDecimal) { // If the input already contains a decimal point, return
            return
        }
        isDecimal = true // Set isDecimal to true to prevent multiple decimal points
        var currText = tvInput?.text.toString() // Get the current input
        currText += "." // Append a decimal point to the input
        tvInput?.text = currText
        findViewById<Button>(R.id.acBtn).text = "C" // Change the AC button to C
    }

    fun toText(num: BigDecimal): String { // Function to format the input with commas
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

    fun onPercent(view: View) { // Function to handle the percent button
        var num = tvInput?.text.toString().replace(",", "").toBigDecimal()
        num = num.divide(BigDecimal(100))
        tvInput?.text = toText(num)
        rewrite()
    }

    fun rewrite() { // Function to adjust the text size
        digitCount = tvInput?.text.toString().replace(",", "").replace(".", "").length
        if (digitCount > 7) {
            tvInput?.textSize = 60f
        } else if (digitCount > 5) {
            tvInput?.textSize = 70f
        } else {
            tvInput?.textSize = 100f
        }
    }

    fun onSign(view: View) { // Function to handle the +/- button
        var num = tvInput?.text.toString().replace(",", "").toBigDecimal()
        num = num.negate()
        tvInput?.text = toText(num)
        rewrite()
    }

    fun onAction(view: View) { // Function to handle the +, -, ×, and ÷ buttons
        // save the current input
        currNum = tvInput?.text.toString().replace(",", "").toBigDecimal()
        calculate() // perform the previous action
        tvInput?.text = toText(currNum) // display the result
        rewrite()
        resetActions()

        val btn = view as Button
        val action = btn.text.toString()
        when (action) { // set the action to be performed
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
        prevNum = currNum // save the current input as the previous number
        currNum = BigDecimal.ZERO // reset the current input
        isNewNum = true // set isNewNum to true to indicate that the next input is a new number
    }


    fun resetActions() {
        isPlus = false
        isMinus = false
        isMultiply = false
        isDivide = false
    }

    fun calculate() { // Function to perform the previous action
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
            if(currNum == BigDecimal.ZERO) { // If the current input is 0, display an error message
                Toast.makeText(this, "Cannot divide by 0", Toast.LENGTH_SHORT).show()
                return
            }
            currNum = prevNum.divide(currNum, 8, RoundingMode.HALF_EVEN)
            isDivide = false
        }
        prevNum = BigDecimal.ZERO // reset the previous number
    }

}