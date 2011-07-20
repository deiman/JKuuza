package com.github.mefi.jkuuza.analyzer;

import com.github.mefi.jkuuza.parser.ContentExtractor;
import com.github.mefi.jkuuza.parser.MethodInfo;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

/**
 * Encapsulates Java Reflection into static functions to retrieving informations of methods and help to calling them.
 *
 * @author Marek Pilecky
 */
public class Reflector {

	/**
	 * Gets methods and its parameters from class and retuns it as Methods instance
	 *
	 * @param c class
	 * @return instance of Methods class
	 */
	public static Methods getDeclaredMethodsWithInfo(Class c) {

		Methods methods = new Methods();
		java.lang.reflect.Method[] dMethods = c.getDeclaredMethods();

		for (java.lang.reflect.Method dMethod : dMethods) {
			if (dMethod.getAnnotation(MethodInfo.class) != null) {
				Method method = new Method();
				MethodInfo annotation = dMethod.getAnnotation(MethodInfo.class);
				Type[] types = dMethod.getGenericParameterTypes();

				String returnType = dMethod.getReturnType().toString();
				returnType = returnType.substring(returnType.toString().lastIndexOf(".") + 1);

				String annotationParameters = annotation.parameters();
				String packageName = c.getName().substring(0, c.getName().lastIndexOf("."));
				String className = c.getName().substring(c.getName().lastIndexOf(".") + 1);

				String[] paramNames;
				if (annotationParameters.contains(",")) {
					paramNames = annotationParameters.replaceAll(", ", ",").split(",");
				} else if (annotationParameters.contains(";")) {
					paramNames = annotationParameters.replaceAll("; ", ";").split(";");
				} else {
					paramNames = new String[1];
					paramNames[0] = annotationParameters;
				}

				method.setClassName(className);
				method.setPackageName(packageName);
				method.setName(dMethod.getName());
				method.setDescription(annotation.description());
				method.setReturnType(returnType);
				for (int i = 0; i < paramNames.length; i++) {
					String type = types[i].toString().substring(types[i].toString().lastIndexOf(".") + 1);
					method.addParameter(paramNames[i], type);
				}
				methods.add(method);
			}
		}
		return methods;
	}

	/**
	 * Run specified method from instance with parameters
	 *
	 * @param o Instance of class whose method will be runned
	 * @param methodName name of the method to be runned
	 * @param params array of parameters, must be in order defined in method
	 * @return Object with value returned by called method
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static Object call(Object o, String methodName, Object[] params) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {

		Object object = null;
		java.lang.reflect.Method[] methods = o.getClass().getDeclaredMethods();

		for (java.lang.reflect.Method method : methods) {
			Type[] types = method.getGenericParameterTypes();
			if (types.length <= params.length) {
				String sType = "";
				boolean validParams = true;
				for (int i = 0; i < types.length; i++) {
					// "java.lang.Integer", "java.lang.String", etc..
					sType = types[i].toString().substring(types[i].toString().lastIndexOf(" ") + 1, types[i].toString().length());

					if (!Class.forName(sType).isInstance(params[i])) {
						validParams = false;
					}
				}

				if (method.getName().equals(methodName) && (validParams)) {
					object = method.invoke(o, params);
				}
			}
		}
		return object;
	}

	/**
	 * Run specified method from instance with one parameter
	 *
	 * @param o Instance of class whose method will be runned
	 * @param methodName name of the method to be runned
	 * @param param1 parameter to be used
	 * @return Object with value returned by called method
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static Object call(Object o, String methodName, Object param1) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		Object params[] = {param1};
		return call(o, methodName, params);
	}

	/**
	 * Run specified method from instance with two parameters
	 *
	 * @param o Instance of class whose method will be runned
	 * @param methodName name of the method to be runned
	 * @param param1 first parameter to be used
	 * @param param2 second parameter to be used
	 * @return Object with value returned by called method
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static Object call(Object o, String methodName, Object param1, Object param2) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		Object params[] = {param1, param2};
		return call(o, methodName, params);
	}

	/**
	 * Run specified method from instance with three parameters
	 *
	 * @param o Instance of class whose method will be runned
	 * @param methodName name of the method to be runned
	 * @param param1 first parameter to be used
	 * @param param1 second parameter to be used
	 * @param param1 third parameter to be used
	 * @return Object with value returned by called method
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static Object call(Object o, String methodName, Object param1, Object param2, Object param3) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		Object params[] = {param1, param2, param3};
		return call(o, methodName, params);
	}

	/**
	 * Run specified method from class with array of parameters
	 *
	 * @param c class whose method will be runned
	 * @param methodName name of the method to be runned
	 * @param params array of parameters, must be in order defined in method
	 * @return Object with value returned by called method
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static Object call(Class c, String methodName, Object[] params) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		return call(c.newInstance(), methodName, params);
	}

	/**
	 * Run specified method from class with one parameter
	 *
	 * @param c class whose method will be runned
	 * @param methodName name of the method to be runned
	 * @param param1 first parameter to be used
	 * @return Object with value returned by called method
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static Object call(Class c, String methodName, Object param1) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		Object params[] = {param1};
		return call(c.newInstance(), methodName, params);
	}

	/**
	 * Run specified method from class with two parameters
	 *
	 * @param c class whose method will be runned
	 * @param methodName name of the method to be runned
	 * @param param1 first parameter to be used
	 * @param param2 second parameter to be used
	 * @return Object with value returned by called method
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static Object call(Class c, String methodName, Object param1, Object param2) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		Object params[] = {param1, param2};
		return call(c.newInstance(), methodName, params);
	}

	/**
	 * Run specified method from class with three parameters
	 *
	 * @param c class whose method will be runned
	 * @param methodName name of the method to be runned
	 * @param param1 first parameter to be used
	 * @param param2 second parameter to be used
	 * @param param3 third parameter to be used
	 * @return Object with value returned by called method
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static Object call(Class c, String methodName, Object param1, Object param2, Object param3) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		Object params[] = {param1, param2, param3};
		return call(c.newInstance(), methodName, params);
	}

	static Methods getDeclaredMethods(Class<ContentExtractor> aClass) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
