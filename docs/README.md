# MaybeWeijun – User Guide

Welcome to **MaybeWeijun**, your friendly chatbot for managing tasks from the command line or GUI!  
This guide will help you get started, explain every supported feature, and make sure you get the most out of MaybeWeijun.

---

## Table of Contents
- [Introduction](#introduction)
- [Getting Started](#getting-started)
- [Command Overview](#command-overview)
    - [`todo`](#todo)
    - [`deadline`](#deadline)
    - [`event`](#event)
    - [`list`](#list)
    - [`sort`](#sort)
    - [`mark`](#mark)
    - [`unmark`](#unmark)
    - [`delete`](#delete)
    - [`find`](#find)
    - [`bye`](#bye)
- [Date/Time Format](#datetime-format)
- [Sample Session](#sample-session)


---

## Introduction

**MaybeWeijun** is a simple task-tracking chatbot.  
You interact using short text commands to add, view, mark as done, delete, search, and sort tasks—all instantly and with friendly output!

Supported task types:
- **Todo**: a plain task
- **Deadline**: a task with a due date/time
- **Event**: a task with a start _and_ end date/time

---

## Getting Started

1. Open MaybeWeijun in your application (terminal or GUI).
2. Type a command and hit Enter.
3. Read the response and continue!

## Command Overview
- **Todo**: /todo <task>
- **Deadline**: /deadline <task> /by <due date/time>
- **Event**: /event <task> /from <start date/time> /to <end date/time>
- **List**: /list
- **Sort**: /sort 
- **Mark**: /mark <task_no>
- **Unmark**: /unmark <task_no>
- **Delete**: /delete <task_no>
- **Find**: /find <task_name>
- **Bye**: /bye

## Date/Time Format
- **yyyy-MM-dd HHmm**: 2021-01-01 1200 corresponds to 12:00am on January 1, 2021

## Sample Session
/todo homework
/deadline proposal /by 2021-01-01 1200
/event meeting /from 2021-01-01 1200 /to 2021-01-01 1300
/list
/sort
/mark 1
/unmark 2
/delete 3
/find homework
/bye
