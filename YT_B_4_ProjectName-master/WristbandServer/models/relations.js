var mysqlModel = require('mysql-model');
var MyAppModel = require('./appModel');
/**
 * @class Relation
 * Represents a Relation DB MySQL Model
 * @param {tableName} party_relation table in db
 */
var Relation = MyAppModel.extend({
    tableName: "party_relation",
});

module.exports = Relation;