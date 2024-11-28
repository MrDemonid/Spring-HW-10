# Задание: Напишите тесты для основной логики вашего приложения с использованием Spring Testing, JUnit и Mockito.

Здесь два типа тестов - модульные и интеграционные.

## Модули.
- Config-server - обеспечивает сервисам доступ к конфигурации на GitHub.
- Eureka-server - регистрирует сервисы и позволяет находить их адреса по их именам.
- Gateway-server - обеспечивает доступ к сервисам через единый адрес и порт.
- Catalog-service - ведет учет товаров и их списание.
- User-service - хранит данные о пользователях, а так же их платежные данные.
- Payment-service - проводит транзакции оплаты заказов пользователями.
- Order-service - создает и сопровождает заказ, вплоть до момента оплаты (или отказа от заказа).
- Web-client - веб-интерфейс магазина.

## Тесты

Если тестов с аннотацией `@SpringBootTest` больше одной штуки, как у нас,
то возможен конфликт настроек тестов. И у меня он был.

Вариантов решить конфликт много, но я остановился на отдельном профиле
для своих интеграционных тестов. В профиле создается временная БД H2 специально
для тестов, а так же отключается зависимость от `Eureka` и `Cloud Config Server`.

Можно так же использовать `@DirtiesContext`, для перезагрузки контекста после
выполнения теста, но это сильно увеличивает время теста, хотя конечно
делает его более приближенным к реальной задаче и более универсальным
решением.

Так же конфликта можно избежать если просто удалять БД в `schema.sql`:
```sql
drop table if exists products;
drop table if exists blocked_products;
```
и больше тесты не конфликтуют. Но это хорошо только с БД в памяти, типа H2.



## H2-Console

http://localhost:8090/CATALOG-SERVICE/h2-console

http://localhost:8090/ORDER-SERVICE/h2-console

http://localhost:8090/PAYMENT-SERVICE/h2-console

http://localhost:8090/USER-SERVICE/h2-console