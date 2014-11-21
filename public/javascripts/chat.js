$(function() {
    var changeRoom = '';

    $('#chat').hide();
    $('#alert').hide();
    var hub = $.connection.chatHub;

    hub.client.sendMessage = function(username, message) {
        var el = $('<div class="message"><span></span><p></p></div>');
        $("span", el).text(username + ': ');
        $("p", el).text(message);
        $(el).addClass("talk");
        $('#messages').append(el);
    };

    hub.client.userJoined = function(username) {
        var li = document.createElement('li');
        li.textContent = username;
        $("#members").append(li);
    };

    hub.client.userList = function(users) {
        $("#members").html('');
        $(users).each(function() {
            var li = document.createElement('li');
            li.textContent = this;
            $("#members").append(li);
        });
    };

    hub.client.roomList = function(rooms) {
        var room = '';
        if(changeRoom == '') {
            var optionSelected = $('#roomselect').find("option:selected");
            room = optionSelected.text();
        } else {
            room = changeRoom;
            changeRoom = '';
        }
        $('#roomselect').find('option').remove().end();
        $.each(rooms, function( index, value ) {
            $('#roomselect').append(new Option(value, value));
        });
        $('#roomselect').val(room);
    };

    hub.client.userLeftRoom = function(username) {
        var el = $('<div class="message"><span></span><p></p></div>');
        $("span", el).text(username);
        $("p", el).text("has left the room");
        $(el).addClass("join");
        $('#messages').append(el);
    };

    hub.client.userJoinedRoom = function(username) {
        var el = $('<div class="message"><span></span><p></p></div>');
        $("span", el).text(username);
        $("p", el).text("has joined the room");
        $(el).addClass("join");
        $('#messages').append(el);
    };

    $.connection.hub.start().done(function () {
        $('#home').click(function() {
            hub.server.logout();
            $('#chat').hide();
            $('#login').show();
        });

        $('#loginSubmit').click(function(event) {
            event.preventDefault();
            hub.server.login($('#inputUsername').val()).done(function(result) {
                if(result) {
                    $('#login').hide();
                    $('#chat').show();
                    $('#inputUsername').val('');
                    $('#alert').hide();
                } else {
                    $('#alert').show();
                }
            });
        });

        $('#roomselect').change(function () {
            var optionSelected = $(this).find("option:selected");
            var room = optionSelected.text();
            hub.server.joinRoom(room).done(function() {
                changedRoom(room);
            });
        });

        $('#create').click(function(event) {
            event.preventDefault();
            changeRoom = $('#roomname').val();
            hub.server.createRoom($('#roomname').val()).done(function() {
                changedRoom(changeRoom);
                $('#roomname').val('');
            });
        });

        var sendMessage = function() {
            hub.server.sendMessage($('#roomselect').val(), $("#talk").val());
            var el = $('<div class="message"><span></span><p></p></div>');
            $("span", el).text('Me: ');
            $("p", el).text($("#talk").val());
            $(el).addClass('talk');
            $(el).addClass('me');
            $('#messages').append(el);
            $("#talk").val('');
        };

        var changedRoom = function(room) {
            var el = $('<div class="message"><span></span><p></p></div>');
            $("span", el).text("You");
            $("p", el).text("have joined room " + room);
            $(el).addClass("join");
            $('#messages').append(el);
        };

        var handleReturnKey = function(e) {
            if(e.charCode == 13 || e.keyCode == 13) {
                e.preventDefault();
                sendMessage();
            }
        };

        $("#talk").keypress(handleReturnKey);
    });
});