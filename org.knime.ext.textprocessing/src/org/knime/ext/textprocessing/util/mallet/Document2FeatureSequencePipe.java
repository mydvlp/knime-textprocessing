/*
 * ------------------------------------------------------------------------
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
 * Created on 20.05.2013 by koetter
 */
package org.knime.ext.textprocessing.util.mallet;

import java.util.Iterator;

import org.knime.ext.textprocessing.data.Document;
import org.knime.ext.textprocessing.data.Sentence;
import org.knime.ext.textprocessing.data.Term;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.Instance;

/**
 * Helper class that converts {@link Document}s into a term stream.
 * Expects an {@link Instance} which returns {@link Document}s in the {@link Instance#getData()}
 * class and extracts the terms from each document.
 * @author Tobias Koetter, KNIME AG, Zurich, Switzerland
 */
public class Document2FeatureSequencePipe extends Pipe {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public Document2FeatureSequencePipe() {
        super(new Alphabet(), null);
    }


    /**
     * {@inheritDoc}
     * The {@link Instance#getData()} class must return a {@link Document} class.
     */
    @Override
    public Instance pipe(final Instance carrier) {
        final Document ts = (Document) carrier.getData();
        final Iterator<Sentence> sentences = ts.sentenceIterator();
        final FeatureSequence ret = new FeatureSequence(getDataAlphabet(), ts.getLength());
        while (sentences.hasNext()) {
            final Sentence sentence = sentences.next();
            for (final Term  term : sentence.getTerms()) {
                ret.add(term.getText());
            }
        }
        carrier.setData(ret);
        return carrier;
    }
}
