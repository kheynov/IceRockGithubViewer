package ru.kheynov.icerockgithubviewer.presentation

import android.net.http.HttpResponseCache
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import retrofit2.http.HTTP
import ru.kheynov.icerockgithubviewer.R
import ru.kheynov.icerockgithubviewer.data.entities.UserInfo
import ru.kheynov.icerockgithubviewer.data.repository.AppRepository
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
}