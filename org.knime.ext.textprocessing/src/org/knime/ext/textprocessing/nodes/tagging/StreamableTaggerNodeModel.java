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
 * ------------------------------------------------------------------------
 *
 * History
 *   Aug 20, 2014 (thiel): created
 */
package org.knime.ext.textprocessing.nodes.tagging;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.streamable.InputPortRole;
import org.knime.core.node.streamable.OutputPortRole;
import org.knime.core.node.streamable.PartitionInfo;
import org.knime.core.node.streamable.PortInput;
import org.knime.core.node.streamable.PortObjectInput;
import org.knime.core.node.streamable.PortOutput;
import org.knime.core.node.streamable.StreamableOperator;
import org.knime.ext.textprocessing.util.DataTableSpecVerifier;
import org.knime.ext.textprocessing.util.TextContainerDataCellFactory;
import org.knime.ext.textprocessing.util.TextContainerDataCellFactoryBuilder;

/**
 * Abstract definition of a node that applies a tagger using a {@link ColumnRearranger}.
 *
 * @author Kilian Thiel, KNIME.com, Berlin, Germany
 * @since 2.11
 */
public abstract class StreamableTaggerNodeModel extends NodeModel implements DocumentTaggerFactory {

    private InputPortRole[] m_roles;

    /** The settings model storing the number of threads to use for tagging. */
    private SettingsModelIntegerBounded m_numberOfThreadsModel = TaggerNodeSettingsPane.getNumberOfThreadsModel();

    /**
     * Default constructor, defining one data input and one data output port.
     */
    public StreamableTaggerNodeModel() {
        this(1, new InputPortRole[] {});
    }

    /**
     * Constructor defining a specified number of data input and one data output port.
     * @param dataInPorts The number of data input ports.
     * @param roles The roles of the input ports after the first port.
     */
    public StreamableTaggerNodeModel(final int dataInPorts, final InputPortRole[] roles) {
        super(dataInPorts, 1);

        if (roles.length != dataInPorts - 1) {
            throw new IllegalArgumentException(
                "Number of input port roles must be equal to number of data in ports -1!");
        }
        m_roles = new InputPortRole[dataInPorts];
        m_roles[0] = InputPortRole.DISTRIBUTED_STREAMABLE;
        for (int i = 1; i < m_roles.length; i++) {
            m_roles[i] = roles[i - 1];
        }
    }

    /** {@inheritDoc} */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
        throws Exception {
        prepareTagger(inData, exec);
        final ColumnRearranger rearranger = createColumnRearranger(inData[0].getDataTableSpec());
        return new BufferedDataTable[]{exec.createColumnRearrangeTable(inData[0], rearranger, exec)};
    }

    /** {@inheritDoc} */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
        checkInputDataTableSpecs(inSpecs);

        DataTableSpec in = inSpecs[0];
        ColumnRearranger r = createColumnRearranger(in);
        DataTableSpec out = r.createSpec();
        return new DataTableSpec[]{out};
    }

    /**
     * Method to check specs of input data tables. This method can be overwritten to apply specific checks.
     *
     * @param inSpecs Specs of the input data tables.
     * @throws InvalidSettingsException If settings or specs of input data tables are invalid.
     */
    protected void checkInputDataTableSpecs(final DataTableSpec[] inSpecs) throws InvalidSettingsException { }

    /**
     * Method to prepare the tagger model. This method can be overwritten to apply loading of data from input data
     * tables for tagging.
     * @param inData Input data tables.
     * @param exec The execution context of the node.
     * @throws Exception If tagger can not be prepared.
     */
    protected void prepareTagger(final BufferedDataTable[] inData, final ExecutionContext exec) throws Exception { }

    /**
     * Creates column rearranger for creation of new data table.
     * @param in the input data table spec.
     * @return A new instance of column rearranger to create output data table
     * @throws InvalidSettingsException If tagger instance can not be created.
     * @since 3.1
     */
    protected final ColumnRearranger createColumnRearranger(final DataTableSpec in) throws InvalidSettingsException {
        DataTableSpecVerifier verfier = new DataTableSpecVerifier(in);
        verfier.verifyDocumentCell(true);
        final int docColIndex = verfier.getDocumentCellIndex();

        final TextContainerDataCellFactory docFactory = TextContainerDataCellFactoryBuilder.createDocumentCellFactory();
        DataColumnSpec docCol = new DataColumnSpecCreator("Document", docFactory.getDataType()).createSpec();

        final TaggerCellFactory cellFac =
            new TaggerCellFactory(this, docColIndex, docCol, m_numberOfThreadsModel.getIntValue());
        final ColumnRearranger rearranger = new ColumnRearranger(in);
        rearranger.replace(cellFac, docColIndex);
        rearranger.keepOnly(docColIndex);

        return rearranger;
    }

    /** {@inheritDoc} */
    @Override
    public InputPortRole[] getInputPortRoles() {
        return m_roles;
    }

    /** {@inheritDoc} */
    @Override
    public OutputPortRole[] getOutputPortRoles() {
        OutputPortRole out = OutputPortRole.DISTRIBUTED;
        return new OutputPortRole[]{out};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StreamableOperator createStreamableOperator(final PartitionInfo partitionInfo, final PortObjectSpec[] inSpecs)
        throws InvalidSettingsException {
         return new StreamableOperator() {

            @Override
            public void runFinal(final PortInput[] inputs, final PortOutput[] outputs, final ExecutionContext exec) throws Exception {

                // covert non streamable in ports to BufferedDatatables
                BufferedDataTable[] inData = new BufferedDataTable[inputs.length];
                for (int i = 0; i < inputs.length; i++) {
                    if (m_roles[i].equals(InputPortRole.DISTRIBUTED_STREAMABLE)
                        || m_roles[i].equals(InputPortRole.NONDISTRIBUTED_STREAMABLE)) {
                        inData[i] = null;
                    } else {
                        inData[i] = (BufferedDataTable) ((PortObjectInput) inputs[i]).getPortObject();
                    }
                }

                prepareTagger(inData, exec);
                ColumnRearranger colre = createColumnRearranger((DataTableSpec)inSpecs[0]);
                colre.createStreamableFunction().runFinal(inputs, outputs, exec);
            }
        };
    }

    /** {@inheritDoc} */
    @Override
    protected void reset() {
        // possibly overwritten
    }

    /** {@inheritDoc} */
    @Override
    protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
        CanceledExecutionException {
        // possibly overwritten
    }

    /** {@inheritDoc} */
    @Override
    protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
        CanceledExecutionException {
        // possibly overwritten
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        try {
            m_numberOfThreadsModel.loadSettingsFrom(settings);
        } catch (InvalidSettingsException e) {
            // don't warn just catch (for downwards compatibility)
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_numberOfThreadsModel.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        try {
            m_numberOfThreadsModel.validateSettings(settings);
        } catch (InvalidSettingsException e) {
            // don't warn just catch (for downwards compatibility)
        }
    }
}