package com.ss.ml.suggest;


import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.AbstractIDMigrator;

      public  class ItemMemIDMigrator extends AbstractIDMigrator {
       
        private final FastByIDMap<String> longToString;
       
        public ItemMemIDMigrator() {
          this.longToString = new FastByIDMap<String>(100);
        }
       
        public void storeMapping(long longID, String stringID) {
          synchronized (longToString) {
            longToString.put(longID, stringID);
          }
        }
       
        @Override
        public String toStringID(long longID) {
          synchronized (longToString) {
            return longToString.get(longID);
          }
        }
        public void singleInit(String stringID) throws TasteException {
            storeMapping(toLongID(stringID), stringID);
        }
       
      }