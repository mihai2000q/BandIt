package com.bandit.ui.account

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bandit.R
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.data.model.Account
import com.bandit.databinding.ActivityAccountBinding
import com.bandit.di.DILocator
import com.bandit.service.IPreferencesService
import com.bandit.service.IValidatorService
import com.bandit.ui.component.AndroidComponents
import com.bandit.ui.component.ImagePickerDialog
import com.bandit.util.AndroidUtils
import com.bumptech.glide.Glide

class AccountActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityAccountBinding
    private lateinit var account: Account
    private lateinit var validatorService: IValidatorService
    private lateinit var preferencesService: IPreferencesService
    private var profilePicChanged = false
    private lateinit var profilePicUri: Uri
    private var roleIndex = 0
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        super.setContentView(binding.root)
        super.setSupportActionBar(binding.accountToolbar)
        title = resources.getString(R.string.account_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        validatorService = DILocator.getValidatorService(this)
        preferencesService = DILocator.getPreferencesService(this)

        account = intent.extras?.getParcelable(Constants.Account.EXTRA, Account::class.java)!!
        with(binding) {
            AndroidComponents.spinner(
                applicationContext,
                accountSpinnerRole,
                this@AccountActivity,
                BandItEnums.Account.Role.values()
            )
            with(account) {
                accountEtBandName.setText(this.bandName)
                accountEtEmail.setText(this.email)
                accountEtName.setText(this.name)
                accountEtNickname.setText(this.nickname)
                accountSpinnerRole.setSelection(this.role.ordinal)
            }
            val pic = intent.extras?.getByteArray(Constants.Account.PROFILE_PIC_EXTRA)!!
            val pic = Uri.parse(intent.extras?.getString(Constants.Account.PROFILE_PIC_EXTRA)!!)
            Glide.with(this@AccountActivity)
                .load(if(pic == Uri.EMPTY) R.drawable.default_avatar else pic)
                .placeholder(R.drawable.placeholder_profile_pic)
                .into(accountIvProfilePicture)
            val imagePickerDialog = ImagePickerDialog(accountIvProfilePicture) {
                profilePicUri = it
                profilePicChanged = true
            }
            accountIvProfilePicture.setOnClickListener {
                AndroidUtils.showDialogFragment(imagePickerDialog, supportFragmentManager)
            }
            accountBtUpdate.setOnClickListener { if(validateFields()) updateAccount() }
            accountBtSignOut.setOnClickListener { signOut() }
        }
    }

    private fun updateAccount() {
        val resultingIntent = Intent()
        val newAccount: Account
        with(binding) {
            newAccount = Account(
                name = this.accountEtName.text.toString(),
                nickname = this.accountEtNickname.text.toString(),
                role = BandItEnums.Account.Role.values()[roleIndex],
                email = account.email,
                bandId = account.bandId,
                bandName = account.bandName,
                id = account.id,
                userUid = account.userUid
            )
        }
        resultingIntent.putExtra(Constants.Account.RESULT_ACCOUNT_EXTRA, newAccount)
        resultingIntent.putExtra(Constants.Account.RESULT_PROFILE_PIC_CHANGED_EXTRA, profilePicChanged)
        if(profilePicChanged)
            resultingIntent.putExtra(Constants.Account.RESULT_PROFILE_PIC_EXTRA, profilePicUri)
        setResult(Activity.RESULT_OK, resultingIntent)
        finish()
    }

    private fun signOut() {
        setResult(Constants.Account.RESULT_SIGN_OUT)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        this.finish()
        setResult(Activity.RESULT_CANCELED)
        return true
    }

    private fun validateFields(): Boolean {
        return  validatorService.validateName(binding.accountEtName, binding.accountEtNameLayout) &&
                validatorService.validateNickname(binding.accountEtNickname, binding.accountEtNicknameLayout)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        roleIndex = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}