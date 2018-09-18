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
 * Fraseador (parser) de Maestro Modbus RTU
 * Genera las tramas de las diferentes requerimientos Modbus.
 * Reconoce y detecta tramas (respuestas) de esclavos Modbus.
 * 
 * Basado en especificaciones de Modbus Organization Inc.
 *   MODBUS Application Protocol Specification V1.1b3
 *   MODBUS over serial line specification and implementation guide V1.02
 *   http://modbus.org/
 * 
 * @author Luis Pichio | luispichio@gmail.com | https://sites.google.com/site/luispichio/ | https://github.com/luispichio
 */
public class ModbusMasterParser {
    static byte[] readCoils(int slaveAddress, int address, int quantity){
	byte[] parse = new byte[8];
        int size = 0;
        parse[size++] = (byte) slaveAddress;
        parse[size++] = ModbusTypes.MODBUS_FUNCTION_READ_COILS;
        ModbusUtils.putWord(parse, address, size); size += 2;
        ModbusUtils.putWord(parse, quantity, size); size += 2;
        ModbusUtils.putWordFlip(parse, ModbusUtils.crc16(0xFFFF, parse, size), size); size += 2;
        return parse;
    }
    
    static byte[] readHoldingRegisters(int slaveAddress, int address, int quantity){
	byte[] parse = new byte[8];
        int size = 0;
        parse[size++] = (byte) slaveAddress;
        parse[size++] = ModbusTypes.MODBUS_FUNCTION_READ_HOLDING_REGISTERS;
        ModbusUtils.putWord(parse, address, size); size += 2;
        ModbusUtils.putWord(parse, quantity, size); size += 2;
        ModbusUtils.putWordFlip(parse, ModbusUtils.crc16(0xFFFF, parse, size), size); size += 2;
        return parse;
    }

    static byte[] readInputRegisters(int slaveAddress, int address, int quantity){
	byte[] parse = new byte[8];
        int size = 0;
        parse[size++] = (byte) slaveAddress;
        parse[size++] = ModbusTypes.MODBUS_FUNCTION_READ_INPUT_REGISTERS;
        ModbusUtils.putWord(parse, address, size); size += 2;
        ModbusUtils.putWord(parse, quantity, size); size += 2;
        ModbusUtils.putWordFlip(parse, ModbusUtils.crc16(0xFFFF, parse, size), size); size += 2;
        return parse;
    }

    static byte[] writeSingleCoil(int slaveAddress, int address, int value){
	byte[] parse = new byte[8];
        int size = 0;
        parse[size++] = (byte) slaveAddress;
        parse[size++] = ModbusTypes.MODBUS_FUNCTION_WRITE_SINGLE_COIL;
        ModbusUtils.putWord(parse, address, size); size += 2;
        ModbusUtils.putWord(parse, value, size); size += 2;
        ModbusUtils.putWordFlip(parse, ModbusUtils.crc16(0xFFFF, parse, size), size); size += 2;
        return parse;
    }
        
    static byte[] writeSingleRegister(int slaveAddress, int address, int value){
	byte[] parse = new byte[8];
        int size = 0;
        parse[size++] = (byte) slaveAddress;
        parse[size++] = ModbusTypes.MODBUS_FUNCTION_WRITE_SINGLE_REGISTER;
        ModbusUtils.putWord(parse, address, size); size += 2;
        ModbusUtils.putWord(parse, value, size); size += 2;
        ModbusUtils.putWordFlip(parse, ModbusUtils.crc16(0xFFFF, parse, size), size); size += 2;
        return parse;
    }
    
    static byte[] writeMultipleCoils(int slaveAddress, int address, int quantity, int values[]){
        byte byteCount = (byte)((quantity % 8) == 0 ? quantity / 8 : quantity / 8 + 1);
        byte[] parse = new byte[9 + byteCount];
        int size = 0;
        parse[size++] = (byte) slaveAddress;
        parse[size++] = ModbusTypes.MODBUS_FUNCTION_WRITE_MULTIPLE_COILS;
        ModbusUtils.putWord(parse, address, size); size += 2;
        ModbusUtils.putWord(parse, quantity, size); size += 2;
        parse[size++] = byteCount;
        for (int i = 0 ; i < quantity ; i++)
            if (values[i] != 0)
                parse[size + i / 8] |= 1 << (i % 8);
        size += byteCount;
        ModbusUtils.putWordFlip(parse, ModbusUtils.crc16(0xFFFF, parse, size), size); size += 2;
        return parse;
    }
    
    static byte[] writeMultipleRegisters(int slaveAddress, int address, int quantity, int values[]){
        byte[] parse = new byte[9 + 2 * values.length];
        int size = 0;
        parse[size++] = (byte) slaveAddress;
        parse[size++] = ModbusTypes.MODBUS_FUNCTION_WRITE_MULTIPLE_REGISTERS;
        ModbusUtils.putWord(parse, address, size); size += 2;
        ModbusUtils.putWord(parse, quantity, size); size += 2;
        parse[size++] = (byte) (2 * quantity);
        for (int value : values){
            ModbusUtils.putWord(parse, value, size);
            size += 2;
        }
        ModbusUtils.putWordFlip(parse, ModbusUtils.crc16(0xFFFF, parse, size), size); size += 2;
        return parse;
    }

    static byte[] readFileRecord(int slaveAddress, int fileNumber, int recordNumber, int recordLength){
        byte[] parse = new byte[12];
        int size = 0;
        parse[size++] = (byte) slaveAddress;
        parse[size++] = ModbusTypes.MODBUS_FUNCTION_READ_FILE_RECORD;
        parse[size++] = 7;
        parse[size++] = 6;
        ModbusUtils.putWord(parse, fileNumber, size); size += 2;
        ModbusUtils.putWord(parse, recordNumber, size); size += 2;
        ModbusUtils.putWord(parse, recordLength, size); size += 2;
        ModbusUtils.putWordFlip(parse, ModbusUtils.crc16(0xFFFF, parse, size), size); size += 2;
        return parse;         
    }

    static boolean validSlaveAddress(int slaveAddress, int expectedSlaveAddress){
        return slaveAddress == expectedSlaveAddress;
    }
    
    static boolean validFunction(int function, int expectedFunction){
        return (function & 0x7f) == expectedFunction; 
    }

    static boolean isException(int function){
        return (function & 0x80) == 0x80;
    }
    
    static int findValidSlaveADU(byte[] frame, int frameSize, int expectedSlaveAddress, int expectedFunction){
        int offset_begin = 0;
        int offset_end;
        while (offset_begin <= frameSize - 5){
            offset_end = offset_begin;
            if (validSlaveAddress(frame[offset_end++] & 0xFF, expectedSlaveAddress)){
                if (validFunction(frame[offset_end] & 0xFF, expectedFunction)){
                    if (isException(frame[offset_end] & 0xFF))
                        offset_end += 2;
                    else {  //normal response
                        switch (frame[offset_end++]){
                            case ModbusTypes.MODBUS_FUNCTION_READ_COILS:
                            case ModbusTypes.MODBUS_FUNCTION_READ_HOLDING_REGISTERS:
                            case ModbusTypes.MODBUS_FUNCTION_READ_INPUT_REGISTERS:
                            case ModbusTypes.MODBUS_FUNCTION_READ_FILE_RECORD:
                                int byteCount = frame[offset_end++] & 0xFF;
                                if (frame.length - offset_end < byteCount + 2){
                                    offset_begin++;
                                    continue;
                                }
                                offset_end += byteCount;
                            break;                       
                            case ModbusTypes.MODBUS_FUNCTION_WRITE_SINGLE_COIL:
                            case ModbusTypes.MODBUS_FUNCTION_WRITE_SINGLE_REGISTER:
                            case ModbusTypes.MODBUS_FUNCTION_WRITE_MULTIPLE_COILS:
                            case ModbusTypes.MODBUS_FUNCTION_WRITE_MULTIPLE_REGISTERS:
                                if (frame.length - offset_end < 6){
                                    offset_begin++;
                                    continue;
                                }
                                offset_end += 4;                   
                            break;
                        }
                    }
                    if (ModbusUtils.getWordFlip(frame, offset_end) == ModbusUtils.crc16(0xFFFF, frame, offset_end))
                        return offset_begin;
                }
            }
            offset_begin++;
        }
        return -1;
    }
     
    static ModbusResponse takeResponseFromADU(byte[] adu, int offset, ModbusRequest request){
        ModbusNormalResponse response = new ModbusNormalResponse();
        response.slaveAddress = adu[offset++] & 0xFF;
        response.function = adu[offset++] & 0xFF;
        if (isException(response.function))
            return ModbusResponse.exception(response.slaveAddress, response.function, adu[offset++] & 0xFF);
        else {
            switch (response.function){
                case ModbusTypes.MODBUS_FUNCTION_READ_COILS:
                    response.quantity = adu[offset++] & 0xFF;
                    response.quantity *= 8;
                    if (response.quantity < request.quantity)
                        return null;
                    response.address = request.address;
                    response.quantity = request.quantity;
                    response.value = new int[response.quantity];
                    for (int i = 0 ; i < response.quantity ; i++)
                        response.value[i] = (adu[offset + i / 8] & (1 << (i & 0x7))) != 0 ? 0xFF00 : 0;
                break;
                case ModbusTypes.MODBUS_FUNCTION_READ_HOLDING_REGISTERS:
                case ModbusTypes.MODBUS_FUNCTION_READ_INPUT_REGISTERS:
                    response.quantity = adu[offset++] & 0xFF;
                    response.quantity /= 2;
                    if (response.quantity != request.quantity)
                        return null;
                    response.address = request.address;
                    response.value = new int[response.quantity];
                    for (int i = 0 ; i < response.quantity ; i++){
                        response.value[i] = ModbusUtils.getWord(adu, offset); offset += 2;
                    }
                break;
                case ModbusTypes.MODBUS_FUNCTION_WRITE_SINGLE_COIL:                
                case ModbusTypes.MODBUS_FUNCTION_WRITE_SINGLE_REGISTER:
                    response.address = ModbusUtils.getWord(adu, offset); offset += 2;
                    response.quantity = 1;
                    response.value = new int[response.quantity];
                    response.value[0] = ModbusUtils.getWord(adu, offset); offset += 2;
                    if ((response.address != request.address) || (response.value[0] != request.value[0]))
                        return null;                    
                break;
                case ModbusTypes.MODBUS_FUNCTION_WRITE_MULTIPLE_COILS:
                case ModbusTypes.MODBUS_FUNCTION_WRITE_MULTIPLE_REGISTERS:
                    response.address = ModbusUtils.getWord(adu, offset); offset += 2;
                    response.quantity = ModbusUtils.getWord(adu, offset); offset += 2;
                    if ((response.quantity != request.quantity) || (response.address != request.address))
                        return null;                 
                    response.value = new int[response.quantity];
                    System.arraycopy(request.value, 0, response.value, 0, response.quantity);
                break;
                case ModbusTypes.MODBUS_FUNCTION_READ_FILE_RECORD:
                    int dataLength = adu[offset++] & 0xFF;
                    while (dataLength > 0){
                        int subRequestFileResponseLength = adu[offset++] & 0xFF; dataLength--;
                        response.quantity = (subRequestFileResponseLength - 1) / 2;
                        offset++; dataLength--; //Reference Type
                        response.value = new int[response.quantity];
                        for (int i = 0 ; i < response.quantity ; i++){
                            response.value[i] = ModbusUtils.getWord(adu, offset); offset += 2; dataLength -= 2;
                        }
                    }
                break;
            }
            return response;
        }
    } 
}