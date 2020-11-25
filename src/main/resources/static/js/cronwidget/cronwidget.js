// This is provides a widget to input/generate spring cron expressions
const periods = {
    MINUTES: "minutes",
    HOURLY: "hourly",
    DAILY: "daily",
    WEEKLY: "weekly",
    MONTHLY: "monthly",
    YEARLY: "yearly"
}
class Cronwidget {
    constructor(renderElement, initial = "0 0 9 * * ?", label="Select period") {
        this.renderElement = renderElement;
        this.cron = initial;
        this.label = label;
        this.periodSelect = document.createElement("select");
    }

    render() {
        console.log("Rendering cronwidget");
        let lbl = document.createElement("label");
        lbl.appendChild(document.createTextNode(this.label + " "));
        this.renderElement.appendChild(lbl);
        this.periodSelect.appendChild(createOption(periods.MINUTES, "Minutes"));
        this.periodSelect.appendChild(createOption(periods.HOURLY, "Hourly"));
        this.periodSelect.appendChild(createOption(periods.DAILY, "Daily"));
        this.periodSelect.appendChild(createOption(periods.WEEKLY, "Weekly"));
        this.periodSelect.appendChild(createOption(periods.MONTHLY, "Monthly"));
        this.periodSelect.appendChild(createOption(periods.YEARLY, "Yearly"));
        this.renderElement.appendChild(this.periodSelect);
        let instance = this;
        this.periodSelect.onchange = function() {renderPeriod(this.value, instance);};
        // Todo: select based on initial and populate values as appropriate
    }
}

function createOption(name, textValue) {
    let option = document.createElement("option");
    option.setAttribute("value", name);
    option.appendChild(document.createTextNode(textValue));
    return option;
}

function renderPeriod(period, cronWidget) {
    switch (period) {
        case periods.MINUTES:
            renderMinutes(cronWidget);
            break;
        case periods.HOURLY:
            renderHourly(cronWidget);
            break;
        case periods.DAILY:
            renderDaily(cronWidget);
            break;
        case periods.WEEKLY:
            renderWeekly(cronWidget);
            break;
        case periods.MONTHLY:
            renderMonthly(cronWidget);
            break;
        case periods.YEARLY:
            renderYearly(cronWidget);
    }
}

function renderMinutes(cronWidget) {
    let p = document.createElement("p");
    p.appendChild(document.createTextNode("render minutes"));
    cronWidget.periodSelect.parentElement.appendChild(p);
}

function renderHourly(cronWidget) {
    console.log("renderHourly not yet implemented");
}

function renderDaily(cronWidget) {
    console.log("renderDaily not yet implemented");
}

function renderWeekly(cronWidget) {
    console.log("renderWeekly not yet implemented");
}

function renderMonthly(cronWidget) {
    console.log("renderMonthly not yet implemented");
}

function renderYearly(cronWidget) {
    console.log("renderYearly not yet implemented");
}