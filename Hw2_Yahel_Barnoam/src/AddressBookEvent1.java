
interface AddressBookEvent1 {

	enum eventType {
		ADD(false), FIRST(true), LAST(true), NEXT(true), PREVIOUS(true), UNDO(false), REDO(false);
		private boolean doEvent;

		eventType(boolean doEvent) {
			this.doEvent = doEvent;
		}

		boolean getDoEvent() {
			return doEvent;
		}

		public void setDoEvent(boolean doEvent) {
			this.doEvent = doEvent;
		}
	}
	
	
}