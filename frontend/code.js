/* ==================================================
   URL BASE DA API
   Endpoint responsável pelo gerenciamento dos mocks.
================================================== */
const API_URL = 'http://localhost:8080/mock';

/* ==================================================
   REFERÊNCIAS DOS ELEMENTOS DA INTERFACE
================================================== */

// Botão para criar ou atualizar endpoint
const createBtn = document.getElementById('createBtn');

// Botão para recarregar a lista
const reloadBtn = document.getElementById('reloadBtn');

// Container onde os endpoints serão exibidos
const endpointList = document.getElementById('endpointList');

// Campos do formulário
const labelInput = document.getElementById('label');
const statusCodeInput = document.getElementById('statusCode');
const delayMsInput = document.getElementById('delayMs');
const payloadInput = document.getElementById('payload');

/* ==================================================
   CONTROLE DE EDIÇÃO
   Armazena o ID do endpoint que está sendo editado.
   Se for null, significa que será criado um novo.
================================================== */
let editingId = null;

/* ==================================================
   CARREGA TODOS OS ENDPOINTS DA API
================================================== */
async function loadEndpoints() {

    try {

        // Busca os endpoints cadastrados
        const response = await fetch(API_URL);

        // Converte a resposta para JSON
        const endpoints = await response.json();

        // Renderiza a lista na tela
        renderEndpoints(endpoints);

    } catch (error) {

        console.error(error);

        // Exibe mensagem de erro
        endpointList.innerHTML = `
            <div class="empty">
                Erro ao carregar endpoints
            </div>
        `;
    }
}

/* ==================================================
   RENDERIZA A LISTA DE ENDPOINTS
================================================== */
function renderEndpoints(endpoints) {

    // Limpa a lista atual
    endpointList.innerHTML = '';

    // Caso não existam endpoints cadastrados
    if (endpoints.length === 0) {

        endpointList.innerHTML = `
            <div class="empty">
                Nenhum endpoint criado
            </div>
        `;

        return;
    }

    // Percorre todos os endpoints recebidos
    endpoints.forEach(endpoint => {

        // Cria um card para cada endpoint
        const div = document.createElement('div');

        div.className = 'endpoint-item';

        div.innerHTML = `

            <div class="endpoint-top">

                <!-- Nome do endpoint -->
                <div class="endpoint-label">
                    ${endpoint.label || 'Sem nome'}
                </div>

                <!-- Código HTTP -->
                <div class="status status-${endpoint.statusCode}">
                    ${endpoint.statusCode}
                </div>

            </div>

            <!-- URL gerada -->
            <div class="endpoint-url">
                http://localhost:8080/api/${endpoint.hash}
            </div>

            <!-- Botões de ação -->
            <div class="endpoint-actions">

                <button class="action-btn edit-btn">
                    Editar
                </button>

                <button class="action-btn delete-btn">
                    Excluir
                </button>

            </div>
        `;

        // Obtém os botões do card
        const editBtn = div.querySelector('.edit-btn');
        const deleteBtn = div.querySelector('.delete-btn');

        // Evento de edição
        editBtn.addEventListener('click', () => {
            fillForm(endpoint);
        });

        // Evento de exclusão
        deleteBtn.addEventListener('click', async () => {
            await deleteEndpoint(endpoint.id);
        });

        // Adiciona o card à tela
        endpointList.appendChild(div);
    });
}

/* ==================================================
   PREENCHE O FORMULÁRIO PARA EDIÇÃO
================================================== */
function fillForm(endpoint) {

    // Define o endpoint atual como sendo editado
    editingId = endpoint.id;

    // Preenche os campos
    labelInput.value = endpoint.label || '';
    statusCodeInput.value = endpoint.statusCode;
    delayMsInput.value = endpoint.delayMs;

    // Formata o JSON para melhor leitura
    payloadInput.value = JSON.stringify(
        JSON.parse(endpoint.payload),
        null,
        2
    );

    // Altera o texto do botão principal
    createBtn.innerText = 'Salvar Alterações';
}

/* ==================================================
   CRIA OU ATUALIZA UM ENDPOINT
================================================== */
async function createOrUpdateEndpoint() {

    let parsedPayload;

    try {

        // Valida se o JSON é válido
        parsedPayload = JSON.parse(payloadInput.value);

    } catch {

        alert('JSON inválido');
        return;
    }

    // Monta o objeto enviado para a API
    const body = {
        label: labelInput.value,
        statusCode: Number(statusCodeInput.value),
        delayMs: Number(delayMsInput.value),
        payload: JSON.stringify(parsedPayload)
    };

    try {

        // Se existe um ID, atualiza o endpoint
        if (editingId) {

            await fetch(`${API_URL}/${editingId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(body)
            });

        } else {

            // Caso contrário cria um novo endpoint
            await fetch(API_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(body)
            });
        }

        // Limpa o formulário
        resetForm();

        // Atualiza a lista
        await loadEndpoints();

    } catch (error) {

        console.error(error);

        alert('Erro ao salvar endpoint');
    }
}

/* ==================================================
   EXCLUI UM ENDPOINT
================================================== */
async function deleteEndpoint(id) {

    // Solicita confirmação do usuário
    const confirmDelete = confirm(
        'Deseja realmente excluir este endpoint?'
    );

    if (!confirmDelete) {
        return;
    }

    try {

        // Remove o endpoint na API
        await fetch(`${API_URL}/${id}`, {
            method: 'DELETE'
        });

        // Atualiza a lista
        await loadEndpoints();

    } catch (error) {

        console.error(error);

        alert('Erro ao excluir endpoint');
    }
}

/* ==================================================
   RESETA O FORMULÁRIO PARA O ESTADO INICIAL
================================================== */
function resetForm() {

    // Sai do modo de edição
    editingId = null;

    // Limpa os campos
    labelInput.value = '';
    statusCodeInput.value = '200';
    delayMsInput.value = '0';

    // JSON padrão exibido no textarea
    payloadInput.value = `{
  "message": "Olá mundo"
}`;

    // Restaura o texto original do botão
    createBtn.innerText = 'Criar Endpoint';
}

/* ==================================================
   FORMATA O JSON INSERIDO NO TEXTAREA
================================================== */
function formatJson() {

    // Obtém o campo de payload
    const textarea = document.getElementById("payload");

    try {

        // Converte o JSON em objeto
        const parsed = JSON.parse(textarea.value);

        // Reescreve formatado com indentação
        textarea.value = JSON.stringify(
            parsed,
            null,
            4
        );

    } catch (error) {

        // Exibe erro caso o JSON seja inválido
        alert("JSON inválido!");
    }
}

/* ==================================================
   EVENTOS DA INTERFACE
================================================== */

// Clique no botão criar/salvar
createBtn.addEventListener(
    'click',
    createOrUpdateEndpoint
);

// Clique no botão atualizar
reloadBtn.addEventListener(
    'click',
    loadEndpoints
);

/* ==================================================
   INICIALIZAÇÃO DA APLICAÇÃO
   Carrega os endpoints assim que a página abre.
================================================== */
loadEndpoints();