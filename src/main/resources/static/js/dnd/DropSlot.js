class DropSlot extends React.Component {
	
	drop = (e) => {
		e.preventDefault();
		// new item for this slot
		const data = e.dataTransfer.getData('transfer');
		// the slot to swap this slot's item to
		const originalSlotID = e.dataTransfer.getData("originalSlot");
		
		// check if accepted dragItem was dragged onto this
		if (data == "" || originalSlotID == "") {
			return;
		}

		// always default to parent DropSlot 
		let selected = e.target;
		
		if (selected.nodeName == "IMG") {
			// first selected is the img inside slot inside item
			selected = selected.parentElement.parentElement;
		} else if (selected.className == "toolbaritem") {
			// first selected is the item
			selected = selected.parentElement;
		}
		
		// do nothing if dragging on self
		if (selected.children.length > 0 && selected.children[0].id === data) {
			return;	
		}
		
		// swap current child if there is any
		if (selected.firstChild) {
			if (document.getElementById(originalSlotID).className === "toolSlot") {
				this.updateDatabase(originalSlotID.substring(originalSlotID.length-1), 
						selected.lastChild.getAttribute("data-tool-type"),
						selected.lastChild.id);
			}
			// perform the swap
			document.getElementById(originalSlotID).appendChild(selected.lastChild);
		} else if (document.getElementById(originalSlotID).className === "toolSlot") {
			// if current child is empty and being swapped to a tool slot, then also update database
			this.updateDatabase(originalSlotID.substring(originalSlotID.length-1), "", "");
		}
		
		// append the dragged element to this slot
		const newElement = document.getElementById(data)
		selected.appendChild(newElement);
		
		// update database if this slot is toolSlot
		if (selected.className === "toolSlot") {
			const thisSlotId = selected.id;
			this.updateDatabase(thisSlotId.substring(thisSlotId.length-1), 
					newElement.getAttribute("data-tool-type"),
					newElement.id);
		}
		//TODO: add backend handler for this
	}
	
	allowDrop = (e) => {
		e.preventDefault();
	};
	
	updateDatabase = (slotNumber, newType, newItem) => {
		const dict = {
				slotNumber: slotNumber,
				newType: newType,
				newItem: newItem
		}
		
		$.post("/updateShortcutTool", dict);
		console.log("done")
	}
		
	render() {
		return (
				<div id={this.props.id} className={this.props.className} onDrop={this.drop} onDragOver={this.allowDrop}>
				{this.props.children}
				</div>
		);
	}
}