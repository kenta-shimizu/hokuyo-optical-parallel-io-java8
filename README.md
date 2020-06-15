# hokuyo-optical-parallel-io-java8

## Introduction

This library is HOKUYO Optical-Parallel-IO control implementation on Java8.  
HOKUYO DME-G/H is able to read and write 8-bit-parallel-IO-data by UDP/IP.

## Supports

- [HOKUYO DME-G/H](https://www.hokuyo-aut.co.jp/search/single.php?serial=74)

## Create new instance and open

```
DMEConfig config = new DMEConfig();
config.bindSocketAddress(new InetSocketAddress("192.168.0.10", 0));
config.addConnect(new InetSocketAddress("192.168.0.1", 10940));

DME dme = DME.open(config);
```

## Add Listener for Receive-packet

```
dme.addReceiveListener((DMEReceivePacket r) -> {

    boolean output1_on = r.isOn(DMEOutput.Output1);
    boolean output2_on = r.isOn(DMEOutput.Output2);
    boolean output3_on = r.isOn(DMEOutput.Output3);
    boolean output4_on = r.isOn(DMEOutput.Output4);
    boolean output5_on = r.isOn(DMEOutput.Output5);
    boolean output6_on = r.isOn(DMEOutput.Output6);
    boolean output7_on = r.isOn(DMEOutput.Output7);
    boolean output8_on = r.isOn(DMEOutput.Output8);

    boolean go_on    = r.isMode(DMEMode.GO_ON);
    boolean reset_on = r.isMode(DMEMode.RESET_ON);
});
```

## Send Mode-packet

```
dme.send(
    DMEMode.MODE_OFF,   /* or DMEMode.MODE_ON   */
    DMEMode.SELECT_OFF  /* or DMEMode.SELECT_ON */
    );
```

## Send Send-packet

```
dme.send(
    DMEInput.Input1_OFF, /* or DMEInput.Input1_ON */
    DMEInput.Input2_OFF, /* or DMEInput.Input2_ON */
    DMEInput.Input3_OFF, /* or DMEInput.Input3_ON */
    DMEInput.Input4_OFF, /* or DMEInput.Input4_ON */
    DMEInput.Input5_OFF, /* or DMEInput.Input5_ON */
    DMEInput.Input6_OFF, /* or DMEInput.Input6_ON */
    DMEInput.Input7_OFF, /* or DMEInput.Input7_ON */
    DMEInput.Input8_OFF  /* or DMEInput.Input8_ON */
    );
```
