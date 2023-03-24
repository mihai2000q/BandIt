package com.bandit.repository

import com.bandit.constant.Constants
import com.bandit.data.template.Item
import com.bandit.data.template.TemplateRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert

abstract class BaseRepositoryTest <T : Item> {
    protected fun different_ids(
        repository: TemplateRepository<T>,
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
    protected abstract fun import_data()

    protected fun repository_remove(
        repository: TemplateRepository<T>
    ) {
        import_data()
        val itemToRemove = repository.list[0]
        val before = repository.list.filter { it == itemToRemove }
        Assert.assertEquals(1, before.size)
        Assert.assertEquals(6, repository.list.size)
        runBlocking { repository.remove(itemToRemove) }
        val after = repository.list.filter { it == itemToRemove }
        Assert.assertEquals(0, after.size)
        Assert.assertEquals(5, repository.list.size)
        Assert.assertThrows(IndexOutOfBoundsException::class.java) {
            repository.list[5]
        }
    }
}