package com.example.projetohelio


import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset


class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getJsonProdutosFromWEB();
    }



    @SuppressLint("StaticFieldLeak")
    @Suppress("DEPRECATION")
    private inner class DownloadImageFromInternet(var imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {
        init {
            Toast.makeText(applicationContext, "Carregando imagem", Toast.LENGTH_SHORT).show()
        }
        override fun doInBackground(vararg urls: String): Bitmap? {
            val imageURL = urls[0]
            var image: Bitmap? = null
            try {
                val `in` = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(`in`)
            }
            catch (e: Exception) {
                Log.e("Error Message", e.message.toString())
                e.printStackTrace()
            }
            return image
        }
        override fun onPostExecute(result: Bitmap?) {
            imageView.setImageBitmap(result)
        }
    }
    fun getJsonProdutosFromWEB() {
        val queue = Volley.newRequestQueue(this)
        val url = "http://helioesperidiao.com/api.php"
        val requestBody = "id=1" + "&msg=test_msg"
        val stringReq : StringRequest =
            object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    val linearLayou: LinearLayout = findViewById(R.id.linearL)
                    var resposta = response.toString()
                    val array = JSONArray(resposta)
                    val tamanho =array.length()
                    for (i in 0 until tamanho ) {
                        val item: JSONObject = array.getJSONObject(i) // recupera o objeto na posição dentro do array
                        var idProduto = item.get("id").toString()
                        var type = item.get("type").toString();
                        var desc = item.get("desc").toString();
                        var qtd = item.get("qtd").toString();
                        var img = item.get("img").toString();


                            var novoImageView = ImageView(this)
                            novoImageView.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            DownloadImageFromInternet(novoImageView).execute(img)
                            linearLayou.addView(novoImageView)


                            var novoTextView = TextView(this)
                            novoTextView.text = desc
                            novoTextView.text = idProduto + " - " +type + " - " + qtd + "\n" + desc  + "\n"
                            novoTextView.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            linearLayou.addView(novoTextView)
                            println(idProduto + " - " +type + " - " + qtd)



                        var meubutao = Button(this)
                        meubutao.text = "pedido"
                        meubutao.setOnClickListener{

                        }

                        linearLayou.addView(meubutao)
                    }
                },
                Response.ErrorListener { error ->
                    Log.d("API", "error => $error")
                }
            ){
                override fun getBody(): ByteArray {
                    return requestBody.toByteArray(Charset.defaultCharset())
                }
            }
        queue.add(stringReq)
    }
}