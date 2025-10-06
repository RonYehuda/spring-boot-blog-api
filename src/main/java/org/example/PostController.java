package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class PostController {
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/users/{userId}/posts")
    public ResponseEntity<Post> newPost(@PathVariable Long userId, @RequestBody Post post){
        logger.info("Creating new post: {} \n by user with ID: {}",  post.getTitle(),userId);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            post.setUser(user);
            Post savedPost = postRepository.save(post);
            logger.info("Post '{}' created successfully by user with ID: {}", savedPost.getTitle(), userId);
            return ResponseEntity.ok(savedPost);
        }
        logger.warn("User with ID: {} not found, post creation failed", userId);
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/users/{userId}/posts")
    public ResponseEntity<List<Post>> userAllPosts(@PathVariable Long userId){
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()){
            return ResponseEntity.ok(optionalUser.get().getPosts());
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/posts")
    public ResponseEntity<List<Post>> returnAllPost(){
        List<Post> allPost = postRepository.findAll();
        return ResponseEntity.ok(allPost);
    }
    @GetMapping("/posts/title/{title}")
    public ResponseEntity<List<Post>> findByTitle(@PathVariable String title){
        List<Post> posts = postRepository.findByTitle(title);
        if (posts.isEmpty())return ResponseEntity.notFound().build();
        return ResponseEntity.ok(posts);
    }
    @GetMapping("/posts/search/{keyword}")
    public ResponseEntity<List<Post>> findByTitleContaining(@PathVariable String keyword){
        List<Post> posts = postRepository.findByTitleContaining(keyword);
        if (posts.isEmpty())return ResponseEntity.notFound().build();
        return ResponseEntity.ok(posts);
    }
    @GetMapping("/posts/user/{userId}")
    public ResponseEntity<List<Post>> findByUserId(@PathVariable Long userId){
        List<Post> posts = postRepository.findByUserId(userId);
        if (posts.isEmpty())return ResponseEntity.notFound().build();
        return ResponseEntity.ok(posts);
    }


}
