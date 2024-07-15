import React, { useState } from 'react';
import './CardComments.css';

const CardComments = () => {
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');

  const handleAddComment = (e) => {
    e.preventDefault();
    if (newComment.trim() === '') return;
    setComments([...comments, newComment]);
    setNewComment('');
  };

  const handleRemoveComment = (index) => {
    const updatedComments = comments.filter((_, idx) => idx !== index);
    setComments(updatedComments);
  };

  return (
      <div className="card-comments">
        <h3>카드 댓글</h3>
        <ul className="comment-list">
          {comments.map((comment, index) => (
              <li key={index} className="comment-item">
                {comment}
                <button className="delete-btn" onClick={() => handleRemoveComment(index)}>
                  삭제
                </button>
              </li>
          ))}
        </ul>
        <form className="add-comment-form" onSubmit={handleAddComment}>
          <input
              type="text"
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
              placeholder="댓글을 입력하세요"
              required
          />
          <button type="submit">추가</button>
        </form>
      </div>
  );
};

export default CardComments;
