/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Decoder;

import Derivation.Covering;
import Derivation.Derivation;
import Instance.Instance;
import Lemmatizer.Lemmatizer;
import Model.Model;
import java.util.List;

/**
 *
 * @author sherzod
 */
public interface Decoder {
    
    public void setUseInstanceBasedFeatures(boolean b);

    public void setStackSize(int stackSize);
    
    public List<Covering> decode(String[] tokens, int indexOfInstance);
}
