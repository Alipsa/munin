// This is provides a widget to input/generate spring cron expressions
const periods = {
    MINUTES: "minutes",
    HOURLY: "hourly",
    DAILY: "daily",
    WEEKLY: "weekly",
    MONTHLY: "monthly",
    YEARLY: "yearly"
}

class Cron {
    #second;
    #minute;
    #hour;
    #dayOfMonth;
    #month;
    #daysOfWeek;

    constructor() {
        this.#second = "*";
        this.#minute = "*";
        this.#hour = "*";
        this.#dayOfMonth = "*";
        this.#month = "*";
        this.#daysOfWeek = "*";
    }

    setValue(cronString) {
        let arr = cronString.split(" ");
        if (arr.length !== 6) {
            throw cronString + " is not a valid spring cron expression";
        }
        this.setSecond(arr[0]);
        this.setMinute(arr[1]);
        this.setHour(arr[2]);
        this.setDayOfMonth(arr[3]);
        this.setMinute(arr[4]);
        this.setDaysOfWeek(arr[5]);
    }

    getValue() {
        return this.#second + " " + this.#minute + " " + this.#hour + " " + this.#dayOfMonth + " "
        + this.#month + " " + this.#daysOfWeek;
    }

    setSecond(secondExp) {
        this.#second = secondExp;
    }

    setMinute(minuteExp) {
        this.#minute = minuteExp;
    }

    setHour(hourExp) {
        this.#hour = hourExp;
    }

    setDayOfMonth(dayOfMonthExp) {
        this.#dayOfMonth = dayOfMonthExp;
    }

    setMonth(monthExp) {
        this.#month = monthExp;
    }

    setDaysOfWeek(daysOfWeekExp) {
        this.#daysOfWeek = daysOfWeekExp;
    }
}

class CronWidget {
    #renderElement;
    #cron;
    #label;
    #periodSelect;
    #onchange;

    constructor(renderElement, onchangeAction, initial = "0 0 9 * * ?", label="Select period") {
        this.#renderElement = renderElement;
        this.#cron = new Cron();
        this.#cron.setValue(initial);
        this.#label = label;
        this.#periodSelect = document.createElement("select");
        this.#onchange = onchangeAction;
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
        return this.#cron.getValue();
    }

    loadValue(cronExpression) {
        this.#cron.setValue(cronExpression);
        // Todo fire selectors as appropriate
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
        this.#cron.setValue("0 * * * * *");
        this.#onchange(this.#cron.getValue());
        let p = document.createElement("p");
        p.appendChild(document.createTextNode("Every "));
        let select = document.createElement("select");
        for (let i = 1; i <= 59; i++) {
            select.appendChild(createOption(i, i));
        }
        p.appendChild(select);
        p.appendChild(document.createTextNode(" minutes(s)"));
        this.#renderElement.appendChild(p);
        let instance = this;
        select.onchange = function() {
            if (select.value === "1") {
                instance.#cron.setMinute("*");
            } else {
                instance.#cron.setMinute("0/" + select.value);
            }
            instance.#onchange(instance.#cron.getValue());
        };
    }

    #renderHourly() {
        this.#clear();

        let p = document.createElement("p");
        let everyRadio = createRadio("hourlyType", "every");
        p.appendChild(everyRadio);
        p.appendChild(document.createTextNode(" Every "));
        let select = document.createElement("select");
        select.setAttribute("value", "every");
        for (let i = 1; i < 24; i++) {
            select.appendChild(createOption(i, i));
        }
        p.appendChild(select);
        p.appendChild(document.createTextNode(" hour(s) "));
        this.#renderElement.appendChild(p);

        let p2 = document.createElement("p");
        let clockRadio = createRadio("hourlyType", "clock");
        p2.appendChild(clockRadio);
        p2.appendChild(document.createTextNode(" Every day at "));
        let hourSelect = createHourSelect();
        p2.appendChild(hourSelect);
        p2.appendChild(document.createTextNode(" : "));
        let minuteSelect = createMinuteSelect();
        p2.appendChild(minuteSelect);
        this.#renderElement.appendChild(p2);
    }

    #renderDaily() {
        this.#clear();
        let p = document.createElement("p");
        let everyRadio = createRadio("dailyType", "every");
        p.appendChild(everyRadio);
        p.appendChild(document.createTextNode(" Every "));
        let select = document.createElement("select");
        select.setAttribute("value", "every");
        for (let i = 1; i <= 90; i++) {
            select.appendChild(createOption(i, i));
        }
        p.appendChild(select);
        p.appendChild(document.createTextNode(" day(s) "));
        this.#renderElement.appendChild(p);

        let p2 = document.createElement("p");
        let weekDaysRadio = createRadio("dailyType", "weekdays");
        p2.appendChild(weekDaysRadio);
        p2.appendChild(document.createTextNode(" Every week day "));
        this.#renderElement.appendChild(p2);

        let p3 = document.createElement("p");
        p3.appendChild(document.createTextNode("Start time "));
        let hourSelect = createHourSelect();
        p3.appendChild(hourSelect);
        p3.appendChild(document.createTextNode(" : "));
        let minuteSelect = createMinuteSelect();
        p3.appendChild(minuteSelect);

        this.#renderElement.appendChild(p3);
    }

    #renderWeekly() {
        this.#clear();
        let p = document.createElement("p");
        p.appendChild(createCheckbox("weeklyMon"));
        p.appendChild(document.createTextNode(" Mon "));
        p.appendChild(createCheckbox("weeklyTue"));
        p.appendChild(document.createTextNode(" Tue "));
        p.appendChild(createCheckbox("weeklyWed"));
        p.appendChild(document.createTextNode(" Wed "));
        p.appendChild(createCheckbox("weeklyThu"));
        p.appendChild(document.createTextNode(" Thu "));
        p.appendChild(createCheckbox("weeklyFri"));
        p.appendChild(document.createTextNode(" Fri "));
        p.appendChild(createCheckbox("weeklySat"));
        p.appendChild(document.createTextNode(" Sat "));
        p.appendChild(createCheckbox("weeklySun"));
        p.appendChild(document.createTextNode(" Sun "));
        this.#renderElement.appendChild(p);

        let p2 = document.createElement("p");
        p2.appendChild(document.createTextNode("Start time "));
        let hourSelect = createHourSelect();
        p2.appendChild(hourSelect);
        p2.appendChild(document.createTextNode(" : "));
        let minuteSelect = createMinuteSelect();
        p2.appendChild(minuteSelect);

        this.#renderElement.appendChild(p2);
    }

    #renderMonthly() {
        this.#clear();
        let p = document.createElement("p");
        let byDay = createRadio("monthlyType", "byDay");
        p.appendChild(byDay);
        p.appendChild(document.createTextNode(" Day "));
        let daySelect = document.createElement("select");
        for (let i = 1; i <= 31; i++) {
            daySelect.appendChild(createOption(i, i));
        }
        p.appendChild(daySelect);
        p.appendChild(document.createTextNode(" of every "));
        let monthSelect = document.createElement("select");
        for (let i = 1; i <= 12; i++) {
            monthSelect.appendChild(createOption(i, i));
        }
        p.appendChild(monthSelect);
        p.appendChild(document.createTextNode(" month(s)"));
        this.#renderElement.appendChild(p);

        let p2 = document.createElement("p");
        let byWeek = createRadio("monthlyType", "byWeek");
        p2.appendChild(byWeek);
        p2.appendChild(document.createTextNode(" The "));
        let monthDaySelect = createNthDaySelect();
        p2.appendChild(monthDaySelect);
        p2.appendChild(document.createTextNode(" "));
        let weekDaySelect  = createDaySelect();
        p2.appendChild(weekDaySelect);
        p2.appendChild(document.createTextNode(" of every "));
        let everyMonthSelect = document.createElement("select");
        for (let i = 1; i <= 12; i++) {
            everyMonthSelect.appendChild(createOption(i, i));
        }
        p2.appendChild(everyMonthSelect);
        p2.appendChild(document.createTextNode(" month(s)"));
        this.#renderElement.appendChild(p2);

        let p3 = document.createElement("p");
        p3.appendChild(document.createTextNode("Start time "));
        let hourSelect = createHourSelect();
        p3.appendChild(hourSelect);
        p3.appendChild(document.createTextNode(" : "));
        let minuteSelect = createMinuteSelect();
        p3.appendChild(minuteSelect);
        this.#renderElement.appendChild(p3);
    }

    #renderYearly() {
        this.#clear();
        let p = document.createElement("p");
        let byDay = createRadio("yearlyType", "byDay");
        p.appendChild(byDay);
        p.appendChild(document.createTextNode(" Every "));
        let monthSelect = createMonthSelect();
        p.appendChild(monthSelect);
        p.appendChild(document.createTextNode(" "));

        let daySelect = document.createElement("select");
        for (let i = 1; i <= 31; i++) {
            daySelect.appendChild(createOption(i, i));
        }
        p.appendChild(daySelect);
        this.#renderElement.appendChild(p);

        let p2 = document.createElement("p");
        let byWeek = createRadio("yearlyType", "byWeek");
        p2.appendChild(byWeek);
        p2.appendChild(document.createTextNode(" The "));
        let monthDaySelect = createNthDaySelect()
        p2.appendChild(monthDaySelect);
        p2.appendChild(document.createTextNode(" "));

        let weekDaySelect  = createDaySelect();
        p2.appendChild(weekDaySelect);
        p2.appendChild(document.createTextNode(" of "));
        let monthSelect2 = createMonthSelect();
        p2.appendChild(monthSelect2);
        this.#renderElement.appendChild(p2);

        let p3 = document.createElement("p");
        p3.appendChild(document.createTextNode("Start time "));
        let hourSelect = createHourSelect();
        p3.appendChild(hourSelect);
        p3.appendChild(document.createTextNode(" : "));
        let minuteSelect = createMinuteSelect();
        p3.appendChild(minuteSelect);
        this.#renderElement.appendChild(p3);
    }
}

function createOption(name, textValue) {
    let option = document.createElement("option");
    option.setAttribute("value", name);
    option.appendChild(document.createTextNode(textValue));
    return option;
}

function createRadio(name, textValue) {
    let radio = document.createElement("input");
    radio.setAttribute("type", "radio");
    radio.setAttribute("name", name);
    radio.setAttribute("value", textValue);
    return radio;
}

function createCheckbox(name) {
    let input = document.createElement("input");
    input.setAttribute("type", "checkbox");
    input.setAttribute("name", name);
    return input;
}

function createHourSelect() {
    let hourSelect = document.createElement("select");
    hourSelect.setAttribute("value", "every");
    for (let i = 0; i <= 23; i++) {
        if (i < 10) i = "0" + i;
        hourSelect.appendChild(createOption(i, i));
    }
    return hourSelect;
}

function createMinuteSelect() {
    let minuteSelect = document.createElement("select");
    minuteSelect.setAttribute("value", "every");
    for (let i = 0; i <= 59; i++) {
        if (i < 10) i = "0" + i;
        minuteSelect.appendChild(createOption(i, i));
    }
    return minuteSelect;
}

function createNthDaySelect() {
    let nthDaySelect = document.createElement("select");
    nthDaySelect.appendChild(createOption("nthDay", "1st"));
    nthDaySelect.appendChild(createOption("nthDay", "2nd"));
    nthDaySelect.appendChild(createOption("nthDay", "3rd"));
    nthDaySelect.appendChild(createOption("nthDay", "4th"));
    return nthDaySelect;
}

function createDaySelect() {
    let daySelect = document.createElement("select");
    daySelect.appendChild(createOption("monthlyWeekDay", "Mon"));
    daySelect.appendChild(createOption("monthlyWeekDay", "Tue"));
    daySelect.appendChild(createOption("monthlyWeekDay", "Wed"));
    daySelect.appendChild(createOption("monthlyWeekDay", "Thu"));
    daySelect.appendChild(createOption("monthlyWeekDay", "Fri"));
    daySelect.appendChild(createOption("monthlyWeekDay", "Sat"));
    daySelect.appendChild(createOption("monthlyWeekDay", "Sun"));
    return daySelect;
}

function createMonthSelect() {
    let monthSelect = document.createElement("select");
    monthSelect.appendChild(createOption("month", "Jan"));
    monthSelect.appendChild(createOption("month", "Feb"));
    monthSelect.appendChild(createOption("month", "Mar"));
    monthSelect.appendChild(createOption("month", "Apr"));
    monthSelect.appendChild(createOption("month", "May"));
    monthSelect.appendChild(createOption("month", "Jun"));
    monthSelect.appendChild(createOption("month", "Jul"));
    monthSelect.appendChild(createOption("month", "Aug"));
    monthSelect.appendChild(createOption("month", "Sep"));
    monthSelect.appendChild(createOption("month", "Oct"));
    monthSelect.appendChild(createOption("month", "Nov"));
    monthSelect.appendChild(createOption("month", "Dec"));
    return monthSelect;
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

