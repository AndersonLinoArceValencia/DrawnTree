// Variables globales
const container = document.getElementById('network');
let network = null;
let lastHighlightedNode = null;
let lastHighlightedNodeColor = null;

// Función para actualizar el estado en la UI
function updateStatus(message, type = 'info') {
    const statusElement = document.getElementById('status');
    statusElement.innerHTML = `<strong>Estado:</strong> ${message}`;
    statusElement.className = `status-box alert alert-${type}`;
}

// Función para convertir la estructura del árbol B a datos de vis.js
function convertTreeToVisData(tree, highlightValue = null) {
    const nodes = [];
    const edges = [];
    let nodeId = 0;
    const nodeMap = {};

    function processNode(node, parentId = null, level = 0) {
        if (!node) return null;

        const currentNodeId = `n${nodeId++}`;
        const isRoot = parentId === null;
        const isHighlighted = node.claves && node.claves.includes(parseInt(highlightValue));

        // Almacenar mapeo de claves a nodo
        if (node.claves) {
            node.claves.forEach(key => {
                nodeMap[key] = currentNodeId;
            });
        }

        // Crear nodo
        nodes.push({
            id: currentNodeId,
            label: node.claves ? node.claves.join(' | ') : '',
            level: level,
            color: {
                background: isRoot ? '#1cc88a' : isHighlighted ? '#f6c23e' : '#4e73df',
                border: isRoot ? '#17a673' : isHighlighted ? '#dda20a' : '#2e59d9'
            },
            font: {
                color: '#ffffff',
                size: 14,
                face: 'Arial'
            },
            shape: 'box',
            margin: 10,
            borderWidth: 2,
            shadow: true
        });

        // Conectar con padre
        if (parentId) {
            edges.push({
                from: parentId,
                to: currentNodeId,
                arrows: 'to',
                width: 2,
                color: {
                    color: '#858796',
                    highlight: '#2e59d9'
                }
            });
        }

        // Procesar hijos
        if (node.hijos) {
            node.hijos.forEach(child => {
                processNode(child, currentNodeId, level + 1);
            });
        }

        return currentNodeId;
    }

    processNode(tree.root || tree);
    return { nodes, edges, nodeMap };
}

// Función para cargar y visualizar el árbol
async function loadTree(highlightValue = null) {
    updateStatus('Cargando estructura del árbol...', 'info');

    try {
        const response = await fetch('/api/btree/listar');
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

        const data = await response.json();
        if (data.status !== 'success') throw new Error(data.message || 'Error en los datos del árbol');

        // Manejar árbol vacío
        if (!data.tree || Object.keys(data.tree).length === 0) {
            updateStatus('El árbol está vacío', 'info');
            if (network) {
                network.setData({
                    nodes: new vis.DataSet([]),
                    edges: new vis.DataSet([])
                });
            }
            updateTreeInfo(null);
            return;
        }

        const { nodes, edges, nodeMap } = convertTreeToVisData(data.tree, highlightValue);

        // Configuración de vis.js
        const options = {
            physics: false,
            layout: {
                hierarchical: {
                    direction: 'UD',
                    nodeSpacing: 150,
                    levelSeparation: 120
                }
            },
            nodes: {
                shape: 'box',
                margin: 10,
                font: {
                    size: 14,
                    face: 'Arial'
                },
                borderWidth: 2,
                shadow: true
            },
            edges: {
                width: 2,
                smooth: {
                    type: 'cubicBezier'
                }
            }
        };

        if (network) network.destroy();
        network = new vis.Network(container, {
            nodes: new vis.DataSet(nodes),
            edges: new vis.DataSet(edges)
        }, options);

        // Evento click en nodos
        network.on('click', function(params) {
            if (params.nodes.length > 0) {
                const nodeId = params.nodes[0];
                const node = network.body.nodes[nodeId];
                document.getElementById('valor').value = node.options.label.split(' | ')[0];
            }
        });

        // Ajustar vista
        network.fit({
            animation: {
                duration: 1000,
                easingFunction: 'easeInOutQuad'
            }
        });

        updateTreeInfo(data);

        // Resaltar nodo si es necesario
        if (highlightValue && nodeMap[highlightValue]) {
            highlightNode(nodeMap[highlightValue], true);
        }

    } catch (error) {
        console.error('Error al cargar el árbol:', error);
        updateStatus(`Error al cargar el árbol: ${error.message}`, 'danger');

        // Mostrar mensaje de error en el contenedor
        container.innerHTML = `
            <div style="padding: 20px; color: #721c24; background-color: #f8d7da; border: 1px solid #f5c6cb; border-radius: 4px;">
                <h4>Error al cargar el árbol</h4>
                <p>${error.message}</p>
                <p>Verifica la consola para más detalles.</p>
            </div>
        `;
    }
}

// Función para actualizar información del árbol en la UI
function updateTreeInfo(data) {
    let rootKeys = '-';
    if (data && data.tree) {
        if (data.tree.root && data.tree.root.claves) {
            rootKeys = data.tree.root.claves.join(', ');
        } else if (data.tree.claves) {
            rootKeys = data.tree.claves.join(', ');
        }
    }

    document.getElementById('raiz-actual').textContent = rootKeys;
    document.getElementById('tamano-arbol').textContent = (data && data.size) ? data.size : '0';
    document.getElementById('grado-arbol').textContent = (data && data.grado) ? data.grado : '3';
}
// Función para resaltar un nodo
function highlightNode(nodeId, isFound) {
    if (!network || !nodeId) return;

    // Restaurar nodo previamente resaltado
    if (lastHighlightedNode) {
        network.body.data.nodes.update({
            id: lastHighlightedNode,
            color: lastHighlightedNodeColor
        });
    }

    const node = network.body.nodes[nodeId];
    if (node) {
        // Guardar estado actual del nodo
        lastHighlightedNode = nodeId;
        lastHighlightedNodeColor = {
            background: node.options.color.background,
            border: node.options.color.border
        };

        // Actualizar con nuevo color
        network.body.data.nodes.update({
            id: nodeId,
            color: {
                background: isFound ? '#ff9800' : '#ffe135',
                border: isFound ? '#e68a00' : '#e6d135'
            }
        });

        // Enfocar el nodo
        network.focus(nodeId, {
            scale: 1.2,
            animation: {
                duration: 1000,
                easingFunction: 'easeInOutQuad'
            }
        });
    }
}

// Operaciones del árbol
async function insertValue() {
    const valueInput = document.getElementById('valor');
    const value = valueInput.value.trim();

    if (!value) {
        updateStatus('Error: Ingrese un valor', 'warning');
        return;
    }

    updateStatus(`Insertando valor ${value}...`, 'info');

    try {
        const response = await fetch('/api/btree/insertar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ valor: parseInt(value) })
        });

        const data = await response.json();
        if (data.status === 'success') {
            updateStatus(`Valor ${value} insertado correctamente`, 'success');
            loadTree(value);
        } else {
            throw new Error(data.message || 'Error al insertar');
        }
    } catch (error) {
        console.error('Error al insertar:', error);
        updateStatus(`Error al insertar ${value}: ${error.message}`, 'danger');
    }
}

async function deleteValue() {
    const value = document.getElementById('valor').value.trim();

    if (!value) {
        updateStatus('Error: Ingrese un valor', 'warning');
        return;
    }

    updateStatus(`Eliminando valor ${value}...`, 'info');

    try {
        const response = await fetch(`/api/btree/eliminar/${value}`, {
            method: 'DELETE'
        });

        const data = await response.json();
        if (data.status === 'success') {
            updateStatus(`Valor ${value} eliminado correctamente`, 'success');
            loadTree();
        } else {
            updateStatus(data.message || 'El valor no existe en el árbol', 'warning');
            loadTree();
        }
    } catch (error) {
        console.error('Error al eliminar:', error);
        updateStatus(`Error al eliminar ${value}: ${error.message}`, 'danger');
    }
}

async function searchValue() {
    const value = document.getElementById('valor').value.trim();

    if (!value) {
        updateStatus('Error: Ingrese un valor', 'warning');
        return;
    }

    updateStatus(`Buscando valor ${value}...`, 'info');

    try {
        const response = await fetch(`/api/btree/buscar/${value}`);
        const data = await response.json();

        if (data.encontrado) {
            updateStatus(`Valor ${value} encontrado`, 'success');
            loadTree(value);
        } else {
            updateStatus(`Valor ${value} no encontrado en el árbol`, 'warning');
            loadTree();
        }
    } catch (error) {
        console.error('Error al buscar:', error);
        updateStatus(`Error al buscar ${value}: ${error.message}`, 'danger');
    }
}

async function clearTree() {
    updateStatus('Vaciando el árbol...', 'info');

    try {
        const response = await fetch('/api/btree/vaciar', {
            method: 'DELETE'
        });

        const data = await response.json();
        if (data.status === 'success') {
            updateStatus('Árbol vaciado correctamente', 'success');
            if (network) {
                network.setData({
                    nodes: new vis.DataSet([]),
                    edges: new vis.DataSet([])
                });
            }
            updateTreeInfo(null);
        }
    } catch (error) {
        console.error('Error al vaciar el árbol:', error);
        updateStatus(`Error al vaciar el árbol: ${error.message}`, 'danger');
    }
}

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    // Cargar el árbol al iniciar
    loadTree();

    // Asignar eventos a los botones
    document.getElementById('insert-btn').addEventListener('click', insertValue);
    document.getElementById('delete-btn').addEventListener('click', deleteValue);
    document.getElementById('search-btn').addEventListener('click', searchValue);
    document.getElementById('clear-btn').addEventListener('click', clearTree);
    document.getElementById('reload-btn').addEventListener('click', loadTree);

    // Configurar evento para la tecla Enter
    document.getElementById('valor').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            insertValue();
        }
    });
});