package com.bandit.data.repository

import com.bandit.data.db.Database

open class BaseRepository(protected val _database: Database? = null)