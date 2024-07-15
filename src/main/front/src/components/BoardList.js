import React, { useState } from 'react';
import './BoardList.css';

const BoardList = () => {
  const [boards, setBoards] = useState([]);
  const [newBoardName, setNewBoardName] = useState('');

  const addBoard = () => {
    if (newBoardName.trim()) {
      setBoards([...boards, newBoardName.trim()]);
      setNewBoardName('');
    }
  };

  const editBoard = (index) => {
    const newTitle = window.prompt('보드 이름 수정:', boards[index]);
    if (newTitle !== null && newTitle.trim() !== '') {
      const updatedBoards = [...boards];
      updatedBoards[index] = newTitle.trim();
      setBoards(updatedBoards);
    }
  };

  const deleteBoard = (index) => {
    if (window.confirm('이 보드를 삭제하시겠습니까?')) {
      setBoards(boards.filter((_, i) => i !== index));
    }
  };

  const viewBoard = (boardName) => {
    alert(`${boardName} 보드를 조회합니다.`);
    // 이 부분에서 보드를 조회하는 로직을 추가할 수 있습니다.
    // 예를 들어, 다른 페이지로 이동하거나 해당 보드의 상세 정보를 표시할 수 있습니다.
  };

  return (
      <div className="container">
        <h1>BoardList</h1>
        <ul className="board-list">
          {boards.map((board, index) => (
              <li key={index} className="board-item">
                <span className="board-title">{board}</span>
                <div className="board-actions">
                  <button className="btn btn-edit" onClick={() => editBoard(index)}>수정</button>
                  <button className="btn btn-delete" onClick={() => deleteBoard(index)}>삭제</button>
                  <button className="btn btn-view" onClick={() => viewBoard(board)}>조회</button>
                </div>
              </li>
          ))}
        </ul>
        <div className="add-board">
          <input
              type="text"
              value={newBoardName}
              onChange={(e) => setNewBoardName(e.target.value)}
              placeholder="새 보드 이름 입력"
          />
          <button className="btn btn-add" onClick={addBoard}>추가</button>
        </div>
      </div>
  );
};

export default BoardList;
