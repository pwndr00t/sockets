var name = getQueryVariable('name') || 'Anonymus';
var room = getQueryVariable('room');
console.log(name + " wants to join " + room);
var socket = io();

var $message = jQuery('.room-title').text(room);


socket.on('connect', function () {
    console.log('Connected to server');
    socket.emit('joinRoom', {
        name: name,
        room: room
    });
});

socket.on('message', function (message) {
    var timestamp = moment.utc(message.timestamp);
    var $message = jQuery('.messages');
    console.log('New Message:');
    console.log(message.text);
    $message.append("<p><strong>" + message.name + " " + timestamp.local().format("h:mm a") + " </strong><p>");
    $message.append("<p>" + message.text + "<p>");
});

// handles submiting of new message
var $form = jQuery('#message-form');
var $msg = $form.find('input[name=message]');
$form.on('submit', function (event) {
    event.preventDefault();
    socket.emit('message', {
        name: name,
        text: $msg.val()
    });
    $msg.val('');
});
