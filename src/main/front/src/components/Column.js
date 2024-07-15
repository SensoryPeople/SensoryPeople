import React, { useRef } from 'react';
import { useDrag, useDrop } from 'react-dnd';
import Task from './Task';
import { useParams } from 'react-router-dom';
import axios from 'axios';

const Column = ({ column, index, moveColumn, setColumns, columns }) => {
  const ref = useRef(null);
  const { columnId } = useParams();
  const { boardId } = useParams(); // useParams를 사용하여 URL에서 boardId 가져오기

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

  const addTask = async () => {
    const taskText = prompt('새 카드의 내용을 입력하세요:');
    if (taskText) {
      try {
        const token = sessionStorage.getItem('token');
        if (!token) {
          console.error('No token found in sessionStorage');
          return;
        }

        const response = await axios.post(`/boards/${boardId}/columns`, { text: taskText }, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setColumns(columns.map(col =>
            col.id === column.id ? { ...col, tasks: [...col.tasks, response.data] } : col
        ));
      } catch (error) {
        console.error('Error adding task:', error);
      }
    }
  };

  return (
      <div ref={ref} className="column" style={{ opacity: isDragging ? 0.5 : 1 }}>
        <div className="column-header">
          <span className="column-title">{column.title}</span>
          <div className="column-actions">
            <button className="btn" onClick={async () => {
              const newTitle = prompt('컬럼 제목을 수정하세요:', column.title);
              if (newTitle) {
                try {
                  const token = sessionStorage.getItem('token');
                  if (!token) {
                    console.error('No token found in sessionStorage');
                    return;
                  }

                  await axios.patch(`/boards/${boardId}/columns`, { title: newTitle }, {
                    headers: {
                      Authorization: `Bearer ${token}`,
                    },
                  });
                  setColumns(columns.map(col => col.id === column.id ? { ...col, title: newTitle } : col));
                } catch (error) {
                  console.error('Error updating column title:', error);
                }
              }
            }}>수정</button>
            <button className="btn btn-delete" onClick={async () => {
              if (window.confirm('이 컬럼을 삭제하시겠습니까? 모든 카드가 함께 삭제됩니다.')) {
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
                  setColumns(columns.filter(col => col.id !== column.id));
                } catch (error) {
                  console.error('Error deleting column:', error);
                }
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
