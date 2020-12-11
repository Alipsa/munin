describe("cron expression input", function() {

   let fixture;

   beforeEach(function(){
      fixture = createTestFixture();
   });

   afterEach(function() {
      removeFixture(fixture);
   });

   it("should render every minute", function() {
      fixture.widget.loadValue("0 * * * * ?");
      const period = periodSelect();
      expect(period.value).toEqual("minutes");
      const minutes = document.querySelector("#cronWidget > p > select");
      expect(minutes.value).toEqual("1");
   });

   // 0 0/2 * * * ?
   it("should render every other minute", function() {
      fixture.widget.loadValue("0 0/2 * * * ?");
      const period = periodSelect();
      expect(period.value).toEqual("minutes");
      const minutes = document.querySelector("#cronWidget > p > select");
      expect(minutes.value).toEqual("2");
   });

});
