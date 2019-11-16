const express = require('express');
const shell = require('shelljs');
const app = express();
const port = 3000;

app.get('/publishData', (req, res) => {
    const iterations = req.query.iterations;
    const sleep = req.query.sleep;
    const maxBudget = req.query.maxBudget;
    const actions = req.query.actions;

    try {
	      shell.exec(`./publishData.sh ${iterations} ${sleep} ${maxBudget} ${actions}`);
        res.send(actions);
    } catch (e) {
        console.error(e);
    }
    
})

app.listen(port, () => console.log(`Example app listening on port ${port}!`));
