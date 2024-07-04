Run PostgreSQL
docker run -it -d -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 postgres:latest

Run RabbitMQ
docker run -it -d -p 5672:5672 rabbitmq:latest

Run application.
