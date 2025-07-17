const canvas = document.getElementById("canvas");
const ctx = canvas.getContext("2d");

async function insertar() {
    const valor = document.getElementById("valor").value;
    if (!valor) return;
    const res = await fetch(`/api/btree/insertar/${valor}`, { method: "POST" });
    const data = await res.json();
    mostrarArbol(data.nodos);
}

async function eliminar() {
    const valor = document.getElementById("valor").value;
    if (!valor) return;
    const res = await fetch(`/api/btree/eliminar/${valor}`, { method: "DELETE" });
    const data = await res.json();
    mostrarArbol(data.nodos);
}

async function vaciar() {
    const res = await fetch(`/api/btree/vaciar`, { method: "DELETE" });
    const data = await res.json();
    mostrarArbol(data.nodos);
}

function mostrarArbol(nodos) {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    // Agrupa por padre
    const mapa = {};
    nodos.forEach(n => {
        if (!mapa[n.padre]) mapa[n.padre] = [];
        mapa[n.padre].push(n);
    });

    dibujarNivel(mapa, null, canvas.width / 2, 50, canvas.width / 4);
}

function dibujarNivel(mapa, padreId, x, y, offsetX) {
    const nodos = mapa[padreId];
    if (!nodos) return;

    const total = nodos.length;
    let startX = x - ((total - 1) * 50);

    nodos.forEach((nodo, index) => {
        const cx = startX + index * 100;
        ctx.fillStyle = "lightblue";
        ctx.fillRect(cx - 15, y, 30, 30);
        ctx.strokeRect(cx - 15, y, 30, 30);
        ctx.fillStyle = "black";
        ctx.fillText(nodo.valor, cx - 5, y + 20);

        // Línea hacia el padre
        if (padreId !== null) {
            const padre = mapa[null].find(n => `${n.id}` === padreId);
            if (padre) {
                ctx.beginPath();
                ctx.moveTo(x, y - 20);
                ctx.lineTo(cx, y);
                ctx.stroke();
            }
        }

        // Recursivo
        dibujarNivel(mapa, nodo.id, cx, y + 80, offsetX / 2);
    });
}