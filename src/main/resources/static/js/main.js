function setBusy(state = true) {
   if (state) {
      document.body.classList.add('busy');
      document.body.classList.remove('not-busy');
   } else {
      document.body.classList.remove('busy');
      document.body.classList.add('not-busy');
   }
}