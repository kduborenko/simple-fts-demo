(function () {
    function renderKeywordsRows(keywords, index) {
        let html = '';

        for (let id in keywords) if (keywords.hasOwnProperty(id)) {
            for (let i = 0; i < keywords[id].text.length; i++) {
                let kw = keywords[id].text[i];
                html += '<tr>';
                if (i === 0) {
                    html += `<td rowspan="${keywords[id].text.length}">${index[kw.word][id].text}</td>`;
                }
                html += `<td class="mdl-data-table__cell--non-numeric">${kw.word}</td>`;
                html += `<td class="mdl-data-table__cell--non-numeric">${kw.position.start} .. ${kw.position.end}</td>`;
                html += '</tr>';
            }
        }

        return html;
    }

    function renderKeywordsTable(keywords, index) {
        let keywordsSection = document.createElement('div');

        keywordsSection.innerHTML = `
            <table class="mdl-data-table mdl-js-data-table mdl-shadow--2dp">
              <thead>
                <tr>
                  <th>Text</th>
                  <th class="mdl-data-table__cell--non-numeric">Word</th>
                  <th class="mdl-data-table__cell--non-numeric">Position</th>
                </tr>
              </thead>
              <tbody>
                ${renderKeywordsRows(keywords, index)}
              </tbody>
            </table>
        `;

        return keywordsSection;
    }

    function highlightMatches(text, kws) {
        kws = kws.sort();
        let result = text.substr(0, kws[0].position.start);
        for (let i = 0; i < kws.length; i++) {
            let kw = kws[i];

            result += '<b>';
            result += text.substr(kw.position.start,
                kw.position.end - kw.position.start + 1);
            result += '</b>';

            if (i + 1 === kws.length) {
                result += text.substr(kw.position.end + 1);
            } else {
                result += text.substr(kw.position.end + 1,
                    kws[i + 1].position.start - kw.position.end - 1);
            }
        }
        return result;
    }

    function renderIndexRows(index, keywords) {
        let html = '';

        for (let word in index) if (index.hasOwnProperty(word)) {
            let firstId = true;
            for (let id in index[word]) if (index[word].hasOwnProperty(id)) {
                html += '<tr>';
                if (firstId) {
                    html += `<td rowspan="${Object.keys(index[word]).length}">${word}</td>`;
                    firstId = false;
                }
                let kws = keywords[id].text.filter(i => i.word === word);
                let text = highlightMatches(index[word][id].text, kws);
                html += `<td class="mdl-data-table__cell--non-numeric">${text}</td>`;
                html += '</tr>';
            }
        }

        return html;
    }

    function renderIndexTable(index, keywords) {
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
                ${renderIndexRows(index, keywords)}
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
                            .appendChild(renderKeywordsTable(json.keywords, json.index));
                        document.querySelector('#index')
                            .appendChild(renderIndexTable(json.index, json.keywords));
                    });
                } else {
                    // todo handle error
                }
            })
    }

    window.addEventListener('load', loadDataStructure);

})();