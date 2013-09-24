/*******************************************************************************
 * Copyright (c) 2011, Nathan Sweet <nathan.sweet@gmail.com>
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/

package com.esotericsoftware.tablelayout;

/** Base class for a table or cell property value. Values are provided a table or cell for context. Eg, the value may compute its
 * size taking into consideration the size of the table or the widget in the cell. Some values may be only valid for use with
 * either call.
 * @author Nathan Sweet */
abstract public class Value<C, T extends C> {
    
    protected final Toolkit<C,T> toolkit;
    
    Value(Toolkit<C,T> toolkit) {
        this.toolkit = toolkit;        
    }
    
	/** Returns the value in the context of the specified table. */
	abstract public float get (T table);

	/** Returns the value in the context of the specified cell. */
	abstract public float get (Cell<C, T> cell);

	/** Returns the value in the context of a width for the specified table. */
	public float width (T table) {
		return toolkit.width(get(table));
	}

	/** Returns the value in the context of a height for the specified table. */
	public float height (T table) {
		return toolkit.height(get(table));
	}

	/** Returns the value in the context of a width for the specified cell. */
	public float width (Cell<C, T> cell) {
		return toolkit.width(get(cell));
	}

	/** Returns the value in the context of a height for the specified cell. */
	public float height (Cell<C,T> cell) {
		return toolkit.height(get(cell));
	}

	/** Returns a value that is a percentage of the table's width. */
	static public <C, T extends C> Value<C, T> percentWidth (Toolkit<C, T> toolkit, final float percent) {
		return new TableValue<C, T>(toolkit) {
			public float get (T table) {
				return toolkit.getWidth(table) * percent;
			}
		};
	}

	/** Returns a value that is a percentage of the table's height. */
	static public <C, T extends C> Value<C, T> percentHeight (Toolkit<C, T> toolkit, final float percent) {
		return new TableValue<C, T>(toolkit) {
			public float get (T table) {
				return toolkit.getHeight(table) * percent;
			}
		};
	}

	/** Returns a value that is a percentage of the specified widget's width. */
	static public <C, T extends C> Value<C, T> percentWidth (Toolkit<C, T> toolkit, final float percent, final C widget) {
		return new Value<C, T>(toolkit) {
			public float get (Cell<C, T> cell) {
				return toolkit.getWidth(widget) * percent;
			}

			public float get (Object table) {
				return toolkit.getWidth(widget) * percent;
			}
		};
	}

	/** Returns a value that is a percentage of the specified widget's height. */
	static public <C, T extends C> Value<C, T> percentHeight (Toolkit<C, T> toolkit, final float percent, final C widget) {
		return new TableValue<C, T>(toolkit) {
			public float get (Object table) {
				return toolkit.getHeight(widget) * percent;
			}
		};
	}
}
