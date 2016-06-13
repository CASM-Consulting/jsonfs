# jsonfs
file system backed json-like data structures in Java


```java

JsonFS fs = new JsonFS("path/to/data");

List list = new ArrayList<>();
list.add(1);
list.add(2);
list.add("three");


Map data = new HashMap<>();
data.put("zero", list);
data.put("four", false);

fs.object(data);


((Map)fs.get()).get("four") == false


```

