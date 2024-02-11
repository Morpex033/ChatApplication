document.addEventListener("DOMContentLoaded", function() {
    // Обработчики событий и другие действия на странице
    
    // Пример обработчика события для клика по чату из списка
    const chatListItems = document.querySelectorAll('.chat-list li');
    chatListItems.forEach(item => {
        item.addEventListener('click', () => {
            // Действия при выборе чата из списка
        });
    });

    // Пример обработчика события для отправки сообщения
    const messageForm = document.querySelector('.chat form');
    messageForm.addEventListener('submit', (event) => {
        event.preventDefault();
        const messageInput = document.querySelector('.chat form input[type="text"]');
        const message = messageInput.value;
        // Действия при отправке сообщения
        messageInput.value = ''; // Очистить поле ввода после отправки
    });

    // Пример обработчика события для просмотра всех участников чата
    const viewParticipantsButton = document.querySelector('.chat-info button');
    viewParticipantsButton.addEventListener('click', () => {
        // Действия при нажатии кнопки "Посмотреть всех участников"
    });
});
