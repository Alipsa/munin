// This is provides a widget to input/generate spring cron expressions
const periods = {
    MINUTES: "minutes",
    HOURLY: "hourly",
    DAILY: "daily",
    WEEKLY: "weekly",
    MONTHLY: "monthly",
    YEARLY: "yearly"
}

class CronWidget {
    #renderElement;
    #cron;
    #label;
    #periodSelect;

    constructor(renderElement, initial = "0 0 9 * * ?", label="Select period") {
        this.#renderElement = renderElement;
        this.#cron = initial;
        this.#label = label;
        this.#periodSelect = document.createElement("select");
    }

    render() {
        this.#clear();
        console.log("Rendering cronwidget");
        let lbl = document.createElement("label");
        lbl.appendChild(document.createTextNode(this.#label + " "));
        this.#renderElement.appendChild(lbl);
        this.#periodSelect.appendChild(createOption(periods.MINUTES, "Minutes"));
        this.#periodSelect.appendChild(createOption(periods.HOURLY, "Hourly"));
        this.#periodSelect.appendChild(createOption(periods.DAILY, "Daily"));
        this.#periodSelect.appendChild(createOption(periods.WEEKLY, "Weekly"));
        this.#periodSelect.appendChild(createOption(periods.MONTHLY, "Monthly"));
        this.#periodSelect.appendChild(createOption(periods.YEARLY, "Yearly"));
        this.#renderElement.appendChild(this.#periodSelect);
        let instance = this;
        this.#periodSelect.onchange = function() {instance.renderPeriod(this.value, instance);};
        // Todo: select based on initial and populate values as appropriate
    }

    renderPeriod(period) {
        switch (period) {
            case periods.MINUTES:
                this.#renderMinutes();
                break;
            case periods.HOURLY:
                this.#renderHourly();
                break;
            case periods.DAILY:
                this.#renderDaily();
                break;
            case periods.WEEKLY:
                this.#renderWeekly();
                break;
            case periods.MONTHLY:
                this.#renderMonthly();
                break;
            case periods.YEARLY:
                this.#renderYearly();
        }
    }

    getValue() {
        return this.#cron;
    }

    setValue(cronExpression) {
        this.#cron = cronExpression;
        this.render();
    }

    #clear() {
        while (this.#renderElement.firstChild) {
            if (this.#renderElement.lastChild === this.#periodSelect) {
                break;
            } else {
                this.#renderElement.removeChild(this.#renderElement.lastChild);
            }
        }
    }

    #renderMinutes() {
        this.#clear();
        let p = document.createElement("p");
        p.appendChild(document.createTextNode("Every "));
        let select = document.createElement("select");
        for (let i = 1; i < 60; i++) {
            select.appendChild(createOption(i, i));
        }
        p.appendChild(select);
        p.appendChild(document.createTextNode(" minutes(s)"));
        this.#renderElement.appendChild(p);
    }

    #renderHourly() {
        this.#clear();
        let p = document.createElement("p");
        p.appendChild(document.createTextNode("render Hourly"));
        this.#renderElement.appendChild(p);
    }

    #renderDaily() {
        this.#clear();
        let p = document.createElement("p");
        p.appendChild(document.createTextNode("render daily"));
        this.#renderElement.appendChild(p);
    }

    #renderWeekly() {
        this.#clear();
        console.log("renderWeekly not yet implemented");
    }

    #renderMonthly() {
        this.#clear();
        console.log("renderMonthly not yet implemented");
    }

    #renderYearly() {
        this.#clear();
        console.log("renderYearly not yet implemented");
    }
}

function createOption(name, textValue) {
    let option = document.createElement("option");
    option.setAttribute("value", name);
    option.appendChild(document.createTextNode(textValue));
    return option;
}

function htmlToElement(html) {
    let template = document.createElement('template');
    html = html.trim(); // Never return a text node of whitespace as the result
    template.innerHTML = html;
    return template.content.firstChild;
}

function htmlToElements(html) {
    let template = document.createElement('template');
    template.innerHTML = html;
    return template.content.childNodes;
}

