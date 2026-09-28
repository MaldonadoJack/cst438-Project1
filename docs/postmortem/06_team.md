# Project 01 Post Mortem - \[Group 6 / cst438-Project1]

## Context

## By the numbers

* Issues opened: 27 ([issues](https://github.com/MaldonadoJack/cst438-Project1/issues)) | closed: 25
* Pull requests opened: 27 ([pull requests](https://github.com/MaldonadoJack/cst438-Project1/pulls)) | merged: 27
* Planned at kickoff: 15 stories | done: 13



## What went well

1. We used our group chat to share progress, request PR reviews, and let each other know when we were available. This really helped us coordinate tasks.
2. We established shared database functionality that other features could reuse. The user-linked food-log foundation supported adding, viewing, and deleting entries, while account editing preserved each user's existing food-log associations.
3. Pull request reviews helped identify missing tests. For example, the login review led to a follow-up PR with authentication tests, improving our confidence in the completed login flow.



## What went wrong

1. We had a major problem with the API we chose for the project. The API required an IP address as a kind of whitelist for who is allowed to access it, which caused a lot of problems with testing and running the app.
2. A second problem was the Gradle issue we had at the very beginning of the project. Gradle wasn't building on any of our systems so we had to edit the Gradle file along with some other config files to allow Gradle to build.



## Advice to our next teams

1. Verify the development environment and API access on every teammate's computer early. Each person should build the app and make an API request before starting features that depend on those tools.
2. Communicate which issues you are working on and announce when your scope changes. Our daily-log implementation covered several issues, so sharing that update helped other teammates avoid duplicating the same work.
3. Include relevant tests and clear testing instructions with each pull request. Request reviews early enough to address feedback before other features depend on the changes.



