package com.example.guilhermeantonio.analyse_image

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.google.gson.Gson
import com.microsoft.projectoxford.vision.VisionServiceClient
import com.microsoft.projectoxford.vision.VisionServiceRestClient
import com.microsoft.projectoxford.vision.contract.AnalysisResult
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    var visionServiceClient = VisionServiceRestClient("cec6de68ab564f5e83dd5195b0b9e4f5",
            "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.eu)
        imageView.setImageBitmap(bitmap)

        var outPutStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outPutStream)
        var inputStream = ByteArrayInputStream(outPutStream.toByteArray())

        var visionTask = @SuppressLint("StaticFieldLeak")
        object : AsyncTask<InputStream, String, String>() {

            var progressDialog = ProgressDialog(this@MainActivity)


            override fun doInBackground(vararg params: InputStream?): String? {

                try {
                    publishProgress("Recognizing...")
                    var features = arrayOf("Description")
                    var details = arrayOf("")


                    var result = visionServiceClient.analyzeImage(params[0], features, details)

                    var strResult = Gson().toJson(result)

                    return strResult

                } catch (e: Exception) {
                    return null
                }


            }

            override fun onPreExecute() {


                progressDialog.show()

            }

            override fun onProgressUpdate(vararg values: String?) {
                progressDialog.setMessage(values[0])
            }

            override fun onPostExecute(result: String?) {

                progressDialog.dismiss()

                var result = Gson().fromJson(result, AnalysisResult::class.java)
                var stringBuilder = StringBuilder()

                for (caption in result.description.captions) {

                    stringBuilder.append(caption.text)

                }

                description.text = stringBuilder

            }
        }

        button.setOnClickListener {

            visionTask.execute(inputStream)


        }


    }
}
