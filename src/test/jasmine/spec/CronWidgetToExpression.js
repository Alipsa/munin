describe("cron expression output", function() {

   let fixture;

   beforeEach(function(){
      fixture = createTestFixture();
   });

   afterEach(function() {
      removeFixture(fixture);
   });

   it("selecting every minute should result in '0 * * * * ?'", function() {
      selectValue(periodSelect(), periods.MINUTES );
      const minutes = document.querySelector("#cronWidget > p > select");
      selectValue(minutes, "1");
      // 0 * * * * ?
      expect(fixture.cronVal.value).toEqual("0 * * * * ?")
   });

   // 0 0/2 * * * ?
   it("selecting very other minute should result in '0 0/2 * * * ?'", function() {
      selectValue(periodSelect(), periods.MINUTES );
      const minutes = document.querySelector("#cronWidget > p > select");
      selectValue(minutes, "2");
      // 0 * * * * ?
      expect(fixture.cronVal.value).toEqual("0 0/2 * * * ?")
   });

   it ("selecting every hour should result in '0 0 * * * ?'", function() {
      selectValue(periodSelect(), periods.HOURLY);
      const hour = document.querySelector("#cronWidget > p > select[name = 'hour']")
      selectValue(hour, "1");
      expect(fixture.cronVal.value).toEqual("0 0 * * * ?");
   })

   it ("selecting every 9:th hour on the 8:th minute should result in '0 8 0/9 * * ?'", function() {
      selectValue(periodSelect(), periods.HOURLY);
      const hour = document.querySelector("#cronWidget > p > select[name = 'hour']")
      selectValue(hour, "9");
      const minute = document.querySelector("#cronWidget > p > select[name='minute']");
      selectValue(minute, "08");
      expect(fixture.cronVal.value).toEqual("0 8 0/9 * * ?");
   })

   it("selecting daily at midnight should result in '0 0 0 * * ?'", function() {
      selectValue(periodSelect(), periods.DAILY);
      const day = document.querySelector("#cronWidget > p > select[name = 'day']");
      selectValue(day, "1");
      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      selectValue(atHour, "00");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      selectValue(atMinute, "00");
      expect(fixture.cronVal.value).toEqual("0 0 0 * * ?");
   });

   it("selecting every 51 days at 12:23 should result in '0 23 12 1/51 * ?'", function() {
      selectValue(periodSelect(), periods.DAILY);
      const day = document.querySelector("#cronWidget > p > select[name = 'day']");
      selectValue(day, "51");
      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      selectValue(atHour, "12");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      selectValue(atMinute, "23");
      expect(fixture.cronVal.value).toEqual("0 23 12 1/51 * ?");
   });

   it("selecting every week day at 03:11 should result in '0 11 3 ? * 1-5'", function() {
      selectValue(periodSelect(), periods.DAILY);
      const day = document.querySelector("#cronWidget > p[data-name = 'everyWeekDay'] > input[type = 'radio']");
      day.checked = true;
      fireClick(day);
      const atHour = document.querySelector("#cronWidget > p > select[name = 'atHour']");
      selectValue(atHour, "03");
      const atMinute = document.querySelector("#cronWidget > p > select[name = 'atMinute']");
      selectValue(atMinute, "11");
      expect(fixture.cronVal.value).toEqual("0 11 3 ? * 1-5");
   })

   it("selecting weekly at midnight should result in '0 0 0 ? * *'", function() {
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
      console.log("cronVal = ", fixture.cronVal.value)
      expect(fixture.cronVal.value).toEqual("0 0 0 ? * *");
   });

   it("selecting weekly on mon,thu.sun at 20:30 should result in '0 3 20 ? * 1,4,0'", function() {
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

});
