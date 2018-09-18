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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luis Pichio | luispichio@gmail.com | https://sites.google.com/site/luispichio/ | https://github.com/luispichio
 */
public class ModbusSlave {
    private ModbusSlaveState mState;
    private ModbusSlaveListener mListener;
    private int mFrameTimeOut;
    private int mResponseDelay;
    
    private final InputStream mInputStream;
    private final OutputStream mOutputStream;

    private final byte[] mRXBuffer = new byte[8192];
    private int mRXBufferSize;
    private long mLastRX;
    private long mLastTX;
    
    public ModbusSlave(InputStream inputStream, OutputStream outputStream, ModbusSlaveListener listener){
        mInputStream = inputStream;
        mOutputStream = outputStream;
        mState = ModbusSlaveState.STATE_IDLE;
        mListener = listener;
        mRXBufferSize = 0;
        mLastRX = System.currentTimeMillis();
        mLastTX = System.currentTimeMillis();
        setup(100, 10);
    }
    
    public ModbusSlave(ModbusSlaveListener listener){
        this(null, null, listener);
    }
    
    public void setup(int responseDelay, int frameTimeOut){
        this.mResponseDelay = responseDelay;
        this.mFrameTimeOut = frameTimeOut;
    }
    
    private long timeFromLastRX(){
        return System.currentTimeMillis() - mLastRX;
    }
    
    private long timeFromLastTX(){
        return System.currentTimeMillis() - mLastTX;
    }
    
    private long timeFromLastRXTX() {
        return Math.min(timeFromLastRX(), timeFromLastTX());
    }
    
    private void purgeRX(){
        mRXBufferSize = 0;
    }
   
    private void sendResponse(ModbusResponse response){
        byte[] parse = null;
        if (response == null)
            return;
        if (response.getClass().equals(ModbusNormalResponse.class)){
            ModbusNormalResponse normalResponse = (ModbusNormalResponse) response;
            switch (normalResponse.function){
                case ModbusTypes.MODBUS_FUNCTION_READ_COILS:
                    parse = ModbusSlaveParser.readCoils(normalResponse.slaveAddress, normalResponse.address, normalResponse.quantity, normalResponse.value);
                break;
                case ModbusTypes.MODBUS_FUNCTION_READ_HOLDING_REGISTERS:
                    parse = ModbusSlaveParser.readHoldingRegisters(normalResponse.slaveAddress, normalResponse.address, normalResponse.quantity, normalResponse.value);
                break;
                case ModbusTypes.MODBUS_FUNCTION_READ_INPUT_REGISTERS:
                    parse = ModbusSlaveParser.readInputRegisters(normalResponse.slaveAddress, normalResponse.address, normalResponse.quantity, normalResponse.value);
                break;
                case ModbusTypes.MODBUS_FUNCTION_WRITE_SINGLE_COIL:
                    parse = ModbusSlaveParser.writeSingleCoil(normalResponse.slaveAddress, normalResponse.address, normalResponse.value[0]);
                break;
                case ModbusTypes.MODBUS_FUNCTION_WRITE_SINGLE_REGISTER:
                    parse = ModbusSlaveParser.writeSingleRegister(normalResponse.slaveAddress, normalResponse.address, normalResponse.value[0]);
                break;
                case ModbusTypes.MODBUS_FUNCTION_WRITE_MULTIPLE_COILS:
                    parse = ModbusSlaveParser.writeMultipleCoils(normalResponse.slaveAddress, normalResponse.address, normalResponse.quantity);
                break;
                case ModbusTypes.MODBUS_FUNCTION_WRITE_MULTIPLE_REGISTERS:
                    parse = ModbusSlaveParser.writeMultipleRegisters(normalResponse.slaveAddress, normalResponse.address, normalResponse.quantity);
                break;
            }
        } else {
            ModbusExceptionResponse exceptionResponse = (ModbusExceptionResponse) response;
            parse = ModbusSlaveParser.exception(exceptionResponse.slaveAddress, exceptionResponse.function, exceptionResponse.code);
        }
        if (parse != null){
            mListener.onTX(parse);
            try {
                if (mOutputStream != null)
                    mOutputStream.write(parse);
            } catch (IOException ex) {
                Logger.getLogger(ModbusSlave.class.getName()).log(Level.SEVERE, null, ex);
            }
            mLastTX = System.currentTimeMillis();
            mState = ModbusSlaveState.STATE_IDLE;
        }
    }
    
    private void processRequest(ModbusRequest request){
        switch (request.function){
            case ModbusTypes.MODBUS_FUNCTION_READ_COILS:
                sendResponse(mListener.onReadCoils(request.slaveAddress, request.function, request.address, request.quantity));
            break;
            case ModbusTypes.MODBUS_FUNCTION_READ_HOLDING_REGISTERS:
                sendResponse(mListener.onReadHoldingRegisters(request.slaveAddress, request.function, request.address, request.quantity));
            break;
            case ModbusTypes.MODBUS_FUNCTION_READ_INPUT_REGISTERS:
                sendResponse(mListener.onReadInputRegisters(request.slaveAddress, request.function, request.address, request.quantity));
            break;
            case ModbusTypes.MODBUS_FUNCTION_WRITE_SINGLE_COIL:
                sendResponse(mListener.onWriteSingleCoil(request.slaveAddress, request.function, request.address, request.value[0] != 0));
            break;
            case ModbusTypes.MODBUS_FUNCTION_WRITE_SINGLE_REGISTER:
                sendResponse(mListener.onWriteSingleRegister(request.slaveAddress, request.function, request.address, request.value[0]));
            break;
            case ModbusTypes.MODBUS_FUNCTION_WRITE_MULTIPLE_COILS:
                sendResponse(mListener.onWriteMultipleCoils(request.slaveAddress, request.function, request.address, request.quantity, ModbusUtils.int2boolean(request.value)));
            break;            
            case ModbusTypes.MODBUS_FUNCTION_WRITE_MULTIPLE_REGISTERS:
                sendResponse(mListener.onWriteMultipleRegisters(request.slaveAddress, request.function, request.address, request.quantity, request.value));
            break;
            default:
                sendResponse(ModbusResponse.exception(request.slaveAddress, request.function, ModbusExceptionResponse.ILLEGAL_FUNCTION));
        }
    }
    
    public void onRX(byte[] bytes){
        mLastRX = System.currentTimeMillis();
        if (mRXBufferSize + bytes.length < mRXBuffer.length){
            System.arraycopy(bytes, 0, mRXBuffer, mRXBufferSize, bytes.length);
            mRXBufferSize += bytes.length;
        } else
            purgeRX();
        mListener.onRX(bytes);
    }    

    private void streamRX(){
        try {
            int available;
            if ((available = mInputStream.available()) > 0){
                byte[] frame = new byte[available];
                mInputStream.read(frame);
                onRX(frame);
            }
        } catch (IOException ex) {
            Logger.getLogger(ModbusSlave.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void poll(){
        if (mInputStream != null)
            streamRX();

        switch (mState){
            case STATE_IDLE:
                if (mRXBufferSize > 0){
                    if (timeFromLastRX() >= mFrameTimeOut)
                        mState = ModbusSlaveState.STATE_REQUEST_RECEIVED;
                }
            break;
            case STATE_REQUEST_RECEIVED:
                int offset = ModbusSlaveParser.findValidADU(mRXBuffer, mRXBufferSize);
                mState = ModbusSlaveState.STATE_IDLE;
                if (offset != -1)
                    processRequest(ModbusSlaveParser.takeRequestFromADU(mRXBuffer, offset));
                purgeRX();
            break;
        }
    }
}

enum ModbusSlaveState {
    STATE_IDLE,
    STATE_REQUEST_RECEIVED,
}