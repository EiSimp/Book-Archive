document.addEventListener("DOMContentLoaded", () => {
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

  window.addEventListener("load", async () => {
    if (startingUsername.length != 0) {
      const usernameTaken = await checkUsernameUniqueness(startingUsername);
      if (usernameTaken) {
        usernameInput.classList.add("is-invalid");
        usernameInvalid.textContent = "Username is taken";
      }
    }
  });

  usernameInput.addEventListener("input", (event) => {
    checkUsernameWithDebounce(event.target.value);
  });

  passwordInput.addEventListener("input", () => {
    if (!passwordInput.validity.valid) {
      showPasswordError();
    } else {
      passwordInput.classList.remove("is-invalid");
      passwordInput.classList.add("is-valid");
    }
    validatePassword2();
  });

  password2Input.addEventListener("input", () => {
    validatePassword2();
  });

  emailInput.addEventListener("input", (event) => {
    checkEmailWithDebounce(event.target.value);
  })



  async function checkUsernameUniqueness(username) {
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

  async function checkEmailUniqueness(email) {
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

  function showUsernameError() {
    if (usernameInput.validity.patternMismatch) {
      usernameInvalid.textContent =
        "Must contain only letters, numbers, and underscores";
    } else if (usernameInput.validity.valueMissing) {
      usernameInvalid.textContent = "Please enter a username";
    }
    usernameInput.classList.add("is-invalid");
  }

  function showPasswordError() {
    if (passwordInput.validity.patternMismatch) {
      passwordInvalid.textContent =
        "Must contain an uppercase letter, a number, and be at least 8 characters long";
    } else if (passwordInput.validity.valueMissing) {
      passwordInvalid.textContent = "Please enter a password";
    }
    passwordInput.classList.add("is-invalid");
  }


  function validatePassword2() {
    if (password2Input.value !== passwordInput.value) {
      password2Invalid.textContent = "The password confirmation does not match";
      password2Input.classList.add("is-invalid");
      password2Input.classList.remove("is-valid");
    } else if (password2Input.validity.valueMissing) {
      password2Invalid.textContent = "Please confirm your password";
      password2Input.classList.add("is-invalid");
      password2Input.classList.remove("is-valid");
    } else {
      password2Input.classList.remove("is-invalid");
      password2Input.classList.add("is-valid");
      password2Invalid.textContent = "";
    }
  }


  function showEmailError() {
    if (emailInput.validity.patternMismatch) {
      emailInvalid.textContent = "Invalid email format";
    } else if (emailInput.validity.valueMissing) {
      emailInvalid.textContent = "Please enter an email";
    }
    emailInput.classList.add("is-invalid");
  }

  let debounceTimer;
  function debounce(func, delay) {
    return function (...args) {
      clearTimeout(debounceTimer);
      debounceTimer = setTimeout(() => func.apply(this, args), delay);
    };
  }

  const checkUsernameWithDebounce = debounce(async (username) => {
    if (!usernameInput.validity.valid) {
      showUsernameError();
      return;
    }
    const usernameTaken = await checkUsernameUniqueness(username);
    if (usernameTaken) {
      usernameInput.classList.add("is-invalid");
      usernameInvalid.textContent = "Username is taken";
    } else {
      usernameInput.classList.remove("is-invalid");
      usernameInput.classList.add("is-valid");
    }
  }, 300);

  const checkEmailWithDebounce = debounce(async (email) => {
    if (!emailInput.validity.valid) {
      showEmailError();
      return;
    }
    const emailTaken = await checkEmailUniqueness(email);
    if (emailTaken) {
      emailInput.classList.add("is-invalid");
      emailInvalid.textContent = "Email is already taken";
    } else {
      emailInput.classList.remove("is-invalid");
      emailInput.classList.add("is-valid");
    }
  }, 300);


  signupForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    let valid = true;

    console.log("Form submission started");
    try {
      // Username validation
      if (!usernameInput.validity.valid) {
        showUsernameError();
        //console.log("Username input is not valid");
        valid = false;
      } else if (await checkUsernameUniqueness(usernameInput.value)) {
        usernameInput.classList.add("is-invalid");
        usernameInvalid.textContent = "Username is taken";
        //console.log("Username is taken");
        valid = false;
      } else {
        usernameInput.classList.remove("is-invalid");
        usernameInput.classList.add("is-valid");
      }

      // Password Validation
      if (!passwordInput.validity.valid) {
        showPasswordError();
        //console.log("Password input is not valid");
        valid = false;
      } else {
        passwordInput.classList.remove("is-invalid");
        passwordInput.classList.add("is-valid");
      }

      // Confirm Password Validation
      if (password2Input.value !== passwordInput.value) {
        validatePassword2();
        valid = false;
      } else {
        password2Input.classList.remove("is-invalid");
        password2Input.classList.add("is-valid");
      }

      // Email Validation
      if (!emailInput.validity.valid) {
        showEmailError();
        //console.log("Email input is not valid");
        valid = false;
      } else if (await checkEmailUniqueness(emailInput.value)) {
        emailInput.classList.add("is-invalid");
        emailInvalid.textContent = "Email is already taken";
        //console.log("Email is taken");
        valid = false;
      } else {
        emailInput.classList.remove("is-invalid");
        emailInput.classList.add("is-valid");
      }

      if (valid) {
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
