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
