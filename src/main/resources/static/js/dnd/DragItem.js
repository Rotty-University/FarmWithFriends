class DragItem extends React.Component {
	constructor(props)
	{
		super(props);
	}
	
	drag = (e) => {
		const itemInfo = [];
		itemInfo.push("true");
		itemInfo.push(this.props.className);
		itemInfo.push(this.props.id);
		itemInfo.push(this.props.type);
		itemInfo.push(this.props.amount);
		
		// save new item info for new slot to update child
		e.dataTransfer.setData("ogItemInfo", itemInfo);
		// save original slot class
		e.dataTransfer.setData("ogSlotClass", this.props.slotClass);
		// save original slot number
		e.dataTransfer.setData("ogSlotNumber", this.props.slotNumber);
		// 
	}
	
	render() {
		return (
				<div id={this.props.id} data-tool-type={this.props.type} onClick={this.props.onClick} 
				className={this.props.className} draggable="true" onDragStart={this.drag}>
					{this.props.children}
				</div>
		);
	}
}