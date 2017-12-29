# javaRPC
Create the Object and play with it :)

```java
try {
  XboxConsole console = new XboxConsole("192.168.1.172");
  console.SetMemory("0xFFF", "000"); // No Bytes or Unsigned ints in Java :)
  console.XNotify("Testing", Xtype.PLAIN); // Plain, Friend or Invite types supported
} catch (Exception ex) {
  ex.printStackTrace();
}
```
