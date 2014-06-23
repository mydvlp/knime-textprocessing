/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * Created on 25.11.2013 by Kilian Thiel
 */

package org.knime.ext.textprocessing.nodes.tagging;

import java.util.concurrent.atomic.AtomicInteger;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataType;
import org.knime.core.data.container.AbstractCellFactory;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;
import org.knime.ext.textprocessing.data.Document;
import org.knime.ext.textprocessing.data.DocumentValue;
import org.knime.ext.textprocessing.util.TextContainerDataCellFactory;
import org.knime.ext.textprocessing.util.TextContainerDataCellFactoryBuilder;

/**
 * Cell Factory, creating new document cells of tagged documents. The cell factory uses thread local tagger instances
 * for parallel tagging.
 *
 * @author Kilian Thiel, KNIME.com, Zurich, Switzerland
 * @since 2.9
 */
final class TaggerCellFactory extends AbstractCellFactory {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(TaggerCellFactory.class);

    private final DocumentTaggerFactory m_taggerFac;

    private final int m_docColIndex;

    private final TextContainerDataCellFactory m_documentCellFac;

    private DocumentTagger[] m_taggerPool = null;

    private AtomicInteger m_taggerCount = new AtomicInteger(0);

    /**
     * Constructor for class TaggerCellFactory, with tagger factory, index of the document column, new column specs,
     * number of threads to use and execution context to set.
     *
     * @param taggerFac The factory, creating tagger instances for thread local use.
     * @param documentColIndex The index of the column containing the documents to tag.
     * @param newColSpecs The spec of the create columns.
     * @param numberOfThreads The number of threads to use.
     * @param exec The execution context.
     */
    public TaggerCellFactory(final DocumentTaggerFactory taggerFac, final int documentColIndex,
        final DataColumnSpec[] newColSpecs, final int numberOfThreads, final ExecutionContext exec) {
        super(true, newColSpecs);
        this.setParallelProcessing(true, numberOfThreads, 20 * numberOfThreads);

        LOGGER.debug("Tagging with " + numberOfThreads + " parallel threads.");

        m_taggerFac = taggerFac;
        m_docColIndex = documentColIndex;
        m_documentCellFac = TextContainerDataCellFactoryBuilder.createDocumentCellFactory();
        m_documentCellFac.prepare(exec);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataCell[] getCells(final DataRow row) {
        DocumentTagger tagger;
        try {
            tagger = getTaggerFromPool();
        } catch (Exception e) {
            LOGGER.error("Tagger could not be borrowed from pool.", e);
            return new DataCell[] {DataType.getMissingCell()};
        }

        final Document d = ((DocumentValue)row.getCell(m_docColIndex)).getDocument();

        synchronized (tagger) {
            return new DataCell[]{m_documentCellFac.createDataCell(tagger.tag(d))};
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterProcessing() {
        // clean up thread local tagger models here!
        LOGGER.debug("Clearing tagger pool.");
        for (int i = 0; i < m_taggerPool.length; i++) {
            m_taggerPool[i] = null;
        }
        m_taggerPool = null;
        m_taggerCount = new AtomicInteger(0);
    }

    private synchronized DocumentTagger getTaggerFromPool() throws Exception {
        // initialize pool with number of threads to use if it has not been initialized
        if (m_taggerPool == null) {
            LOGGER.debug("Creating tagger pool of size: " + getMaxParallelWorkers());
            m_taggerPool = new DocumentTagger[getMaxParallelWorkers()];
        }
        // get tagger from pool
        final int index = m_taggerCount.get() % getMaxParallelWorkers();
        DocumentTagger tagger = m_taggerPool[index];
        // initialize tagger if it has not been initialized and put to pool
        if (tagger == null) {
            tagger = m_taggerFac.createTagger();
            m_taggerPool[index] = tagger;
            LOGGER.debug("Created new pooled tagger instance: " + tagger.getClass()
                + " (" + (m_taggerCount.get() + 1) + ")");
        }

        // update current index of tagger in pool
        m_taggerCount.incrementAndGet();

        return tagger;
    }
}