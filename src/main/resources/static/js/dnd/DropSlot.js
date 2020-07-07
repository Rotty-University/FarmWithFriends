class DropSlot extends React.Component {
	constructor(props)
	{
		super(props);

		// ***REMEMBER: handleClick is in PROPS and will never change
		this.state = {
				/*
				 * 0: hasItem
				 * 1: itemClassName
				 * 2: itemName
				 * 3: itemType
				 * 4: itemAmount
				 */
				itemInfo: this.props.itemInfo
		};
		
		this.drop = this.drop.bind(this);
		this.swapItems = this.swapItems.bind(this);
		this.allowDrop = this.allowDrop.bind(this);
	}
	
	drop = (e) => {
		e.preventDefault();
		
		// new item for this slot
		const newItemInfo = e.dataTransfer.getData("itemInfo").split(",");
		const toolSlotNumber = parseInt(e.dataTransfer.getData("toolSlotNumber"));
		
		if (toolSlotNumber !== -1) {
			// need to update database
			if (this.state.itemInfo[0] === "true") {
				// update to current item
				this.updateDatabase(toolSlotNumber, 
						this.state.itemInfo[3],
						this.state.itemInfo[2]);
			} else {
				// no current item, update to empty strings
				this.updateDatabase(toolSlotNumber, 
						"",
						"");
			}
		}
		
		// update current item
		this.setState({
			itemInfo: newItemInfo
		});
		
		// update database if this slot is toolSlot
		if (this.props.className === "toolSlot") {
			const thisSlotId = this.props.id.substring(this.props.id.length - 1);
			this.updateDatabase(thisSlotId, 
					newItemInfo[3],
					newItemInfo[2]);
		}
	}
	
	allowDrop = (e) => {
		e.preventDefault();
		
		const itemInfo = [];
		itemInfo.push(this.state.itemInfo[0]);
		itemInfo.push(this.state.itemInfo[1]);
		itemInfo.push(this.state.itemInfo[2]);
		itemInfo.push(this.state.itemInfo[3]);
		itemInfo.push(this.state.itemInfo[4]);
		
		// swap current child if there is any
		e.dataTransfer.setData("swapItemInfo", itemInfo);
		// save data to check if this slot is toolSlot
		e.dataTransfer.setData("swapToolSlotNumber", this.props.className==="toolSlot" ? this.props.id.substring(this.props.id.length-1) : -1);

		
		console.log(itemInfo)
	};
	
	updateDatabase = (slotNumber, newType, newItem) => {
		const dict = {
				slotNumber: slotNumber,
				newType: newType,
				newItem: newItem
		}
		
		$.post("/updateShortcutTool", dict);
	}
	
	swapItems = (newItemInfo) => {
		this.setState({
			itemInfo: newItemInfo
		});
	}
		
	render() {		
		const itemInfo = this.state.itemInfo;

		if (itemInfo[0] === "true") {
			const item = <img src={"css/images/toolImages/" + itemInfo[3] + "/" + itemInfo[2] + ".png"} height={40} width={40}/>;
			const parentToolSlotNumber = this.props.className==="toolSlot" ? this.props.id.substring(this.props.id.length-1) : -1;
			
			return (
					<div id={this.props.id} className={this.props.className} onDrop={this.drop} onDragOver={this.allowDrop}>
						<DragItem className={itemInfo[1]} id={itemInfo[2]} type={itemInfo[3]} 
						onClick={this.props.handleItemClick} children={item} swapItems={this.swapItems}
						parentToolSlotNumber={parentToolSlotNumber}> 
						</DragItem>
					</div>
			);
		} else {
			return (
					<div id={this.props.id} className={this.props.className} onDrop={this.drop} onDragOver={this.allowDrop}>
					</div>
			);
		}
	}
}