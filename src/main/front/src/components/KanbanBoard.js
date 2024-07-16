import React, { useState, useEffect } from 'react';
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import './KanbanBoard.css';
import AddCardForm from './AddCardForm'; // AddCardForm 컴포넌트 import

const KanbanBoard = () => {
  const { boardId } = useParams();
  const [columns, setColumns] = useState([]);
  const [showAddCardFormForColumn, setShowAddCardFormForColumn] = useState({}); // 각 컬럼의 폼 표시 상태를 저장하는 객체

  useEffect(() => {
    const fetchColumns = async () => {
      try {
        const token = sessionStorage.getItem('token');
        if (!token) {
          console.error('No token found in sessionStorage');
          return;
        }

        const response = await axios.get(`/boards/${boardId}/columns`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        const fetchedColumns = response.data.data || [];

        const columnRequests = fetchedColumns.map(column =>
            axios.get(`/boards/${boardId}/cards/status`, {
              params: {
                columnId: column.id,
              },
              headers: {
                Authorization: `Bearer ${token}`,
              },
            })
        );

        // 각 컬럼에 대한 카드 데이터를 한꺼번에 가져옵니다.
        const cardResponses = await Promise.all(columnRequests);

        // 가져온 카드 데이터를 각 컬럼에 추가합니다.
        const updatedColumns = fetchedColumns.map((column, index) => ({
          ...column,
          cards: cardResponses[index].data.data,
        }));

        setColumns(updatedColumns);
      } catch (error) {
        console.error('Error fetching columns:', error);
      }
    };

    fetchColumns();
  }, [boardId]);

  const addColumn = async (columnId, newCardData) => {
    const updatedColumns = columns.map(column =>
        column.id === columnId ? {
          ...column,
          cards: [...column.cards, newCardData],
        } : column
    );
    setColumns(updatedColumns);
  };

  const deleteColumn = async (columnId, columnName) => {
    try {
      const token = sessionStorage.getItem('token');
      if (!token) {
        console.error('No token found in sessionStorage');
        return;
      }

      await axios.delete(`/boards/${boardId}/columns/${columnId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      setColumns(columns.filter(column => column.id !== columnId));
      alert(`${columnName} 컬럼이 삭제되었습니다.`);
    } catch (error) {
      console.error('Error deleting column:', error);
    }
  };

  const confirmDelete = (columnId, columnName) => {
    if (window.confirm(`${columnName}을 삭제하시겠습니까?`)) {
      deleteColumn(columnId, columnName);
    }
  };

  const toggleAddCardForm = (columnId) => {
    setShowAddCardFormForColumn(prevState => ({
      ...prevState,
      [columnId]: !prevState[columnId],
    }));
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
                      <button className="btn btn-delete" onClick={() => confirmDelete(column.id, column.columnName)}>
                        삭제
                      </button>
                    </div>
                  </div>
                  <div className="add-task" onClick={() => toggleAddCardForm(column.id)}>
                    + 새 카드 추가
                  </div>
                  {column.cards && (
                      <div className="card-list">
                        {column.cards.map((card, cardIndex) => (
                            <div key={cardIndex} className="card">
                              <h3>{card.name}</h3>
                              <p>{card.contents}</p>
                              {/* 추가적인 카드 정보 (예: 마감일, 담당자 등) 표시 */}
                            </div>
                        ))}
                      </div>
                  )}
                  {showAddCardFormForColumn[column.id] && (
                      <AddCardForm
                          boardId={boardId}
                          columnId={column.id}
                          token={sessionStorage.getItem('token')}
                          onClose={() => toggleAddCardForm(column.id)}
                          addColumn={addColumn} // addColumn 함수를 AddCardForm에 전달
                      />
                  )}
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