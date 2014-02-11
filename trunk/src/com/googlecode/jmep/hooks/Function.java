/* * JMEP - Java Mathematical Expression Parser. * Copyright (C) 1999  Jo Desmet *  * This library is free software; you can redistribute it and/or * modify it under the terms of the GNU Lesser General Public * License as published by the Free Software Foundation; either * version 2.1 of the License, or any later version. *  * This library is distributed in the hope that it will be useful, * but WITHOUT ANY WARRANTY; without even the implied warranty of * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU * Lesser General Public License for more details. *  * You should have received a copy of the GNU Lesser General Public * License along with this library; if not, write to the Free Software * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA *  * You can contact the Original submitter of this library by * email at: Jo_Desmet@yahoo.com. *  */package com.googlecode.jmep.hooks;/** * Interface that defines a skeleton for a Function call-back adaptor. * It should be implemented in order to use it with the addFunction method of * Environment. A way to accomplish this is by using in-line adaptor classes:<P> *<ul><pre><code>oEnv.addFunction("sin", *  new Function() { *    public Object call(Object [] oPars) { *      if (oPars == null) return null; *      if (oPars.length != 1) return null; *      if (oPars[0] instanceof Double || oPars[0] instanceof Integer) *        return new Double(Math.sin(((Number)oPars[0]).doubleValue())); *      return null; *    } *  } *);</code></pre></ul> * * @see com.googlecode.jmep.Environment */public interface Function {  /**   * Defines the function's behaviour. This method is expected to check the   * arguments validity, and in case of any problems (unsupported argument   * type or count) it should return <code>null</code>.   * @param oPars the arguments in left-to-right appearance order.   * @return the function result or <code>null</code> in case of problems. Supported   * return types are: <code>String</code>, <code>Integer</code> and <code>Double</code>.   */  public Object call(Object[] oPars);}