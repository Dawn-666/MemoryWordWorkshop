package com.tencentmusic.memory.data

import com.tencentmusic.memory.R

object Database {
    // 模拟数据
    val playlist by lazy {
        @Suppress("SpellCheckingInspection")
        listOf(
            Word("abandon", "/əˈbændən/", "放弃，抛弃"),
            Word("beautiful", "/ˈbjuːtɪfəl/", "美丽的，漂亮的"),
            Word("challenge", "/tʃælɪndʒ/", "挑战，质疑"),
            Word("determine", "/dɪtɜːrmɪn/", "决定，确定"),
            Word("essential", "/ɪsenʃl/", "必要的，基本的")
        )
    }
    val playlists by lazy {
        @Suppress("SpellCheckingInspection")
        listOf(
            Playlist(
                name = "今日复习",
                words = listOf(
                    Word("abandon", "/əˈbændən/", "放弃，抛弃"),
                    Word("beautiful", "/ˈbjuːtɪfəl/", "美丽的，漂亮的"),
                    Word("challenge", "/ˈtʃælɪndʒ/", "挑战，质疑"),
                    Word("difficult", "/ˈdɪfɪkəlt/", "困难的，艰难的"),
                    Word("efficient", "/ɪˈfɪʃənt/", "高效的，有效的"),
                    Word("fascinate", "/ˈfæsɪneɪt/", "迷住，使着迷"),
                    Word("generous", "/ˈdʒenərəs/", "慷慨的，大方的"),
                    Word("harmony", "/ˈhɑːrməni/", "和谐，协调"),
                    Word("illustrate", "/ˈɪləstreɪt/", "说明，阐明"),
                    Word("journey", "/ˈdʒɜːrni/", "旅程，旅行"),
                    Word("knowledge", "/ˈnɒlɪdʒ/", "知识，学问"),
                    Word("language", "/ˈlæŋɡwɪdʒ/", "语言，言语")
                ),
                isCurrent = true
            ),
            Playlist(
                name = "我的收藏",
                words = listOf(
                    Word("essential", "/ɪˈsenʃəl/", "必要的，基本的"),
                    Word("fantastic", "/fænˈtæstɪk/", "极好的，了不起的"),
                    Word("grateful", "/ˈɡreɪtfəl/", "感激的，感谢的"),
                    Word("harmonious", "/hɑːrˈmoʊniəs/", "和谐的，协调的"),
                    Word("inspiring", "/ɪnˈspaɪərɪŋ/", "鼓舞人心的"),
                    Word("joyful", "/ˈdʒɔɪfəl/", "快乐的，令人愉快的"),
                    Word("kindness", "/ˈkaɪndnəs/", "仁慈，善良"),
                    Word("lovely", "/ˈlʌvli/", "可爱的，美好的"),
                    Word("magnificent", "/mæɡˈnɪfɪsənt/", "壮丽的，宏伟的"),
                    Word("noble", "/ˈnoʊbl/", "高尚的，崇高的"),
                    Word("optimistic", "/ˌɑːptɪˈmɪstɪk/", "乐观的"),
                    Word("peaceful", "/ˈpiːsfəl/", "和平的，安宁的")
                )
            ),
            Playlist(
                name = "怎么也记不住系列",
                words = listOf(
                    Word("ambiguous", "/æmˈbɪɡjuəs/", "模糊的，不明确的"),
                    Word("benevolent", "/bəˈnevələnt/", "仁慈的，慈善的"),
                    Word("conscientious", "/ˌkɒnʃiˈenʃəs/", "认真的，尽责的"),
                    Word("dilemma", "/dɪˈlemə/", "困境，进退两难"),
                    Word("exacerbate", "/ɪɡˈzæsərbeɪt/", "使恶化，加剧"),
                    Word("flabbergasted", "/ˈflæbərɡæstɪd/", "目瞪口呆的，大吃一惊的"),
                    Word("gregarious", "/ɡrɪˈɡeriəs/", "爱交际的，群居的"),
                    Word("hierarchy", "/ˈhaɪərɑːrki/", "等级制度，层级"),
                    Word("idiosyncrasy", "/ˌɪdiəˈsɪŋkrəsi/", "特质，癖好"),
                    Word("juxtaposition", "/ˌdʒʌkstəpəˈzɪʃn/", "并置，并列"),
                    Word("kaleidoscope", "/kəˈlaɪdəskoʊp/", "万花筒，变化多端"),
                    Word("labyrinthine", "/ˌlæbəˈrɪnθaɪn/", "迷宫般的，复杂的")
                )
            )
        )
    }

    val favorites by lazy {
        listOf("essential", "fantastic", "grateful", "harmonious", "inspiring", "joyful", "kindness", "lovely", "magnificent", "noble", "optimistic", "peaceful")
    }

    val books by lazy {
        listOf(
            WordBook(
                id = "1",
                title = "四级核心词汇",
                coverRes = R.drawable.ic_launcher_background,
                wordCount = 3000,
                learnedCount = 450,
                category = "考试词汇",
                difficulty = "中级",
                isStudying = true
            ),
            WordBook(
                id = "2",
                title = "雅思必备词汇",
                coverRes = R.drawable.ic_launcher_background,
                wordCount = 5000,
                learnedCount = 1200,
                category = "考试词汇",
                difficulty = "高级"
            ),
            WordBook(
                id = "3",
                title = "托福高频词汇",
                coverRes = R.drawable.ic_launcher_background,
                wordCount = 4000,
                learnedCount = 0,
                category = "考试词汇",
                difficulty = "高级"
            ),
            WordBook(
                id = "4",
                title = "考研英语词汇",
                coverRes = R.drawable.ic_launcher_background,
                wordCount = 5500,
                learnedCount = 2800,
                category = "考试词汇",
                difficulty = "高级"
            ),
            WordBook(
                id = "5",
                title = "日常生活词汇",
                coverRes = R.drawable.ic_launcher_background,
                wordCount = 2000,
                learnedCount = 1500,
                category = "生活词汇",
                difficulty = "初级",
                isStudying = true
            ),
            WordBook(
                id = "6",
                title = "商务英语词汇",
                coverRes = R.drawable.ic_launcher_background,
                wordCount = 2500,
                learnedCount = 2500,
                category = "专业词汇",
                difficulty = "中级"
            ),
            WordBook(
                id = "7",
                title = "医学英语词汇",
                coverRes = R.drawable.ic_launcher_background,
                wordCount = 3000,
                learnedCount = 800,
                category = "专业词汇",
                difficulty = "高级"
            ),
            WordBook(
                id = "8",
                title = "计算机英语词汇",
                coverRes = R.drawable.ic_launcher_background,
                wordCount = 1800,
                learnedCount = 0,
                category = "专业词汇",
                difficulty = "中级"
            ),
            WordBook(
                id = "9",
                title = "旅游英语词汇",
                coverRes = R.drawable.ic_launcher_background,
                wordCount = 1200,
                learnedCount = 1200,
                category = "生活词汇",
                difficulty = "初级"
            ),
            WordBook(
                id = "10",
                title = "美食英语词汇",
                coverRes = R.drawable.ic_launcher_background,
                wordCount = 800,
                learnedCount = 600,
                category = "生活词汇",
                difficulty = "初级"
            )
        )
    }
}