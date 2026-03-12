const path = require("path");
const HtmlWebpackPlugin = require("html-webpack-plugin");

const webInput = "./src/web";
const webOutput = "dist/web";

module.exports = {
   mode: "production",
   entry: `${webInput}/index.ts`,
   output: {
      filename: "bundle.js",
      path: path.resolve(__dirname, webOutput),
      clean: true,
   },
   plugins: [
     new HtmlWebpackPlugin({
        template: `${webInput}/index.html`,
     })
   ],
   resolve: {
      extensions: [ ".ts", ".js" ],
   },
   module: {
      rules: [
         {
            test: /\.ts$/, // Target .ts files
            use: "ts-loader",
            exclude: [ /node_modules/, /dist/, /src\/core/, /src\/desktop/, /src\/mobile/ ],
         },
         {
            test: /\.scss$/, // Target .scss files
            use: [
               "style-loader",
               "css-loader",
               "sass-loader",
            ],
            exclude: [ /node_modules/, /dist/, /core/, /desktop/, /mobile/ ],
         },
         {
            test: /\.(png|jpe?g|gif|svg)$/, // Target image files
            type: "asset/resource",
         }
      ],
   },
   devServer: {
    static: path.resolve(__dirname, webOutput),
    port: 3000,
    open: true,
  },
};
