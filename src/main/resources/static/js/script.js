const toggleThemeButton = document.querySelector("#theme-toggle-button");

// Toggle between light and dark themes
toggleThemeButton.addEventListener("click", () => {
  const isDarkMode = document.body.classList.toggle("dark_mode");
  localStorage.setItem("themeColor", isDarkMode ? "dark_mode" : "light_mode");
  toggleThemeButton.innerText = isDarkMode ? "light_mode" : "dark_mode";
});