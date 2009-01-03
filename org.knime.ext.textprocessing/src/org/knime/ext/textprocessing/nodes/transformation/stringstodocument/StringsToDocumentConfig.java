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
 *   29.07.2008 (thiel): created
 */
package org.knime.ext.textprocessing.nodes.transformation.stringstodocument;

import org.knime.ext.textprocessing.data.DocumentType;
import org.knime.ext.textprocessing.data.PublicationDate;

/**
 * Holds all necessary variables needed to build a document out of a data row,
 * like certain indices for title, author or full text column, publication
 * date, document source, category, and type.
 * 
 * @author Kilian Thiel, University of Konstanz
 */
public class StringsToDocumentConfig {

    /**
     * The default document publication date.
     */
    static final String DEF_DOCUMENT_PUBDATE = PublicationDate.getToday();
    
    /**
     * The default document source.
     */
    static final String DEF_DOCUMENT_SOURCE = "";
    
    /**
     * The default document category.
     */
    static final String DEF_DOCUMENT_CATEGORY = "";

    /**
     * The default document type
     */
    static final String DEF_DOCUMENT_TYPE = DocumentType.UNKNOWN.toString();  
    
    /**
     * The default split value for author strings.
     */
    static final String DEF_AUTHORS_SPLITCHAR = ", ";
    
    /**
     * The default author names.
     */
    static final String DEF_AUTHOR_NAMES = "-";
    
    
    private int m_titleStringIndex = -1;
    
    private int m_fulltextStringIndex = -1;
    
    private int m_authorsStringIndex = -1;

    private String m_authorsSplitChar = DEF_AUTHORS_SPLITCHAR;
    
    private String m_docSource = DEF_DOCUMENT_SOURCE;
    
    private String m_docCat = DEF_DOCUMENT_CATEGORY;
    
    private String m_docType = DEF_DOCUMENT_TYPE;
    
    private String m_publicationDate = DEF_DOCUMENT_PUBDATE;
    
    /**
     * @return the titleStringIndex
     */
    public int getTitleStringIndex() {
        return m_titleStringIndex;
    }

    /**
     * @param titleStringIndex the titleStringIndex to set
     */
    public void setTitleStringIndex(final int titleStringIndex) {
        m_titleStringIndex = titleStringIndex;
    }

    /**
     * @return the authorsStringIndex
     */
    public int getAuthorsStringIndex() {
        return m_authorsStringIndex;
    }

    /**
     * @param authorsStringIndex the authorsStringIndex to set
     */
    public void setAuthorsStringIndex(final int authorsStringIndex) {
        m_authorsStringIndex = authorsStringIndex;
    }

    /**
     * @return the authorsSplitChar
     */
    public String getAuthorsSplitChar() {
        return m_authorsSplitChar;
    }

    /**
     * @param authorsSplitChar the authorsSplitChar to set
     */
    public void setAuthorsSplitChar(final String authorsSplitChar) {
        m_authorsSplitChar = authorsSplitChar;
    }

    /**
     * @return the fulltextStringIndex
     */
    public int getFulltextStringIndex() {
        return m_fulltextStringIndex;
    }

    /**
     * @param fulltextStringIndex the fulltextStringIndex to set
     */
    public void setFulltextStringIndex(final int fulltextStringIndex) {
        m_fulltextStringIndex = fulltextStringIndex;
    }

    /**
     * @return the docSource
     */
    public String getDocSource() {
        return m_docSource;
    }

    /**
     * @param docSource the docSource to set
     */
    public void setDocSource(final String docSource) {
        m_docSource = docSource;
    }

    /**
     * @return the docCat
     */
    public String getDocCat() {
        return m_docCat;
    }

    /**
     * @param docCat the docCat to set
     */
    public void setDocCat(final String docCat) {
        m_docCat = docCat;
    }

    /**
     * @return the docType
     */
    public String getDocType() {
        return m_docType;
    }

    /**
     * @param docType the docType to set
     */
    public void setDocType(final String docType) {
        m_docType = docType;
    }

    /**
     * @return the publicationDate
     */
    public String getPublicationDate() {
        return m_publicationDate;
    }

    /**
     * @param publicationDate the publicationDate to set
     */
    public void setPublicationDate(final String publicationDate) {
        m_publicationDate = publicationDate;
    }
}
