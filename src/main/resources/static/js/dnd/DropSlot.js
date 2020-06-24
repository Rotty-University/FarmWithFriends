class DropSlot extends React.Component {
	
	drop = (e) => {
		e.preventDefault();
		// new item for this slot
		const data = e.dataTransfer.getData('transfer');
		// the slot to swap this slot's item to
		const originalSlotID = e.dataTransfer.getData("originalSlot");
		
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
		while (selected.firstChild) {
			document.getElementById(originalSlotID).appendChild(selected.lastChild);
		}
		// append the dragged element to this slot
		selected.appendChild(document.getElementById(data));
	}
	
	allowDrop = (e) => {
		e.preventDefault();
	};
		
	render() {
		return (
				<div id={this.props.id} className={this.props.className} onDrop={this.drop} onDragOver={this.allowDrop}>
				{this.props.children}
				</div>
		);
	}
}