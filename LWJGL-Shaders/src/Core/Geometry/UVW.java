/*
Copyright (c) 2011, Ryan Fechney - ryan.fechney gmail
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met: 

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer. 
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution. 

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

/**
 * UVW's are collections of three vectors - usually as an R3 set of orthogonal
 * vectors describing the normal.  WWe've got a bit of functionality in here
 * for find inverses, which are useful for reverse transforms.
 *
 * @author Ryan Fehcney
 */
package Core.Geometry;

public class UVW 
{
    // Some defaults
    public Point U = new Point(0, 0, 0);
    public Point V = new Point(0, 0, 0);
    public Point W = new Point(0, 0, 0);

    public void overwriteWith(UVW input) 
    {
        U.overwriteWith(input.U);
        V.overwriteWith(input.V);
        W.overwriteWith(input.W);
    }

    public void overwriteWithInverseOf(UVW input)
    {
        /* Find the inverse of the input and put it in this.
         * Please note U,V and W are considered column vectors.
         */        
        U.overwriteWith(
                input.V.y * input.W.z - input.V.z * input.W.y,
                input.W.y * input.U.z - input.W.z * input.U.y,
                input.U.y * input.V.z - input.U.z * input.V.y);
        V.overwriteWith(
                input.W.x * input.V.z - input.W.z * input.V.x,
                input.U.x * input.W.z - input.U.z * input.W.x,
                input.V.x * input.U.z - input.V.z * input.U.x);
        W.overwriteWith(
                input.V.x * input.W.y - input.V.y * input.W.x,
                input.W.x * input.U.y - input.W.y * input.U.x,
                input.U.x * input.V.y - input.U.y * input.V.x);

        double scale = input.U.x * U.x + input.V.x * U.y + input.W.x * U.z;
        scale = 1 / scale;
        U.scale(scale);
        V.scale(scale);
        W.scale(scale);
    }
    
    public void normalise()
    {
        U.normalise();
        V.normalise();
        W.normalise();
    }
}
