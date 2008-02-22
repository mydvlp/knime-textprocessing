/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright, 2003 - 2008
 * University of Konstanz, Germany
 * Chair for Bioinformatics and Information Mining (Prof. M. Berthold)
 * and KNIME GmbH, Konstanz, Germany
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.org
 * email: contact@knime.org
 * ---------------------------------------------------------------------
 * 
 * History
 *   18.02.2008 (Kilian Thiel): created
 */
package org.knime.ext.textprocessing.data;

/**
 * This interface has to be implemented by all different kind of tags such
 * as i.e. {@link org.knime.ext.textprocessing.data.PartOfSpeechTag}. It 
 * provides a method to create a valid 
 * {@link org.knime.ext.textprocessing.data.Tag} due to the given type. If
 * the underlying implementation is not responsible for the given type no
 * tag instance will be created but <code>null</code>.
 * <br/><br/>
 * To create your own tag type, i.e. named entity tag type, etc. set up an 
 * <code>enum</code> like the 
 * {@link org.knime.ext.textprocessing.data.PartOfSpeechTag}. This 
 * <code>enum</code> has to implement the interface <code>TagBuilder</code>
 * and additionally it has to provide the method 
 * <code>public static Tag getDefault()</code> which returns the default
 * instance of the tag type <code>enum</code> as a <code>TagBuilder</code>.
 * If these conditions are fulfilled, the tag type can be registered via an xml
 * file (see tagset.dtd for details) at the
 * {@link org.knime.ext.textprocessing.data.TagFactory} by calling 
 * {@link org.knime.ext.textprocessing.data.TagFactory#addTagSet(java.io.File)}.
 * For more details see: <br/>
 * {@link org.knime.ext.textprocessing.data.TagFactory} or <br/>
 * {@link org.knime.ext.textprocessing.data.PartOfSpeechTag}.
 * 
 * @author Kilian Thiel, University of Konstanz
 */
public interface TagBuilder {

    /**
     * Builds a valid {@link org.knime.ext.textprocessing.data.Tag} instance
     * if the underlying specific tag is responsible for the given tag type.
     * If the type is different to that of the underlying tag <code>null</code>
     * is returned. 
     * 
     * @param type The type of tag to create.
     * @param value The value of the tag to create.
     * @return The valid instance of tag if the type of the underlying 
     * implementation matches the given type, otherwise <code>null</code>.
     */
    public Tag buildTag(final String type, final String value);
}