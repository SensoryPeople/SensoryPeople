import React, { useRef } from 'react';
import { useDrag, useDrop } from 'react-dnd';
import Task from './Task';

const Column = ({ column, index, moveColumn, setColumns, columns }) => {
  const ref = useRef(null);

  const [{ handlerId }, drop] = useDrop({
    accept: 'column',
    collect: monitor => ({
      handlerId: monitor.getHandlerId(),
    }),
    hover(item, monitor) {
      if (!ref.current) {
        return;
      }
      const dragIndex = item.index;
      const hoverIndex = index;

      if (dragIndex === hoverIndex) {
        return;
      }

      const hoverBoundingRect = ref.current?.getBoundingClientRect();
      const hoverMiddleX = (hoverBoundingRect.right - hoverBoundingRect.left) / 2;
      const clientOffset = monitor.getClientOffset();
      const hoverClientX = clientOffset.x - hoverBoundingRect.left;

      if (dragIndex < hoverIndex && hoverClientX < hoverMiddleX) {
        return;
      }

      if (dragIndex > hoverIndex && hoverClientX > hoverMiddleX) {
        return;
      }

      moveColumn(dragIndex, hoverIndex);
      item.index = hoverIndex;
    },
  });

  const [{ isDragging }, drag] = useDrag({
    type: 'column',
    item: { index },
    collect: monitor => ({
      isDragging: monitor.isDragging(),
    }),
  });

  drag(drop(ref));

  const addTask = () => {
    const taskText = prompt('새 카드의 내용을 입력하세요:');
    if (taskText) {
      setColumns(
          columns.map(col =>
              col.id === column.id
                  ? { ...col, tasks: [...col.tasks, { id: Date.now(), text: taskText }] }
                  : col
          )
      );
    }
  };

  return (
      <div ref={ref} className="column" style={{ opacity: isDragging ? 0.5 : 1 }}>
        <div className="column-header">
          <span className="column-title">{column.title}</span>
          <div className="column-actions">
            <button className="btn" onClick={() => {
              const newTitle = prompt('컬럼 제목을 수정하세요:', column.title);
              if (newTitle) {
                setColumns(columns.map(col => col.id === column.id ? { ...col, title: newTitle } : col));
              }
            }}>수정</button>
            <button className="btn btn-delete" onClick={() => {
              if (window.confirm('이 컬럼을 삭제하시겠습니까? 모든 카드가 함께 삭제됩니다.')) {
                setColumns(columns.filter(col => col.id !== column.id));
              }
            }}>삭제</button>
          </div>
        </div>
        {column.tasks.map(task => (
            <Task key={task.id} task={task} columnId={column.id} columns={columns} setColumns={setColumns} />
        ))}
        <div className="add-task" onClick={addTask}>+ 새 카드 추가</div>
      </div>
  );
};

export default Column;
