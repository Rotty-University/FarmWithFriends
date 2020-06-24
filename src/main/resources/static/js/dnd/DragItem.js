class DragItem extends React.Component {
	
	drag = (e) => {
		// if img is selected, default to parent DragItem
		const selected = e.target.nodeName == "IMG" ? e.target.parentElement : e.target;
		e.dataTransfer.setData('transfer', selected.id);
		
		// save parent DropSlot for swapping
		e.dataTransfer.setData('originalSlot', selected.parentElement.id);
	}
	
	render() {
		return (
				<div id={this.props.id} data-tool-type={this.props.type} onClick={this.props.onClick} className={this.props.className} draggable="true" onDragStart={this.drag}>
				{this.props.children}
				</div>
		);
	}
}