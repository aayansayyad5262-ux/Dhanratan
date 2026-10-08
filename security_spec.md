# Security Specification: DhanRatan Games Firestore Rules

## 1. Data Invariants
- Every document in `/users/{userId}` belongs exclusively to the authenticated Firebase user whose `request.auth.uid == userId`.
- `userId` in the document body must equal the path wildcard `userId` and is immutable on update.
- `createdAt` must equal `request.time` on creation and is strictly immutable on update.
- `updatedAt` must equal `request.time` on both creation and update.
- `fullName` must be a non-empty string with length `<= 100`.
- `username` must be a non-empty string with length `<= 120`.
- `phone` must be a string with length `<= 15`.
- `balance` must be an integer `>= 0` and `<= 100000000`.
- Strict key whitelist: only `['userId', 'fullName', 'username', 'phone', 'balance', 'createdAt', 'updatedAt']` are allowed.

## 2. Dirty Dozen Attack Payloads
1. **Unauthenticated read/write**: `auth == null` attempting `get`, `list`, `create`, `update`, or `delete` on `/users/{userId}`.
2. **Cross-user read (PII leak)**: `user_a` attempting to `get` `/users/user_b` or `list` `/users` without `where("userId", "==", "user_a")`.
3. **Cross-user write**: `user_a` attempting to `create`, `update`, or `delete` `/users/user_b`.
4. **Ownership hijacking on create**: `user_a` creating `/users/user_a` with `userId: "user_b"`.
5. **Ownership mutation on update**: `user_a` updating `/users/user_a` to change `userId` to `"user_b"`.
6. **CreatedAt backdating on create**: `user_a` creating `/users/user_a` with a client-forged past timestamp instead of `request.time`.
7. **CreatedAt tampering on update**: `user_a` modifying `createdAt` during an `update`.
8. **UpdatedAt tampering**: `user_a` setting `updatedAt` to a non-server timestamp.
9. **Schema pollution (extra fields)**: `user_a` injecting `"isAdmin": true` into `/users/user_a`.
10. **Negative balance injection**: `user_a` setting `balance: -500`.
11. **Oversized string payload**: `user_a` setting `fullName` to a 5000-character string.
12. **Type confusion**: `user_a` setting `balance` to `"1000"` (string instead of int).
