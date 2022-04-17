/** @module  comment_controller */
Comment = require('../models/comments');

/**
 * @function newComment
 * creates a new comment in the db
 * @param {String} id
 * @param {String} username
 * @param {String} cmt
 * @return {json} a comment
 */
module.exports.newComment = function(party_id, username, text, res) {
    var comment1 = new Comment();
    comment = new Comment({
        party_id: party_id,
        username: username,
        comment: text
    });
    comment.save(function(err) {
        if (err) {
            console.log("Unable to create comment" + err);
            res.json({
                comment: "Error"
            })
        } else {
            comment.query("SELECT * FROM db309ytb4.comments WHERE comment=\"" + text + "\" AND username=\"" + username + "\";", function(err, rows, fields) {
                if (err) {
                    console.log("error");
                    res.json({
                        comments: "Error"
                    })
                } else {
                    if (rows.length == 0) {
                        console.log("Comment not found.");
                        res.json({
                            comments: "Error"
                        })
                    } else {
                        console.log("Comment :" + text);
                        res.json({
                            id: rows[0].id,
                            comment: rows[0].comment,
                            party_id: rows[0].party_id,
                            username: rows[0].username,
                        })
                    }
                }
            });
        }
    });
}
/**
 * @function findAllComments
 * Gets all comments from the database
 * @return {json} A list of all comments in the database
 */
module.exports.findAllComments = function(res) {
    var comment = new Comment();
    comment.find('all', function(err, rows, fields) {
        if (err) {
            console.log("error");
            res.json({
                comment: "Error"
            })
        } else {
            if (rows.length == 0) {
                console.log("Comments not found.");
                res.json({
                    comment: "Error"
                })
            } else {
                console.log(rows);
                res.contentType('application/json');
                res.send(JSON.stringify(rows));
            }
        }
    });
}
/**
 * @function findCommentByID
 * Gets a comment from the db
 * @param {String} id
 * @return {json} a comment
 */
module.exports.findCommentByID = function(id, res) {
    var comment = new Comment();
    comment.find('all', {
        where: 'id=' + id
    }, function(err, rows, fields) {
        if (err) {
            console.log("error");
            res.json({
                comments: "Error"
            })
        } else {
            if (rows.length == 0) {
                console.log("Comment not found.");
                res.json({
                    comments: "Error"
                })
            } else {
                res.contentType('application/json');
                res.send(JSON.stringify(rows));
            }
        }
    });
}
/**
 * @function deleteComment
 * deletes a comment by id
 * @param {String} id
 * @return {json} a comment
 */
module.exports.deleteComment = function(id, res) {
    comment = new Comment();
    comment.set('id', id);
    comment.remove(function(err) {
        if (err) {
            console.log("Tried to delete a null comment: ", id);
            res.json({
                comments: "Error"
            })
        } else {
            console.log('Deleted comment: ', id);
            res.json({
                comments: "Success"
            })
        }
    });
}
/**
 * @function get: getAllCommentsByPartyId
 * Gets a list of comments based on party id
 * @param {String} id
 * @return {json} a list of comments
 */

module.exports.getAllCommentsByPartyId = function(id, res) {
    var comment = new Comment();
    comment.find('all', {
        where: 'party_id=' + id
    }, function(err, rows, fields) {
        if (err) {
            console.log("error");
            res.json({
                Comments: "Error"
            })
        } else {
            if (rows.length == 0) {
                console.log("Comments not found.");
                res.json({
                    Comments: "Error"
                })
            } else {
                res.contentType('application/json');
                res.send(JSON.stringify(rows));
                console.log(id);
            }
        }
    });
}