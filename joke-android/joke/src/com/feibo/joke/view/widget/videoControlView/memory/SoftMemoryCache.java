package com.feibo.joke.view.widget.videoControlView.memory;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

public class SoftMemoryCache<T> extends BaseMemoryCache<T>{

    @Override
    protected Reference<T> createReference(T value) {
        return new SoftReference<T>(value);
    }
}
