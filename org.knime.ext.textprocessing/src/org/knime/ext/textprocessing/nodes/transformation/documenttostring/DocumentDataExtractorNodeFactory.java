/*
 * -------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright, 2003 - 2007
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
 * -------------------------------------------------------------------
 *
 * History
 *    09.12.2008 (Tobias Koetter): created
 */

package org.knime.ext.textprocessing.nodes.transformation.documenttostring;

import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;


/**
 *
 * @author Tobias Koetter, University of Konstanz
 */
public class DocumentDataExtractorNodeFactory
    extends NodeFactory<DocumentDataExtractorNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected DocumentDataExtractorNodeDialog createNodeDialogPane() {
        return new DocumentDataExtractorNodeDialog();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DocumentDataExtractorNodeModel createNodeModel() {
        return new DocumentDataExtractorNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<DocumentDataExtractorNodeModel> createNodeView(
            final int viewIndex, 
            final DocumentDataExtractorNodeModel nodeModel) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getNrNodeViews() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean hasDialog() {
        return true;
    }

}