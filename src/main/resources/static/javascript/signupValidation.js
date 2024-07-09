const signupForm = document.getElementById("signupform");
const usernameInput = document.getElementById("username");
const usernameInvalid = document.querySelector("#username + .invalid-feedback");
const passwordInput = document.getElementById("password");
const passwordInvalid = document.querySelector("#password + .invalid-feedback");
const emailInput = document.getElementById("email");
const emailInvalid = document.querySelector("#email + .invalid-feedback");
const password2Input = document.getElementById("password2");
const password2Invalid = document.querySelector("#password2 + .invalid-feedback");

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

password2Input.addEventListener("input", () => {
  if (!password2Input.validity.valid) {
    showPassword2Error();
  } else {
    password2Input.classList.remove("is-invalid");
    password2Input.classList.add("is-valid");
  }
});

function showPassword2Error() {
   if (password2Input.value != passwordInput.value) {
    password2Invalid.textContent =
      "The password confirmation does not match";
  } else if (password2Input.validity.valueMissing) {
    password2Invalid.textContent = "Please confirm your password";
  }
  password2Input.classList.add("is-invalid");
}

emailInput.addEventListener("input", async () => {
  if (!emailInput.validity.valid) {
    showEmailError();
  } else {
    const emailTaken = await checkEmailUniqueness(emailInput.value);
    if (emailTaken) {
      emailInput.classList.add("is-invalid");
      emailInvalid.textContent = "Email is already taken";
    } else {
      emailInput.classList.remove("is-invalid");
      emailInput.classList.add("is-valid");
    }
  }
});

async function checkEmailUniqueness(email) {
  const response = await fetch('/check-email?email=${encodeURIComponent(email)}');
  const data = await response.json();
  return data.exists;
}

function showEmailError() {
  if (emailInput.validity.patternMismatch) {
    emailInvalid.textContent =
      "Invalid email format";
  } else if (emailInput.validity.valueMissing) {
    emailInvalid.textContent = 
      "Please enter an email";
  } 
  emailInput.classList.add("is-invalid");
}


signupForm.addEventListener("submit", (event) => {
  let valid = true;

  if (!usernameInput.validity.valid) {
    showUsernameError();
    valid = false;
  }

  if (!passwordInput.validity.valid) {
    showPasswordError();
    valid = false;
  }

  if (!password2Input.validity.valid) {
    showPassword2Error();
    valid = false;
  }

  if (!emailInput.validity.valid) {
    showEmailError();
    valid = false;
  }
  
  if (!valid) {
    event.preventDefault();
  }
});
