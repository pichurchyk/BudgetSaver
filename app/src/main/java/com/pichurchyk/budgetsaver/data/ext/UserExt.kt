package com.pichurchyk.budgetsaver.data.ext

import com.pichurchyk.budgetsaver.domain.model.User
import io.github.jan.supabase.gotrue.user.UserInfo

fun UserInfo.toUser() = User(
    id = this.id,
    name = this.userMetadata?.get("full_name")?.toString()?.removeQuotes(),
    email = this.email,
    avatarUrl = this.userMetadata?.get("avatar_url")?.toString()?.removeQuotes()
)

private fun String.removeQuotes(): String {
    return if (this.startsWith("\"") && this.endsWith("\"")) {
        this.substring(1, this.length - 1)
    } else {
        this
    }
}
