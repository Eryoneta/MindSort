import { BrowserWindow } from "electron";

export default class Main {
    public static mainWindow: Electron.BrowserWindow | null;
    public static application: Electron.App;
    private static BrowserWindow: typeof BrowserWindow;
    
    private static createMainWindow() {
        Main.mainWindow = new Main.BrowserWindow({
            width: 1000,
            height: 800,
            webPreferences: {
                nodeIntegration: false // Security for plugins
            }
        });
        Main.mainWindow.loadFile("./renderer.html"); // Load content
        Main.mainWindow.on("closed", () => {
            Main.mainWindow = null;
        });
    }
    
    public static main(app: Electron.App, browserWindow: typeof BrowserWindow) {
        Main.application = app;
        Main.BrowserWindow = browserWindow;
        Main.application.on("ready", Main.createMainWindow);
    }
}
