package br.gov.mec.aghu.core.etc;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;

import org.primefaces.component.datatable.DataTable;

@RequestScoped
public class DataTableHolder {
	
	private DataTable dataTable=(DataTable) FacesContext.getCurrentInstance().getApplication().createComponent(DataTable.COMPONENT_TYPE);

	public DataTable getDataTableComponent() {
		return dataTable;
	}

	public void setDataTableComponent(DataTable dataTableComponent) {
		this.dataTable = dataTableComponent;
	}
	
	public void reset(){
		dataTable.reset();
	}

}
