/*
* HotKeyAgent.java
*
* Copyright 2000-2007 by Tangosol, Inc.  All rights reserved.
*
* This software is the confidential and proprietary information of
* Tangosol, Inc.  You shall not disclose such confidential and pro-
* prietary information and shall use it only in accordance with the
* terms of the license agreement you entered into with Tangosol, Inc.
*
* Tangosol, Inc. makes no representations or warranties about the suit-
* ability of the software, either express or implied, including but not
* limited to the implied warranties of merchantability, fitness for a
* particular purpose, or non-infringement.  Tangosol, Inc. shall not be
* liable for any damages suffered by licensee as a result of using,
* modifying or distributing this software or its derivatives.
*
* Tangosol, Inc. is located at http://www.tangosol.com and can be
* contacted by e-mail at info@tangosol.com.
*
* This notice may not be removed or altered.
*/
package com.tangosol.examples;


import com.tangosol.net.AbstractInvocable;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.DefaultConfigurableCacheFactory;
import com.tangosol.net.DistributedCacheService;
import com.tangosol.net.InvocationService;
import com.tangosol.net.cache.LocalCache;
import com.tangosol.net.cache.ReadWriteBackingMap;

import com.tangosol.util.Base;
import com.tangosol.util.Binary;
import com.tangosol.util.comparator.InverseComparator;
import com.tangosol.util.ExternalizableHelper;
import com.tangosol.util.SimpleMapEntry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import java.io.Serializable;


/**
* Agent which finds hot keys a cache.
* To use this agent the class must be in the classpath of all storage enabled
* nodes, and the nodes must be running either an Invocation or Managmenet
* service.  To find the hot keys, run this class specifying the cache name.
*
*/
public class HotKeyAgent
    extends AbstractInvocable
    {
    /**
    * Run the HotKeyAgent on all cache servers for the specified cache.
    *
    * @param asArg  cachename [maxEntries 10] [service Management]
    */
    public static void main(String[] asArg)
        {
        if (asArg.length == 0)
            {
            System.err.println("You must supply at least the cache name.");
            System.err.println("HotKeyAgent cachename [maxEntries 10] [service Management]");
            System.exit(1);
            }
        String sCache = asArg[0];
        int    cMax   = asArg.length > 1 ? Integer.parseInt(asArg[1]) : 10;
        String sSvc   = asArg.length > 2 ? asArg[2] : "Management";

        // force this process to be storage disabled
        System.setProperty("tangosol.coherence.distributed.localstorage", "false");

        // find cache servers for the specified cache
        Set setMembers = ((DistributedCacheService) CacheFactory
                .getCache(sCache).getCacheService()).getStorageEnabledMembers();

        // find specified Invocation service
        InvocationService svc = (InvocationService) CacheFactory.getCluster()
                .ensureService(sSvc, "Invocation");

        System.out.println("Using " + sSvc + " service to find top "
                + cMax + " accessed entries from each of " + setMembers.size()
                + " cache servers");

        String sResult = Result.toString(svc.query(new HotKeyAgent(sCache, cMax), setMembers));

        System.out.println(sResult);
        }


    // ---- Constructor(s) --------------------------------------------------

    /**
    * Construct an agent to collect the most frequently used entries from all
    * cache servers.
    *
    * @param sCacheName  the cache to operate on
    * @param cMax        the maximum number of entries to return per server
    */
    public HotKeyAgent(String sCacheName, int cMax)
        {
        m_sCacheName  = sCacheName;
        m_cMaxEntries = cMax;
        }


    // ---- Invocable interface----------------------------------------------

    /**
    * {@inheritDoc}
    */
    public void run()
        {
        String sCacheName = m_sCacheName;
        Map mapLocal = ((DefaultConfigurableCacheFactory.Manager)
                CacheFactory.getCache(sCacheName).getCacheService().
                        getBackingMapManager()).getBackingMap(sCacheName);
        if (mapLocal instanceof ReadWriteBackingMap)
            {
            mapLocal = ((ReadWriteBackingMap) mapLocal).getInternalCache();
            }

        if (!(mapLocal instanceof LocalCache))
            {
            throw new RuntimeException("Backing map must be a LocalCache");
            }

        // count total number of touches, and track the top entries
        SortedSet setTop      = new TreeSet(new InverseComparator(new TouchComparator()));
        long      cTouches    = mapLocal.size(); // touch count is 0 based
        int       cMaxEntries = m_cMaxEntries;
        int       cMinTouch   = Integer.MAX_VALUE;
        for (Iterator iter = mapLocal.entrySet().iterator(); iter.hasNext(); )
            {
            LocalCache.Entry entry  = (LocalCache.Entry) iter.next();
            int              cTouch = entry.getTouchCount();

            if (setTop.size() < cMaxEntries)
                {
                // always add until we have at least N results
                setTop.add(entry);
                if (cTouch < cMinTouch)
                    {
                    cMinTouch = cTouch;
                    }
                }
            else if (cTouch > cMinTouch)
                {
                // we have at least N entries, but this one is bigger than
                // our smallest
                setTop.remove(setTop.last());
                setTop.add(entry);
                cMinTouch = ((LocalCache.Entry) setTop.last()).getTouchCount();
                }

            cTouches += cTouch;
            }

        // copy the relevant data into a seralizable structure
        List listTop = new ArrayList(cMaxEntries);
        for (Iterator iter = setTop.iterator(); iter.hasNext(); )
            {
            LocalCache.Entry entry = (LocalCache.Entry) iter.next();
            listTop.add(new SimpleMapEntry(entry.getKey(),
                    Base.makeInteger(entry.getTouchCount() + 1)));
            }

        setResult(new Result(cTouches, listTop));
        }


    // ---- inner class: TouchComparator ------------------------------------

    /**
    * Comparator which compares LocalCache Entrys based on their touch count.
    */
    public static class TouchComparator
        implements Comparator
        {
        /**
        * {@inheritDoc}
        */
        public int compare(Object o1, Object o2)
            {
            LocalCache.Entry e1 = (LocalCache.Entry) o1;
            LocalCache.Entry e2 = (LocalCache.Entry) o2;
            int              c1 = e1.getTouchCount();
            int              c2 = e2.getTouchCount();

            // for equal touch count, use key comparison as we want to allow
            // entries with the same touch count to appear in the result set
            return c1 == c2 ? ((Binary) e1.getKey()).compareTo(e2.getKey())
                            : c1 - c2;
            }
        }


    // ---- inner class: Result ---------------------------------------------

    /**
    * Result of the HotKey check for each cache server.
    */
    public static class Result
           implements Serializable
        {
        /**
        * Constuct a result for a single cache server.
        *
        * @param cTouches  the total number of cache touches for this server
        * @param listTop   the most frequently used entries
        */
        public Result(long cTouches, List listTop)
            {
            m_cTouches = cTouches;
            m_listTop  = listTop;
            }

        /**
        * {@inheritDoc}
        */
        public String toString()
            {
            StringBuffer sbResult = new StringBuffer();
            long         cTouches = m_cTouches;
            for (Iterator iter = m_listTop.iterator(); iter.hasNext(); )
                {
                Map.Entry entry = (Map.Entry) iter.next();
                long      lPct  = ((Integer) entry.getValue()).intValue() * 10000L / cTouches;
                sbResult.append("Key '")
                        .append(ExternalizableHelper.fromBinary((Binary) entry.getKey()))
                        .append("' accounted for ")
                        .append(lPct / 100F)
                        .append("% of cache access for server\n");
                }
            return sbResult.toString();
            }

        /**
        * Return a string describing a set of Results.
        *
        * @param mapResults  map of results, key Member, value Result
        *
        * @return a string describing a set of Results
        */
        public static String toString(Map mapResults)
            {
            long cTouchesAll = 0;
            for (Iterator iter = mapResults.values().iterator(); iter.hasNext(); )
                {
                cTouchesAll += ((Result) iter.next()).m_cTouches;
                }

            StringBuffer sbResult = new StringBuffer();
            for (Iterator iter = mapResults.entrySet().iterator(); iter.hasNext(); )
                {
                Map.Entry entry  = (Map.Entry) iter.next();
                Result    result = (Result) entry.getValue();
                long      lPct   = result.m_cTouches * 10000L / cTouchesAll;
                sbResult.append("\nCacheServer ")
                        .append(entry.getKey())
                        .append(" handled ")
                        .append(lPct / 100F)
                        .append("% of total cache access\n")
                        .append(result);
                }
            return sbResult.toString();
            }

        // ---- data fields ---------------------------------------------

        /**
        * The total number of cache touches for the server.
        */
        public long m_cTouches;

        /**
        * The most frequently touched entries.
        */
        public List m_listTop;
        }


    // ---- data fields -----------------------------------------------------

    /**
    * The cache to scan for hot keys.
    */
    protected String m_sCacheName;

    /**
    * The maximum number of keys to report per server.
    */
    protected int m_cMaxEntries;
    }