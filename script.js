// Global variables
let currentSlide = 0;
const slides = document.querySelectorAll('.slide');

// DOM Content Loaded
document.addEventListener('DOMContentLoaded', function() {
    initializeSlider();
    addScrollEffects();
    addParticleAnimation();
});

// Copy IP to clipboard function
function copyIP() {
    const ip = 'c3.play2go.cloud:20071';
    
    // Try modern clipboard API first
    if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(ip).then(function() {
            showNotification();
        }).catch(function(err) {
            // Fallback to legacy method
            fallbackCopyTextToClipboard(ip);
        });
    } else {
        // Fallback for older browsers
        fallbackCopyTextToClipboard(ip);
    }
}

// Fallback copy method for older browsers
function fallbackCopyTextToClipboard(text) {
    const textArea = document.createElement("textarea");
    textArea.value = text;
    
    // Avoid scrolling to bottom
    textArea.style.top = "0";
    textArea.style.left = "0";
    textArea.style.position = "fixed";
    textArea.style.opacity = "0";
    
    document.body.appendChild(textArea);
    textArea.focus();
    textArea.select();
    
    try {
        const successful = document.execCommand('copy');
        if (successful) {
            showNotification();
        }
    } catch (err) {
        console.error('Fallback: Oops, unable to copy', err);
    }
    
    document.body.removeChild(textArea);
}

// Show notification
function showNotification() {
    const notification = document.getElementById('notification');
    notification.classList.add('show');
    
    setTimeout(function() {
        notification.classList.remove('show');
    }, 3000);
}

// Initialize slider
function initializeSlider() {
    const slides = document.querySelectorAll('.slide');
    if (slides.length === 0) return;
    
    // Show first slide
    slides[0].classList.add('active');
    
    // Auto-slide every 5 seconds
    setInterval(function() {
        changeSlide(1);
    }, 5000);
}

// Change slide function
function changeSlide(direction) {
    const slides = document.querySelectorAll('.slide');
    if (slides.length === 0) return;
    
    // Remove active class from current slide
    slides[currentSlide].classList.remove('active');
    
    // Calculate new slide index
    currentSlide += direction;
    
    // Wrap around if necessary
    if (currentSlide >= slides.length) {
        currentSlide = 0;
    } else if (currentSlide < 0) {
        currentSlide = slides.length - 1;
    }
    
    // Add active class to new slide
    slides[currentSlide].classList.add('active');
}

// Add scroll effects
function addScrollEffects() {
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };
    
    const observer = new IntersectionObserver(function(entries) {
        entries.forEach(function(entry) {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
            }
        });
    }, observerOptions);
    
    // Observe elements for scroll animations
    const animatedElements = document.querySelectorAll('.feature-card, .step, .section-title');
    animatedElements.forEach(function(element) {
        element.style.opacity = '0';
        element.style.transform = 'translateY(30px)';
        element.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
        observer.observe(element);
    });
}

// Add particle animation enhancement
function addParticleAnimation() {
    const particles = document.querySelectorAll('.particle');
    
    particles.forEach(function(particle, index) {
        // Add random positions
        particle.style.left = Math.random() * 100 + '%';
        particle.style.top = Math.random() * 100 + '%';
        
        // Add random animation delays
        particle.style.animationDelay = (Math.random() * 8) + 's';
        
        // Add random sizes
        const size = Math.random() * 6 + 2; // 2-8px
        particle.style.width = size + 'px';
        particle.style.height = size + 'px';
        
        // Add random colors
        const colors = ['#8a2be2', '#9932cc', '#4b0082', '#663399'];
        const randomColor = colors[Math.floor(Math.random() * colors.length)];
        particle.style.background = randomColor;
        particle.style.boxShadow = `0 0 10px ${randomColor}`;
    });
}

// Add hover effects to buttons
document.addEventListener('DOMContentLoaded', function() {
    const buttons = document.querySelectorAll('.play-button, .copy-ip-button, .gallery-btn');
    
    buttons.forEach(function(button) {
        button.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-2px) scale(1.05)';
        });
        
        button.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0) scale(1)';
        });
    });
});

// Add floating animation to feature cards
document.addEventListener('DOMContentLoaded', function() {
    const featureCards = document.querySelectorAll('.feature-card');
    
    featureCards.forEach(function(card, index) {
        // Add slight delay to each card
        card.style.animationDelay = (index * 0.2) + 's';
        
        // Add floating animation
        card.addEventListener('mouseenter', function() {
            this.style.animation = 'none';
            this.style.transform = 'translateY(-15px) rotateY(5deg)';
        });
        
        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0) rotateY(0deg)';
        });
    });
});

// Add parallax effect to hero section
window.addEventListener('scroll', function() {
    const scrolled = window.pageYOffset;
    const parallaxElements = document.querySelectorAll('.floating-block');
    
    parallaxElements.forEach(function(element, index) {
        const speed = 0.5 + (index * 0.1);
        const yPos = -(scrolled * speed);
        element.style.transform = `translateY(${yPos}px) rotate(${scrolled * 0.1}deg)`;
    });
});

// Add glow effect to logo on scroll
window.addEventListener('scroll', function() {
    const logo = document.querySelector('.logo');
    const scrolled = window.pageYOffset;
    const maxScroll = 500;
    
    if (scrolled < maxScroll) {
        const intensity = 1 + (scrolled / maxScroll);
        logo.style.textShadow = `
            0 0 ${10 * intensity}px #8a2be2,
            0 0 ${20 * intensity}px #8a2be2,
            0 0 ${30 * intensity}px #8a2be2,
            0 0 ${40 * intensity}px #8a2be2
        `;
    }
});

// Add click sound effect (optional - commented out for now)
/*
function playClickSound() {
    // Create audio context for click sound
    const audioContext = new (window.AudioContext || window.webkitAudioContext)();
    const oscillator = audioContext.createOscillator();
    const gainNode = audioContext.createGain();
    
    oscillator.connect(gainNode);
    gainNode.connect(audioContext.destination);
    
    oscillator.frequency.setValueAtTime(800, audioContext.currentTime);
    oscillator.frequency.exponentialRampToValueAtTime(400, audioContext.currentTime + 0.1);
    
    gainNode.gain.setValueAtTime(0.1, audioContext.currentTime);
    gainNode.gain.exponentialRampToValueAtTime(0.001, audioContext.currentTime + 0.1);
    
    oscillator.start(audioContext.currentTime);
    oscillator.stop(audioContext.currentTime + 0.1);
}
*/

// Smooth scrolling for anchor links (if any are added later)
document.addEventListener('click', function(e) {
    if (e.target.matches('a[href^="#"]')) {
        e.preventDefault();
        const targetId = e.target.getAttribute('href').substring(1);
        const targetElement = document.getElementById(targetId);
        
        if (targetElement) {
            targetElement.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    }
});

// Add keyboard navigation for slider
document.addEventListener('keydown', function(e) {
    if (e.key === 'ArrowLeft') {
        changeSlide(-1);
    } else if (e.key === 'ArrowRight') {
        changeSlide(1);
    }
});

// Performance optimization: Throttle scroll events
function throttle(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = function() {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Apply throttling to scroll events
const throttledScroll = throttle(function() {
    // Scroll-based animations here
}, 16); // ~60fps

window.addEventListener('scroll', throttledScroll);