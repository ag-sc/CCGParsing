/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Derivation;

import java.util.Comparator;

/**
 *
 * @author sherzod
 */
public class CoveringComparator implements Comparator<Covering>{

    @Override
    public int compare(Covering o1, Covering o2) {
        return o1.compareTo(o2);
    }
    
}
