import { ImageRecord, ImageType, WordImages, ImageCollection } from '../models/Image';
 
// 本地存储键名
const IMAGES_STORAGE_KEY = 'memory_workshop_images';

// 系统默认图片数据 - 使用Unsplash的相关主题图片
const SYSTEM_DEFAULT_IMAGES: { [wordId: number]: string } = {
  1: 'https://images.unsplash.com/photo-1544027993-37dbfe43562a?w=400&h=300&fit=crop', // abandon - 废弃的地方
  2: 'https://plus.unsplash.com/premium_photo-1667520490279-b5f0a4608235', // serendipity - 意外发现
  3: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=300&fit=crop', // eloquent - 演讲
  4: 'https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?w=400&h=300&fit=crop', // nostalgia - 怀旧
  5: 'https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c?w=400&h=300&fit=crop', // ubiquitous - 无处不在的手机
};

// 生成图片ID
const generateImageId = (): string => {
  return `img_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
};

// 创建系统默认图片记录
const createSystemImage = (wordId: number): ImageRecord => {
  const defaultUrl = SYSTEM_DEFAULT_IMAGES[wordId] || 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400&h=300&fit=crop';
  
  return {
    id: `system_${wordId}`,
    wordId,
    type: ImageType.SYSTEM,
    url: defaultUrl,
    description: '系统默认图片',
    createdAt: new Date('2024-01-01'), // 系统图片使用固定时间
    isActive: true, // 默认激活系统图片
    metadata: {}
  };
};

// 从本地存储加载图片数据
const loadImagesFromStorage = (): ImageCollection => {
  try {
    const savedImages = localStorage.getItem(IMAGES_STORAGE_KEY);
    if (savedImages) {
      const parsedImages = JSON.parse(savedImages) as ImageCollection;
      // 恢复日期对象
      Object.values(parsedImages).forEach((wordImages: WordImages) => {
        wordImages.images.forEach(image => {
          image.createdAt = new Date(image.createdAt);
        });
      });
      return parsedImages;
    }
  } catch (error) {
    console.error('Failed to load images from storage:', error);
  }
  return {};
};

// 保存图片数据到本地存储
const saveImagesToStorage = (images: ImageCollection): void => {
  try {
    localStorage.setItem(IMAGES_STORAGE_KEY, JSON.stringify(images));
  } catch (error) {
    console.error('Failed to save images to storage:', error);
  }
};

// 图片数据集合
let IMAGES_COLLECTION: ImageCollection = loadImagesFromStorage();

// 初始化单词的图片数据（确保每个单词都有系统默认图片）
const initializeWordImages = (wordId: number): WordImages => {
  if (!IMAGES_COLLECTION[wordId]) {
    const systemImage = createSystemImage(wordId);
    IMAGES_COLLECTION[wordId] = {
      wordId,
      images: [systemImage],
      activeImageId: systemImage.id
    };
    saveImagesToStorage(IMAGES_COLLECTION);
  }
  return IMAGES_COLLECTION[wordId];
};

// 获取单词的所有图片
export const getWordImages = (wordId: number): WordImages => {
  return initializeWordImages(wordId);
};

// 获取单词的当前激活图片
export const getActiveImage = (wordId: number): ImageRecord | null => {
  const wordImages = getWordImages(wordId);
  const activeImage = wordImages.images.find(img => img.id === wordImages.activeImageId);
  return activeImage || wordImages.images[0] || null;
};

// 添加新图片
export const addImage = (
  wordId: number, 
  type: ImageType, 
  url: string, 
  description?: string,
  metadata?: ImageRecord['metadata']
): ImageRecord => {
  const wordImages = initializeWordImages(wordId);
  
  const newImage: ImageRecord = {
    id: generateImageId(),
    wordId,
    type,
    url,
    description,
    createdAt: new Date(),
    isActive: false, // 新添加的图片默认不激活
    metadata
  };
  
  wordImages.images.push(newImage);
  IMAGES_COLLECTION[wordId] = wordImages;
  saveImagesToStorage(IMAGES_COLLECTION);
  
  return newImage;
};

// 设置激活图片
export const setActiveImage = (wordId: number, imageId: string): boolean => {
  const wordImages = IMAGES_COLLECTION[wordId];
  if (!wordImages) return false;
  
  const targetImage = wordImages.images.find(img => img.id === imageId);
  if (!targetImage) return false;
  
  // 取消所有图片的激活状态
  wordImages.images.forEach(img => {
    img.isActive = false;
  });
  
  // 激活目标图片
  targetImage.isActive = true;
  wordImages.activeImageId = imageId;
  
  IMAGES_COLLECTION[wordId] = wordImages;
  saveImagesToStorage(IMAGES_COLLECTION);
  
  return true;
};

// 删除图片
export const deleteImage = (wordId: number, imageId: string): boolean => {
  const wordImages = IMAGES_COLLECTION[wordId];
  if (!wordImages) return false;
  
  // 不允许删除系统默认图片
  const targetImage = wordImages.images.find(img => img.id === imageId);
  if (!targetImage || targetImage.type === ImageType.SYSTEM) return false;
  
  // 如果删除的是激活图片，需要重新设置激活图片
  const wasActive = targetImage.isActive;
  
  // 删除图片
  wordImages.images = wordImages.images.filter(img => img.id !== imageId);
  
  // 如果删除的是激活图片，激活系统默认图片
  if (wasActive) {
    const systemImage = wordImages.images.find(img => img.type === ImageType.SYSTEM);
    if (systemImage) {
      systemImage.isActive = true;
      wordImages.activeImageId = systemImage.id;
    } else if (wordImages.images.length > 0) {
      wordImages.images[0].isActive = true;
      wordImages.activeImageId = wordImages.images[0].id;
    }
  }
  
  IMAGES_COLLECTION[wordId] = wordImages;
  saveImagesToStorage(IMAGES_COLLECTION);
  
  return true;
};

// 更新图片信息
export const updateImage = (
  wordId: number, 
  imageId: string, 
  updates: Partial<Pick<ImageRecord, 'description' | 'metadata'>>
): boolean => {
  const wordImages = IMAGES_COLLECTION[wordId];
  if (!wordImages) return false;
  
  const targetImage = wordImages.images.find(img => img.id === imageId);
  if (!targetImage) return false;
  
  // 更新图片信息
  if (updates.description !== undefined) {
    targetImage.description = updates.description;
  }
  if (updates.metadata !== undefined) {
    targetImage.metadata = { ...targetImage.metadata, ...updates.metadata };
  }
  
  IMAGES_COLLECTION[wordId] = wordImages;
  saveImagesToStorage(IMAGES_COLLECTION);
  
  return true;
};



// 清理过期的临时图片（可选功能）
export const cleanupTempImages = (): void => {
  const now = new Date();
  const oneWeekAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
  
  Object.values(IMAGES_COLLECTION).forEach((wordImages: WordImages) => {
    wordImages.images = wordImages.images.filter(img => {
      // 保留系统图片和一周内的用户图片
      return img.type === ImageType.SYSTEM || img.createdAt > oneWeekAgo;
    });
  });
  
  saveImagesToStorage(IMAGES_COLLECTION);
};