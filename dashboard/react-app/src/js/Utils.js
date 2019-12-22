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

import {notification} from "antd";

export const handleApiError = (error, message, isForbiddenMessageSilent = false) => {
    if (error.hasOwnProperty("response") && error.response.status === 401) {
        const redirectUrl = encodeURI(window.location.href);
        window.location.href = window.location.origin + `/publisher/login?redirect=${redirectUrl}`;
        // silence 403 forbidden message
    } else if (!(isForbiddenMessageSilent && error.hasOwnProperty("response") && error.response.status === 403)) {
        notification["error"]({
            message: "There was a problem",
            duration: 10,
            description: message,
        });
    }
};
