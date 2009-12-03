/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.e4.workbench.ui.internal;

import java.util.Collection;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.e4.workbench.modeling.IModelReconcilingService;
import org.eclipse.e4.workbench.modeling.ModelDelta;
import org.eclipse.e4.workbench.modeling.ModelReconciler;

public class ModelReconcilingService implements IModelReconcilingService {

	public ModelReconciler createModelReconciler() {
		return new XMLModelReconciler();
	}

	public IStatus applyDeltas(Collection<ModelDelta> deltas) {
		return applyDeltas(deltas, null);
	}

	public IStatus applyDeltas(Collection<ModelDelta> deltas, String[] filters) {
		MultiStatus multiStatus = new MultiStatus(Activator.PI_WORKBENCH, 0, "", null); //$NON-NLS-1$
		for (ModelDelta delta : deltas) {
			IStatus status = delta.apply();
			multiStatus.add(status);
		}
		return multiStatus;
	}

}
