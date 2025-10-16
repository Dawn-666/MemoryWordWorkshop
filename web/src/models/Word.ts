// 词典释义接口
export interface Definition {
  meaning: string;
  partOfSpeech?: string;
  order: number;
}

export interface Word {
  id: number;
  text: string;
  phonetic?: string;
  
  // 词典释义（多个）
  definitions: Definition[];
  
  // 例句
  example?: string;
  
  // 用户自定义内容
  customDefinition?: string;
  imageMemory?: string;
  
  // 状态
  isMemorized: boolean;
  memorizedAt?: Date;
  isCollected?: boolean;
}

export interface WordCollection {
  [id: number]: Word;
}