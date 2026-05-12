import { createRoot } from 'react-dom/client';
import App from './App';
import './App.css';

const rootElement = document.getElementById('outlet');
if (rootElement) {
  const root = createRoot(rootElement);
  root.render(<App />);
}
