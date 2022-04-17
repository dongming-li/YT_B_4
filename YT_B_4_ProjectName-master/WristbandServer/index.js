var express = require('express');
var mysqlModel = require('mysql-model');
var app = express();
var config = require('./config');
var jwt = require('jwt-simple');
var moment = require('moment');
var User = require('./controllers/user_controller')(app);
var Party = require('./controllers/party_controller');
var Comment = require('./controllers/comment_controller');
var serveIndex = require('serve-index');

require('./routes')(app);
app.set('jwtTokenSecret', config.crypt);
app.use(express.static(__dirname + "/"))
app.use('/images', serveIndex(__dirname + '/images'));

app.listen(config.port);
console.log('Listening on port:', config.port);