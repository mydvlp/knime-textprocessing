/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright, 2003 - 2009
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
 *   14.02.2008 (Kilian Thiel): created
 */
package org.knime.ext.textprocessing.util;

import org.knime.ext.textprocessing.TextprocessingCorePlugin;



/**
 * Provides the paths to the models used by the OpenNlp library. The paths 
 * are based on the root directory of the Textprocessing plugin, which is
 * held in the plugin's activator class 
 * {@link org.knime.ext.textprocessing.TextprocessingCorePlugin}. Without the 
 * activation of the plugin (which is usually done automatically by eclipse 
 * by the creation of an instance of the activator class) the plugin root path
 * and the model paths can not be created / provided.
 * 
 * @author Kilian Thiel, University of Konstanz
 */
public final class OpenNlpModelPaths {

    private static OpenNlpModelPaths instance = null;

    private static final String SENTENCE_MODEL_POSTFIX = 
        "/resources/opennlpmodels/sentdetect/EnglishSD.bin"; 

    private static final String TOKENIZATION_MODEL_POSTFIX = 
        "/resources/opennlpmodels/tokenize/EnglishTok.bin"; 
    
    private static final String POS_MODEL_POSTFIX = 
        "/resources/opennlpmodels/pos/tag.bin";
    
    private static final String POS_DICT_POSTFIX = 
        "/resources/opennlpmodels/pos/tagdict";
    
    private static final String NER_LOCATION_MODEL_POSTFIX =
        "/resources/opennlpmodels/namefind/location.bin.gz";
    
    private static final String NER_PERSON_MODEL_POSTFIX =
        "/resources/opennlpmodels/namefind/person.bin.gz";
    
    private static final String NER_ORGANIZATION_MODEL_POSTFIX =
        "/resources/opennlpmodels/namefind/organization.bin.gz";
    
    private static final String NER_MONEY_MODEL_POSTFIX =
        "/resources/opennlpmodels/namefind/money.bin.gz";

    private static final String NER_DATE_MODEL_POSTFIX =
        "/resources/opennlpmodels/namefind/date.bin.gz";

    private static final String NER_TIME_MODEL_POSTFIX =
        "/resources/opennlpmodels/namefind/time.bin.gz";
    
    /**
     * The base path to the models.
     */
    private String m_basePath;
    
    /**
     * @return The singleton <code>OpenNlpModelPaths</code> instance holding
     * the paths to the OpenNlp models.
     */
    public static OpenNlpModelPaths getOpenNlpModelPaths() {
        if (instance == null) {
            TextprocessingCorePlugin plugin = 
                TextprocessingCorePlugin.getDefault();
            String pluginPath = plugin.getPluginRootPath();            
            instance = new OpenNlpModelPaths(pluginPath);
        }
        return instance;
    }
    
    private OpenNlpModelPaths(final String basePath) { 
        m_basePath = basePath;
    }

    /**
     * @return the model file of the sentence detection model.
     */
    public String getSentenceModelFile() {
        return m_basePath + SENTENCE_MODEL_POSTFIX;
    }

    /**
     * @return the model file of the tokenization model.
     */
    public String getTokenizerModelFile() {
        return m_basePath + TOKENIZATION_MODEL_POSTFIX;
    }
    
    /**
     * @return the model file of the pos tagger model.
     */
    public String getPosTaggerModelFile() {
        return m_basePath + POS_MODEL_POSTFIX;
    }
    
    /**
     * @return the model file of the pos tagger tag dictionary.
     */
    public String getPosTaggerDictFile() {
        return m_basePath + POS_DICT_POSTFIX;
    }
    
    /**
     * @return the model file of the person recognizer.
     */
    public String getPersonNERModelFile() {
        return m_basePath + NER_PERSON_MODEL_POSTFIX;
    }

    /**
     * @return the model file of the location recognizer.
     */
    public String getLocationNERModelFile() {
        return m_basePath + NER_LOCATION_MODEL_POSTFIX;
    }
    
    /**
     * @return the model file of the organization recognizer.
     */
    public String getOrganizationNERModelFile() {
        return m_basePath + NER_ORGANIZATION_MODEL_POSTFIX;
    }
    
    /**
     * @return the model file of the money recognizer.
     */
    public String getMoneyNERModelFile() {
        return m_basePath + NER_MONEY_MODEL_POSTFIX;
    }
    
    /**
     * @return the model file of the date recognizer.
     */
    public String getDateNERModelFile() {
        return m_basePath + NER_DATE_MODEL_POSTFIX;
    }
    
    /**
     * @return the model file of the time recognizer.
     */
    public String getTimeNERModelFile() {
        return m_basePath + NER_TIME_MODEL_POSTFIX;
    }
}
