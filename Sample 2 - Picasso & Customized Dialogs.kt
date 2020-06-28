class ProfileFragment(var ownerActivity: AppCompatActivity) : Fragment() {
    var currentPhoto: String = ""

/*    companion object {
        fun newInstance() = ProfileFragment(context = )
    }*/

    private lateinit var viewModel: ProfileViewModel
    private lateinit var basicNavigation: BasicNavigation
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        basicNavigation = BasicNavigation(ownerActivity)
        Log.d("ProfileFragment", "onViewCreated: PROFILE")


        userData.photo.observe(viewLifecycleOwner, Observer {
            when {
                currentPhoto == "" -> {
                    Log.d("ProfileFragment - Photo", "onViewCreated: First Time Photo")
                    currentPhoto = it
                    Picasso.get().load(it).into(profilePic_iv)
                }
                currentPhoto != it -> {
                    Log.d("ProfileFragment - Photo", "onViewCreated: New Photo")
                    Picasso.get().load(it).into(profilePic_iv)
                }
                else               -> Log.d("ProfileFragment - Photo", "onViewCreated: Data Changed but Same Photo")
            }

        })

        userData.name.observe(viewLifecycleOwner, Observer { profileName_tv.text = it })


        //region First Section
        editProfile_rLayout.setOnClickListener { basicNavigation.goToEditProfile() }
        changePhone_rLayout.setOnClickListener { basicNavigation.goToChangePhone() }
        changePassword_rLayout.setOnClickListener { basicNavigation.goToChangePassword() }
        savedAddresses_rLayout.setOnClickListener { basicNavigation.goToSavedAddresses() }
        //endregion

        //region Second Section
        aboutDoviey_rLayout.setOnClickListener {

            var testDialog = Dialog(ownerActivity)
            testDialog.setContentView(R.layout.about_dialog)
            testDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            testDialog.title_tv.text = getString(R.string.AboutDoviey)
            testDialog.show()
        }

        TermsAndConditions_rLayout.setOnClickListener {

            var testDialog = Dialog(ownerActivity)
            testDialog.setContentView(R.layout.about_dialog)
            testDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            testDialog.title_tv.text = getString(R.string.TermsAndConditions)
            testDialog.show()
        }

        PrivacyPolicy_rLayout.setOnClickListener {

            var testDialog = Dialog(ownerActivity)
            testDialog.setContentView(R.layout.about_dialog)
            testDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            testDialog.title_tv.text = getString(R.string.PrivacyPolicy)
            testDialog.show()
        }
        Help_rLayout.setOnClickListener { basicNavigation.goToHelp() }
        //endregion


        signOut_btn.setOnClickListener { basicNavigation.signOut() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        // TODO: Use the ViewModel
    }


}
