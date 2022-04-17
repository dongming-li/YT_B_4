/** @module  relation_controller */
Relation = require('../models/relations');
/**
 * @function createRelation
 * Creates a new relation
 * @param {String} user_id
 * @param {String} party_id
 * @param {String} relation
 * @return {json} success/fail
 */
module.exports.createRelation = function(user_id, party_id, relation, res) {
    var relation = new Relation({
        user_id: user_id,
        party_id: party_id,
        party_user_relation: relation
    });
    relation.query("SELECT * FROM db309ytb4.party_relation WHERE user_id=\"" + user_id + "\" AND party_id=\"" + party_id + "\";", function(err, rows, fields) {
            if (err) {
                console.log("error db");
                res.json({
                    relation: "Error"
                })
            } else {
                if (rows.length == 0) {
                    
                    relation.save(function(err) {
                        if (err) {
                            console.log("Unable to create relation");
                            res.json({
                                relations: "Error"
                            })
                        } else {
                            console.log("Created new relation: ", user_id);
                            res.json({
                                user_id: user_id,
                                party_id: party_id
                            })
                        }
                    });
                } else {
                    console.log("relations already exist.");
                    res.json({
                        relations: "Error"
                    })
                }
            }
        });
    
}
/**
 * @function findAllRelations
 * Gets all relations from db
 * @return {json} A list of relations
 */
module.exports.findAllRelations = function(res) {
    var relation = new Relation();
    relation.find('all', function(err, rows, fields) {
        if (err) {
            console.log("error");
            res.json({
                relations: "Error"
            })
        } else {
            if (rows.length == 0) {
                console.log("Relations not found.");
                res.json({
                    relations: "Error"
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
 * @function findRelationByID
 * Gets a relation by id
 * @param {String} id
 * @return {json} relation
 */
module.exports.findRelationByID = function(id, res) {
    var relation = new Relation();
    relation.find('all', {
        where: 'user_id=' + id
    }, function(err, rows, fields) {
        if (err) {
            console.log("error");
            res.json({
                relations: "Error"
            })
        } else {
            if (rows.length == 0) {
                console.log("Relation not found.");
                res.json({
                    relations: "Error"
                })
            } else {
                res.contentType('application/json');
                res.send(JSON.stringify(rows));
            }
        }
    });
}
/**
 * @function deleteRelation
 * Deletes a relation
 * @param {String} user_id
 * @param {String} party_id
 * @return {json} success/fail
 */
module.exports.deleteRelation = function(user_id, party_id, res) {
    relation = new Relation();
    relation.query("DELETE FROM party_relation WHERE user_id=\"" + user_id + "\" AND party_id=\"" + party_id + "\"", function(err, rows, fields) {
        if (err) {
            console.log("Tried to delete a null relation: ", user_id, err);
            res.json({
                relations: "Error"
            })
        } else {
            console.log('Deleted relation: ', user_id, party_id);
            res.json({
                relations: "Success"
            })
        }
    });
}
module.exports.scanUser = function(user_id, party_id, scanned_in, res) {
    relation = new Relation();
    
    relation.query("UPDATE party_relation SET scanned_in=" + scanned_in + " WHERE user_id=" + user_id + " AND party_id=" + party_id + ";", function(err, rows, fields) {
        if (err) {
            console.log("Failed to scan: ", user_id, err);
            res.json({
                relations: "Error"
            })
        } else {
            console.log('scan complete: ', user_id, party_id);
            res.json({
                relations: "Success"
            })
        }
    });
}