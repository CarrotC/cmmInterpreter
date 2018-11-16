package util.impl;

import util.FileHandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileHandlerImpl implements FileHandler {
    @Override
    public String FileToString(String fileName) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        StringBuilder builder = new StringBuilder();
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            builder.append(str + "\n");
        }
        bufferedReader.close();
        return builder.toString();
    }
}
