/* 
   ==================================================
   CONFIGURAÇÃO DA API
   A URL aponta para o 'MockEndpointController' no Spring Boot.
   ================================================== 
*/
const API_URL = '/mock';

// Referências da Interface (DOM)
const createBtn = document.getElementById('createBtn');
const reloadBtn = document.getElementById('reloadBtn');
const endpointList = document.getElementById('endpointList');

const labelInput = document.getElementById('label');
const statusCodeInput = document.getElementById('statusCode');
const delayMsInput = document.getElementById('delayMs');
const payloadInput = document.getElementById('payload');

// Controle de estado: Armazena o UUID da entidade MockEndpoint durante a edição
let editingId = null;

/* 
   ==================================================
   CARREGAMENTO DE DADOS (GET /mock)
   Consome o método findAll() do MockEndpointService.
   ================================================== 
*/
async function loadEndpoints() {
    try {
        const response = await fetch(API_URL);
        const endpoints = await response.json(); // Recebe lista de 'EndpointResponse'
        renderEndpoints(endpoints);
    } catch (error) {
        console.error('Erro ao carregar:', error);
        endpointList.innerHTML = `<div class="empty">Erro ao conectar com o servidor.</div>`;
    }
}

/* 
   ==================================================
   RENDERIZAÇÃO DA LISTAGEM
   Transforma os dados do Backend em elementos visuais.
   ================================================== 
*/
function renderEndpoints(endpoints) {
    endpointList.innerHTML = '';

    if (endpoints.length === 0) {
        endpointList.innerHTML = `<div class="empty">Nenhum endpoint criado.</div>`;
        return;
    }

    endpoints.forEach(endpoint => {
        const div = document.createElement('div');
        div.className = 'endpoint-item';

        // O 'hash' é gerado pelo 'HashGeneratorService' no Backend
        div.innerHTML = `
            <div class="endpoint-top">
                <div class="endpoint-label">${endpoint.label || 'Sem nome'}</div>
                <div class="status status-${endpoint.statusCode}">${endpoint.statusCode}</div>
            </div>
            <div class="endpoint-url">
                http://localhost:8080/api/${endpoint.hash}
            </div>
            <div class="endpoint-actions">
                <button class="action-btn edit-btn">Editar</button>
                <button class="action-btn delete-btn">Excluir</button>
            </div>
        `;

        // Listeners para ações de CRUD
        div.querySelector('.edit-btn').addEventListener('click', () => fillForm(endpoint));
        div.querySelector('.delete-btn').addEventListener('click', () => deleteEndpoint(endpoint.id));

        endpointList.appendChild(div);
    });
}

/* 
   ==================================================
   PREPARAÇÃO PARA EDIÇÃO
   Preenche o formulário com dados da entidade selecionada.
   ================================================== 
*/
function fillForm(endpoint) {
    editingId = endpoint.id; // UUID necessário para o método update() no Controller
    labelInput.value = endpoint.label || '';
    statusCodeInput.value = endpoint.statusCode;
    delayMsInput.value = endpoint.delayMs;

    try {
        payloadInput.value = JSON.stringify(JSON.parse(endpoint.payload), null, 2);
    } catch {
        payloadInput.value = endpoint.payload;
    }

    createBtn.innerText = 'Salvar Alterações';
}

/* 
   ==================================================
   CRIAÇÃO OU ATUALIZAÇÃO (POST/PUT /mock)
   Envia os dados modelados no 'CreateEndpointRequest' (DTO).
   ================================================== 
*/
async function createOrUpdateEndpoint() {
    let parsedPayload;
    try {
        parsedPayload = JSON.parse(payloadInput.value);
    } catch {
        alert('JSON inválido! Verifique a sintaxe.');
        return;
    }

    // Objeto 'body' mapeado para o DTO 'CreateEndpointRequest' no Java
    const body = {
        label: labelInput.value,
        statusCode: Number(statusCodeInput.value),
        delayMs: Number(delayMsInput.value),
        payload: JSON.stringify(parsedPayload)
    };

    try {
        if (editingId) {
            // Chamada PUT: MockEndpointController.update(UUID id, CreateEndpointRequest req)
            await fetch(`${API_URL}/${editingId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(body)
            });
        } else {
            // Chamada POST: MockEndpointController.create(CreateEndpointRequest req)
            await fetch(API_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(body)
            });
        }

        resetForm();
        await loadEndpoints();
    } catch (error) {
        alert('Erro ao salvar no banco de dados.');
    }
}

/* 
   ==================================================
   EXCLUSÃO (DELETE /mock/{id})
   Consome o método delete() do MockEndpointRepository (via Service).
   ================================================== 
*/
async function deleteEndpoint(id) {
    if (!confirm('Deseja realmente excluir este endpoint?')) return;

    try {
        await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
        await loadEndpoints();
    } catch (error) {
        alert('Erro ao excluir registro.');
    }
}

/* 
   ==================================================
   UTILITÁRIOS DA INTERFACE
   ================================================== 
*/
function resetForm() {
    editingId = null;
    labelInput.value = '';
    statusCodeInput.value = '200';
    delayMsInput.value = '0';
    payloadInput.value = '{\n  "message": "Olá mundo"\n}';
    createBtn.innerText = 'Criar Endpoint';
}

function formatJson() {
    try {
        const parsed = JSON.parse(payloadInput.value);
        payloadInput.value = JSON.stringify(parsed, null, 4);
    } catch (error) {
        alert("JSON inválido para formatação!");
    }
}

// Inicialização dos Event Listeners
createBtn.addEventListener('click', createOrUpdateEndpoint);
reloadBtn.addEventListener('click', loadEndpoints);

// Carga inicial (Marco 1: Sincronização automática ao abrir a página)
loadEndpoints();
