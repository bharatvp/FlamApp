const img = document.getElementById('frame') as HTMLImageElement;
const stats = document.getElementById('stats') as HTMLDivElement;

fetch('processed_base64.txt').then(r => r.text()).then(b64 => {
  img.src = 'data:image/png;base64,' + b64.trim();
  stats.innerText = 'Loaded static processed frame.';
}).catch(() => {
  stats.innerText = 'No processed_base64.txt found. Add one or produce a frame from app.';
});
