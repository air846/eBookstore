const { request } = require("../utils/request");

function getBookList(data) {
  return request({
    url: "/book/list",
    data
  });
}

function getBookDetail(id) {
  return request({
    url: `/book/detail/${id}`
  });
}

function getChapters(bookId) {
  return request({
    url: `/book/${bookId}/chapters`
  });
}

function getReadText(bookId) {
  return request({
    url: `/book/read-text/${bookId}`
  });
}

function favorite(bookId) {
  return request({
    url: `/book/favorite/${bookId}`,
    method: "POST"
  });
}

function unfavorite(bookId) {
  return request({
    url: `/book/favorite/${bookId}`,
    method: "DELETE"
  });
}

function getFavoriteList() {
  return request({
    url: "/book/favorite/list"
  });
}

function saveHistory(data) {
  return request({
    url: "/book/history",
    method: "POST",
    data
  });
}

function getHistoryList() {
  return request({
    url: "/book/history/list"
  });
}

function getCommentCounts(bookId, chapterId, virtualChapterIndex) {
  return request({
    url: `/book/${bookId}/chapter/${chapterId}/comment-counts`,
    data: {
      virtualChapterIndex
    }
  });
}

function getComments(bookId, chapterId, paragraphIndex, sortBy = "time", virtualChapterIndex) {
  return request({
    url: `/book/${bookId}/chapter/${chapterId}/comments`,
    data: {
      paragraphIndex,
      sortBy,
      virtualChapterIndex
    }
  });
}

function createComment(bookId, chapterId, data) {
  return request({
    url: `/book/${bookId}/chapter/${chapterId}/comments`,
    method: "POST",
    data
  });
}

function reactComment(commentId, value) {
  return request({
    url: `/book/comments/${commentId}/reaction`,
    method: "POST",
    data: { value }
  });
}

function hideComment(commentId) {
  return request({
    url: `/book/comments/${commentId}/hide`,
    method: "POST"
  });
}

function unhideComment(commentId) {
  return request({
    url: `/book/comments/${commentId}/hide`,
    method: "DELETE"
  });
}

function urgeBook(bookId) {
  return request({
    url: `/book/${bookId}/urge`,
    method: "POST"
  });
}

module.exports = {
  getBookList,
  getBookDetail,
  getChapters,
  getReadText,
  favorite,
  unfavorite,
  getFavoriteList,
  saveHistory,
  getHistoryList,
  getCommentCounts,
  getComments,
  createComment,
  reactComment,
  hideComment,
  unhideComment,
  urgeBook
};
