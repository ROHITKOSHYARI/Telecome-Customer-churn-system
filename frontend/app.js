/* ═══════════════════════════════════════════════════════════
   Customer Churn Prediction — Frontend Application
   Apple Glassmorphism Light Theme
   ═══════════════════════════════════════════════════════════ */

/* ── Configuration ── */
const API_BASE =
  window.CHURN_API_BASE_URL ||
  (window.location.protocol === "file:" ? "http://localhost:8080" : "/api");

const STORAGE = { token: "churn.jwt", user: "churn.user" };

/* ── Prediction Form Schema ── */
const yesNo = ["Yes", "No"];
const internetAddon = ["Yes", "No", "No internet service"];

const predictionGroups = [
  {
    title: "Customer",
    icon: "👤",
    fields: [
      { name: "gender", label: "Gender", type: "select", options: ["Female", "Male"] },
      { name: "seniorCitizen", label: "Senior Citizen", type: "select", options: ["0", "1"] },
      { name: "partner", label: "Partner", type: "select", options: yesNo },
      { name: "dependents", label: "Dependents", type: "select", options: yesNo },
      { name: "tenure", label: "Tenure (months)", type: "number", min: 0, step: 1 },
    ],
  },
  {
    title: "Services",
    icon: "📡",
    fields: [
      { name: "phoneService", label: "Phone Service", type: "select", options: yesNo },
      { name: "multipleLines", label: "Multiple Lines", type: "select", options: ["Yes", "No", "No phone service"] },
      { name: "internetService", label: "Internet Service", type: "select", options: ["DSL", "Fiber optic", "No"] },
      { name: "onlineSecurity", label: "Online Security", type: "select", options: internetAddon },
      { name: "onlineBackup", label: "Online Backup", type: "select", options: internetAddon },
      { name: "deviceProtection", label: "Device Protection", type: "select", options: internetAddon },
      { name: "techSupport", label: "Tech Support", type: "select", options: internetAddon },
      { name: "streamingTV", label: "Streaming TV", type: "select", options: internetAddon },
      { name: "streamingMovies", label: "Streaming Movies", type: "select", options: internetAddon },
    ],
  },
  {
    title: "Billing",
    icon: "💳",
    fields: [
      { name: "contract", label: "Contract", type: "select", options: ["Month-to-month", "One year", "Two year"] },
      { name: "paperlessBilling", label: "Paperless Billing", type: "select", options: yesNo },
      { name: "paymentMethod", label: "Payment Method", type: "select", options: ["Electronic check", "Mailed check", "Bank transfer (automatic)", "Credit card (automatic)"] },
      { name: "monthlyCharges", label: "Monthly Charges ($)", type: "number", min: 0, step: 0.01 },
      { name: "totalCharges", label: "Total Charges ($)", type: "number", min: 0, step: 0.01 },
    ],
  },
];

const sampleCustomer = {
  gender: "Female", seniorCitizen: 0, partner: "Yes", dependents: "No",
  tenure: 12, phoneService: "Yes", multipleLines: "No", internetService: "Fiber optic",
  onlineSecurity: "No", onlineBackup: "Yes", deviceProtection: "No", techSupport: "No",
  streamingTV: "Yes", streamingMovies: "Yes", contract: "Month-to-month",
  paperlessBilling: "Yes", paymentMethod: "Electronic check",
  monthlyCharges: 89.10, totalCharges: 1069.20,
};

/* ── Application State ── */
const state = {
  token: localStorage.getItem(STORAGE.token),
  user: readStoredUser(),
  predictionResult: null,
  authMode: "login",
};

function readStoredUser() {
  try { return JSON.parse(localStorage.getItem(STORAGE.user)); }
  catch { return null; }
}

/* ══════════════════════════════════════
   API Layer — XMLHttpRequest
   ══════════════════════════════════════ */
function apiRequest(path, options = {}) {
  return new Promise((resolve, reject) => {
    const method = (options.method || "GET").toUpperCase();
    const url = API_BASE + path;
    const hasBody = options.body !== undefined;

    const xhr = new XMLHttpRequest();
    xhr.open(method, url, true);

    if (hasBody) {
      xhr.setRequestHeader("Content-Type", "application/json");
    }
    if (options.auth !== false && state.token) {
      xhr.setRequestHeader("Authorization", "Bearer " + state.token);
    }

    xhr.onload = function () {
      let data = null;
      if (xhr.responseText) {
        try { data = JSON.parse(xhr.responseText); }
        catch { data = xhr.responseText; }
      }

      if (xhr.status >= 200 && xhr.status < 300) {
        resolve(data);
      } else {
        if ((xhr.status === 401 || xhr.status === 403) && options.auth !== false) {
          logout();
          reject(new Error("Session expired. Please login again."));
          return;
        }
        const message = (data && data.message) ? data.message
          : (typeof data === "string" && data.trim()) ? data
          : "Request failed (" + xhr.status + ")";
        reject(new Error(message));
      }
    };

    xhr.onerror = function () {
      reject(new Error("Cannot reach the backend. Make sure Docker is running."));
    };

    xhr.send(hasBody ? JSON.stringify(options.body) : null);
  });
}

/* ══════════════════════════════════════
   Router
   ══════════════════════════════════════ */
const app = document.getElementById("app");

function navigate(hash) {
  location.hash = hash;
}

function currentRoute() {
  return location.hash || "#/login";
}

function router() {
  const route = currentRoute();

  if (!state.token) {
    if (route === "#/register") {
      state.authMode = "register";
    } else {
      state.authMode = "login";
    }
    renderAuthPage();
    return;
  }

  switch (route) {
    case "#/profile":
      renderProfilePage();
      break;
    case "#/predict":
    default:
      renderPredictPage();
      break;
  }
}

window.addEventListener("hashchange", router);
window.addEventListener("DOMContentLoaded", () => {
  router();
  if (state.token) refreshUser();
});

/* ══════════════════════════════════════
   Auth Page
   ══════════════════════════════════════ */
function renderAuthPage() {
  const isLogin = state.authMode === "login";

  app.innerHTML = `
    <div class="page-center">
      <div class="auth-container">
        <div class="glass auth-card">
          <div class="auth-header">
            <div class="logo-icon-lg">CP</div>
            <h1>${isLogin ? "Welcome Back" : "Create Account"}</h1>
            <p class="muted">${isLogin ? "Sign in to run churn predictions" : "Register a new account to get started"}</p>
          </div>

          <div class="tab-toggle">
            <button type="button" class="${isLogin ? "active" : ""}" data-tab="login">Login</button>
            <button type="button" class="${!isLogin ? "active" : ""}" data-tab="register">Register</button>
          </div>

          ${isLogin ? loginFormHTML() : registerFormHTML()}
        </div>
      </div>
    </div>
  `;

  bindAuthEvents();
}

function loginFormHTML() {
  return `
    <form id="authForm" class="form-stack">
      <div class="form-group">
        <label for="login-username">Username</label>
        <input id="login-username" name="username" autocomplete="username" placeholder="Enter your username" required />
      </div>
      <div class="form-group">
        <label for="login-password">Password</label>
        <input id="login-password" name="password" type="password" autocomplete="current-password" placeholder="Enter your password" required />
      </div>
      <button class="btn btn-primary btn-full" type="submit">Sign In</button>
      <div id="authStatus"></div>
    </form>
  `;
}

function registerFormHTML() {
  return `
    <form id="authForm" class="form-stack">
      <div class="form-group">
        <label for="reg-username">Username</label>
        <input id="reg-username" name="username" autocomplete="username" placeholder="Choose a username" required />
      </div>
      <div class="form-group">
        <label for="reg-email">Email</label>
        <input id="reg-email" name="email" type="email" autocomplete="email" placeholder="you@example.com" required />
      </div>
      <div class="form-group">
        <label for="reg-password">Password</label>
        <input id="reg-password" name="password" type="password" autocomplete="new-password" placeholder="Min 6 characters" minlength="6" required />
      </div>
      <div class="form-group">
        <label for="reg-confirm">Confirm Password</label>
        <input id="reg-confirm" name="confirmPassword" type="password" autocomplete="new-password" placeholder="Repeat password" minlength="6" required />
      </div>
      <button class="btn btn-primary btn-full" type="submit">Create Account</button>
      <div id="authStatus"></div>
    </form>
  `;
}

function bindAuthEvents() {
  document.querySelectorAll("[data-tab]").forEach(btn => {
    btn.addEventListener("click", () => {
      state.authMode = btn.dataset.tab;
      if (state.authMode === "register") navigate("#/register");
      else navigate("#/login");
    });
  });

  const form = document.getElementById("authForm");
  if (!form) return;

  if (state.authMode === "login") {
    form.addEventListener("submit", handleLogin);
  } else {
    form.addEventListener("submit", handleRegister);
  }
}

async function handleLogin(e) {
  e.preventDefault();
  const form = e.currentTarget;
  const statusEl = document.getElementById("authStatus");
  const username = form.elements.username.value.trim();
  const password = form.elements.password.value;

  if (!username || !password) {
    showStatus(statusEl, "Username and password are required.", "error");
    return;
  }

  showStatus(statusEl, "Signing in…", "info");
  setFormBusy(form, true);

  try {
    const res = await apiRequest("/public/login", {
      method: "POST",
      auth: false,
      body: { username: username, password: password },
    });

    state.token = res.token;
    state.user = res.user;
    persistSession();
    navigate("#/predict");
  } catch (err) {
    showStatus(statusEl, err.message, "error");
  } finally {
    setFormBusy(form, false);
  }
}

async function handleRegister(e) {
  e.preventDefault();
  const form = e.currentTarget;
  const statusEl = document.getElementById("authStatus");
  const username = form.elements.username.value.trim();
  const email = form.elements.email.value.trim();
  const password = form.elements.password.value;
  const confirmPassword = form.elements.confirmPassword.value;

  if (!username || !email || !password || !confirmPassword) {
    showStatus(statusEl, "All fields are required.", "error");
    return;
  }
  if (password !== confirmPassword) {
    showStatus(statusEl, "Passwords do not match.", "error");
    return;
  }

  showStatus(statusEl, "Creating account…", "info");
  setFormBusy(form, true);

  try {
    await apiRequest("/public/saveuser", {
      method: "POST",
      auth: false,
      body: { username, email, password, roles: ["USER"] },
    });
    state.authMode = "login";
    navigate("#/login");
    setTimeout(() => {
      const st = document.getElementById("authStatus");
      if (st) showStatus(st, "Account created! You can now login.", "success");
    }, 100);
  } catch (err) {
    showStatus(statusEl, err.message, "error");
  } finally {
    setFormBusy(form, false);
  }
}

/* ══════════════════════════════════════
   Prediction Page
   ══════════════════════════════════════ */
function renderPredictPage() {
  app.innerHTML = `
    ${navbarHTML("predict")}
    <div class="page">
      <div class="container">
        <div class="predict-layout">
          <div class="glass predict-card">
            <div class="section-header">
              <div>
                <p class="eyebrow">Model Input</p>
                <h2>Customer Details</h2>
              </div>
              <button class="btn btn-secondary" id="resetBtn" type="button">↺ Reset Sample</button>
            </div>

            <form id="predictForm">
              ${predictionGroups.map(groupHTML).join("")}
              <div class="predict-footer">
                <button class="btn btn-primary" type="submit">🔍 Get Prediction</button>
                <span id="predictStatus"></span>
              </div>
            </form>
          </div>

          <div class="result-sidebar">
            <div class="glass result-card-wrapper">
              <p class="eyebrow">Result</p>
              <div id="resultBox">
                ${resultEmptyHTML()}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `;

  bindPredictEvents();
}

function groupHTML(group) {
  return `
    <fieldset class="field-group">
      <legend>${group.icon} ${group.title}</legend>
      <div class="field-grid">
        ${group.fields.map(f => fieldHTML(f)).join("")}
      </div>
    </fieldset>
  `;
}

function fieldHTML(field) {
  const val = sampleCustomer[field.name];
  if (field.type === "select") {
    return `
      <div class="form-group">
        <label for="f-${field.name}">${field.label}</label>
        <select id="f-${field.name}" name="${field.name}">
          ${field.options.map(o => `<option value="${esc(o)}" ${String(val) === String(o) ? "selected" : ""}>${o}</option>`).join("")}
        </select>
      </div>
    `;
  }
  return `
    <div class="form-group">
      <label for="f-${field.name}">${field.label}</label>
      <input id="f-${field.name}" name="${field.name}" type="number"
        value="${esc(val)}" min="${field.min ?? ""}" step="${field.step ?? "1"}" required />
    </div>
  `;
}

function resultEmptyHTML() {
  return `
    <div class="result-empty">
      <div class="result-icon">📊</div>
      <p>Submit a customer to see<br/>the churn prediction.</p>
    </div>
  `;
}

function renderResult() {
  const box = document.getElementById("resultBox");
  if (!box) return;

  const r = state.predictionResult;
  if (!r) { box.innerHTML = resultEmptyHTML(); return; }

  const churn = String(r.churn || "Unknown");
  const prob = Number(r.churnProbability);
  const pct = Number.isFinite(prob) ? (prob * 100).toFixed(1) : null;
  const isHigh = churn.toLowerCase() === "yes" || prob >= 0.5;
  const riskClass = isHigh ? "high" : "low";

  box.innerHTML = `
    <div class="result-display">
      <div class="result-risk ${riskClass}">
        <div class="risk-label">${isHigh ? "⚠️ High Risk" : "✅ Low Risk"}</div>
        <div class="risk-value">${isHigh ? "Will Churn" : "Will Stay"}</div>
      </div>

      <div class="result-meta">
        <div class="result-meta-row">
          <span class="label">Churn</span>
          <span class="value">${esc(churn)}</span>
        </div>
        ${pct !== null ? `
          <div class="result-meta-row">
            <span class="label">Probability</span>
            <span class="value" style="color: ${isHigh ? "var(--color-danger)" : "var(--color-success)"}">${pct}%</span>
          </div>
          <div class="prob-bar-track">
            <div class="prob-bar-fill ${riskClass}" style="width: ${pct}%"></div>
          </div>
        ` : ""}
      </div>
    </div>
  `;
}

function bindPredictEvents() {
  document.getElementById("resetBtn").addEventListener("click", () => {
    state.predictionResult = null;
    renderPredictPage();
  });

  document.getElementById("predictForm").addEventListener("submit", handlePredict);
  bindNavEvents();
}

async function handlePredict(e) {
  e.preventDefault();
  const form = e.currentTarget;
  const statusEl = document.getElementById("predictStatus");
  showStatus(statusEl, "Running prediction…", "info");
  setFormBusy(form, true);

  try {
    const payload = buildPayload(form);
    state.predictionResult = await apiRequest("/customer/getpredection", {
      method: "POST",
      body: payload,
    });
    renderResult();
    showStatus(statusEl, "Prediction complete!", "success");
    setTimeout(() => { if (statusEl) statusEl.innerHTML = ""; }, 3000);
  } catch (err) {
    showStatus(statusEl, err.message, "error");
  } finally {
    setFormBusy(form, false);
  }
}

function buildPayload(form) {
  return {
    gender: form.elements.gender.value,
    seniorCitizen: Number(form.elements.seniorCitizen.value),
    partner: form.elements.partner.value,
    dependents: form.elements.dependents.value,
    tenure: Number(form.elements.tenure.value),
    phoneService: form.elements.phoneService.value,
    multipleLines: form.elements.multipleLines.value,
    internetService: form.elements.internetService.value,
    onlineSecurity: form.elements.onlineSecurity.value,
    onlineBackup: form.elements.onlineBackup.value,
    deviceProtection: form.elements.deviceProtection.value,
    techSupport: form.elements.techSupport.value,
    streamingTV: form.elements.streamingTV.value,
    streamingMovies: form.elements.streamingMovies.value,
    contract: form.elements.contract.value,
    paperlessBilling: form.elements.paperlessBilling.value,
    paymentMethod: form.elements.paymentMethod.value,
    monthlyCharges: Number(form.elements.monthlyCharges.value),
    totalCharges: Number(form.elements.totalCharges.value),
  };
}

/* ══════════════════════════════════════
   Profile Page
   ══════════════════════════════════════ */
function renderProfilePage() {
  const u = state.user || {};
  const roles = Array.isArray(u.roles) ? u.roles.join(", ") : "USER";

  app.innerHTML = `
    ${navbarHTML("profile")}
    <div class="page">
      <div class="container">
        <div style="margin-bottom: 24px;">
          <p class="eyebrow">Account</p>
          <h1>Profile Settings</h1>
        </div>

        <div class="profile-layout">
          <!-- Update Details -->
          <div class="glass profile-card">
            <div class="card-header">
              <p class="eyebrow">Details</p>
              <h2>Update Profile</h2>
            </div>
            <form id="profileForm" class="form-stack">
              <div class="form-group">
                <label>Username</label>
                <input name="username" value="${esc(u.username || "")}" disabled />
              </div>
              <div class="form-group">
                <label>Email</label>
                <input name="email" type="email" value="${esc(u.email || "")}" required />
              </div>
              <div class="form-group">
                <label>Roles</label>
                <input name="roles" value="${esc(roles)}" />
              </div>
              <button class="btn btn-primary" type="submit">Save Changes</button>
              <div id="profileStatus"></div>
            </form>
          </div>

          <!-- Change Password -->
          <div class="glass profile-card">
            <div class="card-header">
              <p class="eyebrow">Security</p>
              <h2>Change Password</h2>
            </div>
            <form id="passwordForm" class="form-stack">
              <div class="form-group">
                <label>Current Password</label>
                <input name="currentPassword" type="password" autocomplete="current-password" required />
              </div>
              <div class="form-group">
                <label>New Password</label>
                <input name="newPassword" type="password" autocomplete="new-password" minlength="6" required />
              </div>
              <div class="form-group">
                <label>Confirm New Password</label>
                <input name="confirmPassword" type="password" autocomplete="new-password" minlength="6" required />
              </div>
              <button class="btn btn-primary" type="submit">Update Password</button>
              <div id="passwordStatus"></div>
            </form>
          </div>

          <!-- Danger Zone -->
          <div class="glass profile-card danger-zone" style="grid-column: 1 / -1;">
            <div class="card-header">
              <p class="eyebrow">Danger Zone</p>
              <h2>Delete Account</h2>
              <p class="muted" style="margin-top: 4px;">This action is permanent and cannot be undone.</p>
            </div>
            <button class="btn btn-danger" id="deleteAccountBtn" type="button">Delete My Account</button>
          </div>
        </div>
      </div>
    </div>
  `;

  bindProfileEvents();
}

function bindProfileEvents() {
  bindNavEvents();

  document.getElementById("profileForm").addEventListener("submit", handleProfileUpdate);
  document.getElementById("passwordForm").addEventListener("submit", handlePasswordChange);
  document.getElementById("deleteAccountBtn").addEventListener("click", handleDeleteAccount);
}

async function handleProfileUpdate(e) {
  e.preventDefault();
  const form = e.currentTarget;
  const statusEl = document.getElementById("profileStatus");
  const email = form.elements.email.value.trim();
  const rolesStr = form.elements.roles.value.trim();
  const roles = rolesStr.split(",").map(r => r.trim()).filter(Boolean);

  showStatus(statusEl, "Saving…", "info");
  setFormBusy(form, true);

  try {
    await apiRequest("/user/updateuser", {
      method: "PUT",
      body: { email, roles },
    });
    if (state.user) {
      state.user.email = email;
      state.user.roles = roles;
      persistSession();
    }
    showStatus(statusEl, "Profile updated!", "success");
  } catch (err) {
    showStatus(statusEl, err.message, "error");
  } finally {
    setFormBusy(form, false);
  }
}

async function handlePasswordChange(e) {
  e.preventDefault();
  const form = e.currentTarget;
  const statusEl = document.getElementById("passwordStatus");
  const currentPassword = form.elements.currentPassword.value;
  const newPassword = form.elements.newPassword.value;
  const confirmPassword = form.elements.confirmPassword.value;

  if (newPassword !== confirmPassword) {
    showStatus(statusEl, "New passwords do not match.", "error");
    return;
  }

  showStatus(statusEl, "Updating password…", "info");
  setFormBusy(form, true);

  try {
    await apiRequest("/user/changepassword", {
      method: "PUT",
      body: { currentPassword, newPassword, confirmPassword },
    });
    form.reset();
    showStatus(statusEl, "Password changed!", "success");
  } catch (err) {
    showStatus(statusEl, err.message, "error");
  } finally {
    setFormBusy(form, false);
  }
}

async function handleDeleteAccount() {
  if (!confirm("Are you absolutely sure? This will permanently delete your account.")) return;

  try {
    await apiRequest("/user/delete_user", { method: "DELETE" });
    logout();
  } catch (err) {
    alert("Failed to delete account: " + err.message);
  }
}

/* ══════════════════════════════════════
   Shared: Navbar
   ══════════════════════════════════════ */
function navbarHTML(activePage) {
  const u = state.user || {};
  return `
    <nav class="navbar">
      <div class="brand">
        <div class="logo-icon">CP</div>
        <span>Churn Predict</span>
      </div>

      <div class="nav-links">
        <a href="#/predict" class="nav-link ${activePage === "predict" ? "active" : ""}">Predictions</a>
        <a href="#/profile" class="nav-link ${activePage === "profile" ? "active" : ""}">Profile</a>
      </div>

      <div class="nav-user">
        <span class="nav-user-chip">${esc(u.username || "User")}</span>
        <button class="nav-logout" id="logoutBtn" type="button">Logout</button>
      </div>
    </nav>
  `;
}

function bindNavEvents() {
  const logoutBtn = document.getElementById("logoutBtn");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", logout);
  }
}

/* ══════════════════════════════════════
   Session Helpers
   ══════════════════════════════════════ */
function persistSession() {
  localStorage.setItem(STORAGE.token, state.token);
  localStorage.setItem(STORAGE.user, JSON.stringify(state.user));
}

function logout() {
  state.token = null;
  state.user = null;
  state.predictionResult = null;
  localStorage.removeItem(STORAGE.token);
  localStorage.removeItem(STORAGE.user);
  navigate("#/login");
}

async function refreshUser() {
  try {
    state.user = await apiRequest("/user/getuser");
    persistSession();
  } catch {
    /* silent — token may be expired */
  }
}

/* ══════════════════════════════════════
   Utilities
   ══════════════════════════════════════ */
function showStatus(el, message, type) {
  if (!el) return;
  el.innerHTML = `<div class="status-msg ${type}">${esc(message)}</div>`;
}

function setFormBusy(form, busy) {
  form.querySelectorAll("button").forEach(b => { b.disabled = busy; });
}

function esc(value) {
  return String(value)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}
