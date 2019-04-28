var fs = require('fs')
var express = require('express')
var app = express()
var bodyParser = require('body-parser')

app.use(express.static(__dirname))
app.use(bodyParser.urlencoded({
	extended : false
}))
app.use(bodyParser.json())
var serverPort = app.listen(3000)

app.get('/', function(req, res) {
    rstream = fs.createReadStream(__dirname + '/chat.html');
	rstream.pipe(res);
})

var io = require('socket.io').listen(serverPort)

io.sockets.on('connection', function(socket) {
    console.log(`${socket.id} connected`)

	socket.on('message', function(data) {
		io.sockets.emit('message', data)
    })
    
    socket.on('disconnect', function () {
        console.log(`${socket.id} disconnected`)
    })
})