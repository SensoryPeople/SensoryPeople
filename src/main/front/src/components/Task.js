import React, { useRef } from 'react';
import { useDrag, useDrop } from 'react-dnd';
import { useParams } from 'react-router-dom';
import axios from 'axios';

const Task = ({ task, columnId, columns, setColumns }) => {
  const ref = useRef(null);
  const { boardId } = useParams(); // useParams를 사용하여 URL에서 boardId 가져오기
  const { cardId } = useParams();

  // Drop 기능 설정
  const [{ handlerId }, drop] = useDrop({
    accept: 'task', // 태스크 타입을 설정
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

  // Drag 기능 설정
  const [{ isDragging }, drag] = useDrag({
    type: 'task', // 태스크 타입을 설정
    item: { index: columns.find(col => col.id === columnId).tasks.indexOf(task) },
    collect: monitor => ({
      isDragging: monitor.isDragging(),
    }),
  });

  drag(drop(ref));

  // 태스크 내용 수정 함수
  const handleEditTask = async () => {
    const newText = prompt('카드 내용을 수정하세요:', task.text);
    if (newText) {
      try {
        const token = sessionStorage.getItem('token');
        if (!token) {
          console.error('No token found in sessionStorage');
          return;
        }

        const response = await axios.patch(`/boards/${boardId}/cards/${cardId}`, { text: newText }, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        // 업데이트된 태스크 정보를 상태에 반영
        setColumns(columns.map(col =>
            col.id === columnId ? { ...col, tasks: col.tasks.map(t => t.id === task.id ? { ...t, text: newText } : t) } : col
        ));
      } catch (error) {
        console.error('Error editing task:', error);
      }
    }
  };

  // 태스크 삭제 함수
  const handleDeleteTask = async () => {
    if (window.confirm('이 카드를 삭제하시겠습니까?')) {
      try {
        const token = sessionStorage.getItem('token');
        if (!token) {
          console.error('No token found in sessionStorage');
          return;
        }

        await axios.delete(`/boards/${boardId}/cards/${cardId}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        // 삭제된 태스크를 제외하고 상태를 업데이트
        setColumns(columns.map(col =>
            col.id === columnId ? { ...col, tasks: col.tasks.filter(t => t.id !== task.id) } : col
        ));
      } catch (error) {
        console.error('Error deleting task:', error);
      }
    }
  };

  return (
      <div ref={ref} className="task" style={{ opacity: isDragging ? 0.5 : 1 }}>
        <div className="task-content">{task.text}</div>
        <div className="task-actions">
          <button className="btn" onClick={handleEditTask}>수정</button>
          <button className="btn btn-delete" onClick={handleDeleteTask}>삭제</button>
        </div>
      </div>
  );
};

export default Task;
