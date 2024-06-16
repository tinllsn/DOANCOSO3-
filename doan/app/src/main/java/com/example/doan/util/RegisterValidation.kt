package com.example.doan.util
//Sealed class trong Kotlin là một lớp trừu tượng mà chỉ có thể được mở rộng bởi các lớp nằm trong cùng một tệp

//Trong Kotlin, khi chúng ta nói "lớp con" của một sealed class, điều này có nghĩa là các thành phần con bên trong sealed class
sealed class RegisterValidation(){
    object Success: RegisterValidation()
    data class Failed(val message: String): RegisterValidation()
}
//Data class RegisterFieldsState là một lớp dữ liệu đơn giản trong Kotlin,
// được sử dụng để lưu trữ trạng thái của các trường liên quan đến đăng ký, ví dụ như email và password.
data class RegisterFieldsState(
    val email: RegisterValidation,
    val password: RegisterValidation
)