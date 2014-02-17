/* * JMEP - Java Mathematical Expression Parser. * Copyright (C) 1999  Jo Desmet *  * This library is free software; you can redistribute it and/or * modify it under the terms of the GNU Lesser General Public * License as published by the Free Software Foundation; either * version 2.1 of the License, or any later version. *  * This library is distributed in the hope that it will be useful, * but WITHOUT ANY WARRANTY; without even the implied warranty of * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU * Lesser General Public License for more details. *  * You should have received a copy of the GNU Lesser General Public * License along with this library; if not, write to the Free Software * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA *  * You can contact the Original submitter of this library by * email at: Jo_Desmet@yahoo.com. *  */ package com.googlecode.jmep;import com.googlecode.jmep.hooks.Constant;import com.googlecode.jmep.hooks.Unit;import com.googlecode.jmep.hooks.Variable;import com.googlecode.jmep.hooks.Function;import com.googlecode.jmep.hooks.BinaryOperator;import com.googlecode.jmep.hooks.UnaryOperator;import java.util.EnumMap;import java.util.Map;import java.util.HashMap;/** * The container for operators, functions, variables and units. The Environment * allows for operators to be customized to introduce new value types, or to take * into account how numbers should be constraint for a specific domain (Engineering vs Financial). * @author Jo Desmet */public class Environment {  private final Map<String,Variable> variables;  private final Map<String,Function> functions;  private final Map<String,Unit> units;  private final Map<BinaryOperatorType,Map<SimpleClassPair, BinaryOperator>> foundingBinaryOperators;  private final Map<BinaryOperatorType,Map<SimpleClassPair, BinaryOperator>> binaryOperators;  private final Map<UnaryOperatorType,Map<Class, UnaryOperator>> foundingUnaryOperators;  private final Map<UnaryOperatorType,Map<Class, UnaryOperator>> unaryOperators;  private final Expression.OperationalMode operationalMode;  private final Map<SimpleClassPair,UpgradeConversion> upgrades;  private boolean resolved;  /**   * Allocates the Expression Environment.   * @param operationalMode   */  protected Environment(Expression.OperationalMode operationalMode) {    this.operationalMode = operationalMode;    variables = new HashMap<>();    functions = new HashMap<>();    units = new HashMap<>();    foundingBinaryOperators = new EnumMap<>(BinaryOperatorType.class);    foundingUnaryOperators = new EnumMap<>(UnaryOperatorType.class);    binaryOperators = new EnumMap<>(BinaryOperatorType.class);    unaryOperators = new EnumMap<>(UnaryOperatorType.class);    upgrades = new HashMap<>();    resolved = false;  }  /**   * Adds a labeled String constant to the environment.   * @param name the label attached to the constant.   * @param value the string value of the labeled constant.   */  public <T> void addConstant(String name,final T value) {    variables.put(name,new Constant(value));  }    /**   * Adds a labeled variable to the environment. This is done by   * using an adapter class. You can either use an inner class or   * an anonymous class for this purpose.   * @param name the label attached to the variable.   * @param variable the variable call-back instance.   * @see Variable   */  public void addVariable(String name,Variable variable) {    variables.put(name,variable);  }  /**   * Returns a map containing all the variables and constants. The   * contents will be of type: String, Double, Integer or Variable.   * Note that you can interact directly with this map.   * @see Environment#addConstant   * @see Environment#addVariable   */  Map<String,Variable> getVariables() {    return variables;  }    /**   * Returns a map containing all the units. The contents will be   * only of type Unit.   * Note that you can interact directly with this map.   * @see Environment#addUnit   */  Map<String,Unit> getUnits() {    return units;  }    /**   * Returns a map containing all the functions. The contents will be   * only of type Function.   * Note that you can interact directly with this map.   * @see Environment#addFunction   */  Map<String,Function> getFunctions() {    return functions;  }    /**   * Returns the labeled variable. Depending how the variable was   * added, this could be: String, Double, Integer or Variable.   * @see Environment#addConstant   * @see Environment#addVariable   */  Variable getVariable(String name) {    return variables.get(name);  }    /**   * Returns the labeled unit.   * @see Environment#addUnit   */  Unit getUnit(String name) {    return (Unit)units.get(name);  }    /**   * Returns the labeled function.   * @see Environment#addFunction   */  Function getFunction(String name) {    return (Function)functions.get(name);  }    /**   * Adds a Function to the environment. This is done by using an   * adapter class. You can either us an inner class or an anonymous   * class for this purpose.   * @param name the label attached to the added function.   * @param function the function call-back instance.   * @see Function   */  final public void addFunction(String name,Function function) {    functions.put(name,function);    resolved = false;  }    /**   * Adds a Unit to the environment. This is done by using an   * adapter class. You can either us an inner class or an anonymous   * class for this purpose.   * @param name the label attached to the added unit.   * @param unit the unit call-back instance.   * @see Unit   */  final public void addUnit(String name,Unit unit) {    units.put(name,unit);    resolved = false;  }  final public <T> void register(UnaryOperatorType operatorType,Class<T> t,final UnaryOperator<T> operator) {    Map<Class,UnaryOperator> implementations = foundingUnaryOperators.get(operatorType);    if (implementations == null) {      implementations = new HashMap<>();      foundingUnaryOperators.put(operatorType, implementations);    }    resolved = false;    implementations.put(t,operator);  }  final public <T,U> void register(BinaryOperatorType operatorType,Class<T> t,Class<U> u,final BinaryOperator<T,U> operator) {    Map<SimpleClassPair,BinaryOperator> implementations = foundingBinaryOperators.get(operatorType);    if (implementations == null) {      implementations = new HashMap<>();      foundingBinaryOperators.put(operatorType, implementations);    }    resolved = false;    implementations.put(SimpleClassPair.of(t,u),operator);    if (t != u && operatorType.isCommutative())  {      // If operator is Commutative, then automatically store the commutative version if not already exists.      SimpleClassPair<U,T> p = SimpleClassPair.of(u, t);      if (!implementations.containsKey(p)) {        implementations.put(p, new BinaryOperator<U,T>() {          @Override          public Object apply(U uu, T tt) {            return operator.apply(tt,uu);          }        } );      }    }  }      final public <T,U> void register(Class<T> t,Class<U> u,final UpgradeConversion<T,U> upgrade) {    resolved = false;    upgrades.put(SimpleClassPair.of(t,u),upgrade);  }      final Map<UnaryOperatorType,Map<Class, UnaryOperator>> getUnaryOperators() {    if (!resolved) resolve();    return this.unaryOperators;  }    final Map<BinaryOperatorType,Map<SimpleClassPair, BinaryOperator>> getBinaryOperators() {    if (!resolved) resolve();    return this.binaryOperators;  }    /**   * provides a default implementation based on the provided Operational Mode. The returned   * Environment can be further customized after retrieving.   * @param operationalMode indicates how operators will be evaluated, and typically has an effect on   * how rounding and internal storage of numbers take place.   * @return an instance of a default Environment.   */  static public Environment getInstance(Expression.OperationalMode operationalMode) {    switch (operationalMode) {      case BASIC: return new BasicEnvironment();      case FINANCIAL: return new FinancialEnvironment();    }    return new Environment(operationalMode);  }    public Expression.OperationalMode getOperationalMode() {    return this.operationalMode;  }    static private class UpgradedBinaryOperator implements BinaryOperator {    private final UpgradeConversion upgradeConversion1;    private final UpgradeConversion upgradeConversion2;    private final BinaryOperator operator;        UpgradedBinaryOperator(UpgradeConversion upgradeConversion,BinaryOperator operator) {      this(upgradeConversion,operator,null);    }    UpgradedBinaryOperator(BinaryOperator operator,UpgradeConversion upgradeConversion) {      this(null,operator,upgradeConversion);    }    UpgradedBinaryOperator(UpgradeConversion upgradeConversion1,BinaryOperator operator,UpgradeConversion upgradeConversion2) {      this.upgradeConversion1 = upgradeConversion1;      this.upgradeConversion2 = upgradeConversion2;      this.operator = operator;    }    @Override    public Object apply(Object t, Object u) {      return operator.apply(upgradeConversion1==null?t:upgradeConversion1.apply(t), upgradeConversion2==null?u:upgradeConversion2.apply(u));    }  }    /*   * This function creates extra versions of operators for new Operand Types, using   * the Upgrade Conversions.  */  private void upgradeBinaryOperators(BinaryOperatorType type,Map<SimpleClassPair, BinaryOperator> implementations) {    Map<SimpleClassPair, BinaryOperator> upgradedImplementations = new HashMap<>();    for (Map.Entry<SimpleClassPair,BinaryOperator> e:implementations.entrySet()) {      SimpleClassPair operatorClassPair = e.getKey();      Class operator1Class = operatorClassPair.t;      Class operator2Class = operatorClassPair.u;      BinaryOperator operator = e.getValue();      for (Map.Entry<SimpleClassPair,UpgradeConversion> ee:upgrades.entrySet()) {        SimpleClassPair conversionClassPair = ee.getKey();        Class sourceClass = conversionClassPair.t;        Class targetClass = conversionClassPair.u;        UpgradeConversion conversion = ee.getValue();        if (targetClass.equals(operator1Class)) {          SimpleClassPair pair = SimpleClassPair.of(sourceClass,operator2Class);          if (!this.binaryOperators.get(type).containsKey(pair)) {            BinaryOperator upgradedOperator = new UpgradedBinaryOperator(conversion,operator);            upgradedImplementations.put(pair, upgradedOperator);          }          if (targetClass.equals(operator2Class)) {            pair = SimpleClassPair.of(sourceClass,sourceClass);            if (!this.binaryOperators.get(type).containsKey(pair)) {              BinaryOperator upgradedOperator = new UpgradedBinaryOperator(conversion,operator,conversion);              upgradedImplementations.put(pair, upgradedOperator);            }          }        }        if (targetClass.equals(operator2Class)) {          SimpleClassPair pair = SimpleClassPair.of(operator1Class,sourceClass);          if (!this.binaryOperators.get(type).containsKey(pair)) {            BinaryOperator upgradedOperator = new UpgradedBinaryOperator(operator,conversion);            upgradedImplementations.put(pair, upgradedOperator);          }        }      }    }    if (!upgradedImplementations.isEmpty()) {      this.binaryOperators.get(type).putAll(upgradedImplementations);      upgradeBinaryOperators(type,upgradedImplementations);    }  }  private void upgradeBinaryOperators() {    for (Map.Entry<BinaryOperatorType,Map<SimpleClassPair,BinaryOperator>> e:this.binaryOperators.entrySet()) {      BinaryOperatorType type = e.getKey();      Map<SimpleClassPair,BinaryOperator> implementations = e.getValue();      upgradeBinaryOperators(type,implementations);    }  }    final public void resolve() {    binaryOperators.clear();    binaryOperators.putAll(foundingBinaryOperators);    unaryOperators.clear();    unaryOperators.putAll(foundingUnaryOperators);    upgradeBinaryOperators();    resolved = true;  }  }