# Steply Web ↔ Mobile Remote Camera Contract

## 1. QR 생성

웹은 QR payload를 생성합니다.

```json
{
  "type": "steply-web-session",
  "sessionId": "SESSION_ID",
  "serverUrl": "http://YOUR_PC_IP:3000"
}
```

## 2. 프로필 연결

모바일은 QR 스캔 후 선택된 로컬 프로필을 웹 세션에 보냅니다.

`POST /api/session/{sessionId}/connect`

```json
{
  "sessionId": "SESSION_ID",
  "profile": {
    "id": "local-profile-id",
    "displayName": "홍길동",
    "birthYear": 1950,
    "heightCm": 165,
    "mobilityNote": "필요 시 보호자 동행",
    "emergencyNote": "어지러우면 즉시 중단"
  }
}
```

## 3. 카메라 송출

모바일은 아래 WebSocket으로 연결합니다.

```text
ws://PC_IP:3000/ws?sessionId=SESSION_ID&role=mobile
```

연결 후 모바일은 약 10fps로 JPEG binary frame을 전송합니다.
웹은 binary frame을 dashboard socket으로 broadcast해서 PC 화면에 표시합니다.
