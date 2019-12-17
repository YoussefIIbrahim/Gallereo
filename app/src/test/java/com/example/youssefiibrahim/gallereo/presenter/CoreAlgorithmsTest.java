package com.example.youssefiibrahim.gallereo.presenter;

import com.example.youssefiibrahim.gallereo.model.PairWrapper;
import com.example.youssefiibrahim.gallereo.model.Response;
import com.example.youssefiibrahim.gallereo.model.ResponseItem;
import com.example.youssefiibrahim.gallereo.model.ResponseWrapper;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;

public class CoreAlgorithmsTest {

    @Test
    public void shouldFilterImages() throws IOException {
        String input = "Nice Desert and cat";
        PairWrapper pairWrapper = communication.processInput(input);

        ResponseWrapper expectedWrapper = new ResponseWrapper();

        Response response1 = new Response("1");
        Response response2 = new Response("2");
        Response response3 = new Response("3");

        ResponseItem item = new ResponseItem("natural environment", 0.9935060739517212, true);

        ResponseItem item2 = new ResponseItem("desert", 0.9629292488098145, true);

        ResponseItem item3 = new ResponseItem("cat", 0.9, true);

        response1.add(item);    // 1
        response2.add(item2);   // 2
        response3.add(item3);   // 3

        expectedWrapper.add(response1);
        expectedWrapper.add(response2);
        expectedWrapper.add(response3);
        ResponseWrapper.singleton = expectedWrapper;

        ArrayList<String> lst = CoreAlgorithms.filterImages(pairWrapper);
        assertEquals(2, lst.size());
        assertEquals(response2.id, lst.get(0));

    }
}