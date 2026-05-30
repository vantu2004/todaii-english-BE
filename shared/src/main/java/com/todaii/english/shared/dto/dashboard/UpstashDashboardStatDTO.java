package com.todaii.english.shared.dto.dashboard;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UpstashDashboardStatDTO {
  private Integer dailyNetCommands;
  private Integer dailyReadRequests;
  private Integer dailyWriteRequests;
  private Long totalMonthlyRequests;
  private Long currentStorage;
  private Long dailyBandwidth;

  // Biểu đồ lưu trữ (Storage & Keyspace)
  private List<DataPoint> keyspace;
  private List<DataPoint> diskusage;

  // Biểu đồ kết nối & Băng thông (Traffic)
  private List<DataPoint> connectionCount;
  private List<DataPoint> throughput;

  // Biểu đồ hiệu suất Cache (Cache Performance)
  private List<DataPoint> hits;
  private List<DataPoint> misses;

  // Biểu đồ Độ trễ (Latency)
  private List<DataPoint> latencymean;
  private List<DataPoint> latency99;

  // Biểu đồ Phân bổ lệnh (Command Breakdown)
  private List<CommandStat> commandCounts;

  // Dùng chung cho tất cả các mảng [{x, y}] (Thời gian - Giá trị)
  @Getter
  @Setter
  @JsonIgnoreProperties(ignoreUnknown = true)
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public static class DataPoint {
    private String x; // Thời gian: "2026-05-30 07:34:00.000 +0000 UTC"
    private Double y; // Giá trị (Để Double để bao quát được cả int và float từ Upstash)
  }

  // Dành riêng cho dữ liệu có thêm thông tin region (dailyrequests, bandwidths)
  @Getter
  @Setter
  @JsonIgnoreProperties(ignoreUnknown = true)
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public static class RegionalDataPoint {
    private String x;
    private Double y;
    private Map<String, Integer> regionalDetails;
  }

  // Dành riêng cho mảng phân tích từng lệnh (EXISTS, GET, SET...)
  @Getter
  @Setter
  @JsonIgnoreProperties(ignoreUnknown = true)
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public static class CommandStat {
    private String metricIdentifier; // Tên lệnh
    private List<DataPoint> dataPoints; // Danh sách [x, y] của lệnh đó
  }
}
