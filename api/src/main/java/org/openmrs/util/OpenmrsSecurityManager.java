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
 * Helper class to determine caller classes using StackWalker API (Java 9+).
 * This replaces the deprecated SecurityManager approach.
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
		
		// Skip this method and the calling method to match original SecurityManager behavior
		// We need to skip 2 frames: this method and the method that called this method
		return STACK_WALKER.walk(frames -> 
			frames.skip(2 + callStackDepth)
				.findFirst()
				.map(StackWalker.StackFrame::getDeclaringClass)
				.orElseThrow(() -> new APIException("call.stack.depth.error", (Object[]) null))
		);
	}
	
}
