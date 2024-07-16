import React, { useState } from 'react';
import axios from 'axios';

const AddCardForm = ({ boardId, columnId, token, onClose, addCard }) => {
    const [newCardName, setNewCardName] = useState('');
    const [newCardContent, setNewCardContent] = useState('');
    const [formError, setFormError] = useState('');

    const handleAddCard = async () => {
        try {
            if (!newCardName || !newCardContent) {
                setFormError('카드명과 카드 내용을 모두 입력하세요.');
                return;
            }

            const response = await axios.post(
                `/boards/${boardId}/cards/status/${columnId}`,
                {
                    name: newCardName,
                    contents: newCardContent,
                },
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        'Content-Type': 'application/json',
                    },
                }
            );

            // 새 카드 데이터를 부모 컴포넌트로 전달하여 추가
            const newCardData = response.data.data;
            addCard(columnId, newCardData);

            // 폼 닫기
            onClose();
        } catch (error) {
            console.error('Error adding card:', error);
        }
    };

    return (
        <div className="add-card-form">
            <input
                type="text"
                value={newCardName}
                onChange={(e) => setNewCardName(e.target.value)}
                placeholder="카드 제목"
            />
            <textarea
                value={newCardContent}
                onChange={(e) => setNewCardContent(e.target.value)}
                placeholder="카드 내용"
            />
            <button onClick={handleAddCard}>카드 추가</button>
            {formError && <p className="error">{formError}</p>}
        </div>
    );
};

export default AddCardForm;
