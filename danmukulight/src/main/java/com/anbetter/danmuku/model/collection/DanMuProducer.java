package com.anbetter.danmuku.model.collection;

import android.os.Handler;
import android.os.Message;

import com.anbetter.danmuku.model.DanMuModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_ls on 2016/12/7.
 */
public class DanMuProducer {

    private DanMuConsumedPool danMuConsumedPool;

    private DanMuProducedPool danMuProducedPool;

    private ProducerHandler producerHandler;

    public DanMuProducer(DanMuProducedPool danMuProducedPool, DanMuConsumedPool danMuSharedPool) {
        this.danMuConsumedPool = danMuSharedPool;
        this.danMuProducedPool = danMuProducedPool;
    }

    public void start() {
        producerHandler = new ProducerHandler(this);
    }

    public void produce(int index, DanMuModel danMuView) {
        if (producerHandler != null) {
            ProduceMessage produceMessage = new ProduceMessage();
            produceMessage.index = index;
            produceMessage.danMuView = danMuView;
            Message message = producerHandler.obtainMessage();
            message.obj = produceMessage;
            message.what = 2;
            producerHandler.sendMessage(message);
        }
    }

    public void jumpQueue(List<DanMuModel> danMuViews) {
        danMuProducedPool.jumpQueue(danMuViews);
    }

    public void release() {
        danMuConsumedPool = null;
        if (producerHandler != null) {
            producerHandler.removeMessages(1);
            producerHandler.release();
        }
    }

    static class ProducerHandler extends Handler {

        private final int SLEEP_TIME = 100;

        private DanMuProducer danMuProducer;

        ProducerHandler(DanMuProducer danMuProducer) {
            this.danMuProducer = danMuProducer;
            obtainMessage(1).sendToTarget();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (danMuProducer != null && danMuProducer.danMuConsumedPool != null) {
                        if (danMuProducer.danMuProducedPool != null) {
                            ArrayList<DanMuModel> danMuViews = danMuProducer.danMuProducedPool.dispatch();
                            if (danMuViews != null) {
                                danMuProducer.danMuConsumedPool.put(danMuViews);
                            }
                        }
                        Message message = obtainMessage();
                        message.what = 1;
                        sendMessageDelayed(message, SLEEP_TIME);
                    }
                    break;
                case 2:
                    if (danMuProducer != null && msg.obj instanceof ProduceMessage) {
                        ProduceMessage produceMessage = (ProduceMessage) msg.obj;
                        danMuProducer.danMuProducedPool.addDanMuView(produceMessage.index, produceMessage.danMuView);
                    }
                    break;
            }
        }

        public void release() {
            if (danMuProducer != null) {
                if(danMuProducer.danMuProducedPool != null) {
                    danMuProducer.danMuProducedPool.clear();
                }
                danMuProducer = null;
            }
        }

    }

    static class ProduceMessage {
        public int index;
        public DanMuModel danMuView;
    }

}

