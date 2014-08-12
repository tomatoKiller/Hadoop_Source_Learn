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

package org.apache.hadoop.yarn.api.records;

import java.text.NumberFormat;

import org.apache.hadoop.classification.InterfaceAudience.Private;
import org.apache.hadoop.classification.InterfaceAudience.Public;
import org.apache.hadoop.classification.InterfaceStability.Stable;
import org.apache.hadoop.classification.InterfaceStability.Unstable;
import org.apache.hadoop.yarn.util.Records;

/**
 * <p><code>ContainerId</code> represents a globally unique identifier
 * for a {@link Container} in the cluster.</p>
 */
@Public
@Stable
public abstract class ContainerId implements Comparable<ContainerId>{

  @Private
  @Unstable
  public static ContainerId newInstance(ApplicationAttemptId appAttemptId,
      int containerId) {
    ContainerId id = Records.newRecord(ContainerId.class);
    id.setId(containerId);
    id.setApplicationAttemptId(appAttemptId);
    id.build();
    return id;
  }

  /**
   * Get the <code>ApplicationAttemptId</code> of the application to which
   * the <code>Container</code> was assigned.
   * @return <code>ApplicationAttemptId</code> of the application to which
   *         the <code>Container</code> was assigned
   */
  @Public
  @Stable
  public abstract ApplicationAttemptId getApplicationAttemptId();
  
  @Private
  @Unstable
  protected abstract void setApplicationAttemptId(ApplicationAttemptId atId);

  /**
   * Get the identifier of the <code>ContainerId</code>.
   * @return identifier of the <code>ContainerId</code>
   */
  @Public
  @Stable
  public abstract int getId();

  @Private
  @Unstable
  protected abstract void setId(int id);
 
  
  // TODO: fail the app submission if attempts are more than 10 or something
  private static final ThreadLocal<NumberFormat> appAttemptIdFormat =
      new ThreadLocal<NumberFormat>() {
        @Override
        public NumberFormat initialValue() {
          NumberFormat fmt = NumberFormat.getInstance();
          fmt.setGroupingUsed(false);
          fmt.setMinimumIntegerDigits(2);
          return fmt;
        }
      };
  // TODO: Why thread local?
  // ^ NumberFormat instances are not threadsafe
  private static final ThreadLocal<NumberFormat> containerIdFormat =
      new ThreadLocal<NumberFormat>() {
        @Override
        public NumberFormat initialValue() {
          NumberFormat fmt = NumberFormat.getInstance();
          fmt.setGroupingUsed(false);
          fmt.setMinimumIntegerDigits(6);
          return fmt;
        }
      };
  
  @Override
  public int hashCode() {
    // Generated by eclipse.
    final int prime = 435569;
    int result = 7507;
    result = prime * result + getId();
    result = prime * result + getApplicationAttemptId().hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ContainerId other = (ContainerId) obj;
    if (!this.getApplicationAttemptId().equals(other.getApplicationAttemptId()))
      return false;
    if (this.getId() != other.getId())
      return false;
    return true;
  }

  @Override
  public int compareTo(ContainerId other) {
    if (this.getApplicationAttemptId().compareTo(
        other.getApplicationAttemptId()) == 0) {
      return this.getId() - other.getId();
    } else {
      return this.getApplicationAttemptId().compareTo(
          other.getApplicationAttemptId());
    }
    
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("container_");
    ApplicationId appId = getApplicationAttemptId().getApplicationId();
    sb.append(appId.getClusterTimestamp()).append("_");
    sb.append(ApplicationId.appIdFormat.get().format(appId.getId()))
        .append("_");
    sb.append(
        appAttemptIdFormat.get().format(
            getApplicationAttemptId().getAttemptId())).append("_");
    sb.append(containerIdFormat.get().format(getId()));
    return sb.toString();
  }

  protected abstract void build();
}
