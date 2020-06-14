# hokuyo-optical-parallel-io-java8

This library is HOKUYO Optical-Parallel-IO implementation on Java8.

## Create new instance and open

```
DMEConfig config = new DMEConfig();
config.bindSocketAddress(new InetSocketAddress("192.168.0.10", 0));
config.addConnect(new InetSocketAddress("192.168.0.1", 10940);

DME dme = DME.open(config);
```

## Receive-packet

Add Listener
```
dme.addReceiveDataListener(r -> {
  
});
```

## Send Mode-packet

```
dme.send(DMEMode.MODE_OFF, DMEMode.SELECT_OFF);
```

## Send Send-packet

```
dme.send(DMEInput.Input1_ON, DMEInput.Input2_ON);
```
