const API_URL = '/mock';

const createBtn = document.getElementById('createBtn');
const reloadBtn = document.getElementById('reloadBtn');
const endpointList = document.getElementById('endpointList');

const labelInput = document.getElementById('label');
const statusCodeInput = document.getElementById('statusCode');
const delayMsInput = document.getElementById('delayMs');
const payloadInput = document.getElementById('payload');

let editingId = null;

async function loadEndpoints() {

    try {

        const response = await fetch(API_URL);
        const endpoints = await response.json();

        renderEndpoints(endpoints);

    } catch (error) {

        console.error(error);

        endpointList.innerHTML = `
            <div class="empty">
                Erro ao carregar endpoints
            </div>
        `;
    }
}


function renderEndpoints(endpoints) {

    endpointList.innerHTML = '';

    if (endpoints.length === 0) {

        endpointList.innerHTML = `
            <div class="empty">
                Nenhum endpoint criado
            </div>
        `;

        return;
    }

    endpoints.forEach(endpoint => {

        const div = document.createElement('div');

        div.className = 'endpoint-item';

        div.innerHTML = `

            <div class="endpoint-top">
                <div class="endpoint-label">
                    ${endpoint.label || 'Sem nome'}
                </div>

                <div class="status status-${endpoint.statusCode}">
                    ${endpoint.statusCode}
                </div>
            </div>

            <div class="endpoint-url">
                http://localhost:8080/api/${endpoint.hash}
            </div>

            <div class="endpoint-actions">
                <button class="action-btn edit-btn">
                    Editar
                </button>

                <button class="action-btn delete-btn">
                    Excluir
                </button>
            </div>
        `;

        const editBtn = div.querySelector('.edit-btn');
        const deleteBtn = div.querySelector('.delete-btn');

        editBtn.addEventListener('click', () => {
            fillForm(endpoint);
        });

        deleteBtn.addEventListener('click', async () => {
            await deleteEndpoint(endpoint.id);
        });

        endpointList.appendChild(div);
    });
}


function fillForm(endpoint) {

    editingId = endpoint.id;

    labelInput.value = endpoint.label || '';
    statusCodeInput.value = endpoint.statusCode;
    delayMsInput.value = endpoint.delayMs;

    payloadInput.value = JSON.stringify(
        JSON.parse(endpoint.payload),
        null,
        2
    );

    createBtn.innerText = 'Salvar Alterações';
}


async function createOrUpdateEndpoint() {

    let parsedPayload;

    try {

        parsedPayload = JSON.parse(payloadInput.value);

    } catch {

        alert('JSON inválido');
        return;
    }

    const body = {
        label: labelInput.value,
        statusCode: Number(statusCodeInput.value),
        delayMs: Number(delayMsInput.value),
        payload: JSON.stringify(parsedPayload)
    };

    try {

        if (editingId) {

            await fetch(`${API_URL}/${editingId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(body)
            });

        } else {

            await fetch(API_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(body)
            });
        }

        resetForm();
        await loadEndpoints();

    } catch (error) {

        console.error(error);
        alert('Erro ao salvar endpoint');
    }
}


async function deleteEndpoint(id) {

    const confirmDelete = confirm('Deseja realmente excluir este endpoint?');

    if (!confirmDelete) {
        return;
    }

    try {

        await fetch(`${API_URL}/${id}`, {
            method: 'DELETE'
        });

        await loadEndpoints();

    } catch (error) {

        console.error(error);
        alert('Erro ao excluir endpoint');
    }
}


function resetForm() {

    editingId = null;

    labelInput.value = '';
    statusCodeInput.value = '200';
    delayMsInput.value = '0';

    payloadInput.value = `{
  "message": "Olá mundo"
}`;

    createBtn.innerText = 'Criar Endpoint';
}

function formatJson() {

    const textarea = document.getElementById("payload");

    try {

        const parsed = JSON.parse(textarea.value);

        textarea.value = JSON.stringify(parsed, null, 4);

    } catch (error) {

        alert("JSON inválido!");

    }

}

createBtn.addEventListener('click', createOrUpdateEndpoint);
reloadBtn.addEventListener('click', loadEndpoints);

loadEndpoints();