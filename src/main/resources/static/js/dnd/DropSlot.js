class DropSlot extends React.Component {
	constructor(props)
	{
		/*
		 * 0: hasItem
		 * 1: itemClassName
		 * 2: itemName
		 * 3: itemType
		 * 4: itemAmount
		 */
		super(props);
		
		this.drop = this.drop.bind(this);
		this.allowDrop = this.allowDrop.bind(this);
		this.handleItemClick = this.handleItemClick.bind(this);
	}
	
	drop = (e) => {
		e.preventDefault();
		
		// new item for this slot
		const newSlotClass = e.dataTransfer.getData("ogSlotClass");
		const newSlotNumber = parseInt(e.dataTransfer.getData("ogSlotNumber"));
		const newItemInfo = e.dataTransfer.getData("ogItemInfo").split(",");
		
		// this slot's current info
		const thisSlotNumber = this.props.id;
		
		// update database if THE OTHER slot is toolSlot
		if (newSlotClass === "toolSlot") {
			if (this.props.itemInfo[0] === "true") {
				// update to current item
				this.updateDatabase(newSlotNumber, 
						this.props.itemInfo[3],
						this.props.itemInfo[2]);
			} else {
				// no current item, update to empty strings
				this.updateDatabase(newSlotNumber, 
						"",
						"");
			}
		}
		
		// update database if this slot is toolSlot
		if (this.props.className === "toolSlot") {
			this.updateDatabase(thisSlotNumber, 
					newItemInfo[3],
					newItemInfo[2]);
		}
		
		this.props.swapItems(newSlotClass, newSlotNumber, newItemInfo,
				this.props.className, thisSlotNumber, this.props.itemInfo);
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
	}
	
	handleItemClick() {
		this.props.handleItemClick(this.props.className, this.props.id);
	}
	
	// for some reason we don't need this lol
	// react is already only re-rendering slots that changed
//	shouldComponentUpdate(nextProps, nextState) {
//    	for (let i=0; i<this.props.itemInfo.length; i++) {
//    		if (nextProps.itemInfo[i] !== this.props.itemInfo[i]) {
//    			return true;
//    		}
//    	}
//    	
//    	return false;
//    }
		
	render() {		
		const itemInfo = this.props.itemInfo;

		if (itemInfo[0] === "true") {
			const item = <img src={"css/images/toolImages/" + itemInfo[3] + "/" + itemInfo[2] + ".png"} height={40} width={40}/>;
			const slotClass = this.props.className;
			const slotNumber = this.props.id
			
			return (
					<div id={this.props.className + this.props.id} className={this.props.className} onDrop={this.drop} onDragOver={this.allowDrop}>
						<DragItem className={itemInfo[1]} id={itemInfo[2]} type={itemInfo[3]} 
						onClick={this.handleItemClick} children={item}
						slotClass={slotClass} slotNumber={slotNumber}> 
						</DragItem>
					</div>
			);
		} else {
			return (
					<div id={this.props.className + this.props.id} className={this.props.className} onDrop={this.drop} onDragOver={this.allowDrop}>
					</div>
			);
		}
	}
}