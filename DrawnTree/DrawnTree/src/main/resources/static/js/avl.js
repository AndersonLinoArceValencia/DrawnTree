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
    fetch(`http://localhost:8080/avl/buscar?valor=${valor}`)
        .then(res => res.json())
        .then(data => {
            const status = document.getElementById("status");
            status.className = `status-box alert ${data.encontrado ? 'alert-success' : 'alert-danger'}`;
            status.innerHTML = `<strong>Estado:</strong> ${data.encontrado ? 'Valor encontrado ✅' : 'Valor no encontrado ❌'}`;
        });
}

function cargarArbol() {
    fetch("http://localhost:8080/avl/estructura")
        .then(res => res.json())
        .then(data => {
            nodes.clear();
            edges.clear();
            let tamaño = 0;

            function recorrer(nodo, padreId = null) {
                if (!nodo) return;
                nodes.add({ id: nodo.value, label: `${nodo.value}` });
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