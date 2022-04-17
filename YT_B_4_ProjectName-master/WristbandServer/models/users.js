var mysqlModel = require('mysql-model');
var MyAppModel = require('./appModel');
/**
 * @class User
 * Represents a User DB MySQL Model
 * @param {tableName} users table in db
 */
var User = MyAppModel.extend({
    tableName: "users",
});

module.exports = User;