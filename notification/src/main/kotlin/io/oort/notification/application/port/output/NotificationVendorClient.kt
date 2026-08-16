package io.oort.notification.application.port.output

interface NotificationVendorClient {
    fun dispatch(notification: NotificationDispatch)
}
