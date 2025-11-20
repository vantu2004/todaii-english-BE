package com.todaii.english.client.user;

import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.todaii.english.client.user.UserRepository;
import com.todaii.english.core.entity.Article;
import com.todaii.english.core.entity.User;
import com.todaii.english.shared.enums.error_code.UserErrorCode;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.client.article.ArticleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserArticleService {
	
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    public void saveArticleForUser(Long userId, Long articleId) {
        User user = userRepository.findById(userId)
				.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new BusinessException(404, "Article not found with id: " + articleId));
        
        user.getSavedArticles().add(article);
        article.getSavedByUser().add(user);
        
        userRepository.save(user);
    }
    

    public void removeSavedArticleFromUser(Long userId, Long articleId) {
        User user = userRepository.findById(userId)
				.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new BusinessException(404, "Article not found with id: " + articleId));
        
        user.getSavedArticles().remove(article);
        article.getSavedByUser().remove(user);
        
        userRepository.save(user);
    }
    
    @Transactional(readOnly = true)
    public Set<Article> getSavedArticlesByUser(Long userId) {
        User user = userRepository.findById(userId)
				.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        
        return user.getSavedArticles();
    }

    @Transactional(readOnly = true)
    public Page<Article> getSavedArticlesByUserPageable(Long userId, Pageable pageable) {
        return articleRepository.findSavedArticlesByUserId(userId, pageable);
    }
    
    @Transactional(readOnly = true)
    public boolean isArticleSavedByUser(Long userId, Long articleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        return user.getSavedArticles().stream()
                .anyMatch(article -> article.getId().equals(articleId));
    }
    
    @Transactional(readOnly = true)
    public long countSavedArticlesByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        return user.getSavedArticles().size();
    }
    
   
    public void clearAllSavedArticlesForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        Set<Article> articles = user.getSavedArticles();
        
        for (Article article : articles) {
            article.getSavedByUser().remove(user);
        }
        
        user.getSavedArticles().clear();
        userRepository.save(user);
    }
}
