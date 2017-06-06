(function () {
    function search() {
        let searchTermInput = document.querySelector("#search-term");
        fetch('/fts?search=' + searchTermInput.value)
            .then(response => {
                let contentType = response.headers.get("content-type");
                if (contentType && contentType.indexOf("application/json") !== -1) {
                    return response.json().then(function (json) {
                        updateSearchResults(json)
                    });
                } else {
                    // todo handle error
                }
            });
    }

    function highlightMatches(sr, field) {
        let text = sr.document[field];
        let matches = sr.matches[field].sort();
        let result = text.substr(0, matches[0].position.start);
        for (let i = 0; i < matches.length; i++) {
            let match = matches[i];

            result += '<b>';
            result += text.substr(match.position.start,
                match.position.end - match.position.start + 1);
            result += '</b>';

            if (i + 1 === matches.length) {
                result += text.substr(match.position.end + 1);
            } else {
                result += text.substr(match.position.end + 1,
                    matches[i + 1].position.start - match.position.end - 1);
            }
        }
        return result;
    }

    function updateSearchResults(searchResults) {
        let searchResultsDiv = document.querySelector("#search-results");
        while (searchResultsDiv.hasChildNodes()) {
            searchResultsDiv.removeChild(searchResultsDiv.lastChild);
        }

        let minTextLength = Math.min(...searchResults.map(sr => sr.document.text.length));

        searchResults.forEach(sr => {
            let card = document.createElement('div');
            let cardSize = Math.min(3, Math.round(sr.document.text.length / minTextLength)) * 4;
            card.className = `mdl-card mdl-shadow--2dp mdl-cell mdl-cell mdl-cell--${cardSize}-col`;
            card.innerHTML = `
<div class="mdl-card__supporting-text">${highlightMatches(sr, 'text')}</div>
<div class="mdl-card__actions">
    <button class="mdl-button mdl-js-button mdl-js-button mdl-js-ripple-effect delete-btn">
      <i class="material-icons">delete</i>
    </button>
</div>`;
            card.querySelector('.mdl-card__actions .delete-btn')
                .addEventListener('click', () => removeNote(sr.document.id));
            componentHandler.upgradeElement(card);
            searchResultsDiv.appendChild(card);
        });
    }

    window.addNote = function () {
        let dialog = document.querySelector("#add-note");
        let newNoteTextarea = document.querySelector("#new-note-text");

        fetch('/fts', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({text: newNoteTextarea.value})
        }).then(() => {
            dialog.close();
            newNoteTextarea.value = '';
            search();
        });
    };

    window.removeNote = function (id) {
        fetch('/fts/' + id, {
            method: 'DELETE'
        }).then(() => {
            search();
        });
    };

    // Add listeners to components
    window.addEventListener('load', () => {
        document.querySelector('#search-btn')
            .addEventListener('click', () => search());
        document.querySelector('#add-note-btn')
            .addEventListener('click', () => document.querySelector('#add-note').show());
        document.querySelector('#close-add-note-dialog-btn')
            .addEventListener('click', () => document.querySelector('#add-note').close());
        document.querySelector('#add-new-note-btn')
            .addEventListener('click', () => addNote());
    })
})();