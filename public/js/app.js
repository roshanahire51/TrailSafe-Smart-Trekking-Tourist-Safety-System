/**
 * TrailSafe – Smart Trekking & Tourist Safety System
 * Main Frontend Application Controller
 * Handles Routes, Interactive Maps, Live Location, SOS, Feedback,
 * Admin Monitoring, and Offline Payment Queuing with Auto-Sync.
 */

const App = {
    // State
    currentUser: null,
    currentAdmin: null,
    isOnline: true,
    routes: [],
    trips: [],
    services: [],
    selectedRoute: null,
    isSharingLocation: false,
    sharingInterval: null,
    map: null,
    mapMarkers: [],
    userMarker: null,
    currentCoords: { lat: 19.9210, lng: 73.8030 },

    init() {
        this.setupAuth();
        this.setupNetworkMonitor();
        this.setupEventListeners();
        this.loadRoutes();
        this.loadEmergencyServices();
        this.loadFeedbacks();
        this.checkOfflineQueue();

        // Check if user was previously logged in
        const savedUser = localStorage.getItem('trailsafe_user');
        if (savedUser) {
            try {
                this.currentUser = JSON.parse(savedUser);
            } catch (e) {
                this.currentUser = null;
            }
        } else {
            this.currentUser = null;
        }

        const savedAdmin = localStorage.getItem('trailsafe_admin');
        if (savedAdmin) {
            try {
                this.currentAdmin = JSON.parse(savedAdmin);
            } catch (e) {
                this.currentAdmin = null;
            }
        } else {
            this.currentAdmin = null;
        }

        this.renderUserSession();
        this.loadUserTrips();
        this.initMap();
    },

    // --- Network & Offline Payment Queue ---
    setupNetworkMonitor() {
        const toggleBtn = document.getElementById('networkToggleBtn');
        const statusText = document.getElementById('networkStatusText');

        // Initial check
        this.isOnline = navigator.onLine;
        this.updateNetworkUI();

        window.addEventListener('online', () => {
            this.isOnline = true;
            this.updateNetworkUI();
            this.showToast('Network connection restored!', 'success');
            this.syncPendingOfflineBookings();
        });

        window.addEventListener('offline', () => {
            this.isOnline = false;
            this.updateNetworkUI();
            this.showToast('Network connection lost. Offline mode active.', 'info');
        });

        if (toggleBtn) {
            toggleBtn.addEventListener('click', () => {
                this.isOnline = !this.isOnline;
                this.updateNetworkUI();
                if (this.isOnline) {
                    this.showToast('Simulated Online Mode activated.', 'success');
                    this.syncPendingOfflineBookings();
                } else {
                    this.showToast('Simulated Remote Mountain (Offline) Mode activated.', 'info');
                }
            });
        }
    },

    updateNetworkUI() {
        const toggleBtn = document.getElementById('networkToggleBtn');
        const statusText = document.getElementById('networkStatusText');
        const paymentModeBox = document.getElementById('paymentModeIndicator');

        if (toggleBtn && statusText) {
            if (this.isOnline) {
                toggleBtn.className = 'network-toggle-btn online';
                statusText.innerText = 'Online';
            } else {
                toggleBtn.className = 'network-toggle-btn offline';
                statusText.innerText = 'Offline (Remote)';
            }
        }

        if (paymentModeBox) {
            if (this.isOnline) {
                paymentModeBox.className = 'payment-mode-box online';
                paymentModeBox.innerHTML = `<strong>Online Gateway:</strong> Real-time payment processing enabled via secure server verification.`;
            } else {
                paymentModeBox.className = 'payment-mode-box offline';
                paymentModeBox.innerHTML = `<strong>Offline Mode Active:</strong> Payment will be securely saved locally on your device and automatically synced to the server once network is detected.`;
            }
        }
    },

    checkOfflineQueue() {
        const queue = this.getOfflineQueue();
        const badge = document.getElementById('offlineQueueBadge');
        const notice = document.getElementById('offlineTripsNotice');

        if (queue.length > 0) {
            if (badge) {
                badge.innerText = `${queue.length} Pending Sync`;
                badge.classList.remove('hidden');
            }
            if (notice) notice.classList.remove('hidden');
        } else {
            if (badge) badge.classList.add('hidden');
            if (notice) notice.classList.add('hidden');
        }
    },

    getOfflineQueue() {
        try {
            return JSON.parse(localStorage.getItem('trailsafe_offline_payments') || '[]');
        } catch (e) {
            return [];
        }
    },

    saveOfflineQueue(queue) {
        localStorage.setItem('trailsafe_offline_payments', JSON.stringify(queue));
        this.checkOfflineQueue();
    },

    async syncPendingOfflineBookings() {
        const queue = this.getOfflineQueue();
        if (queue.length === 0) return;

        this.showToast(`Syncing ${queue.length} offline transactions with server...`, 'info');

        const remainingQueue = [];
        for (const item of queue) {
            try {
                // Book trip on server
                const res = await fetch('/api/trips/book', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        userId: item.userId,
                        routeId: item.routeId,
                        startDate: item.startDate,
                        endDate: item.endDate,
                        amount: item.amount,
                        isOffline: false
                    })
                });
                const data = await res.json();
                if (data.success && data.booking && data.booking.payment) {
                    // Call sync
                    await fetch('/api/payments/sync', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ paymentId: data.booking.payment.paymentId })
                    });
                } else {
                    remainingQueue.push(item);
                }
            } catch (e) {
                remainingQueue.push(item);
            }
        }

        this.saveOfflineQueue(remainingQueue);
        if (remainingQueue.length === 0) {
            this.showToast('All offline payments successfully synchronized with TrailSafe server!', 'success');
        }
        this.loadUserTrips();
        if (this.currentAdmin) {
            this.loadAdminData();
        }
    },

    // --- Authentication ---
    setupAuth() {
        // Auth setup
    },

    switchAuthMode(mode) {
        const loginForm = document.getElementById('loginForm');
        const regForm = document.getElementById('registerForm');
        const adminForm = document.getElementById('adminLoginForm');
        const tabLogin = document.getElementById('authTabLogin');
        const tabReg = document.getElementById('authTabRegister');
        const tabAdmin = document.getElementById('authTabAdmin');

        loginForm.classList.add('hidden');
        regForm.classList.add('hidden');
        adminForm.classList.add('hidden');
        tabLogin.classList.remove('active');
        tabReg.classList.remove('active');
        tabAdmin.classList.remove('active');

        if (mode === 'login') {
            loginForm.classList.remove('hidden');
            tabLogin.classList.add('active');
        } else if (mode === 'register') {
            regForm.classList.remove('hidden');
            tabReg.classList.add('active');
        } else if (mode === 'admin') {
            adminForm.classList.remove('hidden');
            tabAdmin.classList.add('active');
        }
    },

    openAuthModal(mode = 'login') {
        const modal = document.getElementById('authModal');
        modal.classList.remove('hidden');
        this.switchAuthMode(mode);
    },

    closeAuthModal() {
        document.getElementById('authModal').classList.add('hidden');
    },

    fillDemoUser() {
        const emailInput = document.getElementById('loginEmail');
        const passInput = document.getElementById('loginPassword');
        if (emailInput && passInput) {
            emailInput.value = 'roshan@gmail.com';
            passInput.value = 'password123';
            this.showToast('Demo credentials filled for Roshan.', 'info');
        }
    },

    async handleUserLogin(e) {
        e.preventDefault();
        const email = document.getElementById('loginEmail').value;
        const password = document.getElementById('loginPassword').value;

        try {
            const res = await fetch('/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password })
            });
            const data = await res.json();
            if (data.success) {
                this.currentUser = data.user;
                this.currentAdmin = null;
                localStorage.setItem('trailsafe_user', JSON.stringify(this.currentUser));
                localStorage.removeItem('trailsafe_admin');
                document.getElementById('loginEmail').value = '';
                document.getElementById('loginPassword').value = '';
                this.closeAuthModal();
                this.renderUserSession();
                this.loadUserTrips();
                this.showToast(`Welcome back, ${this.currentUser.name}!`, 'success');
            } else {
                this.showToast(data.error || 'Invalid credentials', 'error');
            }
        } catch (err) {
            this.showToast('Login failed. Please check server.', 'error');
        }
    },

    async handleUserRegister(e) {
        e.preventDefault();
        const name = document.getElementById('regName').value;
        const email = document.getElementById('regEmail').value;
        const phone = document.getElementById('regPhone').value;
        const password = document.getElementById('regPassword').value;
        const contactName = document.getElementById('regContactName').value;
        const contactPhone = document.getElementById('regContactPhone').value;
        const relationship = document.getElementById('regRelationship').value;

        try {
            const res = await fetch('/api/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name, email, phone, password, contactName, contactPhone, relationship })
            });
            const data = await res.json();
            if (data.success) {
                this.currentUser = data.user;
                this.currentAdmin = null;
                localStorage.setItem('trailsafe_user', JSON.stringify(this.currentUser));
                localStorage.removeItem('trailsafe_admin');
                document.getElementById('registerForm').reset();
                this.closeAuthModal();
                this.renderUserSession();
                this.loadUserTrips();
                this.showToast(`Welcome, ${this.currentUser.name}! Your account has been created.`, 'success');
            } else {
                this.showToast(data.error || 'Registration failed', 'error');
            }
        } catch (err) {
            this.showToast('Registration error.', 'error');
        }
    },

    async handleAdminLogin(e) {
        e.preventDefault();
        const email = document.getElementById('adminEmail').value;
        const password = document.getElementById('adminPassword').value;

        try {
            const res = await fetch('/api/auth/admin-login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password })
            });
            const data = await res.json();
            if (data.success) {
                this.currentAdmin = data.admin;
                this.currentUser = null;
                localStorage.setItem('trailsafe_admin', JSON.stringify(this.currentAdmin));
                localStorage.removeItem('trailsafe_user');
                this.closeAuthModal();
                this.renderUserSession();
                this.showTab('admin');
                this.loadAdminData();
                this.showToast('Admin session authenticated.', 'success');
            } else {
                this.showToast(data.error || 'Invalid admin credentials', 'error');
            }
        } catch (err) {
            this.showToast('Admin login error.', 'error');
        }
    },

    logout() {
        this.currentUser = null;
        this.currentAdmin = null;
        localStorage.removeItem('trailsafe_user');
        localStorage.removeItem('trailsafe_admin');
        this.renderUserSession();
        this.loadUserTrips();
        this.showTab('dashboard');
        this.showToast('Logged out successfully.', 'info');
    },

    renderUserSession() {
        const widget = document.getElementById('userSessionWidget');
        const adminTab = document.querySelector('.nav-item.admin-only');

        if (this.currentAdmin) {
            widget.innerHTML = `
                <div class="user-pill">
                    <span>🛡️ <strong class="user-pill-name">Admin Control</strong></span>
                    <button class="btn-ghost-sm" onclick="App.logout()">Logout</button>
                </div>
            `;
            if (adminTab) adminTab.classList.remove('hidden');
        } else if (this.currentUser) {
            widget.innerHTML = `
                <div class="user-pill">
                    <span>👤 <span class="user-pill-name">${this.currentUser.name}</span></span>
                    <button class="btn-ghost-sm" onclick="App.logout()">Logout</button>
                </div>
            `;
            if (adminTab) adminTab.classList.add('hidden');
        } else {
            widget.innerHTML = `
                <div style="display:flex; gap:6px; align-items:center;">
                    <button class="btn btn-sm btn-secondary" onclick="App.openAuthModal('login')">Sign In</button>
                    <button class="btn btn-sm btn-primary" onclick="App.openAuthModal('register')">Register</button>
                </div>
            `;
            if (adminTab) adminTab.classList.add('hidden');
        }

        this.renderEmergencyContactStrip();
    },

    renderEmergencyContactStrip() {
        const strip = document.getElementById('emergencyContactStrip');
        if (!strip) return;

        if (this.currentUser && this.currentUser.emergencyContact) {
            const ec = this.currentUser.emergencyContact;
            strip.innerHTML = `
                <div>
                    <strong>Trusted Emergency Contact:</strong>
                    <span>${ec.name} (${ec.relationship}) – 📞 <strong>${ec.phone}</strong></span>
                </div>
                <small class="badge badge-nature">Designated for Live Location & SOS</small>
            `;
        } else if (this.currentUser) {
            strip.innerHTML = `
                <div>
                    <strong>Trusted Emergency Contact:</strong>
                    <span>No emergency contact registered yet.</span>
                </div>
                <small class="badge badge-warning">Standard Safety Dispatch Active</small>
            `;
        } else {
            strip.innerHTML = `
                <div style="display:flex; justify-content:space-between; align-items:center; width:100%; flex-wrap:wrap; gap:8px;">
                    <div>
                        <strong>Emergency Contact:</strong>
                        <span>Please sign in or register to link trusted contacts for live trail updates.</span>
                    </div>
                    <button class="btn btn-sm btn-primary" onclick="App.openAuthModal('register')">Create Account</button>
                </div>
            `;
        }
    },

    // --- Tab Navigation ---
    setupEventListeners() {
        document.querySelectorAll('.app-nav .nav-item').forEach(button => {
            button.addEventListener('click', () => {
                const tab = button.getAttribute('data-tab');
                this.showTab(tab);
            });
        });

        // Search and filter
        const searchInput = document.getElementById('routeSearchInput');
        const difficultyFilter = document.getElementById('difficultyFilter');
        if (searchInput) {
            searchInput.addEventListener('input', () => this.filterRoutes());
        }
        if (difficultyFilter) {
            difficultyFilter.addEventListener('change', () => this.filterRoutes());
        }

        // Star rating
        document.querySelectorAll('#starRatingSelector .star').forEach(star => {
            star.addEventListener('click', () => {
                const rating = parseInt(star.getAttribute('data-rating'));
                document.getElementById('feedbackRatingInput').value = rating;
                document.querySelectorAll('#starRatingSelector .star').forEach(s => {
                    const r = parseInt(s.getAttribute('data-rating'));
                    if (r <= rating) s.classList.add('active');
                    else s.classList.remove('active');
                });
            });
        });
    },

    showTab(tabName) {
        document.querySelectorAll('.app-nav .nav-item').forEach(btn => {
            btn.classList.toggle('active', btn.getAttribute('data-tab') === tabName);
        });

        document.querySelectorAll('.content-view').forEach(view => {
            view.classList.toggle('active', view.id === `view-${tabName}`);
        });

        if (tabName === 'tracking') {
            setTimeout(() => {
                if (this.map) this.map.invalidateSize();
            }, 200);
        }

        if (tabName === 'admin' && this.currentAdmin) {
            this.loadAdminData();
        }
    },

    showAdminSubTab(subtabId) {
        document.querySelectorAll('.admin-tab-btn').forEach(btn => {
            btn.classList.toggle('active', btn.getAttribute('onclick').includes(subtabId));
        });
        document.querySelectorAll('.admin-subtab').forEach(tab => {
            tab.classList.toggle('active', tab.id === `subtab-${subtabId}`);
        });
    },

    // --- Routes & Data ---
    async loadRoutes() {
        try {
            const res = await fetch('/api/routes');
            const data = await res.json();
            this.routes = data.routes || [];
            this.renderRoutes();
            this.renderDashboard();
            this.populateAdminRouteDropdown();
        } catch (e) {
            console.error('Failed to load routes', e);
        }
    },

    renderRoutes() {
        const container = document.getElementById('allRoutesContainer');
        if (!container) return;
        container.innerHTML = this.routes.map(route => this.createRouteCard(route)).join('');
    },

    createRouteCard(route) {
        const diffClass = (route.difficulty || 'Medium').toLowerCase();
        const weatherText = route.weather ? `${route.weather.condition}, ${route.weather.temperature}°C` : 'Clear, 24°C';
        const checkpointCount = route.checkpoints ? route.checkpoints.length : 0;
        const hasAlerts = route.alerts && route.alerts.length > 0;

        return `
            <div class="route-card">
                <div class="route-card-banner ${diffClass}">
                    <div class="route-card-tags">
                        <span class="badge badge-${diffClass}">${route.difficulty}</span>
                        <span class="badge badge-nature">${route.distance} km</span>
                    </div>
                    <h3 class="route-card-title">${route.routeName}</h3>
                </div>
                <div class="route-card-body">
                    <div class="route-meta-row">
                        <div class="route-meta-item">Checkpoints: <strong>${checkpointCount} Stops</strong></div>
                        <div class="route-meta-item">Safety Status: <strong>${hasAlerts ? '⚠️ Advisory' : '✅ Clear'}</strong></div>
                    </div>
                    <div class="route-weather-chip">
                        <span>🌤️</span>
                        <span>Forecast: <strong>${weatherText}</strong></span>
                    </div>
                    <div class="route-features-list">
                        <span>📍 Trail Points: ${route.checkpoints ? route.checkpoints.map(c => c.name).slice(0, 2).join(', ') + '...' : 'Available'}</span>
                        <span>🚰 Facilities: ${route.facilities ? route.facilities.map(f => f.facilityName).join(', ') : 'Water, Shelter'}</span>
                    </div>
                    <div class="route-card-footer">
                        <button class="btn btn-outline btn-sm btn-block" onclick="App.openRouteDetailsModal(${route.routeId})">Route Details</button>
                        <button class="btn btn-primary btn-sm btn-block" onclick="App.openBookingModal(${route.routeId})">Book Permit</button>
                    </div>
                </div>
            </div>
        `;
    },

    filterRoutes() {
        const query = (document.getElementById('routeSearchInput')?.value || '').toLowerCase();
        const diff = document.getElementById('difficultyFilter')?.value || 'ALL';

        const filtered = this.routes.filter(r => {
            const matchesQuery = r.routeName.toLowerCase().includes(query);
            const matchesDiff = diff === 'ALL' || r.difficulty.toLowerCase() === diff.toLowerCase();
            return matchesQuery && matchesDiff;
        });

        const container = document.getElementById('allRoutesContainer');
        if (container) {
            container.innerHTML = filtered.length > 0
                ? filtered.map(route => this.createRouteCard(route)).join('')
                : '<p style="grid-column: 1/-1; text-align: center; color: var(--gray-600); padding: 40px;">No trekking routes match your search filters.</p>';
        }
    },

    renderDashboard() {
        // Featured routes (first 3)
        const feat = document.getElementById('featuredRoutesContainer');
        if (feat) {
            feat.innerHTML = this.routes.slice(0, 3).map(r => this.createRouteCard(r)).join('');
        }

        // Stats
        document.getElementById('statRoutesCount').innerText = this.routes.length;
        let totalCp = 0;
        let totalAlerts = 0;
        const allAlerts = [];

        this.routes.forEach(r => {
            if (r.checkpoints) totalCp += r.checkpoints.length;
            if (r.alerts) {
                totalAlerts += r.alerts.length;
                r.alerts.forEach(a => allAlerts.push({ ...a, routeName: r.routeName }));
            }
        });

        document.getElementById('statCheckpointsCount').innerText = totalCp;
        document.getElementById('statAlertsCount').innerText = totalAlerts;

        // Alerts panel
        const alertsContainer = document.getElementById('dashboardAlertsContainer');
        if (alertsContainer) {
            if (allAlerts.length > 0) {
                alertsContainer.innerHTML = allAlerts.map(a => `
                    <div class="alert-item-card ${a.severity}">
                        <div style="display:flex; justify-content:space-between; margin-bottom:4px;">
                            <strong style="color:var(--dark);">${a.routeName}</strong>
                            <span class="badge badge-${a.severity.toLowerCase()}">${a.severity} Severity</span>
                        </div>
                        <p style="font-size:0.85rem; color:var(--gray-700);">${a.message}</p>
                        <small style="color:var(--gray-600); display:block; margin-top:6px;">Issued at: ${a.issuedAt}</small>
                    </div>
                `).join('');

                // Update global top ticker
                const ticker = document.getElementById('globalAlertBanner');
                const tickerText = document.getElementById('alertBannerText');
                if (ticker && tickerText) {
                    tickerText.innerText = `${allAlerts[0].routeName}: ${allAlerts[0].message}`;
                    ticker.classList.remove('hidden');
                }
            } else {
                alertsContainer.innerHTML = `<p style="color:var(--gray-600); padding:10px;">All trail corridors are currently clear with normal weather.</p>`;
            }
        }
    },

    // --- Route Details Modal ---
    openRouteDetailsModal(routeId) {
        const route = this.routes.find(r => r.routeId === routeId);
        if (!route) return;

        const content = document.getElementById('routeDetailsContent');
        const checkpointsHtml = (route.checkpoints || []).map(cp => `
            <div style="display:flex; justify-content:space-between; padding:8px 0; border-bottom:1px solid var(--gray-200);">
                <span>📍 <strong>${cp.name}</strong></span>
                <span style="font-family:monospace; color:var(--gray-600); font-size:0.8rem;">${cp.latitude.toFixed(4)}, ${cp.longitude.toFixed(4)}</span>
            </div>
        `).join('');

        const facilitiesHtml = (route.facilities || []).map(f => `
            <div style="display:flex; justify-content:space-between; padding:8px 0; border-bottom:1px solid var(--gray-200);">
                <span>🏪 <strong>${f.facilityName}</strong> (${f.facilityType})</span>
                <span style="color:var(--gray-600); font-size:0.85rem;">Location: ${f.location}</span>
            </div>
        `).join('');

        const weather = route.weather || { condition: "Clear", temperature: 24.0, forecastTime: "Today" };

        content.innerHTML = `
            <div style="border-bottom:1px solid var(--gray-200); padding-bottom:16px; margin-bottom:16px;">
                <span class="badge badge-${route.difficulty.toLowerCase()}">${route.difficulty}</span>
                <h2 style="font-size:1.6rem; color:var(--dark); margin:8px 0;">${route.routeName}</h2>
                <p class="subtitle">Official Distance: <strong>${route.distance} km</strong> | Mountain Trail Guide</p>
            </div>

            <div style="display:grid; grid-template-columns: 1fr 1fr; gap:16px; margin-bottom:20px;">
                <div style="background:var(--gray-50); padding:14px; border-radius:var(--radius-sm);">
                    <h4 style="font-size:0.9rem; margin-bottom:8px;">🌤️ Weather Information</h4>
                    <p style="font-size:0.85rem;">Condition: <strong>${weather.condition}</strong></p>
                    <p style="font-size:0.85rem;">Temperature: <strong>${weather.temperature}°C</strong></p>
                    <p style="font-size:0.75rem; color:var(--gray-600);">Forecast Date: ${weather.forecastTime}</p>
                </div>
                <div style="background:var(--gray-50); padding:14px; border-radius:var(--radius-sm);">
                    <h4 style="font-size:0.9rem; margin-bottom:8px;">🛡️ Safety & Advisories</h4>
                    ${route.alerts && route.alerts.length > 0
                        ? `<p style="color:var(--danger); font-size:0.85rem;"><strong>${route.alerts[0].severity} Alert:</strong> ${route.alerts[0].message}</p>`
                        : `<p style="color:var(--primary); font-size:0.85rem;">No active hazard alerts. Normal trail safety precautions apply.</p>`}
                </div>
            </div>

            <div style="margin-bottom:20px;">
                <h4 style="font-size:0.95rem; margin-bottom:10px;">Checkpoints & Route Navigation Waypoints</h4>
                <div style="background:var(--gray-50); padding:12px; border-radius:var(--radius-sm);">
                    ${checkpointsHtml || '<p>No checkpoints listed.</p>'}
                </div>
            </div>

            <div style="margin-bottom:20px;">
                <h4 style="font-size:0.95rem; margin-bottom:10px;">Route Facilities (Water Points, Shelters, Medical)</h4>
                <div style="background:var(--gray-50); padding:12px; border-radius:var(--radius-sm);">
                    ${facilitiesHtml || '<p>No facilities listed.</p>'}
                </div>
            </div>

            <div style="display:flex; justify-content:flex-end; gap:10px;">
                <button class="btn btn-outline" onclick="App.closeRouteDetailsModal()">Close</button>
                <button class="btn btn-primary" onclick="App.closeRouteDetailsModal(); App.openBookingModal(${route.routeId});">Book Permit (₹500)</button>
            </div>
        `;

        document.getElementById('routeDetailsModal').classList.remove('hidden');
    },

    closeRouteDetailsModal() {
        document.getElementById('routeDetailsModal').classList.add('hidden');
    },

    // --- Booking & Payments ---
    openBookingModal(routeId) {
        if (!this.currentUser) {
            this.showToast('Please sign in or create an account to book permits.', 'info');
            this.openAuthModal('login');
            return;
        }

        const route = this.routes.find(r => r.routeId === routeId) || this.routes[0];
        if (!route) return;

        this.selectedRoute = route;
        document.getElementById('bookingRouteId').value = route.routeId;
        document.getElementById('bookingRouteSubtitle').innerText = `${route.routeName} (${route.difficulty} • ${route.distance} km)`;

        // Set default dates (tomorrow to +2 days)
        const d = new Date();
        d.setDate(d.getDate() + 1);
        const startStr = d.toISOString().split('T')[0];
        d.setDate(d.getDate() + 2);
        const endStr = d.toISOString().split('T')[0];

        document.getElementById('bookStartDate').value = startStr;
        document.getElementById('bookEndDate').value = endStr;

        this.updateNetworkUI();
        document.getElementById('bookingModal').classList.remove('hidden');
    },

    closeBookingModal() {
        document.getElementById('bookingModal').classList.add('hidden');
    },

    async handleBookingSubmit(e) {
        e.preventDefault();
        const routeId = parseInt(document.getElementById('bookingRouteId').value);
        const startDate = document.getElementById('bookStartDate').value;
        const endDate = document.getElementById('bookEndDate').value;
        const amount = 500.00;
        const userId = this.currentUser ? this.currentUser.userId : 101;

        this.activePendingBooking = {
            userId,
            routeId,
            startDate,
            endDate,
            amount,
            route: this.selectedRoute || this.routes.find(r => r.routeId === routeId) || { routeName: "Trek Permit", distance: 12.5 }
        };

        this.closeBookingModal();
        this.openPaymentSandboxModal(this.activePendingBooking);
    },

    openPaymentSandboxModal(bookingData) {
        this.activePendingBooking = bookingData;
        const info = document.getElementById('sandboxRouteInfo');
        if (info && bookingData && bookingData.route) {
            info.innerText = `Trail: ${bookingData.route.routeName} | Dates: ${bookingData.startDate} to ${bookingData.endDate} | Amount: ₹${bookingData.amount.toFixed(2)}`;
        }
        const cardHolder = document.getElementById('cardHolderDisplay');
        if (cardHolder) {
            cardHolder.innerText = (this.currentUser ? this.currentUser.name : "Roshan") + " (Trekker)";
        }

        if (!this.isOnline) {
            this.switchPaymentMethod('offline');
        } else {
            this.switchPaymentMethod('upi');
        }

        document.getElementById('paymentSandboxModal').classList.remove('hidden');
    },

    closePaymentSandboxModal() {
        document.getElementById('paymentSandboxModal').classList.add('hidden');
    },

    switchPaymentMethod(method) {
        const methods = ['card', 'upi', 'netbanking', 'offline'];
        methods.forEach(m => {
            const btn = document.getElementById('payTabBtn' + m.charAt(0).toUpperCase() + m.slice(1));
            const panel = document.getElementById('payMethod' + m.charAt(0).toUpperCase() + m.slice(1));
            if (btn) btn.classList.toggle('active', m === method);
            if (panel) {
                panel.classList.toggle('hidden', m !== method);
                panel.classList.toggle('active', m === method);
            }
        });
    },

    async executeTestPayment(outcome) {
        if (!this.activePendingBooking) {
            this.showToast('No active booking selected for payment.', 'error');
            return;
        }

        const booking = this.activePendingBooking;
        const userId = booking.userId || (this.currentUser ? this.currentUser.userId : 101);

        // 1. OFFLINE SIMULATION
        if (outcome === 'Offline' || !this.isOnline) {
            const offlineItem = {
                id: 'offline_' + Date.now(),
                userId,
                routeId: booking.routeId,
                startDate: booking.startDate,
                endDate: booking.endDate,
                amount: booking.amount,
                timestamp: new Date().toISOString(),
                routeName: booking.route ? booking.route.routeName : 'Trekking Route'
            };

            const queue = this.getOfflineQueue();
            queue.push(offlineItem);
            this.saveOfflineQueue(queue);

            this.closePaymentSandboxModal();
            this.showToast('Remote Mountain Mode: Payment saved in local queue. Will auto-sync when network returns.', 'info');

            this.showReceipt({
                trip: { tripId: 'PENDING-SYNC', startDate: booking.startDate, endDate: booking.endDate, status: 'Planned (Offline)' },
                payment: { paymentId: 'OFFLINE-' + Date.now().toString().slice(-4), amount: booking.amount, status: 'Pending', paymentDate: new Date().toISOString().split('T')[0] },
                route: booking.route,
                isOffline: true
            });
            this.loadUserTrips();
            return;
        }

        // 2. EXISTING TRIP CONFIRMATION
        if (booking.existingTripId && booking.paymentId) {
            try {
                const res = await fetch('/api/payments/status', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        paymentId: booking.paymentId,
                        status: outcome === 'Success' ? 'Success' : 'Failed'
                    })
                });
                const data = await res.json();
                this.closePaymentSandboxModal();
                if (outcome === 'Success') {
                    this.showToast('Payment confirmed successfully in Test Mode!', 'success');
                    this.loadUserTrips();
                    this.showExistingReceipt(booking.existingTripId);
                } else {
                    this.showToast('Test Mode: Payment simulated as Failed/Declined.', 'error');
                    this.loadUserTrips();
                }
            } catch (e) {
                this.showToast('Network error while updating payment status.', 'error');
            }
            return;
        }

        // 3. NEW BOOKING ONLINE (Success or Failure test)
        try {
            const res = await fetch('/api/trips/book', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    userId,
                    routeId: booking.routeId,
                    startDate: booking.startDate,
                    endDate: booking.endDate,
                    amount: booking.amount,
                    isOffline: false
                })
            });
            const data = await res.json();
            this.closePaymentSandboxModal();

            if (data.success) {
                const tripData = data.booking;
                if (outcome === 'Success') {
                    this.showToast('Test Payment Confirmed! Official Permit generated.', 'success');
                    this.loadUserTrips();
                    this.showReceipt(tripData);
                } else {
                    // Simulate Failure on the newly created payment
                    if (tripData.payment && tripData.payment.paymentId) {
                        await fetch('/api/payments/status', {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify({ paymentId: tripData.payment.paymentId, status: 'Failed' })
                        });
                    }
                    this.showToast('Test Mode: Bank card simulated as Declined. You can re-attempt from My Bookings.', 'error');
                    this.loadUserTrips();
                }
            } else {
                this.showToast('Booking failed.', 'error');
            }
        } catch (err) {
            this.showToast('Payment processing error.', 'error');
        }
    },

    openPaymentTestModalForTrip(tripId) {
        const t = this.trips.find(x => x.tripId === tripId);
        if (!t) return;
        const paymentId = t.payment ? t.payment.paymentId : 0;
        const bookingData = {
            existingTripId: t.tripId,
            paymentId: paymentId,
            userId: t.userId,
            routeId: t.routeId,
            startDate: t.startDate,
            endDate: t.endDate,
            amount: t.payment ? t.payment.amount : 500.00,
            route: t.route || { routeName: "Trekking Route", distance: 12.5 }
        };
        this.openPaymentSandboxModal(bookingData);
    },

    async loadUserTrips() {
        if (!this.currentUser) {
            this.trips = [];
            this.renderUserTrips();
            return;
        }
        const userId = this.currentUser.userId;
        try {
            const res = await fetch(`/api/trips?userId=${userId}`);
            const data = await res.json();
            this.trips = data.trips || [];
            this.renderUserTrips();
        } catch (e) {
            console.error('Failed to load trips', e);
        }
    },

    renderUserTrips() {
        const tbody = document.getElementById('userTripsTableBody');
        if (!tbody) return;

        if (!this.currentUser) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="7" style="text-align:center; padding:36px 16px;">
                        <h4 style="color:var(--dark); font-size:1.05rem; margin-bottom:6px;">You are currently browsing as Guest</h4>
                        <p style="color:var(--gray-600); font-size:0.85rem; margin-bottom:14px;">Sign in or create a new trekker account to manage trips, permits, and payment receipts.</p>
                        <div style="display:inline-flex; gap:8px;">
                            <button class="btn btn-primary btn-sm" onclick="App.openAuthModal('login')">Sign In</button>
                            <button class="btn btn-outline btn-sm" onclick="App.openAuthModal('register')">Register New Account</button>
                        </div>
                    </td>
                </tr>
            `;
            return;
        }

        const offlineQueue = this.getOfflineQueue();
        let rowsHtml = '';

        // First render any offline items
        offlineQueue.forEach(item => {
            rowsHtml += `
                <tr style="background:#fffbeb;">
                    <td><strong style="color:var(--warning);">OFFLINE-QUEUE</strong></td>
                    <td>${item.routeName}</td>
                    <td>${item.startDate} to ${item.endDate}</td>
                    <td><span class="badge badge-warning">Pending Network</span></td>
                    <td>₹${item.amount.toFixed(2)} (Cached Locally)</td>
                    <td><span class="badge badge-warning">Awaiting Sync</span></td>
                    <td>
                        <button class="btn btn-sm btn-warning" onclick="App.syncPendingOfflineBookings()">Sync Now</button>
                    </td>
                </tr>
            `;
        });

        // Server trips
        this.trips.forEach(t => {
            const routeName = t.route ? t.route.routeName : 'Trail Route';
            const paymentStatus = t.payment ? t.payment.status : 'Success';
            const syncStatus = t.sync ? t.sync.syncStatus : 'Completed';
            const canTestPay = paymentStatus !== 'Success';

            rowsHtml += `
                <tr>
                    <td><strong>#${t.tripId}</strong></td>
                    <td>${routeName}</td>
                    <td>${t.startDate} to ${t.endDate}</td>
                    <td><span class="badge badge-${t.status === 'Completed' ? 'nature' : (t.status === 'Payment Failed' ? 'danger' : 'primary')}">${t.status}</span></td>
                    <td>₹${t.payment ? t.payment.amount.toFixed(2) : '500.00'} (${paymentStatus})</td>
                    <td><span class="badge badge-${syncStatus === 'Completed' ? 'success' : 'warning'}">${syncStatus}</span></td>
                    <td>
                        <div style="display:flex; gap:6px; flex-wrap:wrap;">
                            ${canTestPay ? `<button class="btn btn-warning btn-sm" onclick="App.openPaymentTestModalForTrip(${t.tripId})">💳 Confirm Payment (Test Mode)</button>` : ''}
                            <button class="btn btn-outline btn-sm" onclick="App.showExistingReceipt(${t.tripId})">Receipt / Invoice</button>
                        </div>
                    </td>
                </tr>
            `;
        });

        tbody.innerHTML = rowsHtml || `<tr><td colspan="7" style="text-align:center; padding:30px; color:var(--gray-600);">No trip permits booked yet.</td></tr>`;
    },

    // --- Official Receipt / Invoice Modal (Chapter 5.2) ---
    showExistingReceipt(tripId) {
        const t = this.trips.find(x => x.tripId === tripId);
        if (!t) return;
        this.showReceipt({
            trip: t,
            payment: t.payment || { paymentId: 501, amount: 500, status: 'Success', paymentDate: t.startDate },
            route: t.route || { routeName: "Harishchandragad", distance: 12.5 },
            sync: t.sync || { syncStatus: "Completed", lastSyncedAt: t.startDate },
            isOffline: false
        });
    },

    showReceipt(bookingData) {
        const area = document.getElementById('printableReceiptArea');
        const user = this.currentUser || { name: "Roshan", phone: "9876543210", email: "roshan@gmail.com" };
        const trip = bookingData.trip || {};
        const payment = bookingData.payment || {};
        const route = bookingData.route || { routeName: "Harishchandragad", distance: 12.5 };
        const isOffline = bookingData.isOffline;

        area.innerHTML = `
            <div class="receipt-header">
                <div style="font-size:0.8rem; font-weight:700; color:var(--primary); letter-spacing:1px; text-transform:uppercase;">Government Forest Department & Tourist Safety Bureau</div>
                <h3 class="receipt-title">TrailSafe Trekking Permit & Payment Invoice</h3>
                <p style="font-size:0.75rem; color:var(--gray-600);">Issued under National Wilderness Recreation and Safety Protocols</p>
                <div class="receipt-stamp">${isOffline ? 'OFFLINE TRANSACTION' : 'OFFICIALLY VERIFIED & SECURED'}</div>
            </div>

            <div class="receipt-meta-grid">
                <div>
                    <span style="color:var(--gray-600);">Booking Reference:</span>
                    <strong>TRIP-${trip.tripId}</strong>
                </div>
                <div>
                    <span style="color:var(--gray-600);">Payment Transaction ID:</span>
                    <strong>PAY-${payment.paymentId}</strong>
                </div>
                <div>
                    <span style="color:var(--gray-600);">Trekker Name:</span>
                    <strong>${user.name}</strong>
                </div>
                <div>
                    <span style="color:var(--gray-600);">Contact Phone:</span>
                    <strong>${user.phone || '9876543210'}</strong>
                </div>
                <div>
                    <span style="color:var(--gray-600);">Permit Dates:</span>
                    <strong>${trip.startDate} to ${trip.endDate}</strong>
                </div>
                <div>
                    <span style="color:var(--gray-600);">Designated Trail:</span>
                    <strong>${route.routeName} (${route.distance || '12.5'} km)</strong>
                </div>
            </div>

            <table class="receipt-table">
                <thead>
                    <tr>
                        <th>Item Description</th>
                        <th>Coverage</th>
                        <th style="text-align:right;">Amount (INR)</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>Wildlife Sanctuary & Eco-Trail Permit</td>
                        <td>Valid for designated dates</td>
                        <td style="text-align:right;">₹350.00</td>
                    </tr>
                    <tr>
                        <td>Rapid SOS & Mountain Search Coverage</td>
                        <td>Active 24/7 during trek</td>
                        <td style="text-align:right;">₹150.00</td>
                    </tr>
                    <tr style="font-weight:bold; border-top:2px solid var(--dark);">
                        <td colspan="2">Total Paid</td>
                        <td style="text-align:right; color:var(--primary);">₹${payment.amount ? payment.amount.toFixed(2) : '500.00'}</td>
                    </tr>
                </tbody>
            </table>

            <div style="font-size:0.75rem; color:var(--gray-600); margin-top:12px; border-top:1px solid var(--gray-200); padding-top:8px;">
                <p>Status: <strong>${payment.status || 'Success'}</strong> | Sync Record: <strong>${isOffline ? 'Pending Automatic Network Sync' : 'Synchronized with TrailSafe Central Registry'}</strong></p>
                <p>In case of emergency, use the SOS button on TrailSafe or call <strong>112</strong> / <strong>108</strong>.</p>
            </div>
        `;

        document.getElementById('receiptModal').classList.remove('hidden');
    },

    closeReceiptModal() {
        document.getElementById('receiptModal').classList.add('hidden');
    },

    // --- Interactive Map & Live Location Sharing ---
    initMap() {
        if (typeof L === 'undefined') return;
        const mapElement = document.getElementById('leafletMap');
        if (!mapElement) return;

        // Centered around Harishchandragad / Sahyadri mountain region (Maharashtra, India)
        this.map = L.map('leafletMap', {
            tap: false,
            scrollWheelZoom: false
        }).setView([19.3900, 73.7800], 12);

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; OpenStreetMap contributors | TrailSafe Smart Trekking'
        }).addTo(this.map);

        this.renderMapFeatures();
    },

    renderMapFeatures() {
        if (!this.map) return;

        // Clear existing markers
        this.mapMarkers.forEach(m => this.map.removeLayer(m));
        this.mapMarkers = [];

        // Checkpoints (Red Pins)
        const sampleCheckpoints = [
            { name: "Base Camp (Khireshwar)", lat: 19.3820, lng: 73.7950 },
            { name: "Tolar Khind Pass", lat: 19.3872, lng: 73.7820 },
            { name: "Harishchandreshwar Temple", lat: 19.3900, lng: 73.7780 },
            { name: "Konkan Kada Cliff Edge", lat: 19.3930, lng: 73.7740 }
        ];

        const lineCoords = [];

        sampleCheckpoints.forEach(cp => {
            lineCoords.push([cp.lat, cp.lng]);
            const marker = L.circleMarker([cp.lat, cp.lng], {
                radius: 7,
                fillColor: '#ef4444',
                color: '#ffffff',
                weight: 2,
                opacity: 1,
                fillOpacity: 0.9
            }).addTo(this.map);

            marker.bindPopup(`<strong>📍 Checkpoint</strong><br>${cp.name}<br><small>Lat: ${cp.lat}, Lng: ${cp.lng}</small>`);
            this.mapMarkers.push(marker);
        });

        // Trail route path line
        const polyline = L.polyline(lineCoords, { color: '#059669', weight: 4, opacity: 0.8, dashArray: '6, 8' }).addTo(this.map);
        this.mapMarkers.push(polyline);

        // Route Facilities (Blue Pins)
        const sampleFacilities = [
            { name: "Water Point (Km 3)", lat: 19.3850, lng: 73.7880, type: "Water Shelter" },
            { name: "Forest Rest Shelter", lat: 19.3872, lng: 73.7820, type: "Rest Area" },
            { name: "Emergency First-Aid Post", lat: 19.3820, lng: 73.7950, type: "Medical Aid" }
        ];

        sampleFacilities.forEach(f => {
            const marker = L.circleMarker([f.lat, f.lng], {
                radius: 6,
                fillColor: '#3b82f6',
                color: '#ffffff',
                weight: 2,
                opacity: 1,
                fillOpacity: 0.9
            }).addTo(this.map);

            marker.bindPopup(`<strong>🏪 Route Facility</strong><br>${f.name}<br>Type: ${f.type}`);
            this.mapMarkers.push(marker);
        });

        // User Live Marker (Green pulsing pin)
        this.userMarker = L.circleMarker([this.currentCoords.lat, this.currentCoords.lng], {
            radius: 9,
            fillColor: '#10b981',
            color: '#ffffff',
            weight: 3,
            opacity: 1,
            fillOpacity: 1
        }).addTo(this.map);

        this.userMarker.bindPopup(`<strong>👤 Live Trekker Position</strong><br>Roshan (User ID 101)<br>Status: Sharing Active`);
    },

    toggleLocationSharing() {
        if (!this.currentUser) {
            this.showToast('Please sign in or register to share your live location with emergency contacts.', 'info');
            this.openAuthModal('login');
            return;
        }

        this.isSharingLocation = !this.isSharingLocation;
        const pill = document.getElementById('trackingStatusPill');
        const text = document.getElementById('trackingStatusText');
        const btn = document.getElementById('toggleSharingBtn');

        if (this.isSharingLocation) {
            pill.classList.add('active');
            text.innerText = 'Sharing Status: Active (Live GPS)';
            btn.className = 'btn btn-danger';
            btn.innerText = 'Stop Live Sharing';
            this.showToast('Live location sharing activated. Coordinates sent to emergency contact.', 'success');

            // Periodic slight simulation or real GPS update
            this.sharingInterval = setInterval(() => {
                this.updateLivePosition();
            }, 6000);
        } else {
            pill.classList.remove('active');
            text.innerText = 'Sharing Status: Inactive';
            btn.className = 'btn btn-success';
            btn.innerText = 'Start Live Sharing';
            clearInterval(this.sharingInterval);
            this.showToast('Live location sharing paused.', 'info');
        }
    },

    updateLivePosition() {
        // Move slightly along the mountain trail
        this.currentCoords.lat += (Math.random() - 0.5) * 0.0006;
        this.currentCoords.lng += (Math.random() - 0.5) * 0.0006;

        if (this.userMarker && this.map) {
            this.userMarker.setLatLng([this.currentCoords.lat, this.currentCoords.lng]);
        }

        // Send to backend
        fetch('/api/location/share', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                tripId: 201,
                latitude: this.currentCoords.lat,
                longitude: this.currentCoords.lng,
                status: 'Active'
            })
        }).catch(err => console.error('Location sync error', err));
    },

    // --- Emergency SOS ---
    openSOSModal() {
        // Update coordinates display
        document.getElementById('sosLatDisplay').innerText = this.currentCoords.lat.toFixed(6);
        document.getElementById('sosLngDisplay').innerText = this.currentCoords.lng.toFixed(6);
        document.getElementById('sosModal').classList.remove('hidden');
    },

    closeSOSModal() {
        document.getElementById('sosModal').classList.add('hidden');
    },

    async loadEmergencyServices() {
        try {
            const res = await fetch('/api/services');
            const data = await res.json();
            this.services = data.services || [];

            const select = document.getElementById('sosServiceSelect');
            if (select) {
                select.innerHTML = this.services.map(s => `
                    <option value="${s.serviceId}">🚨 ${s.serviceName} (${s.serviceType} – Tel: ${s.phone})</option>
                `).join('');
            }
        } catch (e) {
            console.error('Failed to load emergency services', e);
        }
    },

    async handleSendSOS(e) {
        e.preventDefault();
        const serviceId = parseInt(document.getElementById('sosServiceSelect').value);
        const userId = this.currentUser ? this.currentUser.userId : 101;
        const tripId = 201;

        try {
            const res = await fetch('/api/sos/send', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    userId,
                    tripId,
                    serviceId,
                    latitude: this.currentCoords.lat,
                    longitude: this.currentCoords.lng
                })
            });
            const data = await res.json();
            if (data.success) {
                this.closeSOSModal();
                alert(`🚨 SOS EMERGENCY ALERT TRANSMITTED!\n\nRescue Service: Mountain Rescue Unit has been dispatched to coordinates:\nLatitude: ${this.currentCoords.lat.toFixed(6)}\nLongitude: ${this.currentCoords.lng.toFixed(6)}\n\nStay at your location, conserve battery, and await beacon contact.`);
                this.showToast('SOS alert registered with regional emergency services.', 'error');
                if (this.currentAdmin) this.loadAdminData();
            } else {
                this.showToast('Failed to dispatch SOS alert.', 'error');
            }
        } catch (err) {
            this.showToast('SOS dispatch network failure. If in immediate danger, dial 112 directly.', 'error');
        }
    },

    // --- Feedback ---
    async loadFeedbacks() {
        try {
            const res = await fetch('/api/feedback');
            const data = await res.json();
            const list = document.getElementById('feedbacksList');
            if (list) {
                list.innerHTML = (data.feedback || []).map(fb => `
                    <div class="review-item">
                        <div class="review-header">
                            <span class="review-author">${fb.userName || 'Trekker'}</span>
                            <span class="review-date">${fb.createdAt || 'Recent'}</span>
                        </div>
                        <div class="review-stars">${'★'.repeat(fb.rating)}${'☆'.repeat(5 - fb.rating)}</div>
                        <p style="font-size:0.85rem; color:var(--gray-700);">${fb.comments}</p>
                    </div>
                `).join('');
            }
        } catch (e) {
            console.error('Failed to load feedback', e);
        }
    },

    async handleFeedbackSubmit(e) {
        e.preventDefault();
        if (!this.currentUser) {
            this.showToast('Please sign in or register to submit feedback.', 'info');
            this.openAuthModal('login');
            return;
        }

        const rating = parseInt(document.getElementById('feedbackRatingInput').value);
        const comments = document.getElementById('feedbackComments').value;
        const userId = this.currentUser.userId;

        try {
            const res = await fetch('/api/feedback/submit', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ userId, rating, comments })
            });
            const data = await res.json();
            if (data.success) {
                document.getElementById('feedbackComments').value = '';
                this.showToast('Thank you! Your feedback has been recorded.', 'success');
                this.loadFeedbacks();
            }
        } catch (err) {
            this.showToast('Failed to submit feedback.', 'error');
        }
    },

    // --- Admin Portal ---
    populateAdminRouteDropdown() {
        const select = document.getElementById('alertRouteSelect');
        if (select) {
            select.innerHTML = this.routes.map(r => `
                <option value="${r.routeId}">${r.routeName} (${r.difficulty})</option>
            `).join('');
        }
    },

    async loadAdminData() {
        // Load SOS Requests
        try {
            const res = await fetch('/api/sos/list');
            const data = await res.json();
            const tbody = document.getElementById('adminSosTableBody');
            if (tbody) {
                tbody.innerHTML = (data.sosRequests || []).map(sos => `
                    <tr>
                        <td><strong>#${sos.sosId}</strong></td>
                        <td>${sos.userName}</td>
                        <td>${sos.userPhone}</td>
                        <td>${sos.routeName}</td>
                        <td style="font-family:monospace; font-size:0.8rem;">${sos.latitude.toFixed(4)}, ${sos.longitude.toFixed(4)}</td>
                        <td>${sos.serviceName}</td>
                        <td>${sos.timestamp}</td>
                        <td><span class="badge badge-${sos.status === 'Resolved' ? 'nature' : 'danger'}">${sos.status}</span></td>
                        <td>
                            ${sos.status !== 'Resolved' ? `
                                <button class="btn btn-sm btn-primary" onclick="App.updateSOSStatus(${sos.sosId}, 'Acknowledged')">Acknowledge</button>
                                <button class="btn btn-sm btn-success" onclick="App.updateSOSStatus(${sos.sosId}, 'Resolved')">Resolve</button>
                            ` : `<span style="color:var(--primary); font-weight:700;">Completed</span>`}
                        </td>
                    </tr>
                `).join('') || `<tr><td colspan="9" style="text-align:center; padding:20px;">No SOS requests.</td></tr>`;
            }
        } catch (e) {
            console.error('Failed to load SOS for admin', e);
        }

        // Load Users
        try {
            const res = await fetch('/api/admin/users');
            const data = await res.json();
            const tbody = document.getElementById('adminUsersTableBody');
            if (tbody) {
                tbody.innerHTML = (data.users || []).map(u => {
                    const ec = u.emergencyContact || { name: "N/A", phone: "N/A", relationship: "N/A" };
                    return `
                        <tr>
                            <td><strong>${u.userId}</strong></td>
                            <td>${u.name}</td>
                            <td>${u.email}</td>
                            <td>${u.phone}</td>
                            <td>${ec.name}</td>
                            <td>${ec.phone}</td>
                            <td><span class="badge badge-nature">${ec.relationship}</span></td>
                        </tr>
                    `;
                }).join('');
            }
        } catch (e) {
            console.error('Failed to load users for admin', e);
        }

        // Load Payments
        try {
            const res = await fetch('/api/admin/payments');
            const data = await res.json();
            const tbody = document.getElementById('adminPaymentsTableBody');
            if (tbody) {
                tbody.innerHTML = (data.payments || []).map(p => `
                    <tr>
                        <td><strong>#${p.paymentId}</strong></td>
                        <td>${p.userName}</td>
                        <td>${p.routeName}</td>
                        <td>₹${p.amount.toFixed(2)}</td>
                        <td>${p.paymentDate}</td>
                        <td><span class="badge badge-${p.status === 'Success' ? 'success' : 'warning'}">${p.status}</span></td>
                    </tr>
                `).join('');
            }
        } catch (e) {
            console.error('Failed to load payments for admin', e);
        }

        // Load Alerts
        try {
            const res = await fetch('/api/alerts');
            const data = await res.json();
            const container = document.getElementById('adminAlertsList');
            if (container) {
                container.innerHTML = (data.alerts || []).map(a => `
                    <div class="alert-item-card ${a.severity}">
                        <div style="display:flex; justify-content:space-between;">
                            <strong>${a.routeName}</strong>
                            <span class="badge badge-${a.severity.toLowerCase()}">${a.severity}</span>
                        </div>
                        <p style="font-size:0.85rem; margin-top:4px;">${a.message}</p>
                        <small style="color:var(--gray-600);">${a.issuedAt}</small>
                    </div>
                `).join('');
            }
        } catch (e) {
            console.error('Failed to load alerts for admin', e);
        }
    },

    async updateSOSStatus(sosId, status) {
        try {
            const res = await fetch('/api/sos/status', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ sosId, status })
            });
            const data = await res.json();
            if (data.success) {
                this.showToast(`SOS incident marked as ${status}.`, 'info');
                this.loadAdminData();
            }
        } catch (e) {
            this.showToast('Failed to update status', 'error');
        }
    },

    async handleCreateAlert(e) {
        e.preventDefault();
        const routeId = parseInt(document.getElementById('alertRouteSelect').value);
        const severity = document.getElementById('alertSeverity').value;
        const message = document.getElementById('alertMessage').value;

        try {
            const res = await fetch('/api/alerts/create', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    adminId: this.currentAdmin ? this.currentAdmin.adminId : 1,
                    routeId,
                    severity,
                    message
                })
            });
            const data = await res.json();
            if (data.success) {
                document.getElementById('alertMessage').value = '';
                this.showToast('Safety alert broadcasted successfully.', 'success');
                this.loadRoutes();
                this.loadAdminData();
            }
        } catch (e) {
            this.showToast('Failed to broadcast alert', 'error');
        }
    },

    // --- Toast Notifications ---
    showToast(msg, type = 'info') {
        const container = document.getElementById('toastContainer');
        if (!container) return;

        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.innerText = msg;
        container.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = '0';
            setTimeout(() => toast.remove(), 300);
        }, 4000);
    }
};

// Initialize application on DOM content loaded
document.addEventListener('DOMContentLoaded', () => {
    App.init();
});
