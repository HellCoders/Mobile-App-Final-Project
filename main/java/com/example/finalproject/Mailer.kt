package com.example.finalproject

import android.annotation.SuppressLint
import io.reactivex.Completable
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object Mailer
{
    @SuppressLint("CheckResult")
    fun sendMail(email: String, subject: String, message: String) : Completable {
        return Completable.create { emitter ->
            val properties: Properties = Properties().also {
                it.put("mail.smtp.host", "smtp.gmail.com")
                it.put("mail.smtp.socketFactory.port", "465")
                it.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
                it.put("mail.smtp.auth", "true")
                it.put("mail.smtp.port", "465")
            }

            val session = Session.getDefaultInstance(properties, object: Authenticator() {
                override fun getPasswordAuthentication() : PasswordAuthentication {
                    return PasswordAuthentication(EmailConfig.EMAIL, EmailConfig.PASSWORD)
                }
            })

            try {
                MimeMessage(session).let {mime ->
                    mime.setFrom(InternetAddress(EmailConfig.EMAIL))
                    mime.addRecipient(Message.RecipientType.TO, InternetAddress(email))
                    mime.setSubject(subject)
                    mime.setText(message)
                    Transport.send(mime)
                }
            } catch (error: MessagingException) {
                emitter.onError(error)
            }

            emitter.onComplete()
        }
    }
}