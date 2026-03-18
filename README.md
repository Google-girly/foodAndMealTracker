# 438-project2

## Project Description

Food and Meal Tracker is a full-stack application that allows users to log meals, track food intake, and manage nutrition data. The backend provides secure RESTful APIs with authentication, database integration, and data persistence for users, meals, and food entries.

## Team

@Google-girly
@mreynoso123
@dayshaunlyy
@Omar-Martinez-F

# Tech Stack

## Backend
- Java
- Spring Boot
- Spring Data JPA
- Spring Security + OAuth2 (Google Login)

## Database
- Supabase

## Tools
- Docker
- GitHub Actions
- Swagger (API docs)
- Postman (API testing)

## Frontend
- React
- JavaScript
- HTML/CSS

# Running it locally(with Docker)
- cd backend
- docker build -t food-api .
- docker run -p 8080:8080 \
-e SPRING_DATASOURCE_URL="jdbc:postgresql://aws-0-us-west-2.pooler.supabase.com:6543/postgres?sslmode=require" \
-e SPRING_DATASOURCE_USERNAME="postgres.peiotjyqfufhdqasyham" \
-e SPRING_DATASOURCE_PASSWORD="group3foodandmeal" \
-e SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=1 \
-e SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE=0 \
-e FRONTEND_ORIGIN="http://localhost:5173" \
food-api
- Base URL: http://localhost:8080

## Live API
- Base URL: https://food-meal-api.onrender.com
- Foods: https://food-meal-api.onrender.com/foods
- Meals: https://food-meal-api.onrender.com/meals
- Meal-Foods: https://food-meal-api.onrender.com/meal-foods

## Swagger UI
- http://localhost:8080/swagger-ui.html

## Postman Collection
Postman Collection Link: https://go.postman.co/collection/51750845-c699eb5b-1a68-4c39-a854-4273aec5e256?source=collection_link



# [Mockup Link](https://dart-cabin-67600433.figma.site)

##  Contribution Workflow

Please read:
- [CONTRIBUTING.md before creating branches](https://github.com/Google-girly/foodAndMealTracker/blob/chore/mauricio/github-workflow-setup/CONTRIBUTING.md)
- Pull Requests must use the provided template at [.github/pull_request_template.md](https://github.com/Google-girly/foodAndMealTracker/blob/chore/mauricio/github-workflow-setup/.github/pull_request_template.md)

- Issues must follow the User Story template at [.github/ISSUE_TEMPLATE/user_stories.md](https://github.com/Google-girly/foodAndMealTracker/blob/chore/mauricio/github-workflow-setup/.github/ISSUE_TEMPLATE/user_stories.md)


## Pull Requests Thus Far

|                                                         Linked PRs | Title | Author | Outcome | Date       |
|-------------------------------------------------------------------:|---|---|---|------------|
| [#25](https://github.com/Google-girly/foodAndMealTracker/pull/25) | Configure GitHub workflow and templates | @mreynoso123 | Merged | 2026-02-24 |
| [#4](https://github.com/Google-girly/foodAndMealTracker/pull/4) | Added mockup to README | @Google-girly | Merged | 2026-02-22 |
| [#3](https://github.com/Google-girly/foodAndMealTracker/pull/3) | added mockup link to README | @Google-girly | Closed | 2026-02-22 |
| [#2](https://github.com/Google-girly/foodAndMealTracker/pull/2) | frontend | @Google-girly | Merged | 2026-02-19 |
| [#1](https://github.com/Google-girly/foodAndMealTracker/pull/1) | Added file structure to backend files | @dayshaunlyy | Merged | 2026-02-19 |