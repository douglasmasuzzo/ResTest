// URL base das rotas do painel admin (intermediário — sem X-API-Key no front-end)
// O back-end recebe em /admin/mock e processa internamente via MockEndpointService.
// A X-API-Key nunca trafega entre navegador e servidor.
const API_URL = '/admin/mock';

// Referências aos elementos do DOM reutilizados em múltiplas funções
const createBtn    = document.getElementById('createBtn');
const reloadBtn    = document.getElementById('reloadBtn');
const endpointList = document.getElementById('endpointList');

const labelInput      = document.getElementById('label');
const statusCodeInput = document.getElementById('statusCode');
const delayMsInput    = document.getElementById('delayMs');
const payloadInput    = document.getElementById('payload');

// Armazena o ID do endpoint em edição; null significa modo de criação
let editingId = null;

// ─────────────────────────────────────────────────────────────────────────────
// Carregamento e renderização dos endpoints
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Busca todos os endpoints cadastrados no back-end e renderiza na tela.
 *
 * GET /admin/mock — sem X-API-Key (protegido por localhost-only no back-end).
 * O back-end retorna uma lista de EndpointResponse (JSON).
 */
async function loadEndpoints() {

    try {

        const response = await fetch(API_URL);

        // Verifica se o servidor retornou um status de erro
        if (!response.ok) {
            throw new Error(`Erro HTTP ${response.status}`);
        }

        const endpoints = await response.json();

        renderEndpoints(endpoints);

    } catch (error) {

        console.error('Erro ao carregar endpoints:', error);

        endpointList.innerHTML = `
            <div class="empty">
                Erro ao carregar endpoints. Verifique se o servidor está rodando.
            </div>
        `;
    }
}


/**
 * Renderiza a lista de endpoints no DOM.
 *
 * Cada card exibe:
 *   - Label (nome do endpoint)
 *   - Status code com cor indicativa
 *   - URL pública (relativa ao host atual — funciona em qualquer ambiente)
 *   - Delay configurado
 *   - Botões de editar e excluir
 *
 * @param {Array} endpoints lista de objetos EndpointResponse retornados pela API
 */
function renderEndpoints(endpoints) {

    endpointList.innerHTML = '';

    if (endpoints.length === 0) {

        endpointList.innerHTML = `
            <div class="empty">
                Nenhum endpoint criado ainda.
            </div>
        `;

        return;
    }

    endpoints.forEach(endpoint => {

        const div = document.createElement('div');
        div.className = 'endpoint-item';

        // Monta a URL pública usando o host atual em vez de localhost fixo.
        // Funciona tanto em desenvolvimento (localhost:8080) quanto em produção.
        const publicUrl = `${window.location.origin}/api/${endpoint.hash}`;

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
                ${publicUrl}
            </div>

            <div class="endpoint-meta">
                <span>Delay: ${endpoint.delayMs}ms</span>
            </div>

            <div class="actions">
                <button class="action-btn edit-btn">
                    Editar
                </button>

                <button class="action-btn delete-btn">
                    Excluir
                </button>
            </div>
        `;

        // Associa os eventos aos botões após inserir no DOM
        div.querySelector('.edit-btn').addEventListener('click', () => {
            fillForm(endpoint);
        });

        div.querySelector('.delete-btn').addEventListener('click', async () => {
            await deleteEndpoint(endpoint.id);
        });

        endpointList.appendChild(div);
    });
}

// ─────────────────────────────────────────────────────────────────────────────
// Edição
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Preenche o formulário com os dados do endpoint selecionado para edição.
 * Muda o botão de "Criar Endpoint" para "Salvar Alterações".
 *
 * @param {Object} endpoint objeto EndpointResponse com os dados atuais
 */
function fillForm(endpoint) {

    // Armazena o ID para que createOrUpdateEndpoint saiba que é um PUT
    editingId = endpoint.id;

    labelInput.value      = endpoint.label || '';
    statusCodeInput.value = endpoint.statusCode;
    delayMsInput.value    = endpoint.delayMs;

    // Formata o payload JSON para exibição legível no textarea
    try {
        payloadInput.value = JSON.stringify(JSON.parse(endpoint.payload), null, 2);
    } catch {
        // Se o payload não for JSON válido, exibe como está
        payloadInput.value = endpoint.payload;
    }

    createBtn.innerText = 'Salvar Alterações';

    // Rola até o formulário para o usuário perceber que entrou em modo de edição
    document.querySelector('.form-card').scrollIntoView({ behavior: 'smooth' });
}

// ─────────────────────────────────────────────────────────────────────────────
// Criação e atualização
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Cria um novo endpoint (POST) ou atualiza um existente (PUT),
 * dependendo se editingId está preenchido.
 *
 * Chama /admin/mock — sem X-API-Key.
 * O corpo enviado corresponde ao CreateEndpointRequest do back-end:
 *   { label, statusCode, delayMs, payload }
 */
async function createOrUpdateEndpoint() {

    // Valida o JSON antes de enviar para evitar erro 400 desnecessário
    let parsedPayload;

    try {
        parsedPayload = JSON.parse(payloadInput.value);
    } catch {
        alert('O payload não é um JSON válido. Corrija antes de salvar.');
        return;
    }

    // Monta o objeto conforme o CreateEndpointRequest do back-end
    const body = {
        label:      labelInput.value,
        statusCode: Number(statusCodeInput.value),
        delayMs:    Number(delayMsInput.value),
        payload:    JSON.stringify(parsedPayload)
    };

    try {

        if (editingId) {

            // PUT /admin/mock/{id} — atualiza endpoint existente
            await fetch(`${API_URL}/${editingId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(body)
            });

        } else {

            // POST /admin/mock — cria novo endpoint
            await fetch(API_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(body)
            });
        }

        // Limpa o formulário e recarrega a lista após salvar
        resetForm();
        await loadEndpoints();

    } catch (error) {

        console.error('Erro ao salvar endpoint:', error);
        alert('Erro ao salvar endpoint. Verifique o console para mais detalhes.');
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Exclusão
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Remove um endpoint pelo ID após confirmação do usuário.
 *
 * DELETE /admin/mock/{id} — sem X-API-Key.
 * O back-end retorna 204 No Content em caso de sucesso.
 *
 * @param {string} id UUID do endpoint a ser removido
 */
async function deleteEndpoint(id) {

    const confirmDelete = confirm('Deseja realmente excluir este endpoint?');

    if (!confirmDelete) {
        return;
    }

    try {

        await fetch(`${API_URL}/${id}`, {
            method: 'DELETE'
        });

        // Recarrega a lista após excluir
        await loadEndpoints();

    } catch (error) {

        console.error('Erro ao excluir endpoint:', error);
        alert('Erro ao excluir endpoint. Verifique o console para mais detalhes.');
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Utilitários do formulário
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Restaura o formulário ao estado inicial (modo de criação).
 * Limpa os campos e reseta o botão para "Criar Endpoint".
 */
function resetForm() {

    editingId = null;

    labelInput.value      = '';
    statusCodeInput.value = '200';
    delayMsInput.value    = '0';

    payloadInput.value = `{\n  "message": "Olá mundo"\n}`;

    createBtn.innerText = 'Criar Endpoint';
}

/**
 * Formata o conteúdo do textarea de payload como JSON indentado.
 * Exibe alerta caso o conteúdo não seja um JSON válido.
 */
function formatJson() {

    try {
        const parsed = JSON.parse(payloadInput.value);
        payloadInput.value = JSON.stringify(parsed, null, 4);
    } catch {
        alert('JSON inválido! Corrija o conteúdo antes de formatar.');
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Inicialização
// ─────────────────────────────────────────────────────────────────────────────

// Associa os eventos principais aos botões
createBtn.addEventListener('click', createOrUpdateEndpoint);
reloadBtn.addEventListener('click', loadEndpoints);

// Expõe formatJson globalmente para o onclick no HTML
window.formatJson = formatJson;

// Carrega os endpoints assim que a página é aberta
loadEndpoints();