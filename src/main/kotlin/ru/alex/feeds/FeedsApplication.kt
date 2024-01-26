package ru.alex.feeds

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@SpringBootApplication
@EnableReactiveMongoRepositories(basePackages = ["ru.alex.feeds.repository.mongo"])
@EnableReactiveElasticsearchRepositories(basePackages = ["ru.alex.feeds.repository.elastic"])
class FeedsApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<FeedsApplication>(*args)
}
