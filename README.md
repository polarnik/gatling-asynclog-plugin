# Gatling AsyncLog Plugin

## Назначение

Плагин для логирования операций в Gatling 3.2.1.
Поддерживает логирование, в том числе, и операций, которые начались в одном сценарии, а завершились в другом сценарии.

## Методы для замера длительности работы


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