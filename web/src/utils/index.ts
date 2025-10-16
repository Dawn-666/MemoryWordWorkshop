export const speak = (text: string) => {
  window.Android?.speak(text);
};