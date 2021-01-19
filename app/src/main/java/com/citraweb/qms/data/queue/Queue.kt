package com.citraweb.qms.data.queue

data class Queue(
        val id : String,
        val name: String ,
        val companyId : String,
        val createdAt : String,
        val currentQueue : Int,
        val isOpen : Boolean,
        val prefix : String,
        val waiting : Int,
        )
