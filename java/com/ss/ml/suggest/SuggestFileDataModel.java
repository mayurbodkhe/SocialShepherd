package com.ss.ml.suggest;

import java.io.File;
import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;

public class SuggestFileDataModel extends FileDataModel {
      /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final ItemMemIDMigrator  memIdMigtr = new ItemMemIDMigrator();
     
      public SuggestFileDataModel(File dataFile) throws IOException {
            super(dataFile);       
      }

      public SuggestFileDataModel(File dataFile, String transpose) throws IOException {
            super(dataFile, transpose);
      }

      @Override
      protected long readItemIDFromString(String value) {
            long retValue =  memIdMigtr.toLongID(value);
            if(null == memIdMigtr.toStringID(retValue)){
                  try {
                        memIdMigtr.singleInit(value);
                  } catch (TasteException e) {
                        e.printStackTrace();
                  }
            }
            return retValue;
      }
   
      String getItemIDAsString(long itemId){
            return memIdMigtr.toStringID(itemId);
      }
}
