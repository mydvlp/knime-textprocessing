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
 *   04.01.2007 (thiel): created
 */
package org.knime.ext.textprocessing.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataCellSerializer;
import org.knime.core.data.DataType;
import org.knime.core.data.DataValue;
import org.knime.core.data.StringValue;

/**
 * A data cell implementation holding a 
 * {@link org.knime.ext.textprocessing.data.Term} value by storing this value in
 * a private <code>Term</code> member. It provides a term value as well
 * as a string value.
 * 
 * @author Kilian Thiel, University of Konstanz
 */
public class TermCell extends DataCell implements StringValue, TermValue {

    private static final String SEPARATOR = "\n";
    
    private static final String TAG_SECTION = "<<TAGS>>";
    
    /**
     * Convenience access member for
     * <code>DataType.getType(TermCell.class)</code>.
     * 
     * @see DataType#getType(Class)
     */
    public static final DataType TYPE = DataType.getType(TermCell.class);

    /**
     * Returns the preferred value class of this cell implementation. This
     * method is called per reflection to determine which is the preferred
     * renderer, comparator, etc.
     * 
     * @return TermValue.class;
     */
    public static final Class<? extends DataValue> getPreferredValueClass() {
        return TermValue.class;
    }

    private static final TermSerializer SERIALIZER = new TermSerializer();

    /**
     * Returns the factory to read/write DataCells of this class from/to a
     * DataInput/DataOutput. This method is called via reflection.
     * 
     * @return A serializer for reading/writing cells of this kind.
     * @see DataCell
     */
    public static final TermSerializer getCellSerializer() {
        return SERIALIZER;
    }

    private Term m_term;

    /**
     * Creates a new instance of <code>TermCell</code> with given 
     * {@link org.knime.ext.textprocessing.data.Term}.
     * @param term The <code>Term</code> to set. 
     */
    public TermCell(final Term term) {
        m_term = term;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean equalsDataCell(final DataCell dc) {
        if (dc == null) {
            return false;
        }
        if (!(dc instanceof TermCell)) {
            return false;
        }
        TermCell t = (TermCell)dc;
        
        return t.getTermValue().equals(m_term);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return m_term.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getStringValue();
    }

    /**
     * {@inheritDoc}
     */
    public String getStringValue() {
        return m_term.toString();
    }

    /**
     * {@inheritDoc}
     */
    public Term getTermValue() {
        return m_term;
    }
    
    
    
    /** Factory for (de-)serializing a TermCell. */
    private static class TermSerializer implements 
        DataCellSerializer<TermCell> {

        /**
         * {@inheritDoc}
         */
        public void serialize(final TermCell cell, 
                final DataOutput output) throws IOException {
            output.writeUTF(cell.getSerializationString());
        }
        
        /**
         * {@inheritDoc}
         */
        public TermCell deserialize(final DataInput input) 
            throws IOException {
            String s = input.readUTF();
            return TermCell.createTermCell(s);
        }
    }
    
    
    private String getSerializationString() {        
        return TermCell.getSerializationString(this.getTermValue());
    }
    
    private static TermCell createTermCell(final String s) {
        return new TermCell(TermCell.createTerm(s));
    }
    
    /**
     * Returns the instance of <code>Term</code> related to the given string.
     * @param s The string to get the related <code>Term</code> instance for.
     * @return The instance of <code>Term</code> related to the given string.
     */
    static Term createTerm(final String s) {
        List<Word> words = new ArrayList<Word>();
        List<Tag> tags = new ArrayList<Tag>();

        String[] str = s.split(TermCell.TAG_SECTION);
        
        // words
        if (str.length > 0) {
            String wordStr = str[0]; 
            String[] wordsArr = wordStr.split(TermCell.SEPARATOR);
            for (String w : wordsArr) {
                if (w != null && w.length() > 0) {
                    words.add(new Word(w));
                }
            }
        }
        
        // tags
        if (str.length > 1) {
            String tagStr = str[1]; 
            String[] tagsArr = tagStr.split(TermCell.SEPARATOR);
            for (int i = 0; i < tagsArr.length; i++) {
                String type = tagsArr[i];
                i++;
                String value = tagsArr[i];
                tags.add(TagFactory.getInstance().createTag(type, value));
            }
        }
        
        return new Term(words, tags);
    }    
    
    /**
     * Returns the serialization string of the given term.
     * @param term The term to return its serialization string.
     * @return The serialization string of the given term.
     */
    static String getSerializationString(final Term term) {
        StringBuffer buf = new StringBuffer();
        
        for (Word w : term.getWords()) {
            buf.append(w.getWord());
            buf.append(TermCell.SEPARATOR);
        }
        buf.append(TermCell.TAG_SECTION);
        for (Tag t : term.getTags()) {
            buf.append(t.getTagType());
            buf.append(TermCell.SEPARATOR);
            buf.append(t.getTagValue());
            buf.append(TermCell.SEPARATOR);
        }
        return buf.toString();
    }
}