'use strict';

let categoryChart = null;
let sentimentChart = null;

const API_BASE = '/api/v1';

// Retrieve JWT from localStorage (set after login)
function getToken() { return localStorage.getItem('ci_token') || ''; }
function headers()  { return { 'Authorization': 'Bearer ' + getToken(), 'Content-Type': 'application/json' }; }

async function loadDashboard(days = 30) {
    try {
        const res  = await fetch(`${API_BASE}/dashboard/stats?daysBack=${days}`, { headers: headers() });
        if (res.status === 401) { window.location.href = '/swagger-ui.html'; return; }
        const data = await res.json();

        // KPIs
        document.getElementById('kpiCompetitors').textContent = data.activeCompetitors ?? '--';
        document.getElementById('kpiEvents30d').textContent   = data.eventsLast30Days  ?? '--';
        document.getElementById('kpiEnriched').textContent    = data.enrichedEvents     ?? '--';
        document.getElementById('kpiAlerts').textContent      = data.pendingAlerts      ?? '--';

        // Category Chart
        renderBar('categoryChart', data.eventsByCategory ?? {}, 'Events by Category');

        // Sentiment Chart
        renderPie('sentimentChart', data.eventsBySentiment ?? {});

        // Top Competitors
        renderTopCompetitors(data.topCompetitors ?? []);

    } catch (err) {
        console.error('Dashboard load error:', err);
    }
}

function renderBar(canvasId, dataMap, label) {
    const canvas = document.getElementById(canvasId);
    if (!canvas) return;
    const labels = Object.keys(dataMap);
    const values = Object.values(dataMap);

    if (categoryChart) { categoryChart.destroy(); categoryChart = null; }

    categoryChart = new Chart(canvas, {
        type: 'bar',
        data: {
            labels,
            datasets: [{ label, data: values,
                backgroundColor: 'rgba(13,110,253,0.7)', borderRadius: 4 }]
        },
        options: { responsive: true, plugins: { legend: { display: false } },
            scales: { y: { beginAtZero: true, ticks: { precision: 0 } } } }
    });
}

function renderPie(canvasId, dataMap) {
    const canvas = document.getElementById(canvasId);
    if (!canvas) return;
    const COLORS = ['#198754','#0dcaf0','#ffc107','#dc3545','#6f42c1'];
    const labels = Object.keys(dataMap);
    const values = Object.values(dataMap);

    if (sentimentChart) { sentimentChart.destroy(); sentimentChart = null; }

    sentimentChart = new Chart(canvas, {
        type: 'doughnut',
        data: {
            labels,
            datasets: [{ data: values,
                backgroundColor: labels.map((_, i) => COLORS[i % COLORS.length]) }]
        },
        options: { responsive: true, plugins: { legend: { position: 'right' } } }
    });
}

function renderTopCompetitors(list) {
    const tbody = document.getElementById('topCompetitorsTable');
    if (!tbody) return;
    if (!list.length) { tbody.innerHTML = '<tr><td colspan="3" class="text-center text-muted py-3">No data</td></tr>'; return; }
    tbody.innerHTML = list.map(c => `
        <tr>
            <td class="fw-semibold">${c.name ?? ''}</td>
            <td><span class="badge bg-primary rounded-pill">${c.eventCount ?? 0}</span></td>
            <td><a href="/api/v1/competitors/${c.id}" class="btn btn-sm btn-outline-secondary">View</a></td>
        </tr>`).join('');
}

// Bootstrap — load on DOMContentLoaded
document.addEventListener('DOMContentLoaded', () => loadDashboard(30));
