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
 * This is the simple atomic R3 point type.
 * 
 * This class contains all the mathematical operations to do with manipulating
 * points that the demo needs.
 * 
 * @author Ryan Fechney
 */
package Core.Geometry;

public class Point 
{

    public double x = 0;
    public double y = 0;
    public double z = 0;

    public Point() 
    {
    }

    public Point(double newX, double newY, double newZ)
    {
        x = newX;
        y = newY;
        z = newZ;
    }

    public Point(Point newPoint) 
    {
        x = newPoint.x;
        y = newPoint.y;
        z = newPoint.z;
    }

    public void overwriteWith(Point input) 
    {
        x = input.x;
        y = input.y;
        z = input.z;
    }

    public void overwriteWith(double newX, double newY, double newZ) 
    {
        x = newX;
        y = newY;
        z = newZ;
    }

    public double modulus2()
    {
        return x * x + y * y + z * z;
    }

    public double modulus() 
    {
        double modulus2 = modulus2();
        if(modulus2 == 1.0)
        {
            return 1.0;
        }
        return Math.pow(modulus2, 0.5);
    }

    public void add(Point other) 
    {
        x += other.x;
        y += other.y;
        z += other.z;
    }

    public void addScale(double scale, Point other) 
    {
        x += other.x * scale;
        y += other.y * scale;
        z += other.z * scale;
    }

    public void subtract(Point other)
    {
        x -= other.x;
        y -= other.y;
        z -= other.z;
    }

    public void scale(double scale)
    {
        x *= scale;
        y *= scale;
        z *= scale;
    }

    public void normalise()
    {
        double modulus = modulus();
        if(modulus == 1.0)
        {
            return;
        }
        scale(1 / modulus);
    }
    
   /* public void fastNormalise()
    {
        scale(Functions.fastReciprocalSquareRoot((float)modulus2()));
    }    */ 

    public void dotZero(Point base) 
    {
        double t = -(Point.sdot(base, this));
        double b = (Point.sdot(base, base));
        double s = t / b;

        x += base.x * s;
        y += base.y * s;
        z += base.z * s;
    }

    public void rotate(Point axis, double angle)
    {
        double r = axis.modulus();
        double ax = axis.x / r;
        double ay = axis.y / r;
        double az = axis.z / r;

        // from left to right, as English reads.
        double s = Math.sin(angle);
        double c = Math.cos(angle);

        double m00 = (ax * ax) * (1 - c) + c;
        double m10 = (ax * ay) * (1 - c) - s * az;
        double m20 = (ax * az) * (1 - c) + s * ay;

        double m01 = (ay * ax) * (1 - c) + s * az;
        double m11 = (ay * ay) * (1 - c) + c;
        double m21 = (ay * az) * (1 - c) - s * ax;

        double m02 = (az * ax) * (1 - c) - s * ay;
        double m12 = (az * ay) * (1 - c) + s * ax;
        double m22 = (az * az) * (1 - c) + c;

        double tx = x * m00 + y * m10 + z * m20;
        double ty = x * m01 + y * m11 + z * m21;
        z = x * m02 + y * m12 + z * m22;
        x = tx;
        y = ty;
    }

    public void base(Point u, Point v, Point w) 
    {
        double tx = u.x * x + v.x * y + w.x * z;
        double ty = u.y * x + v.y * y + w.y * z;
        z = u.z * x + v.z * y + w.z * z;
        x = tx;
        y = ty;
    }

    public void cross(Point u, Point v) 
    {
        x = (u.y * v.z - u.z * v.y);
        y = -(u.x * v.z - u.z * v.x);
        z = (u.x * v.y - u.y * v.x);
    }

    public void interpolate(Point inputA, Point inputB, double distance) 
    {
        double invd = 1.0d - distance;
        x = inputA.x * invd + inputB.x * distance;
        y = inputA.y * invd + inputB.y * distance;
        z = inputA.z * invd + inputB.z * distance;
    }

    /* Static helpers
     */
    static public Point ssubtract(Point inputA, Point inputB) 
    {
        return new Point(inputA.x - inputB.x, inputA.y - inputB.y, inputA.z - inputB.z);
    }

    static public Point sinterpolate(Point inputA, Point inputB, double distance) 
    {
        Point result = new Point(0, 0, 0);
        result.interpolate(inputA, inputB, distance);

        return result;
    }

    static public Point scross(Point u, Point v) 
    {
        Point result = new Point(0, 0, 0);
        result.x = (u.y * v.z - u.z * v.y);
        result.y = -(u.x * v.z - u.z * v.x);
        result.z = (u.x * v.y - u.y * v.x);

        return result;
    }

    static public double sdot(Point inputA, Point inputB) 
    {
        return inputA.x * inputB.x + inputA.y * inputB.y + inputA.z * inputB.z;
    }
}
