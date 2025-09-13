(function () {
  'use strict';

  var SERVER_IP = 'c3.play2go.cloud:20071';

  // Copy IP logic with toast
  function copyIpToClipboard() {
    if (!navigator.clipboard) {
      // Fallback
      var textArea = document.createElement('textarea');
      textArea.value = SERVER_IP;
      document.body.appendChild(textArea);
      textArea.focus();
      textArea.select();
      try { document.execCommand('copy'); } catch (_) {}
      document.body.removeChild(textArea);
      showToast('IP скопирован!');
      return;
    }
    navigator.clipboard.writeText(SERVER_IP).then(function () {
      showToast('IP скопирован!');
    }, function () {
      showToast('Не удалось скопировать IP', true);
    });
  }

  function showToast(message, isError) {
    var toast = document.getElementById('toast');
    if (!toast) return;
    toast.textContent = message;
    toast.style.borderColor = isError ? 'var(--danger)' : '#2b2049';
    toast.classList.add('show');
    window.clearTimeout(showToast._t);
    showToast._t = window.setTimeout(function () {
      toast.classList.remove('show');
    }, 2200);
  }

  // Init copy buttons
  function initCopyButtons() {
    var buttons = document.querySelectorAll('[data-copy-ip]');
    for (var i = 0; i < buttons.length; i++) {
      buttons[i].addEventListener('click', copyIpToClipboard);
    }
  }

  // Simple slider
  function initSlider() {
    var track = document.querySelector('[data-track]');
    if (!track) return;
    var slides = track.children;
    var index = 0;
    function update() {
      track.style.transform = 'translateX(' + (-index * 100) + '%)';
    }
    var prev = document.querySelector('[data-prev]');
    var next = document.querySelector('[data-next]');
    if (prev) prev.addEventListener('click', function () {
      index = (index - 1 + slides.length) % slides.length;
      update();
    });
    if (next) next.addEventListener('click', function () {
      index = (index + 1) % slides.length;
      update();
    });
    // Auto-advance
    var reduce = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (!reduce) {
      setInterval(function () {
        index = (index + 1) % slides.length;
        update();
      }, 5000);
    }
  }

  // Background canvas particles + runic glow
  function initBackgroundCanvas() {
    var canvas = document.getElementById('bg-canvas');
    if (!canvas) return;
    var ctx = canvas.getContext('2d');
    var dpr = Math.max(1, Math.floor(window.devicePixelRatio || 1));
    var particles = [];
    var maxParticles = 120;
    var reduce = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (reduce) maxParticles = 40;

    function resize() {
      canvas.width = window.innerWidth * dpr;
      canvas.height = window.innerHeight * dpr;
    }
    window.addEventListener('resize', resize);
    resize();

    function spawnParticle() {
      var size = Math.random() * 2 + 1; // px
      var speed = Math.random() * 0.2 + 0.05;
      particles.push({
        x: Math.random() * canvas.width,
        y: Math.random() * canvas.height,
        vx: (Math.random() - 0.5) * 0.1,
        vy: -speed,
        size: size * dpr,
        alpha: Math.random() * 0.6 + 0.2
      });
    }

    for (var i = 0; i < maxParticles; i++) spawnParticle();

    // Runes positions
    var runes = [];
    for (var r = 0; r < 8; r++) {
      runes.push({
        x: Math.random() * 1,
        y: Math.random() * 0.6 + 0.1,
        s: Math.random() * 40 + 40,
        a: Math.random() * Math.PI * 2
      });
    }

    function drawRune(cx, cy, size, angle) {
      ctx.save();
      ctx.translate(cx, cy);
      ctx.rotate(angle);
      ctx.globalAlpha = 0.12;
      ctx.strokeStyle = 'rgba(173,113,255,0.6)';
      ctx.lineWidth = 2 * dpr;
      var s = size;
      // Simple square rune with inner cross
      ctx.beginPath();
      ctx.rect(-s/2, -s/2, s, s);
      ctx.stroke();
      ctx.beginPath();
      ctx.moveTo(-s/2, 0); ctx.lineTo(s/2, 0);
      ctx.moveTo(0, -s/2); ctx.lineTo(0, s/2);
      ctx.stroke();
      ctx.restore();
    }

    function tick() {
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      // Gradient ground
      var grd = ctx.createLinearGradient(0, 0, 0, canvas.height);
      grd.addColorStop(0, 'rgba(13,10,22,0)');
      grd.addColorStop(1, 'rgba(13,10,22,0.15)');
      ctx.fillStyle = grd;
      ctx.fillRect(0, 0, canvas.width, canvas.height);

      // Runes
      var w = canvas.width, h = canvas.height;
      for (var i = 0; i < runes.length; i++) {
        var rn = runes[i];
        drawRune(rn.x * w, rn.y * h, rn.s * dpr, rn.a);
        rn.a += 0.0006;
      }

      // Particles
      ctx.fillStyle = 'rgba(173,113,255,0.7)';
      for (var p = 0; p < particles.length; p++) {
        var pt = particles[p];
        // glow
        ctx.globalAlpha = pt.alpha * 0.35;
        ctx.beginPath();
        ctx.arc(pt.x, pt.y, pt.size * 2.2, 0, Math.PI * 2);
        ctx.fill();
        // core
        ctx.globalAlpha = pt.alpha;
        ctx.fillRect(pt.x, pt.y, pt.size, pt.size);
        pt.x += pt.vx * dpr;
        pt.y += pt.vy * dpr;
        if (pt.y < -10 * dpr) {
          pt.y = canvas.height + 10 * dpr;
          pt.x = Math.random() * canvas.width;
        }
      }

      requestAnimationFrame(tick);
    }

    tick();
  }

  // Boot
  function ready(fn) {
    if (document.readyState !== 'loading') fn();
    else document.addEventListener('DOMContentLoaded', fn);
  }

  ready(function () {
    initCopyButtons();
    initSlider();
    initBackgroundCanvas();
  });
})();

