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
    fetchEventsForMonth(currentDate.getFullYear(), currentDate.getMonth() + 1);
}

function fetchEventsForMonth(year, month) {
    const bookClubId = document.getElementById('bookClubId').value;
    fetch(`/api/events/bookclub/${bookClubId}?year=${year}&month=${month}`)
        .then(response => response.json())
        .then(events => {
            events.sort((a, b) => new Date(a.dateTime) - new Date(b.dateTime)); // Sort events by date
            displayEvents(events);
        })
        .catch(error => console.error('Error fetching events:', error));
}

function displayEvents(events) {
    const eventList = document.getElementById('event-list');
    eventList.innerHTML = '';

    events.forEach(event => {
        const eventItem = document.createElement('li');
        eventItem.textContent = `${event.title} - ${new Date(event.dateTime).toLocaleString()}`;
        eventList.appendChild(eventItem);

        const eventDate = new Date(event.dateTime).getDate();
        const dateDivs = document.querySelectorAll('.calendar-dates > div');
        dateDivs.forEach(div => {
            if (parseInt(div.textContent) === eventDate) {
                div.classList.add('event');
                div.title = event.title;
                if (!div.events) {
                    div.events = [];
                }
                div.events.push(event);

                div.classList.add('clickable');
                div.onclick = function () {
                    openEventOptionsModal(div.events);
                };
            }
        });
    });
}


function openEventOptionsModal(events) {
    const userId = document.getElementById("userId").value;
    const managerId = document.getElementById("managerId").value;
    const optionsModal = document.createElement('div');
    optionsModal.classList.add('modal');
    let modalContent = `
        <div class="modal-content">
            <span class="close" onclick="closeEventOptionsModal()">&times;</span>
            <h2>Events</h2>
            <ul>
    `;

    events.forEach(event => {
        modalContent += `
            <li>
                ${event.title} - ${new Date(event.dateTime).toLocaleString()}
                <p>--${event.description}</p>
        `;
        if (userId === managerId) {
            modalContent += `
                <button onclick="closeEventOptionsModal(); openUpdateEventModal(${event.id}, '${event.title}', '${event.description}', '${event.dateTime}')">Update</button>
                <button onclick="closeEventOptionsModal(); openDeleteEventModal(${event.id})">Delete</button>
            `;
        }
        modalContent += `</li>`;
    });

    modalContent += `
            </ul>
        </div>
    `;

    optionsModal.innerHTML = modalContent;
    document.body.appendChild(optionsModal);
    optionsModal.style.display = 'block';

    window.closeEventOptionsModal = function () {
        optionsModal.remove();
    };
}

renderCalendar();
