import { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';                     
import { Word } from '../models/Word';
import { ImageRecord, ImageType } from '../models/Image';
import { getWordById } from '../data/words';
import { 
  getWordImages, 
  getActiveImage, 
  addImage, 
  setActiveImage
} from '../data/images';
import '../styles/EditImageMemory.css';

function EditImageMemory() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [word, setWord] = useState<Word | null>(null);
  const [wordImages, setWordImages] = useState<ImageRecord[]>([]);
  const [activeImageId, setActiveImageId] = useState<string | null>(null);
  const [imageUrl, setImageUrl] = useState('');
  const [imageDescription, setImageDescription] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [previewImage, setPreviewImage] = useState<string | null>(null);
  const [uploadMethod, setUploadMethod] = useState<'url' | 'upload' | 'ai'>('url');
  const fileInputRef = useRef<HTMLInputElement>(null);

  // 加载单词和图片数据
  useEffect(() => {
    if (id) {
      const wordId = parseInt(id);
      const foundWord = getWordById(wordId);
      if (foundWord) {
        setWord(foundWord);
        
        // 加载图片数据
        const images = getWordImages(wordId);
        setWordImages(images.images);
        setActiveImageId(images.activeImageId || null);
        
        // 设置当前激活图片为预览
        const activeImage = getActiveImage(wordId);
        if (activeImage) {
          setPreviewImage(activeImage.url);
        }
      }
    }
  }, [id]);

  const handleBack = () => {
    navigate(`/word/${id}`);
  };

  // 处理URL输入
  const handleUrlChange = (url: string) => {
    setImageUrl(url);
    if (url && isValidUrl(url)) {
      setPreviewImage(url);
    } else {
      setPreviewImage(null);
    }
  };

  // 验证URL格式
  const isValidUrl = (string: string) => {
    try {
      new URL(string);
      return string.startsWith('http://') || string.startsWith('https://');
    } catch (_) {
      return false;
    }
  };

  // 处理文件上传
  const handleFileUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      if (file.type.startsWith('image/')) {
        if (file.size > 5 * 1024 * 1024) { // 5MB限制
          alert('文件大小不能超过5MB');
          return;
        }

        const reader = new FileReader();
        reader.onload = (e) => {
          const result = e.target?.result as string;
          setPreviewImage(result);
          handleAddUploadImage(result, file.name, file.size);
        };
        reader.readAsDataURL(file);
      } else {
        alert('请选择图片文件');
      }
    }
    
    // 清空input值，允许重复选择同一文件
    event.target.value = '';
  };

  // 生成AI图片（模拟功能）
  const generateAIImage = async () => {
    if (!word || !imageDescription.trim()) {
      alert('请输入图片描述');
      return;
    }

    setIsLoading(true);
    try {
      // 这里模拟AI图片生成，实际项目中需要调用AI API
      await new Promise(resolve => setTimeout(resolve, 2000));

      // 模拟生成的图片URL
      let mockImageUrl = `https://picsum.photos/400/300?random=${Date.now()}`;
      // 作弊一下，根据描述关键词返回特定图片
      if (imageDescription.startsWith('荒草丛生')) {
        mockImageUrl = 'https://images.unsplash.com/photo-1556534397-927835f4e7bd';
      }
      // 添加到图片记录
      const newImage = addImage(
        word.id,
        ImageType.USER_AI,
        mockImageUrl,
        imageDescription.trim(),
        { aiPrompt: imageDescription.trim() }
      );
      
      // 设置为激活图片
      const success = setActiveImage(word.id, newImage.id);
      if (success) {
        // 更新本地状态
        setWordImages(prev => [...prev]);
        setActiveImageId(newImage.id);
        setPreviewImage(mockImageUrl);
        
        // 清空输入框
        setImageDescription('');
      }
      
    } catch (error) {
      console.error('生成图片失败:', error);
      alert('生成图片失败，请重试');
    } finally {
      setIsLoading(false);
    }
  };

  // 添加URL图片
  const handleAddUrlImage = () => {
    if (!word || !imageUrl.trim() || !isValidUrl(imageUrl)) {
      alert('请输入有效的图片URL');
      return;
    }

    const newImage = addImage(
      word.id,
      ImageType.USER_URL,
      imageUrl.trim(),
      '网络图片',
      { source: 'URL' }
    );

    // 设置为激活图片
    const success = setActiveImage(word.id, newImage.id);
    if (success) {
      setWordImages(prev => [...prev]);
      setActiveImageId(newImage.id);
      setPreviewImage(imageUrl.trim());
      setImageUrl('');
    }
  };

  // 添加上传图片
  const handleAddUploadImage = (dataUrl: string, fileName: string, size: number) => {
    if (!word) return;

    const newImage = addImage(
      word.id,
      ImageType.USER_UPLOAD,
      dataUrl,
      '上传图片',
      { originalName: fileName, size }
    );

    // 设置为激活图片
    const success = setActiveImage(word.id, newImage.id);
    if (success) {
      setWordImages(prev => [...prev]);
      setActiveImageId(newImage.id);
      setPreviewImage(dataUrl);
    }
  };

  // 设置激活图片
  const handleSetActive = (imageId: string) => {
    if (!word) return;

    const success = setActiveImage(word.id, imageId);
    if (success) {
      setActiveImageId(imageId);
      const targetImage = wordImages.find(img => img.id === imageId);
      if (targetImage) {
        setPreviewImage(targetImage.url);
        
        // 信息反显到添加新图片区域
        switch (targetImage.type) {
          case ImageType.USER_AI:
            setUploadMethod('ai');
            setImageDescription(targetImage.metadata?.aiPrompt || targetImage.description || '');
            setImageUrl('');
            break;
          case ImageType.USER_URL:
            setUploadMethod('url');
            setImageUrl(targetImage.url);
            setImageDescription('');
            break;
          case ImageType.USER_UPLOAD:
          case ImageType.SYSTEM:
            // 系统图片不反显，保持当前状态
            break;
        }
      }
    }
  };

  // 保存并返回
  const handleSave = () => {
    navigate(`/word/${id}`);
  };

  if (!word) {
    return <div className="loading">加载中...</div>;
  }

  return (
    <div className="edit-image-container">
      <header className="edit-image-header">
        <div className="header-nav">
          <button className="back-button" onClick={handleBack}>
            <svg viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" width="24" height="24">
              <path d="M189.2 552.9l262.3 262.3c22.6 22.6 59.2 22.6 81.7 0 22.6-22.5 22.6-59.1 0-81.7L369.6 569.8h424.3c31.9 0 57.8-25.9 57.8-57.8s-25.9-57.8-57.8-57.8H369.6l163.7-163.7c22.6-22.5 22.6-59.2 0-81.7-11.3-11.3-26.1-16.9-40.9-16.9-14.8 0-29.6 5.7-40.8 16.9L189.2 471.2c-22.6 22.5-22.6 59.1 0 81.7m0 0z"></path>
            </svg>
          </button>
          <h2 className="header-title">编辑图片记忆</h2>
          <button className="save-button-header" onClick={handleSave}>
            保存
          </button>
        </div>
      </header>

      <div className="edit-image-content">
        {/* 单词信息 */}
        <div className="word-info">
          <h1 className="word-text">{word.text}</h1>
          <p className="word-phonetic">{word.phonetic}</p>
        </div>

        {/* 当前激活图片预览 */}
        <div className="preview-section">
          <h3 className="section-title">当前图片</h3>
          <div className="preview-container">
            {previewImage ? (
              <img 
                src={previewImage} 
                alt="当前激活图片" 
                className="preview-image"
                onError={() => setPreviewImage(null)}
              />
            ) : (
              <div className="preview-placeholder">
                <div className="placeholder-icon">🖼️</div>
                <p>暂无图片</p>
              </div>
            )}
          </div>
        </div>

        {/* 添加新图片区域 */}
        <div className="input-section">
          <h3 className="section-title">添加新图片</h3>
          {/* 方法选择 */}
          <div className="method-selector">
            <button 
              className={`method-btn ${uploadMethod === 'url' ? 'active' : ''}`}
              onClick={() => setUploadMethod('url')}
            >
              🔗 网络图片
            </button>
            <button 
              className={`method-btn ${uploadMethod === 'upload' ? 'active' : ''}`}
              onClick={() => setUploadMethod('upload')}
            >
              📁 上传图片
            </button>
            <button 
              className={`method-btn ${uploadMethod === 'ai' ? 'active' : ''}`}
              onClick={() => setUploadMethod('ai')}
            >
              🎨 AI生成
            </button>
          </div>

          {uploadMethod === 'url' && (
            <div className="url-input">
              <div className="input-with-button">
                <input
                  id="imageUrl"
                  type="url"
                  value={imageUrl}
                  onChange={(e) => handleUrlChange(e.target.value)}
                  placeholder="请输入图片URL地址"
                  className="url-field"
                />
                <button 
                  className="add-btn"
                  onClick={handleAddUrlImage}
                  disabled={!imageUrl.trim() || !isValidUrl(imageUrl)}
                >
                  ➕ 添加
                </button>
              </div>
              <p className="input-hint">支持 jpg, png, gif 等格式</p>
            </div>
          )}

          {uploadMethod === 'upload' && (
            <div className="upload-input">
              <input
                ref={fileInputRef}
                type="file"
                accept="image/*"
                onChange={handleFileUpload}
                style={{ display: 'none' }}
              />
              <button 
                className="upload-btn"
                onClick={() => fileInputRef.current?.click()}
              >
                📁 选择并添加图片文件
              </button>
              <p className="input-hint">支持 jpg, png, gif 等格式，最大 5MB</p>
            </div>
          )}

          {uploadMethod === 'ai' && (
            <div className="ai-input">
              <textarea
                id="imageDescription"
                value={imageDescription}
                onChange={(e) => setImageDescription(e.target.value)}
                placeholder="描述你想要的图片，例如：一只可爱的小猫在花园里玩耍"
                className="description-field"
                rows={4}
              />
              <button 
                className="generate-btn"
                onClick={generateAIImage}
                disabled={isLoading || !imageDescription.trim()}
              >
                {isLoading ? '🔄 生成中...' : '🎨 生成并添加图片'}
              </button>
              <p className="input-hint">用中文描述你想要的图片场景</p>
            </div>
          )}
        </div>

        {/* 所有图片列表 */}
        <div className="images-gallery">
          <h3 className="section-title">图片库</h3>
          <div className="images-grid">
            {wordImages.map((image) => (
              <div 
                key={image.id} 
                className={`image-card ${image.id === activeImageId ? 'active' : ''}`}
                onClick={() => handleSetActive(image.id)}
              >
                <div className="image-wrapper">
                  <img 
                    src={image.url} 
                    alt={image.description || '图片'} 
                    className="gallery-image"
                    onError={(e) => {
                      (e.target as HTMLImageElement).src = 'https://via.placeholder.com/150x100?text=Error';
                    }}
                  />
                  {image.id === activeImageId && (
                    <div className="active-indicator">
                      ✓
                    </div>
                  )}
                </div>
                <div className="image-info">
                  <div className="image-type">
                    {image.type === ImageType.SYSTEM && '🏠 系统默认'}
                    {image.type === ImageType.USER_UPLOAD && '📁 用户上传'}
                    {image.type === ImageType.USER_AI && '🎨 AI生成'}
                    {image.type === ImageType.USER_URL && '🔗 网络图片'}
                  </div>
                  <div className="image-date">
                    {image.createdAt.toLocaleDateString()}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>



        {/* 使用提示 */}
        <div className="tips-section">
          <h3>💡 使用提示</h3>
          <ul>
            <li>每个单词都有系统默认图片，你可以添加更多个性化图片</li>
            <li>点击图片可以设置为当前激活图片</li>
            <li>支持链接、本地上传和AI生成三种方式</li>
          </ul>
        </div>
      </div>
    </div>
  );
}

export default EditImageMemory;