package ru.alex.feeds.repository.elastic


import org.springframework.data.repository.reactive.ReactiveCrudRepository
import ru.alex.feeds.repository.elastic.entity.Article

interface ArticleRepository : ReactiveCrudRepository<Article, String>, CustomArticleRepository
