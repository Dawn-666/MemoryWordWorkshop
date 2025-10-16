import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';       
import { Word, Definition } from '../models/Word';
import { 
  getWordById, 
  updateWordMemorizedStatus, 
  getNextWord,
  updateCustomDefinition,
  updateWordDefinitions
} from '../data/words';
import { getLyricsMemoryByWordId } from '../data/lyricsMemory';
import '../styles/WordDetail.css';
import { speak } from '../utils';

function WordDetail() {
  const { id } = useParams<{ id: string }>();
  const [word, setWord] = useState<Word | null>(null);
  const [nextWord, setNextWord] = useState<Word | null>(null);
  const [isCollected, setIsCollected] = useState(false);
  const [customDefinition, setCustomDefinition] = useState('');
  const [isEditing, setIsEditing] = useState({
    customDefinition: false,
    imageMemory: false
  });
  const [definitions, setDefinitions] = useState<Definition[]>([]);
  const [draggedIndex, setDraggedIndex] = useState<number | null>(null);
  const [dragOverIndex, setDragOverIndex] = useState<number | null>(null);
  const [isDragging, setIsDragging] = useState(false);
  const [touchStartY, setTouchStartY] = useState<number | null>(null);
  const [dragStartTime, setDragStartTime] = useState<number | null>(null);
  
  // 歌词记忆相关状态
  const [showLyricsCard, setShowLyricsCard] = useState(false);
  const [cardTouchStart, setCardTouchStart] = useState<{ x: number; y: number } | null>(null);
  const [cardTransform, setCardTransform] = useState(0);
  const [isCardSwiping, setIsCardSwiping] = useState(false);
  
  const navigate = useNavigate();

  // 加载单词数据
  useEffect(() => {
    if (id) {
      const wordId = parseInt(id);
      const foundWord = getWordById(wordId);
      if (foundWord) {
        setWord(foundWord);
        setDefinitions(foundWord.definitions || []);
        setCustomDefinition(foundWord.customDefinition || '');
        setIsCollected(foundWord.isCollected || false);
        
        // 获取下一个单词
        const next = getNextWord(wordId);
        if (next) {
          setNextWord(next);
        }
      }
    }
  }, [id]);

  const handleBack = () => {
    navigate('/');
  };
  
  // 导航到下一个单词
  const goToNextWord = () => {
    if (nextWord) {
      navigate(`/word/${nextWord.id}`);
    }
  };

  // 切换单词记忆状态
  const toggleMemorized = () => {
    if (word) {
      const newStatus = !word.isMemorized;
      const updatedWord = updateWordMemorizedStatus(word, newStatus);
      if (updatedWord) {
        setWord(updatedWord);
      }
    }
  };

  // 切换收藏状态（仅UI效果）
  const toggleCollected = () => {
    setIsCollected(!isCollected);
  };

  // 保存自定义释义
  const saveCustomDefinition = () => {
    if (word && customDefinition !== word.customDefinition) {
      const updatedWord = updateCustomDefinition(word, customDefinition);
      if (updatedWord) {
        setWord(updatedWord);
      }
    }
    setIsEditing({...isEditing, customDefinition: false});
  };

  // 保存图片记忆
  // const saveImageMemory = () => {
  //   if (word && imageMemory !== word.imageMemory) {
  //     const updatedWord = updateImageMemory(word.id, imageMemory);
  //     if (updatedWord) {
  //       setWord(updatedWord);
  //     }
  //   }
  //   setIsEditing({...isEditing, imageMemory: false});
  // };

  // 防止拖拽时页面滚动
  useEffect(() => {
    const preventScroll = (e: any) => {
      if (isDragging) {
        e.preventDefault();
        e.stopPropagation();
      }
    };

    const preventScrollWheel = (e: WheelEvent) => {
      if (isDragging) {
        e.preventDefault();
      }
    };

    if (isDragging) {
      // 添加多种事件监听器来防止滚动
      document.addEventListener('touchmove', preventScroll, { passive: false });
      document.addEventListener('wheel', preventScrollWheel, { passive: false });
      document.addEventListener('scroll', preventScroll, { passive: false });
    } else {
      // 移除事件监听器
      document.removeEventListener('touchmove', preventScroll);
      document.removeEventListener('wheel', preventScrollWheel);
      document.removeEventListener('scroll', preventScroll);
    }

    return () => {
      // 清理函数
      document.removeEventListener('touchmove', preventScroll);
      document.removeEventListener('wheel', preventScrollWheel);
      document.removeEventListener('scroll', preventScroll);
    };
  }, [isDragging]);

  // 拖拽排序相关函数
  const handleDragStart = (e: React.DragEvent, index: number) => {
    setDraggedIndex(index);
    setIsDragging(true);
    e.dataTransfer.effectAllowed = 'move';
    e.dataTransfer.setData('text/html', e.currentTarget.outerHTML);
    e.dataTransfer.setDragImage(e.currentTarget as Element, 0, 0);
  };

  const handleDragOver = (e: React.DragEvent, index: number) => {
    e.preventDefault();
    e.dataTransfer.dropEffect = 'move';
    setDragOverIndex(index);
  };

  const handleDragLeave = () => {
    setDragOverIndex(null);
  };

  const handleDrop = (e: React.DragEvent, dropIndex: number) => {
    e.preventDefault();
    
    if (draggedIndex === null || draggedIndex === dropIndex) {
      resetDragState();
      return;
    }

    reorderDefinitions(draggedIndex, dropIndex);
    resetDragState();
  };

  const handleDragEnd = () => {
    resetDragState();
  };

  // 重置拖拽状态
  const resetDragState = () => {
    setDraggedIndex(null);
    setDragOverIndex(null);
    setIsDragging(false);
    setTouchStartY(null);
    setDragStartTime(null);
  };

  // 重新排序定义
  const reorderDefinitions = (fromIndex: number, toIndex: number) => {
    const newDefinitions = [...definitions];
    const draggedItem = newDefinitions[fromIndex];
    
    // 移除被拖拽的项目
    newDefinitions.splice(fromIndex, 1);
    // 在新位置插入
    newDefinitions.splice(toIndex, 0, draggedItem);
    
    // 更新order字段
    const updatedDefinitions = newDefinitions.map((def, index) => ({
      ...def,
      order: index + 1
    }));
    
    setDefinitions(updatedDefinitions);
    
    // 保存到数据源
    if (word) {
      const updatedWord = updateWordDefinitions(word, updatedDefinitions);
      if (updatedWord) {
        setWord(updatedWord);
      }
    }
  };

  // 触摸设备支持 - 改进版本
  const handleTouchStart = (e: React.TouchEvent, index: number) => {
    // 阻止默认行为，但不阻止事件冒泡（允许其他触摸事件）
    e.preventDefault();
    
    const touch = e.touches[0];
    setTouchStartY(touch.clientY);
    setDragStartTime(Date.now());
    
    // 立即设置拖拽状态，但延迟启动实际拖拽
    setDraggedIndex(index);
    
    // 短暂延迟后启动拖拽模式
    setTimeout(() => {
      if (draggedIndex === index && dragStartTime && Date.now() - dragStartTime >= 150) {
        setIsDragging(true);
      }
    }, 150);
  };

  const handleTouchMove = (e: React.TouchEvent) => {
    // 如果还没开始拖拽，检查是否应该取消拖拽（用户在滚动）
    if (!isDragging && touchStartY !== null && draggedIndex !== null) {
      const touch = e.touches[0];
      const deltaY = Math.abs(touch.clientY - touchStartY);
      const deltaX = Math.abs(touch.clientX - (e.currentTarget as HTMLElement).getBoundingClientRect().left);
      
      // 如果垂直移动距离大于水平移动距离，认为是滚动操作
      if (deltaY > 15 && deltaY > deltaX) {
        resetDragState();
        return;
      }
      
      // 如果移动距离足够，启动拖拽
      if (deltaX > 10 || deltaY > 10) {
        setIsDragging(true);
      }
    }

    if (!isDragging || draggedIndex === null) {
      return;
    }

    // 拖拽进行中，阻止所有默认行为
    e.preventDefault();
    e.stopPropagation();
    
    const touch = e.touches[0];
    const elementBelow = document.elementFromPoint(touch.clientX, touch.clientY);
    const definitionItem = elementBelow?.closest('.definition-item');
    
    if (definitionItem) {
      const index = parseInt(definitionItem.getAttribute('data-index') || '0');
      if (index !== draggedIndex) {
        setDragOverIndex(index);
      }
    } else {
      setDragOverIndex(null);
    }
  };

  const handleTouchEnd = (e: React.TouchEvent) => {
    // 如果没有进入拖拽模式，直接重置
    if (!isDragging) {
      resetDragState();
      return;
    }

    e.preventDefault();
    e.stopPropagation();
    
    // 执行重排序
    if (dragOverIndex !== null && dragOverIndex !== draggedIndex && draggedIndex !== null) {
      reorderDefinitions(draggedIndex, dragOverIndex);
    }
    
    resetDragState();
  };

  // 处理拖拽手柄的鼠标/触摸事件
  const handleDragHandleMouseDown = (e: React.MouseEvent, index: number) => {
    e.preventDefault();
    e.stopPropagation();
    // 桌面端直接启动拖拽
    setDraggedIndex(index);
    setIsDragging(true);
  };

  const handleDragHandleTouchStart = (e: React.TouchEvent, index: number) => {
    e.stopPropagation();
    // 触摸手柄时立即启动拖拽模式
    const touch = e.touches[0];
    setTouchStartY(touch.clientY);
    setDragStartTime(Date.now());
    setDraggedIndex(index);
    setIsDragging(true);
  };

  // 卡片滑动处理函数
  const handleCardTouchStart = (e: React.TouchEvent) => {
    const touch = e.touches[0];
    setCardTouchStart({ x: touch.clientX, y: touch.clientY });
    setIsCardSwiping(false);
  };

  const handleCardTouchMove = (e: React.TouchEvent) => {
    if (!cardTouchStart) return;
    
    const touch = e.touches[0];
    const deltaX = touch.clientX - cardTouchStart.x;
    const deltaY = touch.clientY - cardTouchStart.y;
    
    // 检查是否为水平滑动
    if (Math.abs(deltaX) > Math.abs(deltaY) && Math.abs(deltaX) > 10) {
      setIsCardSwiping(true);
      e.preventDefault();
      
      if (!showLyricsCard && deltaX < 0) {
        // 右滑显示歌词卡片
        setCardTransform(Math.max(deltaX, -300));
      } else if (showLyricsCard && deltaX > 0) {
        // 左滑隐藏歌词卡片
        setCardTransform(Math.min(deltaX, 300));
      }
    }
  };

  const handleCardTouchEnd = () => {
    if (!isCardSwiping) {
      setCardTouchStart(null);
      return;
    }
    
    const threshold = 100;
    
    if (!showLyricsCard && cardTransform < -threshold) {
      // 切换到歌词卡片
      setShowLyricsCard(true);
      playLyrics();
    } else if (showLyricsCard && cardTransform > threshold) {
      // 切换回单词卡片
      setShowLyricsCard(false);
    }
    
    setCardTransform(0);
    setCardTouchStart(null);
    setIsCardSwiping(false);
  };

  // 播放歌词
  const playLyrics = () => {
    if (word) {
      const audio = new Audio('/abandon.m4a');
      audio.play().catch(error => {
        console.error('音频播放失败:', error);
      });
    }
  };

  // 渲染歌词卡片
  const renderLyricsCard = () => {
    if (!word) return null;
    
    const lyricsMemory = getLyricsMemoryByWordId(word.id);
    
    if (!lyricsMemory) {
      return (
        <div className="lyrics-content">
          <h2 className="lyrics-title">歌词记忆</h2>
          <p className="lyrics-text no-lyrics">暂无歌词记忆</p>
          <div className="lyrics-actions">
            <button className="action-button" onClick={() => setShowLyricsCard(false)}>
              <span className="action-icon">←</span>
              返回单词
            </button>
          </div>
        </div>
      );
    }
    
    return (
      <div className="lyrics-content">
        <h2 className="lyrics-title">歌词记忆</h2>
        <p className="lyrics-text">{lyricsMemory.lyric}</p>
        <p>New Divide - Linkin Park</p>
      </div>
    );
  };

  if (!word) {
    return <div className="loading">加载中...</div>;
  }

  return (
    <div className="word-detail-container">
      <div className="word-detail-content">

        <div className="word-detail-content-sticky">
          <header className="word-detail-header">
            <div className="header-nav">
              <button className="back-button" onClick={handleBack}>
                <svg viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="3470" width="24" height="24"><path d="M600.8 659.1s0-0.1 0 0c-0.2-0.1-0.2-0.1 0 0m0 0z" p-id="3471"></path><path d="M189.2 552.9l262.3 262.3c22.6 22.6 59.2 22.6 81.7 0 22.6-22.5 22.6-59.1 0-81.7L369.6 569.8h424.3c31.9 0 57.8-25.9 57.8-57.8s-25.9-57.8-57.8-57.8H369.6l163.7-163.7c22.6-22.5 22.6-59.2 0-81.7-11.3-11.3-26.1-16.9-40.9-16.9-14.8 0-29.6 5.7-40.8 16.9L189.2 471.2c-22.6 22.5-22.6 59.1 0 81.7m0 0z" p-id="3472"></path></svg>
              </button>
              <h2 className="header-title">单词详情</h2>
              <button className="next-button" onClick={goToNextWord}>
                <svg viewBox="0 0 1346 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1555" width="20" height="20"><path d="M253.157209 626.902326C173.261395 579.048186 75.219349 449.952744 34.437953 294.554791-6.355349 139.156837 0.407814 0 0.407814 0 0.407814 0 53.215256 85.694512 74.624 115.735814 96.032744 145.777116 186.251907 257.702698 326.28986 322.571907 466.327814 387.441116 700.395163 375.879442 700.395163 375.879442L700.395163 105.757767 1345.503256 564.878884 700.395163 1024 700.395163 751.389767C700.395163 751.389767 545.42586 739.673302 464.422698 722.813023 327.992558 694.426791 253.157209 626.902326 253.157209 626.902326L253.157209 626.902326Z" p-id="1556"></path></svg>
              </button>
            </div>
          </header>
          <div className="word-card-container">
            <div 
              className={`word-card-detail ${showLyricsCard ? 'card-hidden' : ''}`}
              style={{ transform: `translateX(${cardTransform}px)` }}
              onTouchStart={handleCardTouchStart}
              onTouchMove={handleCardTouchMove}
              onTouchEnd={handleCardTouchEnd}
            >
              <h1 className="word-text">{word.text}</h1>
              <p className="phonetic">{word.phonetic}</p>
              
              <div className="word-actions">
                <button className="action-button speak-btn" onClick={() => speak(word.text)}>
                  <span className="action-icon">🔊</span>
                  发音
                </button>
                <button 
                  className={`action-button collect-btn ${isCollected ? 'active' : ''}`}
                  onClick={toggleCollected}
                >
                  <span className="action-icon">★</span>
                  收藏
                </button>
              </div>
            </div>
            
            {/* 歌词记忆卡片 */}
            <div>
              <div 
                className={`lyrics-card ${showLyricsCard ? 'card-visible' : ''}`}
                style={{ 
                  transform: `translateX(${showLyricsCard ? cardTransform : '100%'})`,
                  backgroundImage: word ? (() => {
                    const lyricsMemory = getLyricsMemoryByWordId(word.id);
                    return lyricsMemory?.background ? `url(${lyricsMemory.background})` : 'linear-gradient(135deg, #ff6b6b, #ff8e8e)';
                  })() : 'linear-gradient(135deg, #ff6b6b, #ff8e8e)'
                }}
                onTouchStart={handleCardTouchStart}
                onTouchMove={handleCardTouchMove}
                onTouchEnd={handleCardTouchEnd}
              >
                {renderLyricsCard()}
              </div>
              {showLyricsCard ? <p className="lyrics-tip">*歌词为AI根据您上传的歌单分析音乐偏好匹配而来</p> : null}
            </div>
          </div>
        </div>

        <div className="content-container">
          {/* 词典释义 */}
          <section className="dictionary-section">
            <h3 className="section-title">
              词典释义 
              <span className="hint">*拖拽排序，优先展示重要释义</span>
            </h3>
            <div className="definitions-list">
              {definitions.map((def, index) => (
                <div 
                  key={`${def.order}-${index}`}
                  className={`definition-item ${
                    draggedIndex === index ? 'dragging' : ''
                  } ${
                    dragOverIndex === index ? 'drag-over' : ''
                  }`}
                  data-index={index}
                  draggable
                  onDragStart={(e) => handleDragStart(e, index)}
                  onDragOver={(e) => handleDragOver(e, index)}
                  onDragLeave={handleDragLeave}
                  onDrop={(e) => handleDrop(e, index)}
                  onDragEnd={handleDragEnd}
                  onTouchStart={(e) => handleTouchStart(e, index)}
                  onTouchMove={handleTouchMove}
                  onTouchEnd={handleTouchEnd}
                >
                  <span 
                    className="drag-handle"
                    onMouseDown={(e) => handleDragHandleMouseDown(e, index)}
                    onTouchStart={(e) => handleDragHandleTouchStart(e, index)}
                  >
                    ⋮⋮
                  </span>
                  <span className="definition-number">{index + 1}.</span>
                  <span className="definition-text">{def.meaning}</span>
                </div>
              ))}
            </div>
          </section>

          {/* 我的释义 */}
          <section className="custom-section">
            <div className="section-header">
              <h3 className="section-title">我的释义</h3>
              <button 
                className="edit-button"
                onClick={() => setIsEditing({...isEditing, customDefinition: true})}
              >
                编辑
              </button>
            </div>
            
            {isEditing.customDefinition ? (
              <div className="edit-area">
                <textarea 
                  value={customDefinition} 
                  onChange={(e) => setCustomDefinition(e.target.value)}
                  placeholder="添加你自己的释义..."
                  rows={3}
                />
                <button className="save-button" onClick={saveCustomDefinition}>保存</button>
              </div>
            ) : (
              <div className="custom-content">
                {word.customDefinition || <span className="placeholder">添加你自己的释义...</span>}
              </div>
            )}
          </section>

          {/* 图片记忆 */}
          <section className="custom-section">
            <div className="section-header">
              <h3 className="section-title">图片记忆</h3>
              <button 
                className="edit-button"
                onClick={() => navigate(`/word/${id}/editImage`)}
              >
                🎨 编辑
              </button>
            </div>
            
            <div className="image-memory">
              {word.imageMemory ? (
                word.imageMemory.startsWith('http') ? (
                  <img src={word.imageMemory} alt="记忆图片" className="memory-image" />
                ) : (
                  <div className="image-description">
                    <div className="description-icon">📝</div>
                    <p className="description-text">{word.imageMemory}</p>
                  </div>
                )
              ) : (
                <div className="placeholder-container" onClick={() => navigate(`/word/${id}/editImage`)}>
                  <div className="placeholder-icon">🖼️</div>
                  <div className="placeholder">点击添加图片记忆</div>
                  <div className="placeholder-hint">支持网络图片、本地上传或AI生成</div>
                </div>
              )}
            </div>
          </section>

          <footer className="memorize-section">
            <button 
              className={`memorize-button ${word.isMemorized ? 'memorized' : ''}`}
              onClick={toggleMemorized}
            >
              {word.isMemorized ? '已记住' : '标记为已记住'}
            </button>
          </footer>
        </div>
      </div>
    </div>
  );
}

export default WordDetail;