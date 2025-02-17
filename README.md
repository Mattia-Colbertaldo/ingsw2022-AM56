# Eryantis Board Game with Java


## Implemented Functionalities

| Functionality         | State |
|:----------------------|:-----:|
| Basic rules           |  🟢   |
| Complete rules        |  🟢   |
| Socket                |  🟢   |
| GUI                   |  🟢   |
| CLI                   |  🟢   |
| Characters (12)       |  🟢   |
| 4 Players Game        |  🟢   |
| Persistence           |  🟢   |
| Multiple games        |  🔴   |
| Connection Resilience |  🔴   |

🔴
🟢
🟡


## Test cases
All tests in model and controller has a classes' coverage at 100%.

**Coverage criteria: code lines.**

| Package |    Coverage     |
|:--------|:---------------:|
| Enum    |  128/129 (99%)  |
| Network |  118/143 (82%)  |
| Model   | 1103/1211 (91%) |
| Server  | 1592/1802 (91%) |

![alt text](./Login.PNG)

![alt text](./login2.PNG)

![alt text](./mode.PNG)

![alt text](./characters.PNG)

![alt text](./game.PNG)

![alt text](./UML.png)


## Run configuration

The only requirement is the JRE (tested version 17.0.2).

To start the game you need to run first the server:
```
java -jar <file jar> -s -p <port where open the server>
```

To start a client you need to run:
```
java -jar <file jar> -c -p <port of server> -ip <ip of the server>
```
This will start the standard Cli client.

To specify the exact type of graphic:

CLI:
```
java -jar <file jar> -c -p <port of server> -ip <ip of the server> -g Cli
```
GUI:
```
java -jar <file jar> -c -p <port of server> -ip <ip of the server> -g Gui
```


## Additional Information

We also implemented a Bot class for testing.
To run it intellij is required: execute it from the class src/test/java/it/polimi/ingsw/Client/Bot.java.
Set the port of the server and the number of bot that you wanna launch.
