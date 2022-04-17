/** @module  party_controller */
Party = require('../models/parties');
/**
 * Creates a party
 * @function createParty
 * @param {String} party_name
 * @param {String} date
 * @param {String} time
 * @param {String} privacy
 * @param {String} max_people
 * @param {String} alerts
 * @param {String} host
 * @param {String} location
 * @return {json} success/fail
 */
module.exports.createParty = function(party_name, date, time, privacy, max_people, alerts, host, location, res) {
    var party1 = new Party();
    party = new Party({
        party_name: party_name,
        date: date,
        time: time,
        privacy: privacy,
        max_people: max_people,
        alerts: alerts,
        host: host,
        location: location
    });
    party1.find('all', {
        where: 'party_name=' + '\'' + party_name + '\''
    }, function(err, rows, fields) {
        if (err) {
            console.log("error");
            res.json({
                parties: "Error"
            })
        } else {
            if (rows.length == 0) {
                party.save(function(err) {
                    if (err) {
                        console.log("Unable to create party");
                        res.json({
                            parties: "Unable to create party, err"
                        })
                    } else {
                        console.log("Created new party: ", party_name);
                        res.json({
                            parties: "Created new party",
                            party_name: party_name
                        })
                    }
                });
            } else {
                res.json({
                    parties: "Party already exists"
                })
            }
        }
    });
}
/**
 * @function findAllParties
 * Gets all parties from the db
 * @return {json} List of parties
 */
module.exports.findAllParties = function(res) {
    var party = new Party();
    party.find('all', function(err, rows, fields) {
        if (err) {
            console.log("error");
            res.json({
                parties: "Error"
            })
        } else {
            if (rows.length == 0) {
                console.log("No parties not found.");
                res.json({
                    parties: "Error"
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
 * @function findPartyByID
 * Gets a party from the db by id
 * @param {String} id
 * @return {json} a party
 */
module.exports.findPartyByID = function(id, res) {
    var party = new Party();
    party.find('all', {
        where: 'id=' + id
    }, function(err, rows, fields) {
        if (err) {
            console.log("error");
            res.json({
                parties: "Error"
            })
        } else {
            if (rows.length == 0) {
                console.log("Party not found.");
                res.json({
                    parties: "Error"
                })
            } else {
                res.contentType('application/json');
                res.send(JSON.stringify(rows));
                console.log(party);
            }
        }
    });
}
/**
 * @function findPartyByName
 * Find a party by name in the db
 * @param {String} name
 * @return {json} a party
 */
module.exports.findPartyByName = function(name, res) {
    var party = new Party();
    party.find('all', {
        where: 'party_name=' + '\'' + name + '\''
    }, function(err, rows, fields) {
        if (err) {
            console.log("error");
            res.json({
                parties: "Error"
            })
        } else {
            if (rows.length == 0) {
                console.log("Party not found.");
                res.json({
                    parties: "Error"
                })
            } else {
                res.contentType('application/json');
                res.send(JSON.stringify(rows));
                console.log(party);
            }
        }
    });
}
/**
 * @function deleteParty
 * Deletes a party by id
 * @param {String} id
 * @return {json} success/fail
 */
module.exports.deleteParty = function(id, res) {
    party = new Party();
    party.set('id', id);
    party.remove(function(err) {
        if (err) {
            console.log("Tried to delete a null party: ", id);
            res.json({
                parties: "Error"
            })
        } else {
            console.log('Deleted party: ', id);
            res.json({
                parties: "Success"
            })
        }
    });
}
/**
 * @function updateParty
 * Updates a party by id
 * @param {String} id
 * @param {String} party_name
 * @param {String} date
 * @param {String} time
 * @param {String} privacy
 * @param {String} max_people
 * @param {String} alerts
 * @param {String} host
 * @param {String} location
 * @return {json} success/fail
 */
module.exports.updateParty = function(id, party_name, date, time, privacy, max_people, alerts, host, location, res) {
    party = new Party({
        id: id,
        party_name: party_name,
        date: date,
        time: time,
        host: host,
        privacy: privacy,
        max_people: max_people,
        alerts: alerts,
        location: location
    });
    party.save(function(err) {
        if (err) {
            console.log("Unable to create party", err);
            res.json({
                parties: "Error"
            })
        } else {
            console.log("Updated new party: ", party_name);
            res.json({
                parties: "Success",
                party_name: party_name
            })
        }
    });
}
/**
 * @function joinByPartyId
 * Gets a list of all users associated with a party
 * @param {String} id
 * @return {json} A list of all users associated with a party
 */
module.exports.joinByPartyId = function(id, res) {
    var user = new User();
    user.query("SELECT users.id, users.f_name, users.l_name, party_relation.party_id, party_relation.scanned_in, party_relation.user_id, party_relation.party_user_relation FROM users join party_relation ON users.id=party_relation.user_id join parties ON parties.id=party_relation.party_id WHERE parties.id=\"" + id + "\";", function(err, rows, fields) {
        if (err) {
            console.log("error");
            res.json({
                users: "Error"
            })
        } else {
            if (rows.length == 0) {
                console.log("User not found.");
                res.json({
                    users: "Error"
                })
            } else {
                res.contentType('application/json');
                res.send(JSON.stringify(rows));
            }
        }
    });
}