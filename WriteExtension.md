# Расширение Gatling

Основано на статье: https://blog.codecentric.de/en/2017/07/gatling-load-testing-part-2-extending-gatling/

## Основы

### Gatling Expression

### Stats Engine

`StatsEngine` - класс, необходимый для логирования времени ответа.
Он должен всегда передаваться от ActionBuilder к Action.

По умолчанию
[`DataWritersStatsEngine`](https://github.com/gatling/gatling/search?q=DataWritersStatsEngine&unscoped_q=DataWritersStatsEngine)
записывает результаты в файл,
из которого создаются отчеты.

Самый важный метод `StatsEngine` - это `logResponse()`.
Который мы будем использовать в нашей реализации.

Одним из параметров, которые принимает метод, является статус.
Гатлинг предоставляет два объекта: `io.gatling.commons.stats.OK` и
`io.gatling.commons.stats.KO`.
Используя их, можно зарегистрировать как успешный, так и неудачный ответ.

## Реализация

### Predef и AsynclogDsl

Комбинация этих двух классов может быть немного перегружена,
но я хотел как можно больше походить на модули Гатлинга.

Объект `Predef` предназначен для импорта с подчеркиванием в коде симуляции.
Таким образом, все необходимое для доступа к функциональности
`Asynclog` должно присутствовать. 
`Predef` расширяет `AsynclogDsl` trait.
Если модуль будет состоять из более чем одного протокола
(например, Gatling HTTP также содержит WebSocket и Server Sent Events (SSE)),
должен существовать второй *Dsl trait,
который также должен расширять `Predef`. Вот полный код `Predef`:

```scala
object Predef extends AsynclogDsl
```


### AsynclogBuilderBase

