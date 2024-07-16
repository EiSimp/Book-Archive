document.addEventListener("DOMContentLoaded", () => {
  const signupForm = document.getElementById("signupform");
  const usernameInput = document.getElementById("username");
  const usernameInvalid = document.querySelector("#username + .invalid-feedback");
  const passwordInput = document.getElementById("password");
  const passwordInvalid = document.querySelector("#password + .invalid-feedback");
  const emailInput = document.getElementById("email");
  const emailInvalid = document.querySelector("#email + .invalid-feedback");
  const passwordConfirmationInput = document.getElementById("password2");
  const passwordConfirmationInvalid = document.querySelector("#password2 + .invalid-feedback");

  let debounceTimer;
  function debounce(func, delay) {
    return function (...args) {
      clearTimeout(debounceTimer);
      debounceTimer = setTimeout(() => func.apply(this, args), delay);
    };
  }

  const checkUsernameWithDebounce = debounce(checkUsername, 300);
  const checkEmailWithDebounce = debounce(checkEmail, 300);

  usernameInput.addEventListener("input", checkUsernameWithDebounce);
  passwordInput.addEventListener("input", checkPassword);
  passwordConfirmationInput.addEventListener("input", checkPasswordConfirmation);
  emailInput.addEventListener("input", checkEmailWithDebounce);

  async function usernameIsTaken(username) {
    try {
      const response = await fetch(`/check-username?username=${encodeURIComponent(username)}`)
      if (!response.ok) {
        throw new Error(`Server error: ${response.status}`);
      }
      const data = await response.json();
      return data.exists;
    } catch (error) {
      console.error('error checking username uniqueness: ', error);
      return false;
    }
  }

  async function emailIsTaken(email) {
    try {
      const response = await fetch(`/check-email?email=${encodeURIComponent(email)}`);
      if (!response.ok) {
        throw new Error(`Server error: ${response.status}`);
      }
      const data = await response.json();
      return data.exists;
    } catch (error) {
      console.error('Error checking email uniqueness: ', error);
      return false;
    }
  }

  async function checkUsername() {
    usernameInput.className = "is-invalid";
    let isValid = false;
    
    if (usernameInput.validity.patternMismatch) {
      usernameInvalid.textContent = "Must contain only letters, numbers, and underscores";
    } else if (usernameInput.validity.valueMissing) {
      usernameInvalid.textContent = "Please enter a username";
    } else if (await usernameIsTaken(usernameInput.value)){
      usernameInvalid.textContent = "Username is taken";
    } else {
      usernameInput.className = "is-valid";
      isValid = true;
    }
    return isValid;
  }

  function checkPassword() {
    passwordInput.className = "is-invalid";
    let isValid = false;

    if (passwordInput.validity.valueMissing) {
      passwordInvalid.textContent = "Please enter a password";
    } else if (passwordInput.validity.patternMismatch) {
      passwordInvalid.textContent = "Must contain an uppercase letter, a number, and be at least 8 characters long";
    } else {
      passwordInput.className = "is-valid";
      isValid = true
    }
    return isValid;
  }

  function checkPasswordConfirmation() {
    passwordConfirmationInput.className = "is-invalid";
    let isValid = false;

    if (passwordConfirmationInput.value !== passwordInput.value) {
      passwordConfirmationInvalid.textContent = "The password confirmation does not match";
    } else if (passwordConfirmationInput.validity.valueMissing) {
      passwordConfirmationInvalid.textContent = "Please confirm your password";
    } else {
      passwordConfirmationInput.className = "is-valid";
      isValid = true;
    }
    return isValid;
  }

  async function checkEmail() {
    emailInput.className = "is-invalid";
    let isValid = false;

    if (emailInput.validity.patternMismatch) {
      emailInvalid.textContent = "Invalid email format";
    } else if (emailInput.validity.valueMissing) {
      emailInvalid.textContent = "Please enter an email";
    } else if (await emailIsTaken(emailInput.value)){
      emailInvalid.textContent = "Email is already taken";
    } else {
      emailInput.className = "is-valid";
      isValid = true;
    }
    return isValid;
  }

  signupForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    console.log("Form submission started");
    try {
      if (await checkUsername() && checkPassword() && checkPasswordConfirmation() && await checkEmail()) {
        console.log("Form is valid. Submitting...");
        signupForm.submit();
      } else {
        console.log("Form is not valid. not submitting.");
      }
    } catch (error) {
      console.error('Form validation error: ', error);
    }
  });

});
