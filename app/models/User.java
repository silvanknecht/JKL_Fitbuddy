package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity(name="fitUser")
public class User{

    @Id
    @SequenceGenerator(name="fitUser_id_seq", sequenceName="fitUser_id_seq",allocationSize=1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator="fitUser_id_seq")
    private long id;
    private String description;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String avatarUrl;

    @ManyToMany(fetch=FetchType.EAGER, mappedBy="users")
    @JsonManagedReference  // siehe jackson bidirectional relationships and infinite recursion
    private Set<Category> categories = new HashSet<>();

    // siehe jpa many to many relashionship causing infinite recursion auf stack overflow
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    }, fetch=FetchType.EAGER)
    @JoinTable(name="Buddies",
            joinColumns = @JoinColumn(name="fitUser_id"),
            inverseJoinColumns = @JoinColumn(name="buddy_id")
    )
    @JsonBackReference
    private Set<User> buddies = new HashSet<>();

    @ManyToMany(fetch=FetchType.EAGER, mappedBy="buddies")
    @JsonManagedReference
    private Set<User> following = new HashSet<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Set<Category> getCategories() { return this.categories; }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public Set<User> getBuddies() { return this.buddies; }

    public void setBuddies(Set<User> buddies) { this.buddies = buddies; }

    public Set<User> getFollowing() { return this.following; }

    public void setFollowing(Set<User> following) { this.following = following; }
}
