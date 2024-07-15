import React, { useState, useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import axios from 'axios';
import './BoardMember.css'; // CSS 파일 import

const BoardMember = () => {
  const { boardId } = useParams(); // useParams를 사용하여 URL에서 boardId 가져오기
  const [members, setMembers] = useState([]);
  const [newMemberId, setNewMemberId] = useState('');
  const [message, setMessage] = useState('');

  useEffect(() => {
    fetchMembers(); // 컴포넌트가 마운트될 때 멤버 리스트를 가져오는 함수 호출
  }, []);

  const fetchMembers = async () => {
    try {
      const token = sessionStorage.getItem('token'); // 세션 스토리지에서 토큰 가져오기
      if (!token) {
        console.error('No token found in sessionStorage');
        return;
      }

      const response = await axios.get(`/boards/${boardId}/members`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setMembers(response.data.data); // 멤버 데이터를 상태에 설정
    } catch (error) {
      console.error('Error fetching board members:', error);
    }
  };


  const addMember = async (memberId) => {
    try {
      const token = sessionStorage.getItem('token'); // 세션 스토리지에서 토큰 가져오기
      if (!token) {
        setMessage('로그인이 필요합니다.');
        return;
      }

      const response = await axios.post(`/boards/${boardId}/invite`, {
        userName: memberId,
        userRole: 'USER', // 추가 멤버의 기본 역할 설정 (매니저로 변경 가능)
      }, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      const newMember = response.data;
      setMembers([...members, newMember]);
      setNewMemberId('');
      alert(`${memberId}님이 팀에 추가되었습니다.`);
    } catch (error) {
      console.error('Error adding member:', error);
    }
  };

  // const excludeMember = async (memberId) => {
  //   if (window.confirm(`정말로 ${memberId}님을 팀에서 제외하시겠습니까?`)) {
  //     try {
  //       const token = sessionStorage.getItem('token'); // 세션 스토리지에서 토큰 가져오기
  //       if (!token) {
  //         setMessage('로그인이 필요합니다.');
  //         return;
  //       }
  //
  //       await axios.delete(`/boards/${boardId}/members/${memberId}`, {
  //         headers: {
  //           Authorization: `Bearer ${token}`,
  //         },
  //       });
  //       const updatedMembers = members.filter(member => member.id !== memberId);
  //       setMembers(updatedMembers);
  //       alert(`${memberId}님이 팀에서 제외되었습니다.`);
  //     } catch (error) {
  //       console.error('Error excluding member:', error);
  //     }
  //   }
  // };

  const handleSubmit = (e) => {
    e.preventDefault();
    const memberId = newMemberId.trim();
    if (memberId) {
      addMember(memberId);
    } else {
      alert('멤버 ID를 입력해주세요.');
    }
  };

  return (
      <div>
        <header>
          <h1>Board Member</h1>
        </header>
        <div className="container">
          <div className="member-list">
            {members.map((member, index) => (
                <div key={index} className="member-item">
                  <div className="member-avatar">{member.userName.charAt(0)}</div>
                  <div className="member-info">
                    <div className="member-id">{member.userName}</div>
                    <div className="member-role">{member.userRole}</div>
                  </div>
                  {/* 추가적인 멤버 액션 버튼 */}
                  {/* <div className="member-actions">
                <button className="exclude-btn" onClick={() => excludeMember(member.id)}>제외</button>
              </div> */}
                </div>
            ))}
          </div>
          <div className="add-member-section">
            <h2>새 멤버 추가</h2>
            <form onSubmit={handleSubmit} className="add-member-form" id="addMemberForm">
              <input
                  type="text"
                  value={newMemberId}
                  onChange={(e) => setNewMemberId(e.target.value)}
                  placeholder="멤버 ID"
                  required
              />
              <button type="submit">추가하기</button>
            </form>
          </div>
          {/* 칸반 보드로 이동하는 버튼 */}
          <div style={{marginTop: '20px'}}>
            <Link to={`/boards/${boardId}/kanban`}>
              <button className="btn-kanban">칸반 보드로 이동</button>
            </Link>
          </div>
        </div>
      </div>
  );
};

export default BoardMember;