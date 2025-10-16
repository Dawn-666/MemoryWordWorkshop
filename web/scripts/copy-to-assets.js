import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

// 获取当前文件的目录
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// 源目录和目标目录
const sourceDir = path.resolve(__dirname, '../dist');
const targetDir = path.resolve(__dirname, '../../app/src/main/assets/h5');

// 递归复制函数
function copyFolderSync(source, target) {
  // 创建目标目录
  if (!fs.existsSync(target)) {
    fs.mkdirSync(target, { recursive: true });
  }

  // 读取源目录内容
  const files = fs.readdirSync(source);

  // 遍历源目录内容
  files.forEach(file => {
    const sourcePath = path.join(source, file);
    const targetPath = path.join(target, file);
    
    // 检查是否为目录
    if (fs.lstatSync(sourcePath).isDirectory()) {
      // 递归复制子目录
      copyFolderSync(sourcePath, targetPath);
    } else {
      // 复制文件
      fs.copyFileSync(sourcePath, targetPath);
    }
  });
}

// 清空目标目录
if (fs.existsSync(targetDir)) {
  fs.rmSync(targetDir, { recursive: true, force: true });
}

// 复制构建产物
try {
  copyFolderSync(sourceDir, targetDir);
  console.log('成功将构建文件复制到Android assets目录');
} catch (error) {
  console.error('复制文件时出错:', error);
  process.exit(1);
}