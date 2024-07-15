import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import './BoardMember.css'; // CSS 파일 import

const BoardMember = () => {
  const [members, setMembers] = useState([]);

  const addMember = (memberId) => {
    const initials = memberId.split('_').map(word => word[0].toUpperCase()).join('');
    const newMember = {
      id: memberId,
      initials: initials
    };
    setMembers([...members, newMember]);
    alert(`${memberId}님이 팀에 추가되었습니다.`);
  };

  const excludeMember = (memberId) => {
    if (window.confirm(`정말로 ${memberId}님을 팀에서 제외하시겠습니까?`)) {
      const updatedMembers = members.filter(member => member.id !== memberId);
      setMembers(updatedMembers);
      alert(`${memberId}님이 팀에서 제외되었습니다.`);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const memberId = e.target.memberId.value;
    addMember(memberId);
    e.target.reset();
  };

  return (
      <div>
        <header>
          <h1>Board Member</h1>
        </header>
        <div className="container">
          <div className="member-list">
            {members.map(member => (
                <div key={member.id} className="member-item">
                  <div className="member-avatar">{member.initials}</div>
                  <div className="member-info">
                    <div className="member-id">{member.id}</div>
                  </div>
                  <div className="member-actions">
                    <button className="exclude-btn" onClick={() => excludeMember(member.id)}>제외</button>
                  </div>
                </div>
            ))}
          </div>
          <div className="add-member-section">
            <h2>새 멤버 추가</h2>
            <form onSubmit={handleSubmit} className="add-member-form" id="addMemberForm">
              <input type="text" id="memberId" placeholder="멤버 ID" required />
              <button type="submit">추가하기</button>
            </form>
          </div>
          {/* 칸반 보드로 이동하는 버튼 */}
          <div style={{ marginTop: '20px' }}>
            <Link to="/kanban">
              <button className="btn-kanban">칸반 보드로 이동</button>
            </Link>
          </div>
        </div>
      </div>
  );
};

export default BoardMember;
