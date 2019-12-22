/*
 * Copyright (c) 2019, Entgra (pvt) Ltd. (http://entgra.io) All Rights Reserved.
 *
 * Entgra (pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
