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
 *   20.02.2008 (Kilian Thiel): created
 */
package org.knime.ext.textprocessing.nodes.source.parser.pubmed;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.knime.core.node.NodeLogger;
import org.knime.ext.textprocessing.data.Author;
import org.knime.ext.textprocessing.data.Document;
import org.knime.ext.textprocessing.data.DocumentBuilder;
import org.knime.ext.textprocessing.data.DocumentCategory;
import org.knime.ext.textprocessing.data.DocumentSource;
import org.knime.ext.textprocessing.data.DocumentType;
import org.knime.ext.textprocessing.data.SectionAnnotation;
import org.knime.ext.textprocessing.nodes.source.parser.DocumentParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Implements the 
 * {@link org.knime.ext.textprocessing.nodes.source.parser.DocumentParser} 
 * interface. The provided method
 * {@link org.knime.ext.textprocessing.nodes.source.parser.dml.DmlDocumentParser#parse(InputStream)}
 * is able to parse the data of the given input stream containing PubMed 
 * (http://www.pubmed.org) document search results. For more details
 * about the xml format used by PubMed to deliver search results see
 * (http://www.ncbi.nlm.nih.gov/entrez/query/DTD/pubmed_060101.dtd).
 * 
 * @author Kilian Thiel, University of Konstanz
 */
public class PubMedDocumentParser extends DefaultHandler implements
        DocumentParser {

    /**
     * The default source of the pub med parser.
     */
    public final static String DEFAULT_SOURCE = "PubMed";
    
    /**
     * The name of the article tag.
     */
    public static final String PUBMEDARTICLE = "pubmedarticle";

    /**
     * The name of the abstract text tag.
     */
    public static final String ABSTRACTTEXT = "abstracttext";

    /**
     * The name of the article title tag.
     */
    public static final String ARTICLETITLE = "articletitle";
    
    /**
     * The name of the author tag.
     */
    public static final String AUTHOR = "author";
    
    /**
     * The name of the first name tag.
     */
    public static final String FIRSTNAME = "firstname";

    /**
     * The name of the fore name tag.
     */
    public static final String FORENAME = "forename";
    
    /**
     * The name of the last name tag.
     */
    public static final String LASTNAME = "lastname";    
    
    
    
    private static final NodeLogger LOGGER = 
        NodeLogger.getLogger(PubMedDocumentParser.class);
    
    private List<Document> m_docs;
    
    private DocumentCategory m_category;
    
    private DocumentSource m_source;
    
    private DocumentType m_type;
    
    private String m_docPath;    
    
    private DocumentBuilder m_currentDoc;
    
    private String m_lastTag;
    
    private String m_abstract = "";
    
    private String m_title = "";
    
    private String m_firstName = "";
    
    private String m_lastName = "";    
    
    
    /**
     * Creates a new instance of <code>PubMedDocumentParser</code>. The 
     * document source is set to 
     * {@link org.knime.ext.textprocessing.nodes.source.parser.pubmed.PubMedDocumentParser#DEFAULT_SOURCE} 
     * by default. The document category and file path will be set to 
     * <code>null</code> by default.
     */
    public PubMedDocumentParser() {
        this(null, null, new DocumentSource(DEFAULT_SOURCE));
    }
    
    /**
     * Creates a new instance of <code>PubMedDocumentParser</code>. The given
     * source, category and file path is set to the created documents.
     * 
     * @param docPath The path to the file containing the document.
     * @param category The category of the document to set.
     * @param source The source of the document to set.
     */
    public PubMedDocumentParser(final String docPath,
            final DocumentCategory category, final DocumentSource source) {
        m_category = category;
        m_source = source;
        m_docPath = docPath;
    } 
    
    /**
     * {@inheritDoc}
     */
    public List<Document> parse(InputStream is) {
        try {
            m_docs = new ArrayList<Document>();
            SAXParserFactory.newInstance().newSAXParser().parse(is, this);
        } catch (ParserConfigurationException e) {
            LOGGER.error("Could not instanciate parser");
            LOGGER.info(e.getMessage());
        } catch (SAXException e) {
            LOGGER.error("Could not parse file");
            LOGGER.info(e.getMessage());
        } catch (IOException e) {
            LOGGER.error("Could not read file");
            LOGGER.info(e.getMessage());
        }
        return m_docs;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(final String uri, final String localName, 
            final String qName, final Attributes attributes) {
        m_lastTag = qName.toLowerCase();
        
        if (m_lastTag.equals(PUBMEDARTICLE)) {
            m_currentDoc = new DocumentBuilder();
            if (m_category != null) {
                m_currentDoc.addDocumentCategory(m_category);
            }
            if (m_source != null) {
                m_currentDoc.addDocumentSource(m_source);
            }
            if (m_type != null) {
                m_currentDoc.setDocumentType(m_type);
            }            
            if (m_docPath != null) {
                File f = new File(m_docPath);
                if (f.exists()) {
                    m_currentDoc.setDocumentFile(f);
                }
            }
        } else if (m_lastTag.equals(ABSTRACTTEXT)) {
            m_abstract = "";    
        } else if (m_lastTag.equals(ARTICLETITLE)) {
            m_title = "";
        } else if (m_lastTag.equals(FIRSTNAME) || qName.equals(FORENAME)) {
            m_firstName = "";
        } else if (m_lastTag.equals(LASTNAME)) {
            m_lastName = "";
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(final String uri, final String localName, 
            final String qName) {
        String name = qName.toLowerCase();
        if (name.equals(PUBMEDARTICLE) && m_currentDoc != null) {
            Document doc = m_currentDoc.createDocument();
            m_docs.add(doc);
            m_currentDoc = null;
        } else if (name.equals(ABSTRACTTEXT)) {
            m_currentDoc.addSection(m_abstract.trim(), 
                    SectionAnnotation.ABSTRACT);
        } else if (name.equals(ARTICLETITLE)) {
            m_currentDoc.addTitle(m_title.trim());
        } else if (name.equals(AUTHOR)) {
            Author a = new Author(m_firstName.trim(), m_lastName.trim());
            m_currentDoc.addAuthor(a);
        }       
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void characters(final char[] ch, final int start, final int length) {
        if (m_lastTag.equals(FIRSTNAME)) {
            m_firstName += new String(ch, start, length);
        } else if (m_lastTag.equals(LASTNAME)) {
            m_lastName += new String(ch, start, length);
        } else if (m_lastTag.equals(ARTICLETITLE)) {
            m_title += new String(ch, start, length);
        } else if (m_lastTag.equals(ABSTRACTTEXT)) {
            m_abstract += new String(ch, start, length);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setDocumentCategroy(final DocumentCategory category) {
        m_category = category;
    }

    /**
     * {@inheritDoc}
     */
    public void setDocumentSource(final DocumentSource source) {
        m_source = source;
    }

    /**
     * {@inheritDoc}
     */
    public void setDocumentType(final DocumentType type) {
        m_type = type;
    } 
    
    /**
     * {@inheritDoc}
     */
    public void setDocumentFilepath(final String filePath) {
        m_docPath = filePath;
    }    
}