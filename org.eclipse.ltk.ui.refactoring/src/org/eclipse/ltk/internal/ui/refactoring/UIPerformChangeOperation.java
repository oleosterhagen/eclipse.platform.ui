/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ltk.internal.ui.refactoring;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

import org.eclipse.core.resources.ResourcesPlugin;

import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CreateChangeOperation;
import org.eclipse.ltk.core.refactoring.PerformChangeOperation;
import org.eclipse.jface.wizard.IWizardContainer;

public class UIPerformChangeOperation extends PerformChangeOperation {

	private Display fDisplay;
	private IWizardContainer fWizardContainer;
	
	public UIPerformChangeOperation(Display display, Change change, IWizardContainer container) {
		super(change);
		fDisplay= display;
		fWizardContainer= container;
	}

	public UIPerformChangeOperation(Display display, CreateChangeOperation op, IWizardContainer container) {
		super(op);
		fDisplay= display;
		fWizardContainer= container;
	}
	
	protected void executeChange(final IProgressMonitor pm) throws CoreException {
		if (fDisplay != null && !fDisplay.isDisposed()) {
			final CoreException[] exception= new CoreException[1];
			final ISchedulingRule rule= ResourcesPlugin.getWorkspace().getRoot();
			final Thread callerThread= Thread.currentThread();
			Runnable r= new Runnable() {
				public void run() {
					try {
						final Button cancel= getCancelButton();
						boolean enabled= true;
						if (cancel != null && !cancel.isDisposed()) {
							enabled= cancel.isEnabled();
							cancel.setEnabled(false);
						}
						try {
							UIPerformChangeOperation.super.executeChange(pm);
						} finally {
							if (cancel != null && !cancel.isDisposed()) {
								cancel.setEnabled(enabled);
							}
						}
					} catch (CoreException e) {
						exception[0]= e;
					} finally {
						Platform.getJobManager().transferRule(rule, callerThread);
					}
				}
			};
			Platform.getJobManager().transferRule(rule, fDisplay.getThread());
			fDisplay.syncExec(r);
			if (exception[0] != null)
				throw new CoreException(exception[0].getStatus());
		} else {
			super.executeChange(pm);
		}
	}

	private Button getCancelButton() {
		if (fWizardContainer instanceof RefactoringWizardDialog2) {
			return ((RefactoringWizardDialog2)fWizardContainer).getCancelButton();
		} else if (fWizardContainer instanceof RefactoringWizardDialog) {
			return ((RefactoringWizardDialog)fWizardContainer).getCancelButton();
		}
		return null;
	}
}
