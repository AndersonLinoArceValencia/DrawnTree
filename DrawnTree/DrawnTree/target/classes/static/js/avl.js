const urlBase = 'http://localhost:8080/avl';

function insertar() {
    const valor = document.getElementById('valor').value;
    if (!valor) return alert('Ingrese un valor');
    fetch(`${urlBase}/insertar?valor=${valor}`, { method: 'POST' })
        .then(() => actualizarArbol());
}

function eliminar() {
    const valor = document.getElementById('valor').value;
    if (!valor) return alert('Ingrese un valor');
    fetch(`${urlBase}/eliminar?valor=${valor}`, { method: 'POST' })
        .then(() => actualizarArbol());
}

function reiniciar() {
    fetch(`${urlBase}/reiniciar`, { method: 'POST' })
        .then(() => actualizarArbol());
}

function actualizarArbol() {
    fetch(`${urlBase}/estructura`)
        .then(res => res.json())
        .then(data => {
            d3.select("svg").selectAll("*").remove();
            if (Object.keys(data).length > 0) {
                dibujarArbol(data);
            }
        });
}

async function obtenerDatos() {
    const response = await fetch("http://localhost:8080/avl/estructura");

    if (!response.ok) {
        throw new Error("Error al obtener la estructura del árbol");
    }

    const text = await response.text();

    if (!text) {
        throw new Error("Respuesta vacía del servidor");
    }

    const data = JSON.parse(text);
    return data;
}

function dibujarArbol(data) {
    const svg = d3.select("svg");
    const width = +svg.attr("width");
    const height = +svg.attr("height");

    const g = svg.append("g").attr("transform", "translate(50,50)");
    const tree = d3.tree().size([width - 70, height - 250]);

    const root = d3.hierarchy(data, d => {
        const children = [];
        if (d.left) children.push(d.left);
        if (d.right) children.push(d.right);
        return children.length ? children : null;
    });

    const treeData = tree(root);

    const link = g.selectAll(".link")
        .data(treeData.links())
        .enter().append("line")
        .attr("class", "link")
        .attr("stroke", "#999")
        .attr("x1", d => d.source.x)
        .attr("y1", d => d.source.y)
        .attr("x2", d => d.target.x)
        .attr("y2", d => d.target.y);

    const node = g.selectAll(".node")
        .data(treeData.descendants())
        .enter().append("g")
        .attr("class", "node")
        .attr("transform", d => `translate(${d.x},${d.y})`);

    node.append("circle")
        .attr("r", 20)
        .attr("fill", "#1f77b4");

    node.append("text")
        .attr("dy", 5)
        .attr("text-anchor", "middle")
        .attr("fill", "#fff")
        .text(d => d.data.value);
}

// Cargar árbol al iniciar
actualizarArbol();