package org.wechat.dbo.vo.param;

public class InParam extends SQLParam {

	private Object value;
	
	public InParam(int index,Object value){
		this.setIndex(index);
		this.setValue(value);
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
