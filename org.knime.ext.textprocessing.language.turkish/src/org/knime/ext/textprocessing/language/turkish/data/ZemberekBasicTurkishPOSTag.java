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
 *   Aug 10, 2018 (julian): created
 */
package org.knime.ext.textprocessing.language.turkish.data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.knime.ext.textprocessing.data.Tag;
import org.knime.ext.textprocessing.data.TagBuilder;

import zemberek.core.turkish.PrimaryPos;

/**
 * This class provides methods given by the {@link TagBuilder} interface to use the {@link PrimaryPos} tag set
 * provided by ZemberekNLP.
 *
 * @author Julian Bunzel, KNIME GmbH, Berlin, Germany
 * @since 3.7
 */
public class ZemberekBasicTurkishPOSTag implements TagBuilder {

    /**
     * The tag type constant for the ZemberekNLP tag set.
     */
    private static final String TAG_TYPE = "ZEMNLP";

    /**
     * {@inheritDoc}
     */
    @Override
    public Tag buildTag(final String value) {
        return stringToTag(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> asStringList() {
        return Stream.of(PrimaryPos.values())//
            .map(e -> getShortForm(e.name()))//
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Tag> getTags() {
        return Stream.of(PrimaryPos.values())//
            .map(e -> stringToTag(e.name()))//
            .collect(Collectors.toSet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return TAG_TYPE;
    }

    /**
     * Converts a string to the corresponding {@link Tag}.
     *
     * @param str The string to be converted to a {@code Tag}.
     * @return Returns the corresponding {@code Tag}.
     */
    public static final Tag stringToTag(final String str) {
        return new Tag(getShortForm(str), TAG_TYPE);
    }

    /**
     * Returns the short form of the original {@link PrimaryPos} enum value.
     *
     * @param tag The tag value as {@code String}.
     * @return The short form of the original {@link PrimaryPos} enum value.
     */
    private static final String getShortForm(final String tag) {
        // tag is already in short form
        if (PrimaryPos.exists(tag)) {
            return tag.toUpperCase();
        }
        // tag is according to .name() except for PrimaryPos.Unknown which never is shortened
        try {
            final PrimaryPos enumValue = PrimaryPos.valueOf(tag);
            return enumValue.name().equalsIgnoreCase(PrimaryPos.Unknown.name())
                ? PrimaryPos.Unknown.name().toUpperCase() : enumValue.getStringForm().toUpperCase();
        } catch (final IllegalArgumentException e) {
            return PrimaryPos.Unknown.name().toUpperCase();
        }
    }
}
