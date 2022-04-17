var mysqlModel = require('mysql-model');
var MyAppModel = require('./appModel');
/**
 * @class Comment
 * Represents a Comment DB MySQL Model
 * @param {tableName} table in db
 */
var Comment = MyAppModel.extend({
    tableName: "comments",
});

module.exports = Comment;