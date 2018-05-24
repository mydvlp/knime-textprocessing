/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
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
 * History
 *   Apr 18, 2018 (julian): created
 */
package org.knime.ext.textprocessing.nodes.tagging.dict.multicolumn;

import java.util.Set;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.ext.textprocessing.nodes.tagging.DocumentTaggerConfiguration;
import org.knime.ext.textprocessing.nodes.tagging.MultiTaggerConfigKeys;

/**
 * The {@code DictionaryTaggerConfiguration} is an extension of the {@link DocumentTaggerConfiguration}. It contains a
 * set of entities (the dictionary) which is used to tag documents.
 *
 * @author Julian Bunzel, KNIME GmbH, Berlin, Germany
 * @since 3.6
 */
class DictionaryTaggerConfiguration extends DocumentTaggerConfiguration {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(DictionaryTaggerConfiguration.class);

    private Set<String> m_entities;

    private String m_columnName;

    /**
     * Creates an instance of {@code DictionaryTaggerConfiguration} based on a {@link DocumentTaggerConfiguration} and a
     * set of entities.
     *
     * @param config The {@code DocumentTaggerConfiguration} to create the {@code DictionaryTaggerConfiguration}.
     * @param entities A set of entities.
     */
    DictionaryTaggerConfiguration(final String colName, final DocumentTaggerConfiguration config,
        final Set<String> entities) {
        this(colName, config.getCaseSensitivityOption(), config.getExactMatchOption(), config.getTagType(),
            config.getTagValue(), entities);
    }

    /**
     * Creates an instance of {@code DictionaryTaggerConfiguration} based on a column name, values for the case
     * sensitivity and exact match flag, the tag type and tag value and as well the set of entities.
     *
     * @param colName The column name.
     * @param caseSensitivity Set {@code true} for case sensitive matching.
     * @param exactMatch Set {@code true} for exact matching.
     * @param tagType The tag type to set.
     * @param tagValue The tag value to set.
     * @param entities The set of entities used for tagging.
     */
    DictionaryTaggerConfiguration(final String colName, final boolean caseSensitivity, final boolean exactMatch,
        final String tagType, final String tagValue, final Set<String> entities) {
        super(caseSensitivity, exactMatch, tagType, tagValue);

        setColumnName(colName);
        setEntities(entities);
    }

    /**
     * Creates an instance of {@code DictionaryTaggerConfiguration} based on column name. Tagger parameters will be set
     * to their default values. Entities will be set to null.
     *
     * @param colName The column name.
     */
    DictionaryTaggerConfiguration(final String colName) {
        this(colName, new DocumentTaggerConfiguration(), null);
    }

    /**
     * Sets a set of entities as dictionary.
     *
     * @param entities The set of entities to set.
     */
    void setEntities(final Set<String> entities) {
        m_entities = entities;
    }

    /**
     * Returns the set of entities used for tagging.
     *
     * @return Returns the set of entities.
     */
    Set<String> getEntities() {
        return m_entities;
    }

    /**
     * Returns the column name containing the dictionaries.
     *
     * @return Returns the column name.
     */
    String getColumnName() {
        return m_columnName;
    }

    /**
     * Sets the column name containing the dictionary.
     *
     * @param columnName The column name to set.
     */
    void setColumnName(final String columnName) {
        m_columnName = columnName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSettingsTo(final NodeSettingsWO settings) {
        super.saveSettingsTo(settings);
        settings.addString(MultiTaggerConfigKeys.CFGKEY_COLUMNNAME, m_columnName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettingsFrom(final NodeSettingsRO settings) {
        String columnName = "";
        try {
            columnName = settings.getString(MultiTaggerConfigKeys.CFGKEY_COLUMNNAME);
        } catch (InvalidSettingsException ise) {
            // this method is called from the dialog which inits "this" first
            // and immediately calls this method, name should (must) match
            LOGGER.warn("Can't safely update settings for column \"" + m_columnName + "\": No matching identifier.",
                ise);
            columnName = m_columnName;
        }

        if (!m_columnName.equals(columnName)) {
            LOGGER.warn("Can't update settings for column \"" + m_columnName + "\": got NodeSettings for \""
                + columnName + "\"");
        }
        setColumnName(columnName);
        super.loadSettingsFrom(settings);
    }

    public static DictionaryTaggerConfiguration createFrom(final NodeSettingsRO settings) throws InvalidSettingsException {

        String colName = settings.getString(MultiTaggerConfigKeys.CFGKEY_COLUMNNAME);
        boolean caseSensitive = settings.getBoolean(MultiTaggerConfigKeys.CFGKEY_CASESENSITIVE);
        boolean exactMatch = settings.getBoolean(MultiTaggerConfigKeys.CFGKEY_EXACTMATCH);
        String tagType = settings.getString(MultiTaggerConfigKeys.CFGKEY_TAGTYPE);
        String tagValue = settings.getString(MultiTaggerConfigKeys.CFGKEY_TAGVALUE);

        return new DictionaryTaggerConfiguration(colName, caseSensitive, exactMatch, tagType, tagValue, null);
    }
}