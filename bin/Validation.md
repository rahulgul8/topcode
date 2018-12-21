# Doppler App - Validation

## Pending Items:

The following logic still has not been defined since previous challenges as well as the Swagger, so please consider them as out of scope:

- When we need to create the notifications (e.g. someone likes your comment, someone replies your comment, ...)?
- When we need to gain badges for a user?

## Postman

- Configure and run the application as described in README.md

- Execute `src/test/resources/insert-test-data.sql` to insert test data in DB

- Import `docs/postman_collection.json` and `docs/postman_environment.json` into Postman

- Run each Postman call to verify each endpoint. You should run calls `Auth (RUN THESE CALLS FIRST)` first so that the JWT token is cached for subsequent calls

## Updated Swagger (`docs/swagger.yaml`) - Part 1

- `GET /events`:
  - Changed status filter enum to PAST and UPCOMING to match with Java enum style
  - Changed sort field `start` to `firstSessionStart` for more meaningful
  
- `POST /events/{eventId}/scanTicket`
  - Use a configured m2m token to authorize so that the other system can call this endpoint
  - Sent userId in the request body

- `DELETE /events/{eventId}/comments/{commentId}`:
  - Changed to soft deletion

- `PUT /events/{eventId}/comments/{commentId}`, `POST /events/{eventId}/comments/{commentId}/like` and `POST /events/{eventId}/comments/{commentId}/unlike`:
  - Not allowed to update/like/unlike a deleted comment

- `EventCommentRequest`:
  - Removed the unnecessary `id` field

- `Event`:
  - Changed `points` to `pointsForRegistering`
  - Added `pointsForScanningTicket`, this will used in scanTicket endpoint
  - Added `portraitImageUrl` and `landscapeImageUrl`

## Updated Swagger (`docs/swagger.yaml`) - Part 2

- `POST /userSettings`:
  - Added request body

- `POST /rewards/{rewardId}/redeem`:
  - Added logic to calculate the expiredAt

