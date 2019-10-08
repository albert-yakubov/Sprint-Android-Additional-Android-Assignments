package com.example.basicmediaplayer

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.SeekBar
import androidx.annotation.RawRes
import androidx.core.graphics.convertTo
import androidx.core.os.postDelayed
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_main.*

/*
This activity demonstrates how to use ExoPlayer to play a video from local file system and
also from the internet.
 */

//TODO 1: Make sure to turn on the permission for internet in the manifest.
//TODO 2: Enable 'targetCompatibility JavaVersion.VERSION_1_8' in app build.gradle file.
//TODO 3: Enable ExoPlayer dependency in the app build.gradle file.

class MainActivity : AppCompatActivity() {

    //TODO 4: Declare the variables needed. A simpleExoPlayer instance.
    lateinit var videoExoPlayer: SimpleExoPlayer


    //TODO 5: Notice the url we will be using to streaming mp4 over the internet.
    //  val URL = "https://archive.org/download/Popeye_forPresident/Popeye_forPresident_512kb.mp4"
    val URL = "https://my.mail.ru/mail/irinaosnova/video/3417/3492.html"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //TODO 6: Create a videoplayer instance with default settings, implement createVideoPlayer function
        createVideoPlayer()

        //TODO 7: Create a function to setup video player from file system
        //This function can be switched out with the setupVideoPlayerWithURL to stream the video
        //from the internet.
        setupVideoPlayerFromFileSystem()
        seekBarFunctionality()
        //setupVideoPlayerWithURL()
        //TODO 9: Setup clicklisteners for play and pause buttons
        play.setOnClickListener {

            videoExoPlayer.playWhenReady = true
            video_seek_bar.max = videoExoPlayer.duration.toInt()

            //todo display the time
        val handler = Handler()
            this@MainActivity.runOnUiThread ( object : Runnable{
                override fun run() {
                    val currentPos = videoExoPlayer.currentPosition
                    video_seek_bar.progress = currentPos.toInt()
                    println("CURRENT POS: $currentPos")
                    iptext.text = "$currentPos"
                    handler.postDelayed(this, 1000)
                }

            })
        }
        pause.setOnClickListener { videoExoPlayer.playWhenReady = false }

        //TODO 9a: Set the player for the PlayerView
        video_view.player = videoExoPlayer

    }

    private fun seekBarFunctionality() {
        // In the SeekBar listener, when the seekbar progress is changed,
        // update the video progress
        video_seek_bar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                seekBar?.let {
                    videoExoPlayer.seekTo(progress.toLong())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) { }

            override fun onStopTrackingTouch(seekBar: SeekBar?) { }

        })
    }

    fun setupVideoPlayerFromFileSystem() {
        videoExoPlayer.prepare(createRawMediaSource(R.raw.hitman))
    }

    //TODO 8: Create a function to setup video player with url to stream video through internet.
    fun setupVideoPlayerWithURL() {
        videoExoPlayer.prepare(createUrlMediaSource(URL))
    }

    fun createVideoPlayer() {
        // Need a track selector
        val trackSelector = DefaultTrackSelector()
        // Need a load control
        val loadControl = DefaultLoadControl()
        // Need a renderers factory
        val renderFact = DefaultRenderersFactory(this)
        // Set up the ExoPlayer
        videoExoPlayer = ExoPlayerFactory.newSimpleInstance(this, renderFact, trackSelector, loadControl)

        // Set up the scaling mode to crop and fit the video to the screen
        videoExoPlayer.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING



    }


    //TODO 12: Do not forget to stop the player when the user navigates away from the screen
    override fun onStop() {
        super.onStop()
        videoExoPlayer.stop()
    }

    //TODO 10: Notice the code to implement and create a mediasource function using URL, returns a mediasource
    fun createUrlMediaSource(url: String): MediaSource {
        val userAgent = Util.getUserAgent(this, getString(R.string.app_name))
        return ExtractorMediaSource.Factory(DefaultDataSourceFactory(this, userAgent))
            .setExtractorsFactory(DefaultExtractorsFactory())
            .createMediaSource(Uri.parse(url))
    }

    //TODO 11: Notice the code to implement and create a mediasource function using raw resource, returns a mediasource
    fun createRawMediaSource(@RawRes rawId: Int): MediaSource {
        val rawResourceDataSource = RawResourceDataSource(this)
        val dataSpec = DataSpec(RawResourceDataSource.buildRawResourceUri(rawId))
        rawResourceDataSource.open(dataSpec)
        return ExtractorMediaSource.Factory(DataSource.Factory {
            rawResourceDataSource
        }).createMediaSource(rawResourceDataSource.uri)
    }

}
