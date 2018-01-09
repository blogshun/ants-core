package com.ants.plugin.tool;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author MrShun
 * @version 1.0
 */
public class ConcurrentToolMap {

    public static final ConcurrentMap<String, Object> PLUGINS = new ConcurrentHashMap<>();
}
