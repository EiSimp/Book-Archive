const monthNames = ["January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"];

let currentDate = new Date();

document.getElementById('prev-btn').addEventListener('click', () => changeMonth(-1));
document.getElementById('next-btn').addEventListener('click', () => changeMonth(1));

function changeMonth(direction) {
    currentDate.setMonth(currentDate.getMonth() + direction);
    renderCalendar();
}

function renderCalendar() {
    const monthYear = document.getElementById('month-year');
    monthYear.textContent = `${monthNames[currentDate.getMonth()]} ${currentDate.getFullYear()}`;

    const calendarDates = document.getElementById('calendar-dates');
    calendarDates.innerHTML = '';

    const firstDay = new Date(currentDate.getFullYear(), currentDate.getMonth(), 1).getDay();
    const lastDate = new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 0).getDate();

    for (let i = 0; i < firstDay; i++) {
        const emptyDiv = document.createElement('div');
        calendarDates.appendChild(emptyDiv);
    }

    for (let date = 1; date <= lastDate; date++) {
        const dateDiv = document.createElement('div');
        dateDiv.textContent = date;

        if (date === new Date().getDate() &&
            currentDate.getMonth() === new Date().getMonth() &&
            currentDate.getFullYear() === new Date().getFullYear()) {
            dateDiv.classList.add('today');
        }

        calendarDates.appendChild(dateDiv);
    }
}

renderCalendar();