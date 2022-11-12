package com.example.aposs_admin.ui_controller.login_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aposs_admin.model.dto.TokenDTO
import com.example.aposs_admin.network.AuthRepository
import com.example.aposs_admin.util.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.SocketTimeoutException
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

        var email: MutableLiveData<String> = MutableLiveData()
        var password: MutableLiveData<String> = MutableLiveData()
        var toastMessage: MutableLiveData<String> = MutableLiveData()
        var emailErrorMessage: String? = ""
        var passwordErrorMessage: String? = ""
        var loginState: MutableLiveData<LoginState> = MutableLiveData()
        var token: TokenDTO? = null
        private var viewModelJob = Job()
        private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

        fun onLoginClick() {
            if (email.value != null && password.value != null) {
                if (isValidEmail() && isValidPassword()) {
                    signIn(email.value!!, password.value!!)
                } else {
                    toastMessage.value = "Email or password is invalid!"
                }
            } else {
                toastMessage.value = "Email or password is invalid!"
            }
        }

        private fun signIn(email: String, password: String) {
            loginState.value = LoginState.Loading
            viewModelScope.launch(Dispatchers.Default) {
                try {
                    val response = authRepository.signIn(email, password)
                    if (response.code() == 200) {
                        token = response.body()
                        loginState.postValue(LoginState.Success)
                        toastMessage.postValue("Login success")
                    } else {
                        loginState.postValue(LoginState.Wait)
                        val jsonError: JSONObject = JSONObject(response.errorBody()!!.string())
                        toastMessage.postValue(jsonError.getString("message"))
                    }
                } catch (e : Exception) {
                    if (e is SocketTimeoutException) {
                        signIn(email, password)
                    } else {
                        e.printStackTrace()
                    }
                }
            }
        }


        private fun isEmailRightFormat(email: String): Boolean {
            return Pattern.compile(
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
            ).matcher(email).matches()
        }

        fun isValidPassword(): Boolean {
            return if (password.value!!.isBlank()) {
                passwordErrorMessage = "Password can not empty"
                false
            } else {
                if (password.value!!.count() < 8) {
                    passwordErrorMessage = "Password least than 8 character"
                    false
                } else {
                    passwordErrorMessage = null
                    true
                }
            }
        }

        fun isValidEmail(): Boolean {
            return if (email.value!!.isBlank()) {
                emailErrorMessage = "Email can not empty"
                false
            } else {
                if (isEmailRightFormat(email.value!!.trim())) {
                    emailErrorMessage = null
                    true
                } else {
                    emailErrorMessage = "Invalid email"
                    false
                }
            }
        }
}