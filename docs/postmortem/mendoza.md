# Julian Mendoza - Project 01 Retrospective

## My work
- Merged PRs: [#21](https://github.com/MaldonadoJack/cst438-Project1/pull/21), [#25](https://github.com/MaldonadoJack/cst438-Project1/pull/25), [#26](https://github.com/MaldonadoJack/cst438-Project1/pull/26), [#38](https://github.com/MaldonadoJack/cst438-Project1/pull/38), [#40](https://github.com/MaldonadoJack/cst438-Project1/pull/40), [#42](https://github.com/MaldonadoJack/cst438-Project1/pull/42), [#43](https://github.com/MaldonadoJack/cst438-Project1/pull/43), [#45](https://github.com/MaldonadoJack/cst438-Project1/pull/45), [#51](https://github.com/MaldonadoJack/cst438-Project1/pull/51), [#55](https://github.com/MaldonadoJack/cst438-Project1/pull/55)
- My issues: [#6](https://github.com/MaldonadoJack/cst438-Project1/issues/6), [#7](https://github.com/MaldonadoJack/cst438-Project1/issues/7), [#8](https://github.com/MaldonadoJack/cst438-Project1/issues/8), [#9](https://github.com/MaldonadoJack/cst438-Project1/issues/9), [#24](https://github.com/MaldonadoJack/cst438-Project1/issues/24), [#39](https://github.com/MaldonadoJack/cst438-Project1/issues/39), [#44](https://github.com/MaldonadoJack/cst438-Project1/issues/44), [#50](https://github.com/MaldonadoJack/cst438-Project1/issues/50), [#54](https://github.com/MaldonadoJack/cst438-Project1/issues/54)
- What I built: Local accounts, Room, password hashing, login checks, logout, the Compose home screen, and the user-linked food log.

## Biggest challenge
Connecting separately built screens through one user id and a shared database. Login, search, and nutrition facts were merged before each food entry had an owner. Pull request #45 passed the logged-in user's database id into Home and added the `food_log` table. Pull request #47 then built today's log on that table.

## Most valuable thing I learned
How to manage my issues and keep communicating with my team when things change.

## What I carry into Project 02
1. I will tell the team as soon as a story changes, before the next merge. I will know it worked if every change shows up in the issue or the group chat before the pull request is opened.
2. I will lock the API in the first week and prove one real call works. I will know it worked if that call succeeds from each teammate's machine before we build screens on top of it.
