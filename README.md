# Gatling AsyncLog Plugin

## Назначение

Плагин для логирования операций в Gatling 3.2.1.
Поддерживает логирование, в том числе, и операций, которые начались в одном сценарии, а завершились в другом сценарии.

## Методы для ручного создания событий (готово)

Пример вызова есть в example/src/test/scala/qaload/BasicSimulation.scala

Для создания события нужно заполнить:

* имя запроса (обязательно)
* момент старта (обязательно)
* момент завершения (обязательно)
* статус (по умолчанию OK)
* код ответа (по умолчанию 200, но код ответа не попадает в статистику Gatling Open Source)
* сообщение (по умолчанию пустое)

Момент старта и момент завершения можно заполнять с помощью разных типов:

* long или scala.Long (`System.currentTimeMillis`)
* java.util.Date (`java.util.Calendar.getInstance().getTime()`)
* java.lang.String или scala.String (строка с датой, временем, с точностью и миллитекунд и часовым поясом в любом формате)

Передавать можно как значения с указанными типами, так и Expression.

Для трёх типов есть три вида методов:

1. startTimestamp(Long start), endTimestamp(Long stop)
2. startDate(Date startDate), endDate(Date endDate)
3. startTimestamp(String startDateString, String dateFormat), stopTimestamp(String stopDateString, String dateFormat)

К каждому событию можно применить их все. Сделано для простоты реализации. И если одновременно применять методы, 
то значения будут использоваться по мере уменьшения приоритета:

* 1 - применится значение 1 (`startTimestamp(Long start)`)
* 2 - применится значение 2 (`startDate(Date startDate)`)
* 3 - применится значение 3 (`startDate(Date startDate)`)
* 1 и 2 - применится только 1 (`startTimestamp(Long start)`)
* 1 и 3 - применится только 1 (`startTimestamp(Long start)`)
* 1, 2 и 3 - применится значение 1 (`startTimestamp(Long start)`)
* 2 и 3  - применится значение 2 (`startDate(Date startDate)`)

Аналогично и для момента завершения.

Пример:

    .exec(asynclog
      .requestName("Generate request")
      .startTimestamp("2020-04-22 20:53:28.546 MSK", "yyyy-MM-dd HH:mm:ss.SSS zzz")
      .endTimestamp("2020-04-22 20:53:29.069 MSK", "yyyy-MM-dd HH:mm:ss.SSS zzz")
      .status(io.gatling.commons.stats.OK)
      .responseCode("200")
      .message("All correct")
    )

Пример с передачей параметров, через параметры сессии Gatling:


    val scn = scenario("Scenario Name") // A scenario is a chain of requests and pauses
    .exec {
      session =>
        val now = System.currentTimeMillis;
      session
          .set("start", now)
    }
    .exec(
      http("/ (GET)").get("/")
    )
    .exec(
      http("/mainPage.php (GET)").get("/mainPage.php")
    )    
    .exec(
      http("/finalPage.php (GET)").get("/finalPage.php")
    )    
    .exec {
      session =>
        val now = System.currentTimeMillis;
        session
          .set("stop", now)
    }
    .exec(asynclog
      .requestName("Group of three requests")
      .startTimestamp("${start}")
      .endTimestamp("${stop}")
    )

## Методы для замера длительности работы (TODO, проект)


### Доступные действия

* start — старт
* id — обновить идентификатор транзакции 
* log — залогировать операцию
* stop — удалить транзакцию
* stopAll — удалить все транзакции

#### Старт, начать отчет времени

```scala
asyncLog(transactionID: Expression[String]).start(requestName: Expression[String])
asyncLog(transactionID: Expression[String]).start(requestName: Expression[String], maxDuration: Expression[Int])
```

При старте транзкации можно указать сразу имя транзакции.
И максимальную длительность транзакций, после которой транзакция автоматически удаляется.

По умолчанию имя транзакции не задано.
А максимальная длительность транзакций не ограничена.

#### Обновить идентификатор транзакции

```scala
asyncLog(transactionID: Expression[String]).id(newTransactionID: Expression[String])
```

Для транзакции с идентификатором transactionID, заменить идентификатор на newTransactionID

#### Залогировать операцию

```scala
asyncLog(transactionID: Expression[String]).log()
asyncLog(transactionID: Expression[String]).log(requestName: Expression[String])

```

#### Удалить транзакцию

```scala
asyncLog(transactionID: Expression[String]).stop()
```

#### Залогировать и удалить все запущенные транзакции

```scala
asyncLog(transactionID: Expression[String]).stopAll()
asyncLog(transactionID: Expression[String]).stopAll(stopMessage: Expression[String])
```

### Доступные аттрибуты

* timeStart — переопределить время старта
* timeEnd — переопределить время завершения
* label — переопределить название транзакций
* status — переопределить статус транзакции
* message — переопределить сообщение

## Примеры

### Запустить транзакцию, залогировать промежуточное состояние, залогировать финальное состояние и остановить

```
1. 12:00:00 # asyncLog("1").start("Generate PDF Report")
2. 12:00:05 # asyncLog("1").log().label("Generate PDF Report (prepare report complete)")
3. 12:03:00 # asyncLog("1").stop()
```

В результате в статистике зафиксируется два сообытия:

| №  | label | timeStart | timeEnd | duration | status | message |
| -- | ------------------- | ------------- | ----------------- | ------------ | ------ | ------ |
| 1  | Generate PDF Report (prepare report complete) | 12:00:00 | 12:00:05 | 5 second | OK | (empty) |
| 2  | Generate PDF Report | 12:00:00 | 12:03:00 | 180 second | OK | (empty) |