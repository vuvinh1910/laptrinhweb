document.addEventListener('DOMContentLoaded', () => {
    // Select all copy buttons
    const copyButtons = document.querySelectorAll('.copy-btn');

    copyButtons.forEach(button => {
        button.addEventListener('click', () => {
            // Get the voucher code from the data-code attribute
            const code = button.getAttribute('data-code');

            // Copy the code to the clipboard
            navigator.clipboard.writeText(code)
                .then(() => {
                    // Find the associated copy-feedback element
                    const feedback = button.nextElementSibling;
                    if (feedback && feedback.classList.contains('copy-feedback')) {
                        // Show the "Copied!" feedback
                        feedback.style.display = 'inline';

                        // Hide the feedback after 2 seconds
                        setTimeout(() => {
                            feedback.style.display = 'none';
                        }, 2000);
                    }
                })
                .catch(err => {
                    console.error('Failed to copy: ', err);
                });
        });
    });
});