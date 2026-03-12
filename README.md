[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/NYuLn2p4)

# College Basketball Scores Android Application

## Student Information

**Name:** Ngoc Lam

**Computing ID:** czg9kd

**GitHub Username:** julialam071502

## Assignment

CS 4720 - HW3: Basketball Scores Application

## Description

An Android application built with Jetpack Compose that displays live NCAA college basketball scores for Men's and Women's Division 1 using the ncaa-api.henrygd.me API. The app supports:

- A date picker for selecting which day's games to view
- Toggle between Men's and Women's college basketball
- Live game scores with current period and clock
- Final scores with winner highlighted
- Upcoming games with scheduled start times
- Pull-to-refresh and manual refresh button
- Offline mode using a local Room SQLite database

## Features

- Date picker defaulting to today's date
- Men's / Women's segmented toggle button
- Game cards showing Away vs Home teams, scores, and game status
- Live games show current clock and period (halves for men's, quarters for women's)
- Final games show "Final" and highlight the winning team with a checkmark
- Upcoming games show the scheduled start time in Eastern Time
- Pull-to-refresh and refresh button in the toolbar
- Loading indicator shown during all API calls
- Offline mode — previously loaded games remain visible without internet
- Room database persists all scores across app restarts and updates scores on refresh
- Rotation-safe — no data loss on screen rotation or configuration changes

## Testing

- Tested on Android emulator with API 36 (Pixel 6 - Google APIs)
- Verified date picker defaults to today and changes load new games
- Verified Men's and Women's toggle fetches correct data
- Verified live, final, and upcoming game states display correctly
- Verified offline mode shows cached games when network is unavailable
- Verified scores update correctly on refresh
- Verified no data loss on screen rotation
