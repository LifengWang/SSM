/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartdata.actions.hdfs;

import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.hdfs.protocol.HdfsFileStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartdata.actions.ActionStatus;

/**
 * Check and return file blocks storage location.
 */
public class CheckStorageAction extends HdfsAction {
  private static final Logger LOG = LoggerFactory.getLogger(WriteFileAction.class);

  private String fileName;

  @Override
  public void init(String[] args) {
    super.init(args);
    fileName = args[0];
  }

  @Override
  protected void execute() {
    ActionStatus actionStatus = getActionStatus();
    actionStatus.setStartTime();
    try {
      HdfsFileStatus fileStatus = dfsClient.getFileInfo(fileName);
      long length = fileStatus.getLen();
      BlockLocation[] blockLocations = dfsClient.getBlockLocations(fileName,
          0, length);
      for (BlockLocation blockLocation : blockLocations) {
        StringBuilder hosts = new StringBuilder();
        hosts.append("{");
        for (String host : blockLocation.getHosts()) {
          hosts.append(host + " ");
        }
        hosts.append("}");
        this.resultOut.println(hosts);
      }
      actionStatus.setSuccessful(true);
    } catch (Exception e) {
      actionStatus.setSuccessful(false);
      throw new RuntimeException(e);
    } finally {
      actionStatus.setFinished(true);
    }
  }
}
