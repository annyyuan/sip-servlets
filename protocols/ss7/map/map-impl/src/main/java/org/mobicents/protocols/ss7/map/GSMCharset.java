/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.mobicents.protocols.ss7.map;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * <p>
 * The encoding/decoding of 7 bits characters in USSD strings is used doing
 * GSMCharset.
 * </p>
 * <br/>
 * <p>
 * For further details look at GSM 03.38 Specs
 * </p>
 * 
 * @author amit bhayani
 * 
 */
public class GSMCharset extends Charset {

	protected static final float averageCharsPerByte = 8 / 7f;
	protected static final float maxCharsPerByte = 2f;

	protected static final float averageBytesPerChar = 1f;
	protected static final float maxBytesPerChar = 2f;

	protected static final int BUFFER_SIZE = 256;

	public GSMCharset(String canonicalName, String[] aliases) {
		super(canonicalName, aliases);
	}

	@Override
	public boolean contains(Charset cs) {
		return this.getClass().isInstance(cs);
	}

	@Override
	public CharsetDecoder newDecoder() {
		return new GSMCharsetDecoder(this, averageCharsPerByte, maxCharsPerByte);
	}

	@Override
	public CharsetEncoder newEncoder() {
		// TODO Auto-generated method stub
		return new GSMCharsetEncoder(this, averageBytesPerChar, maxBytesPerChar);
	}

	// Look at http://www.unicode.org/Public/MAPPINGS/ETSI/GSM0338.TXT
	static final int[] BYTE_TO_CHAR = { 0x0040, 0x00A3, 0x0024, 0x00A5, 0x00E8,
			0x00E9, 0x00F9, 0x00EC, 0x00F2, 0x00E7, 0x000A, 0x00D8, 0x00F8,
			0x000D, 0x00C5, 0x00E5, 0x0394, 0x005F, 0x03A6, 0x0393, 0x039B,
			0x03A9, 0x03A0, 0x03A8, 0x03A3, 0x0398, 0x039E, 0x00A0, 0x00C6,
			0x00E6, 0x00DF, 0x00C9, 0x0020, 0x0021, 0x0022, 0x0023, 0x00A4,
			0x0025, 0x0026, 0x0027, 0x0028, 0x0029, 0x002A, 0x002B, 0x002C,
			0x002D, 0x002E, 0x002F, 0x0030, 0x0031, 0x0032, 0x0033, 0x0034,
			0x0035, 0x0036, 0x0037, 0x0038, 0x0039, 0x003A, 0x003B, 0x003C,
			0x003D, 0x003E, 0x003F, 0x00A1, 0x0041, 0x0042, 0x0043, 0x0044,
			0x0045, 0x0046, 0x0047, 0x0048, 0x0049, 0x004A, 0x004B, 0x004C,
			0x004D, 0x004E, 0x004F, 0x0050, 0x0051, 0x0052, 0x0053, 0x0054,
			0x0055, 0x0056, 0x0057, 0x0058, 0x0059, 0x005A, 0x00C4, 0x00D6,
			0x00D1, 0x00DC, 0x00A7, 0x00BF, 0x0061, 0x0062, 0x0063, 0x0064,
			0x0065, 0x0066, 0x0067, 0x0068, 0x0069, 0x006A, 0x006B, 0x006C,
			0x006D, 0x006E, 0x006F, 0x0070, 0x0071, 0x0072, 0x0073, 0x0074,
			0x0075, 0x0076, 0x0077, 0x0078, 0x0079, 0x007A, 0x00E4, 0x00F6,
			0x00F1, 0x00FC, 0x00E0 };

}