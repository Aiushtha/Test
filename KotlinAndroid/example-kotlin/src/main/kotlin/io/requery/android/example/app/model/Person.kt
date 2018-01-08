package io.requery.android.example.app.model

import android.os.Parcelable
import io.requery.*
import java.util.*


//Suppose this is 1.1版本
@Entity
interface Person : Parcelable, Persistable {
    @get:Key
    @get:Generated
    val id: Int

    var name: String

    var email: String

    var birthday: Date

    var age: Int

    @get:ForeignKey
    @get:OneToOne
    var address: Address?

    @get:OneToMany(mappedBy = "owner")
    val phoneNumbers: MutableSet<Phone>

    @get:Column(unique = true)
    var uuid: UUID

    var company: String  //<--this is 1.2 append field

    var company2: String  //<--this is 1.2 append field

    var company3: String  //<--this is 1.2 append field
}
