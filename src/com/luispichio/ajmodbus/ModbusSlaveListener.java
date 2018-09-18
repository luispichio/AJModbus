/*
 * The MIT License
 *
 * Copyright 2018 Luis Pichio.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.luispichio.ajmodbus;

/**
 *
 * @author Luis Pichio | luispichio@gmail.com | https://sites.google.com/site/luispichio/ | https://github.com/luispichio
 */
public interface ModbusSlaveListener {

    /**
     * Evento de requerimiento genérico (todos los requerimientos).
     * @param request
     * @return
     */
    public ModbusResponse onRequest(ModbusRequest request);

    /**
     * Evento de lectura de múltiples coils.
     * @param slaveAddress
     * @param function
     * @param address
     * @param quantity
     * @return
     */
    public ModbusResponse onReadCoils(int slaveAddress, int function, int address, int quantity);

    /**
     * Evento de lectura de múltiples registros holding.
     * @param slaveAddress
     * @param function
     * @param address
     * @param quantity
     * @return
     */
    public ModbusResponse onReadHoldingRegisters(int slaveAddress, int function, int address, int quantity);

    /**
     * Evento de lectura de múltiples registros input.
     * @param slaveAddress
     * @param function
     * @param address
     * @param quantity
     * @return
     */
    public ModbusResponse onReadInputRegisters(int slaveAddress, int function, int address, int quantity);

    /**
     * Evento de escritura de coil simple.
     * @param slaveAddress
     * @param function
     * @param address
     * @param value
     * @return
     */
    public ModbusResponse onWriteSingleCoil(int slaveAddress, int function, int address, boolean value);

    /**
     * Evento de escritura de registro holding simple.
     * @param slaveAddress
     * @param function
     * @param address
     * @param value
     * @return
     */
    public ModbusResponse onWriteSingleRegister(int slaveAddress, int function, int address, int value);

    /**
     * Evento de escritura de múltiples coils.
     * @param slaveAddress
     * @param function
     * @param address
     * @param quantity
     * @param values
     * @return
     */
    public ModbusResponse onWriteMultipleCoils(int slaveAddress, int function, int address, int quantity, boolean[] values);

    /**
     * Evento de escritura de múltiples registros holding.
     * @param slaveAddress
     * @param function
     * @param address
     * @param quantity
     * @param values
     * @return
     */
    public ModbusResponse onWriteMultipleRegisters(int slaveAddress, int function, int address, int quantity, int[] values);
    
    /**
     * Evento de recepción de tramas.
     * @param bytes
     */
    public void onRX(byte[] bytes);

    /**
     * Evento de transmisión de tramas.
     * @param bytes Trama a transmitir / que será transmitida por stream.
     */
    public void onTX(byte[] bytes);
}
