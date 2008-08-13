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
 *   17.04.2008 (thiel): created
 */
package org.knime.ext.textprocessing.nodes.frequencies;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.RowKey;
import org.knime.core.data.container.CellFactory;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.node.ExecutionMonitor;

/**
 * A {@link org.knime.core.data.container.CellFactory} adapted for frequency
 * computation nodes. 
 * Underlying implementations only have to add a data cell containing
 * a certain frequency value. 
 * The rest is done automatically by this abstract class, like
 * adding the related {@link org.knime.core.data.DataColumnSpec} specifying a 
 * double or integer value, monitoring the progress and providing the index
 * values of the term and the document cell.   
 * 
 * @author Kilian Thiel, University of Konstanz
 */
public abstract class FrequencyCellFactory implements CellFactory {

    private int m_documentColIndex = -1;
    
    private int m_termColIndex = -1;    
    
    /**
     * The name of the column to add.
     */
    protected String m_colName;
    
    /**
     * The flag specifying if a column containing integers or doubles is added.
     */
    protected boolean m_addIntCol = false;
    
    
    /**
     * Creates a new instance of <code>FrequencyCellFactory</code>.
     * 
     * @param documentCellIndex The index of the document column.
     * @param termCellindex The index of the term column.
     * @param colName The name of the column to add.
     * @param addIntCol If <code>true</code> a column containing integer values 
     * will be added otherwise a column containing doubles.
     */
    protected FrequencyCellFactory(final int documentCellIndex, 
            final int termCellindex, final String colName, 
            final boolean addIntCol) {
        m_documentColIndex = documentCellIndex;
        m_termColIndex = termCellindex;
        m_colName = colName;
        m_addIntCol = addIntCol;
    }
    

    /**
     * {@inheritDoc}
     */
    public final DataColumnSpec[] getColumnSpecs() {
        DataColumnSpec freq = null;
        if (m_addIntCol) {
            freq = new DataColumnSpecCreator(m_colName, IntCell.TYPE)
                    .createSpec();
        } else {
            freq = new DataColumnSpecCreator(m_colName, DoubleCell.TYPE)
                    .createSpec();
        }
        return new DataColumnSpec[]{freq};
    }

    /**
     * {@inheritDoc}
     */
    public final void setProgress(int curRowNr, int rowCount, RowKey lastKey,
            ExecutionMonitor exec) {
        double prog = (double)curRowNr / (double)rowCount;
        exec.setProgress(prog, "Computing frequency of row: " + curRowNr 
                + " of " + rowCount + " rows");
    }


    /**
     * @return the documentColIndex
     */
    protected final int getDocumentColIndex() {
        return m_documentColIndex;
    }


    /**
     * @return the termColIndex
     */
    protected final int getTermColIndex() {
        return m_termColIndex;
    }
}