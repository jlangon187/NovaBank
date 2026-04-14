package com.jlanzasg.novabank.service;

public interface OperacionService {
    void ingresar(Double cantidad);
    void retirar(Double cantidad);
    void transferir(Double cantidad, String ibanOrigen, String ibanDestino);
}
