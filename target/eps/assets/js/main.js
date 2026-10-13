/**
 * Employee Performance Evaluation System (EPS)
 * Main JavaScript Helpers
 */

document.addEventListener('DOMContentLoaded', () => {
    // 1. Password visibility toggle
    const togglePasswordBtn = document.getElementById('togglePassword');
    const passwordInput = document.getElementById('password');
    if (togglePasswordBtn && passwordInput) {
        togglePasswordBtn.addEventListener('click', () => {
            const isPassword = passwordInput.getAttribute('type') === 'password';
            passwordInput.setAttribute('type', isPassword ? 'text' : 'password');
            togglePasswordBtn.innerHTML = isPassword ? 
                '<i class="bi bi-eye-slash"></i>' : 
                '<i class="bi bi-eye"></i>';
        });
    }

    // 2. Evaluation Form Live Weighted Score Preview
    const evalForm = document.getElementById('evaluationForm');
    if (evalForm) {
        const scoreSelects = evalForm.querySelectorAll('.criterion-score-select');
        const liveScoreDisplay = document.getElementById('liveCalculatedScore');
        const liveRatingDisplay = document.getElementById('liveCalculatedRating');

        function updateLiveScore() {
            let totalWeighted = 0;
            let totalWeight = 0;

            scoreSelects.forEach(select => {
                const score = parseFloat(select.value);
                const weight = parseFloat(select.getAttribute('data-weight')) || 16.67;
                if (!isNaN(score)) {
                    totalWeighted += score * weight;
                    totalWeight += weight;
                }
            });

            if (totalWeight > 0) {
                const finalScore = (totalWeighted / totalWeight).toFixed(2);
                if (liveScoreDisplay) liveScoreDisplay.textContent = finalScore;
                
                let rating = 'Unsatisfactory';
                let badgeClass = 'bg-danger';
                if (finalScore >= 4.5) {
                    rating = 'Outstanding';
                    badgeClass = 'bg-success';
                } else if (finalScore >= 3.5) {
                    rating = 'Exceeds Expectations';
                    badgeClass = 'bg-primary';
                } else if (finalScore >= 2.5) {
                    rating = 'Meets Expectations';
                    badgeClass = 'bg-info text-dark';
                } else if (finalScore >= 1.5) {
                    rating = 'Needs Improvement';
                    badgeClass = 'bg-warning text-dark';
                }

                if (liveRatingDisplay) {
                    liveRatingDisplay.textContent = rating;
                    liveRatingDisplay.className = 'badge ' + badgeClass;
                }
            }
        }

        scoreSelects.forEach(sel => sel.addEventListener('change', updateLiveScore));
        updateLiveScore();
    }
});

/**
 * Quick-fill helper for Demo credentials on Login page
 */
function fillLogin(username, password) {
    const userField = document.getElementById('username');
    const passField = document.getElementById('password');
    if (userField && passField) {
        userField.value = username;
        passField.value = password;
    }
}
