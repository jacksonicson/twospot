/*******************************************************************************
 * Copyright (c) 2010 Andreas Wolke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Wolke - initial API and implementation and initial documentation
 *******************************************************************************/
// source: management.proto

package org.prot.util.managment.generated;

public final class ManagementData {
  private ManagementData() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }
  public static final class Controller extends
      com.google.protobuf.GeneratedMessageLite {
    // Use Controller.newBuilder() to construct.
    private Controller() {
      initFields();
    }
    private Controller(boolean noInit) {}
    
    private static final Controller defaultInstance;
    public static Controller getDefaultInstance() {
      return defaultInstance;
    }
    
    public Controller getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    // required string address = 1;
    public static final int ADDRESS_FIELD_NUMBER = 1;
    private boolean hasAddress;
    private java.lang.String address_ = "";
    public boolean hasAddress() { return hasAddress; }
    public java.lang.String getAddress() { return address_; }
    
    // required uint32 runningApps = 11;
    public static final int RUNNINGAPPS_FIELD_NUMBER = 11;
    private boolean hasRunningApps;
    private int runningApps_ = 0;
    public boolean hasRunningApps() { return hasRunningApps; }
    public int getRunningApps() { return runningApps_; }
    
    // required double cpu = 12;
    public static final int CPU_FIELD_NUMBER = 12;
    private boolean hasCpu;
    private double cpu_ = 0D;
    public boolean hasCpu() { return hasCpu; }
    public double getCpu() { return cpu_; }
    
    // required double procCpu = 17;
    public static final int PROCCPU_FIELD_NUMBER = 17;
    private boolean hasProcCpu;
    private double procCpu_ = 0D;
    public boolean hasProcCpu() { return hasProcCpu; }
    public double getProcCpu() { return procCpu_; }
    
    // required double idleCpu = 18;
    public static final int IDLECPU_FIELD_NUMBER = 18;
    private boolean hasIdleCpu;
    private double idleCpu_ = 0D;
    public boolean hasIdleCpu() { return hasIdleCpu; }
    public double getIdleCpu() { return idleCpu_; }
    
    // required int64 totalMem = 15;
    public static final int TOTALMEM_FIELD_NUMBER = 15;
    private boolean hasTotalMem;
    private long totalMem_ = 0L;
    public boolean hasTotalMem() { return hasTotalMem; }
    public long getTotalMem() { return totalMem_; }
    
    // required int64 freeMem = 14;
    public static final int FREEMEM_FIELD_NUMBER = 14;
    private boolean hasFreeMem;
    private long freeMem_ = 0L;
    public boolean hasFreeMem() { return hasFreeMem; }
    public long getFreeMem() { return freeMem_; }
    
    // required float rps = 13;
    public static final int RPS_FIELD_NUMBER = 13;
    private boolean hasRps;
    private float rps_ = 0F;
    public boolean hasRps() { return hasRps; }
    public float getRps() { return rps_; }
    
    // repeated .test.AppServer appServers = 20;
    public static final int APPSERVERS_FIELD_NUMBER = 20;
    private java.util.List<org.prot.util.managment.generated.ManagementData.AppServer> appServers_ =
      java.util.Collections.emptyList();
    public java.util.List<org.prot.util.managment.generated.ManagementData.AppServer> getAppServersList() {
      return appServers_;
    }
    public int getAppServersCount() { return appServers_.size(); }
    public org.prot.util.managment.generated.ManagementData.AppServer getAppServers(int index) {
      return appServers_.get(index);
    }
    
    private void initFields() {
    }
    public final boolean isInitialized() {
      if (!hasAddress) return false;
      if (!hasRunningApps) return false;
      if (!hasCpu) return false;
      if (!hasProcCpu) return false;
      if (!hasIdleCpu) return false;
      if (!hasTotalMem) return false;
      if (!hasFreeMem) return false;
      if (!hasRps) return false;
      for (org.prot.util.managment.generated.ManagementData.AppServer element : getAppServersList()) {
        if (!element.isInitialized()) return false;
      }
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (hasAddress()) {
        output.writeString(1, getAddress());
      }
      if (hasRunningApps()) {
        output.writeUInt32(11, getRunningApps());
      }
      if (hasCpu()) {
        output.writeDouble(12, getCpu());
      }
      if (hasRps()) {
        output.writeFloat(13, getRps());
      }
      if (hasFreeMem()) {
        output.writeInt64(14, getFreeMem());
      }
      if (hasTotalMem()) {
        output.writeInt64(15, getTotalMem());
      }
      if (hasProcCpu()) {
        output.writeDouble(17, getProcCpu());
      }
      if (hasIdleCpu()) {
        output.writeDouble(18, getIdleCpu());
      }
      for (org.prot.util.managment.generated.ManagementData.AppServer element : getAppServersList()) {
        output.writeMessage(20, element);
      }
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (hasAddress()) {
        size += com.google.protobuf.CodedOutputStream
          .computeStringSize(1, getAddress());
      }
      if (hasRunningApps()) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(11, getRunningApps());
      }
      if (hasCpu()) {
        size += com.google.protobuf.CodedOutputStream
          .computeDoubleSize(12, getCpu());
      }
      if (hasRps()) {
        size += com.google.protobuf.CodedOutputStream
          .computeFloatSize(13, getRps());
      }
      if (hasFreeMem()) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(14, getFreeMem());
      }
      if (hasTotalMem()) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(15, getTotalMem());
      }
      if (hasProcCpu()) {
        size += com.google.protobuf.CodedOutputStream
          .computeDoubleSize(17, getProcCpu());
      }
      if (hasIdleCpu()) {
        size += com.google.protobuf.CodedOutputStream
          .computeDoubleSize(18, getIdleCpu());
      }
      for (org.prot.util.managment.generated.ManagementData.AppServer element : getAppServersList()) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(20, element);
      }
      memoizedSerializedSize = size;
      return size;
    }
    
    public static org.prot.util.managment.generated.ManagementData.Controller parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.prot.util.managment.generated.ManagementData.Controller parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.prot.util.managment.generated.ManagementData.Controller parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.prot.util.managment.generated.ManagementData.Controller parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.prot.util.managment.generated.ManagementData.Controller parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.prot.util.managment.generated.ManagementData.Controller parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static org.prot.util.managment.generated.ManagementData.Controller parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static org.prot.util.managment.generated.ManagementData.Controller parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static org.prot.util.managment.generated.ManagementData.Controller parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.prot.util.managment.generated.ManagementData.Controller parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(org.prot.util.managment.generated.ManagementData.Controller prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageLite.Builder<
          org.prot.util.managment.generated.ManagementData.Controller, Builder> {
      private org.prot.util.managment.generated.ManagementData.Controller result;
      
      // Construct using org.prot.util.managment.gen.ManagementData.Controller.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new org.prot.util.managment.generated.ManagementData.Controller();
        return builder;
      }
      
      protected org.prot.util.managment.generated.ManagementData.Controller internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new org.prot.util.managment.generated.ManagementData.Controller();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public org.prot.util.managment.generated.ManagementData.Controller getDefaultInstanceForType() {
        return org.prot.util.managment.generated.ManagementData.Controller.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public org.prot.util.managment.generated.ManagementData.Controller build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private org.prot.util.managment.generated.ManagementData.Controller buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public org.prot.util.managment.generated.ManagementData.Controller buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        if (result.appServers_ != java.util.Collections.EMPTY_LIST) {
          result.appServers_ =
            java.util.Collections.unmodifiableList(result.appServers_);
        }
        org.prot.util.managment.generated.ManagementData.Controller returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(org.prot.util.managment.generated.ManagementData.Controller other) {
        if (other == org.prot.util.managment.generated.ManagementData.Controller.getDefaultInstance()) return this;
        if (other.hasAddress()) {
          setAddress(other.getAddress());
        }
        if (other.hasRunningApps()) {
          setRunningApps(other.getRunningApps());
        }
        if (other.hasCpu()) {
          setCpu(other.getCpu());
        }
        if (other.hasProcCpu()) {
          setProcCpu(other.getProcCpu());
        }
        if (other.hasIdleCpu()) {
          setIdleCpu(other.getIdleCpu());
        }
        if (other.hasTotalMem()) {
          setTotalMem(other.getTotalMem());
        }
        if (other.hasFreeMem()) {
          setFreeMem(other.getFreeMem());
        }
        if (other.hasRps()) {
          setRps(other.getRps());
        }
        if (!other.appServers_.isEmpty()) {
          if (result.appServers_.isEmpty()) {
            result.appServers_ = new java.util.ArrayList<org.prot.util.managment.generated.ManagementData.AppServer>();
          }
          result.appServers_.addAll(other.appServers_);
        }
        return this;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              return this;
            default: {
              if (!parseUnknownField(input, extensionRegistry, tag)) {
                return this;
              }
              break;
            }
            case 10: {
              setAddress(input.readString());
              break;
            }
            case 88: {
              setRunningApps(input.readUInt32());
              break;
            }
            case 97: {
              setCpu(input.readDouble());
              break;
            }
            case 109: {
              setRps(input.readFloat());
              break;
            }
            case 112: {
              setFreeMem(input.readInt64());
              break;
            }
            case 120: {
              setTotalMem(input.readInt64());
              break;
            }
            case 137: {
              setProcCpu(input.readDouble());
              break;
            }
            case 145: {
              setIdleCpu(input.readDouble());
              break;
            }
            case 162: {
              org.prot.util.managment.generated.ManagementData.AppServer.Builder subBuilder = org.prot.util.managment.generated.ManagementData.AppServer.newBuilder();
              input.readMessage(subBuilder, extensionRegistry);
              addAppServers(subBuilder.buildPartial());
              break;
            }
          }
        }
      }
      
      
      // required string address = 1;
      public boolean hasAddress() {
        return result.hasAddress();
      }
      public java.lang.String getAddress() {
        return result.getAddress();
      }
      public Builder setAddress(java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  result.hasAddress = true;
        result.address_ = value;
        return this;
      }
      public Builder clearAddress() {
        result.hasAddress = false;
        result.address_ = getDefaultInstance().getAddress();
        return this;
      }
      
      // required uint32 runningApps = 11;
      public boolean hasRunningApps() {
        return result.hasRunningApps();
      }
      public int getRunningApps() {
        return result.getRunningApps();
      }
      public Builder setRunningApps(int value) {
        result.hasRunningApps = true;
        result.runningApps_ = value;
        return this;
      }
      public Builder clearRunningApps() {
        result.hasRunningApps = false;
        result.runningApps_ = 0;
        return this;
      }
      
      // required double cpu = 12;
      public boolean hasCpu() {
        return result.hasCpu();
      }
      public double getCpu() {
        return result.getCpu();
      }
      public Builder setCpu(double value) {
        result.hasCpu = true;
        result.cpu_ = value;
        return this;
      }
      public Builder clearCpu() {
        result.hasCpu = false;
        result.cpu_ = 0D;
        return this;
      }
      
      // required double procCpu = 17;
      public boolean hasProcCpu() {
        return result.hasProcCpu();
      }
      public double getProcCpu() {
        return result.getProcCpu();
      }
      public Builder setProcCpu(double value) {
        result.hasProcCpu = true;
        result.procCpu_ = value;
        return this;
      }
      public Builder clearProcCpu() {
        result.hasProcCpu = false;
        result.procCpu_ = 0D;
        return this;
      }
      
      // required double idleCpu = 18;
      public boolean hasIdleCpu() {
        return result.hasIdleCpu();
      }
      public double getIdleCpu() {
        return result.getIdleCpu();
      }
      public Builder setIdleCpu(double value) {
        result.hasIdleCpu = true;
        result.idleCpu_ = value;
        return this;
      }
      public Builder clearIdleCpu() {
        result.hasIdleCpu = false;
        result.idleCpu_ = 0D;
        return this;
      }
      
      // required int64 totalMem = 15;
      public boolean hasTotalMem() {
        return result.hasTotalMem();
      }
      public long getTotalMem() {
        return result.getTotalMem();
      }
      public Builder setTotalMem(long value) {
        result.hasTotalMem = true;
        result.totalMem_ = value;
        return this;
      }
      public Builder clearTotalMem() {
        result.hasTotalMem = false;
        result.totalMem_ = 0L;
        return this;
      }
      
      // required int64 freeMem = 14;
      public boolean hasFreeMem() {
        return result.hasFreeMem();
      }
      public long getFreeMem() {
        return result.getFreeMem();
      }
      public Builder setFreeMem(long value) {
        result.hasFreeMem = true;
        result.freeMem_ = value;
        return this;
      }
      public Builder clearFreeMem() {
        result.hasFreeMem = false;
        result.freeMem_ = 0L;
        return this;
      }
      
      // required float rps = 13;
      public boolean hasRps() {
        return result.hasRps();
      }
      public float getRps() {
        return result.getRps();
      }
      public Builder setRps(float value) {
        result.hasRps = true;
        result.rps_ = value;
        return this;
      }
      public Builder clearRps() {
        result.hasRps = false;
        result.rps_ = 0F;
        return this;
      }
      
      // repeated .test.AppServer appServers = 20;
      public java.util.List<org.prot.util.managment.generated.ManagementData.AppServer> getAppServersList() {
        return java.util.Collections.unmodifiableList(result.appServers_);
      }
      public int getAppServersCount() {
        return result.getAppServersCount();
      }
      public org.prot.util.managment.generated.ManagementData.AppServer getAppServers(int index) {
        return result.getAppServers(index);
      }
      public Builder setAppServers(int index, org.prot.util.managment.generated.ManagementData.AppServer value) {
        if (value == null) {
          throw new NullPointerException();
        }
        result.appServers_.set(index, value);
        return this;
      }
      public Builder setAppServers(int index, org.prot.util.managment.generated.ManagementData.AppServer.Builder builderForValue) {
        result.appServers_.set(index, builderForValue.build());
        return this;
      }
      public Builder addAppServers(org.prot.util.managment.generated.ManagementData.AppServer value) {
        if (value == null) {
          throw new NullPointerException();
        }
        if (result.appServers_.isEmpty()) {
          result.appServers_ = new java.util.ArrayList<org.prot.util.managment.generated.ManagementData.AppServer>();
        }
        result.appServers_.add(value);
        return this;
      }
      public Builder addAppServers(org.prot.util.managment.generated.ManagementData.AppServer.Builder builderForValue) {
        if (result.appServers_.isEmpty()) {
          result.appServers_ = new java.util.ArrayList<org.prot.util.managment.generated.ManagementData.AppServer>();
        }
        result.appServers_.add(builderForValue.build());
        return this;
      }
      public Builder addAllAppServers(
          java.lang.Iterable<? extends org.prot.util.managment.generated.ManagementData.AppServer> values) {
        if (result.appServers_.isEmpty()) {
          result.appServers_ = new java.util.ArrayList<org.prot.util.managment.generated.ManagementData.AppServer>();
        }
        super.addAll(values, result.appServers_);
        return this;
      }
      public Builder clearAppServers() {
        result.appServers_ = java.util.Collections.emptyList();
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:test.Controller)
    }
    
    static {
      defaultInstance = new Controller(true);
      org.prot.util.managment.generated.ManagementData.internalForceInit();
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:test.Controller)
  }
  
  public static final class Test extends
      com.google.protobuf.GeneratedMessageLite {
    // Use Test.newBuilder() to construct.
    private Test() {
      initFields();
    }
    private Test(boolean noInit) {}
    
    private static final Test defaultInstance;
    public static Test getDefaultInstance() {
      return defaultInstance;
    }
    
    public Test getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    // required .test.Test test = 1;
    public static final int TEST_FIELD_NUMBER = 1;
    private boolean hasTest;
    private org.prot.util.managment.generated.ManagementData.Test test_;
    public boolean hasTest() { return hasTest; }
    public org.prot.util.managment.generated.ManagementData.Test getTest() { return test_; }
    
    private void initFields() {
      test_ = org.prot.util.managment.generated.ManagementData.Test.getDefaultInstance();
    }
    public final boolean isInitialized() {
      if (!hasTest) return false;
      if (!getTest().isInitialized()) return false;
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (hasTest()) {
        output.writeMessage(1, getTest());
      }
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (hasTest()) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(1, getTest());
      }
      memoizedSerializedSize = size;
      return size;
    }
    
    public static org.prot.util.managment.generated.ManagementData.Test parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.prot.util.managment.generated.ManagementData.Test parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.prot.util.managment.generated.ManagementData.Test parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.prot.util.managment.generated.ManagementData.Test parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.prot.util.managment.generated.ManagementData.Test parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.prot.util.managment.generated.ManagementData.Test parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static org.prot.util.managment.generated.ManagementData.Test parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static org.prot.util.managment.generated.ManagementData.Test parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static org.prot.util.managment.generated.ManagementData.Test parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.prot.util.managment.generated.ManagementData.Test parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(org.prot.util.managment.generated.ManagementData.Test prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageLite.Builder<
          org.prot.util.managment.generated.ManagementData.Test, Builder> {
      private org.prot.util.managment.generated.ManagementData.Test result;
      
      // Construct using org.prot.util.managment.gen.ManagementData.Test.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new org.prot.util.managment.generated.ManagementData.Test();
        return builder;
      }
      
      protected org.prot.util.managment.generated.ManagementData.Test internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new org.prot.util.managment.generated.ManagementData.Test();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public org.prot.util.managment.generated.ManagementData.Test getDefaultInstanceForType() {
        return org.prot.util.managment.generated.ManagementData.Test.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public org.prot.util.managment.generated.ManagementData.Test build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private org.prot.util.managment.generated.ManagementData.Test buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public org.prot.util.managment.generated.ManagementData.Test buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        org.prot.util.managment.generated.ManagementData.Test returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(org.prot.util.managment.generated.ManagementData.Test other) {
        if (other == org.prot.util.managment.generated.ManagementData.Test.getDefaultInstance()) return this;
        if (other.hasTest()) {
          mergeTest(other.getTest());
        }
        return this;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              return this;
            default: {
              if (!parseUnknownField(input, extensionRegistry, tag)) {
                return this;
              }
              break;
            }
            case 10: {
              org.prot.util.managment.generated.ManagementData.Test.Builder subBuilder = org.prot.util.managment.generated.ManagementData.Test.newBuilder();
              if (hasTest()) {
                subBuilder.mergeFrom(getTest());
              }
              input.readMessage(subBuilder, extensionRegistry);
              setTest(subBuilder.buildPartial());
              break;
            }
          }
        }
      }
      
      
      // required .test.Test test = 1;
      public boolean hasTest() {
        return result.hasTest();
      }
      public org.prot.util.managment.generated.ManagementData.Test getTest() {
        return result.getTest();
      }
      public Builder setTest(org.prot.util.managment.generated.ManagementData.Test value) {
        if (value == null) {
          throw new NullPointerException();
        }
        result.hasTest = true;
        result.test_ = value;
        return this;
      }
      public Builder setTest(org.prot.util.managment.generated.ManagementData.Test.Builder builderForValue) {
        result.hasTest = true;
        result.test_ = builderForValue.build();
        return this;
      }
      public Builder mergeTest(org.prot.util.managment.generated.ManagementData.Test value) {
        if (result.hasTest() &&
            result.test_ != org.prot.util.managment.generated.ManagementData.Test.getDefaultInstance()) {
          result.test_ =
            org.prot.util.managment.generated.ManagementData.Test.newBuilder(result.test_).mergeFrom(value).buildPartial();
        } else {
          result.test_ = value;
        }
        result.hasTest = true;
        return this;
      }
      public Builder clearTest() {
        result.hasTest = false;
        result.test_ = org.prot.util.managment.generated.ManagementData.Test.getDefaultInstance();
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:test.Test)
    }
    
    static {
      defaultInstance = new Test(true);
      org.prot.util.managment.generated.ManagementData.internalForceInit();
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:test.Test)
  }
  
  public static final class AppServer extends
      com.google.protobuf.GeneratedMessageLite {
    // Use AppServer.newBuilder() to construct.
    private AppServer() {
      initFields();
    }
    private AppServer(boolean noInit) {}
    
    private static final AppServer defaultInstance;
    public static AppServer getDefaultInstance() {
      return defaultInstance;
    }
    
    public AppServer getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    // required string appId = 1;
    public static final int APPID_FIELD_NUMBER = 1;
    private boolean hasAppId;
    private java.lang.String appId_ = "";
    public boolean hasAppId() { return hasAppId; }
    public java.lang.String getAppId() { return appId_; }
    
    // required int64 runtime = 12;
    public static final int RUNTIME_FIELD_NUMBER = 12;
    private boolean hasRuntime;
    private long runtime_ = 0L;
    public boolean hasRuntime() { return hasRuntime; }
    public long getRuntime() { return runtime_; }
    
    // required double procCpu = 3;
    public static final int PROCCPU_FIELD_NUMBER = 3;
    private boolean hasProcCpu;
    private double procCpu_ = 0D;
    public boolean hasProcCpu() { return hasProcCpu; }
    public double getProcCpu() { return procCpu_; }
    
    // required int64 cpuTotal = 19;
    public static final int CPUTOTAL_FIELD_NUMBER = 19;
    private boolean hasCpuTotal;
    private long cpuTotal_ = 0L;
    public boolean hasCpuTotal() { return hasCpuTotal; }
    public long getCpuTotal() { return cpuTotal_; }
    
    // required int64 cpuProcTotal = 21;
    public static final int CPUPROCTOTAL_FIELD_NUMBER = 21;
    private boolean hasCpuProcTotal;
    private long cpuProcTotal_ = 0L;
    public boolean hasCpuProcTotal() { return hasCpuProcTotal; }
    public long getCpuProcTotal() { return cpuProcTotal_; }
    
    // required float rps = 10;
    public static final int RPS_FIELD_NUMBER = 10;
    private boolean hasRps;
    private float rps_ = 0F;
    public boolean hasRps() { return hasRps; }
    public float getRps() { return rps_; }
    
    // required float averageDelay = 13;
    public static final int AVERAGEDELAY_FIELD_NUMBER = 13;
    private boolean hasAverageDelay;
    private float averageDelay_ = 0F;
    public boolean hasAverageDelay() { return hasAverageDelay; }
    public float getAverageDelay() { return averageDelay_; }
    
    // required bool overloaded = 11;
    public static final int OVERLOADED_FIELD_NUMBER = 11;
    private boolean hasOverloaded;
    private boolean overloaded_ = false;
    public boolean hasOverloaded() { return hasOverloaded; }
    public boolean getOverloaded() { return overloaded_; }
    
    private void initFields() {
    }
    public final boolean isInitialized() {
      if (!hasAppId) return false;
      if (!hasRuntime) return false;
      if (!hasProcCpu) return false;
      if (!hasCpuTotal) return false;
      if (!hasCpuProcTotal) return false;
      if (!hasRps) return false;
      if (!hasAverageDelay) return false;
      if (!hasOverloaded) return false;
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (hasAppId()) {
        output.writeString(1, getAppId());
      }
      if (hasProcCpu()) {
        output.writeDouble(3, getProcCpu());
      }
      if (hasRps()) {
        output.writeFloat(10, getRps());
      }
      if (hasOverloaded()) {
        output.writeBool(11, getOverloaded());
      }
      if (hasRuntime()) {
        output.writeInt64(12, getRuntime());
      }
      if (hasAverageDelay()) {
        output.writeFloat(13, getAverageDelay());
      }
      if (hasCpuTotal()) {
        output.writeInt64(19, getCpuTotal());
      }
      if (hasCpuProcTotal()) {
        output.writeInt64(21, getCpuProcTotal());
      }
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (hasAppId()) {
        size += com.google.protobuf.CodedOutputStream
          .computeStringSize(1, getAppId());
      }
      if (hasProcCpu()) {
        size += com.google.protobuf.CodedOutputStream
          .computeDoubleSize(3, getProcCpu());
      }
      if (hasRps()) {
        size += com.google.protobuf.CodedOutputStream
          .computeFloatSize(10, getRps());
      }
      if (hasOverloaded()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(11, getOverloaded());
      }
      if (hasRuntime()) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(12, getRuntime());
      }
      if (hasAverageDelay()) {
        size += com.google.protobuf.CodedOutputStream
          .computeFloatSize(13, getAverageDelay());
      }
      if (hasCpuTotal()) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(19, getCpuTotal());
      }
      if (hasCpuProcTotal()) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(21, getCpuProcTotal());
      }
      memoizedSerializedSize = size;
      return size;
    }
    
    public static org.prot.util.managment.generated.ManagementData.AppServer parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.prot.util.managment.generated.ManagementData.AppServer parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.prot.util.managment.generated.ManagementData.AppServer parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.prot.util.managment.generated.ManagementData.AppServer parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.prot.util.managment.generated.ManagementData.AppServer parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.prot.util.managment.generated.ManagementData.AppServer parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static org.prot.util.managment.generated.ManagementData.AppServer parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static org.prot.util.managment.generated.ManagementData.AppServer parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static org.prot.util.managment.generated.ManagementData.AppServer parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.prot.util.managment.generated.ManagementData.AppServer parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(org.prot.util.managment.generated.ManagementData.AppServer prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageLite.Builder<
          org.prot.util.managment.generated.ManagementData.AppServer, Builder> {
      private org.prot.util.managment.generated.ManagementData.AppServer result;
      
      // Construct using org.prot.util.managment.gen.ManagementData.AppServer.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new org.prot.util.managment.generated.ManagementData.AppServer();
        return builder;
      }
      
      protected org.prot.util.managment.generated.ManagementData.AppServer internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new org.prot.util.managment.generated.ManagementData.AppServer();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public org.prot.util.managment.generated.ManagementData.AppServer getDefaultInstanceForType() {
        return org.prot.util.managment.generated.ManagementData.AppServer.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public org.prot.util.managment.generated.ManagementData.AppServer build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private org.prot.util.managment.generated.ManagementData.AppServer buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public org.prot.util.managment.generated.ManagementData.AppServer buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        org.prot.util.managment.generated.ManagementData.AppServer returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(org.prot.util.managment.generated.ManagementData.AppServer other) {
        if (other == org.prot.util.managment.generated.ManagementData.AppServer.getDefaultInstance()) return this;
        if (other.hasAppId()) {
          setAppId(other.getAppId());
        }
        if (other.hasRuntime()) {
          setRuntime(other.getRuntime());
        }
        if (other.hasProcCpu()) {
          setProcCpu(other.getProcCpu());
        }
        if (other.hasCpuTotal()) {
          setCpuTotal(other.getCpuTotal());
        }
        if (other.hasCpuProcTotal()) {
          setCpuProcTotal(other.getCpuProcTotal());
        }
        if (other.hasRps()) {
          setRps(other.getRps());
        }
        if (other.hasAverageDelay()) {
          setAverageDelay(other.getAverageDelay());
        }
        if (other.hasOverloaded()) {
          setOverloaded(other.getOverloaded());
        }
        return this;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              return this;
            default: {
              if (!parseUnknownField(input, extensionRegistry, tag)) {
                return this;
              }
              break;
            }
            case 10: {
              setAppId(input.readString());
              break;
            }
            case 25: {
              setProcCpu(input.readDouble());
              break;
            }
            case 85: {
              setRps(input.readFloat());
              break;
            }
            case 88: {
              setOverloaded(input.readBool());
              break;
            }
            case 96: {
              setRuntime(input.readInt64());
              break;
            }
            case 109: {
              setAverageDelay(input.readFloat());
              break;
            }
            case 152: {
              setCpuTotal(input.readInt64());
              break;
            }
            case 168: {
              setCpuProcTotal(input.readInt64());
              break;
            }
          }
        }
      }
      
      
      // required string appId = 1;
      public boolean hasAppId() {
        return result.hasAppId();
      }
      public java.lang.String getAppId() {
        return result.getAppId();
      }
      public Builder setAppId(java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  result.hasAppId = true;
        result.appId_ = value;
        return this;
      }
      public Builder clearAppId() {
        result.hasAppId = false;
        result.appId_ = getDefaultInstance().getAppId();
        return this;
      }
      
      // required int64 runtime = 12;
      public boolean hasRuntime() {
        return result.hasRuntime();
      }
      public long getRuntime() {
        return result.getRuntime();
      }
      public Builder setRuntime(long value) {
        result.hasRuntime = true;
        result.runtime_ = value;
        return this;
      }
      public Builder clearRuntime() {
        result.hasRuntime = false;
        result.runtime_ = 0L;
        return this;
      }
      
      // required double procCpu = 3;
      public boolean hasProcCpu() {
        return result.hasProcCpu();
      }
      public double getProcCpu() {
        return result.getProcCpu();
      }
      public Builder setProcCpu(double value) {
        result.hasProcCpu = true;
        result.procCpu_ = value;
        return this;
      }
      public Builder clearProcCpu() {
        result.hasProcCpu = false;
        result.procCpu_ = 0D;
        return this;
      }
      
      // required int64 cpuTotal = 19;
      public boolean hasCpuTotal() {
        return result.hasCpuTotal();
      }
      public long getCpuTotal() {
        return result.getCpuTotal();
      }
      public Builder setCpuTotal(long value) {
        result.hasCpuTotal = true;
        result.cpuTotal_ = value;
        return this;
      }
      public Builder clearCpuTotal() {
        result.hasCpuTotal = false;
        result.cpuTotal_ = 0L;
        return this;
      }
      
      // required int64 cpuProcTotal = 21;
      public boolean hasCpuProcTotal() {
        return result.hasCpuProcTotal();
      }
      public long getCpuProcTotal() {
        return result.getCpuProcTotal();
      }
      public Builder setCpuProcTotal(long value) {
        result.hasCpuProcTotal = true;
        result.cpuProcTotal_ = value;
        return this;
      }
      public Builder clearCpuProcTotal() {
        result.hasCpuProcTotal = false;
        result.cpuProcTotal_ = 0L;
        return this;
      }
      
      // required float rps = 10;
      public boolean hasRps() {
        return result.hasRps();
      }
      public float getRps() {
        return result.getRps();
      }
      public Builder setRps(float value) {
        result.hasRps = true;
        result.rps_ = value;
        return this;
      }
      public Builder clearRps() {
        result.hasRps = false;
        result.rps_ = 0F;
        return this;
      }
      
      // required float averageDelay = 13;
      public boolean hasAverageDelay() {
        return result.hasAverageDelay();
      }
      public float getAverageDelay() {
        return result.getAverageDelay();
      }
      public Builder setAverageDelay(float value) {
        result.hasAverageDelay = true;
        result.averageDelay_ = value;
        return this;
      }
      public Builder clearAverageDelay() {
        result.hasAverageDelay = false;
        result.averageDelay_ = 0F;
        return this;
      }
      
      // required bool overloaded = 11;
      public boolean hasOverloaded() {
        return result.hasOverloaded();
      }
      public boolean getOverloaded() {
        return result.getOverloaded();
      }
      public Builder setOverloaded(boolean value) {
        result.hasOverloaded = true;
        result.overloaded_ = value;
        return this;
      }
      public Builder clearOverloaded() {
        result.hasOverloaded = false;
        result.overloaded_ = false;
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:test.AppServer)
    }
    
    static {
      defaultInstance = new AppServer(true);
      org.prot.util.managment.generated.ManagementData.internalForceInit();
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:test.AppServer)
  }
  
  
  static {
  }
  
  public static void internalForceInit() {}
  
  // @@protoc_insertion_point(outer_class_scope)
}
