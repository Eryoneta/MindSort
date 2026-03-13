const path = require("path");
const HtmlWebpackPlugin = require("html-webpack-plugin");

const webInput = "./src/web";
const webOutput = "build/web";
const dskInput = "./src/desktop";
const dskOutput = "build/desktop";

module.exports = [
   {
      name: "web",
      mode: "production", // Optimizated for production code
      entry: `${webInput}/index.ts`,
      output: {
         filename: "bundle.js",
         path: path.resolve(__dirname, webOutput), // The output path
         compareBeforeEmit: false, // Write only when the source file is modified
         clean: true, // Clears the directory before emit
      },
      resolve: {
         tsconfig: "src/web/tsconfig.web.json",
      },
      plugins: [
      new HtmlWebpackPlugin({
         template: `${webInput}/index.html`,
      })
      ],
      module: {
         rules: [
            {
               test: /\.ts$/, // Target .ts files
               use: [{
                  loader: "ts-loader", // Compiles into .js files
                  options: {
                     configFile: "tsconfig.web.json"
                  }
               }],
               include: [
                  path.resolve(__dirname, webInput),
               ],
            },
            {
               test: /\.scss$/, // Target .scss files
               use: [
                  "style-loader", // Injects styles into the DOM with <style> tags
                  "css-loader", // Resolves css imports
                  "sass-loader", // Compiles into .css files
               ],
               include: [
                  path.resolve(__dirname, webInput),
               ],
            },
            {
               test: /\.(png|jpe?g|gif|svg)$/, // Target image files
               type: "asset/resource", // Handles as resources
            }
         ],
      },
      devServer: {
         static: path.resolve(__dirname, webOutput),
         port: 3000,
         open: true,
      },
   },
   {
      name: "desktop",
      mode: "production", // Optimizated for production code
      entry: `${dskInput}/main.ts`,
      output: {
         filename: "bundle.js",
         path: path.resolve(__dirname, dskOutput), // The output path
         compareBeforeEmit: false, // Write only when the source file is modified
         clean: true, // Clears the directory before emit
      },
      resolve: {
         tsconfig: "src/desktop/tsconfig.desktop.json",
      },
      plugins: [
      new HtmlWebpackPlugin({
         template: `${dskInput}/index.html`,
      })
      ],
      module: {
         rules: [
            {
               test: /\.ts$/, // Target .ts files
               use: [{
                  loader: "ts-loader", // Compiles into .js files
                  options: {
                     configFile: "tsconfig.desktop.json"
                  }
               }],
               include: [
                  path.resolve(__dirname, dskInput),
               ],
            },
            {
               test: /\.scss$/, // Target .scss files
               use: [
                  "style-loader", // Injects styles into the DOM with <style> tags
                  "css-loader", // Resolves css imports
                  "sass-loader", // Compiles into .css files
               ],
               include: [
                  path.resolve(__dirname, dskInput),
               ],
            },
            {
               test: /\.(png|jpe?g|gif|svg)$/, // Target image files
               type: "asset/resource", // Handles as resources
            }
         ],
      },
      devServer: {
         static: path.resolve(__dirname, dskOutput),
         port: 3000,
         open: true,
      },
   }
];
