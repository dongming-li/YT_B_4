var config = require('../config');
var mysqlModel = require('mysql-model');
/**
 * @class MySqlModel
 * Represents the MySQL Database
 * @param {host} db host
 * @param {user} db user
 * @param {password} db password
 * @param {database} db database 
 */
module.exports = mysqlModel.createConnection({
  host     : config.mysql.host,
  user     : config.mysql.user,
  password : config.mysql.password,
  database : config.mysql.database
});