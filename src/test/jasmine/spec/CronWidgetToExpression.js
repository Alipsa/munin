describe("cron expression output", function() {

   let fixture;

   beforeEach(function(){
      fixture = createTestFixture();
   });

   afterEach(function() {
      removeFixture(fixture);
   });

   it("MINUTES: every minute should result in '0 * * * * ?'", function() {
      selectValue(periodSelect(), periods.MINUTES );
      const minutes = document.querySelector("#cronWidget > p > select");
      selectValue(minutes, "1");
      // 0 * * * * ?
      expect(fixture.cronVal.value).toEqual("0 * * * * ?")
   });

   // 0 0/2 * * * ?
   it("MINUTES: very other minute should result in '0 0/2 * * * ?'", function() {
      selectValue(periodSelect(), periods.MINUTES );
      const minutes = document.querySelector("#cronWidget > p > select");
      selectValue(minutes, "2");
      // 0 * * * * ?
      expect(fixture.cronVal.value).toEqual("0 0/2 * * * ?")
   });

   it ("HOURLY: every hour should result in '0 0 * * * ?'", function() {
      selectValue(periodSelect(), periods.HOURLY);
      const hour = document.querySelector("#cronWidget > p > select[name = 'hour']")
      selectValue(hour, "1");
      expect(fixture.cronVal.value).toEqual("0 0 * * * ?");
   })

   it ("HOURLY: every 9:th hour on the 8:th minute should result in '0 8 0/9 * * ?'", function() {
      selectValue(periodSelect(), periods.HOURLY);
      const hour = document.querySelector("#cronWidget > p > select[name = 'hour']")
      selectValue(hour, "9");
      const minute = document.querySelector("#cronWidget > p > select[name='minute']");
      selectValue(minute, "08");
      expect(fixture.cronVal.value).toEqual("0 8 0/9 * * ?");
   })

   it("DAILY: daily at midnight should result in '0 0 0 * * ?'", function() {
      selectValue(periodSelect(), periods.DAILY);
      const day = document.querySelector("#cronWidget > p > select[name = 'day']");
      selectValue(day, "1");
      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      selectValue(atHour, "00");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      selectValue(atMinute, "00");
      expect(fixture.cronVal.value).toEqual("0 0 0 * * ?");
   });

   it("DAILY: every 51 days at 12:23 should result in '0 23 12 1/51 * ?'", function() {
      selectValue(periodSelect(), periods.DAILY);
      const day = document.querySelector("#cronWidget > p > select[name = 'day']");
      selectValue(day, "51");
      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      selectValue(atHour, "12");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      selectValue(atMinute, "23");
      expect(fixture.cronVal.value).toEqual("0 23 12 1/51 * ?");
   });

   it("DAILY: every week day at 03:11 should result in '0 11 3 ? * 1-5'", function() {
      selectValue(periodSelect(), periods.DAILY);
      const day = document.querySelector("#cronWidget > p[data-name = 'everyWeekDay'] > input[type = 'radio']");
      clickRadioButton(day);
      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      selectValue(atHour, "03");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      selectValue(atMinute, "11");
      expect(fixture.cronVal.value).toEqual("0 11 3 ? * 1-5");
   })

   it("WEEKLY: weekly at midnight should result in '0 0 0 ? * *'", function() {
      selectValue(periodSelect(), periods.WEEKLY);

      function check(name, checkedVal) {
         const weekDay = document.querySelector("#cronWidget > p[data-name=daysOfWeek] > input[name='" + name + "']");
         weekDay.checked = checkedVal;
         fireChange(weekDay);
      }
      check("weeklyMon", false);
      check("weeklyTue", false);
      check("weeklyWed", false);
      check("weeklyThu", false);
      check("weeklyFri", false);
      check("weeklySat", false);
      check("weeklySun", false);
      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      selectValue(atHour, "00");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      selectValue(atMinute, "00");
      expect(fixture.cronVal.value).toEqual("0 0 0 ? * *");
   });

   it("WEEKLY: weekly on mon,thu.sun at 20:30 should result in '0 3 20 ? * 1,4,0'", function() {
      selectValue(periodSelect(), periods.WEEKLY);

      function check(name, checkedVal) {
         const weekDay = document.querySelector("#cronWidget > p[data-name=daysOfWeek] > input[name='" + name + "']");
         weekDay.checked = checkedVal;
         fireChange(weekDay);
      }
      check("weeklyMon", true);
      check("weeklyTue", false);
      check("weeklyWed", false);
      check("weeklyThu", true);
      check("weeklyFri", false);
      check("weeklySat", false);
      check("weeklySun", true);
      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      selectValue(atHour, "20");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      selectValue(atMinute, "03");
      console.log("cronVal = ", fixture.cronVal.value)
      expect(fixture.cronVal.value).toEqual("0 3 20 ? * 1,4,0");
   });

   it("MONTHLY: monthly on day one of every month at midnight should result in '0 0 0 1 * ?'", function() {
      selectValue(periodSelect(), periods.MONTHLY);
      const radio = document.querySelector("#cronWidget > p[data-name='dayOfEveryNthMonth'] > input[type='radio']");
      clickRadioButton(radio);
      const dayNum = document.querySelector("#cronWidget > p[data-name='dayOfEveryNthMonth'] > select[name='dayNum']");
      selectValue(dayNum, "1");

      const monthNum = document.querySelector("#cronWidget > p[data-name='dayOfEveryNthMonth'] > select[name='monthNum']");
      selectValue(monthNum, "1");
      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      selectValue(atHour, "00");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      selectValue(atMinute, "00");
      expect(fixture.cronVal.value).toEqual("0 0 0 1 * ?");
   });

   it("MONTHLY: monthly on day 25 of every 3:rd month at 14:58 should result in '0 58 14 25 1/3 ?'", function() {
      selectValue(periodSelect(), periods.MONTHLY);
      const radio = document.querySelector("#cronWidget > p[data-name='dayOfEveryNthMonth'] > input[type='radio']");
      clickRadioButton(radio);
      const dayNum = document.querySelector("#cronWidget > p[data-name='dayOfEveryNthMonth'] > select[name='dayNum']");
      selectValue(dayNum, "25");

      const monthNum = document.querySelector("#cronWidget > p[data-name='dayOfEveryNthMonth'] > select[name='monthNum']");
      selectValue(monthNum, "3");

      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      selectValue(atHour, "14");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      selectValue(atMinute, "58");
      expect(fixture.cronVal.value).toEqual("0 58 14 25 1/3 ?");
   });

   it("MONTHLY: the first monday of of every 1 months at midnight should result in '0 0 0 ? * 1#1'", function() {
      selectValue(periodSelect(), periods.MONTHLY);
      const radio = document.querySelector("#cronWidget > p[data-name='NthWeekDayOfEveryNthMonth'] > input[type='radio']");
      clickRadioButton(radio);
      const weekOrdinal = document.querySelector("#cronWidget > p[data-name='NthWeekDayOfEveryNthMonth'] > select[name='ordinal']");
      selectValue(weekOrdinal, "1");

      const weekDay = document.querySelector("#cronWidget > p[data-name='NthWeekDayOfEveryNthMonth'] > select[name='weekDay']");
      selectValue(weekDay, "1");

      const monthOrdinal = document.querySelector("#cronWidget > p[data-name='NthWeekDayOfEveryNthMonth'] > select[name='month']");
      selectValue(monthOrdinal, "1");

      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      selectValue(atHour, "00");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      selectValue(atMinute, "00");

      expect(fixture.cronVal.value).toEqual("0 0 0 ? * 1#1");
   });

   it("MONTHLY: the 4th sunday of of every 11 months at 03:15 should result in '0 15 3 ? 1/11 0#4'", function() {
      selectValue(periodSelect(), periods.MONTHLY);
      const radio = document.querySelector("#cronWidget > p[data-name='NthWeekDayOfEveryNthMonth'] > input[type='radio']");
      clickRadioButton(radio);
      const weekOrdinal = document.querySelector("#cronWidget > p[data-name='NthWeekDayOfEveryNthMonth'] > select[name='ordinal']");
      selectValue(weekOrdinal, "4");

      const weekDay = document.querySelector("#cronWidget > p[data-name='NthWeekDayOfEveryNthMonth'] > select[name='weekDay']");
      selectValue(weekDay, "0");

      const monthOrdinal = document.querySelector("#cronWidget > p[data-name='NthWeekDayOfEveryNthMonth'] > select[name='month']");
      selectValue(monthOrdinal, "11");

      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      selectValue(atHour, "03");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      selectValue(atMinute, "15");

      expect(fixture.cronVal.value).toEqual("0 15 3 ? 1/11 0#4");
   });

   it("YEARLY: jan 1 at midnight should result in '0 0 0 1 1 ?'", function(){
      selectValue(periodSelect(), periods.YEARLY);
      const radio = document.querySelector("#cronWidget > p[data-name='byDay'] > input[type='radio']");
      clickRadioButton(radio);

      const month = document.querySelector("#cronWidget > p[data-name='byDay'] > select[name='month']");
      selectValue(month, "1");

      const dayNum = document.querySelector("#cronWidget > p[data-name='byDay'] > select[name='dayNum']");
      selectValue(dayNum, "1");

      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      selectValue(atHour, "00");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      selectValue(atMinute, "00");

      expect(fixture.cronVal.value).toEqual("0 0 0 1 1 ?");
   });

   it("YEARLY: jun 8 at 23:59 should result in '0 59 23 8 6 ?'", function(){
      selectValue(periodSelect(), periods.YEARLY);
      const radio = document.querySelector("#cronWidget > p[data-name='byDay'] > input[type='radio']");
      clickRadioButton(radio);

      const month = document.querySelector("#cronWidget > p[data-name='byDay'] > select[name='month']");
      selectValue(month, "6");

      const dayNum = document.querySelector("#cronWidget > p[data-name='byDay'] > select[name='dayNum']");
      selectValue(dayNum, "8");

      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      selectValue(atHour, "23");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      selectValue(atMinute, "59");

      expect(fixture.cronVal.value).toEqual("0 59 23 8 6 ?");
   });

   it("YEARLY: 1st mon of january at midnight should result in '0 0 0 ? 1 1#1'", function() {
      selectValue(periodSelect(), periods.YEARLY);

      const radio = document.querySelector("#cronWidget > p[data-name='byWeek'] > input[type='radio']");
      clickRadioButton(radio);

      const ordinal = document.querySelector("#cronWidget > p[data-name='byWeek'] > select[name='ordinal']");
      selectValue(ordinal, "1");

      const weekDay = document.querySelector("#cronWidget > p[data-name='byWeek'] > select[name='weekDay']");
      selectValue(weekDay, "1");

      const month = document.querySelector("#cronWidget > p[data-name='byWeek'] > select[name='month']");
      selectValue(month, "1");

      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      selectValue(atHour, "00");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      selectValue(atMinute, "00");

      expect(fixture.cronVal.value).toEqual("0 0 0 ? 1 1#1");
   });

   it("YEARLY: 2nd sat of October at 15:10 should result in '0 10 15 ? 10 6#2'", function() {
      selectValue(periodSelect(), periods.YEARLY);

      const radio = document.querySelector("#cronWidget > p[data-name='byWeek'] > input[type='radio']");
      clickRadioButton(radio);

      const ordinal = document.querySelector("#cronWidget > p[data-name='byWeek'] > select[name='ordinal']");
      selectValue(ordinal, "2");

      const weekDay = document.querySelector("#cronWidget > p[data-name='byWeek'] > select[name='weekDay']");
      selectValue(weekDay, "6");

      const month = document.querySelector("#cronWidget > p[data-name='byWeek'] > select[name='month']");
      selectValue(month, "10");

      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      selectValue(atHour, "15");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      selectValue(atMinute, "10");

      expect(fixture.cronVal.value).toEqual("0 10 15 ? 10 6#2");
   });

});
