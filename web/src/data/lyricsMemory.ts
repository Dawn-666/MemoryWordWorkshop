export interface LyricsMemory {
  wordId: number; 
  word: string;
  lyric: string;
  background?: string; // 背景图片URL
  audioUrl?: string;
}

export const LYRICS_MEMORY_DATA: Record<number, LyricsMemory> = {
  1: {
    wordId: 1,
    word: 'abandon',
    lyric: 'There was nothing in sight But memories left abandoned',
    background: 'https://www.cccimg.com/view.php/960ac328e376bd21794ece4d2367446a.png',
    audioUrl: '' // 暂时为空，后续可以添加音频文件
  }
};

export const getLyricsMemoryByWordId = (wordId: number): LyricsMemory | null => {
  return LYRICS_MEMORY_DATA[wordId] || null;
};