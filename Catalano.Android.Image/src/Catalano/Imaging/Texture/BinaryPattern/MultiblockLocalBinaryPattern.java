// Catalano Android Imaging Library
// The Catalano Framework
//
// Copyright © Diego Catalano, 2015
// diego.catalano at live.com
//
//    This library is free software; you can redistribute it and/or
//    modify it under the terms of the GNU Lesser General Public
//    License as published by the Free Software Foundation; either
//    version 2.1 of the License, or (at your option) any later version.
//
//    This library is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//    Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public
//    License along with this library; if not, write to the Free Software
//    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
//

package Catalano.Imaging.Texture.BinaryPattern;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Tools.ImageHistogram;
import Catalano.Imaging.Tools.IntegralImage;

/**
 * Multi-block Local binary patterns (MBLBP) is a type of feature used for classification in computer vision.
 * It has since been found to be a powerful feature for texture classification.
 * 
 * @author Diego Catalano
 */
public class MultiblockLocalBinaryPattern implements IBinaryPattern{
    
    private int recWidth;
    private int recHeight;

    /**
     * Initializes a new instance of the MultiblockLocalBinaryPattern class.
     */
    public MultiblockLocalBinaryPattern() {
        this(3,2);
    }

    /**
     * Initializes a new instance of the MultiblockLocalBinaryPattern class.
     * @param width Width of the rectangle.
     * @param height Height of the rectangle.
     */
    public MultiblockLocalBinaryPattern(int width, int height) {
        this.recWidth = width;
        this.recHeight = height;
    }

    @Override
    public ImageHistogram ProcessImage(FastBitmap fastBitmap) {
        
        if (!fastBitmap.isGrayscale()) {
            try {
                throw new Exception("Multiblock LBP works only with grayscale images.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        IntegralImage ii = new IntegralImage(fastBitmap);
        int[] hist = new int[256];
        
        int width = fastBitmap.getWidth() - 3 * recWidth;
        int height = fastBitmap.getHeight() - 3 * recHeight;
        int[] mask = new int[9];
        
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                
                //Get rectangle mean for each top block
                mask[0] = (int)ii.getRectangleMean(i, j, i + recHeight - 1, j + recWidth - 1);
                mask[1] = (int)ii.getRectangleMean(i, j + recWidth - 1, i + recHeight - 1, j + 2 * recWidth - 1);
                mask[2] = (int)ii.getRectangleMean(i, j + 2 * recWidth, i + recHeight - 1, j + 3 * recWidth - 1);
                
                //Get rectangle mean for each mid block
                mask[3] = (int)ii.getRectangleMean(i + recHeight, j, i + 2 * recHeight - 1, j + recWidth - 1);
                mask[4] = (int)ii.getRectangleMean(i + recHeight, j + recWidth - 1, i + 2 * recHeight - 1, j + 2 * recWidth - 1);
                mask[5] = (int)ii.getRectangleMean(i + recHeight, j + 2 * recWidth, i + 2 * recHeight - 1, j + 3 * recWidth - 1);
                
                //Get rectangle mean for each bot block
                mask[6] = (int)ii.getRectangleMean(i + 2 * recHeight, j, i + 3 * recHeight - 1, j + recWidth - 1);
                mask[7] = (int)ii.getRectangleMean(i + 2 * recHeight, j + recWidth - 1, i + 3 * recHeight - 1, j + 2 * recWidth - 1);
                mask[8] = (int)ii.getRectangleMean(i + 2 * recHeight, j + 2 * recWidth, i + 3 * recHeight - 1, j + 3 * recWidth - 1);
                
                int sum = 0;
                //Compute the LBP
                if (mask[4] < mask[0])    sum += 128;
                if (mask[4] < mask[1])    sum += 64;
                if (mask[4] < mask[2])    sum += 32;
                if (mask[4] < mask[5])    sum += 16;
                if (mask[4] < mask[8])    sum += 8;
                if (mask[4] < mask[7])    sum += 4;
                if (mask[4] < mask[6])    sum += 2;
                if (mask[4] < mask[3])    sum += 1;
                hist[sum]++;
            }
        }
        return new ImageHistogram(hist);
    }
}