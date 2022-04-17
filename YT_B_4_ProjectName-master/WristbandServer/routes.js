/** @module  Routes */
module.exports = function(app) {
    var User = require('./controllers/user_controller');
    var Party = require('./controllers/party_controller');
    var Relation = require('./controllers/relation_contoller');
    var Comment = require('./controllers/comment_controller');
    var path = require('path');
    /**
     * Register view engine
     * @param {String} View engine
     * @param {String} pug (file type to recognize)
     */
    app.set('view engine', 'pug');
    /**
     * @function get: /
     * Represents the default path to the website
     * @return {html} index
     */
    app.get('/', function(req, res) {
        var data;
        app.get('/users', function(req, res) {
            data = User.findAllUsers(res);
        });
        res.render('index', {
            title: 'Users',
            users: 'Hello there!'
        });
    });
    /**
     * @function post: /gitlab
     * Continuous Integration
     * Runs a script to pull from git
     */
    app.post('/gitlab', function(req, res) {
        console.log("Updating git");
        require('child_process').spawn('bash', ['test.sh'], {
            stdio: 'inherit'
        });
    });

    /**
     * @function get: /users
     * @return All users in database
     */
    app.get('/users', function(req, res) {
        User.findAllUsers(res);
    });
    /**
     * @function get: /users/:id
     * Finds a user by their id in the database
     * @return {json} a list of users
     */
    app.get('/users/:id', function(req, res) {
        User.findUserByID(req.params.id, res);
    });
    /**
     * @function post: /users_by_full_name
     * Finds a user by their id in the database
     * @return {json} a list of users
     */
    app.post('/users_by_full_name', function(req, res) {
        User.findUserByFullName(req.headers.f_name, req.headers.l_name, res);
    });
    /**
     * @function get: /user_name/:username
     * Finds a user by their username in the db
     * @return {json} a user
     */
    app.get('/user_name/:username', function(req, res) {
        User.findUserByUsername(req.params.username, res);
    });
    /**
     * @function post: /users
     * Finds a user by their username in the db
     * @param {String} username
     * @return {json} a user
     */
    app.post('/users', function(req, res) {
        User.createUser(req.headers.f_name, req.headers.l_name, req.headers.username, req.headers.password, req.headers.email, res);
    });
    /**
     * @function put: /users/:id
     * Updates a user by their user id
     * @param {String} f_name
     * @param {String} l_name
     * @param {String} username
     * @param {String} password
     * @param {String} email
     * @param {String} email
     * @return {json} success/fail
     */
    app.put('/users/:id', function(req, res) {
        User.updateUser(req.params.id, req.headers.f_name, req.headers.l_name, req.headers.username, req.headers.password, req.headers.email, res);
    });
    /**
     * @function delete: /users/:id
     * Deletes a user by their user id
     * @param {String} id
     * @param {String} f_name
     * @param {String} l_name
     * @param {String} username
     * @param {String} password
     * @return {json} success/fail
     */
    app.delete('/users/:id', function(req, res) {
        User.deleteUser(req.params.id, res);
    });
    /**
     * @function post: /users/login
     * Lets a user login
     * @param {String} username
     * @param {String} password
     */
    app.post('/users/login', function(req, res) {
        User.login(req.headers.username, req.headers.password, res);
    });
    /**
     * @function get: /parties
     * Gets all parties from the db
     * @return {json} List of parties
     */
    app.get('/parties', function(req, res) {
        Party.findAllParties(res);
    });
    /**
     * @function get: /parties/:id
     * Gets a party from the db by id
     * @param {String} id
     * @return {json} a party
     */
    app.get('/parties/:id', function(req, res) {
        Party.findPartyByID(req.params.id, res);
    });
    /**
     * @function get: /party_name/:name
     * Find a party by name in the db
     * @param {String} name
     * @return {json} a party
     */
    app.get('/party_name/:name', function(req, res) {
        Party.findPartyByName(req.params.name, res);
    });
    /**
     * @function post: /parties
     * Creates a party
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
    app.post('/parties', function(req, res) {
        Party.createParty(req.headers.party_name, req.headers.date, req.headers.time, req.headers.privacy, req.headers.max_people, req.headers.alerts, req.headers.host, req.headers.location, res);
    });
    /**
     * @function put: /parties
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
    app.put('/parties', function(req, res) {
        Party.updateParty(req.headers.id, req.headers.party_name, req.headers.date, req.headers.time, req.headers.privacy, req.headers.max_people, req.headers.alerts, req.headers.host, req.headers.location, res);
    });
    /**
     * @function delete: /parties/:id
     * Deletes a party by id
     * @param {String} id
     * @return {json} success/fail
     */
    app.delete('/parties/:id', function(req, res) {
        Party.deleteParty(req.params.id, res);
    });
    /**
     * @function get: /relation
     * Gets all relations from db
     * @return {json} A list of relations
     */
    app.get('/relation', function(req, res) {
        Relation.findAllRelations(res);
    });
    /**
     * @function get: /relation/:id
     * Gets a relation by id
     * @param {String} id
     * @return {json} relation
     */
    app.get('/relation/:id', function(req, res) {
        Relation.findRelationByID(req.params.id, res);
    });
    /**
     * @function get: /relation
     * Creates a new relation
     * @param {String} user_id
     * @param {String} party_id
     * @param {String} relation
     * @return {json} success/fail
     */
    app.post('/relation', function(req, res) {
        Relation.createRelation(req.headers.user_id, req.headers.party_id, req.headers.relation, res);
    });
    /**
     * @function delete: /relation
     * Deletes a relation
     * @param {String} user_id
     * @param {String} party_id
     * @return {json} success/fail
     */
    app.delete('/relation', function(req, res) {
        Relation.deleteRelation(req.headers.user_id, req.headers.party_id, res);
    });
    /**
     * @function delete: /scan
     * Scans in a user
     * @param {String} user_id
     * @param {String} party_id
     * @return {json} success/fail
     */
    app.post('/scan', function(req, res) {
        Relation.scanUser(req.headers.user_id, req.headers.party_id, req.headers.scanned_in, res);
    });
    /**
     * @function get: /join_user/:id
     * Gets a list of all relations associated with a user and the party name
     * @param {String} id
     * @return {json} A list of all relations associated with a user and the party name
     */
    app.get('/join_user/:id', function(req, res) {
        User.joinByUserId(req.params.id, res);
    });
    /**
     * @function get: /join_party/:id
     * Gets a list of all users associated with a party
     * @param {String} id
     * @return {json} A list of all users associated with a party
     */
    app.get('/join_party/:id', function(req, res) {
        Party.joinByPartyId(req.params.id, res);
    });
    /**
     * @function get: /comments
     * Gets all comments from the database
     * @return {json} A list of all comments in the database
     */
    app.get('/comments', function(req, res) {
        Comment.findAllComments(res);
    });
    /**
     * @function get: /get_comments/:id
     * Gets a list of comments based on party id
     * @param {String} id
     * @return {json} a list of comments
     */
    app.get('/get_comments/:id', function(req, res) {
        Comment.getAllCommentsByPartyId(req.params.id, res);
    });
    /**
     * @function get: /comments/:id
     * Gets a comment from the db
     * @param {String} id
     * @return {json} a comment
     */
    app.get('/comments/:id', function(req, res) {
        Comment.findCommentByID(req.params.id, res);
    });
    /**
     * @function post: /comments
     * creates a new comment in the db
     * @param {String} id
     * @param {String} username
     * @param {String} cmt
     * @return {json} a comment
     */
    app.post('/comments', function(req, res) {
        Comment.newComment(req.headers.party_id, req.headers.username, req.headers.text, res)
    });
    /**
     * @function delete: /comments/:id
     * deletes a comment by id
     * @param {String} id
     * @return {json} a comment
     */
    app.delete('/comments/:id', function(req, res) {
        Comment.deleteComment(req.params.id, res);
    });
    /**
     * @function post: /email
     * Sends an email with a qr code
     * @param {String} email
     * @param {String} username
     * @param {String} id
     * @return {json} success/fail
     */    
    app.post('/email', function(req, res) {
    User.email(req.headers.email, req.headers.username, req.headers.id, res);
    });
    /**
     * @function post: /text
     * Sends a text message with a qr code to a phone number
     * @param {String} number
     * @param {String} username
     * @param {String} id
     * @return {json} success/fail
     */  
    app.post('/text', function(req, res) {
        User.text(req.headers.number, req.headers.username, req.headers.id, res);
    });
}