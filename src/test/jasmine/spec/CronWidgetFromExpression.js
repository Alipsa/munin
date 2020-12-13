describe("cron expression input", function() {

   let fixture;

   beforeEach(function(){
      fixture = createTestFixture();
   });

   afterEach(function() {
      removeFixture(fixture);
   });

   it("MINUTES: '0 * * * * ?' should render every minute", function() {
      fixture.widget.loadValue("0 * * * * ?");
      const period = periodSelect();
      expect(period.value).toEqual(periods.MINUTES);
      const minutes = document.querySelector("#cronWidget > p > select");
      expect(minutes.value).toEqual("1");
   });

   // 0 0/2 * * * ?
   it("MINUTES: '0 0/2 * * * ?' should render every other minute", function() {
      fixture.widget.loadValue("0 0/2 * * * ?");
      const period = periodSelect();
      expect(period.value).toEqual(periods.MINUTES);
      const minutes = document.querySelector("#cronWidget > p > select");
      expect(minutes.value).toEqual("2");
   });

   it("HOURLY: '0 0 * * * ?' should render every hour", function() {
      fixture.widget.loadValue("0 0 * * * ?");
      const period = periodSelect();
      expect(period.value).toEqual(periods.HOURLY);
      const hour = document.querySelector("#cronWidget > p > select[name = 'hour']")
      expect(hour.value).toEqual("1");
   });

   it("HOURLY: '0 8 0/9 * * ?' should render every 9:th hour on the 8:th minute", function() {
      fixture.widget.loadValue("0 8 0/9 * * ?");
      const period = periodSelect();
      expect(period.value).toEqual(periods.HOURLY);
      const hour = document.querySelector("#cronWidget > p > select[name = 'hour']");
      expect(hour.value).toEqual("9");
      const minute = document.querySelector("#cronWidget > p > select[name='minute']");
      expect(minute.value).toEqual("08");
   });

   it("DAILY: '0 0 0 * * ?' should render daily at midnight", function() {
      fixture.widget.loadValue("0 0 0 * * ?");
      const period = periodSelect();
      expect(period.value).toEqual(periods.DAILY);
      const day = document.querySelector("#cronWidget > p > select[name = 'day']");
      expect(day.value).toEqual("1");
      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      expect(atHour.value).toEqual("00");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      expect(atMinute.value).toEqual("00");
   });

   it("DAILY: '0 23 12 1/51 * ?' should render every 51 days at 12:23", function() {
      fixture.widget.loadValue("0 23 12 1/51 * ?");
      const period = periodSelect();
      expect(period.value).toEqual(periods.DAILY);
      const day = document.querySelector("#cronWidget > p > select[name = 'day']");
      expect(day.value).toEqual("51");
      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      expect(atHour.value).toEqual("12");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      expect(atMinute.value).toEqual("23");
   })

   it("DAILY: '0 11 3 ? * 1-5' should render every week day at 03:11", function() {
      fixture.widget.loadValue("0 11 3 ? * 1-5");
      const period = periodSelect();
      expect(period.value).toEqual(periods.DAILY);
      const day = document.querySelector("#cronWidget > p > input[data-name = 'everyWeekDay']");
      expect(day.checked).toEqual(true);
      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      expect(atHour.value).toEqual("03");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      expect(atMinute.value).toEqual("11");
   })

   it("WEEKLY: '0 0 0 ? * *' should render weekly at midnight", function() {
      fixture.widget.loadValue("0 0 0 ? * *");
      const period = periodSelect();
      expect(period.value).toEqual(periods.WEEKLY);
      function weekDay(name) {
         return document.querySelector("#cronWidget > p[data-name=daysOfWeek] > input[name='" + name + "']");
      }
      expect(weekDay("weeklyMon").checked).toEqual(false);
      expect(weekDay("weeklyTue").checked).toEqual(false);
      expect(weekDay("weeklyWed").checked).toEqual(false);
      expect(weekDay("weeklyThu").checked).toEqual(false);
      expect(weekDay("weeklyFri").checked).toEqual(false);
      expect(weekDay("weeklySat").checked).toEqual(false);
      expect(weekDay("weeklySun").checked).toEqual(false);
      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      expect(atHour.value).toEqual("00");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      expect(atMinute.value).toEqual("00");
   });

   it("WEEKLY: '0 3 20 ? * 1,4,0' should render weekly on mon,thu,sun at 20:03", function() {
      fixture.widget.loadValue("0 3 20 ? * 1,4,0");
      expect(periodSelect().value).toEqual(periods.WEEKLY);
      function weekDay(name) {
         return document.querySelector("#cronWidget > p[data-name=daysOfWeek] > input[name='" + name + "']");
      }
      expect(weekDay("weeklyMon").checked).toEqual(true);
      expect(weekDay("weeklyTue").checked).toEqual(false);
      expect(weekDay("weeklyWed").checked).toEqual(false);
      expect(weekDay("weeklyThu").checked).toEqual(true);
      expect(weekDay("weeklyFri").checked).toEqual(false);
      expect(weekDay("weeklySat").checked).toEqual(false);
      expect(weekDay("weeklySun").checked).toEqual(true);
      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      expect(atHour.value).toEqual("20");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      expect(atMinute.value).toEqual("03");
   });

   it("MONTHLY: '0 0 0 1 * ?' should render monthly on day one of every month at midnight", function() {
      fixture.widget.loadValue("0 0 0 1 * ?");
      expect(periodSelect().value).toEqual(periods.MONTHLY);
      const radio = document.querySelector("#cronWidget > p[data-name='dayOfEveryNthMonth'] > input[type='radio']");
      expect(radio.checked).toEqual(true);
      expect(radio.getAttribute("value")).toEqual("byDay");
      const radio2 = document.querySelector("#cronWidget > p[data-name='NthWeekDayOfEveryNthMonth'] > input[type='radio']");
      expect(radio2.checked).toEqual(false);
      expect(radio2.getAttribute("value")).toEqual("dayOfWeek");

      const dayNum = document.querySelector("#cronWidget > p[data-name='dayOfEveryNthMonth'] > select[name='dayNum']");
      expect(dayNum.value).toEqual("1");

      const monthNum = document.querySelector("#cronWidget > p[data-name='dayOfEveryNthMonth'] > select[name='monthNum']");
      expect(monthNum.value).toEqual("1");

      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      expect(atHour.value).toEqual("00");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      expect(atMinute.value).toEqual("00");
   });

   it("MONTHLY: '0 58 14 25 1/3 ?' should render monthly on day 25 of every 3:rd month at 14:58", function() {
      fixture.widget.loadValue("0 58 14 25 1/3 ?");
      expect(periodSelect().value).toEqual(periods.MONTHLY);
      const radio = document.querySelector("#cronWidget > p[data-name='dayOfEveryNthMonth'] > input[type='radio']");
      expect(radio.checked).toEqual(true);
      expect(radio.getAttribute("value")).toEqual("byDay");

      const dayNum = document.querySelector("#cronWidget > p[data-name='dayOfEveryNthMonth'] > select[name='dayNum']");
      expect(dayNum.value).toEqual("25");

      const monthNum = document.querySelector("#cronWidget > p[data-name='dayOfEveryNthMonth'] > select[name='monthNum']");
      expect(monthNum.value).toEqual("3");

      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      expect(atHour.value).toEqual("14");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      expect(atMinute.value).toEqual("58");
   });

   it("MONTHLY: '0 0 0 ? * 1#1' should render the first monday of of every 1 months", function() {
      fixture.widget.loadValue("0 0 0 ? * 1#1");
      expect(periodSelect().value).toEqual(periods.MONTHLY);
      const radio = document.querySelector("#cronWidget > p[data-name='NthWeekDayOfEveryNthMonth'] > input[type='radio']");
      expect(radio.checked).toEqual(true);
      expect(radio.getAttribute("value")).toEqual("dayOfWeek");

      const weekOrdinal = document.querySelector("#cronWidget > p[data-name='NthWeekDayOfEveryNthMonth'] > select[name='ordinal']");
      expect(weekOrdinal.value).toEqual("1");

      const weekDay = document.querySelector("#cronWidget > p[data-name='NthWeekDayOfEveryNthMonth'] > select[name='weekDay']");
      expect(weekDay.value).toEqual("1");

      const monthOrdinal = document.querySelector("#cronWidget > p[data-name='NthWeekDayOfEveryNthMonth'] > select[name='month']");
      expect(monthOrdinal.value).toEqual("1");

      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      expect(atHour.value).toEqual("00");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      expect(atMinute.value).toEqual("00");
   });

   it("MONTHLY: '0 15 3 ? 1/11 0#4' should render the 4th sunday of of every 11 months", function() {
      fixture.widget.loadValue("0 15 3 ? 1/11 0#4");
      expect(periodSelect().value).toEqual(periods.MONTHLY);
      const radio = document.querySelector("#cronWidget > p[data-name='NthWeekDayOfEveryNthMonth'] > input[type='radio']");
      expect(radio.checked).toEqual(true);
      expect(radio.getAttribute("value")).toEqual("dayOfWeek");

      const weekOrdinal = document.querySelector("#cronWidget > p[data-name='NthWeekDayOfEveryNthMonth'] > select[name='ordinal']");
      expect(weekOrdinal.value).toEqual("4");

      const weekDay = document.querySelector("#cronWidget > p[data-name='NthWeekDayOfEveryNthMonth'] > select[name='weekDay']");
      expect(weekDay.value).toEqual("0");

      const monthOrdinal = document.querySelector("#cronWidget > p[data-name='NthWeekDayOfEveryNthMonth'] > select[name='month']");
      expect(monthOrdinal.value).toEqual("11");

      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      expect(atHour.value).toEqual("03");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      expect(atMinute.value).toEqual("15");
   });

   it("YEARLY: '0 0 0 1 1 ?' should render jan 1 at midnight", function(){
      fixture.widget.loadValue("0 0 0 1 1 ?");
      expect(periodSelect().value).toEqual(periods.YEARLY);
      const radio = document.querySelector("#cronWidget > p[data-name='byDay'] > input[type='radio']");
      expect(radio.checked).toEqual(true);

      const month = document.querySelector("#cronWidget > p[data-name='byDay'] > select[name='month']");
      expect(month.value).toEqual("1");

      const dayNum = document.querySelector("#cronWidget > p[data-name='byDay'] > select[name='dayNum']");
      expect(dayNum.value).toEqual("1");

      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      expect(atHour.value).toEqual("00");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      expect(atMinute.value).toEqual("00");
   });

   it("YEARLY: '0 59 23 8 6 ?' should render jun 8 at 23:59", function(){
      fixture.widget.loadValue("0 59 23 8 6 ?");
      expect(periodSelect().value).toEqual(periods.YEARLY);
      const radio = document.querySelector("#cronWidget > p[data-name='byDay'] > input[type='radio']");
      expect(radio.checked).toEqual(true);

      const month = document.querySelector("#cronWidget > p[data-name='byDay'] > select[name='month']");
      expect(month.value).toEqual("6");

      const dayNum = document.querySelector("#cronWidget > p[data-name='byDay'] > select[name='dayNum']");
      expect(dayNum.value).toEqual("8");

      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      expect(atHour.value).toEqual("23");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      expect(atMinute.value).toEqual("59");
   });

   it("YEARLY: '0 0 0 ? 1 1#1' should render the 1st mon of january at midnight", function() {
      fixture.widget.loadValue("0 0 0 ? 1 1#1");
      expect(periodSelect().value).toEqual(periods.YEARLY);

      const radio = document.querySelector("#cronWidget > p[data-name='byWeek'] > input[type='radio']");
      expect(radio.checked).toEqual(true);

      const ordinal = document.querySelector("#cronWidget > p[data-name='byWeek'] > select[name='ordinal']");
      expect(ordinal.value).toEqual("1");

      const weekDay = document.querySelector("#cronWidget > p[data-name='byWeek'] > select[name='weekDay']");
      expect(weekDay.value).toEqual("1");

      const month = document.querySelector("#cronWidget > p[data-name='byWeek'] > select[name='month']");
      expect(month.value).toEqual("1");

      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      expect(atHour.value).toEqual("00");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      expect(atMinute.value).toEqual("00");
   });

   it("YEARLY: '0 10 15 ? 10 6#2' should render the 2nd sat of October at 15:10", function() {
      fixture.widget.loadValue("0 10 15 ? 10 6#2");
      expect(periodSelect().value).toEqual(periods.YEARLY);

      const radio = document.querySelector("#cronWidget > p[data-name='byWeek'] > input[type='radio']");
      expect(radio.checked).toEqual(true);

      const ordinal = document.querySelector("#cronWidget > p[data-name='byWeek'] > select[name='ordinal']");
      expect(ordinal.value).toEqual("2");

      const weekDay = document.querySelector("#cronWidget > p[data-name='byWeek'] > select[name='weekDay']");
      expect(weekDay.value).toEqual("6");

      const month = document.querySelector("#cronWidget > p[data-name='byWeek'] > select[name='month']");
      expect(month.value).toEqual("10");

      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      expect(atHour.value).toEqual("15");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      expect(atMinute.value).toEqual("10");
   });

});
