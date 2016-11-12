package com.example.vadim.navigps;

/**
 * Created by Vadim on 23.11.2015.
 */
public class MyMatrix {
    private int _m;
    private int _n;
    public double[][] _mass;
    public int M() {
        return _m;
    }
    public void M(int value){
        _m = value;
    }
    public int N() {
        return _n;
    }
    public void N(int value){
        _n = value;
    }
    public MyMatrix() {
        _m = 3;
        _n = 3;
        _mass = new double[_m][_n];
        for (int i = 0; i < _m; i++)
            for (int j = 0; j < _n; j++)
                _mass[i][j] = 0;
    }
    public MyMatrix(int n){
        _m = n;
        _n = n;
        _mass = new double[_m][_n];
        for (int i = 0; i < _m; i++)
            for (int j = 0; j < _n; j++)
                _mass[i][j] = 0;
    }
    public MyMatrix(int m, int n) {
        _m = m;
        _n = n;
        _mass = new double[m][n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                _mass[i][j] = 0;
    }
    public  MyMatrix Sum(MyMatrix A, MyMatrix B)
    {
        MyMatrix C = new MyMatrix(A._m, B._n);
        for (int i = 0; i < A._m; i++)
            for (int j = 0; j < B._n; j++)
                C._mass[i][j] = A._mass[i][j] + B._mass[i][j];
        return C;
    }
    public  MyMatrix Sum(double A, MyMatrix B)
    {
        MyMatrix C = new MyMatrix(B._m, B._n);
        for (int i = 0; i < B._m; i++)
            for (int j = 0; j < B._n; j++)
                C._mass[i][j] = A + B._mass[i][j];
        return C;
    }
    public  MyMatrix Sub(MyMatrix A, MyMatrix B)
    {
        MyMatrix C = new MyMatrix(A._m, B._n);
        for (int i = 0; i < A._m; i++)
            for (int j = 0; j < B._n; j++)
                C._mass[i][j] = A._mass[i][j] - B._mass[i][j];
        return C;
    }
    public  MyMatrix Mul(double alpha, MyMatrix A)
    {
        MyMatrix C = new MyMatrix(A._m, A._n);
        for (int i = 0; i < A._m; i++)
            for (int j = 0; j < A._n; j++)
                C._mass[i][j] = alpha * A._mass[i][j];
        return C;
    }
    public  MyMatrix Mul(MyMatrix A, MyMatrix B)
    {
        MyMatrix C = new MyMatrix(A._m, B._n);
        for (int i = 0; i < A._m; i++)
            for (int j = 0; j < B._n; j++)
                for (int r = 0; r < A._n; r++)
                    C._mass[i][j] += A._mass[i][r] * B._mass[r][j];
        return C;
    }
}
