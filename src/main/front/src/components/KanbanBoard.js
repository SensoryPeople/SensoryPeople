import React, { useState } from 'react';
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import Column from './Column';
import './KanbanBoard.css';

const KanbanBoard = () => {
  const [columns, setColumns] = useState([]);

  const addColumn = () => {
    const title = prompt('새 컬럼의 제목을 입력하세요:');
    if (title) {
      setColumns([...columns, { id: Date.now(), title, tasks: [] }]);
    }
  };

  const moveColumn = (dragIndex, hoverIndex) => {
    const newColumns = [...columns];
    const [draggedColumn] = newColumns.splice(dragIndex, 1);
    newColumns.splice(hoverIndex, 0, draggedColumn);
    setColumns(newColumns);
  };

  return (
      <DndProvider backend={HTML5Backend}>
        <div className="kanban-board">
          <header className="header">
            <div className="logo">Trello</div>
          </header>
          <div className="board">
            {columns.map((column, index) => (
                <Column
                    key={column.id}
                    index={index}
                    column={column}
                    moveColumn={moveColumn}
                    setColumns={setColumns}
                    columns={columns}
                />
            ))}
            <div className="add-column" onClick={addColumn}>+ 새 컬럼 추가</div>
          </div>
        </div>
      </DndProvider>
  );
};

export default KanbanBoard;
