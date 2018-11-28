[![Waffle.io - Columns and their card count](https://badge.waffle.io/uhh-lt/NLP_ThumbnailAnnotator.svg?columns=all)](https://waffle.io/uhh-lt/NLP_ThumbnailAnnotator)
[![Build Status](https://travis-ci.org/uhh-lt/NLP_ThumbnailAnnotator.svg?branch=master)](https://travis-ci.org/uhh-lt/NLP_ThumbnailAnnotator)


# Thumbnail Annotator
Master Project Web Interfacecs for Language Processing Systems in Language Technology department at the University of Hamburg 

## Basic Idea
The Idea is to get 'context sensitive' Thumbnails for specific words (nouns, named entities) that appear in an input text and induce a 'sense' by editing the priority of the shown Thumbnails.
For example let's have the sentence "My computer has a mouse and a gaming keyboard". If a user clicks on the word "mouse", a list of Thumbnails of animals and technical devices shows up. Now a User can increment or decrement the priority of the Thumbnails to induce the sense for the word "mouse" in this specific context. If we now input the sentence "I have a mouse and a dog." and the User clicks n the word "mouse" again a list of Thumbnails shows up. Since now the word appears in a different context, the Thumbnails won't have a the prioritization and the User can induce the sense of "mouse" with this context again.
The goal of the project is a PoC Application including a Web Frontend as well as a REST API. The moste imporatant technologies or frameworks, used to achieve this goal, are be UIMA, DKPro, Spring(Boot), Redis, Bootstrap, Vue.js and Docker.

## Guides
* [Admin Guide](./docs/ADMIN.md)
* [Developer Guide](./docs/DEV.md)
* [User Guide](./docs/USER.md)
