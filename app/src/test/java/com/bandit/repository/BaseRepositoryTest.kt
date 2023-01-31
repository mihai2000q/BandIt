package com.bandit.repository

import com.bandit.constant.Constants
import com.bandit.data.model.BaseModel
import com.bandit.data.repository.BaseRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert

open class BaseRepositoryTest <T : BaseModel> {
    protected fun different_ids(
        repository: BaseRepository<T>,
        item: T
    ) {
        runBlocking {
            for (i in 0..(Constants.MAX_NR_ITEMS / 2))
                repository.add(item)
            for (song in repository.list)
                if(repository.list.filter { it.id == song.id }.size > 1)
                    Assert.fail("Ids should be different")
        }
    }
}