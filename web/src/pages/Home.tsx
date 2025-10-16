import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Word } from '../models/Word';
import { getWordsList, getLastMemorizedWord, getLearningProgress } from '../data/words';
import '../styles/Home.css';
import { speak } from '../utils';

function Home() {
  const [words, setWords] = useState<Word[]>([]);
  const [activeTab, setActiveTab] = useState<string>('today');
  const [lastMemorizedWord, setLastMemorizedWord] = useState<Word | undefined>(undefined);
  const [progress, setProgress] = useState<number>(0);
  const navigate = useNavigate();

  // 加载单词数据
  useEffect(() => {
    const wordsList = getWordsList();
    setWords(wordsList);
    
    // 获取学习进度
    const lastWord = getLastMemorizedWord();
    setLastMemorizedWord(lastWord);
    
    // 获取进度百分比
    const progressPercent = getLearningProgress();
    setProgress(progressPercent);
  }, []);

  // 跳转到单词详情页
  const goToWordDetail = (wordId: number) => {
    navigate(`/word/${wordId}`);
  };

  // 根据单词状态确定CSS类名
  const getWordCardClassName = (word: Word): string => {
    if (lastMemorizedWord && word.id === lastMemorizedWord.id) {
      return "word-card highlighted";
    }
    return word.isMemorized ? "word-card memorized" : "word-card";
  };

  // 过滤单词列表
  const getFilteredWords = (): Word[] => {
    switch (activeTab) {
      case 'today':
        return words;
      case 'review':
        // 假设"复习昨日"显示已记住的单词
        return words.filter(word => word.isMemorized);
      case 'collected':
        // 假设"我的收藏"也显示已记住的单词
        return words.filter(word => word.isMemorized);
      default:
        return words;
    }
  };

  const filteredWords = getFilteredWords();

  const clickSpeak = (event: React.MouseEvent<HTMLSpanElement>, text: string) => {
    event.stopPropagation();
    speak(text);
  };

  return (
    <div className="home-container">
      <header className="home-header">
        <h1>我的单词库</h1>
        
        <div className="search-bar">
          <div className="search-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <circle cx="11" cy="11" r="8"></circle>
              <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
            </svg>
          </div>
          <input type="text" placeholder="搜索单词..." />
        </div>
        
        <div className="tab-container">
          <button 
            className={`tab-button ${activeTab === 'today' ? 'active' : ''}`}
            onClick={() => setActiveTab('today')}
          >
            今日新学
          </button>
          <button 
            className={`tab-button ${activeTab === 'review' ? 'active' : ''}`}
            onClick={() => setActiveTab('review')}
          >
            复习昨日
          </button>
          <button 
            className={`tab-button ${activeTab === 'collected' ? 'active' : ''}`}
            onClick={() => setActiveTab('collected')}
          >
            我的收藏
          </button>
        </div>
      </header>

      {progress > 0 && (
        <div className="progress-bar">
          <div className="progress-fill" style={{ width: `${progress}%` }}></div>
          <span className="progress-text">{Math.round(progress)}%</span>
        </div>
      )}

      <section className="word-list-section">
        <div className="word-list">
          {filteredWords.map(word => (
            <div 
              key={word.id} 
              className={getWordCardClassName(word)}
              onClick={() => goToWordDetail(word.id)}
            >
              <h3>{word.text}</h3>
              <p className="word-translation">
                <span>{word.definitions[0].meaning}</span>
                <span onClick={(e) => clickSpeak(e, word.text)} style={{ marginLeft: '0.8rem' }}>🔉</span>
              </p>
              <p className="word-example">{word.example}</p>
              {word.isMemorized && (
                <div className="star-icon">★</div>
              )}
            </div>
          ))}
        </div>
      </section>
    </div>
  );
}

export default Home;