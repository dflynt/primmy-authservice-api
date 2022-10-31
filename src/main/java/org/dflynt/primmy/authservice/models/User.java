package org.dflynt.primmy.authservice.models;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;
import java.sql.Timestamp;

@Data
@Entity
public class User {

    @Id
    long id;

    @Column(name="userid")
    String userid;

    @Column(name="firstname")
    String firstName;

    @Column(name="lastname")
    String lastName;

    @Column(name="email")
    String email;

    @Column(name="password")
    String password;

    @Column(name="institution")
    String institution;

    @Column(name="field")
    String field;

    @Column(name="focus")
    String focus;

    @Column(name="signupdate")
    Timestamp signupdate;

    @Column(name="leader")
    boolean leader;

    @Column(name="avatar")
    String avatar;

    @Column(name="deleteddate")
    Timestamp deleteddate;

    @Column(name="enabled")
    boolean enabled;

    @Column(name="verificationcode")
    String verificationCode;

    @Column(name="authtoken")
    String authToken;

    @Column(name="refreshtoken")
    String refreshToken;

    public User() {}

    public User(String firstName) {
        this.firstName = firstName;
    }

    public User(String uuid, String firstName, String lastName,
                String email, String password, String institution,
                String field, String focus, boolean leader, String verificationCode) {
        this.userid = uuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.institution = institution;
        this.leader = leader;
        this.field = field;
        this.focus = focus;
        this.leader = leader;
        this.signupdate = Timestamp.from(Instant.now());
        this.avatar = null;
        this.deleteddate = null;
        this.verificationCode = verificationCode;
        this.enabled = false;
    }
}
