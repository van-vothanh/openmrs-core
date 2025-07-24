/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.util;

import org.openmrs.api.APIException;

/**
 * Helper class to get caller class information from the execution stack.
 * This replaces the deprecated SecurityManager approach with the modern StackWalker API.
 */
public class OpenmrsSecurityManager {
	
	private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
	
	/**
	 * Returns the class on the current execution stack at the given depth. 0 is the most recently
	 * called class.
	 *
	 * @param callStackDepth
	 * @return the most recently called class.
	 * @throws APIException if given a callStackDepth less than zero
	 * <strong>Should</strong> get the most recently called method
	 * <strong>Should</strong> throw an error if given a subzero call stack level
	 */
	public Class<?> getCallerClass(int callStackDepth) {
		if (callStackDepth < 0) {
			throw new APIException("call.stack.depth.error", (Object[]) null);
		}
		
		// Use StackWalker to get the caller class
		// Skip this method (getCallerClass) and the constructor call, then return the class at the requested depth
		return STACK_WALKER.walk(stream -> 
			stream.skip(2) // Skip this getCallerClass method and the constructor call
				  .skip(callStackDepth) // Skip to the requested depth
				  .findFirst()
				  .map(StackWalker.StackFrame::getDeclaringClass)
				  .orElseThrow(() -> new RuntimeException("call.stack.depth.error"))
		);
	}
}
