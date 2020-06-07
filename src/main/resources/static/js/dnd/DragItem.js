class DragItem extends React.Component {
	
	drag = (e) => {
		e.dataTransfer.setData('transfer', e.target.id);
	}
	
	noAllowDrop = (e) => {
		e.stopPropation();
	}
	
	render() {
		return (
				<div id={this.props.id} draggable="true" onDragStart={this.drag} onDragOver={this.noAllowDrop}>
				{this.props.children}
				</div>
		);
	}
}

DragItem.propTypes = {
		id: PropTypes.string,
		type: PropTypes.string,
		children: PropTypes.node
}


