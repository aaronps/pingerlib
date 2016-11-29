# PingerLib - Small pinger library in Java

This library provides a PingManager which will run the ```ping``` command on a
list of IPs. It is designed to run on Linux, for other OS may need some changes.

## Usage

```java
    PingManager pm = new PingManager();
    pm.start();
    pm.addIp("192.168.1.1");
    pm.addIp("192.168.1.2");
    // when you want to finish
    pm.stop();
```
   