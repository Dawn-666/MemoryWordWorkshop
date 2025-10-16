// 全局类型声明

declare global {
  interface Window {
    Android?: {
      getDeviceInfo(): string;
      // 可以在这里添加更多Android WebView接口
      speak(text: string): void;
    };
  }
}

export {};