/**
 * Classe responsável para paginação dinâmica do componente serverDataTable.
 * 
 * @author Cristiano Quadros
 *
 */
package br.gov.mec.aghu.core.etc;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.column.Column;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.ToggleEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SelectableDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.Visibility;

import br.gov.mec.aghu.core.action.ActionArrayPaginator;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.ConfigurationUtil;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.Element;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;

public class DynamicDataModel<T> extends LazyDataModel<T> implements SelectableDataModel<T>{

	private static final long serialVersionUID = 9021023370420074735L;
	
	private static final Log LOG = LogFactory.getLog(DynamicDataModel.class);
	
	private ActionPaginator paginator;
	private ActionArrayPaginator arrayPaginator;
	private List<T> wrappedData;
	private Boolean pesquisaAtiva=false;
	private String timeQuering;
	private Long count;
	private Boolean userEditPermission=true;
	private Boolean userRemovePermission=true;
	private Integer defaultMaxRow=10;
	private Boolean pageRotate=false;
	private Integer first=0;
	
	@Inject
	private DataTableHolder dataTableHolder;
	
	public DynamicDataModel() {
		super();
		defaultMaxRow = obterQtdRegistrosPagina();
	}
	
	public void salvarQtdRegistrosPagina() {
		String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId().replaceAll("/", "_");
		Map map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		WebUtil.setCookie(viewId, (String) map.get("rows"), Integer.MAX_VALUE);
	}

	private void ocultarColunas() {
		int index = 0;
		String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId().replaceAll("/", "_");
		for(UIColumn coluna : dataTableHolder.getDataTableComponent().getColumns()) {
			Cookie cookie = WebUtil.getCookie(viewId + index);
			if(cookie != null && !Boolean.valueOf(cookie.getValue())) {
				((Column)coluna).setStyleClass("ocultar-coluna");
			}
			index++;
		}
	}

	private Integer obterQtdRegistrosPagina() {
		String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId().replaceAll("/", "_");
		Cookie cookie = WebUtil.getCookie(viewId);
		return cookie != null ? Integer.valueOf(cookie.getValue()) : defaultMaxRow;
	}
	
	public void salvarAlteracaoEstrutura(ToggleEvent e) {
		String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId().replaceAll("/", "_");
		WebUtil.setCookie(viewId + (Integer)e.getData(),String.valueOf(Visibility.VISIBLE.equals(e.getVisibility())), Integer.MAX_VALUE);
	}
	
	public DynamicDataModel(ActionPaginator paginator) {
		super();
		this.paginator = paginator;
	}
	
	public DynamicDataModel(ActionArrayPaginator arrayPaginator) {
		super();
		this.arrayPaginator = arrayPaginator;
	}	
	
	public void reiniciarPaginator(){
		dataTableHolder.reset();
		pesquisaAtiva=true;
	}
	
	
	public void limparPesquisa(){
		pesquisaAtiva=false;
		wrappedData=null;
		count=0L;
		first=0;
	}
	
	
	@Override
	public T getRowData(String rowKey) {
		if (rowKey==null || rowKey.equals("null")){
			return null;
		}
		int pos = -1;
		for(T t : wrappedData) {
			pos++;
			if (t.toString().equals(rowKey)) {
				break;
			}
		}
		
        return pos>=0 ? wrappedData.get(pos) : null;
	}	

	
	@Override
	public Object getRowKey(T bean) {
		int pos = wrappedData.indexOf(bean);
		if (pos==-1){
			return null;
		}		
		return String.valueOf(wrappedData.indexOf(bean));
	}
	
	public void onPageChange(PageEvent event) {
		this.setFirst(((DataTable) event.getSource()).getFirst());
	}	
	
	
	@Override
	public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		
		if (!isErrorPage()){
			Date startTime=new Date();
			
			if (paginator!=null){
				count = paginator.recuperarCount();
				wrappedData = paginator.recuperarListaPaginada(first, pageSize, sortField, sortOrder.equals(SortOrder.ASCENDING));
				
			}else if(arrayPaginator!=null){
				Object[] array = arrayPaginator.recuperarListaPaginada(first, pageSize, sortField, sortOrder.equals(SortOrder.ASCENDING));
				count = (Long) array[0];
				wrappedData = (List<T>)array[1];
			}
			
	        setTimeQuering(DateUtil.calculaDiferencaTempo(startTime, new Date()));
		}
		if (wrappedData==null){
			wrappedData = new ArrayList<>();
		}
		ocultarColunas();
        return wrappedData;
	}

	
	private boolean isErrorPage(){
		if (FacesContext.getCurrentInstance().isValidationFailed()){
			return true;
		}
		List<FacesMessage> messages = FacesContext.getCurrentInstance().getMessageList();
		for (FacesMessage mess : messages){
			if (mess.getSeverity().equals(FacesMessage.SEVERITY_ERROR)){
				return true;
			}
		}
		return false;
	}
	
	
	@Override
	public int getRowCount() {
		if (count!=null){
			return count.intValue();
		}
		return 0;
	}		
	
	
	public void preProcessPDF(Object document) throws IOException, BadElementException, DocumentException {
        Document pdf = (Document) document;
        
        String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
        
        if (pageRotate) {
        	pdf.setPageSize(PageSize.A4.rotate());
        } else {
        	pdf.setPageSize(PageSize.A4);
        }
        pdf.open();
 
        Image image = defineLogoHospitalImagemRelatorio();
        
        Chunk chunk = new Chunk(image, 0, 0);        
        Paragraph cabecalho =  new Paragraph();
        cabecalho.add(chunk);
        cabecalho.add(new Phrase("Exportado pelo AGHU (" + StringUtils.substringAfterLast(viewId, "/")  +") em " + DateUtil.dataToString(new Date(), "dd/MM/yyyy HH:mm")));
        cabecalho.setAlignment(Element.ALIGN_MIDDLE);

        HeaderFooter header =  new HeaderFooter(cabecalho, false);
        header.setBorderWidthTop(0);
        pdf.setHeader(header);
        pdf.open();
    }

	private Image defineLogoHospitalImagemRelatorio() throws BadElementException, MalformedURLException, IOException {
		LOG.info("carregando imagem da configuracao ...");
		return ConfigurationUtil.carregarImagemParaItext("logo-hospital.png");
	}
		
	public Boolean getEmpty(){
		return wrappedData==null || wrappedData.isEmpty();
	}
	
	public ActionPaginator getPaginator() {
		return paginator;
	}

	public void setPaginator(ActionPaginator paginator) {
		this.paginator = paginator;
	}

	public Boolean getPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(Boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Boolean getUserEditPermission() {
		return userEditPermission;
	}

	public void setUserEditPermission(Boolean userEditPermission) {
		this.userEditPermission = userEditPermission;
	}

	public Boolean getUserRemovePermission() {
		return userRemovePermission;
	}

	public void setUserRemovePermission(Boolean userRemovePermission) {
		this.userRemovePermission = userRemovePermission;
	}


	public Integer getDefaultMaxRow() {
		return defaultMaxRow;
	}


	public void setDefaultMaxRow(Integer defaultMaxRow) {
		this.defaultMaxRow = defaultMaxRow;
	}

	public DataTable getDataTableComponent() {
		return dataTableHolder.getDataTableComponent();
	}
	
	public void setDataTableComponent(DataTable dataTableComponent) {
		dataTableHolder.setDataTableComponent(dataTableComponent);
	}


	public ActionArrayPaginator getArrayPaginator() {
		return arrayPaginator;
	}


	public void setArrayPaginator(ActionArrayPaginator arrayPaginator) {
		this.arrayPaginator = arrayPaginator;
	}


	public Boolean getPageRotate() {
		return pageRotate;
	}


	public void setPageRotate(Boolean pageRotate) {
		this.pageRotate = pageRotate;
	}


	public String getTimeQuering() {
		return timeQuering;
	}


	public void setTimeQuering(String timeQuering) {
		this.timeQuering = timeQuering;
	}


	public Integer getFirst() {
		return first;
	}


	public void setFirst(Integer first) {
		this.first = first;
	}	
}