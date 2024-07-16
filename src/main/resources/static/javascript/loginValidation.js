const loginForm = document.getElementById("login-form");

const usernameInput = document.getElementById("username");
const usernameInvalid = document.querySelector("#username + .invalid-feedback");
const passwordInput = document.getElementById("password");
const passwordInvalid = document.querySelector("#password + .invalid-feedback");

usernameInput.addEventListener("input", () => {
  usernameInput.className = "is-valid";
});

passwordInput.addEventListener("input", () => {
  passwordInput.className = "is-valid";
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
    usernameInput.className = "is-invalid";
  } else if (!(await usernameExists(usernameInput.value))) {
    usernameInvalid.textContent = "There is no account with that username";
    usernameInput.className = "is-invalid";
    passwordInput.className = "is-valid";
    usernameInput.value = "";
  } else {
    isValid = true;
  }
  return isValid;
}

function checkPassword() {
  passwordInput.className = "is-invalid";
  let isValid = false;

  if (
    !usernameInput.validity.valueMissing &&
    passwordInput.validity.valueMissing
  ) {
    passwordInvalid.textContent = "Please enter your password";
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
      passwordInput.value = "";
      console.log("Form is valid. Submitting...");
      loginForm.submit();
    } else {
      console.log("Form is not valid. not submitting.");
    }
  } catch (error) {
    console.error("Form validation error: ", error);
  }
});
