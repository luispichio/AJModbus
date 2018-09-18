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
public class ModbusExceptionResponse extends ModbusResponse {
    public final static byte ILLEGAL_FUNCTION = 0x01;
    public final static byte ILLEGAL_DATA_ADDRESS = 0x02;
    public final static byte ILLEGAL_DATA_VALUE = 0x03;
    public final static byte SERVER_DEVICE_FAILURE = 0x04;
    public final static byte ACKNOWLEDGE = 0x05;
    public final static byte SERVER_DEVICE_BUSY = 0x06;
    public final static byte MEMORY_PARITY_ERROR = 0x08;
    public final static byte GATEWAY_PATH_UNAVAILABLE = 0x0A;
    public final static byte GATEWAY_TARGET_DEVICE_FAILED_TO_RESPOND = 0x0B;
    public int code;
}
