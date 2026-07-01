# Steply-Mobile 실행 가이드

## 목적

모바일 앱은 이제 분석 앱이 아니라 **원격 카메라 송출 앱**입니다.

## 모바일 화면 흐름

1. 프로필 선택 또는 등록
2. PC 웹 화면의 QR 스캔
3. 웹 세션에 프로필 연결
4. 카메라 권한 허용
5. `PC로 카메라 송출 시작` 버튼 클릭

## QR payload 형식

웹에서 생성되는 QR은 아래 JSON을 담고 있습니다.

```json
{
  "type": "steply-web-session",
  "sessionId": "SESSION_ID",
  "serverUrl": "http://192.168.0.12:3000"
}
```

모바일은 이 값을 파싱해서 다음을 수행합니다.

```text
POST /api/session/{sessionId}/connect
→ 프로필을 웹 세션에 연결
ws://PC_IP:3000/ws?sessionId=SESSION_ID&role=mobile
→ JPEG 카메라 프레임 송출
```
