/**
 * Copyright (C) 2015  the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package mjava.op.record;

import edu.ecnu.sqslab.mjava.MutationSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * <p>Description: Contrast log before and after mutation </p>
 * @author Jian Liu
 * @version 1.0
  */

public class CodeChangeLog {
  private static final Logger logger = LoggerFactory.getLogger(CodeChangeLog.class);
  static final String logFile_name = "mutation_log";
  static PrintWriter log_writer;
  
  public static void openLogFile(){
    try{
      File f = new File(MutationSystem.MUTANT_PATH,logFile_name);
      FileWriter fout = new FileWriter(f);
      log_writer = new PrintWriter(fout);
    }catch(IOException e){
      System.err.println("[IOException] Can't make mutant log file." + e);
      logger.error("[IOException] Can't make mutant log file." + e);
    }
  }

  public static void writeLog(String str){
    log_writer.println(str);
  }

  public static void closeLogFile(){
    log_writer.flush();
    log_writer.close();
  }
}
