const { app, BrowserWindow } = require("electron");

let mainWindow;

function createMainWindow() {
    mainWindow = new BrowserWindow({
        width: 1000,
        height: 800,
        webPreferences: {
            nodeIntegration: false // Security for plugins
        }
    });
    mainWindow.loadFile("index.html"); // Load content
    mainWindow.on("closed", () => {
        mainWindow = null;
    });
}

app.on("ready", createMainWindow);
