import { Word, WordCollection, Definition } from '../models/Word';
import { getActiveImage } from './images'; 
 
// 本地存储键名
const WORDS_STORAGE_KEY = 'memory_workshop_words';

// 初始单词数据
const INITIAL_WORDS: Word[] = [
  { 
    id: 1, 
    text: 'abandon', 
    phonetic: "/əˈbændən/",
    definitions: [
      { order: 1, meaning: "放弃，抛弃 (v.)", partOfSpeech: "v." },
      { order: 2, meaning: "沉溺于 (v.)", partOfSpeech: "v." },
      { order: 3, meaning: "放任，放纵 (v.)", partOfSpeech: "v." }
    ],
    example: 'He decided to abandon the project.',
    customDefinition: "大家都爱写代码，一言不合一写，最后放弃。",
    isMemorized: false
  },
  { 
    id: 2, 
    text: 'serendipity', 
    phonetic: "/ˌserənˈdɪpɪti/",
    definitions: [
      { order: 1, meaning: "意外发现美好事物的能力 (n.)", partOfSpeech: "n." }
    ],
    example: 'Finding this book was pure serendipity.',
    isMemorized: false
  },
  { 
    id: 3, 
    text: 'eloquent', 
    phonetic: "/ˈeləkwənt/",
    definitions: [
      { order: 1, meaning: "雄辩的，有说服力的 (adj.)", partOfSpeech: "adj." }
    ],
    example: 'She gave an eloquent speech.',
    isMemorized: false
  },
  { 
    id: 4, 
    text: 'nostalgia', 
    phonetic: "/nɒˈstældʒə/",
    definitions: [
      { order: 1, meaning: "怀旧，思乡之情 (n.)", partOfSpeech: "n." }
    ],
    example: 'The music filled him with nostalgia.',
    isMemorized: false
  },
  { 
    id: 5, 
    text: 'ubiquitous', 
    phonetic: "/juːˈbɪkwɪtəs/",
    definitions: [
      { order: 1, meaning: "无所不在的，普遍存在的 (adj.)", partOfSpeech: "adj." }
    ],
    example: 'Mobile phones are now ubiquitous.',
    isMemorized: false
  }
];

// 从本地存储加载单词数据或使用初始数据
const loadWordsFromStorage = (): Word[] => {
  try {
    const savedWords = localStorage.getItem(WORDS_STORAGE_KEY);
    if (savedWords) {
      const parsedWords = JSON.parse(savedWords) as Word[];
      // 确保日期对象正确恢复
      return parsedWords.map(word => ({
        ...word,
        memorizedAt: word.memorizedAt ? new Date(word.memorizedAt) : undefined
      }));
    }
  } catch (error) {
    console.error('Failed to load words from storage:', error);
  }
  return [...INITIAL_WORDS]; // 返回初始数据的副本
};

// 保存单词数据到本地存储
const saveWordsToStorage = (words: Word[]): void => {
  try {
    localStorage.setItem(WORDS_STORAGE_KEY, JSON.stringify(words));
  } catch (error) {
    console.error('Failed to save words to storage:', error);
  }
};

// 单词列表数组 - 动态加载
let WORDS_LIST: Word[] = loadWordsFromStorage();

// 单词映射对象 - 动态构建
let WORDS_MAP: WordCollection = WORDS_LIST.reduce((map, word) => {
  map[word.id] = word;
  return map;
}, {} as WordCollection);

// 获取单词列表
export const getWordsList = (): Word[] => {
  return [...WORDS_LIST]; // 返回副本以避免直接修改
};

// 通过ID获取单词
export const getWordById = (id: number): Word | undefined => {
  const word = WORDS_MAP[id] ? { ...WORDS_MAP[id] } : undefined;
  if (word) {
    // 自动获取当前激活的图片
    const activeImage = getActiveImage(id);
    if (activeImage) {
      word.imageMemory = activeImage.url;
    }
  }
  return word;
};

// 获取下一个单词
export const getNextWord = (currentId: number): Word | undefined => {
  // 按ID排序获取所有单词
  const sortedWords = [...WORDS_LIST].sort((a, b) => a.id - b.id);
  const currentIndex = sortedWords.findIndex(word => word.id === currentId);
  
  // 如果找到当前单词且不是最后一个
  if (currentIndex !== -1 && currentIndex < sortedWords.length - 1) {
    return { ...sortedWords[currentIndex + 1] };
  }
  
  // 如果是最后一个，返回第一个（循环）
  if (currentIndex === sortedWords.length - 1 && sortedWords.length > 0) {
    return { ...sortedWords[0] };
  }
  
  return undefined;
};

// 获取上一个单词
export const getPrevWord = (currentId: number): Word | undefined => {
  // 按ID排序获取所有单词
  const sortedWords = [...WORDS_LIST].sort((a, b) => a.id - b.id);
  const currentIndex = sortedWords.findIndex(word => word.id === currentId);
  
  // 如果找到当前单词且不是第一个
  if (currentIndex > 0) {
    return { ...sortedWords[currentIndex - 1] };
  }
  
  // 如果是第一个，返回最后一个（循环）
  if (currentIndex === 0 && sortedWords.length > 0) {
    return { ...sortedWords[sortedWords.length - 1] };
  }
  
  return undefined;
};

// 更新单词记忆状态
export const updateWordMemorizedStatus = (word: Word, isMemorized: boolean): Word | undefined => {
  const wordIndex = WORDS_LIST.findIndex(w => w.id === word.id);
  if (wordIndex === -1) return undefined;
  
  // 创建更新后的单词对象
  const updatedWord: Word = {
    ...word,
    isMemorized,
    memorizedAt: isMemorized ? new Date() : undefined
  };
  
  // 更新列表和映射
  WORDS_LIST = [
    ...WORDS_LIST.slice(0, wordIndex),
    updatedWord,
    ...WORDS_LIST.slice(wordIndex + 1)
  ];
  
  WORDS_MAP = { ...WORDS_MAP, [word.id]: updatedWord };
  
  // 保存到本地存储
  saveWordsToStorage(WORDS_LIST);
  
  return { ...updatedWord }; // 返回更新后的单词副本
};

// 更新单词自定义释义
export const updateCustomDefinition = (word: Word, customDefinition: string): Word | undefined => {
  const wordIndex = WORDS_LIST.findIndex(w => w.id === word.id);
  if (wordIndex === -1) return undefined;
  
  // 创建更新后的单词对象
  const updatedWord: Word = {
    ...word,
    customDefinition
  };
  
  // 更新列表和映射
  WORDS_LIST = [
    ...WORDS_LIST.slice(0, wordIndex),
    updatedWord,
    ...WORDS_LIST.slice(wordIndex + 1)
  ];
  
  WORDS_MAP = { ...WORDS_MAP, [word.id]: updatedWord };
  
  // 保存到本地存储
  saveWordsToStorage(WORDS_LIST);
  
  return { ...updatedWord }; // 返回更新后的单词副本
};

// 更新单词图片记忆
export const updateImageMemory = (word: Word, imageMemory: string): Word | undefined => {
  const wordIndex = WORDS_LIST.findIndex(w => w.id === word.id);
  if (wordIndex === -1) return undefined;
  
  // 创建更新后的单词对象
  const updatedWord: Word = {
    ...word,
    imageMemory
  };
  
  // 更新列表和映射
  WORDS_LIST = [
    ...WORDS_LIST.slice(0, wordIndex),
    updatedWord,
    ...WORDS_LIST.slice(wordIndex + 1)
  ];
  
  WORDS_MAP = { ...WORDS_MAP, [word.id]: updatedWord };
  
  // 保存到本地存储
  saveWordsToStorage(WORDS_LIST);
  
  return { ...updatedWord }; // 返回更新后的单词副本
};

// 获取最后一个已记住的单词（学习进度）
export const getLastMemorizedWord = (): Word | undefined => {
  // 按ID排序，找到最后一个已记住的单词
  const memorizedWords = WORDS_LIST
    .filter(word => word.isMemorized)
    .sort((a, b) => a.id - b.id);
  
  return memorizedWords.length > 0 
    ? { ...memorizedWords[memorizedWords.length - 1] } 
    : undefined;
};

// 获取学习进度百分比
export const getLearningProgress = (): number => {
  const memorizedCount = WORDS_LIST.filter(word => word.isMemorized).length;
  return WORDS_LIST.length > 0 ? (memorizedCount / WORDS_LIST.length) * 100 : 0;
};

// 更新单词词典释义
export const updateWordDefinitions = (word: Word, definitions: Definition[]): Word | undefined => {
  const wordIndex = WORDS_LIST.findIndex(w => w.id === word.id);
  if (wordIndex === -1) return undefined;
  
  // 创建更新后的单词对象
  const updatedWord: Word = {
    ...word,
    definitions: [...definitions] // 创建副本
  };
  
  // 更新列表和映射
  WORDS_LIST = [
    ...WORDS_LIST.slice(0, wordIndex),
    updatedWord,
    ...WORDS_LIST.slice(wordIndex + 1)
  ];
  
  WORDS_MAP = { ...WORDS_MAP, [word.id]: updatedWord };
  
  // 保存到本地存储
  saveWordsToStorage(WORDS_LIST);
  
  return { ...updatedWord }; // 返回更新后的单词副本
};

// 获取随机单词
export const getRandomWord = (): Word => {
  const randomIndex = Math.floor(Math.random() * WORDS_LIST.length);
  return { ...WORDS_LIST[randomIndex] }; // 返回副本以避免直接修改
};