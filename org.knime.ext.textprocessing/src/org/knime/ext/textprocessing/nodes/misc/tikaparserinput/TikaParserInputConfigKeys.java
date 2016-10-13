/*
 * ------------------------------------------------------------------------
 *
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
 * History
 *   26.07.2016 (andisadewi): created
 */
package org.knime.ext.textprocessing.nodes.misc.tikaparserinput;

/**
 *
 * @author Andisa Dewi, KNIME.com, Berlin, Germany
 */
public class TikaParserInputConfigKeys {
    private TikaParserInputConfigKeys() {
    }

    /**
     * The configuration key of the column containing the documents or strings.
     */
    public static final String CFGKEY_COL = "Column";

    /**
     * The configuration key of the list of the file types (either file extensions or MIME-Types).
     */
    public static final String CFGKEY_TYPE_LIST = "TypeList";

    /**
     * The configuration key of the list of the metadata information that are to be extracted.
     */
    public static final String CFGKEY_COLUMNS_LIST = "ColumnsList";

    /**
     * The configuration key of the type button to choose between file extension or MIME-Type.
     */
    public static final String CFGKEY_TYPE = "Type";

    /**
     * The configuration key of the "create error column" flag.
     */
    public static final String CFGKEY_ERROR_COLUMN = "ErrorColumn";

    /**
     * The configuration key of the name of the error column.
     */
    public static final String CFGKEY_ERROR_COLUMN_NAME = "ErrorColumnName";

    /**
     * The configuration key of the "extract attachments" flag.
     */
    public static final String CFGKEY_EXTRACT_BOOLEAN = "ExtractAttachments";

    /**
     * The configuration key of the directory path where attachments will be extracted.
     */
    public static final String CFGKEY_EXTRACT_PATH = "ExtractAttachmentsPath";

    /**
     * The configuration key of the input password for encrypted files.
     */
    public static final String CFGKEY_CREDENTIALS = "CredentialsKey";

    /**
     * The configuration key of the "parse encrypted files" flag.
     */
    public static final String CFGKEY_ENCRYPTED = "ExtractEncrypted";

}
