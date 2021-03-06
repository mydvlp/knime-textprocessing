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
 *   Feb 7, 2019 (julian): created
 */
package org.knime.ext.textprocessing.nodes.mining.relations.openinformationextractor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.ExecutionContext;
import org.knime.ext.textprocessing.nodes.mining.relations.ExtractionResult;
import org.knime.ext.textprocessing.nodes.mining.relations.MultiThreadRelationExtractor;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.util.CoreMap;

/**
 * This class provides functionality to extract relations from data rows, collect results and create a data table based
 * on the results.
 *
 * @author Julian Bunzel, KNIME GmbH, Berlin, Germany
 */
final class StanfordOpenInformationExtractor extends MultiThreadRelationExtractor {

    /**
     * The name of the subject column to create.
     */
    private static final String SUBJECT_COL_NAME = "Subject";

    /**
     * The name of the object column to create.
     */
    private static final String OBJECT_COL_NAME = "Object";

    /**
     * The name of the predicate column to create.
     */
    private static final String PREDICATE_COL_NAME = "Predicate";

    /**
     * The name of the confidence column to create.
     */
    private static final String CONFIDENCE_COL_NAME = "Confidence";

    /**
     * True, if results should be returned as lemmas.
     */
    private final boolean m_lemmatizedResults;

    /**
     * Creates and returns a new instance of {@code StanfordOpenInformationExtractor}.
     *
     * @param container The {@link BufferedDataContainer} used to create a data table.
     * @param docColIdx The document column index.
     * @param lemmaDocColIdx The lemmatized document column index.
     * @param lemmatizedResults Set true, if results should be lemmatized.
     * @param annotationPipeline The {@link AnnotationPipeline}.
     * @param maxQueueSize Maximum queue size of finished jobs (finished computations might be cached in order to ensure
     *            the proper output ordering). If this queue is full (because the next-to-be-processed computation is
     *            still ongoing), no further tasks are submitted.
     * @param maxActiveInstanceSize The maximum number of simultaneously running computations (unless otherwise bound by
     *            the used executor).
     * @param exec The {@link ExecutionContext}.
     */
    StanfordOpenInformationExtractor(final BufferedDataContainer container, final int docColIdx,
        final int lemmaDocColIdx, final boolean lemmatizedResults, final AnnotationPipeline annotationPipeline,
        final int maxQueueSize, final int maxActiveInstanceSize, final ExecutionContext exec) {
        super(container, docColIdx, lemmaDocColIdx, annotationPipeline, maxQueueSize, maxActiveInstanceSize, exec);
        m_lemmatizedResults = lemmatizedResults;
    }

    protected static final DataTableSpec createDataTableSpec(final DataTableSpec inputSpec) {
        final DataColumnSpecCreator subject = new DataColumnSpecCreator(SUBJECT_COL_NAME, StringCell.TYPE);
        final DataColumnSpecCreator predicate = new DataColumnSpecCreator(PREDICATE_COL_NAME, StringCell.TYPE);
        final DataColumnSpecCreator object = new DataColumnSpecCreator(OBJECT_COL_NAME, StringCell.TYPE);
        final DataColumnSpecCreator confidence = new DataColumnSpecCreator(CONFIDENCE_COL_NAME, DoubleCell.TYPE);

        // create new data table with selected columns and term column
        return new DataTableSpec(inputSpec, new DataTableSpec(subject.createSpec(), predicate.createSpec(),
            object.createSpec(), confidence.createSpec()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final List<ExtractionResult> extractRelations(final Annotation annotation) {
        final List<ExtractionResult> results = new ArrayList<>();
        for (final CoreMap sentenceCM : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            // Get the OpenIE triples for the sentence
            final Collection<RelationTriple> triples =
                sentenceCM.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);

            for (final RelationTriple triple : triples) {
                final String subject = m_lemmatizedResults ? triple.subjectLemmaGloss() : triple.subjectGloss();
                final String relation = m_lemmatizedResults ? triple.relationLemmaGloss() : triple.relationGloss();
                final String object = m_lemmatizedResults ? triple.objectLemmaGloss() : triple.objectGloss();
                final Double confidence = triple.confidence;
                results.add(new ExtractionResult(subject, relation, object, confidence));
            }
        }

        if (results.isEmpty()) {
            results.add(ExtractionResult.getEmptyResult());
        }

        return results;
    }

}
