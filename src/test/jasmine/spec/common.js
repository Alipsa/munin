function createTestFixture() {
   const testRoot = document.createElement("div");
   document.body.appendChild(testRoot);
   const cronVal = document.createElement('input');
   cronVal.setAttribute("id", "cronVal")
   testRoot.appendChild(cronVal);
   const cronWidget = document.createElement("div");
   cronWidget.setAttribute("id", "cronWidget");
   testRoot.appendChild(cronWidget);
   const widget = new CronWidget(
      cronWidget,
      function(expression) {
         cronVal.value = expression;
      }
   );
   widget.render();
   return {
      testRoot,
      cronVal,
      widget
   }
}

function removeFixture(fixture) {
   document.body.removeChild(fixture.testRoot);
}

function periodSelect() {
   return document.querySelector("#cronWidget > select");
}

function fireChange(elem) {
   elem.dispatchEvent(new Event('change'));
}

function fireClick(elem) {
   elem.dispatchEvent(new Event('click'));
}

function clickRadioButton(button) {
   button.checked = true;
   fireClick(button);
}

function selectValue(selectElem, value) {
   selectElem.value = value;
   fireChange(selectElem);
}