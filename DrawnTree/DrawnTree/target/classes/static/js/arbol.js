const container = document.getElementById('network');
let network = null;


function calcularPosiciones(nodos) {
    if (!nodos || nodos.length === 0) return {};
    const raiz = nodos.find(n => n.padre === null);
    const posiciones = {};
    let x = 0;

    function asignar(nodo, profundidad) {
        if (!nodo) return;

        const hijos = nodos.filter(n => n.padre === nodo.id)
            .sort((a, b) => a.valor - b.valor);


        if (hijos.length > 0 && hijos[0].valor < nodo.valor) {
            asignar(hijos[0], profundidad + 1);
        }


        posiciones[nodo.id] = { x: x * 150, y: profundidad * 120 };
        x++;


        if (hijos.length === 2) {
            asignar(hijos[1], profundidad + 1);
        } else if (hijos.length === 1 && hijos[0].valor > nodo.valor) {
            asignar(hijos[0], profundidad + 1);
        }
    }

    if (raiz) {
        asignar(raiz, 0);
    }
    return posiciones;
}

function cargarArbol(resaltar = null, camino = []) {
    fetch('/api/arbol/listar')
        .then(res => res.json())
        .then(data => {

            if (!data.nodos || data.nodos.length === 0) {
                if (network) network.setData({ nodes: [], edges: [] });
                return;
            }

            const posiciones = calcularPosiciones(data.nodos);

            const nodos = new vis.DataSet(data.nodos.map(n => ({
                id: n.id,
                label: n.valor.toString(),
                color: resaltar === n.id ? '#ff9800' : (camino.includes(n.id) ? '#ffe135' : undefined),
                x: posiciones[n.id] !== undefined ? posiciones[n.id].x : 0,
                y: posiciones[n.id] !== undefined ? posiciones[n.id].y : 0,
                fixed: true,
                size: 30
            })));

            const edges = data.nodos
                .filter(n => n.padre !== null)
                .map(n => ({ from: n.padre, to: n.id }));

            const datos = { nodes: nodos, edges: edges };

            const opciones = {
                physics: false,
                nodes: {
                    shape: "circle",
                    size: 30,
                    font: {
                        size: 16,
                        bold: true
                    }
                },
                edges: {
                    arrows: {
                        to: { enabled: true, scaleFactor: 0.5 } // Puedes ajustar el tamaño de la flecha aquí
                    },
                    length: 25 // <-- Reduce este valor para flechas más cortas
                }
            };



            if (network) {
                network.setData(datos);
            } else {
                network = new vis.Network(container, datos, opciones);
            }
        })
        .catch(err => {

            console.error("Error al cargar el árbol:", err);
            if (network) network.setData({ nodes: [], edges: [] });
        });
}

function insertar() {
    const valor = document.getElementById("valor").value;
    if (valor === "") return alert("Ingresa un valor");
    fetch(`/api/arbol/insertar/${valor}`, { method: 'POST' })
        .then(() => cargarArbol())
        .catch(err => alert("Error al insertar: " + err));
}

function eliminar() {
    const valor = document.getElementById("valor").value;
    if (valor === "") return alert("Ingresa un valor");
    fetch(`/api/arbol/eliminar/${valor}`, { method: 'DELETE' })
        .then(() => cargarArbol())
        .catch(err => alert("Error al eliminar: " + err));
}

function buscar() {
    const valor = document.getElementById("valor").value;
    if (valor === "") return alert("Ingresa un valor");
    fetch(`/api/arbol/buscar/${valor}`)
        .then(res => res.json())
        .then(data => {
            const camino = data.camino || [];
            const encontrado = data.encontrado;
            const resaltar = encontrado ? camino[camino.length - 1] : null;
            cargarArbol(resaltar, camino);
        })
        .catch(err => alert("Error al buscar: " + err));
}

function vaciar() {
    fetch('/api/arbol/vaciar', { method: 'DELETE' })
        .then(() => cargarArbol())
        .catch(err => alert("Error al vaciar: " + err));
}


cargarArbol();