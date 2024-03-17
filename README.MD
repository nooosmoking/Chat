Многопользовательский чат.

Реализация базового механизма клиент-серверного приложения на основе Java — Sockets API.
Проект содержит два приложения: socket-server и socket-client. Сервер поддерживает подключение нескольких клиентов. Серверный JAR-файл устанавливается и запускается следующим образом:
```
$ mvn install SocketServer
$ java -jar SocketServer/target/socket-server.jar --port={введите необходимый порт}
```
Клиент — это тоже отдельный проект:
```
$ mvn install SocketClient
$ java -jar SocketClient/target/socket-client.jar --server-port={введите необходимый порт}
```

Каждый пользователь может:

Зарегистрироваться
Авторизоваться
Создать чат
Выбрать чат
Отправить сообщение в чат
Покинуть чат

При повторном входе пользователя в приложение в комнате, которую пользователь посещал ранее, отображаются 30 последних сообщений.
Пример работы приложения на стороне клиента:

```
Hello from Server!
1. signIn
2. SignUp
3. Exit
> 1
Enter username:
> Marsel
Enter password:
> qwerty007
1.	Create room
2.	Choose room
3.	Exit
> 2
Rooms:
1. First Room
2. SimpleRoom
3. JavaRoom
4. Exit
> 3
Java Room ---
JavaMan: Hello!
> Hello!
Marsel: Hello!
> Exit
You have left the chat.

```

Каждая пользовательская команда или сообщение передаётся на сервер (и получает от сервера) в виде строки JSON.

Каждое сообщение, пользователь и комената сохраненяется в базе данных, схема и данные для входа в которую содержатся в файле SocketServer/src/main/resources/db.properties