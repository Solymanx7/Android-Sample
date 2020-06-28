class ChangePhoneFragment(var ownerActivity: AppCompatActivity) : Fragment(), TextWatcher {
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_change_phone, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        val btnLoading = Loading(changePhone_btn, loading_spinner)

        phoneNumber_tf.addTextChangedListener(this)
        changePhone_btn.setOnClickListener {
            if (Validation.validatePhone(phoneNumber_tf.text.toString()) == Validation.phoneErrorMessage) {
                clearAllErrors()
                setError(phoneNumber_tfLayout, phoneNumber_tf, Validation.phoneErrorMessage, R.drawable.ic_call)
            }
            else {
                clearAllErrors()
                btnLoading.startLoading()
                viewModel.changePhone(phoneNumber_tf.text.toString())
            }
        }

        viewModel.changePhoneResult.observe(viewLifecycleOwner, Observer {
            Toast.makeText(ownerActivity, it, Toast.LENGTH_SHORT).show()
            btnLoading.finishLoading()
            when (it) {
                "success" -> {
                    //gotoOTP - Alternative way
                    val basicNavigation = BasicNavigation(ownerActivity)
                    basicNavigation.goToLogin()
                }
            }
        })
        toolbar.setNavigationOnClickListener { ownerActivity.onBackPressed() }




        Log.d("ChangePhoneFragment", "onViewCreated: Created")
    }

    override fun afterTextChanged(p0: Editable?) {
    }

    override fun beforeTextChanged(
        p0: CharSequence?,
        p1: Int,
        p2: Int,
        p3: Int) {
    }

    override fun onTextChanged(
        p0: CharSequence?,
        p1: Int,
        p2: Int,
        p3: Int) {
        changePhone_btn.isEnabled = phoneNumber_tf.length() > 0
    }

    override fun onDestroyView() {
        clearAllErrors()
        super.onDestroyView()
    }

    private fun setError(
        layout: TextInputLayout,
        textfield: TextInputEditText,
        errorMessage: String,
        iconId: Int) {
        layout.error = errorMessage

        val unwrappedDrawable = AppCompatResources.getDrawable(context!!, iconId)
        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
        DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(context!!, R.color.buttonColor))
        textfield.setCompoundDrawablesWithIntrinsicBounds(wrappedDrawable, null, null, null)
    }

    private fun clearError(
        layout: TextInputLayout,
        textfield: TextInputEditText,
        iconId: Int) {
        layout.error = null

        val unwrappedDrawable = AppCompatResources.getDrawable(context!!, iconId)
        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
        DrawableCompat.setTintList(wrappedDrawable, null)
        textfield.setCompoundDrawablesWithIntrinsicBounds(wrappedDrawable, null, null, null)
    }

    private fun clearAllErrors() {
        clearError(phoneNumber_tfLayout, phoneNumber_tf, R.drawable.ic_call)
    }


}
