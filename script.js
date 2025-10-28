// Copy IP buttons
(function initCopyIP() {
  function copy(ip) {
    if (!ip) return;
    if (navigator.clipboard && navigator.clipboard.writeText) {
      navigator.clipboard.writeText(ip).then(() => showToast('IP скопирован: ' + ip));
    } else {
      const ta = document.createElement('textarea');
      ta.value = ip; document.body.appendChild(ta); ta.select();
      try { document.execCommand('copy'); showToast('IP скопирован: ' + ip); } finally { document.body.removeChild(ta); }
    }
  }
  const btnHero = document.getElementById('copy-ip-hero');
  const btnConnect = document.getElementById('copy-ip-connect');
  [btnHero, btnConnect].forEach(btn => btn && btn.addEventListener('click', () => copy(btn.getAttribute('data-ip'))));
})();

// Simple toast
function showToast(message) {
  const toast = document.createElement('div');
  toast.textContent = message;
  toast.style.position = 'fixed';
  toast.style.left = '50%';
  toast.style.bottom = '24px';
  toast.style.transform = 'translateX(-50%)';
  toast.style.background = '#160e23';
  toast.style.border = '2px solid #2a1a40';
  toast.style.color = '#e9ddff';
  toast.style.padding = '8px 12px';
  toast.style.borderRadius = '10px';
  toast.style.boxShadow = '0 0 14px rgba(179,140,255,.35)';
  toast.style.fontFamily = "'Minecraft', 'Minecraftia', 'Press Start 2P', system-ui, sans-serif";
  toast.style.fontSize = '12px';
  toast.style.zIndex = '10';
  toast.style.opacity = '0';
  toast.style.transition = 'opacity .2s ease, transform .2s ease';
  document.body.appendChild(toast);
  requestAnimationFrame(() => { toast.style.opacity = '1'; toast.style.transform = 'translateX(-50%) translateY(-4px)'; });
  setTimeout(() => {
    toast.style.opacity = '0'; toast.style.transform = 'translateX(-50%) translateY(0)';
    setTimeout(() => toast.remove(), 250);
  }, 1600);
}

// Slider
(function initSlider() {
  const track = document.getElementById('slider-track');
  if (!track) return;
  const prev = document.getElementById('slider-prev');
  const next = document.getElementById('slider-next');
  let index = 0;
  function update() { track.style.transform = 'translateX(' + (-index * 100) + '%)'; }
  function clamp() { if (index < 0) index = 2; if (index > 2) index = 0; }
  prev && prev.addEventListener('click', () => { index--; clamp(); update(); });
  next && next.addEventListener('click', () => { index++; clamp(); update(); });
  setInterval(() => { index++; clamp(); update(); }, 4000);
})();

// Background canvas (particles + runes)
(function initBackground() {
  const canvas = document.getElementById('bg-canvas');
  if (!canvas) return;
  const ctx = canvas.getContext('2d');
  let width, height, dpr;
  const particles = [];
  const runes = [];

  function resize() {
    dpr = Math.min(window.devicePixelRatio || 1, 2);
    width = canvas.clientWidth = window.innerWidth;
    height = canvas.clientHeight = window.innerHeight;
    canvas.width = Math.floor(width * dpr);
    canvas.height = Math.floor(height * dpr);
  }
  window.addEventListener('resize', resize, { passive: true });
  resize();

  function createParticles(count) {
    particles.length = 0;
    for (let i = 0; i < count; i++) {
      particles.push({
        x: Math.random() * width,
        y: Math.random() * height,
        size: 1 + Math.random() * 2,
        speedY: 0.1 + Math.random() * 0.4,
        speedX: -0.15 + Math.random() * 0.3,
        alpha: 0.15 + Math.random() * 0.35
      });
    }
  }
  function createRunes(count) {
    runes.length = 0;
    for (let i = 0; i < count; i++) {
      runes.push({
        x: Math.random() * width,
        y: Math.random() * height,
        r: 12 + Math.random() * 32,
        rot: Math.random() * Math.PI,
        rotSpeed: (Math.random() - 0.5) * 0.002
      });
    }
  }

  createParticles(140);
  createRunes(18);

  function drawParticle(p) {
    const s = p.size * dpr;
    ctx.globalAlpha = p.alpha;
    ctx.fillStyle = '#b38cff';
    ctx.fillRect(Math.floor(p.x * dpr), Math.floor(p.y * dpr), s, s);
    ctx.globalAlpha = 1;
  }

  function drawRune(r) {
    const cx = r.x * dpr, cy = r.y * dpr;
    const rad = r.r * dpr;
    ctx.save();
    ctx.translate(cx, cy);
    ctx.rotate(r.rot);
    const grad = ctx.createRadialGradient(0,0,0,0,0,rad);
    grad.addColorStop(0, 'rgba(179,140,255,0.35)');
    grad.addColorStop(1, 'rgba(179,140,255,0)');
    ctx.fillStyle = grad;
    ctx.beginPath();
    // draw 6-armed rune star (blocky)
    for (let i = 0; i < 6; i++) {
      const a = (Math.PI * 2 * i) / 6;
      const x = Math.cos(a) * (rad * 0.7);
      const y = Math.sin(a) * (rad * 0.7);
      ctx.fillRect(x - 2 * dpr, -rad * 0.1, 4 * dpr, rad * 0.2);
      ctx.rotate(Math.PI / 3);
    }
    ctx.fill();
    ctx.restore();
  }

  let last = 0;
  function frame(ts) {
    const dt = Math.min(32, ts - last);
    last = ts;
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    for (const r of runes) {
      r.rot += r.rotSpeed * dt;
      drawRune(r);
    }
    for (const p of particles) {
      p.y -= p.speedY; p.x += p.speedX;
      if (p.y < -4) p.y = height + 4;
      if (p.x < -4) p.x = width + 4; else if (p.x > width + 4) p.x = -4;
      drawParticle(p);
    }
    requestAnimationFrame(frame);
  }
  requestAnimationFrame(frame);
})();

