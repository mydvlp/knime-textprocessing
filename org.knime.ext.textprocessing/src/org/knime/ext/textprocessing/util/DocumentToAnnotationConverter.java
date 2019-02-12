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
 *   Jan 10, 2019 (julian): created
 */
package org.knime.ext.textprocessing.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.knime.ext.textprocessing.data.Document;
import org.knime.ext.textprocessing.data.NamedEntityTag;
import org.knime.ext.textprocessing.data.PartOfSpeechTag;
import org.knime.ext.textprocessing.data.Sentence;
import org.knime.ext.textprocessing.data.Tag;
import org.knime.ext.textprocessing.data.Term;
import org.knime.ext.textprocessing.data.Word;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.sequences.SeqClassifierFlags;
import edu.stanford.nlp.util.CoreMap;

/**
 * This class provides functionality to convert KNIME's {@link Document} structure to StanfordNLP's {@link Annotation}
 * structure.
 *
 * @author Julian Bunzel, KNIME GmbH, Berlin, Germany
 * @since 3.8
 */
public final class DocumentToAnnotationConverter {

    /**
     * Unknown tag value.
     */
    private static final String UNKNOWN = "UNKNOWN";

    /**
     * String used as backup part-of-speech tag for untagged terms, since no tag might crash StanfordNLP annotators.
     */
    private static final String POS_FALLBACK_TAG = " ";

    /**
     * Empty constructor.
     */
    private DocumentToAnnotationConverter() {
        // Nothing to do here..
    }

    /**
     * Converts {@link Document} to an {@link Annotation}. Tags and lemmas will not be applied.
     *
     * @param document {@code Document} to convert.
     * @return Returns an {@code Annotation} object.
     */
    public static final Annotation convert(final Document document) {
        final List<CoreMap> coreMap = new ArrayList<>(10);
        final Iterator<Sentence> sentenceIterator = document.sentenceIterator();
        int sentenceCount = 1;

        while (sentenceIterator.hasNext()) {
            final Sentence sentence = sentenceIterator.next();
            final Annotation sentenceAnnotation = new Annotation(sentence.getTextWithWsSuffix());
            final List<Term> terms = sentence.getTerms();
            final List<CoreLabel> labels = new ArrayList<>(terms.stream().mapToInt(t -> t.getWords().size()).sum());
            int wordCount = 1;

            for (final Term term : terms) {
                final List<Word> words = term.getWords();
                for (final Word word : words) {
                    labels.add(wordToCoreLabel(word, null, wordCount++, sentenceCount, null, null));
                }
            }
            sentenceCount++;
            sentenceAnnotation.set(CoreAnnotations.TokensAnnotation.class, labels);
            coreMap.add(sentenceAnnotation);
        }

        return new Annotation(coreMap);
    }

    /**
     * Converts {@link Document} to an {@link Annotation}.
     *
     * @param document {@code Document} to convert.
     * @param lemmatizedDoc The lemmatized {@code Document}.
     * @param setPosTags Set true, if part-of-speech tags should be set.
     * @param setNeTags Set true, if named entity tags should be set.
     * @return Returns an {@code Annotation}.
     */
    public static final Annotation convert(final Document document, final Document lemmatizedDoc,
        final boolean setPosTags, final boolean setNeTags) {
        final List<CoreMap> coreMap = new ArrayList<>();
        final Iterator<Sentence> sentenceIterator = document.sentenceIterator();
        final Iterator<Sentence> lemmatizedSentenceIterator = lemmatizedDoc.sentenceIterator();
        int sentenceCount = 1;

        while (sentenceIterator.hasNext() && lemmatizedSentenceIterator.hasNext()) {
            final Sentence sentence = sentenceIterator.next();
            final Sentence lemmatizedSentence = lemmatizedSentenceIterator.next();
            final Annotation sentenceAnnotation = new Annotation(sentence.getTextWithWsSuffix());
            final List<CoreLabel> labels = new ArrayList<>();
            final List<Term> terms = sentence.getTerms();
            final List<Term> lemmatizedTerms = lemmatizedSentence.getTerms();
            assert terms.size() == lemmatizedTerms.size();
            int wordCount = 1;

            for (int i = 0; i < terms.size(); i++) {
                final Term term = terms.get(i);
                final List<Word> words = term.getWords();
                final List<Word> lemmaWords = lemmatizedTerms.get(i).getWords();
                assert words.size() == lemmaWords.size();
                final List<Tag> posTags = setPosTags ? term.getTags().stream()//
                    .filter(t -> t.getTagType().equals(PartOfSpeechTag.TAG_TYPE))//
                    .collect(Collectors.toList()) : null;
                final List<Tag> neTags = setNeTags ? term.getTags().stream()//
                    .filter(t -> t.getTagType().equals(NamedEntityTag.TAG_TYPE))//
                    .collect(Collectors.toList()) : null;
                for (int j = 0; j < words.size(); j++) {
                    labels.add(
                        wordToCoreLabel(words.get(j), lemmaWords.get(j), wordCount++, sentenceCount, posTags, neTags));
                }
            }
            sentenceCount++;
            sentenceAnnotation.set(CoreAnnotations.TokensAnnotation.class, labels);
            coreMap.add(sentenceAnnotation);
        }

        return new Annotation(coreMap);
    }

    /**
     * Converts a {@link Word} to a {@link CoreLabel}.
     *
     * @param word The {@code Word} to convert. Must not be {@code null}.
     * @param lemma The {@code Word} as lemma. Will not be set, if {@code null}.
     * @param wordIndex The index of the word within the sentence.
     * @param sentenceIndex The index of the current sentence within the document.
     * @param posTags List of part-of-speech {@link Tag Tags}. Will not be set, if {@code null}.
     * @param neTags List of named-entity {@code Tags}. Will not be set, if {@code null}.
     * @return A {@code CoreLabel}
     */
    private static final CoreLabel wordToCoreLabel(final Word word, final Word lemma, final int wordIndex,
        final int sentenceIndex, final List<Tag> posTags, final List<Tag> neTags) {
        // Set word information
        final CoreLabel cl = CoreLabel.wordFromString(word.getText());
        cl.setAfter(word.getWhitespaceSuffix());
        cl.setIndex(wordIndex);
        cl.setSentIndex(sentenceIndex);

        // Set lemma
        if (lemma != null) {
            cl.setLemma(word.getText());
        }

        // Set tags
        setTags(posTags, neTags, cl);
        return cl;
    }

    /**
     * Sets {@link Tag Tags} to a given {@link CoreLabel}.
     *
     * @param posTags The pos tags to set.
     * @param nerTags The ner tags to set.
     * @param cl The CoreLabel.
     */
    private static void setTags(final List<Tag> posTags, final List<Tag> nerTags, final CoreLabel cl) {
        if (posTags != null) {
            if (!posTags.isEmpty()) {
                for (final Tag tag : posTags) {
                    final String tagValue = tag.getTagValue();
                    cl.setTag(!tagValue.equals(UNKNOWN) ? tagValue : POS_FALLBACK_TAG);
                }
            } else {
                cl.setTag(POS_FALLBACK_TAG);
            }
        }

        if (nerTags != null) {
            if (!nerTags.isEmpty()) {
                for (final Tag tag : nerTags) {
                    final String tagValue = tag.getTagValue();
                    cl.setNER(!tagValue.equals(UNKNOWN) ? tagValue : POS_FALLBACK_TAG);
                }
            } else {
                cl.setNER(SeqClassifierFlags.DEFAULT_BACKGROUND_SYMBOL);
            }
        }
    }
}
