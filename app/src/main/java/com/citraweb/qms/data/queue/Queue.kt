package com.citraweb.qms.data.queue

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
class Queue(var name: String?, var companyId: String?, var createdAt: String?, var currentQueue: Int?, var isOpen: Boolean?, var prefix: String?, var waiting: Int?)