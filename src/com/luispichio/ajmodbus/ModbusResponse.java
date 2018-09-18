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

import java.util.Arrays;

/**
 *
 * @author Luis Pichio | luispichio@gmail.com | https://sites.google.com/site/luispichio/ | https://github.com/luispichio
 */
public class ModbusResponse {
    public int slaveAddress;
    public int function;
    
    public static ModbusNormalResponse readCoils(int slaveAddress, int address, int quantity, int value[]){
        ModbusNormalResponse response = new ModbusNormalResponse();
        response.function = ModbusTypes.MODBUS_FUNCTION_READ_COILS;
        response.slaveAddress = slaveAddress;
        response.address = address;
        response.quantity = quantity;
        response.value = Arrays.copyOf(value, quantity);
        return response;
    }
    
    public static ModbusNormalResponse readCoils(int slaveAddress, int address, int quantity, boolean value[]){
        return readCoils(slaveAddress, address, quantity, ModbusUtils.boolean2int(value));
    }
    
    public static ModbusNormalResponse readHoldingRegisters(int slaveAddress, int address, int quantity, int value[]){
        ModbusNormalResponse response = new ModbusNormalResponse();
        response.function = ModbusTypes.MODBUS_FUNCTION_READ_HOLDING_REGISTERS;
        response.slaveAddress = slaveAddress;
        response.address = address;
        response.quantity = quantity;
        response.value = Arrays.copyOf(value, quantity);
        return response;        
    }
    
    public static ModbusNormalResponse readInputRegisters(int slaveAddress, int address, int quantity, int value[]){
        ModbusNormalResponse response = new ModbusNormalResponse();
        response.function = ModbusTypes.MODBUS_FUNCTION_READ_INPUT_REGISTERS;
        response.slaveAddress = slaveAddress;
        response.address = address;
        response.quantity = quantity;
        response.value = Arrays.copyOf(value, quantity);
        return response;         
    }
    
    public static ModbusNormalResponse writeSingleCoil(int slaveAddress, int address, int value){
        ModbusNormalResponse response = new ModbusNormalResponse();
        response.function = ModbusTypes.MODBUS_FUNCTION_WRITE_SINGLE_COIL;
        response.slaveAddress = slaveAddress;
        response.address = address;
        response.quantity = 1;
        response.value = new int[response.quantity];
        response.value[0] = value;
        return response;         
    }
    
    public static ModbusNormalResponse writeSingleCoil(int slaveAddress, int address, boolean value){
        ModbusNormalResponse response = new ModbusNormalResponse();
        response.function = ModbusTypes.MODBUS_FUNCTION_WRITE_SINGLE_COIL;
        response.slaveAddress = slaveAddress;
        response.address = address;
        response.quantity = 1;
        response.value = new int[response.quantity];
        response.value[0] = value ? 0xFF00 : 0x0000;
        return response;         
    }    
    
    public static ModbusNormalResponse writeSingleRegister(int slaveAddress, int address, int value){
        ModbusNormalResponse response = new ModbusNormalResponse();
        response.function = ModbusTypes.MODBUS_FUNCTION_WRITE_SINGLE_REGISTER;
        response.slaveAddress = slaveAddress;
        response.address = address;
        response.quantity = 1;
        response.value = new int[response.quantity];
        response.value[0] = value;
        return response;         
    }
    
   public static ModbusNormalResponse writeMultipleCoils(int slaveAddress, int address, int quantity){
        ModbusNormalResponse response = new ModbusNormalResponse();
        response.function = ModbusTypes.MODBUS_FUNCTION_WRITE_MULTIPLE_COILS;
        response.slaveAddress = slaveAddress;
        response.address = address;
        response.quantity = quantity;
        return response;         
    }
   
   public static ModbusNormalResponse writeMultipleRegisters(int slaveAddress, int address, int quantity){
        ModbusNormalResponse response = new ModbusNormalResponse();
        response.function = ModbusTypes.MODBUS_FUNCTION_WRITE_MULTIPLE_REGISTERS;
        response.slaveAddress = slaveAddress;
        response.address = address;
        response.quantity = quantity;
        return response;         
    }
    
    public static ModbusExceptionResponse exception(int slaveAddress, int function, int code){
        ModbusExceptionResponse response = new ModbusExceptionResponse();
        response.slaveAddress = slaveAddress;
        response.function = function;
        response.code = code;
        return response;
    }   
}
