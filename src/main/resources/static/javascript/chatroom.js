document.addEventListener("DOMContentLoaded", function () {
    console.log("DOM fully loaded and parsed");

    const sendButton = document.getElementById('send-btn');
    const chatInput = document.getElementById('chat-input');

    // Add event listener for the Send button
    sendButton.addEventListener('click', function() {
        console.log("Send button clicked"); // Debugging log
        const content = chatInput.value;
        if (content.trim() !== '') {
            sendMessage(content);
            chatInput.value = '';
        }
    });

    // Add event listener for the Enter key
    chatInput.addEventListener('keydown', function(event) {
        if (event.key === 'Enter') {
            console.log("Enter key pressed");
            event.preventDefault(); // Prevent the default action to avoid form submission
            const content = chatInput.value;
            if (content.trim() !== '') {
                sendMessage(content);
                chatInput.value = '';
            }
        }
    });

    function getCsrfToken() {
        const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
        return { csrfToken, csrfHeader };
    }

    function getCurrentUserId() {
        return document.getElementById('userId').value;
    }

    function getCurrentUsername() {
        return document.querySelector('meta[name="username"]').getAttribute('content');
    }

    function getBookClubId() {
        return document.getElementById('bookClubId').value;
    }

    // WebSocket initialization
    const socket = new SockJS('/chat-websocket');
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        const bookClubId = getBookClubId();
        stompClient.subscribe(`/topic/messages/${bookClubId}`, function (messageOutput) {
            const message = JSON.parse(messageOutput.body);
            console.log('Received message:', message); // Debugging log
            handleMessage(message);
        });
    });

    function sendMessage(content) {
        console.log("Sending message: ", content); // Debugging log
        const message = {
            bookClubId: getBookClubId(),
            userId: getCurrentUserId(),
            content: content,
            action: 'create'
        };

        // Send the message via WebSocket
        stompClient.send("/app/sendMessage", {}, JSON.stringify(message));
    }

    function handleMessage(message) {
        if (message.action === 'create') {
            addMessageToChatBox(message);
        } else if (message.action === 'edit') {
            updateMessageInChatBox(message);
        } else if (message.action === 'delete') {
            removeMessageFromChatBox(message);
        }
    }

    function addMessageToChatBox(message) {
        const chatBox = document.getElementById('chat-box');
        const messageElement = document.createElement('div');
        const isMine = message.userId == getCurrentUserId();
        messageElement.className = isMine ? 'message mine' : 'message';
        messageElement.dataset.messageId = message.messageId;
        messageElement.innerHTML = `
            <div class="header">
                <img src="/images/GreyRobot.jpg" alt="${isMine ? 'Me' : 'User'}'s profile picture">
                <div class="details">
                    <span class="timestamp">${message.sentAt}</span>
                    <span class="username">${isMine ? 'Me' : 'User'}</span>
                </div>
            </div>
            <p class="text">${message.content}</p>
            <div class="popup-bar">
                <span class="edit-message">✏️</span>
                <span class="delete-message">🗑️</span>
            </div>
            <div class="reactions">
                <span class="reaction">👍</span>
                <span class="reaction">❤️</span>
                <span class="reaction">😂</span>
                <span class="reaction">😮</span>
                <span class="reaction">😢</span>
                <span class="reaction">😡</span>
            </div>
        `;
        chatBox.appendChild(messageElement);
        chatBox.scrollTop = chatBox.scrollHeight; // Scroll to the bottom

        const editButton = messageElement.querySelector('.edit-message');
        const deleteButton = messageElement.querySelector('.delete-message');

        editButton.addEventListener('click', () => editMessage(message.messageId, messageElement));
        deleteButton.addEventListener('click', () => deleteMessage(message.messageId, messageElement));
    }

    function updateMessageInChatBox(message) {
        const messageElement = document.querySelector(`div[data-message-id='${message.messageId}']`);
        if (messageElement) {
            const textElement = messageElement.querySelector('.text');
            textElement.innerText = message.content;
        }
    }

    function removeMessageFromChatBox(message) {
        const messageElement = document.querySelector(`div[data-message-id='${message.messageId}']`);
        if (messageElement) {
            messageElement.remove();
        }
    }

    function editMessage(messageId, messageElement) {
        const textElement = messageElement.querySelector('.text');
        const originalText = textElement.innerText;
        const input = document.createElement('input');
        input.type = 'text';
        input.value = originalText;
        input.className = 'edit-input';

        input.addEventListener('keydown', (event) => {
            if (event.key === 'Enter') {
                const newContent = input.value;
                if (newContent) {
                    textElement.innerText = newContent;
                    messageElement.replaceChild(textElement, input);

                    const message = {
                        messageId: messageId,
                        content: newContent,
                        bookClubId: getBookClubId(),
                        userId: getCurrentUserId(),
                        action: 'edit'
                    };
                    stompClient.send("/app/editMessage", {}, JSON.stringify(message));
                } else {
                    messageElement.replaceChild(textElement, input);
                }
            }
        });

        messageElement.replaceChild(input, textElement);
        input.focus();
    }

    function deleteMessage(messageId, messageElement) {
        if (confirm("Are you sure you want to delete this message?")) {
            messageElement.remove();

            const message = {
                messageId: messageId,
                bookClubId: getBookClubId(),
                userId: getCurrentUserId(),
                action: 'delete'
            };
            stompClient.send("/app/deleteMessage", {}, JSON.stringify(message));
        }
    }

    function fetchMessages() {
        const bookClubId = getBookClubId();
        fetch(`/messages/bookclub/${bookClubId}`)
            .then(response => response.json())
            .then(data => {
                if (data) {
                    const chatBox = document.getElementById('chat-box');
                    chatBox.innerHTML = ''; // Clear existing messages
                    data.forEach(message => addMessageToChatBox(message));
                } else {
                    console.error('Failed to fetch messages.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
    }

    // Fetch messages when the page loads
    fetchMessages();
});
