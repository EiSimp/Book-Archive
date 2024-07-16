document.addEventListener('DOMContentLoaded', () => {
  const loginForm = document.getElementById("login-form");
  const usernameInput = document.getElementById("username");
  const usernameInvalid = document.querySelector("#username + .invalid-feedback");
  const passwordInput = document.getElementById("password");
  const passwordInvalid = document.querySelector("#password + .invalid-feedback");

  function debounce(func, wait) {
    let timeout;
    return function (...args) {
      clearTimeout(timeout);
      timeout = setTimeout(() => func.apply(this, args), wait);
    };
  }

  usernameInput.addEventListener("input", debounce(async () => {
    usernameInput.classList.remove("is-valid", "is-invalid");
    if (!usernameInput.validity.valueMissing) {
      const exists = await usernameExists(usernameInput.value);
      if (exists) {
        usernameInput.classList.add('is-valid');
      } else {
        usernameInput.classList.add('is-invalid');
        usernameInvalid.textContent = "There is no account with that username";
      }
    }
  }, 300));

  passwordInput.addEventListener("input", () => {
    passwordInput.classList.remove('us-invalid', 'is-valid');
    if (!passwordInput.validity.valueMissing) {
      passwordInput.classList.add('is-valid');
    }
  });

  let params = new URLSearchParams(window.location.search);
  if (params.has("error")) {
    passwordInput.className = "is-invalid";
    passwordInput.value = "";
    passwordInvalid.textContent = "Incorrect Password";
  }

  async function usernameExists(username) {
    try {
      const response = await fetch(
        `/check-username?username=${encodeURIComponent(username)}`
      );
      if (!response.ok) {
        throw new Error(`Server error: ${response.status}`);
      }
      const data = await response.json();
      return data.exists;
    } catch (error) {
      console.error("error checking username existence: ", error);
      return false;
    }
  }

  async function checkUsername() {
    let isValid = false;

    if (usernameInput.validity.valueMissing) {
      usernameInvalid.textContent = "Please enter your username";
      usernameInput.classList.add('is-invalid');
    } else if (!(await usernameExists(usernameInput.value))) {
      usernameInvalid.textContent = "There is no account with that username";
      usernameInput.classList.add('is-invalid');
      passwordInput.classList.add('is-valid');
      usernameInput.value = "";
    } else {
      isValid = true;
    }
    return isValid;
  }

  function checkPassword() {
    let isValid = false;

    if (passwordInput.validity.valueMissing) {
      passwordInvalid.textContent = "Please enter your password";
      passwordInput.classList.add('is-invalid');
    } else {
      isValid = true;
    }
    return isValid;
  }

  loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    console.log("Form submission started");
    try {
      if ((await checkUsername()) && checkPassword()) {
        console.log("Form is valid. Submitting...");
        loginForm.submit();
      } else {
        console.log("Form is not valid. not submitting.");
      }
    } catch (error) {
      console.error("Form validation error: ", error);
    }
  });
});
