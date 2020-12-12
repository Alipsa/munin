// This is provides a widget to input/generate spring cron expressions
"use strict";

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
        const arr = cronString.split(" ");
        if (arr.length !== 6) {
            throw cronString + " is not a valid spring cron expression";
        }
        this.setSecond(arr[0]);
        this.setMinute(arr[1]);
        this.setHour(arr[2]);
        this.setDayOfMonth(arr[3]);
        this.setMonth(arr[4]);
        this.setDaysOfWeek(arr[5]);
    }

    getValue() {
        return this.#second + " " + this.#minute + " " + this.#hour + " " + this.#dayOfMonth + " "
        + this.#month + " " + this.#daysOfWeek;
    }

    getSecond() {
        return this.#second;
    }

    setSecond(secondExp) {
        this.#second = secondExp;
    }

    getMinute() {
        return this.#minute;
    }

    setMinute(minuteExp) {
        this.#minute = minuteExp;
    }

    getHour() {
        return this.#hour;
    }

    setHour(hourExp) {
        this.#hour = hourExp;
    }

    getDayOfMonth() {
        return this.#dayOfMonth;
    }

    setDayOfMonth(dayOfMonthExp) {
        this.#dayOfMonth = dayOfMonthExp;
    }

    getMonth() {
        return this.#month;
    }

    setMonth(monthExp) {
        this.#month = monthExp;
    }

    getDaysOfWeek() {
        return this.#daysOfWeek;
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

    constructor(renderElement, onchangeAction, initial = "0 0 9 * * ?", label="Select period:") {
        this.#renderElement = renderElement;
        this.#cron = new Cron();
        this.#cron.setValue(initial);
        this.#label = label;
        this.#periodSelect = document.createElement("select");
        this.#periodSelect.setAttribute("name", "period");
        this.#onchange = onchangeAction;
    }

    render() {
        this.#clear();
        const lbl = document.createElement("label");
        lbl.appendChild(document.createTextNode(this.#label));
        this.#renderElement.appendChild(lbl);
        this.#renderElement.appendChild(blank());
        this.#periodSelect.appendChild(createOption(periods.MINUTES, "Minutes"));
        this.#periodSelect.appendChild(createOption(periods.HOURLY, "Hourly"));
        this.#periodSelect.appendChild(createOption(periods.DAILY, "Daily"));
        this.#periodSelect.appendChild(createOption(periods.WEEKLY, "Weekly"));
        this.#periodSelect.appendChild(createOption(periods.MONTHLY, "Monthly"));
        this.#periodSelect.appendChild(createOption(periods.YEARLY, "Yearly"));
        this.#renderElement.appendChild(this.#periodSelect);
        const instance = this;
        this.#periodSelect.onchange = function() {instance.renderPeriod(this.value, instance);};
        this.#renderMinutes();
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

    #setValue(valueString) {
        this.#cron.setValue(valueString);
        //console.log("valueString =", valueString, ", #cron.getValue() =", this.#cron.getValue());
        this.#onchange(this.#cron.getValue());
    }

    loadValue(cronExpression) {
        this.#cron.setValue(cronExpression);
        const c = this.#cron;
        //console.log("minute =", c.getMinute());
        // 0 * * * * ?
        // 0 0/8 * * * ?
        if ((c.getMinute() === "*" || c.getMinute().includes("/")) && c.getHour() === "*" && c.getDayOfMonth() === "*" && c.getMonth() === "*" && c.getDaysOfWeek() === "?") {
            this.#periodSelect.value = periods.MINUTES;
            this.#renderMinutes(c.getValue());
        }
        // 0 0 * * * ?
        // 0 0 0/2 * * ?
        else if ((c.getHour() === "*" || c.getHour().includes("0/")) && c.getDayOfMonth() === "*" && c.getMonth() === "*" && c.getDaysOfWeek() === "?") {
            this.#periodSelect.value = periods.HOURLY;
            this.#renderHourly(c.getValue());
        }
        // 0 0 0 * * ?
        // 0 0 12 * * ?
        // 0 7 16 1/3 * ?
        // 0 8 4 ? * 1-5
        else if (
           ((c.getDayOfMonth() === "*" || c.getDayOfMonth().includes("1/")) && c.getMonth() === "*" && c.getDaysOfWeek() === "?") ||
           (c.getDayOfMonth() === "?" && c.getMonth() === "*" && c.getDaysOfWeek().includes("-"))) {
            this.#periodSelect.value = periods.DAILY;
            this.#renderDaily(c.getValue());
        }
        // 0 7 12 ? * *
        // 0 7 12 ? * 1
        // 0 7 12 ? * 1,2,5
        else if (
           (c.getDayOfMonth() === "?" && c.getMonth() === "*" && c.getDaysOfWeek() === "*") ||
           (c.getDayOfMonth() === "?" && c.getMonth() === "*" && c.getDaysOfWeek().length === 1) ||
           (c.getDayOfMonth() === "?" && c.getMonth() === "*" && c.getDaysOfWeek().includes(","))
        ) {
            this.#periodSelect.value = periods.WEEKLY;
            this.#renderWeekly(c.getValue());
        }
        // 0 1 12 1 * ?
        // 0 16 12 ? * 1#1
        else if (
              (c.getDayOfMonth() !== "*" && c.getDaysOfWeek() === "?" && (c.getMonth() === "*" || c.getMonth().includes("/"))) ||
              ((c.getMonth() === "*" || c.getMonth().includes("/")) && (c.getDayOfMonth() === "?" || c.getDaysOfWeek().includes("#")))
            )  {
            this.#periodSelect.value = periods.MONTHLY;
            this.#renderMonthly(c.getValue());
        }
        // Quarterly, specify on what day in month number and what time
        // 0 0 1 5 */3 ?

        // Yearly
        // 0 0 12 1 1 ?
        // 0 18 12 27 2 ?
        // 0 0 12 ? 3 2#1
        else if (c.getDaysOfWeek() === "?" && isInteger(c.getMonth()) && isInteger(c.getDayOfMonth()) ||
           (c.getDayOfMonth() === "?" && isInteger(c.getMonth()) && c.getDaysOfWeek().includes("#"))
        ) {
            this.#periodSelect.value = periods.YEARLY;
            this.#renderYearly(c.getValue());
        }
        else {
            console.warn("Unknown cron pattern:", cronExpression);
            console.log("minute =", c.getMinute());
            console.log("hour =", c.getHour());
            console.log("DayOfMonth =", c.getDayOfMonth());
            console.log("Month =", c.getMonth());
            console.log("DaysOfWeek =", c.getDaysOfWeek());
            alert("Unknown cron pattern: " + cronExpression + ", see console log for details");
        }
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

    #renderMinutes(initial ="0 * * * * ?") {
        this.#clear();
        this.#setValue(initial);
        this.#onchange(this.#cron.getValue());
        const p = document.createElement("p");
        p.appendChild(document.createTextNode("Every "));
        const select = document.createElement("select");
        select.setAttribute("name", "minute");
        for (let i = 1; i <= 59; i++) {
            select.appendChild(createOption(i, i));
        }
        p.appendChild(select);
        select.value = starToOne(this.#cron.getMinute());

        p.appendChild(document.createTextNode(" minutes(s)"));
        this.#renderElement.appendChild(p);
        const instance = this;
        select.onchange = function() {
            instance.#cron.setMinute(oneToStar(select.value, "0"));
            instance.#onchange(instance.#cron.getValue());
        };
    }

    #renderHourly(initial= "0 0 * * * ?") {
        this.#clear();
        this.#setValue(initial);
        const p = document.createElement("p");
        //const everyRadio = createRadio("radioType", "every");
        //p.appendChild(everyRadio);
        p.appendChild(document.createTextNode(" Every "));
        const select = document.createElement("select");
        select.setAttribute("name", "hour");
        for (let i = 1; i < 24; i++) {
            select.appendChild(createOption(i, i));
        }
        const hour = this.#cron.getHour();
        select.value = starToOne(hour);
        const instance = this;
        select.onchange = function() {
            instance.#cron.setHour(oneToStar(select.value, "0"));
            instance.#onchange(instance.#cron.getValue());
        }
        p.appendChild(select);
        p.appendChild(document.createTextNode(" hour(s) "));
        this.#renderElement.appendChild(p);

        p.appendChild(document.createTextNode(" on minute "));
        const minuteSelect = createMinuteSelect();
        minuteSelect.setAttribute("name", "minute");
        minuteSelect.onchange = function () {
            instance.#cron.setMinute(parseInt(minuteSelect.value));
            instance.#onchange(instance.#cron.getValue());
        }
        minuteSelect.value = padZero(this.#cron.getMinute());
        p.appendChild(minuteSelect);
    }

    #appendTimeOfDay(label, p, radioButton = null) {
        p.setAttribute("data-name", "timeOfDay");
        const instance = this;
        p.appendChild(document.createTextNode(label));
        const hourSelect = createHourSelect();
        hourSelect.setAttribute("name", "atHour");
        hourSelect.onchange = function () {
            if (radioButton === null || radioButton.checked) {
                instance.#cron.setHour(parseInt(hourSelect.value));
                instance.#onchange(instance.#cron.getValue());
            }
        }
        p.appendChild(hourSelect);
        p.appendChild(document.createTextNode(" : "));
        const minuteSelect = createMinuteSelect();
        minuteSelect.setAttribute("name", "atMinute");
        minuteSelect.onchange = function () {
            if (radioButton === null || radioButton.checked) {
                instance.#cron.setMinute(parseInt(minuteSelect.value));
                instance.#onchange(instance.#cron.getValue());
            }
        }
        p.appendChild(minuteSelect);
        hourSelect.value = padZero(this.#cron.getHour());
        minuteSelect.value = padZero(this.#cron.getMinute());
        return {"hourSelect": hourSelect, "minuteSelect": minuteSelect};
    }

    #appendMonth(p2) {
        const instance = this;
        const everyMonthSelect = document.createElement("select");
        for (let i = 1; i <= 12; i++) {
            everyMonthSelect.appendChild(createOption(i, i));
        }
        everyMonthSelect.onchange = function () {
            instance.#cron.setMonth(parseInt(everyMonthSelect.value));
            instance.#onchange(instance.#cron.getValue());
        }
        p2.appendChild(everyMonthSelect);
        p2.appendChild(document.createTextNode(" month(s)"));
        return everyMonthSelect;
    }

    #renderDaily(initial = "0 0 0 * * ?") {
        this.#clear();
        this.#setValue(initial);
        const p = document.createElement("p");
        const everyRadio = createRadio("radioType", "every");
        p.appendChild(everyRadio);
        p.appendChild(document.createTextNode(" Every "));
        const select = document.createElement("select");
        select.setAttribute("name", "day");
        select.setAttribute("value", "every");
        for (let i = 1; i <= 90; i++) {
            select.appendChild(createOption(i, i));
        }

        const instance = this;
        select.onchange = function() {
            if ( everyRadio.checked === true) {
                instance.#cron.setDayOfMonth(oneToStar(select.value));
                instance.#onchange(instance.#cron.getValue());
            }
        }

        everyRadio.onclick = function() {
            instance.#cron.setDaysOfWeek("?");
            instance.#cron.setMonth(oneToStar(select.value, "0"));
            instance.#onchange(instance.#cron.getValue());
        }

        p.appendChild(select);
        p.appendChild(document.createTextNode(" day(s) "));
        this.#renderElement.appendChild(p);

        const p2 = document.createElement("p");
        p2.setAttribute("data-name", "everyWeekDay");
        const weekDaysRadio = createRadio("radioType", "weekdays");
        weekDaysRadio.setAttribute("data-name", "everyWeekDay");
        p2.appendChild(weekDaysRadio);
        p2.appendChild(document.createTextNode(" Every week day "));
        weekDaysRadio.onclick = function() {
            instance.#cron.setDayOfMonth("?");
            instance.#cron.setDaysOfWeek("1-5");
            instance.#onchange(instance.#cron.getValue());
        }
        this.#renderElement.appendChild(p2);

        const p3 = document.createElement("p");
        this.#appendTimeOfDay("Start time ", p3);
        //hourMinute.hourSelect.value = padZero(this.#cron.getHour());
        //hourMinute.minuteSelect.value = padZero(this.#cron.getMinute());
        this.#renderElement.appendChild(p3);

        if (this.#cron.getDaysOfWeek().includes("-")) {
            weekDaysRadio.checked = true;
        } else {
            everyRadio.checked = true;
            const day = this.#cron.getDayOfMonth();
            select.value = starToOne(day);
        }
    }

    #renderWeekly(initial= "0 0 0 ? * *") {
        this.#clear();
        this.#setValue(initial);
        const p = document.createElement("p");
        p.setAttribute("data-name", "daysOfWeek");
        const instance = this;
        function handleClick() {
            const listArray = Array.from(p.querySelectorAll("input"));
            const weekDays = [];
            listArray.forEach((item) => {
                if (item.checked) {
                    weekDays.push(item.getAttribute("value"));
                }
            });
            if (weekDays.length === 0) {
                instance.#cron.setDaysOfWeek("*");
            } else {
                instance.#cron.setDaysOfWeek(weekDays.join());
            }
            instance.#onchange(instance.#cron.getValue());
        }

        const monCheck = createCheckbox("weeklyMon", 1, handleClick);
        p.appendChild(monCheck);
        p.appendChild(document.createTextNode(" Mon "));
        p.appendChild(blank());

        const tueCheck = createCheckbox("weeklyTue", 2, handleClick);
        p.appendChild(tueCheck);
        p.appendChild(document.createTextNode(" Tue "));
        p.appendChild(blank());

        const wedCheck = createCheckbox("weeklyWed", 3, handleClick);
        p.appendChild(wedCheck);
        p.appendChild(document.createTextNode(" Wed "));
        p.appendChild(blank());

        const thuCheck = createCheckbox("weeklyThu", 4, handleClick);
        p.appendChild(thuCheck);
        p.appendChild(document.createTextNode(" Thu "));
        p.appendChild(blank());

        const friCheck = createCheckbox("weeklyFri", 5, handleClick);
        p.appendChild(friCheck);
        p.appendChild(document.createTextNode(" Fri "));
        p.appendChild(blank());

        const satCheck = createCheckbox("weeklySat", 6, handleClick);
        p.appendChild(satCheck);
        p.appendChild(document.createTextNode(" Sat "));
        p.appendChild(blank());

        const sunCheck = createCheckbox("weeklySun", 0, handleClick);
        p.appendChild(sunCheck);
        p.appendChild(document.createTextNode(" Sun "));
        this.#renderElement.appendChild(p);

        const days = this.#cron.getDaysOfWeek().split(",");
        for (let day of days) {
            switch (day) {
                case "1":
                    monCheck.checked = true;
                    break;
                case "2":
                    tueCheck.checked = true;
                    break;
                case "3":
                    wedChceck.checked = true;
                    break;
                case "4":
                    thuCheck.checked = true;
                    break;
                case "5":
                    friCheck.checked = true;
                    break;
                case "6":
                    satCheck.checked = true;
                    break;
                case "0":
                    sunCheck.checked = true;
                    break;
            }
        }

        const p2 = document.createElement("p");
        this.#appendTimeOfDay("Start time ", p2);
        //hourMinute.hourSelect.value = padZero(this.#cron.getHour());
        //hourMinute.minuteSelect.value = padZero(this.#cron.getMinute());
        this.#renderElement.appendChild(p2);
    }

    #renderMonthly(initial = "0 0 0 1 * ?") {
        this.#clear();
        this.#setValue(initial);
        const p = document.createElement("p");
        const byDay = createRadio("radioType", "byDay");
        byDay.checked = true;
        p.appendChild(byDay);
        p.appendChild(document.createTextNode(" Day "));
        const daySelect = document.createElement("select");
        for (let i = 1; i <= 31; i++) {
            daySelect.appendChild(createOption(i, i));
        }
        daySelect.value = this.#cron.getDayOfMonth();
        const instance = this;
        daySelect.onchange = function() {
            instance.#cron.setDayOfMonth(daySelect.value);
            instance.#onchange(instance.#cron.getValue());
        }
        p.appendChild(daySelect);
        p.appendChild(document.createTextNode(" of every "));

        const everyMonthSelect = this.#appendMonth(p);
        everyMonthSelect.onchange = function() {
            if (byDay.checked) {
                instance.#cron.setMonth(oneToStar(everyMonthSelect.value));
                instance.#onchange(instance.#cron.getValue());
            }
        }
        byDay.onclick = function() {
            instance.#cron.setDaysOfWeek("?");
            instance.#cron.setDayOfMonth(daySelect.value);
            instance.#cron.setMonth(oneToStar(everyMonthSelect.value));
            instance.#onchange(instance.#cron.getValue());
        }
        this.#renderElement.appendChild(p);

        // 0 0 0 ? * 1#1
        const p2 = document.createElement("p");
        const dayOfWeekRadio = createRadio("radioType", "dayOfWeek");
        p2.appendChild(dayOfWeekRadio);
        p2.appendChild(document.createTextNode(" The "));
        const weekNumSelect = createNthDaySelect();
        function daysOfWeekFromSelect() {
            if (instance.#cron.getDaysOfWeek() === "?") {
                instance.#cron.setDaysOfWeek("1#1");
            }
            const dow = instance.#cron.getDaysOfWeek().split("#");
            dow[1] = weekNumSelect.value;
            return dow[0] + "#" + dow[1];
        }
        weekNumSelect.onchange = function() {
            if (dayOfWeekRadio.checked) {
                instance.#cron.setDaysOfWeek(daysOfWeekFromSelect());
                instance.#onchange(instance.#cron.getValue());
            }
        }
        p2.appendChild(weekNumSelect);
        p2.appendChild(document.createTextNode(" "));
        const weekDaySelect  = createDaySelect();
        function weekDayFromSelect() {
            if (instance.#cron.getDaysOfWeek() === "?") {
                instance.#cron.setDaysOfWeek("1#1");
            }
            const dow = instance.#cron.getDaysOfWeek().split("#");
            dow[0] = weekDaySelect.value;
            return dow[0] + "#" + dow[1];
        }
        weekDaySelect.onchange = function() {
            if (dayOfWeekRadio.checked) {
                instance.#cron.setDaysOfWeek(weekDayFromSelect());
                instance.#onchange(instance.#cron.getValue());
            }
        }
        p2.appendChild(weekDaySelect);
        p2.appendChild(document.createTextNode(" of every "));

        const monthSelect = this.#appendMonth(p2);
        monthSelect.onchange = function() {
            if (dayOfWeekRadio.checked) {
                instance.#cron.setMonth(oneToStar(monthSelect.value));
                instance.#onchange(instance.#cron.getValue());
            }
        }
        dayOfWeekRadio.onclick = function() {
            instance.#cron.setDayOfMonth("?");
            instance.#cron.setDaysOfWeek(weekDayFromSelect());
            instance.#cron.setDaysOfWeek(daysOfWeekFromSelect());
            instance.#cron.setMonth(oneToStar(monthSelect.value));
            instance.#onchange(instance.#cron.getValue());
        }
        this.#renderElement.appendChild(p2);

        const p3 = document.createElement("p");
        this.#appendTimeOfDay("Start time ", p3, dayOfWeekRadio);
        this.#renderElement.appendChild(p3);

        if (this.#cron.getDaysOfWeek() === "?") {
            byDay.checked = true;
            daySelect.value = this.#cron.getDayOfMonth();
            everyMonthSelect.value = starToOne(this.#cron.getMonth());
        } else {
            // 0 16 12 ? 1/4 3#2
            dayOfWeekRadio.checked = true;
            const dow = this.#cron.getDaysOfWeek().split("#");
            weekDaySelect.value = dow[0];
            weekNumSelect.value = dow[1];
            monthSelect.value = starToOne(this.#cron.getMonth());
        }
    }

    #renderYearly(initial = "0 0 0 1 1 ?") {
        this.#clear();
        this.#setValue(initial);
        const p = document.createElement("p");
        const byDay = createRadio("radioType", "byDay");
        p.appendChild(byDay);
        p.appendChild(document.createTextNode(" Every "));
        const monthSelect = createMonthSelect();
        const instance = this;
        monthSelect.onchange = function() {
            if (byDay.checked) {
                instance.#cron.setMonth(monthSelect.value);
                instance.#onchange(instance.#cron.getValue());
            }
        }
        p.appendChild(monthSelect);
        p.appendChild(document.createTextNode(" "));

        const daySelect = document.createElement("select");
        for (let i = 1; i <= 31; i++) {
            daySelect.appendChild(createOption(i, i));
        }
        daySelect.onchange = function() {
            if (byDay.checked) {
                instance.#cron.setDayOfMonth(daySelect.value);
                instance.#onchange(instance.#cron.getValue());
            }
        }
        p.appendChild(daySelect);
        byDay.onclick = function() {
            instance.#cron.setDaysOfWeek("?");
            instance.#cron.setMonth(monthSelect.value);
            instance.#cron.setDayOfMonth(daySelect.value);
            instance.#onchange(instance.#cron.getValue());
        }
        this.#renderElement.appendChild(p);

        const p2 = document.createElement("p");
        const byWeek = createRadio("radioType", "byWeek");
        p2.appendChild(byWeek);
        p2.appendChild(document.createTextNode(" The "));
        const monthDaySelect = createNthDaySelect();
        function daysOfWeekFromSelect() {
            if (instance.#cron.getDaysOfWeek() === "?") {
                instance.#cron.setDaysOfWeek("1#1");
            }
            const dow = instance.#cron.getDaysOfWeek().split("#");
            dow[1] = monthDaySelect.value;
            return dow[0] + "#" + dow[1];
        }
        monthDaySelect.onchange = function() {
            if (byWeek.checked) {
                instance.#cron.setDaysOfWeek(daysOfWeekFromSelect());
                instance.#onchange(instance.#cron.getValue());
            }
        }
        p2.appendChild(monthDaySelect);
        p2.appendChild(document.createTextNode(" "));

        const weekDaySelect  = createDaySelect();
        function weekDayFromSelect() {
            if (instance.#cron.getDaysOfWeek() === "?") {
                instance.#cron.setDaysOfWeek("1#1");
            }
            const dow = instance.#cron.getDaysOfWeek().split("#");
            dow[0] = weekDaySelect.value;
            return dow[0] + "#" + dow[1];
        }
        weekDaySelect.onchange = function() {
            if (byWeek.checked) {
                instance.#cron.setDaysOfWeek(weekDayFromSelect());
                instance.#onchange(instance.#cron.getValue());
            }
        }
        p2.appendChild(weekDaySelect);
        p2.appendChild(document.createTextNode(" of "));
        const monthSelect2 = createMonthSelect();
        monthSelect2.onchange = function() {
            if (byWeek.checked) {
                instance.#cron.setMonth(monthSelect2.value);
                instance.#onchange(instance.#cron.getValue());
            }
        }
        p2.appendChild(monthSelect2);
        this.#renderElement.appendChild(p2);
        byWeek.onclick = function() {
            instance.#cron.setDayOfMonth("?");
            instance.#cron.setDaysOfWeek(daysOfWeekFromSelect());
            instance.#cron.setMonth(monthSelect2.value);
            instance.#cron.setDaysOfWeek(weekDayFromSelect());
            instance.#onchange(instance.#cron.getValue());
        }

        const p3 = document.createElement("p");
        this.#appendTimeOfDay("Start time ", p3, byWeek);
        this.#renderElement.appendChild(p3);
        // byDay, byWeek
        if (this.#cron.getDaysOfWeek() === "?") {
            byDay.checked = true;
            monthSelect.value = this.#cron.getMonth();
            daySelect.value = this.#cron.getDayOfMonth();
        } else {
            byWeek.checked = true;
            const dow = instance.#cron.getDaysOfWeek().split("#");
            weekDaySelect.value = dow[0];
            monthDaySelect.value = dow[1];
            monthSelect2.value = instance.#cron.getMonth();
        }
    }
}

function createOption(value, text) {
    const option = document.createElement("option");
    option.setAttribute("value", value);
    option.appendChild(document.createTextNode(text + " "));
    return option;
}

function createRadio(name, textValue) {
    const radio = document.createElement("input");
    radio.setAttribute("type", "radio");
    radio.setAttribute("name", name);
    radio.setAttribute("value", textValue);
    return radio;
}

function createCheckbox(name, value, onchangeFunction) {
    const input = document.createElement("input");
    input.setAttribute("type", "checkbox");
    input.setAttribute("name", name);
    input.setAttribute("value", value);
    input.onchange = function() { onchangeFunction(); };
    return input;
}

function createHourSelect() {
    const hourSelect = document.createElement("select");
    hourSelect.setAttribute("value", "every");
    for (let i = 0; i <= 23; i++) {
        if (i < 10) i = "0" + i;
        hourSelect.appendChild(createOption(i, i));
    }
    return hourSelect;
}

function createMinuteSelect() {
    const minuteSelect = document.createElement("select");
    minuteSelect.setAttribute("value", "every");
    for (let i = 0; i <= 59; i++) {
        if (i < 10) i = "0" + i;
        minuteSelect.appendChild(createOption(i, i));
    }
    return minuteSelect;
}

function createNthDaySelect() {
    const nthDaySelect = document.createElement("select");
    nthDaySelect.appendChild(createOption("1", "1st"));
    nthDaySelect.appendChild(createOption("2", "2nd"));
    nthDaySelect.appendChild(createOption("3", "3rd"));
    nthDaySelect.appendChild(createOption("4", "4th"));
    return nthDaySelect;
}

function createDaySelect() {
    const daySelect = document.createElement("select");
    daySelect.appendChild(createOption("1", "Mon"));
    daySelect.appendChild(createOption("2", "Tue"));
    daySelect.appendChild(createOption("3", "Wed"));
    daySelect.appendChild(createOption("4", "Thu"));
    daySelect.appendChild(createOption("5", "Fri"));
    daySelect.appendChild(createOption("6", "Sat"));
    daySelect.appendChild(createOption("0", "Sun"));
    return daySelect;
}

function createMonthSelect() {
    const monthSelect = document.createElement("select");
    monthSelect.appendChild(createOption("1", "Jan"));
    monthSelect.appendChild(createOption("2", "Feb"));
    monthSelect.appendChild(createOption("3", "Mar"));
    monthSelect.appendChild(createOption("4", "Apr"));
    monthSelect.appendChild(createOption("5", "May"));
    monthSelect.appendChild(createOption("6", "Jun"));
    monthSelect.appendChild(createOption("7", "Jul"));
    monthSelect.appendChild(createOption("8", "Aug"));
    monthSelect.appendChild(createOption("9", "Sep"));
    monthSelect.appendChild(createOption("10", "Oct"));
    monthSelect.appendChild(createOption("11", "Nov"));
    monthSelect.appendChild(createOption("12", "Dec"));
    return monthSelect;
}

function oneToStar(val, divider = "1") {
    return val === "1" ? "*" : divider + "/" + val;
}

function starToOne(val) {
    return val === "*" ? "1" : val.split("/")[1];
}

function blank() {
    return document.createTextNode("\u00A0");
}

function padZero(val) {
    if (parseInt(val) < 10) {
        return "0" + val;
    }
    return val;
}

function isInteger(val) {
    return /^\d+$/.test(val);
}

