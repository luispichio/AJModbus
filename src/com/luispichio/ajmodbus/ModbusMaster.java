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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luis Pichio | luispichio@gmail.com | https://sites.google.com/site/luispichio/ | https://github.com/luispichio
 */
public class ModbusMaster {
    private ModbusMasterState mState = ModbusMasterState.STATE_IDLE;
    private final ModbusMasterListener mListener;
    public int responseTimeOut;
    public int frameTimeOut;
    public int turnAroundDelay;
    public int retrys;

    private final InputStream mInputStream;
    private final OutputStream mOutputStream;

    private final byte[] mRXBuffer = new byte[8192];
    private int mRXBufferSize = 0;
    long mLastRX;
    long mLastTX;
    
    private final ArrayList<ModbusRequest> mRequestTail;
    private ModbusRequest mCurrentRequest;
    
    /**
     * Constructor de la clase
     * @param inputStream Stream de entrada de datos (recepción)
     * @param outputStream Stream de salida de datos (transmisión)
     * @param listener "Escuchador" que recibirá los eventos del Mastro Modbus.
     */
    public ModbusMaster(InputStream inputStream, OutputStream outputStream, ModbusMasterListener listener){
        mInputStream = inputStream;
        mOutputStream = outputStream;
        mListener = listener;
        mRequestTail = new ArrayList<>();
        mLastRX = System.currentTimeMillis();
        mLastTX = System.currentTimeMillis();
        setup(1000, 10, 300, 0);
    }

    /**
     * Constructor de la clase
     * @param listener "Escuchador" que recibirá los eventos del Mastro Modbus.
     */
    public ModbusMaster(ModbusMasterListener listener){
        this(null, null, listener);
    }
    
    /**
     * Configuración de parámetros del Maestro Modbus
     * @param responseTimeOut Tiempo máximo de respuesta de un esclavo hasta
     * reintento o timeout.
     * @param frameTimeOut Tiempo [ms] de "corte" de trama (fin de paquete).
     * @param turnAroundDelay Tiempo mínimo entre requerimientos.
     * @param retrys Cantidad de reintentos.
     */
    public void setup(int responseTimeOut, int frameTimeOut, int turnAroundDelay, int retrys){
        this.responseTimeOut = responseTimeOut;
        this.frameTimeOut = frameTimeOut;
        this.turnAroundDelay = turnAroundDelay;
        this.retrys = retrys;
    }
    
    /**
     * Genera y encola (para posterior envío) requerimiento de coils.
     * Código de función: 0x01
     * @param slaveAddress Dirección de esclavo.
     * @param address Dirección de primera Coil.
     * @param quantity Cantidad de Coil's.
     * @return true
     */
    public boolean readCoils(int slaveAddress, int address, int quantity){
        ModbusRequest request = new ModbusRequest();
        request.function = ModbusTypes.MODBUS_FUNCTION_READ_COILS;
        request.slaveAddress = slaveAddress;
        request.address = address;
        request.quantity = quantity;
        request.retrys = retrys;
        mRequestTail.add(request);
        return true;
    }
    
    /**
     * Genera y encola (para posterior envío) requerimiento de registros holding.
     * Código de función: 0x03
     * @param slaveAddress Dirección de esclavo.
     * @param address Dirección de primer registro.
     * @param quantity Cantidad de registros.
     * @return true
     */
    public boolean readHoldingRegisters(int slaveAddress, int address, int quantity){
        ModbusRequest request = new ModbusRequest();
        request.function = ModbusTypes.MODBUS_FUNCTION_READ_HOLDING_REGISTERS;
        request.slaveAddress = slaveAddress;
        request.address = address;
        request.quantity = quantity;
        request.retrys = retrys;
        mRequestTail.add(request);
        return true;
    }
    
    /**
     * Genera y encola (para posterior envío) requerimiento de registro input.
     * Código de función: 0x04
     * @param slaveAddress Dirección de esclavo.
     * @param address Dirección de primer registro.
     * @param quantity Cantidad de registros.
     * @return true
     */
    public boolean readInputRegisters(int slaveAddress, int address, int quantity){
        ModbusRequest request = new ModbusRequest();
        request.function = ModbusTypes.MODBUS_FUNCTION_READ_INPUT_REGISTERS;
        request.slaveAddress = slaveAddress;
        request.address = address;
        request.quantity = quantity;
        request.retrys = retrys;        
        mRequestTail.add(request);
        return true;
    }
    
    /**
     * Escritura de coil simple
     * Código de función: 0x05
     * @param slaveAddress Dirección de esclavo.
     * @param address Dirección del ooil
     * @param value Valor del coil
     * @return true
     */
    public boolean writeSingleCoil(int slaveAddress, int address, boolean value){
        ModbusRequest request = new ModbusRequest();
        request.function = ModbusTypes.MODBUS_FUNCTION_WRITE_SINGLE_COIL;
        request.slaveAddress = slaveAddress;
        request.address = address;
        request.value = new int[1];
        request.value[0] = value ? 0xFF00 : 0x0000;
        request.retrys = retrys;
        mRequestTail.add(request);
        return true;
    }
    
    /**
     * Escritura de registro holding simple.
     * Código de función: 0x06
     * @param slaveAddress Dirección de esclavo.
     * @param address Dirección del registro a escribir.
     * @param value Valor a escribir.
     * @return true
     */
    public boolean writeSingleRegister(int slaveAddress, int address, int value){
        ModbusRequest request = new ModbusRequest();
        request.function = ModbusTypes.MODBUS_FUNCTION_WRITE_SINGLE_REGISTER;
        request.slaveAddress = slaveAddress;
        request.address = address;
        request.value = new int[1];
        request.value[0] = value;
        request.retrys = retrys;
        mRequestTail.add(request);
        return true;
    }

    /**
     * Escritura de múltiples coil's.
     * Código de función: 0x0F
     * @param slaveAddress Dirección de esclavo.
     * @param address Dirección del primer coil.
     * @param quantity Cantidad de coils.
     * @param value Vector con valores a escribir.
     * @return true
     */
    public boolean writeMultipleCoils(int slaveAddress, int address, int quantity, int value[]){
        ModbusRequest request = new ModbusRequest();
        request.function = ModbusTypes.MODBUS_FUNCTION_WRITE_MULTIPLE_COILS;
        request.slaveAddress = slaveAddress;
        request.address = address;
        request.quantity = quantity;
        request.value = value;
        request.retrys = retrys;
        mRequestTail.add(request);
        return true;
    }

    /**
     * Escritura de múltiples coil's.
     * Código de función: 0x0F
     * @param slaveAddress Dirección de esclavo.
     * @param address Dirección del primer coil.
     * @param quantity Cantidad de coils.
     * @param value Vector con valores a escribir.
     * @return true
     */
    public boolean writeMultipleCoils(int slaveAddress, int address, int quantity, boolean value[]){
        return writeMultipleCoils(slaveAddress, address, quantity, ModbusUtils.boolean2int(value));
    }

    /**
     * Escritura de múltiples registros holding.
     * Código de función: 0x10
     * @param slaveAddress Dirección de esclavo.
     * @param address Dirección del primer registro a escribir.
     * @param quantity Cantidad de registros.
     * @param value Vector con valores a escribir.
     * @return true
     */
    public boolean writeMultipleRegisters(int slaveAddress, int address, int quantity, int value[]){
        ModbusRequest request = new ModbusRequest();
        request.function = ModbusTypes.MODBUS_FUNCTION_WRITE_MULTIPLE_REGISTERS;
        request.slaveAddress = slaveAddress;
        request.address = address;
        request.quantity = quantity;
        request.value = Arrays.copyOf(value, quantity);
        request.retrys = retrys;
        mRequestTail.add(request);
        return true;
    }

    /**
     * @param slaveAddress
     * @param fileNumber
     * @param recordNumber
     * @param recordLength
     * @return 
     */
    public boolean readFileRecord(int slaveAddress, int fileNumber, int recordNumber, int recordLength){
        ModbusRequest request = new ModbusRequest();
        request.function = ModbusTypes.MODBUS_FUNCTION_READ_FILE_RECORD;
        request.slaveAddress = slaveAddress;
        request.fileNumber = fileNumber;
        request.recordNumber = recordNumber;
        request.recordLength = recordLength;
        request.retrys = retrys;
        mRequestTail.add(request);
        return true;
    }    
 
    private void doRequest(){
        mCurrentRequest = mRequestTail.get(0);
        byte[] parse = null;
        switch (mCurrentRequest.function){
            case ModbusTypes.MODBUS_FUNCTION_READ_COILS:
                parse = ModbusMasterParser.readCoils(mCurrentRequest.slaveAddress, mCurrentRequest.address, mCurrentRequest.quantity);
            break;                       
            case ModbusTypes.MODBUS_FUNCTION_READ_HOLDING_REGISTERS:
                parse = ModbusMasterParser.readHoldingRegisters(mCurrentRequest.slaveAddress, mCurrentRequest.address, mCurrentRequest.quantity);
            break;                       
            case ModbusTypes.MODBUS_FUNCTION_READ_INPUT_REGISTERS:
                parse = ModbusMasterParser.readInputRegisters(mCurrentRequest.slaveAddress, mCurrentRequest.address, mCurrentRequest.quantity);
            break;                       
            case ModbusTypes.MODBUS_FUNCTION_WRITE_SINGLE_COIL:
                parse = ModbusMasterParser.writeSingleCoil(mCurrentRequest.slaveAddress, mCurrentRequest.address, mCurrentRequest.value[0]);
            break;
            case ModbusTypes.MODBUS_FUNCTION_WRITE_SINGLE_REGISTER:
                parse = ModbusMasterParser.writeSingleRegister(mCurrentRequest.slaveAddress, mCurrentRequest.address, mCurrentRequest.value[0]);
            break;
            case ModbusTypes.MODBUS_FUNCTION_WRITE_MULTIPLE_COILS:
                parse = ModbusMasterParser.writeMultipleCoils(mCurrentRequest.slaveAddress, mCurrentRequest.address, mCurrentRequest.quantity, mCurrentRequest.value);
            break;                
            case ModbusTypes.MODBUS_FUNCTION_WRITE_MULTIPLE_REGISTERS:
                parse = ModbusMasterParser.writeMultipleRegisters(mCurrentRequest.slaveAddress, mCurrentRequest.address, mCurrentRequest.quantity, mCurrentRequest.value);
            break;                    
            case ModbusTypes.MODBUS_FUNCTION_READ_FILE_RECORD:
                parse = ModbusMasterParser.readFileRecord(mCurrentRequest.slaveAddress, mCurrentRequest.fileNumber, mCurrentRequest.recordNumber, mCurrentRequest.recordLength);
            break;                       
        }
        mState = ModbusMasterState.STATE_IDLE;
        if (parse != null){
            purgeRX();
            mListener.onTX(parse);
            try {
                if (mOutputStream != null)
                    mOutputStream.write(parse);
            } catch (IOException ex) {
                Logger.getLogger(ModbusMaster.class.getName()).log(Level.SEVERE, null, ex);
            }
            mLastTX = System.currentTimeMillis();
            if (mCurrentRequest.slaveAddress != 0)
                mState = ModbusMasterState.STATE_WAIT_FOR_RESPONSE;
        }
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
    
    /**
     * Método de entrada de datos (recepción) alternativo al InputStream.
     * @param bytes Trama (bytes) recibidos.
     */
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
            Logger.getLogger(ModbusMaster.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean dispatchResponse(ModbusRequest request, ModbusNormalResponse response){
        boolean result;
        //respuesta genérica
        if (!(result = mListener.onModbusResponse(request, response))){
            //respuesta específica
            switch (response.function){
                case ModbusTypes.MODBUS_FUNCTION_READ_COILS:
                    result = mListener.onResponseReadCoils(response.slaveAddress, response.address, response.quantity, ModbusUtils.int2boolean(response.value));
                break;
                case ModbusTypes.MODBUS_FUNCTION_READ_HOLDING_REGISTERS:
                    result = mListener.onResponseReadHoldingRegisters(response.slaveAddress, response.address, response.quantity, response.value);
                break;
                case ModbusTypes.MODBUS_FUNCTION_READ_INPUT_REGISTERS:
                    result = mListener.onResponseReadInputRegisters(response.slaveAddress, response.address, response.quantity, response.value);
                break;
                case ModbusTypes.MODBUS_FUNCTION_WRITE_SINGLE_COIL:
                    result = mListener.onResponseWriteSingleCoil(response.slaveAddress, response.address, response.value[0] != 0);
                break;
                case ModbusTypes.MODBUS_FUNCTION_WRITE_SINGLE_REGISTER:
                    result = mListener.onResponseWriteSingleRegister(response.slaveAddress, response.address, response.value[0]);
                break;
                case ModbusTypes.MODBUS_FUNCTION_WRITE_MULTIPLE_COILS:
                    result = mListener.onResponseWriteMultipleCoils(response.slaveAddress, response.address, response.quantity, ModbusUtils.int2boolean(response.value));
                break;            
                case ModbusTypes.MODBUS_FUNCTION_WRITE_MULTIPLE_REGISTERS:
                    result = mListener.onResponseWriteMultipleRegisters(response.slaveAddress, response.address, response.quantity, response.value);
                break;
            }        
        }
        return result;
    }
    
    /**
     * Polling de máquina de estados.
     * Realiza los requerimientos, procesa las respuestas y genera los eventos.
     * Debe llamarse de fórma períodica (~10ms).
     */
    public void poll(){
        if (mInputStream != null)
            streamRX();
        switch (mState){
            case STATE_IDLE:
                if (mRequestTail.size() > 0 && timeFromLastRXTX() >= turnAroundDelay)
                    doRequest();
            break;
            case STATE_WAIT_FOR_RESPONSE:
                if (mRXBufferSize > 0){
                    if (timeFromLastRX() >= frameTimeOut){
//                        System.out.println(Arrays.toString(Arrays.copyOf(mRXBuffer, mRXBufferSize)));
                        mState = ModbusMasterState.STATE_RESPONSE_RECEIVED;
                    }
                } else {
                    if (timeFromLastTX() >= responseTimeOut){
                        mListener.onModbusTimeOut(mCurrentRequest);
                        mState = ModbusMasterState.STATE_IDLE;
                        if (mCurrentRequest.retrys > 0)
                            mCurrentRequest.retrys--;
                        else {
                            mRequestTail.remove(mCurrentRequest);
                        }
                    }                    
                }
            break;
            case STATE_RESPONSE_RECEIVED:
                boolean done = false;
                int offset = ModbusMasterParser.findValidSlaveADU(mRXBuffer, mRXBufferSize, mCurrentRequest.slaveAddress, mCurrentRequest.function);
                mState = ModbusMasterState.STATE_IDLE;
                if (offset != -1){
                    ModbusResponse response = ModbusMasterParser.takeResponseFromADU(mRXBuffer, offset, mCurrentRequest);
                    if (response != null){
                        done = false;
                        if (response.getClass().equals(ModbusNormalResponse.class))
                            done = dispatchResponse(mCurrentRequest, (ModbusNormalResponse)response);
                        else
                            mListener.onModbusException(mCurrentRequest, response);
                    }
                }
                if (done)
                    mRequestTail.remove(mCurrentRequest);
                else {
                    if (mCurrentRequest.retrys > 0)
                        mCurrentRequest.retrys--;
                    else 
                        mRequestTail.remove(mCurrentRequest);
                }
                purgeRX();
            break;
        }
    }

    /**
     * Verifica vaciado de cola de requerimientos.
     * Permite determinar cuando el Maestro Modbus a finalizado todos los
     * requerimientos pendientes.
     * @return true en caso de que la cola esté vacía
     */
    public boolean emptyRequestTail() {
        return mRequestTail.isEmpty();
    }
    
    /**
     * Retorna cantidad de requerimientos pendientes del Maestro Modbus.
     * @return Cantidad de requerimientos pendientes
     */
    public int pendingRequestCount() {
        return mRequestTail.size();
    }    
}

enum ModbusMasterState {
    STATE_IDLE,
    STATE_WAIT_FOR_RESPONSE,
    STATE_RESPONSE_RECEIVED,
}