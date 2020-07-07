class DragItem extends React.Component {
	constructor(props)
	{
		super(props);
		
		this.dragEnd = this.dragEnd.bind(this);
	}
	
	drag = (e) => {
		const itemInfo = [];
		itemInfo.push("true");
		itemInfo.push(this.props.className);
		itemInfo.push(this.props.id);
		itemInfo.push(this.props.type);
		itemInfo.push(this.props.amount);
		
		// save new item info for new slot to update child
		e.dataTransfer.setData("itemInfo", itemInfo);
		
		// save data to check if item was dragged from tool box
		e.dataTransfer.setData("toolSlotNumber", this.props.parentToolSlotNumber);
	}
	
	dragEnd = (e) => {
		console.log("dragEnd")
		const newItemInfo = e.dataTransfer.getData("swapItemInfo");
		console.log("swap info: " + newItemInfo)
		this.props.swapItems(newItemInfo);
	}
	
	render() {
		return (
				<div id={this.props.id} data-tool-type={this.props.type} onClick={this.props.onClick} 
				className={this.props.className} draggable="true" onDragStart={this.drag} 
				onDragEnd={this.dragEnd}>
					{this.props.children}
				</div>
		);
	}
}