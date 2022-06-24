package com.chinkyfamily.encryption_android

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricPrompt
import androidx.security.crypto.MasterKeys
import com.chinkyfamily.encryption_android.databinding.ActivityMainBinding

/**
 * MainActivity
 * */
class MainActivity : AppCompatActivity()
{
    /**
     * Default Master Key
     * */
    val keyAlias : String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

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
    val advancedKeyAlias = MasterKeys.getOrCreate(advancedKeySpec)

    /**
     * promptInfo for BioMetric
     * */
    private lateinit var promptInfo : BiometricPrompt.PromptInfo

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
    }
}