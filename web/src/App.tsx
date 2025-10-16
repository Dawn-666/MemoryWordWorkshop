import { Routes, Route, HashRouter } from 'react-router-dom';  
import Home from './pages/Home';
import WordDetail from './pages/WordDetail';
import EditImageMemory from './pages/EditImageMemory';
import './App.css';

function App() {
  // 使用HashRouter而不是BrowserRouter，因为在WebView中更可靠
  return (
    <div className="app">
      <HashRouter>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/word/:id" element={<WordDetail />} />
          <Route path="/word/:id/editImage" element={<EditImageMemory />} />
        </Routes>
      </HashRouter>
    </div>
  );
}

export default App;