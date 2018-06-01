package com.gruppometa.unimarc.output;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.gruppometa.unimarc.object.OutItem;
import com.gruppometa.unimarc.output.JsonOutputFormatter.Consumer;

public abstract class MultiThreadOutFormatter extends BaseOutFormatter{
    protected BlockingQueue<List<OutItem>> issuesQueue = new ArrayBlockingQueue<List<OutItem>>(1000);
    protected Consumer consumer = null;

}
