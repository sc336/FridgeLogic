package com.example.piduck.fridgelogic;

import android.content.Context;
import android.content.res.TypedArray;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by piduck on 26/04/16.
 * TODO: add frequency distribution
 */
public class WordSelector implements Iterable {
    private List<String> wordPool;
    private List<String> wordSelection;
    private Random random = new Random();

    public WordSelector(String wordPath, int wordCount) {
        try {
            Scanner scanWords = new Scanner(new File(wordPath));
            initialise(scanWords, wordCount);
        }
        catch (Exception e) { e.printStackTrace(); }
        //TODO: Add proper exception handling for a word file that's not there
    }

    public WordSelector(InputStream rawInput, int wordCount) {
        Scanner scanWords = new Scanner(rawInput);
        initialise(scanWords, wordCount);
    }

    public WordSelector(TypedArray resourceIDs, int wordCount, Context context) {
        //TODO: standardise constructors
        wordPool = new LinkedList<String>();
        wordSelection= new LinkedList<String>();
        for(int i=0;i<resourceIDs.length();i++) {
            Scanner scanWords = new Scanner(context.getResources().openRawResource(resourceIDs.getResourceId(i,0)));
            while (scanWords.hasNext()) wordPool.add(scanWords.next().trim());
        }

        Collections.shuffle(wordPool);
        wordSelection = wordPool.subList(0,wordCount-1);  //FIXME: this will break if wordpool < wordCount
        /*for(int i = 0; i<wordCount; i++) {
            int r = random.nextInt(wordPool.size());
            wordSelection.add(wordPool.get(r));
            wordPool.remove(r);
        }*/
    }

    public void initialise(Scanner inputScan, int wordCount) {
        wordPool = new LinkedList<String>();
        wordSelection= new LinkedList<String>();
        while (inputScan.hasNext())
            wordPool.add(inputScan.next().trim());

        Collections.shuffle(wordPool);
        wordSelection = wordPool.subList(0,wordCount-1);  //FIXME: this will break if wordpool < wordCount
        /*for(int i = 0; i<wordCount; i++) {
            wordSelection.add(wordPool.get(random.nextInt(wordPool.size())));
        }*/
    }

    @Override
    public Iterator<String> iterator() {
        return wordSelection.iterator();
    }

    public int size() {
        return wordSelection.size();
    }
}
