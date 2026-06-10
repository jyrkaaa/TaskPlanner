import { useEffect, useState } from 'react';
import { LocalStorageNames } from '../constants/LocalStorageNames';

function ThemeToggleButton() {
    const [darkMode, setDarkMode] = useState<boolean>(() => {
        return localStorage.getItem(LocalStorageNames.THEME) === 'dark';
    });

    useEffect(() => {
        const theme = darkMode ? 'dark' : 'light';
        if (darkMode) {
            localStorage.setItem(LocalStorageNames.THEME, theme);
        } else {
            localStorage.removeItem(LocalStorageNames.THEME);
        }
        document.documentElement.setAttribute('data-theme', theme);
    }, [darkMode]);

    return (
        <button
            className="navbar-theme-toggle"
            onClick={() => setDarkMode(d => !d)}
            aria-label={darkMode ? 'Switch to light mode' : 'Switch to dark mode'}
        >
            {darkMode ? (
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <circle cx="12" cy="12" r="4"/>
                    <path d="M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M4.93 19.07l1.41-1.41M17.66 6.34l1.41-1.41"/>
                </svg>
            ) : (
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <path d="M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9Z"/>
                </svg>
            )}
        </button>
    );
}

export default ThemeToggleButton;
