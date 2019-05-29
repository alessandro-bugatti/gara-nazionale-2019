/*
  Copyright (C) 2019 Alessandro Bugatti (alessandro.bugatti@istruzione.it)

  This program is free software; you can redistribute it and/or
  modify it under the terms of the GNU General Public License
  as published by the Free Software Foundation; either version 2
  of the License, or (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

/*! \file
 *  \brief 
 *  \author Alessandro Bugatti
 *  \version 0.1
 *  \date  Creazione 14-mag-2019
 *  \date  Ultima modifica 14-mag-2019
 */

package parallel;

import capre.Capra;
import java.util.concurrent.RecursiveAction;


public class ParallelMergeSort extends RecursiveAction {
    private final Capra[] capre;
    private final Capra[] supporto;
    private final int left;
    private final int right;

    public ParallelMergeSort(Capra[] array, int left, int right) {
            this.capre = array;
            this.supporto = new Capra[array.length];
            this.left = left;
            this.right = right;
    }

    @Override
    protected void compute() {
            if (left < right) {
                    final int center = (left + right) / 2;
                    final ParallelMergeSort sinistra = 
                            new ParallelMergeSort(capre, left, center);
                    final ParallelMergeSort destra = 
                            new ParallelMergeSort(capre, center + 1, right);
                    invokeAll(sinistra, destra);
                    merge(capre, supporto, left, center, right);
            }
    }
        
    private void merge (Capra capre[], Capra supporto[], int left, int center, int right)
    {
        int i = left;
        int j = center + 1;
        int k = 0;
        Capra b[] = new Capra[right - left + 1];
        while(i <= center && j <= right)
        {
            if (capre[i].compareTo(capre[j]) <= 0)
            {
                b[k] = capre[i];
                i++;
            }         
            else{
                b[k] = capre[j];
                j++;
            }
          k++;
        }

        while (i <= center){
            b[k] = capre[i];
            i++;
            k++;
        }

       while (j <= right){
            b[k] = capre[j];
            j++;
            k++;
        }

        for (k = left; k <= right; k++)
            capre[k] = b[k-left];
    }
}

