const signupForm = document.getElementById("signupform");
const usernameInput = document.getElementById("username");
const usernameInvalid = document.querySelector("#username + .invalid-feedback");
const passwordInput = document.getElementById("password");
const passwordInvalid = document.querySelector("#password + .invalid-feedback");

const startingUsername = usernameInput.value;

window.addEventListener("load", () => {
  if (startingUsername.length != 0) {
    usernameInput.classList.add("is-invalid");
    usernameInvalid.textContent = "Username is taken";
  }
});

usernameInput.addEventListener("input", () => {
  if (!usernameInput.validity.valid) {
    showUsernameError();
  } else if (
    usernameInput.value == startingUsername &&
    startingUsername.length != 0
  ) {
    usernameInput.classList.add("is-invalid");
    usernameInvalid.textContent = "Username is taken";
  } else {
    usernameInput.classList.remove("is-invalid");
  }
});

function showUsernameError() {
  if (usernameInput.validity.patternMismatch) {
    usernameInvalid.textContent =
      "Must contain only letters, numbers, and underscores";
  } else if (usernameInput.validity.valueMissing) {
    usernameInvalid.textContent = "Please enter a username";
  }
  usernameInput.classList.add("is-invalid");
}

passwordInput.addEventListener("input", () => {
  if (!passwordInput.validity.valid) {
    showPasswordError();
  } else {
    passwordInput.classList.remove("is-invalid");
    passwordInput.classList.add("is-valid");
  }
});

function showPasswordError() {
  if (passwordInput.validity.patternMismatch) {
    passwordInvalid.textContent =
      "Must contain an uppercase letter, a number, and be at least 8 characters long";
  } else if (passwordInput.validity.valueMissing) {
    passwordInvalid.textContent = "Please enter a password";
  }
  passwordInput.classList.add("is-invalid");
}

signupForm.addEventListener("submit", (event) => {
  if (!usernameInput.validity.valid) {
    showUsernameError();
    event.preventDefault();
  }

  if (!passwordInput.validity.valid) {
    showPasswordError();
    event.preventDefault();
  }
});
