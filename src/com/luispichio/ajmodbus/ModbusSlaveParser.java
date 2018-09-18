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
public class ModbusSlaveParser {
    static byte[] readCoils(int slaveAddress, int address, int quantity, int value[]){
	byte[] parse = new byte[256];
        byte byteCount = (byte)((quantity % 8) == 0 ? quantity / 8 : quantity / 8 + 1);
        int size = 0;
        parse[size++] = (byte) (slaveAddress & 0xff);
        parse[size++] = ModbusTypes.MODBUS_FUNCTION_READ_COILS;
        parse[size++] = byteCount;
        for (int i = 0 ; i < quantity ; i++)
            if (value[i] != 0)
                parse[size + i / 8] |= 1 << (i & 0x7);
        size += byteCount;
        ModbusUtils.putWordFlip(parse, ModbusUtils.crc16(0xFFFF, parse, size), size); size += 2;
        return Arrays.copyOf(parse, size);
    }
    
    static byte[] readHoldingRegisters(int slaveAddress, int address, int quantity, int values[]){
	byte[] parse = new byte[256];
        int size = 0;
        parse[size++] = (byte) (slaveAddress & 0xff);
        parse[size++] = ModbusTypes.MODBUS_FUNCTION_READ_HOLDING_REGISTERS;
        parse[size++] = (byte)(2 * quantity);
        for (int i = 0 ; i < quantity ; i++){
            ModbusUtils.putWord(parse, values[i], size); size += 2;
        }
        ModbusUtils.putWordFlip(parse, ModbusUtils.crc16(0xFFFF, parse, size), size); size += 2;
        return Arrays.copyOf(parse, size);
    }

    static byte[] readInputRegisters(int slaveAddress, int address, int quantity, int values[]){
	byte[] parse = new byte[256];
        int size = 0;
        parse[size++] = (byte) (slaveAddress & 0xff);
        parse[size++] = ModbusTypes.MODBUS_FUNCTION_READ_INPUT_REGISTERS;
        parse[size++] = (byte)(2 * quantity);
        for (int i = 0 ; i < quantity ; i++){
            ModbusUtils.putWord(parse, values[i], size); size += 2;
        }
        ModbusUtils.putWordFlip(parse, ModbusUtils.crc16(0xFFFF, parse, size), size); size += 2;
        return Arrays.copyOf(parse, size);
    }

    static byte[] writeSingleCoil(int slaveAddress, int address, int value){
	byte[] parse = new byte[256];
        int size = 0;
        parse[size++] = (byte) (slaveAddress & 0xff);
        parse[size++] = ModbusTypes.MODBUS_FUNCTION_WRITE_SINGLE_COIL;
        ModbusUtils.putWord(parse, address, size); size += 2;
        ModbusUtils.putWord(parse, value, size); size += 2;
        ModbusUtils.putWordFlip(parse, ModbusUtils.crc16(0xFFFF, parse, size), size); size += 2;
        return Arrays.copyOf(parse, size);
    }
        
    static byte[] writeSingleRegister(int slaveAddress, int address, int value){
	byte[] parse = new byte[256];
        int size = 0;
        parse[size++] = (byte) (slaveAddress & 0xff);
        parse[size++] = ModbusTypes.MODBUS_FUNCTION_WRITE_SINGLE_REGISTER;
        ModbusUtils.putWord(parse, address, size); size += 2;
        ModbusUtils.putWord(parse, value, size); size += 2;
        ModbusUtils.putWordFlip(parse, ModbusUtils.crc16(0xFFFF, parse, size), size); size += 2;
        return Arrays.copyOf(parse, size);
    }
    
    static byte[] writeMultipleCoils(int slaveAddress, int address, int quantity){
	byte[] parse = new byte[256];
        int size = 0;
        parse[size++] = (byte) (slaveAddress & 0xff);
        parse[size++] = ModbusTypes.MODBUS_FUNCTION_WRITE_MULTIPLE_COILS;
        ModbusUtils.putWord(parse, address, size); size += 2;
        ModbusUtils.putWord(parse, quantity, size); size += 2;
        ModbusUtils.putWordFlip(parse, ModbusUtils.crc16(0xFFFF, parse, size), size); size += 2;
        return Arrays.copyOf(parse, size);
    }    
    
    static byte[] writeMultipleRegisters(int slaveAddress, int address, int quantity){
	byte[] parse = new byte[256];
        int size = 0;
        parse[size++] = (byte) (slaveAddress & 0xff);
        parse[size++] = ModbusTypes.MODBUS_FUNCTION_WRITE_MULTIPLE_REGISTERS;
        ModbusUtils.putWord(parse, address, size); size += 2;
        ModbusUtils.putWord(parse, quantity, size); size += 2;
        ModbusUtils.putWordFlip(parse, ModbusUtils.crc16(0xFFFF, parse, size), size); size += 2;
        return Arrays.copyOf(parse, size);
    }

    static byte[] exception(int slaveAddress, int function, int code){
	byte[] parse = new byte[256];
        int size = 0;
        parse[size++] = (byte) slaveAddress;
        parse[size++] = (byte) (0x80 | function);
        parse[size++] = (byte) code;
        ModbusUtils.putWordFlip(parse, ModbusUtils.crc16(0xFFFF, parse, size), size); size += 2;
        return Arrays.copyOf(parse, size);        
    }
    
    static boolean validAddressSlave(int address){
        return address < 247;
    }
    
    static boolean validFunction(int function){
        switch (function){
            case ModbusTypes.MODBUS_FUNCTION_READ_COILS:
            case ModbusTypes.MODBUS_FUNCTION_READ_HOLDING_REGISTERS:
            case ModbusTypes.MODBUS_FUNCTION_READ_INPUT_REGISTERS:
            case ModbusTypes.MODBUS_FUNCTION_WRITE_SINGLE_COIL:
            case ModbusTypes.MODBUS_FUNCTION_WRITE_SINGLE_REGISTER:
            case ModbusTypes.MODBUS_FUNCTION_WRITE_MULTIPLE_COILS:
            case ModbusTypes.MODBUS_FUNCTION_WRITE_MULTIPLE_REGISTERS:
                return true;
        }
        return false;
    }
    
    static int findValidADU(byte[] frame, int frameSize){
        int offset_begin = 0;
        int offset_end;
        while (offset_begin <= frameSize - 8){
            offset_end = offset_begin;
            if (validAddressSlave(frame[offset_end++] & 0xFF)){
                if (validFunction(frame[offset_end] & 0xFF)){
                    switch (frame[offset_end++]){
                        case ModbusTypes.MODBUS_FUNCTION_READ_COILS:
                        case ModbusTypes.MODBUS_FUNCTION_READ_HOLDING_REGISTERS:
                        case ModbusTypes.MODBUS_FUNCTION_READ_INPUT_REGISTERS:
                        case ModbusTypes.MODBUS_FUNCTION_WRITE_SINGLE_COIL:
                        case ModbusTypes.MODBUS_FUNCTION_WRITE_SINGLE_REGISTER:
                            offset_end += 4;
                        break;                       
                        case ModbusTypes.MODBUS_FUNCTION_WRITE_MULTIPLE_COILS:
                        case ModbusTypes.MODBUS_FUNCTION_WRITE_MULTIPLE_REGISTERS:
                            offset_end += 4;
                            int byteCount = frame[offset_end++] & 0xFF;
                            if (frame.length - offset_end < byteCount + 2){
                                offset_begin++;
                                continue;
                            }
                            offset_end += byteCount;
                        break;
                    }
                    if (ModbusUtils.getWordFlip(frame, offset_end) == ModbusUtils.crc16(0xFFFF, frame, offset_end))
                        return offset_begin;
                }
            }
            offset_begin++;
        }
        return -1;
    }
     
    static ModbusRequest takeRequestFromADU(byte[] adu, int offset){
        ModbusRequest request = new ModbusRequest();
        request.slaveAddress = adu[offset++] & 0xFF;
        request.function = adu[offset++] & 0xFF;
        switch (request.function){
            case ModbusTypes.MODBUS_FUNCTION_READ_COILS:
            case ModbusTypes.MODBUS_FUNCTION_READ_HOLDING_REGISTERS:
            case ModbusTypes.MODBUS_FUNCTION_READ_INPUT_REGISTERS:
                request.address = ModbusUtils.getWord(adu, offset);
                offset += 2;
                request.quantity = ModbusUtils.getWord(adu, offset);
                offset += 2;
            break;
            case ModbusTypes.MODBUS_FUNCTION_WRITE_SINGLE_COIL:
            case ModbusTypes.MODBUS_FUNCTION_WRITE_SINGLE_REGISTER:
                request.address = ModbusUtils.getWord(adu, offset);
                offset += 2;
                request.value = new int[1];
                request.value[0] = ModbusUtils.getWord(adu, offset);
                offset += 2;
            break;
            case ModbusTypes.MODBUS_FUNCTION_WRITE_MULTIPLE_COILS:
                request.address = ModbusUtils.getWord(adu, offset);
                offset += 2;
                request.quantity = ModbusUtils.getWord(adu, offset);
                offset += 2;
                offset++;
                request.value = new int[request.quantity];
                for (int i = 0 ; i < request.quantity ; i++)
                    request.value[i] = (adu[offset + i / 8] & (1 << (i % 8))) != 0 ? 0xFF00 : 0x0000;
            break;
            case ModbusTypes.MODBUS_FUNCTION_WRITE_MULTIPLE_REGISTERS:
                request.address = ModbusUtils.getWord(adu, offset);
                offset += 2;
                request.quantity = ModbusUtils.getWord(adu, offset);
                offset += 2;
                offset++;
                request.value = new int[request.quantity];
                for (int i = 0 ; i < request.quantity ; i++){
                    request.value[i] = ModbusUtils.getWord(adu, offset);
                    offset += 2;
                }
            break;
            default:
                return null;
        }
        return request;
    }
}