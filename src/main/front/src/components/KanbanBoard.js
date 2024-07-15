import React, { useState, useEffect } from 'react';
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import './KanbanBoard.css';

const KanbanBoard = () => {
  const { boardId } = useParams(); // useParams를 사용하여 URL에서 boardId 가져오기
  const [columns, setColumns] = useState([]); // 초기 상태를 빈 배열로 설정
  const [confirmDeleteColumn, setConfirmDeleteColumn] = useState(null); // 삭제할 컬럼 정보를 저장하는 상태

  // 초기 데이터를 가져오는 함수
  useEffect(() => {
    const fetchColumns = async () => {
      try {
        const token = sessionStorage.getItem('token'); // 세션 스토리지에서 토큰 가져오기
        if (!token) {
          console.error('No token found in sessionStorage');
          return;
        }

        const response = await axios.get(`/boards/${boardId}/columns`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setColumns(response.data.data || []); // 데이터가 없을 경우 빈 배열로 설정
      } catch (error) {
        console.error('Error fetching columns:', error);
      }
    };

    fetchColumns();
  }, [boardId]);

  const addColumn = async () => {
    const columnName = prompt('새 컬럼의 제목을 입력하세요:');
    if (columnName) {
      try {
        const token = sessionStorage.getItem('token'); // 세션 스토리지에서 토큰 가져오기
        if (!token) {
          console.error('No token found in sessionStorage');
          return;
        }

        const response = await axios.post(
            `/boards/${boardId}/columns`,
            { columnName },
            {
              headers: {
                Authorization: `Bearer ${token}`,
                'Content-Type': 'application/json',
              },
            }
        );
        const newColumn = response.data.data; // 새로운 컬럼 데이터 가져오기
        setColumns([...columns, newColumn]); // 새 컬럼 추가 후 상태 업데이트
      } catch (error) {
        console.error('Error adding column:', error);
      }
    }
  };

  const deleteColumn = async (columnId, columnName) => {
    try {
      const token = sessionStorage.getItem('token'); // 세션 스토리지에서 토큰 가져오기
      if (!token) {
        console.error('No token found in sessionStorage');
        return;
      }

      await axios.delete(`/boards/${boardId}/columns/${columnId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      // 컬럼 삭제 후 상태 업데이트
      setColumns(columns.filter(column => column.id !== columnId));
      alert(`${columnName} 컬럼이 삭제되었습니다.`); // 삭제 완료 알림
    } catch (error) {
      console.error('Error deleting column:', error);
    }
  };

  const confirmDelete = (columnId, columnName) => {
    if (window.confirm(`${columnName}을 삭제하시겠습니까?`)) {
      deleteColumn(columnId, columnName); // 확인을 누르면 삭제 함수 호출
    }
  };

  return (
      <DndProvider backend={HTML5Backend}>
        <div className="kanban-board">
          <header className="header">
            <div className="logo">Trello</div>
          </header>
          <div className="board">
            {columns.map((column, index) => (
                <div key={column.id} className="column" draggable="true" style={{ opacity: 1 }}>
                  <div className="column-header">
                    <span className="column-title">{column.columnName}</span>
                    <div className="column-actions">
                      <button className="btn">수정</button>
                      <button className="btn btn-delete" onClick={() => confirmDelete(column.id, column.columnName)}>삭제</button>
                    </div>
                  </div>
                  <div className="add-task">+ 새 카드 추가</div>
                </div>
            ))}
            <div className="add-column">
              <button onClick={addColumn}>+ 새 컬럼 추가</button>
            </div>
          </div>
        </div>
      </DndProvider>
  );
};

export default KanbanBoard;