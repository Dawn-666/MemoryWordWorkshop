// 图片类型枚举
export enum ImageType {
  SYSTEM = 'system',    // 系统默认图片
  USER_UPLOAD = 'user_upload',  // 用户上传
  USER_AI = 'user_ai',  // AI生成
  USER_URL = 'user_url' // 用户网络图片
}

// 图片记录接口
export interface ImageRecord {
  id: string;           // 图片唯一ID
  wordId: number;       // 关联的单词ID
  type: ImageType;      // 图片类型
  url: string;          // 图片URL或base64
  description?: string; // 图片描述（AI生成时的提示词）
  createdAt: Date;      // 创建时间
  isActive: boolean;    // 是否为当前激活的图片
  metadata?: {          // 元数据
    originalName?: string;  // 原始文件名
    size?: number;          // 文件大小
    aiPrompt?: string;      // AI生成提示词
    source?: string;        // 来源信息
  };
}

// 单词图片集合
export interface WordImages {
  wordId: number;
  images: ImageRecord[];
  activeImageId?: string; // 当前激活的图片ID
}

// 图片数据集合
export interface ImageCollection {
  [wordId: number]: WordImages;
}