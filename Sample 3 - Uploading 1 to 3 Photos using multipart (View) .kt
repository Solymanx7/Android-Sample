
class HelpActivity : AppCompatActivity() {
    private lateinit var viewModel: ProfileViewModel
    var images = arrayListOf("extra", "", "", "")
    var parts: ArrayList<MultipartBody.Part> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        val btnLoading = Loading(submit_btn, loading_spinner)

        submit_btn.setOnClickListener {
            btnLoading.startLoading()
            for (i in images) {
                if (i != "extra" && i != "") {
                    Log.d("HelpActivity", "onCreate: File :- \n$i")
                    val f = File(i)

                    val requestFile: RequestBody = RequestBody.create(MediaType.parse("image/*"), f)

                    val body = MultipartBody.Part.createFormData("photos", f.name, requestFile)
                    parts.add(body)
                }

            }
            val message: RequestBody = RequestBody.create(MediaType.parse("text/plain"), explanation_tf.text.toString())
            viewModel.help(message, photos = parts)
        }

        viewModel.helpResult.observe(this, Observer {
            btnLoading.finishLoading()
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        toolbar.setNavigationOnClickListener { onBackPressed() }

        image_cv.setOnClickListener {
            pickImage(1)

        }


        add_btn.setOnClickListener {
            when {
                images[1] == "" -> pickImage(1)
                images[2] == "" -> pickImage(2)
                images[3] == "" -> pickImage(3)
            }
        }

        image_cancel_1.setOnClickListener {
            image_Button_1.setImageURI(null)
            images[1] = ""
            image_preview_iv1.visibility = View.GONE
            add_btn.visibility = View.VISIBLE
        }
        image_cancel_2.setOnClickListener {
            image_Button_2.setImageURI(null)
            images[2] = ""
            image_preview_iv2.visibility = View.GONE
            add_btn.visibility = View.VISIBLE
        }
        image_cancel_3.setOnClickListener {
            image_Button_3.setImageURI(null)
            images[3] = ""
            image_preview_iv3.visibility = View.GONE
            add_btn.visibility = View.VISIBLE
        }


    }

    private fun pickImage(imageButtonNumber: Int) {
        ImagePicker.with(this).crop().compress(1024).maxResultSize(1080, 1080).start(imageButtonNumber)
    }

    private fun isMaxNumberOfPhotosReached(): Boolean {
        for (a in images) {
            if (a == "") return false
        }
        return true
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?) {
        //super method removed
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val returnUri: Uri? = data?.data
            when (requestCode) {
                1 -> {
                    Log.d("SOrderDetails", "Uri:${returnUri.toString()} \n file:${returnUri?.path.toString()}")
                    images[1] = returnUri?.path.toString()
                    image_Button_1.setImageURI(returnUri)
                    image_cv.visibility = View.GONE
                    image_preview_iv1.visibility = View.VISIBLE
                    images_lLayout.visibility = View.VISIBLE


                    /*var bitmapImage: Bitmap =getCapturedImage(returnUri!!)
                            localView.image_preview_iv.visibility = View.VISIBLE
                            localView.first_image_Button.setImageBitmap(bitmapImage)*/
                }
                2 -> {
                    images[2] = returnUri?.path.toString()
                    image_Button_2.setImageURI(returnUri)
                    image_preview_iv2.visibility = View.VISIBLE

                }
                3 -> {
                    images[3] = returnUri?.path.toString()
                    image_Button_3.setImageURI(returnUri)
                    image_preview_iv3.visibility = View.VISIBLE
                }
            }
            if (isMaxNumberOfPhotosReached()) add_btn.visibility = View.GONE
        }


    }
}
