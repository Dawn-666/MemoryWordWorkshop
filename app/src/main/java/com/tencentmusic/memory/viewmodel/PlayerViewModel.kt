package com.tencentmusic.memory.viewmodel

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tencentmusic.memory.data.Database
import com.tencentmusic.memory.data.Word
import java.util.Locale

class PlayerViewModel private constructor(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {
    val context: Context
        get() = getApplication<Application>().applicationContext

    private val handler by lazy {
        Handler(context.mainLooper)
    }

    private val tts by lazy { TextToSpeech(context, this) }

    private val _playlist = MutableLiveData<List<Word>>()
    val playlist: LiveData<List<Word>> get() = _playlist

//    private val _playlistIndex = MutableLiveData(0)
//    val playlistIndex: LiveData<Int> get() = _playlistIndex
    var playlistIndex = 0

    private val _currentIndex = MutableLiveData(0)
    val currentIndex: LiveData<Int> get() = _currentIndex

    private val _currentWord = MutableLiveData<Word?>()
    val currentWord: LiveData<Word?> get() = _currentWord

    private val _playing = MutableLiveData(false)
    val playing: LiveData<Boolean> get() = _playing

    private val _playEnglish = MutableLiveData(true)
    val playEnglish: LiveData<Boolean> get() = _playEnglish

    private val _playChinese = MutableLiveData(true)
    val playChinese: LiveData<Boolean> get() = _playChinese

    private val _playSpelling = MutableLiveData(true)
    val playSpelling: LiveData<Boolean> get() = _playSpelling

    init {
        loadPlaylist(Database.playlist)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // 设置语言
            val result = tts.setLanguage(Locale.CHINA)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // 语言数据缺失或不支持
                Toast.makeText(context, "语言不支持", Toast.LENGTH_SHORT).show()
            }
        } else {
            // TTS 初始化失败
            Toast.makeText(context, "TTS 初始化失败", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCleared() {
        tts.stop()
        tts.shutdown()
        if (INSTANCE === this) {
            synchronized(Companion) {
                if (INSTANCE === this) {
                    INSTANCE = null
                }
            }
        }
    }

    fun setPlayEnglish(value: Boolean) {
        if (!value && _playSpelling.value == false && _playChinese.value == false) {
            Toast.makeText(context, "至少保留一个选项", Toast.LENGTH_SHORT).show()
            throw IllegalArgumentException()
        }
        _playEnglish.value = value
    }

    fun setPlayChinese(value: Boolean) {
        if (!value && _playEnglish.value == false && _playSpelling.value == false) {
            Toast.makeText(context, "至少保留一个选项", Toast.LENGTH_SHORT).show()
            throw IllegalArgumentException()
        }
        _playChinese.value = value
    }

    fun setPlaySpelling(value: Boolean) {
        if (!value && _playEnglish.value == false && _playChinese.value == false) {
            Toast.makeText(context, "至少保留一个选项", Toast.LENGTH_SHORT).show()
            throw IllegalArgumentException()
        }
        _playSpelling.value = value
    }

    // 加载播放列表
    fun loadPlaylist(list: List<Word>, startIndex: Int = 0) {
        pausePlay()
        _playlist.value = list
        _currentIndex.value = startIndex
        _currentWord.value = list.getOrNull(startIndex)
    }

    @UiThread
    fun toggle() {
        if (_playing.value == true) {
            pausePlay()
        } else {
            startPlay(true)
        }
    }

    // 开始朗读播放列表中的所有单词
    @UiThread
    private fun startPlay(isContinuous: Boolean) {
        _playlist.value?.getOrNull(_currentIndex.value ?: 0)?.let {
            startSpeakWord(isContinuous)
            _playing.value = true
        }
    }

    // 朗读下一个单词
    private fun startSpeakWord(isContinuous: Boolean) {
        val words = _playlist.value ?: return
        val index = _currentIndex.value ?: 0
        val word = words.getOrNull(index) ?: return
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}

            override fun onDone(utteranceId: String?) {
                handler.post {
                    if (isContinuous) {
                        _currentIndex.value = index + 1
                        words.getOrNull(index + 1)?.let {
                            _currentWord.value = it
                            return@post startSpeakWord(true)
                        }
                    }
                    _playing.value = false
                }
            }

            override fun onError(utteranceId: String?) {}
        })
        speak(word)
    }

    // 暂停朗读
    @UiThread
    fun pausePlay() {
        _playing.value = false
        tts.stop()
    }

    @UiThread
    fun speakPreviousWord() {
        val words = _playlist.value ?: return
        val index = _currentIndex.value ?: 0
        tts.stop()
        if (index > 0) {
            _currentIndex.value = index - 1
            _currentWord.value = words.getOrNull(index - 1)
        }
        _playing.value = true
        startSpeakWord(true)
    }

    @UiThread
    fun speakNextWord() {
        val words = _playlist.value ?: return
        val index = _currentIndex.value ?: 0
        if (index in words.indices) {
            tts.stop()
            _currentIndex.value = index + 1
            _playing.value = words.getOrNull(index + 1)?.let {
                _currentWord.value = it
                startSpeakWord(true)
            } != null
        }
    }

    private fun speak(word: Word) {
        speak(ArrayList<String>().apply {
            if (_playEnglish.value == true) {
                add(word.word)
            }
            if (_playSpelling.value == true) {
                add(word.word.split("").joinToString(" "))
            }
            if (_playChinese.value == true) {
                add(word.meaning)
            }
        }.joinToString(" "))
    }

    @UiThread
    fun speak(text: String, flush: Boolean = false) {
        // 检查 TTS 是否初始化成功
        if (tts.isLanguageAvailable(Locale.CHINA) != TextToSpeech.LANG_AVAILABLE) {
            Toast.makeText(context, "TTS 未准备好", Toast.LENGTH_SHORT).show()
            return
        }
        if (flush) {
            pausePlay()
            // 朗读文本
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            // 或者使用更高级的参数
            tts.speak(
                text,
                TextToSpeech.QUEUE_ADD, // 添加到队列
                Bundle.EMPTY, // 参数
                "utteranceId" // 唯一标识符，用于回调
            )
        }
    }

    @UiThread
    fun seek(position: Int, isContinuous: Boolean? = null) {
        val words = _playlist.value ?: return
        val playing = _playing.value == true
        if (playing) {
            tts.stop()
        }
        _currentIndex.value = position
        words.getOrNull(position)?.let {
            _currentWord.value = it
            if (isContinuous != null) {
                startPlay(isContinuous)
            }
        }
    }

    companion object {
        private var refCount = 0
        private var INSTANCE: PlayerViewModel? = null

        fun Fragment.playerViewModels() = lazy {
            requireActivity().playerViewModels().value
        }

        fun ComponentActivity.playerViewModels() = lazy {
            synchronized(Companion) {
                ++refCount
                INSTANCE ?: PlayerViewModel(application).also { INSTANCE = it }
            }.also {
                lifecycle.addObserver(object : DefaultLifecycleObserver {
                    override fun onDestroy(owner: LifecycleOwner) {
                        owner.lifecycle.removeObserver(this)
                        synchronized(Companion) {
                            if (--refCount == 0) {
                                INSTANCE = null
                            }
                        }
                    }
                })
            }
        }
    }
}