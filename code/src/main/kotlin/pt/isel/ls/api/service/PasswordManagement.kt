package pt.isel.ls.api.service

import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

class PasswordManagement {
    private val algorithm = "AES"
    private val cipher: Cipher = Cipher.getInstance(algorithm)
    private val plainTextKey = "grupo01_lenght16" //assim geramos sempre a mesma chave em todas as instancias novas do servidor (ao contrario de fazer keygen.generateKey), e tem de ter uma lenght igual ou superior a 16
    private val key = SecretKeySpec(plainTextKey.toByteArray(), algorithm)
    init {
        val keygen = KeyGenerator.getInstance(algorithm)
        keygen.init(256)
        cipher.init(Cipher.ENCRYPT_MODE, key)
    }

    fun encrypt(password: String) : String { //toByteArray = encodeToByteArray. A bigger password will result in a bigger hash
        val byteArray = cipher.doFinal(password.toByteArray())
        return Base64.getEncoder().encode(byteArray).decodeToString()
    }

    //funçao demonstraçao em como nao se deve armazenar como String diretamente pq codigos diferentes podem ser ambos representados com o carater '�' (https://www.compart.com/en/unicode/U+FFFD). O que pode levar a que a comparaçao de 2 passwords diferentes, sejam comparadas como iguais
    private fun encryptSeeStringAndByteArray(password: String) {
        println(cipher.doFinal(password.toByteArray()).map { it }.toString())
        println(cipher.doFinal(password.toByteArray()).decodeToString())
    }

    //funçao demonstraçao
    private fun base64Encoding(password: String){
        val x = cipher.doFinal(password.toByteArray())
        println("Encoded to byte array: "+x.map { it })
        val encoded = Base64.getEncoder().encode(x).decodeToString()
        println("Encoded to string: $encoded")
        println("Decoded from string to byte array: "+Base64.getDecoder().decode(encoded.toByteArray()).map { it })
        println("The byte array converted to text(will contain �) : ${cipher.doFinal(password.toByteArray()).decodeToString()}")
    }
}

