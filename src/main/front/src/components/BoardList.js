import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom'; // useNavigate를 import 합니다.
import axios from 'axios';
import './BoardList.css';
const BoardList = () => {
  const [boards, setBoards] = useState([]);
  const [newBoardName, setNewBoardName] = useState('');
  const [message, setMessage] = useState('');
  const navigate = useNavigate(); // useNavigate 훅을 사용하여 navigate 함수를 가져옵니다.
  useEffect(() => {
    fetchBoards();
  }, []);
  const fetchBoards = async () => {
    try {
      const token = sessionStorage.getItem('token');
      if (!token) {
        console.error('No token found in sessionStorage');
        return;
      }
      const response = await axios.get('/boards', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setBoards(response.data);
    } catch (error) {
      console.error('Error fetching board list:', error);
    }
  };
  const addBoard = async () => {
    if (newBoardName.trim()) {
      try {
        const token = sessionStorage.getItem('token');
        if (!token) {
          setMessage('로그인이 필요합니다.');
          return;
        }
        const response = await axios.post('/boards', {
          name: newBoardName.trim(),
          description: '새 보드입니다.', // 예시로 고정된 값을 넣었습니다.
        }, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setBoards([...boards, response.data]);
        setNewBoardName('');
      } catch (error) {
        console.error('Error adding board:', error);
      }
    } else {
      alert('보드 이름을 입력해주세요.');
    }
  };
  const editBoard = async (boardId, index) => {
    const newTitle = window.prompt('보드 이름 수정:', boards[index].name);
    if (newTitle !== null && newTitle.trim() !== '') {
      try {
        const token = sessionStorage.getItem('token');
        if (!token) {
          setMessage('로그인이 필요합니다.');
          return;
        }
        const response = await axios.patch(`/boards/${boardId}`, {
          name: newTitle.trim(),
          description: boards[index].description,
        }, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        const updatedBoard = response.data;
        const updatedBoards = [...boards];
        updatedBoards[index] = updatedBoard;
        setBoards(updatedBoards);
      } catch (error) {
        console.error('Error editing board:', error);
      }
    }
  };
  const deleteBoard = async (boardId, index) => {
    if (window.confirm('이 보드를 삭제하시겠습니까?')) {
      try {
        const token = sessionStorage.getItem('token');
        if (!token) {
          setMessage('로그인이 필요합니다.');
          return;
        }
        await axios.delete(`/boards/${boardId}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setBoards(boards.filter((_, i) => i !== index));
      } catch (error) {
        console.error('Error deleting board:', error);
      }
    }
  };
  const viewBoard = (boardId) => {
    navigate(`/boards/${boardId}/members`); // 동적 변수인 boardId를 포함한 경로로 이동합니다.
  };
  return (
      <div className="container">
        <h1>BoardList</h1>
        <ul className="board-list">
          {boards.map((board, index) => (
              <li key={index} className="board-item">
                <span className="board-title">{board.name}</span>
                <div className="board-actions">
                  <button className="btn btn-edit" onClick={() => editBoard(board.id, index)}>수정</button>
                  <button className="btn btn-delete" onClick={() => deleteBoard(board.id, index)}>삭제</button>
                  <button className="btn btn-view" onClick={() => viewBoard(board.id)}>조회</button>
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
        {message && <p>{message}</p>}
      </div>
  );
};
export default BoardList;