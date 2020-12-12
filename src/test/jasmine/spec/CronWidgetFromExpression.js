describe("cron expression input", function() {

   let fixture;

   beforeEach(function(){
      fixture = createTestFixture();
   });

   afterEach(function() {
      removeFixture(fixture);
   });

   it("'0 * * * * ?' should render every minute", function() {
      fixture.widget.loadValue("0 * * * * ?");
      const period = periodSelect();
      expect(period.value).toEqual(periods.MINUTES);
      const minutes = document.querySelector("#cronWidget > p > select");
      expect(minutes.value).toEqual("1");
   });

   // 0 0/2 * * * ?
   it("'0 0/2 * * * ?' should render every other minute", function() {
      fixture.widget.loadValue("0 0/2 * * * ?");
      const period = periodSelect();
      expect(period.value).toEqual(periods.MINUTES);
      const minutes = document.querySelector("#cronWidget > p > select");
      expect(minutes.value).toEqual("2");
   });

   it("'0 0 * * * ?' should render every hour", function() {
      fixture.widget.loadValue("0 0 * * * ?");
      const period = periodSelect();
      expect(period.value).toEqual(periods.HOURLY);
      const hour = document.querySelector("#cronWidget > p > select[name = 'hour']")
      expect(hour.value).toEqual("1");
   });

   it("'0 8 0/9 * * ?' should render every 9:th hour on the 8:th minute", function() {
      fixture.widget.loadValue("0 8 0/9 * * ?");
      const period = periodSelect();
      expect(period.value).toEqual(periods.HOURLY);
      const hour = document.querySelector("#cronWidget > p > select[name = 'hour']");
      expect(hour.value).toEqual("9");
      const minute = document.querySelector("#cronWidget > p > select[name='minute']");
      expect(minute.value).toEqual("08");
   });

   it("'0 0 0 * * ?' should render daily at midnight", function() {
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

   it("'0 23 12 1/51 * ?' should render every 51 days at 12:23", function() {
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

   it("'0 11 3 ? * 1-5' should render every week day at 03:11", function() {
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

   it("'0 0 0 ? * *' should render weekly at midnight", function() {
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

   it("'0 3 20 ? * 1,4,0' should render weekly on mon,thu,sun at 20:03", function() {
      fixture.widget.loadValue("0 3 20 ? * 1,4,0");
      const period = periodSelect();
      expect(period.value).toEqual(periods.WEEKLY);
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

});
