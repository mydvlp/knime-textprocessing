/* 
========================================================================
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
 * -------------------------------------------------------------------
 * 
 * History
 *   19.12.2006 (thiel): created
 */
package org.knime.ext.textprocessing.data;

import org.knime.core.data.DataValue;
import org.knime.core.data.DataValueComparator;

/**
 * Comparator returned by the
 * {@link org.knime.ext.textprocessing.data.DocumentValue} interface.
 * 
 * @see org.knime.ext.textprocessing.data.DocumentValue#UTILITY
 * @see org.knime.ext.textprocessing.data.DocumentValue.DocumentUtilityFactory
 * @author Kilian Thiel, University of Konstanz
 */
public class DocumentValueComparator extends DataValueComparator {

    /**
     * Compares two {@link org.knime.ext.textprocessing.data.DocumentValue}s
     * based on their text.
     * 
     * {@inheritDoc}
     */
    @Override
    protected int compareDataValues(final DataValue v1, final DataValue v2) {
        if (v1 == v2) {
            return 0;
        }
        if (((DocumentValue)v1).getDocument() == ((DocumentValue)v2)
                .getDocument()) {
            return 0;
        }

        // compare title
        String str1 = ((DocumentValue)v1).getDocument().getTitle();
        String str2 = ((DocumentValue)v2).getDocument().getTitle();
        int res = str1.compareTo(str2);
        if (res != 0) {
            return res;
        }

        // compare type
        str1 = ((DocumentValue)v1).getDocument().getType().toString();
        str2 = ((DocumentValue)v2).getDocument().getType().toString();
        res = str1.compareTo(str2);
        if (res != 0) {
            return res;
        }

        // compare date
        str1 = ((DocumentValue)v1).getDocument().getPubDate().toString();
        str2 = ((DocumentValue)v2).getDocument().getPubDate().toString();
        res = str1.compareTo(str2);
        if (res != 0) {
            return res;
        }

        // compare file
        str1 = ((DocumentValue)v1).getDocument().getDocFile().getAbsolutePath();
        str2 = ((DocumentValue)v2).getDocument().getDocFile().getAbsolutePath();
        res = str1.compareTo(str2);
        if (res != 0) {
            return res;
        }

        // compare authors
        str1 = ((DocumentValue)v1).getDocument().getAuthors().toString();
        str2 = ((DocumentValue)v2).getDocument().getAuthors().toString();
        res = str1.compareTo(str2);
        if (res != 0) {
            return res;
        }

        // compare categories
        str1 = ((DocumentValue)v1).getDocument().getCategories().toString();
        str2 = ((DocumentValue)v2).getDocument().getCategories().toString();
        res = str1.compareTo(str2);
        if (res != 0) {
            return res;
        }

        // compare sources
        str1 = ((DocumentValue)v1).getDocument().getSources().toString();
        str2 = ((DocumentValue)v2).getDocument().getSources().toString();
        res = str1.compareTo(str2);
        if (res != 0) {
            return res;
        }

        // finally compare text (because its the most expensive comparison
        str1 = ((DocumentValue)v1).getDocument().getText();
        str2 = ((DocumentValue)v2).getDocument().getText();
        return str1.compareTo(str2);
    }
}
