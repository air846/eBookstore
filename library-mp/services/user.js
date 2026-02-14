const { request } = require("../utils/request");

function login(data) {
  return request({
    url: "/user/login",
    method: "POST",
    data
  });
}

function miniLogin(data) {
  return request({
    url: "/user/mp/login",
    method: "POST",
    data
  });
}

function logout() {
  return request({
    url: "/user/logout",
    method: "POST"
  });
}

function getUserInfo() {
  return request({
    url: "/user/info"
  });
}

module.exports = {
  login,
  miniLogin,
  logout,
  getUserInfo
};
