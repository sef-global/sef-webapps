var path = require('path');
const HtmlWebPackPlugin = require("html-webpack-plugin");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const configurations = require("./public/conf/config.json");

const config = {
    devtool: "source-map",
    output: {
        publicPath: '/dashboard/'
    },
    watch: false,
    resolve: {
        alias: {
            AppData: path.resolve(__dirname, 'source/src/app/common/'),
            AppComponents: path.resolve(__dirname, 'source/src/app/components/')
        },
        extensions: ['.jsx', '.js', '.ttf', '.woff', '.woff2', '.svg']
    },
    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                exclude: /node_modules/,
                use: [
                    {
                        loader: 'babel-loader'
                    }
                ]
            },
            {
                test: /\.html$/,
                use: [
                    {
                        loader: "html-loader",
                        options: {minimize: true}
                    }
                ]
            },
            {
                test: /\.css$/,
                use: [MiniCssExtractPlugin.loader, "css-loader"]
            },
            {
                test: /\.scss$/,
                use: [
                    MiniCssExtractPlugin.loader,
                    "css-loader",
                    "postcss-loader",
                    "sass-loader"
                ]
            },
            {
                test: /\.scss$/,
                use: ['style-loader', 'scss-loader']
            },
            {
                test: /\.less$/,
                use: [
                    {
                        loader: "style-loader"
                    },
                    {
                        loader: "css-loader"
                    },
                    {
                        loader: "less-loader",
                        options: {
                            modifyVars: {
                                'primary-color': configurations.theme.primaryColor,
                                'link-color': configurations.theme.primaryColor,
                            },
                            javascriptEnabled: true,
                        },
                    }
                ]
            },
            {
                test: /\.(woff|woff2|eot|ttf|svg)$/,
                loader: 'url-loader?limit=100000',
            },
            {
                test: /\.(png|jpe?g)/i,
                use: [
                    {
                        loader: "url-loader",
                        options: {
                            name: "./img/[name].[ext]",
                            limit: 10000
                        }
                    },
                    {
                        loader: "img-loader"
                    }
                ]
            }
        ]
    },
    plugins: [
        new HtmlWebPackPlugin({
            template: "./src/index.html",
            filename: "./index.html"
        }),
        new MiniCssExtractPlugin({
            filename: "[name].css",
            chunkFilename: "[id].css"
        })
    ],
    externals: {
        'Config': JSON.stringify(require('./public/conf/config.json'))
    }
};

if (process.env.NODE_ENV === "development") {
    config.watch = true;
}

module.exports = config;
