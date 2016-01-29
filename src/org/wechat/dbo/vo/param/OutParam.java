package org.wechat.dbo.vo.param;

public class OutParam extends SQLParam {
	private int dataType;
	
	public OutParam(int index,int dataType){
		this.setIndex(index);
		this.setDataType(dataType);
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
}
