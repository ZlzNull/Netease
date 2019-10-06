package Bean.LoginCallPhone

data class LoginCallPhone(
    val account: Account,
    val bindings: List<Binding>,
    val code: Int,
    val loginType: Int,
    val profile: Profile
)