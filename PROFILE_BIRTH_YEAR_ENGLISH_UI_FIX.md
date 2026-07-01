# Profile / Birth Year / English UI Fix

This patch keeps the mobile app focused on:

1. Local profile storage
2. QR-based PC linking
3. Camera streaming to the PC
4. Local history storage for PC analysis results

## Profile fields

Required:
- Name
- Birth Year

Optional:
- Gender
- Height (centimeters)
- Movement Notes
- Safety Note

Newly created or edited profiles are automatically selected so the QR connection screen can immediately send the profile to the PC.

## Database note

The local Room database version was bumped to 4 because `age` was replaced by `birthYear`. The project uses destructive fallback migration, so reinstalling or clearing app data is recommended while testing this MVP build.
