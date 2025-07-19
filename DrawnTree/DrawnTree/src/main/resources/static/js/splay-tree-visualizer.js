// Variables globales
const container = document.getElementById('network');
let network = null;
let highlightedNodeId = null;
let highlightedEdges = [];

// Función para actualizar el estado en la UI
function updateStatus(message, type = 'info') {
    const statusElement = document.getElementById('status');
    statusElement.innerHTML = `<strong>Estado:</strong> ${message}`;
    statusElement.className = `status-box alert alert-${type}`;
}

// Función para cargar y visualizar el árbol
function cargarArbol(resaltar = null, camino = []) {
    updateStatus('Cargando estructura del árbol...');

    fetch('/api/splaytree/visualizar')
        .then(response => {
            if (!response.ok) throw new Error('Error al cargar el árbol');
            return response.json();
        })
        .then(data => {
            console.log("Datos recibidos:", data); // ← Añade esto para depuración
            if (data.status !== 'success') throw new Error(data.message || 'Error en los datos del árbol');

            updateStatus('Árbol cargado correctamente', 'success');
            actualizarInformacionArbol(data.arbol);

            // Convertir la estructura del árbol a nodos y aristas para vis.js
            const { nodes, edges } = convertirDatosParaVisualizacion(data.arbol, resaltar, camino);

            const datosVisualizacion = {
                nodes: new vis.DataSet(nodes),
                edges: new vis.DataSet(edges)
            };

            const opciones = {
                physics: {
                    enabled: false
                },
                layout: {
                    hierarchical: {
                        direction: 'UD',
                        sortMethod: 'directed'
                    }
                },
                nodes: {
                    shape: 'box',
                    margin: 10,
                    size: 30,
                    font: {
                        size: 14,
                        face: 'Tahoma', // Especifica la fuente correctamente
                        bold: {
                            color: '#343a40', // Color para texto en negrita
                            size: 14, // Tamaño para texto en negrita
                            vadjust: 0,
                            mod: 'bold' // Esto indica que queremos texto en negrita
                        }
                    },
                    borderWidth: 2,
                    shadow: true,
                    color: {
                        background: '#4e73df',
                        border: '#2e59d9',
                        highlight: {
                            background: '#2e59d9',
                            border: '#1c3d8f'
                        }
                    }
                },
                edges: {
                    width: 2,
                    smooth: true,
                    arrows: {
                        to: {
                            enabled: true,
                            scaleFactor: 0.7
                        }
                    },
                    color: {
                        color: '#858796',
                        highlight: '#2e59d9'
                    }
                }
            };
            // Crear o actualizar la red
            if (network) {
                network.setData(datosVisualizacion);
            } else {
                network = new vis.Network(container, datosVisualizacion, opciones);

                // Configurar eventos
                network.on('click', function(params) {
                    if (params.nodes.length > 0) {
                        const nodeId = params.nodes[0];
                        const nodeLabel = network.body.nodes[nodeId].options.label;
                        document.getElementById('valor').value = nodeLabel;
                    }
                });
            }

            // Resaltar nodos si es necesario
            if (resaltar) {
                resaltarNodo(resaltar, true);
            }
            if (camino && camino.length > 0) {
                resaltarCamino(camino);
            }
        })
        .catch(error => {
            console.error('Error al cargar el árbol:', error);
            updateStatus(`Error al cargar el árbol: ${error.message}`, 'danger');
            if (network) {
                network.setData({ nodes: [], edges: [] });
            }
        });
}

// Función para convertir la estructura del árbol a datos de vis.js
function convertirDatosParaVisualizacion(arbol, resaltar = null, camino = []) {
    const nodes = [];
    const edges = [];
    let nodeCounter = 0;

    function procesarNodo(nodo, parentId = null, nivel = 0) {
        if (!nodo) return null;

        const nodeId = `n${nodeCounter++}`;
        const esRaiz = parentId === null;
        const esResaltado = nodo.key === resaltar;
        const enCamino = camino.includes(nodo.key);

        // Determinar color del nodo
        let colorFondo = esRaiz ? '#1cc88a' : '#4e73df';
        if (esResaltado) {
            colorFondo = '#f6c23e';
        } else if (enCamino) {
            colorFondo = '#36b9cc';
        }

        // Agregar nodo
        nodes.push({
            id: nodeId,
            label: nodo.key.toString(),
            color: {
                background: colorFondo,
                border: '#2e59d9',
                highlight: {
                    background: '#2e59d9',
                    border: '#1c3d8f'
                }
            },
            level: nivel,
            fixed: {
                x: false,
                y: false
            }
        });

        // Agregar arista si tiene padre
        if (parentId !== null) {
            edges.push({
                from: parentId,
                to: nodeId,
                arrows: 'to'
            });
        }

        // Procesar hijos recursivamente
        if (nodo.left) {
            procesarNodo(nodo.left, nodeId, nivel + 1);
        }
        if (nodo.right) {
            procesarNodo(nodo.right, nodeId, nivel + 1);
        }

        return nodeId;
    }

    // Procesar desde la raíz
    if (arbol.root) {
        procesarNodo(arbol.root);
    }

    return { nodes, edges };
}

// Función para actualizar la información del árbol en la UI
function actualizarInformacionArbol(arbol) {
    document.getElementById('raiz-actual').textContent = arbol.rootKey || '-';

    // Calcular tamaño y altura (podrías implementar estos métodos en el backend)
    function calcularTamano(nodo) {
        if (!nodo) return 0;
        return 1 + calcularTamano(nodo.left) + calcularTamano(nodo.right);
    }

    function calcularAltura(nodo) {
        if (!nodo) return -1;
        return 1 + Math.max(calcularAltura(nodo.left), calcularAltura(nodo.right));
    }

    const tamano = arbol.root ? calcularTamano(arbol.root) : 0;
    const altura = arbol.root ? calcularAltura(arbol.root) : 0;

    document.getElementById('tamano-arbol').textContent = tamano;
    document.getElementById('altura-arbol').textContent = altura;
}

// Función para insertar un valor
function insertar() {
    const valor = document.getElementById('valor').value.trim();
    if (!valor) {
        updateStatus('Error: Ingrese un valor', 'danger');
        return;
    }

    updateStatus(`Insertando valor ${valor}...`);

    fetch('/api/splaytree/insertar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ valor: parseInt(valor) })
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => { throw new Error(err.message); });
            }
            return response.json();
        })
        .then(data => {
            if (data.status === 'success') {
                updateStatus(`Valor ${valor} insertado correctamente. Nueva raíz: ${data.raiz}`, 'success');
                cargarArbol(valor);
            } else {
                throw new Error(data.message || 'Error al insertar');
            }
        })
        .catch(error => {
            console.error('Error al insertar:', error);
            updateStatus(`Error al insertar ${valor}: ${error.message}`, 'danger');
        });
}

// Función para eliminar un valor
function eliminar() {
    const valor = document.getElementById('valor').value.trim();
    if (!valor) {
        updateStatus('Error: Ingrese un valor', 'danger');
        return;
    }

    updateStatus(`Eliminando valor ${valor}...`);

    fetch(`/api/splaytree/eliminar/${valor}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => { throw new Error(err.message); });
            }
            return response.json();
        })
        .then(data => {
            if (data.status === 'success') {
                updateStatus(`Valor ${valor} eliminado correctamente. Nueva raíz: ${data.raiz}`, 'success');
                cargarArbol();
            } else {
                throw new Error(data.message || 'Error al eliminar');
            }
        })
        .catch(error => {
            console.error('Error al eliminar:', error);
            updateStatus(`Error al eliminar ${valor}: ${error.message}`, 'danger');
        });
}

// Función para buscar un valor
function buscar() {
    const valor = document.getElementById('valor').value.trim();
    if (!valor) {
        updateStatus('Error: Ingrese un valor', 'danger');
        return;
    }

    updateStatus(`Buscando valor ${valor}...`);

    fetch(`/api/splaytree/buscar/${valor}`)
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => { throw new Error(err.message); });
            }
            return response.json();
        })
        .then(data => {
            if (data.encontrado) {
                updateStatus(`Valor ${valor} encontrado. Raíz actual: ${data.raiz}`, 'success');
                // Simulamos un camino (en un Splay Tree real, deberías implementar esto en el backend)
                const caminoSimulado = [data.raiz, valor]; // Esto es un ejemplo
                cargarArbol(valor, caminoSimulado);
            } else {
                updateStatus(`Valor ${valor} no encontrado en el árbol`, 'warning');
                cargarArbol();
            }
        })
        .catch(error => {
            console.error('Error al buscar:', error);
            updateStatus(`Error al buscar ${valor}: ${error.message}`, 'danger');
        });
}

// Función para vaciar el árbol
function vaciarArbol() {
    updateStatus('Vaciando el árbol...');

    fetch('/api/splaytree/vaciar', {
            method: 'DELETE'
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => { throw new Error(err.message); });
            }
            return response.json();
        })
        .then(data => {
            if (data.status === 'success') {
                updateStatus('Árbol vaciado correctamente', 'success');
                cargarArbol();
            } else {
                throw new Error(data.message || 'Error al vaciar el árbol');
            }
        })
        .catch(error => {
            console.error('Error al vaciar el árbol:', error);
            updateStatus(`Error al vaciar el árbol: ${error.message}`, 'danger');
        });
}

// Función para resaltar un nodo específico
function resaltarNodo(nodeKey, isFound = false) {
    if (!network) return;

    // Buscar el nodo por su etiqueta (que es el valor del nodo)
    const nodeId = network.body.data.nodes.getIds({
        filter: node => node.label === nodeKey.toString()
    })[0];

    if (nodeId) {
        network.selectNodes([nodeId]);
        network.focus(nodeId, {
            scale: 1.2,
            animation: {
                duration: 1000,
                easingFunction: 'easeInOutQuad'
            }
        });

        // Cambiar color temporalmente
        network.body.data.nodes.update({
            id: nodeId,
            color: {
                background: isFound ? '#f6c23e' : '#36b9cc'
            }
        });

        highlightedNodeId = nodeId;
    }
}

// Función para resaltar un camino de nodos
function resaltarCamino(nodeKeys) {
    if (!network || !nodeKeys || nodeKeys.length === 0) return;

    // Restaurar nodos previamente resaltados
    if (highlightedEdges.length > 0) {
        highlightedEdges.forEach(edgeId => {
            network.body.data.edges.update({
                id: edgeId,
                color: {
                    color: '#858796'
                },
                width: 2
            });
        });
        highlightedEdges = [];
    }

    // Resaltar nuevo camino
    nodeKeys.forEach(key => {
        const nodeId = network.body.data.nodes.getIds({
            filter: node => node.label === key.toString()
        })[0];

        if (nodeId) {
            // Resaltar aristas entrantes
            network.body.data.edges.get().forEach(edge => {
                if (edge.to === nodeId) {
                    network.body.data.edges.update({
                        id: edge.id,
                        color: {
                            color: '#2e59d9'
                        },
                        width: 3
                    });
                    highlightedEdges.push(edge.id);
                }
            });
        }
    });
}

// Cargar el árbol al iniciar
document.addEventListener('DOMContentLoaded', function() {
    cargarArbol();

    // Configurar evento para la tecla Enter
    document.getElementById('valor').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            insertar();
        }
    });
});