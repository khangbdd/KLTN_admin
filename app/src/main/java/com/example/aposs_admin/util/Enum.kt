package com.example.aposs_admin.util

enum class DialogType{
    CartDialog, CheckOutDialog
}

enum class LoginState{
    Loading, Wait, Success
}

enum class SignUpState{
    Loading, Wait, Success, Verify
}

enum class OrderStatus{
    Pending, Confirmed, Delivering, Success, Cancel
}

enum class PaymentStatus{
    Waiting, Pending, Completed
}

enum class LoadingStatus{
    Loading, Success, Fail, Init
}

enum class PredictionStatus {
    Cancel, Success, Error, Processing
}



