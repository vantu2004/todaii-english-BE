package com.todaii.english.server.dictionary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
class DictionarySeederScript {
  @Autowired private DataSource dataSource;

  @Value("classpath:data/words_dictionary.json")
  private Resource resourceFile;

  @Test
  void runSeeder() throws Exception {
    System.out.println("START IMPORT");

    ObjectMapper mapper = new ObjectMapper();

    Map<String, Integer> map =
        mapper.readValue(
            resourceFile.getInputStream(), new TypeReference<Map<String, Integer>>() {});

    // Đưa cả PreparedStatement vào try-with-resources để tự động close
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps =
            conn.prepareStatement("INSERT IGNORE INTO dictionary_words(word) VALUES (?)")) {

      conn.setAutoCommit(false);

      int batch = 0;

      for (String word : map.keySet()) {
        ps.setString(1, word);
        ps.addBatch();

        if (++batch % 2000 == 0) {
          ps.executeBatch();
          conn.commit();
          System.out.println("Imported: " + batch);
        }
      }

      // Execute và commit phần dư cuối cùng (vd: 600 dòng cuối)
      ps.executeBatch();
      conn.commit();
    } // Khi kết thúc block try này, HikariCP sẽ tự động trả connection về pool một cách an toàn

    System.out.println("DONE");
    // TUYỆT ĐỐI KHÔNG DÙNG System.exit(0); Ở ĐÂY NỮA
  }
}
