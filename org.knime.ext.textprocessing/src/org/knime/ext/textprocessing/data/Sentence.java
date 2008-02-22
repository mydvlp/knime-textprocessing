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
 *   13.02.2008 (Kilian Thiel): created
 */
package org.knime.ext.textprocessing.data;

import java.util.Collections;
import java.util.List;

/**
 * Contains a complete sentence as a list of 
 * {@link org.knime.ext.textprocessing.data.Term}s.
 * 
 * @author Kilian Thiel, University of Konstanz
 */
public class Sentence implements TextContainer {
    
    private List<Term> m_terms;
    
    /**
     * Creates a new instance of <code>Sentence</code> with the given list of
     * {@link org.knime.ext.textprocessing.data.Term}s as its words . 
     * If it is set to <code>null</code> a <code>NullPointerException</code> is 
     * thrown.
     * 
     * @param sentence The list of terms to set as sentence.
     * @throws NullPointerException If the list of terms is <code>null</code>.
     */
    public Sentence(final List<Term> sentence) throws NullPointerException {
        if (sentence == null) {
            throw new NullPointerException(
                    "Term list \"sentence\" may not be null!");
        } 
        
        m_terms = sentence;
    }

    /**
     * @return the unmodifiable list of terms representing the sentence.
     */
    public List<Term> getTerms() {
        return Collections.unmodifiableList(m_terms);
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < m_terms.size(); i++) {
            sb.append(m_terms.get(i).getText());
            if (i < m_terms.size() - 1) {
                sb.append(Term.WORD_SEPARATOR);
            }
        }
        return sb.toString();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < m_terms.size(); i++) {
            sb.append(m_terms.get(i).toString());
            if (i < m_terms.size() - 1) {
                sb.append(Term.WORD_SEPARATOR);
            }
        }
        return sb.toString();        
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        } else if (!(o instanceof Sentence)) {
            return false;
        }
        Sentence s = (Sentence)o;
        if (!s.getTerms().equals(getTerms())) {
            return false;
        }
        
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int fac = 119;
        int div = 13;
        int hash = 0;
        for (Term t : m_terms) {
            hash += fac * t.hashCode() / div; 
        }
        return hash;
    }    
}