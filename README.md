# DotDot
Works with complex and nested Map<Key, Value> easily.

## Why
At [metrix.ir](https://metrix.ir/) we are doing a lot of interseting thing. Our microservice architecture 
heavily relis on JSON:
1. Collecting data from input sources that push JSON to us
2. Inter communication between microservices that talk JSON over HTTP
3. Using document-based databases which accept a document as a JSON

So working with JSON data effectively is our priority and one way of representing Json data 
is through Map data structure. Here are some use cases:

### Elasticsearch
Accessing your document from elasticsearch is accomplished through these methods:
```Java
SearchHit.getSourceAsString()
SearchHit.getSourceAsMap
```
`getSourceAsMap` returns a nested map. So if your document looks like this:
```
{
  "a" : {
    "b": {
      "c": 10
    }
  }
}
```
To get the value of `c` you should write code something like this:
```Java
Map<String, Object> a = (Map<String, Object>)doc.get("a");
if (a != null) {
    Map<String, Object> b = (Map<String, Object>)doc.get("b");
    if (b != null) {
        int c = (Integer) doc.get("c");
    }
}
```
But with DotDot it is easy as:
```
import static com.github.DotDot.*

int c = getInt("a.b.c", doc)
```

### Response of a REST Service
Most of the time, you parse the JSON to your data model, and will use that model later. But sometimes you 
do not have a data model defined for specific response, or response is so big and **you are 
interested to just few fields**. In these cases, Map<K,V> and DotDot are perfect match. Here is the sample code:
```Java
Map<String, Object> response = new Gson().fromJson(jsonStr, Map.class);
int c = getInt("a.b.c", response)
```

### Ensure that some field provided
Sometimes you only need to ensure that some fields are provided. In this case you can use **ensure** method. It returns true or false:
```Java
boolean ok = ensure("a.b.c", response)
```

### Create Nested Map
Instead of writng this code:
```Java
Map<String, Object> addressMap = new HashMap<String, Object>();
addressMap.put("street", "A");
addressMap.put("no", "7");

Map<String, Object> locationMap = new HashMap<String, Object>();
locationMap.put("country", "Germany");
locationMap.put("address", addressMap);

Map<String, Object> map = new HashMap<String, Object>();
map.put("userId","A431");
map.put("location", locationMap);
```
easily write this:
```Java
Map<String, Object> map = new HashMap<String, Object>();

put("userId", "A431", map);
put("userId.location.country", "Germany", map);
put("userId.location.address.street", "A", map);
put("userId.location.address.no", "7", map);
```

## Add to your project
You can reference to this library by either of java build systems 
(Maven, Gradle, SBT or Leiningen) using snippets from this jitpack link: [![](https://jitpack.io/v/mostafa-asg/dotdot.svg)](https://jitpack.io/#mostafa-asg/dotdot)
