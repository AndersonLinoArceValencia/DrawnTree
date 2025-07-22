let nodes = new vis.DataSet();
let edges = new vis.DataSet();
let network = null;

function insertar() {
    const valor = document.getElementById("valor").value;
    fetch(`http://localhost:8080/avl/insertar?valor=${valor}`, { method: "POST" })
        .then(cargarArbol);
}

function eliminar() {
    const valor = document.getElementById("valor").value;
    fetch(`http://localhost:8080/avl/eliminar?valor=${valor}`, { method: "POST" })
        .then(cargarArbol);
}

function vaciarArbol() {
    fetch("http://localhost:8080/avl/reiniciar", { method: "POST" })
        .then(cargarArbol);
}

function buscar() {
    const valor = document.getElementById("valor").value;
    if (!valor) {
        alert("Ingrese un valor para buscar.");
        return;
    }

    fetch(`/avl/buscar?valor=${valor}`)
        .then(response => response.json())
        .then(data => {
            const status = document.getElementById("status");
            status.className = `status-box alert ${data.encontrado ? 'alert-success' : 'alert-danger'}`;
            status.innerHTML = `<strong>Estado:</strong> ${data.encontrado ? 'Valor encontrado ✅' : 'Valor no encontrado ❌'}`;

            // 🔄 1. Recargar el árbol sin colores
            cargarArbol(); // limpia todo y lo carga sin pintar

            // ⏳ 2. Esperar un poco a que se cargue visualmente antes de pintar
            setTimeout(() => {
                if (data.camino && Array.isArray(data.camino)) {
                    const camino = data.camino;

                    camino.forEach((val, idx) => {
                        if (idx === camino.length - 1 && data.encontrado) {
                            // Último nodo: valor encontrado (naranja)
                            nodes.update({
                                id: val,
                                color: { background: "#FFA500" }
                            });
                        } else {
                            // Camino intermedio: amarillo
                            nodes.update({
                                id: val,
                                color: { background: "#FFFF66" }
                            });
                        }
                    });
                }
            }, 200); // espera 200 ms para asegurar que el árbol ya esté visible
        })
        .catch(error => {
            console.error("Error al buscar:", error);
        });
}

function cargarArbol(resaltar = null, camino = []) {
    fetch("http://localhost:8080/avl/estructura")
        .then(res => res.json())
        .then(data => {
            // 🧼 Limpiar colores de nodos antes de recargar el árbol
            const todosLosNodos = nodes.get();
            todosLosNodos.forEach(nodo => {
                nodes.update({
                    id: nodo.id,
                    color: undefined
                });
            });

            nodes.clear();
            edges.clear();
            let tamaño = 0;

            function recorrer(nodo, padreId = null) {
                if (!nodo) return;
                nodes.add({ id: nodo.value, label: `${nodo.value}` }); // sin color
                tamaño++;
                if (padreId !== null) {
                    edges.add({ from: padreId, to: nodo.value });
                }
                recorrer(nodo.left, nodo.value);
                recorrer(nodo.right, nodo.value);
            }

            recorrer(data);

            const container = document.getElementById("network");
            const datos = { nodes, edges };
            const opciones = { layout: { hierarchical: { direction: "UD", sortMethod: "directed" } } };

            if (!network) {
                network = new vis.Network(container, datos, opciones);
            } else {
                network.setData(datos);
            }

            document.getElementById("raiz-actual").textContent = data?.value ?? "Vacío";
            document.getElementById("tamano-arbol").textContent = tamaño;
            document.getElementById("altura-arbol").textContent = calcularAltura(data);
        });
}


function calcularAltura(nodo) {
    if (!nodo) return 0;
    return 1 + Math.max(calcularAltura(nodo.left), calcularAltura(nodo.right));
}
function recargarVista() {
    if (network) {
        network.fit({ animation: true });
    }
}


document.addEventListener("DOMContentLoaded", cargarArbol);