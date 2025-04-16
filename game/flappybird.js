const canvas = document.getElementById('gameCanvas');
const ctx = canvas.getContext('2d');

// Game constants
const WIDTH = 400;
const HEIGHT = 600;
const GROUND_HEIGHT = 100;
const PIPE_WIDTH = 60;
const PIPE_GAP = 150;
const BIRD_SIZE = 30;
const GRAVITY = 0.6;
const FLAP_STRENGTH = -9;
const PIPE_SPEED = 2.5;

// Game state
let birdY = HEIGHT / 2;
let birdVelocity = 0;
let pipes = [];
let score = 0;
let gameOver = false;
let started = false;
let titleY = 50;
let titleDirection = 1;
const titleFloatRange = 15;
const titleBaseY = 50;

function resetGame() {
    birdY = HEIGHT / 2;
    birdVelocity = 0;
    pipes = [];
    score = 0;
    gameOver = false;
    started = false;
    // Add initial pipes
    for (let i = 0; i < 3; i++) {
        addPipe(true);
    }
}

function addPipe(start) {
    const space = PIPE_GAP;
    const minHeight = 50;
    const maxHeight = HEIGHT - GROUND_HEIGHT - space - 100;
    const pipeHeight = minHeight + Math.floor(Math.random() * (maxHeight - minHeight + 1));
    const x = start ? WIDTH + pipes.length * 200 : pipes[pipes.length - 1].x + 200;
    pipes.push({ x: x, y: 0, width: PIPE_WIDTH, height: pipeHeight, scored: false }); // Top pipe
    pipes.push({ x: x, y: pipeHeight + space, width: PIPE_WIDTH, height: HEIGHT - pipeHeight - space - GROUND_HEIGHT, scored: false }); // Bottom pipe
}

function drawTitle() {
    ctx.save();
    ctx.font = 'bold 36px Arial';
    ctx.fillStyle = 'yellow';
    ctx.shadowColor = 'orange';
    ctx.shadowBlur = 6;
    ctx.fillText('Flappy bird V5', 60, titleY + 5);
    ctx.restore();
}

function draw() {
    // Background
    ctx.fillStyle = '#87ceeb';
    ctx.fillRect(0, 0, WIDTH, HEIGHT);
    // Ground
    ctx.fillStyle = 'orange';
    ctx.fillRect(0, HEIGHT - GROUND_HEIGHT, WIDTH, GROUND_HEIGHT);
    ctx.fillStyle = 'green';
    ctx.fillRect(0, HEIGHT - GROUND_HEIGHT, WIDTH, 20);
    // Pipes
    ctx.fillStyle = 'darkgreen';
    pipes.forEach(pipe => {
        ctx.fillRect(pipe.x, pipe.y, pipe.width, pipe.height);
    });
    // Bird
    ctx.fillStyle = 'red';
    ctx.beginPath();
    ctx.ellipse(80 + BIRD_SIZE / 2, birdY + BIRD_SIZE / 2, BIRD_SIZE / 2, BIRD_SIZE / 2, 0, 0, 2 * Math.PI);
    ctx.fill();
    // Title
    drawTitle();
    // Score
    ctx.font = 'bold 24px Arial';
    ctx.fillStyle = 'white';
    ctx.fillText('Score: ' + score, 10, 40);
    // Game over or start
    if (gameOver) {
        ctx.font = 'bold 36px Arial';
        ctx.fillStyle = 'red';
        ctx.fillText('Game Over!', 100, HEIGHT / 2 - 30);
        ctx.font = '20px Arial';
        ctx.fillStyle = 'black';
        ctx.fillText('Press SPACE to restart', 90, HEIGHT / 2 + 10);
    } else if (!started) {
        ctx.font = '20px Arial';
        ctx.fillStyle = 'black';
        ctx.fillText('Press SPACE to start', 110, HEIGHT / 2);
    }
}

function update() {
    if (started && !gameOver) {
        birdVelocity += GRAVITY;
        birdY += birdVelocity;
        // Move pipes
        for (let i = 0; i < pipes.length; i++) {
            pipes[i].x -= PIPE_SPEED;
        }
        // Remove pipes off screen, add new pipes
        if (pipes.length && pipes[0].x + PIPE_WIDTH < 0) {
            pipes.shift();
            pipes.shift();
            addPipe(false);
        }
        // Collision detection
        for (let pipe of pipes) {
            if (rectCircleColliding(80 + BIRD_SIZE / 2, birdY + BIRD_SIZE / 2, BIRD_SIZE / 2, pipe)) {
                gameOver = true;
            }
        }
        // Out of bounds
        if (birdY > HEIGHT - GROUND_HEIGHT - BIRD_SIZE || birdY < 0) {
            gameOver = true;
        }
        // Score
        for (let i = 0; i < pipes.length; i += 2) {
            let pipe = pipes[i];
            if (!pipe.scored && pipe.x + PIPE_WIDTH < 80) {
                score++;
                pipe.scored = true;
            }
        }
    }
    // Floating title animation
    titleY += titleDirection;
    if (titleY > titleBaseY + titleFloatRange || titleY < titleBaseY - titleFloatRange) {
        titleDirection *= -1;
    }
}

function rectCircleColliding(cx, cy, cr, rect) {
    // Find the closest point to the circle within the rectangle
    let closestX = Math.max(rect.x, Math.min(cx, rect.x + rect.width));
    let closestY = Math.max(rect.y, Math.min(cy, rect.y + rect.height));
    // Calculate the distance between the circle's center and this closest point
    let dx = cx - closestX;
    let dy = cy - closestY;
    // If the distance is less than the circle's radius, an intersection occurs
    return (dx * dx + dy * dy) < (cr * cr);
}

function gameLoop() {
    update();
    draw();
    requestAnimationFrame(gameLoop);
}

document.addEventListener('keydown', function(e) {
    if (e.code === 'Space') {
        if (!started) {
            started = true;
            birdVelocity = FLAP_STRENGTH;
        } else if (!gameOver) {
            birdVelocity = FLAP_STRENGTH;
        } else {
            resetGame();
        }
        e.preventDefault();
    }
});

resetGame();
gameLoop();
