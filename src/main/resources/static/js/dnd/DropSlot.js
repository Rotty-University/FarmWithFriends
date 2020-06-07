class DropSlot extends React.Component {
	
	drop = (e) => {
		e.preventDefault();
		const data = e.dataTransfer.getData('transfer');
		// remove current child if there is any
		while (e.target.firstChild) {
		    e.target.removeChild(e.target.lastChild);
		  }
		// append the dragged element to this slot
		e.target.appendChild(document.getElementById(data));
	}
	
	allowDrop = (e) => {
		e.preventDefault();
	};
		
	render() {
		return (
				<div id={this.props.id} onDrop={this.drop} onDragOver={this.allowDrop}>
				this.props.children
				</div>
		);
	}
}

DropSlot.propTypes = {
		id: PropTypes.string,
		children: PropTypes.node
}
