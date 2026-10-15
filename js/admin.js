// ============================================================================
// HSB REFRESH — Admin Coordinator Dashboard (admin.js)
// ============================================================================

let currentEvents = [];
let currentRegistrations = [];

// DOM Elements - Stat Cards
const statTotalEvents = document.getElementById("stat-total-events");
const statTotalRegistrations = document.getElementById("stat-total-registrations");
const statUtilizationPct = document.getElementById("stat-utilization-pct");
const statCapacitySubtext = document.getElementById("stat-capacity-subtext");
const statUpcomingEvents = document.getElementById("stat-upcoming-events");
const categoryBreakdownContainer = document.getElementById("category-breakdown-container");
const lastUpdatedText = document.getElementById("last-updated-text");
const refreshBtn = document.getElementById("refresh-dashboard-btn");

// Tabs
const tabEventsBtn = document.getElementById("tab-events-btn");
const tabRegistrationsBtn = document.getElementById("tab-registrations-btn");
const tabEventsContent = document.getElementById("tab-events-content");
const tabRegistrationsContent = document.getElementById("tab-registrations-content");

// Tables
const eventsTableBody = document.getElementById("events-table-body");
const registrationsTableBody = document.getElementById("registrations-table-body");
const filterRegistrationEvent = document.getElementById("filter-registration-event");
const regCountIndicator = document.getElementById("reg-count-indicator");

// Modal - Add Event
const addEventBtn = document.getElementById("add-event-btn");
const addEventModal = document.getElementById("add-event-modal");
const addModalCloseBtn = document.getElementById("add-modal-close-btn");
const addEventForm = document.getElementById("add-event-form");
const addEventAlert = document.getElementById("add-event-alert");

// ----------------------------------------------------------------------------
// Load Full Dashboard Data
// ----------------------------------------------------------------------------
async function loadDashboard() {
  lastUpdatedText.textContent = "Updating...";
  try {
    await Promise.all([loadStats(), loadEvents(), loadRegistrations()]);
    const now = new Date();
    lastUpdatedText.textContent = `Updated ${now.toLocaleTimeString()}`;
  } catch (err) {
    console.error("Error loading dashboard data:", err);
    lastUpdatedText.textContent = "Error updating";
  }
}

// ----------------------------------------------------------------------------
// 1. Fetch & Render Stats & Category Breakdown (GET /api/stats)
// ----------------------------------------------------------------------------
async function loadStats() {
  try {
    const res = await fetch("/api/stats");
    if (!res.ok) throw new Error(`Stats HTTP ${res.status}`);
    const stats = await res.json();

    statTotalEvents.textContent = stats.total_events;
    statTotalRegistrations.textContent = stats.total_registrations;
    statUtilizationPct.textContent = `${stats.overall_utilization_pct}%`;
    statCapacitySubtext.textContent = `${stats.total_registrations} / ${stats.total_capacity} total seats occupied`;
    statUpcomingEvents.textContent = stats.upcoming_events_count;

    // Render Category Breakdown
    categoryBreakdownContainer.innerHTML = "";
    const categories = Object.entries(stats.events_by_category || {});
    if (categories.length === 0) {
      categoryBreakdownContainer.innerHTML = `<span class="text-xs text-slate-500">No categories found.</span>`;
    } else {
      categoryBreakdownContainer.innerHTML = categories
        .map(([cat, count]) => `
          <div class="flex items-center gap-2 bg-slate-900/90 border border-slate-700 px-3 py-1.5 rounded-xl text-xs">
            <span class="font-semibold text-slate-200">${cat}</span>
            <span class="px-2 py-0.5 rounded-full text-[10px] font-black bg-violet-600/30 text-violet-300 border border-violet-500/30">${count} ${count === 1 ? 'event' : 'events'}</span>
          </div>
        `)
        .join("");
    }
  } catch (err) {
    console.error("Failed to load stats:", err);
    statTotalEvents.textContent = "!";
    statTotalRegistrations.textContent = "!";
    statUtilizationPct.textContent = "!";
    statUpcomingEvents.textContent = "!";
  }
}

// ----------------------------------------------------------------------------
// 2. Fetch & Render Events Utilization Table (GET /api/events)
// ----------------------------------------------------------------------------
async function loadEvents() {
  eventsTableBody.innerHTML = `
    <tr>
      <td colspan="7" class="py-8 text-center text-slate-400">
        <div class="inline-flex items-center gap-2">
          <div class="w-4 h-4 border-2 border-violet-500 border-t-transparent rounded-full animate-spin"></div>
          <span>Loading events table...</span>
        </div>
      </td>
    </tr>
  `;

  try {
    const res = await fetch("/api/events");
    if (!res.ok) throw new Error(`Events HTTP ${res.status}`);
    const events = await res.json();
    currentEvents = events;

    // Populate registration filter dropdown
    populateEventFilterDropdown(events);

    if (events.length === 0) {
      eventsTableBody.innerHTML = `
        <tr>
          <td colspan="7" class="py-8 text-center text-slate-500">No events found in the database.</td>
        </tr>
      `;
      return;
    }

    // Compute registered count & utilization percentage for each event
    const eventsWithUtilization = events.map((ev) => {
      const registered = Math.max(0, ev.capacity - ev.available_seats);
      const utilizationPct = ev.capacity > 0 ? (registered / ev.capacity) * 100 : 0;
      return {
        ...ev,
        registered,
        utilizationPct: Math.round(utilizationPct * 10) / 10,
      };
    });

    // Requirement: Sort by utilization percentage descending by default
    eventsWithUtilization.sort((a, b) => b.utilizationPct - a.utilizationPct);

    // Render Table Rows
    eventsTableBody.innerHTML = eventsWithUtilization.map((ev) => {
      const isOver90 = ev.utilizationPct >= 90;
      const isFull = ev.available_seats === 0;

      const rowClass = isOver90 ? "bg-rose-950/20 hover:bg-rose-950/30" : "hover:bg-slate-800/60";

      let statusBadge;
      if (isFull) {
        statusBadge = `<span class="px-2 py-0.5 rounded text-[10px] font-black bg-rose-600 text-white uppercase tracking-wider">FULL</span>`;
      } else if (isOver90) {
        statusBadge = `<span class="px-2 py-0.5 rounded text-[10px] font-bold bg-amber-500/20 text-amber-300 border border-amber-500/30">Near Capacity</span>`;
      } else {
        statusBadge = `<span class="px-2 py-0.5 rounded text-[10px] font-medium bg-emerald-500/20 text-emerald-300 border border-emerald-500/30">Available</span>`;
      }

      const utilColor = isOver90 ? "text-rose-400 font-bold" : ev.utilizationPct > 50 ? "text-amber-300" : "text-slate-300";

      return `
        <tr class="transition-colors ${rowClass}">
          <td class="py-3 px-4 font-semibold text-white">
            <span class="mr-1.5 text-base">${ev.icon || '⚡'}</span>
            <span>${ev.title}</span>
          </td>
          <td class="py-3 px-4">
            <span class="px-2 py-0.5 rounded-full text-[11px] font-semibold bg-slate-900 border border-slate-700 text-slate-300">
              ${ev.category}
            </span>
          </td>
          <td class="py-3 px-4 text-slate-300 whitespace-nowrap">
            ${ev.event_date} &bull; ${ev.event_time}
          </td>
          <td class="py-3 px-4 text-slate-300">${ev.venue}</td>
          <td class="py-3 px-4 text-center font-mono">
            <span class="font-bold text-white">${ev.registered}</span>
            <span class="text-slate-500">/</span>
            <span class="text-slate-400">${ev.capacity}</span>
          </td>
          <td class="py-3 px-4 text-center font-mono ${utilColor}">
            ${ev.utilizationPct}%
          </td>
          <td class="py-3 px-4 text-center">
            ${statusBadge}
          </td>
        </tr>
      `;
    }).join("");
  } catch (err) {
    console.error("Failed to load events table:", err);
    eventsTableBody.innerHTML = `
      <tr>
        <td colspan="7" class="py-8 text-center text-rose-400">
          Failed to load events from API. Ensure backend is running.
        </td>
      </tr>
    `;
  }
}

// ----------------------------------------------------------------------------
// 3. Fetch & Render Raw Registrations List (GET /api/registrations)
// ----------------------------------------------------------------------------
async function loadRegistrations(eventIdFilter = null) {
  registrationsTableBody.innerHTML = `
    <tr>
      <td colspan="8" class="py-8 text-center text-slate-400">
        <div class="inline-flex items-center gap-2">
          <div class="w-4 h-4 border-2 border-violet-500 border-t-transparent rounded-full animate-spin"></div>
          <span>Loading registration records...</span>
        </div>
      </td>
    </tr>
  `;

  try {
    const url = eventIdFilter ? `/api/registrations?event_id=${eventIdFilter}` : "/api/registrations";
    const res = await fetch(url);
    if (!res.ok) throw new Error(`Registrations HTTP ${res.status}`);
    const registrations = await res.json();
    currentRegistrations = registrations;

    regCountIndicator.textContent = `Showing ${registrations.length} record(s)`;

    if (registrations.length === 0) {
      registrationsTableBody.innerHTML = `
        <tr>
          <td colspan="8" class="py-8 text-center text-slate-500">No registrations found.</td>
        </tr>
      `;
      return;
    }

    registrationsTableBody.innerHTML = registrations.map((r) => {
      const formattedDate = new Date(r.registered_at).toLocaleString();
      return `
        <tr class="hover:bg-slate-800/60 transition-colors">
          <td class="py-3 px-4 font-mono text-slate-400 text-xs">#${r.id}</td>
          <td class="py-3 px-4 font-semibold text-white">${r.student_name}</td>
          <td class="py-3 px-4 font-mono text-slate-300 text-xs">${r.roll_number}</td>
          <td class="py-3 px-4 text-violet-300 text-xs">${r.email}</td>
          <td class="py-3 px-4 text-slate-400 text-xs">${r.phone || "—"}</td>
          <td class="py-3 px-4 font-medium text-slate-200">
            <span class="px-2 py-0.5 rounded bg-slate-900 border border-slate-700 text-xs">
              ${r.event_title}
            </span>
          </td>
          <td class="py-3 px-4 text-slate-400 text-[11px] whitespace-nowrap">${formattedDate}</td>
          <td class="py-3 px-4 text-center">
            <button
              data-reg-id="${r.id}"
              data-student-name="${r.student_name}"
              class="cancel-reg-btn px-2.5 py-1 rounded-lg text-xs font-semibold bg-rose-600/20 text-rose-300 hover:bg-rose-600 hover:text-white border border-rose-500/30 transition active:scale-95"
              title="Cancel Registration (Stretch Goal)"
            >
              Cancel
            </button>
          </td>
        </tr>
      `;
    }).join("");

    // Attach cancel click handlers
    document.querySelectorAll(".cancel-reg-btn").forEach((btn) => {
      btn.addEventListener("click", async () => {
        const regId = btn.getAttribute("data-reg-id");
        const studentName = btn.getAttribute("data-student-name");
        if (confirm(`Are you sure you want to cancel registration #${regId} for ${studentName}?`)) {
          await cancelRegistration(regId);
        }
      });
    });
  } catch (err) {
    console.error("Failed to load registrations:", err);
    registrationsTableBody.innerHTML = `
      <tr>
        <td colspan="8" class="py-8 text-center text-rose-400">
          Failed to load registrations from API.
        </td>
      </tr>
    `;
  }
}

// ----------------------------------------------------------------------------
// Populate Event Filter Dropdown
// ----------------------------------------------------------------------------
function populateEventFilterDropdown(events) {
  const currentVal = filterRegistrationEvent.value;
  filterRegistrationEvent.innerHTML = `<option value="">All Events</option>`;
  events.forEach((ev) => {
    const opt = document.createElement("option");
    opt.value = ev.id;
    opt.textContent = `${ev.icon || '⚡'} ${ev.title}`;
    filterRegistrationEvent.appendChild(opt);
  });
  filterRegistrationEvent.value = currentVal;
}

// ----------------------------------------------------------------------------
// Stretch Goal: Cancel Registration (DELETE /api/registrations/{id})
// ----------------------------------------------------------------------------
async function cancelRegistration(regId) {
  try {
    const res = await fetch(`/api/registrations/${regId}`, {
      method: "DELETE",
    });
    if (!res.ok) {
      const err = await res.json();
      alert(`Failed to cancel: ${err.detail || res.statusText}`);
      return;
    }
    // Refresh all data
    await loadDashboard();
  } catch (err) {
    console.error("Cancel failed:", err);
    alert("Network error: Could not cancel registration.");
  }
}

// ----------------------------------------------------------------------------
// Stretch Goal: Add New Event (POST /api/events)
// ----------------------------------------------------------------------------
addEventBtn.addEventListener("click", () => {
  addEventAlert.className = "hidden";
  addEventAlert.innerHTML = "";
  addEventForm.reset();
  addEventModal.classList.remove("hidden");
});

addModalCloseBtn.addEventListener("click", () => {
  addEventModal.classList.add("hidden");
});

addEventModal.addEventListener("click", (e) => {
  if (e.target === addEventModal) addEventModal.classList.add("hidden");
});

addEventForm.addEventListener("submit", async (e) => {
  e.preventDefault();

  const title = document.getElementById("new-title").value.trim();
  const icon = document.getElementById("new-icon").value.trim() || "🎪";
  const category = document.getElementById("new-category").value;
  const venue = document.getElementById("new-venue").value.trim();
  const date = document.getElementById("new-date").value.trim();
  const time = document.getElementById("new-time").value.trim();
  const capacity = parseInt(document.getElementById("new-capacity").value, 10);
  const fee = parseInt(document.getElementById("new-fee").value, 10) || 0;
  const description = document.getElementById("new-description").value.trim();

  const payload = {
    title,
    icon,
    category,
    venue,
    event_date: date,
    event_time: time,
    capacity,
    fee,
    description: description || null,
  };

  try {
    const res = await fetch("/api/events", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });

    const data = await res.json();
    if (res.status === 201) {
      addEventAlert.className = "mb-4 p-3 rounded-xl text-xs sm:text-sm font-medium bg-emerald-950/70 text-emerald-200 border border-emerald-700/80";
      addEventAlert.textContent = `🎉 Event "${data.title}" successfully created!`;
      addEventForm.reset();
      await loadDashboard();
      setTimeout(() => {
        addEventModal.classList.add("hidden");
      }, 1500);
    } else {
      addEventAlert.className = "mb-4 p-3 rounded-xl text-xs sm:text-sm font-medium bg-rose-950/70 text-rose-200 border border-rose-700/80";
      addEventAlert.textContent = data.detail || "Failed to create event. Please verify all fields.";
    }
  } catch (err) {
    console.error("Create event error:", err);
    addEventAlert.className = "mb-4 p-3 rounded-xl text-xs sm:text-sm font-medium bg-rose-950/70 text-rose-200 border border-rose-700/80";
    addEventAlert.textContent = "Network error: Could not reach backend server.";
  }
});

// ----------------------------------------------------------------------------
// Tab Switching
// ----------------------------------------------------------------------------
tabEventsBtn.addEventListener("click", () => {
  tabEventsBtn.className = "tab-btn pb-3 text-sm font-bold border-b-2 border-violet-500 text-white flex items-center gap-2";
  tabRegistrationsBtn.className = "tab-btn pb-3 text-sm font-medium border-b-2 border-transparent text-slate-400 hover:text-slate-200 flex items-center gap-2";
  tabEventsContent.classList.remove("hidden");
  tabRegistrationsContent.classList.add("hidden");
});

tabRegistrationsBtn.addEventListener("click", () => {
  tabRegistrationsBtn.className = "tab-btn pb-3 text-sm font-bold border-b-2 border-violet-500 text-white flex items-center gap-2";
  tabEventsBtn.className = "tab-btn pb-3 text-sm font-medium border-b-2 border-transparent text-slate-400 hover:text-slate-200 flex items-center gap-2";
  tabRegistrationsContent.classList.remove("hidden");
  tabEventsContent.classList.add("hidden");
});

// ----------------------------------------------------------------------------
// Event Listeners
// ----------------------------------------------------------------------------
refreshBtn.addEventListener("click", loadDashboard);

filterRegistrationEvent.addEventListener("change", (e) => {
  const selectedEventId = e.target.value ? parseInt(e.target.value, 10) : null;
  loadRegistrations(selectedEventId);
});

// Initialize on page load
document.addEventListener("DOMContentLoaded", () => {
  loadDashboard();
});
