package com.suspensive.store.models.interfaces;


public interface IPrototype<T extends IPrototype<T>> extends Cloneable {
    T clone();
}
