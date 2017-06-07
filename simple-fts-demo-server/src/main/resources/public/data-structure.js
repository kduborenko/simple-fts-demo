(function () {
    function renderKeywordsRows(keywords) {
        let html = '';

        for (let id in keywords) if (keywords.hasOwnProperty(id)) {
            for (let i = 0; i < keywords[id].text.length; i++) {
                let kw = keywords[id].text[i];
                html += '<tr>';
                if (i === 0) {
                    html += `<td rowspan="${keywords[id].text.length}">${id}</td>`;
                }
                html += `<td class="mdl-data-table__cell--non-numeric">${kw.word}</td>`;
                html += `<td class="mdl-data-table__cell--non-numeric">${kw.position.start} .. ${kw.position.end}</td>`;
                html += '</tr>';
            }
        }

        return html;
    }

    function renderKeywordsTable(keywords) {
        let keywordsSection = document.createElement('div');

        keywordsSection.innerHTML = `
            <table class="mdl-data-table mdl-js-data-table mdl-shadow--2dp">
              <thead>
                <tr>
                  <th>ID</th>
                  <th class="mdl-data-table__cell--non-numeric">Word</th>
                  <th class="mdl-data-table__cell--non-numeric">Position</th>
                </tr>
              </thead>
              <tbody>
                ${renderKeywordsRows(keywords)}
              </tbody>
            </table>
        `;

        return keywordsSection;
    }


    function renderIndexRows(index) {
        let html = '';

        for (let word in index) if (index.hasOwnProperty(word)) {
            let firstId = true;
            for (let id in index[word]) if (index[word].hasOwnProperty(id)) {
                html += '<tr>';
                if (firstId) {
                    html += `<td rowspan="${Object.keys(index[word]).length}">${word}</td>`;
                    firstId = false;
                }
                html += `<td class="mdl-data-table__cell--non-numeric">${index[word][id].text}</td>`;
                html += '</tr>';
            }
        }

        return html;
    }

    function renderIndexTable(index) {
        let indexSection = document.createElement('div');

        indexSection.innerHTML = `
            <table class="mdl-data-table mdl-js-data-table mdl-shadow--2dp">
              <thead>
                <tr>
                  <th class="mdl-data-table__cell--non-numeric">Word</th>
                  <th class="mdl-data-table__cell--non-numeric">Note</th>
                </tr>
              </thead>
              <tbody>
                ${renderIndexRows(index)}
              </tbody>
            </table>
        `;

        return indexSection;

    }

    function loadDataStructure() {
        fetch('/fts/data-structure')
            .then(response => {
                let contentType = response.headers.get("content-type");
                if (contentType && contentType.indexOf("application/json") !== -1) {
                    return response.json().then(function (json) {
                        document.querySelector('#keywords')
                            .appendChild(renderKeywordsTable(json.keywords));
                        document.querySelector('#index')
                            .appendChild(renderIndexTable(json.index));
                    });
                } else {
                    // todo handle error
                }
            })
    }

    window.addEventListener('load', loadDataStructure);

})();