(function () {
    window.search = function () {
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
    };

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

        searchResults.forEach(sr => {
            let card = document.createElement('div');
            card.className = 'demo-card-wide mdl-card mdl-shadow--2dp mdl-cell';
            card.innerHTML = `
<div class="mdl-card__title">
    <h2 class="mdl-card__title-text">${sr.document.id}</h2>
</div>
<div class="mdl-card__supporting-text">${highlightMatches(sr, 'text')}</div>`;

            componentHandler.upgradeElement(card);
            searchResultsDiv.appendChild(card);
        });
    }
})();