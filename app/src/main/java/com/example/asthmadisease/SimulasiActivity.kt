package com.example.asthmadisease

import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SimulasiActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    private val mModelPath = "asthma.tflite"

    private lateinit var resultText: TextView
    private lateinit var Age: EditText
    private lateinit var Gender: EditText
    private lateinit var Ethnicity: EditText
    private lateinit var EducationLevel: EditText
    private lateinit var Smoking: EditText
    private lateinit var PetAllergy: EditText
    private lateinit var Eczema: EditText
    private lateinit var checkButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulasi)

        resultText = findViewById(R.id.txtResult)
        Age = findViewById(R.id.Age)
        Gender = findViewById(R.id.Gender)
        Ethnicity = findViewById(R.id.Ethnicity)
        EducationLevel = findViewById(R.id.EducationLevel)
        Smoking = findViewById(R.id.Smoking)
        PetAllergy = findViewById(R.id.PetAllergy)
        Eczema = findViewById(R.id.Eczema)
        checkButton = findViewById(R.id.btnCheck)

        checkButton.setOnClickListener {
            var result = doInference(
                Age.text.toString(),
                Gender.text.toString(),
                Ethnicity.text.toString(),
                EducationLevel.text.toString(),
                Smoking.text.toString(),
                PetAllergy.text.toString(),
                Eczema.text.toString())
            runOnUiThread {
                if (result == 0) {
                    resultText.text = "Terkena Penyakit Asthma"
                }else if (result == 1){
                    resultText.text = "Tidak Terkena Penyakit Asthma"
                }
            }
        }
        initInterpreter()
    }

    private fun initInterpreter() {
        val options = org.tensorflow.lite.Interpreter.Options()
        options.setNumThreads(8)
        options.setUseNNAPI(true)
        interpreter = org.tensorflow.lite.Interpreter(loadModelFile(assets, mModelPath), options)
    }

    private fun doInference(input1: String, input2: String, input3: String, input4: String, input5: String, input6: String, input7: String): Int{
        val inputVal = FloatArray(7)
        inputVal[0] = input1.toFloat()
        inputVal[1] = input2.toFloat()
        inputVal[2] = input3.toFloat()
        inputVal[3] = input4.toFloat()
        inputVal[4] = input5.toFloat()
        inputVal[5] = input6.toFloat()
        inputVal[6] = input7.toFloat()
        val output = Array(1) { FloatArray(2) }
        interpreter.run(inputVal, output)

        Log.e("result", (output[0].toList()+" ").toString())

        return output[0].indexOfFirst { it == output[0].maxOrNull() }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer{
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}