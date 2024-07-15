import React, { useRef } from 'react';
import { useDrag, useDrop } from 'react-dnd';

const Task = ({ task, columnId, columns, setColumns }) => {
  const ref = useRef(null);

  const [{ handlerId }, drop] = useDrop({
    accept: 'task',
    collect: monitor => ({
      handlerId: monitor.getHandlerId(),
    }),
    hover(item, monitor) {
      if (!ref.current) {
        return;
      }
      const dragIndex = item.index;
      const hoverIndex = columns.find(col => col.id === columnId).tasks.indexOf(task);

      if (dragIndex === hoverIndex) {
        return;
      }

      const hoverBoundingRect = ref.current?.getBoundingClientRect();
      const hoverMiddleY = (hoverBoundingRect.bottom - hoverBoundingRect.top) / 2;
      const clientOffset = monitor.getClientOffset();
      const hoverClientY = clientOffset.y - hoverBoundingRect.top;

      if (dragIndex < hoverIndex && hoverClientY < hoverMiddleY) {
        return;
      }

      if (dragIndex > hoverIndex && hoverClientY > hoverMiddleY) {
        return;
      }

      const column = columns.find(col => col.id === columnId);
      const tasks = column.tasks.slice();
      const [draggedTask] = tasks.splice(dragIndex, 1);
      tasks.splice(hoverIndex, 0, draggedTask);

      setColumns(columns.map(col => col.id === columnId ? { ...col, tasks } : col));

      item.index = hoverIndex;
    },
  });

  const [{ isDragging }, drag] = useDrag({
    type: 'task',
    item: { index: columns.find(col => col.id === columnId).tasks.indexOf(task) },
    collect: monitor => ({
      isDragging: monitor.isDragging(),
    }),
  });

  drag(drop(ref));

  return (
      <div ref={ref} className="task" style={{ opacity: isDragging ? 0.5 : 1 }}>
        <div className="task-content">{task.text}</div>
        <div className="task-actions">
          <button className="btn" onClick={() => {
            const newText = prompt('카드 내용을 수정하세요:', task.text);
            if (newText) {
              setColumns(columns.map(col =>
                  col.id === columnId
                      ? {
                        ...col,
                        tasks: col.tasks.map(t => t.id === task.id ? { ...t, text: newText } : t)
                      }
                      : col
              ));
            }
          }}>수정</button>
          <button className="btn btn-delete" onClick={() => {
            if (window.confirm('이 카드를 삭제하시겠습니까?')) {
              setColumns(columns.map(col =>
                  col.id === columnId
                      ? { ...col, tasks: col.tasks.filter(t => t.id !== task.id) }
                      : col
              ));
            }
          }}>삭제</button>
        </div>
      </div>
  );
};

export default Task;
