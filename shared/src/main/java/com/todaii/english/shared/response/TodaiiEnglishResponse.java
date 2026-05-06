package com.todaii.english.shared.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
// trường hợp TodaiiEnglishResponse thiếu field so với response thì ignore luôn
@JsonIgnoreProperties(ignoreUnknown = true)
// ko xuất các field bị null
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TodaiiEnglishResponse {
    private Long total;
    private Boolean found;
    private List<Result> result;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Result {
        private String id;
        private Long resultID;

        // Từ vựng chính (ví dụ: "mango")
        private String word;

        // Từ khóa dùng để search
        private String keyword;

        // Ngôn ngữ (en, vi,...)
        private String language;

        // Loại từ (noun, verb,...)
        private String type;

        // Chủ đề (topic nếu có)
        private String topic;

        // Tần suất xuất hiện của từ
        private Double freq;

        // Có phải động từ bất quy tắc không
        private Boolean irregular;

        // Phiên âm
        private Pronounce pronounce;

        // Cấp độ từ (toeic, vietnam level)
        private LevelWord levelWord;

        // Chia động từ (v1, v2, v3,...)
        private Map<String, ConjugationItem> conjugation;

        // Họ từ (word family: noun, verb liên quan)
        private List<WordFamily> wordFamily;

        // Nội dung chính: nghĩa + ví dụ
        private List<ResultContent> content;

        // Collocations (cụm từ đi kèm)
        private List<Coll> coll;

        // Synonym / Antonym
        private List<Snym> snym;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Pronounce {

        // Phiên âm Anh - Anh
        private String gb;

        // Phiên âm Anh - Mỹ
        private String us;

        // Phiên âm cơ bản
        private String base;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LevelWord {

        // Mức độ theo TOEIC
        private Long toeic;

        // Mức độ theo hệ Việt Nam
        private Long vietnam;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ConjugationItem {

        // Phiên âm của dạng chia
        private String p;

        // Từ đã chia (ví dụ: went, gone,...)
        private String w;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class WordFamily {

        // Loại nhóm (ví dụ: related)
        private String kind;

        // Trường thông tin
        private String field;

        // Phiên âm các từ liên quan
        private List<String> p;

        // Danh sách từ cùng họ
        private List<String> content;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ResultContent {

        // Loại nội dung (ví dụ: meaning)
        private String kind;

        // Trường dữ liệu
        private String field;

        // Danh sách nghĩa
        private List<Mean> means;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Mean {

        // Nghĩa của từ
        private String mean;

        // Ví dụ minh họa
        private List<Example> examples;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Example {

        // ID ví dụ
        private Long exampleID;

        // ID dạng string
        private String id;

        // Câu tiếng Anh
        private String e;

        // Nghĩa tiếng Việt
        private String m;

        // Ngôn ngữ
        private String language;

        // Loại câu
        private String type;

        // Phiên âm (có thể null hoặc object → nên để Object)
        private Object p;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Coll {

        // Loại collocation
        private String kind;

        // Danh sách nhóm collocation
        private List<CollCl> cls;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CollCl {

        // Nghĩa của cụm
        private String mean;

        // Danh sách cụm chi tiết
        private List<ClCl> cl;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ClCl {

        // Loại (verb, noun,...)
        private String type;

        // Danh sách cụm từ
        private List<Cb> cbs;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Cb {

        // Collocation (cụm từ)
        private String cb;

        // Ví dụ
        private String ex;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Snym {

        // Loại (synonym / antonym)
        private String kind;

        // Nội dung chi tiết
        private List<SnymContent> content;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SnymContent {

        // Từ đồng nghĩa
        private List<String> syno;

        // Từ trái nghĩa
        private List<String> anto;
    }
}