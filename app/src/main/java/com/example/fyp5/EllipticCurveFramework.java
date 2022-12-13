package com.example.fyp5;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.common.internal.Objects;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EllipticCurveFramework {

        final BigInteger p = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F", 16);
        // P = number of points on the elliptic curve
        // Finite field p = contains finite number of elements
        // Finite filed, number of elements must be a prime {2,3} or a prime power {2^3=8, 5^2=25}

        final int a = 0;
        final int b = 7;
        // Curve parameters used to shape up the curve

        final BigInteger gX = new BigInteger("79BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798", 16);
        final BigInteger gY = new BigInteger("483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8", 16);
        final BigInteger[] gPoint = {gX,gY};
        // Generator point g with coordinates x and y

        final BigInteger n = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", 16);
        // The number of actual points is defined by n

//        19151398136239661237422250561712917086980684758326767648406078176918393735282
//        1453714277448899330796875108471283983549338801323505622815336896137228845633

    public static BigInteger HextoBinary(String hex)
    {
        BigInteger convert = new BigInteger(hex, 16);
        return convert;
    }

    public static BigInteger modInv(BigInteger difference, BigInteger n)
    {
        BigInteger lm = new BigInteger("1", 10);
        BigInteger hm = new BigInteger("0", 10);
        BigInteger low = difference.mod(n);
        BigInteger high = n;
        BigInteger one = new BigInteger("1",10);

//        low-1 > 0 (1= positive, 0= equal, -1= negative)
        while(low.compareTo(one)==1)
        {
            BigInteger ratio = high.divide(low);
            BigInteger nm = hm.subtract(lm.multiply(ratio));
            BigInteger updated = high.subtract(low.multiply(ratio));

            hm = lm;
            lm = nm;
            high = low;
            low = updated;
        }
        return lm.mod(n);
    }

    public static BigInteger[] ECAddition(BigInteger[] xPoint, BigInteger[] gPoint)
    {
        BigInteger p = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F", 16);
        BigInteger xDifference = gPoint[0].subtract(xPoint[0]);
        BigInteger lamdaAdd = ((gPoint[1].subtract(xPoint[1])).multiply(modInv(xDifference,p))).mod(p);
        BigInteger xR = ((lamdaAdd.multiply(lamdaAdd)).subtract(xPoint[0]).subtract(gPoint[0])).mod(p);
        BigInteger yR = (lamdaAdd.multiply(xPoint[0].subtract(xR)).subtract(xPoint[1])).mod(p);
        BigInteger[] pR = {xR,yR};
        return pR;
    }

    public static BigInteger[] ECDoubling(BigInteger[] gPoint)
    {
        BigInteger p = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F", 16);
        BigInteger three = new BigInteger("3",10);
        BigInteger two = new BigInteger("2",10);
        BigInteger lamdaDouble = ((three.multiply(gPoint[0].multiply(gPoint[0]))).multiply(modInv(two.multiply(gPoint[1]),p))).mod(p);
        BigInteger xR = (((lamdaDouble.multiply(lamdaDouble)).subtract(two.multiply(gPoint[0])))).mod(p);
        BigInteger yR = ((lamdaDouble.multiply(gPoint[0].subtract(xR))).subtract(gPoint[1])).mod(p);
        BigInteger[] pR = {xR,yR};
        return pR;
    }

    public static BigInteger[] publicKeyGeneration(BigInteger[] gPoint, BigInteger privateKey)
    {
        BigInteger n = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", 16);
        BigInteger[] Q= gPoint;

            int count = 0;
            String privateKeyBin = privateKey.toString(2);
            for (int i = 0; i < privateKeyBin.length(); i++) {
                count++;
            }

            String zeros = "";
            if(count < 256)
            {
                int j = 256;
                int padding = j-count;
                for(int i =0; i<padding; i++)
                {
                    zeros += "0";
                }
            }

            List<String> binaryArray = new ArrayList<String>();
            String paddedBinary = zeros+privateKeyBin;
            for(int i=0; i<paddedBinary.length(); i++)
            {
                char a = paddedBinary.charAt(i);
                binaryArray.add(String.valueOf(a));
            }

            for(int i=2; i<paddedBinary.length(); i++)
            {
                Q = ECDoubling(Q);
                {
                    if(binaryArray.get(i).equals("1"))
                    {
                        Q = ECAddition(Q,gPoint);
                    }
                }
            }

        return Q;
    }

}
