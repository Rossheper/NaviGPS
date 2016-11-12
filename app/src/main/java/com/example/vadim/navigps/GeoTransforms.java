package com.example.vadim.navigps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Vadim on 23.11.2015.
 */
class Params_ECEF2LLA
{
    public static double a;
    public static double e2;
};
public class GeoTransforms {
    public MyMatrix toPZ90 = new MyMatrix(3);
    public MyMatrix toSK42 = new MyMatrix(3);
    public MyMatrix dxdydzPz = new MyMatrix(3, 1);
    public MyMatrix dxdydzSk42 = new MyMatrix(3, 1);
    private MyMatrix inversOmega = new MyMatrix(3, 3);
    private final double ro = 206264.8062;
    public int EditsGOST = 1;
    //Эллипсоид Красовского
    private final double aP = 6378245;                         //Большая полуось Краковского
    private final double alP = 1.0 / 298.3;                    //Сжатие эллипсоида
    private double e2P;// = 2 * alP - Math.Pow(alP, 2);     //Квадрат эксцентриситета

    //Эллипсоид WGS84
    private final double aW = 6378137;                         //Большая полуось WGS84
    private final double alW = 1.0 / 298.257223563;            //Сжатие эллипсоида
    private double e2W;// = 2 * alW - Math.Pow(alW, 2);     //Квадрат эксцентриситета

    private double _M;
    private double _N;
    private double _Ngeo;
    private double _Brad;
    private double _Lrad;
    private double _sinB;
    private double _sinL;
    private double _cosB;
    private double _cosL;
    private double _tanB;
    private double _sin2sq;
    private double _sin4sq;
    private double _sin6sq;
    private double _B;
    private double _L;
    private double _H;
    //Вспомогательные значения для преобразования эллипсоидов

    private double a;// = (aP + aW) / 2.0;
    private double e2;// = (e2P + e2W) / 2.0;
    private double da;// = aW - aP;//
    private double de2;// = e2W - e2P;//

    //Линейные элементы трансформирования, м

    public double dx;
    public double dy;
    public double dz;

    //Угловые элементы трансформирования, сек
    public double wx;
    public double wy;
    public double wz;

    //Дифференциальные различия масштабов
    public double ms;
    public double mToPZ90;
    public double mToSK42;
    public double M() {
        return _M;
    }
    public double N() {
        return _N;
    }
    public double Ngeo() {
        return _Ngeo;
    }
    public double B() {
        return _Brad;
    }
    public double L() {
        return _Lrad;
    }
    public GeoTransforms()
    {

        e2P = 2 * alP - Math.pow(alP, 2);     //Квадрат эксцентриситета
        e2W = 2 * alW - Math.pow(alW, 2);     //Квадрат эксцентриситета
        a = (aP + aW) / 2.0;
        e2 = (e2P + e2W) / 2.0;
        da = aW - aP;
        de2 = e2W - e2P;//

        for (int i = 0; i < toPZ90.M(); i++)
        {
            for (int j = 0; j < toPZ90.N(); j++)
            {
                if (i == j)
                {
                    toPZ90._mass[i][j] = 1;
                    toSK42._mass[i][j] = 1;
                }
                else
                {
                    toPZ90._mass[i][j] = 0;
                    toSK42._mass[i][j] = 0;
                }
            }
        }
        InitData();
        EditsGOST = 1;
    }
    private void InitData()
    {
        dx = 25;
        dy = -141;
        dz = -80;
        wx = 0;
        wy = 0;
        wz = 0;
        ms = 0.4808e-6;

        //dx = 23.9;
        //dy = -141.3;
        //dz = -80.9;
        //wx = 0;
        //wy = 0;
        //wz = 0;
        //ms = 0.4808e-6;

        mToPZ90 = 0.12e-6;
        mToSK42 = 0;

        dxdydzPz._mass[0][0] = -1.1;
        dxdydzPz._mass[1][0] = -0.3;
        dxdydzPz._mass[2][0] = -0.9;

        dxdydzSk42._mass[0][0] = 25;
        dxdydzSk42._mass[1][0] = -141;
        dxdydzSk42._mass[2][0] = -80;

        toPZ90._mass[0][1] = -DegreeSecondsToRadians(-0.2);
        toPZ90._mass[0][2] = 0;
        toPZ90._mass[1][0] = DegreeSecondsToRadians(-0.2);
        toPZ90._mass[1][2] = 0;
        toPZ90._mass[2][0] = 0;
        toPZ90._mass[2][1] = 0;
        mToPZ90 = 0.12e-6;

        toSK42._mass[0][1] = -DegreeSecondsToRadians(-0.66);
        toSK42._mass[0][2] = DegreeSecondsToRadians(-0.35);
        toSK42._mass[1][0] = DegreeSecondsToRadians(-0.66);
        toSK42._mass[1][2] = 0;
        toSK42._mass[2][0] = -DegreeSecondsToRadians(-0.35);
        toSK42._mass[2][1] = 0;
        mToSK42 = 0;

        inversOmega._mass[0][0] = 2;
        inversOmega._mass[1][0] = 0;
        inversOmega._mass[2][0] = 0;
        inversOmega._mass[0][1] = 0;
        inversOmega._mass[1][1] = 2;
        inversOmega._mass[2][1] = 0;
        inversOmega._mass[0][2] = 0;
        inversOmega._mass[1][2] = 0;
        inversOmega._mass[2][2] = 2;
    }
    public double DegreeSecondsToRadians(double degreeSec)
    {
        return (degreeSec * Math.PI) / 180 / 3600;
    }
    private double RadiansToDegreeSeconds(double rad)
    {
        return (rad / Math.PI) * 180 * 3600;
    }
    private void InitSinCos(double Latitude, double Longitude)
    {
        _Brad = Latitude * Math.PI / 180.0;
        _Lrad = Longitude * Math.PI / 180.0;
        _sinB = Math.sin(B());
        _sinL = Math.sin(L());
        _cosL = Math.cos(L());
        _cosB = Math.cos(B());
        _tanB = Math.tan(B());
        _M = a * (1 - e2) * Math.pow((1 - e2 * Math.pow(_sinB, 2)), -1.5);
        _N = a / Math.sqrt((1 - e2 * Math.pow(_sinB, 2)));
    }
    public MyMatrix ConvertToXYZWGS(double Bd, double Ld, double H)
    {
        List<Double> list = new ArrayList<Double>();
        MyMatrix res = new MyMatrix(3, 1);
        InitSinCos(Bd, Ld);
        _Ngeo = aW / Math.sqrt((1 - (e2W * Math.pow(_sinB, 2))));
        list.add((Ngeo() + H) * _cosB * _cosL);
        list.add((Ngeo() + H) * _cosB * _sinL);
        list.add(((1 - e2W) * Ngeo() + H) * _sinB);

        for (int i = 0; i < res.M(); i++)
            res._mass[i][0] = list.get(i);
        return res;
    }
    public MyMatrix ConvertToXYZSK42(double Bd, double Ld, double H)
    {
        List<Double> list = new ArrayList<Double>();
        MyMatrix res = new MyMatrix(3, 1);
        InitSinCos(Bd, Ld);
        _Ngeo = aP / Math.sqrt((1 - (e2P * Math.pow(_sinB, 2))));
        list.add((Ngeo() + H) * _cosB * _cosL);
        list.add((Ngeo() + H) * _cosB * _sinL);
        list.add(((1 - e2P) * Ngeo() + H) * _sinB);

        for (int i = 0; i < res.M(); i++)
            res._mass[i][0] = list.get(i);
        return res;
    }
    public MyMatrix WGS84_To_PZ90(MyMatrix XYZ, int direct)
    {
        if (direct == 1)
            return XYZ.Sub(XYZ.Mul(XYZ.Mul((1 + mToPZ90),toPZ90),XYZ), dxdydzPz);
        else
            return XYZ.Mul((1 - mToPZ90), XYZ.Sum(XYZ.Mul((XYZ.Sum(XYZ.Mul((-1),toPZ90),inversOmega)), XYZ), dxdydzPz));
    }
    public MyMatrix PZ90_To_SK42(MyMatrix PZ90, int direct)
    {
        if (direct == 1)
            return PZ90.Sub(PZ90.Mul(PZ90.Mul((1 + mToSK42),toSK42),PZ90), dxdydzSk42);
        else
            return PZ90.Mul((1 - mToSK42), PZ90.Sum(PZ90.Mul((PZ90.Sum(PZ90.Mul((-1), toSK42),inversOmega)), PZ90), dxdydzSk42));
    }
    public MyMatrix WGS84_To_SK42(double Bd, double Ld, double H)
    {
        return PZ90_To_SK42(WGS84_To_PZ90(ConvertToXYZWGS(Bd, Ld, H), 1), 1);
    }
    public MyMatrix SK42_To_WGS84(double Bd, double Ld, double H)
    {
        return WGS84_To_PZ90(PZ90_To_SK42(ConvertToXYZSK42(Bd, Ld, H), 0), 0);
    }
    public List<Double> ECEF2LLA(double x, double y, double z)
    {
        List<Double> list = new ArrayList<Double>();
        double d = D(x, y);
        double r, c, p = 0;
        double s1, s2 = 0;
        double b = 0;
        if (d == 0) {
            _B = (Math.PI / 2.0) * (z / Math.abs(z));
            _L = 0;
            _H = z * Math.sin(_B) - Params_ECEF2LLA.a * Math.sqrt((1 - (Params_ECEF2LLA.e2 * Math.pow(Math.sin(_B), 2))));
        }
        else
        {
            _L = Math.abs(Math.asin(y / d));
            if (y < 0 && x > 0)
                _L = Math.PI * 2 - _L;
            else if (y < 0 && x < 0)
                _L = Math.PI + _L;
            else if (y > 0 && x < 0)
                _L = Math.PI - _L;
            else if (y == 0 && x > 0)
                _L = 0;
            else if (y == 0 && x < 0)
                _L = Math.PI;
            else
                _L = _L;
        }
        if (z == 0)
        {
            _B = 0;
            _H = d - Params_ECEF2LLA.a;
        }
        else
        {
            r = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
            c = Math.asin(z / r);
            p = (Params_ECEF2LLA.e2 * Params_ECEF2LLA.a) / (2 * r);
            s1 = 0;

            do
            {
                b = c + s1;
                s2 = Math.asin((p * Math.sin(2 * b)) / Math.sqrt((1 - (Params_ECEF2LLA.e2 * Math.pow(Math.sin(b), 2)))));
                d = Math.abs(s2 - s1);
                s1 = s2;
            }
            while (d > 1e-12);
            _B = b;
            _H = D(x, y) * Math.cos(_B) + z * Math.sin(_B) - Params_ECEF2LLA.a * Math.sqrt((1 - (Params_ECEF2LLA.e2 * Math.pow(Math.sin(_B), 2))));
        }
        list.add(_B * 180 / Math.PI);
        list.add(_L * 180 / Math.PI);
        list.add(_H);
        return list;
    }
    public List<Double> ECEF2LLA_SK42(double x, double y, double z)
    {

        Params_ECEF2LLA.a = aP;
        Params_ECEF2LLA.e2 = e2P;
        return ECEF2LLA(x, y, z);
    }
    public List<Double> ECEF2LLA_WGS84(double x, double y, double z)
    {
        Params_ECEF2LLA.a = aW;
        Params_ECEF2LLA.e2 = e2W;
        return ECEF2LLA(x, y, z);
    }
    public List<Double> WGS84_To_SK42(double Bd, double Ld, double H, int transform)
    {
        List<Double> resultList = new ArrayList<Double>();
        InitSinCos(Bd, Ld);
        resultList.add(WGS84_SK42_Lat(Bd, Ld, H, transform));
        resultList.add(WGS84_SK42_Lon(Bd, Ld, H, transform));
        resultList.add(WGS84_SK42_Alt(Bd, Ld, H, transform));
        return resultList;
    }

    public double Gauss_Kruger_X(double Bdegree, double Ldegree)
    {
        int n = nZoneToXY(Ldegree);
        double l = lZoneToXY(Ldegree, n);
        double l2 = l * l;
        double f2 = 0, f3 = 0, f4 = 0, f5 = 0, f6 = 0;
        InitForGausse(Bdegree, Ldegree);
        double B = 0;
        B = Bdegree * Math.PI / 180.0;
        f6 = l2 * (109500 - 574700 * _sin2sq + 863700 * _sin4sq - 398600 * _sin6sq);
        f5 = l2 * (278194 - 830174 * _sin2sq + 572434 * _sin4sq - 16010 * _sin6sq + f6);
        f4 = l2 * (672483.4 - 811219.9 * _sin2sq + 5420.0 * _sin4sq - 10.6 * _sin6sq + f5);
        f3 = l2 * (1594561.25 + 5336.535 * _sin2sq + 26.790 * _sin4sq + 0.149 * _sin6sq + f4);
        f2 = (16002.8900 + 66.9607 * _sin2sq + 0.3515 * _sin4sq - f3);

        return 6367558.4968 * B - Math.sin(2 * B) * f2;
    }
    public double Gauss_Kruger_Y(double Bdegree, double Ldegree)
    {
        int n = nZoneToXY(Ldegree);
        double l = lZoneToXY(Ldegree, n);
        double l2 = l * l;
        double f1 = 0, f2 = 0, f3 = 0, f4 = 0;
        InitForGausse(Bdegree, Ldegree);

        f4 = l2 * (79690 - 866190 * _sin2sq + 1730360 * _sin4sq - 945460 * _sin6sq);
        f3 = l2 * (270806 - 1523417 * _sin2sq + 1327645 * _sin4sq - 21701 * _sin6sq + f4);
        f2 = l2 * (1070204.16 - 2136826.66 * _sin2sq + 17.98 * _sin4sq - 11.99 * _sin6sq + f3);
        f1 = l * Math.cos(B()) * (6378245 + 21346.1415 * _sin2sq + 107.1590 * _sin4sq + 0.5977 * _sin6sq + f2);
        return (5 + 10 * n) * Math.pow(10, 5) + f1;
    }

    private double D(double x, double y)
    {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    private void InitForGausse(double Bdegree, double Ldegree)
    {
        InitSinCos(Bdegree, Ldegree);
        _sin2sq = Math.pow(_sinB, 2);
        _sin4sq = Math.pow(_sinB, 4);
        _sin6sq = Math.pow(_sinB, 6);
    }
    private double deltaB(double Bd, double Ld, double H)
    {
        double f1 = 0, f2 = 0, f3 = 0, f4 = 0, f5 = 0, f6 = 0, f7 = 0;
        f1 = a * e2 * _sinB * _cosB * da;
        f2 = ((Math.pow(N(), 2) / Math.pow(a, 2)) + 1) * N() * _sinB * _cosB * de2;
        f3 = (dx * _cosL + dy * _sinL) * _sinB;
        f4 = dz * _cosB;
        f5 = wx * _sinL * (1 + e2 * Math.cos(2 * B()));
        f6 = wy * _cosL * (1 + e2 * Math.cos(2 * B()));
        f7 = ro * ms * e2 * _sinB * _cosB;

        return (ro / (M() + H)) * (N() / f1 + f2 / 2.0 - f3 + f4) - f5 + f6 - f7;
    }
    private double deltaL(double Bd, double Ld, double H)
    {
        double tmp1 = 0, tmp2 = 0, tmp3 = 0, tmp4 = 0;
        tmp1 = ((-dx) * _sinL + dy * _cosL);
        tmp2 = _tanB * (1 - e2);
        tmp3 = wx * _cosL + wy * _sinL;
        tmp4 = wz;
        return (ro / ((N() + H) * _cosB)) * tmp1 + tmp2 * tmp3 - tmp4;
    }
    private double WGS84Alt(double Bd, double Ld, double H)
    {
        double f1 = 0, f2 = 0, f3 = 0, f4 = 0, f5 = 0, f6 = 0, f7 = 0, f8 = 0;
        f1 = (-a) / N() * da;
        f2 = N() * Math.pow(_sinB, 2) * de2 / 2.0;
        f3 = (dx * _cosL + dy * _sinL) * _cosB;
        f4 = dz * _sinB;
        f5 = N() * e2 * _sinB * _cosB;
        f6 = (wx / ro) * _sinL;
        f7 = (wy / ro) * _cosL;
        f8 = ((Math.pow(a, 2) / N()) + H) * ms;

        return f1 + f2 + f3 + f4 - (f5 * (f6 - f7)) + f8;
    }
    private double WGS84_SK42_Lat(double Bd, double Ld, double H, int transform)
    {
        if (transform == 1)
            return Bd - (deltaB(Bd, Ld, H) / 3600.0);
        else
            return Bd + (deltaB(Bd, Ld, H) / 3600.0);
    }
    private double WGS84_SK42_Lon(double Bd, double Ld, double H, int transform)
    {
        if (transform == 1)
            return Ld - (deltaL(Bd, Ld, H) / 3600.0);
        else
            return Ld + (deltaL(Bd, Ld, H) / 3600.0);
    }
    private double WGS84_SK42_Alt(double Bd, double Ld, double H, int transform)
    {
        if (transform == 1)
            return H - (WGS84Alt(Bd, Ld, H));
        else
            return H + (WGS84Alt(Bd, Ld, H));
    }
    private double lGauss(double x, double y)
    {
        double b0 = B0(x);
        double z0 = Z0(x, y);
        double z02 = z0 * z0;
        double l4 = 0, l5 = 0, l6 = 0, l7 = 0, l8 = 0;
        double sinB0 = Math.sin(b0);
        double sin2 = Math.pow(sinB0, 2);
        double sin4 = Math.pow(sinB0, 4);
        double sin6 = Math.pow(sinB0, 6);
        l8 = z02 * (0.0038 + 0.0524 * sin2 + 0.0482 * sin4 + 0.0032 * sin6);
        l7 = z02 * (0.01225 + 0.09477 * sin2 + 0.03282 * sin4 - 0.00034 * sin6 - l8);
        l6 = z02 * (0.0420025 + 0.1487407 * sin2 + 0.0059420 * sin4 - 0.0000150 * sin6 - l7);
        l5 = z02 * (0.16778975 + 0.16273586 * sin2 - 0.00052490 * sin4 - 0.00000846 * sin6 - l6);
        l4 = z0 * (1 - 0.0033467108 * sin2 - 0.0000056002 * sin4 - 0.0000000187 * sin6 - l5);
        return l4;
    }
    private double lZoneToXY(double Ldegree, double n)
    {
        return (Ldegree - (3 + 6 * (n - 1))) / 57.29577951;
    }
    private int nZoneToXY(double Ldegree)
    {
        return (int)((6 + Ldegree) / 6.0);
    }
    private double lZoneToBL(double Ldegree, double n)
    {
        double l = 0;
        return (6 * (n - 0.5)) / 57.29577951 + l;
    }
    private int nZoneToBL(double y)
    {
        return (int)(y / Math.pow(10, 6));
    }
    private double B0(double x)
    {
        double beta = Beta(x);
        double b0 = beta + Math.sin(2 * beta) * (0.00252588685 - 0.00001491860 * Math.pow(Math.sin(beta), 2) + 0.00000011904 * Math.pow(Math.sin(beta), 4));
        return b0;
    }
    private double Beta(double x)
    {
        return x / 6367558.4968;
    }
    private double Z0(double x, double y)
    {
        double n = nZoneToBL(y);
        double b0 = B0(x);
        double z0 = (y - (10 * n + 5) * Math.pow(10, 5)) / (6378245 * Math.cos(b0));
        return z0;
    }
}
