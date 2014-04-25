/* 
 * Maltcms, modular application toolkit for chromatography-mass spectrometry. 
 * Copyright (C) 2008-2014, The authors of Maltcms. All rights reserved.
 *
 * Project website: http://maltcms.sf.net
 *
 * Maltcms may be used under the terms of either the
 *
 * GNU Lesser General Public License (LGPL)
 * http://www.gnu.org/licenses/lgpl.html
 *
 * or the
 *
 * Eclipse Public License (EPL)
 * http://www.eclipse.org/org/documents/epl-v10.php
 *
 * As a user/recipient of Maltcms, you may choose which license to receive the code 
 * under. Certain files or entire directories may not be covered by this 
 * dual license, but are subject to licenses compatible to both LGPL and EPL.
 * License exceptions are explicitly declared in all relevant files or in a 
 * LICENSE file in the relevant directories.
 *
 * Maltcms is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. Please consult the relevant license documentation
 * for details.
 */
package net.sf.maltcms.extension.module;

import cross.annotations.Configurable;
import cross.commands.fragments.AFragmentCommand;
import cross.datastructures.fragments.IFileFragment;
import cross.datastructures.tuple.TupleND;
import cross.datastructures.workflow.WorkflowSlot;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import maltcms.datastructures.ms.Chromatogram1D;
import net.sf.maltcms.extension.module.worker.ScanCounter;
import net.sf.mpaxs.api.ICompletionService;
import org.openide.util.lookup.ServiceProvider;

/**
 * Custom Command to show how to create a command that is available in
 * cross/maltcms with parallelization using mpaxs.
 *
 * @author Nils Hoffmann
 */
@Slf4j
@Data
@ServiceProvider(service = AFragmentCommand.class)
public class CustomParallelCommand extends AFragmentCommand {

    //javax.validation constraints
    @NotNull
    @Configurable(description = "This is a custom parameter. Set the value between ... and ...", value = "")
    private String myCustomParameter;
    //javax.validation constraints
    @Max(10)
    @Min(0)
    @Configurable(description = "This is another parameter.", value = "1")
    private int myCustomNumericParameter = 1;
    private final WorkflowSlot workflowSlot = WorkflowSlot.GENERAL_PREPROCESSING;

    @Override
    public String getDescription() {
        return "Prints the number of MS scans of the provided chromatogram files to stdout.";
    }

    @Override
    public TupleND<IFileFragment> apply(TupleND<IFileFragment> in) {
        //print some info about the class
        log.info("{}", getDescription());
        ICompletionService<Integer> ics = createCompletionService(Integer.class);
        for (IFileFragment f : in) {
            ics.submit(new ScanCounter(f.getUri()));
        }
        try {
            for (Integer i : ics.call()) {
                log.info("Chromatogram has {} scans!", i);
            }
        } catch (Exception ex) {
            log.error("Caught exception while waiting for results!", ex);
        }
        return in;
    }

}
