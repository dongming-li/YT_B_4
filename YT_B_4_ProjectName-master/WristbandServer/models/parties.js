var mysqlModel = require('mysql-model');
var MyAppModel = require('./appModel');
/**
 * @class Party
 * Represents a Party DB MySQL Model
 * @param {tableName} parties table in db
 */
var Party = MyAppModel.extend({
    tableName: "parties",
});

module.exports = Party;