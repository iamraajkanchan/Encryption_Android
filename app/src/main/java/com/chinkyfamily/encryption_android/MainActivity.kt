package com.chinkyfamily.encryption_android

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.chinkyfamily.encryption_android.databinding.ActivityMainBinding
import java.security.KeyStore
import java.util.concurrent.Executor
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * MainActivity
 * Note - Code is working for default Biometric Settings.
 * */
class MainActivity : AppCompatActivity()
{
    /**
     * Default Master Key
     * */

    // val keyAlias : String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    /**
     * Customized Key Specifications
     * */

    @RequiresApi(Build.VERSION_CODES.M)
    val advancedKeySpec : KeyGenParameterSpec = KeyGenParameterSpec.Builder("master_key" ,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT).apply {
        setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        setKeySize(256)
        setUserAuthenticationRequired(true)
        setUserAuthenticationValidityDurationSeconds(25)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
        {
            setUnlockedDeviceRequired(true)
            setIsStrongBoxBacked(true)
        }
    }.build()
    /**
     * Customized Advanced Master Key
     * */

    // val advancedKeyAlias = MasterKeys.getOrCreate(advancedKeySpec)

    /**
     * Executor
     * */
    private lateinit var executor : Executor

    /**
     * promptInfo for BioMetric
     * */
    private lateinit var promptInfo : BiometricPrompt.PromptInfo

    /**
     * bioMetricPrompt for BioMetric
     * */
    private lateinit var biometricPrompt : BiometricPrompt

    private val registerForActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        }

    private var _binding : ActivityMainBinding? = null
    private val binding get() = _binding

    /**
     * onCreate callback method of the Activity.
     * */
    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val bioMetricManager = BiometricManager.from(this@MainActivity)
        when (bioMetricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL))
        {
            BiometricManager.BIOMETRIC_SUCCESS -> Log.d("Encryption_Android" ,
                "App can authenticate using biometrics.")
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> Log.e("Encryption_Android" ,
                "No biometric features available on this device.")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> Log.e("Encryption_Android" ,
                "Biometric features are currently unavailable.")
        }
        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
            putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED ,
                BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
        }

        // registerForActivity.launch(enrollIntent)
        configureBioMetricSecurity()
    }

    private fun configureBioMetricSecurity()
    {
        executor = ContextCompat.getMainExecutor(this)
        promptInfo = BiometricPrompt.PromptInfo.Builder().setTitle("BioMetric Login For App")
            .setSubtitle("Log In Using your BioMetric Credentials")
            .setNegativeButtonText("Use Account Password").build()
        biometricPrompt =
            BiometricPrompt(this , executor , object : BiometricPrompt.AuthenticationCallback()
            {
                override fun onAuthenticationError(errorCode : Int , errString : CharSequence)
                {
                    super.onAuthenticationError(errorCode , errString)
                    Toast.makeText(this@MainActivity ,
                        "Authentication Error!!!" ,
                        Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationFailed()
                {
                    super.onAuthenticationFailed()
                    Toast.makeText(this@MainActivity ,
                        "Authentication Failed!!!" ,
                        Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result : BiometricPrompt.AuthenticationResult)
                {
                    super.onAuthenticationSucceeded(result)
                    Intent(this@MainActivity , HomeActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(this)
                    }
                }
            })

        /*
        val cipher = getCipher()
        val secretKey = getSecretKey()
        cipher.init(Cipher.ENCRYPT_MODE , secretKey)
        biometricPrompt.authenticate(promptInfo , BiometricPrompt.CryptoObject(cipher))
        */
        biometricPrompt.authenticate(promptInfo)
    }

    fun generateSecretKey()
    {
        val keyGenerator =
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES , "AndroidKeyStore")
        keyGenerator.init(advancedKeySpec)
        keyGenerator.generateKey()
    }

    private fun getSecretKey() : SecretKey
    {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null) // Shouldn't pass null in the
        return keyStore.getKey("master_key" , null) as SecretKey
    }

    private fun getCipher() : Cipher
    {
        return Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7)
    }
}