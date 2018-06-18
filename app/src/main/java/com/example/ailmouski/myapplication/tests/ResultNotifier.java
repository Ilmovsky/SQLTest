package com.example.ailmouski.myapplication.tests;

public interface ResultNotifier {
    void send(TestResult result);
    void complete();
}
