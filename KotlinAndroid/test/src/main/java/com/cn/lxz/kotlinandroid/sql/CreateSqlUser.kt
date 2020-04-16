package com.cn.lxz.kotlinandroid.sql

import io.reactivex.Observable
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import java.util.*
import java.util.concurrent.Callable

/**
 * Created by linxingzhu on 2018/1/19.
 */
class CreateSqlUser (val data: KotlinReactiveEntityStore<Persistable>) : Callable<Observable<Iterable<SqlUserEntity>>> {


    override fun call(): Observable<Iterable<SqlUserEntity>> {
    val userNames = arrayOf("Alice", "Bob", "Carol", "Chloe", "Dan", "Emily", "Emma", "Eric",
            "Eva", "Frank", "Gary", "Helen", "Jack", "James", "Jane", "Kevin", "Laura", "Leon",
            "Lilly", "Mary", "Maria", "Mia", "Nick", "Oliver", "Olivia", "Patrick", "Robert",
            "Stan", "Vivian", "Wesley", "Zoe")
    val phones = arrayOf("111","222","333","444","555","666")
    val random = Random()

    val people = TreeSet(Comparator<SqlUserEntity> { lhs, rhs -> lhs.name.compareTo(rhs.name) })
    // creating many people (but only with unique names)

    for (i in 0..10) {
        val user = SqlUserEntity()
        user.name=userNames[random.nextInt(userNames.size)]
        user.birthday= Date()
//        phones.sortedWith(kotlin.Comparator { o1, o2 -> random.nextInt() }).run {
//                    for(i in 0..random.nextInt(phones.size))
//                    {
//                        var sqlPhone = SqlPhoneEntity()
//                        sqlPhone.owner=user
//                        sqlPhone.phoneNumber=phones[i]
//                        user.phoneNumbers.add(sqlPhone)
//                    }
//                }
        people.add(user)
    }
    return data.insert(people).toObservable()
}
}