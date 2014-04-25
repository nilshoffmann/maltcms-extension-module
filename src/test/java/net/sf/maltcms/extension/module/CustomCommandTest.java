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

import cross.commands.fragments.IFragmentCommand;
import cross.datastructures.workflow.IWorkflow;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import maltcms.test.AFragmentCommandTest;
import maltcms.test.ZipResourceExtractor;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link CustomCommand}.
 *
 * @author Nils Hoffmann
 */
@Slf4j
public class CustomCommandTest extends AFragmentCommandTest {

    @Test
    public void testInvalidParameterValidation() {

        CustomCommand cc = new CustomCommand();
        cc.setMyCustomNumericParameter(50);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<CustomCommand>> s = validator.validate(cc);
        Assert.assertFalse(s.isEmpty());
        log.warn("Violations: " + s);
    }

    @Test
    public void testValidParameterValidation() {

        CustomCommand cc = new CustomCommand();
        cc.setMyCustomNumericParameter(10);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<CustomCommand>> s = validator.validate(cc);
        Assert.assertTrue(s.isEmpty());
    }

    @Test
    public void testCustomCommand() throws IOException {
        File dataFolder = tf.newFolder("customCommandTest");
        File inputFile1 = ZipResourceExtractor.extract(
                "/cdf/1D/glucoseA.cdf.gz", dataFolder);
        File inputFile2 = ZipResourceExtractor.extract(
                "/cdf/1D/glucoseB.cdf.gz", dataFolder);
        File outputBase = tf.newFolder("customCommandTestOut");
        List<IFragmentCommand> commands = new ArrayList<>();
        CustomCommand cc = new CustomCommand();
        cc.setMyCustomNumericParameter(5);
        cc.setMyCustomParameter("customParameter");
        commands.add(cc);
        IWorkflow w = createWorkflow(outputBase, commands, Arrays.asList(
                inputFile1, inputFile2));
        testWorkflow(w);
    }
}
