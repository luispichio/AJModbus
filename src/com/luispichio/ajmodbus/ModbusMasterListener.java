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
 * "Escuchador" de Maestro Modbus.
 * Recibe los eventos ...
 * @author Luis Pichio | luispichio@gmail.com | https://sites.google.com/site/luispichio/ | https://github.com/luispichio
 */
public interface ModbusMasterListener {

    /**
     * Evento de respuesta genérica (todas las respuestas).
     * @param request
     * @param response
     * @return
     */
    public boolean onModbusResponse(ModbusRequest request, ModbusResponse response);

    /**
     * Evento ...
     * @param request
     * @param response
     */
    public void onModbusException(ModbusRequest request, ModbusResponse response);

    /**
     * Evento ...
     * @param request
     */
    public void onModbusTimeOut(ModbusRequest request);

    /**
     *
     * @param bytes
     */
    public void onRX(byte[] bytes);

    /**
     * 
     * @param bytes
     */
    public void onTX(byte[] bytes);

    /**
     *
     * @param slaveAddress
     * @param address
     * @param quantity
     * @param value
     * @return
     */
    public boolean onResponseReadCoils(int slaveAddress, int address, int quantity, boolean[] value);

    /**
     * Evento de respuesta a lectura de múltiples coils.
     * @param slaveAddress
     * @param address
     * @param quantity
     * @param value
     * @return
     */
    public boolean onResponseReadHoldingRegisters(int slaveAddress, int address, int quantity, int[] value);

    /**
     * Evento de respuesta a lectura de múltiples registros input.
     * @param slaveAddress
     * @param address
     * @param quantity
     * @param value
     * @return
     */
    public boolean onResponseReadInputRegisters(int slaveAddress, int address, int quantity, int[] value);

    /**
     * Evento de respuesta a escritura de coil simple.
     * @param slaveAddress
     * @param address
     * @param value
     * @return
     */
    public boolean onResponseWriteSingleCoil(int slaveAddress, int address, boolean value);

    /**
     * Evento de respuesta a escritura de registro holding simple.
     * @param slaveAddress
     * @param address
     * @param value
     * @return
     */
    public boolean onResponseWriteSingleRegister(int slaveAddress, int address, int value);

    /**
     * Evento de respuesta a escritura de múltiples coil's.
     * @param slaveAddress
     * @param address
     * @param quantity
     * @param value
     * @return
     */
    public boolean onResponseWriteMultipleCoils(int slaveAddress, int address, int quantity, boolean[] value);

    /**
     * Evento de respuesta a escritura de múltiples registros holding.
     * @param slaveAddress
     * @param address
     * @param quantity
     * @param value
     * @return
     */
    public boolean onResponseWriteMultipleRegisters(int slaveAddress, int address, int quantity, int[] value);
}
