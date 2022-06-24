package com.chinkyfamily.encryption_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chinkyfamily.encryption_android.databinding.ActivityHomeBinding

/**
 * HomeActivity
 * */
class HomeActivity : AppCompatActivity()
{
    private var _binding : ActivityHomeBinding? = null
    private val binding get() = _binding

    /**
     * onCreate callback method of the Activity.
     * */
    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }
}