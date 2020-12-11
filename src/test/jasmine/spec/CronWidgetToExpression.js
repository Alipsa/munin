describe("cron expression output", function() {

   let fixture;

   beforeEach(function(){
      fixture = createTestFixture();
   });

   afterEach(function() {
      removeFixture(fixture);
   });

   it("selecting every minute should result in 0 * * * * ?", function() {
      selectValue(periodSelect(),"minutes" );
      const minutes = document.querySelector("#cronWidget > p > select");
      selectValue(minutes, "1");
      // 0 * * * * ?
      expect(fixture.cronVal.value).toEqual("0 * * * * ?")
   });

   // 0 0/2 * * * ?
   it("selecting very other minute should result in 0 0/2 * * * ?", function() {
      selectValue(periodSelect(),"minutes" );
      const minutes = document.querySelector("#cronWidget > p > select");
      selectValue(minutes, "2");
      // 0 * * * * ?
      expect(fixture.cronVal.value).toEqual("0 0/2 * * * ?")
   });

});
