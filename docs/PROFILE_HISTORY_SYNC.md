# Steply Mobile Profile & History Sync

## Mobile responsibility

The Android app keeps only these responsibilities:

1. Local profile storage
2. QR-based PC session linking
3. Remote camera streaming
4. Local copy of PC movement-history results

## Profile fields

Required:

- displayName / name
- age

Optional:

- gender
- heightCm
- movementNotes
- safetyNote

These fields are stored in Room and sent to the PC when the user scans a Steply Web QR session.

## History flow

1. Mobile opens a WebSocket connection for camera streaming.
2. PC receives camera frames and runs MediaPipe analysis in the worker.
3. PC saves the final analysis result through `/api/analysis/final`.
4. PC broadcasts a WebSocket message with `type: "final"`.
5. Mobile receives the message and stores the `result` JSON in `movement_history`.
6. The user can open `이전 이력` on the phone later without the PC.

## Important files

- `domain/model/UserProfile.kt`
- `domain/model/MovementHistory.kt`
- `data/local/entities/UserProfileEntity.kt`
- `data/local/entities/MovementHistoryEntity.kt`
- `data/local/dao/MovementHistoryDao.kt`
- `data/repository/MovementHistoryRepository.kt`
- `remote/RemoteCameraStreamer.kt`
- `ui/screens/history/HistoryScreen.kt`
- `ui/screens/history/HistoryViewModel.kt`
