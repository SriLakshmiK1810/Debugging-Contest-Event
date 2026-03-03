let editor;
let questions = [];
let selectedQuestion = null;

require.config({ paths: { 'vs': 'https://unpkg.com/monaco-editor@0.45.0/min/vs' }});
require(['vs/editor/editor.main'], function () {

    editor = monaco.editor.create(document.getElementById('editor'), {
        value: '',
        language: 'python',
        theme: 'vs-dark'
        
    });
// ✅ Auto-save code
editor.onDidChangeModelContent(() => {
    localStorage.setItem("savedCode", editor.getValue());
});

// ✅ Restore saved code
const saved = localStorage.getItem("savedCode");
if (saved) {
    editor.setValue(saved);
}
    loadQuestions();
}
);

function loadQuestions() {
    fetch('http://localhost:8080/questions')
        .then(res => res.json())
        .then(data => {
            questions = data;
            const list = document.getElementById('questionList');

            data.forEach((q, index) => {
    const li = document.createElement('li');
    li.textContent = "Relay-" + String(index + 1).padStart(2, '0');

    li.onclick = () => selectQuestion(q, index, li);  // pass li also

    list.appendChild(li);
});
        });
}

function selectQuestion(q, index, clickedElement) {

    selectedQuestion = q;

    // ✅ Remove active from all relays
    document.querySelectorAll("#questionList li")
        .forEach(li => li.classList.remove("active"));

    // ✅ Add active to clicked relay
    clickedElement.classList.add("active");

    document.getElementById('questionTitle').innerText =
        "Relay-" + String(index + 1).padStart(2, '0');

    document.getElementById('questionDescription').innerText =
        q.description;

    const lang = document.getElementById('languageSelect').value;

    if (q.languages && q.languages[lang]) {
        editor.setValue(q.languages[lang]);
    }
}

window.onload = function() {
    const languageSelect = document.getElementById('languageSelect');

    if (languageSelect) {
        languageSelect.addEventListener('change', function() {
            if (selectedQuestion) {
                editor.setValue(selectedQuestion.languages[this.value]);
                monaco.editor.setModelLanguage(editor.getModel(), this.value);
            }
        });
    }
};
console.log("Run button clicked");
function runCode() {
    if (!selectedQuestion) {
        alert("Please select a question first!");
        return;
    }

    const code = editor.getValue();
    const selectedLanguage = document.getElementById('languageSelect').value;

    // Convert language name to Judge0 ID
    let languageId;

    switch (selectedLanguage) {
        case "python":
            languageId = 71;
            break;
        case "java":
            languageId = 62;
            break;
        case "c":
            languageId = 50;
            break;
        default:
            languageId = 71;
    }

    fetch('http://localhost:8080/submit', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            questionId: selectedQuestion.id,
            code: code,
            languageId: languageId   // ✅ NOW WE SEND IT
        })
    })
    .then(res => res.json())
    .then(data => {
        document.getElementById('result').innerText =
            data.status + " - " + data.message;
    })
    .catch(error => {
        console.error("Error:", error);
    });
}
// ✅ Warn before refresh (Ctrl+R / Reload / Close tab)
window.addEventListener("beforeunload", function (e) {
    e.preventDefault();
    e.returnValue = "Are you sure you want to refresh? Your progress will be lost.";
});