package com.urosdragojevic.realbookstore.repository;

import com.urosdragojevic.realbookstore.audit.AuditLogger;
import com.urosdragojevic.realbookstore.domain.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CommentRepository {

    private static final Logger LOG = LoggerFactory.getLogger(CommentRepository.class);

    private static final AuditLogger auditLogger = AuditLogger.getAuditLogger(CommentRepository.class);

    private DataSource dataSource;

    public CommentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void create(Comment comment) {
//        String query = "insert into comments(bookId, userId, comment) values (" + comment.getBookId() + ", " + comment.getUserId() + ", '" + comment.getComment() + "')";
//                try (Connection connection = dataSource.getConnection();
//                     Statement statement = connection.createStatement();
//                ) {
//                    statement.execute(query);
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//        SQLi

        String query = "insert into comments(bookId, userId, comment) values (?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setInt(1, comment.getBookId());
            statement.setInt(2, comment.getUserId());
            statement.setString(3, comment.getComment());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            LOG.warn("Failed to insert comment " + comment.getComment());
        }
        auditLogger.audit(comment.getUserId() + " successfully created comment:"
                + comment.getComment() + " for book: " + comment.getBookId());
    }
    public List<Comment> getAll(int bookId) {
        List<Comment> commentList = new ArrayList<>();
        String query = "SELECT bookId, userId, comment FROM comments WHERE bookId = " + bookId;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                commentList.add(new Comment(rs.getInt(1), rs.getInt(2), rs.getString(3)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            LOG.warn("Failed to retrieve comments for book: " + bookId);
        }
        return commentList;
    }
}
