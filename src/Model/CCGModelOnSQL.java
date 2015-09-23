/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Database.DBManagement;
import Feature.CCGFeature;
import Feature.Feature;
import Lemmatizer.Lemmatizer;
import com.mysql.jdbc.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sherzod
 */
public class CCGModelOnSQL implements Model {

    private DBManagement dbProcessor;

    private Lemmatizer lemmatizer;

//    public CCGModelOnSQL(Connection conn, DBManagement db) {
//        this.dbProcessor = db;
//        this.conn = conn;
//    }
    public CCGModelOnSQL(DBManagement db, Lemmatizer l) {
        this.dbProcessor = db;
        this.lemmatizer = l;
    }

    public void addNewFeatures(List<Feature> entries) {

        for (Feature entry : entries) {
            addNewFeature((CCGFeature) entry);
        }

    }

    public void addNewFeature(CCGFeature entry) {
        try {
            long id = dbProcessor.insertFeature(entry);
        } catch (Exception ex) {
            System.err.println(entry.getLambda().toString());
            System.err.println(ex.getMessage());
            //Logger.getLogger(CCGModelOnSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<Feature> getEntriesforLexeme(String lexeme) {
        try {
            return dbProcessor.getFeaturesWithLemma(lexeme);
        } catch (Exception ex) {
            Logger.getLogger(CCGModelOnSQL.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public void normalizeScores() {
        try {
            List<CCGFeature> features = dbProcessor.getInducedFeatures();

            //set all induced feature score to 0
            dbProcessor.updateAllInducedFeaturesScore(0);

            List<CCGFeature> normalizedFeatures = new ArrayList<CCGFeature>();

            for (CCGFeature f : features) {
                double sum = 0;

                for (CCGFeature f2 : features) {

                    if (f2.getWord().equals(f.getWord())) {
                        if (f2.getScore() > 0) {
                            sum += f2.getScore();
                        }
                    }

                }

                if (f.getScore() > 0) {
                    double score = f.getScore() / sum;

                    CCGFeature normalized = f.clone();
                    normalized.setScore(score);

                    dbProcessor.updateFeatureScore(normalized);
                }
            }

            for (CCGFeature n : normalizedFeatures) {

            }

        } catch (Exception ex) {
            Logger.getLogger(CCGModelOnSQL.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public List<Feature> getEntriesforLexeme(String lexeme, boolean isUsed) {
        try {
            List<Feature> features = dbProcessor.getFeaturesWithLemmaIsUsed(lexeme, isUsed + "");

            String l2 = lemmatizer.lemmatize(lexeme);

            for (Feature f : dbProcessor.getFeaturesWithLemmaIsUsed(l2, isUsed + "")) {
                if (!features.contains(f)) {
                    features.add(f);
                }
            }

            return features;
        } catch (Exception ex) {
            Logger.getLogger(CCGModelOnSQL.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    //check database for lexical entries
    public boolean modelExists() {
        try {
            if (dbProcessor.getFeaturesWithInstanceId("-1").size() > 0) {
                if (dbProcessor.getAllInducedFeaturesSize("-1") > 0) {
                    return true;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CCGModelOnSQL.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    public List<Feature> getAllEntries() {
        List<Feature> entries = new ArrayList<Feature>();
        return entries;
    }

    @Override
    public void updateAllFeatures(List<Feature> features, Double value) {

        for (Feature f1 : features) {
            CCGFeature feature = (CCGFeature) f1;

            //update features used in parsed coverings(derivation)
            if (value == 0.0) {
                feature.setScore(value);
                feature.setIsUsed(true);

                udpateExistingFeatureAsUsed(feature);

            } else {
                //update score
                feature.setScore(value);
                udpateExistingFeature(feature);
            }
        }
    }

    @Override
    public void udpateExistingFeature(Feature feature) {
        try {
            CCGFeature f = (CCGFeature) feature;

            if (f.getWord().equals("and")) {
                int z = 1;
            }

            boolean isValid = true;
            List<Feature> oldEntries = getEntriesforLexeme(f.getWord());

            for (Feature f1 : oldEntries) {
                CCGFeature old = (CCGFeature) f1;
                if (old.getWord().equals(f.getWord())) {

                    if (old.getCategory().toString().equals(f.getCategory().toString())) {

                        if (old.getLambda() != null && f.getLambda() != null) {

                            if (f.getLambda().toString().equals(old.getLambda().toString())) {

                                CCGFeature newOne = null;

                                if (old.getInstanceId() != -1) {
                                    newOne = old.clone();
                                    if (f.isUsed()) {
                                        newOne.setIsUsed(true);
                                    }
                                    newOne.setScore(old.getScore() + f.getScore());
                                } else if (old.getInstanceId() == -1 && old.getScore() == 0.0) {
                                    newOne = old.clone();
                                    if (f.isUsed()) {
                                        newOne.setIsUsed(true);
                                    }
                                    newOne.setScore(old.getScore() + f.getScore());
                                }

                                //update newOne
                                dbProcessor.updateFeatureScore(newOne);

                            }

                        } else {
                            CCGFeature newOne = null;

                            if (old.getInstanceId() != -1) {
                                newOne = old.clone();
                                if (f.isUsed()) {
                                    newOne.setIsUsed(true);
                                }
                                newOne.setScore(old.getScore() + f.getScore());
                            } else if (old.getInstanceId() == -1 && old.getScore() == 0.0) {
                                newOne = old.clone();
                                if (f.isUsed()) {
                                    newOne.setIsUsed(true);
                                }
                                newOne.setScore(old.getScore() + f.getScore());
                            }

                            //update newOne
                            dbProcessor.updateFeatureScore(newOne);
                        }
                    }
                }
            }

            //dbProcessor.updateFeatureScore(conn, f1);
        } catch (Exception e) {
        }
    }

    public void udpateExistingFeatureAsUsed(Feature feature) {
        try {
            CCGFeature f1 = (CCGFeature) feature;
            dbProcessor.updateFeatureIsUsed(f1);
        } catch (Exception e) {
        }
    }

    public void udpateAllFeaturesAsUsed(String isUsed) {
        try {
            dbProcessor.updateAllFeaturesIsUsed(isUsed);
        } catch (Exception e) {
        }
    }

    public void udpateAllFeatureScore(double score) {
        try {
            dbProcessor.updateAllFeaturesScore(score);
        } catch (Exception e) {
        }
    }

    public void updateAllInducedFeaturesScore(double score) {
        try {
            dbProcessor.updateAllInducedFeaturesScore(score);
        } catch (Exception e) {
        }
    }

    public void updateDomainIndependentFeaturesScore(double score) {
        try {
            dbProcessor.updateAllInducedFeaturesScore(score);
        } catch (Exception e) {
        }
    }

    public void deleteAllFeatures() {
        try {
            dbProcessor.deleteAllFeatures();
        } catch (Exception e) {
        }
    }

    public void deleteAllInducedFeatures() {
        try {
            dbProcessor.deleteAllInducedFeatures();
        } catch (Exception e) {
        }
    }

    @Override
    public List<Feature> getEntriesforLexeme(String lexeme, int index) {
        try {
            List<Feature> features = dbProcessor.getFeaturesWithLemmaInstanceId(lexeme, index + "");
            String l2 = lemmatizer.lemmatize(lexeme);
            for (Feature f : dbProcessor.getFeaturesWithLemmaInstanceId(l2, index + "")) {
                

                if (!features.contains(f)) {
                    features.add(f);
                }
            }
            return features;

        } catch (Exception ex) {
            Logger.getLogger(CCGModelOnSQL.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

}
