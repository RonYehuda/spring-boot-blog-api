package org.example;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Email;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @NotBlank(message = "Name cant be blank")
    @Size(min = 2, max = 50 , message = "Name must be between 2 to 50 chars")
    private String name;

    @NotBlank(message = "Last Name cant be blank")
    @Size(min = 2, max = 50, message = "Last Name must be between 2 to 50 chars")
    private String lastName;

    @Min(value = 18, message = "Minimum age is 18")
    @Max(value = 120, message = "Maximum age is 120")
    private int age;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @NotBlank(message = "Email cant be blank")
    @Email(message = "Email should be valid")
    @Column(unique = true)
    @Size(max = 254, message = "Email must be less than 254 characters")
    private String email;

    // mappedBy = "user" - means that Post contains a field named "user" that holds the relationship
    // cascade = CascadeType.ALL - If you delete a user, you will also delete all their posts
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Post> posts;

    public User (){}

    public User (String name, String lastName, int age, String password, String email){
        this.name = name;
        this.lastName = lastName;
        this.age = age;
        this.password = password;
        this.email = email;
        this.posts = new ArrayList<>();
    }

    public void setPosts(List<Post> posts) {this.posts = posts;}
    public List<Post> getPosts() {return posts;}

    public void addPost(Post post){
        posts.add(post);
        post.setUser(this); //Establishing a two-way connection
    }

    public Long getId() {
        return Id;
    }
    public void setId(Long Id) {
        this.Id = Id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }
    public int getAge() {
        return age;
    }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Role getRole() {return role;}
    public void setRole(Role role) {this.role = role;}

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
}
