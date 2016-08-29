package br.gov.mec.aghu.bancosangue.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AbsValidAmostrasComponentes;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class ManterValidadeAmostraPaginatorController  extends ActionController implements ActionPaginator{

	@Inject @Paginator
	private DynamicDataModel<AbsValidAmostrasComponentes> dataModel;

	private static final long serialVersionUID = 4282877415523802264L;

	private static final String MANTER_COMPONENTES_SANGUINEOS = "prescricaomedica-manterComponentesSanguineos";

	private static final String MANTER_VALIDADE_AMOSTRA = "bancodesangue-manterValidadeAmostra";
	
	private String origem;
	
	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;
	
	private AbsComponenteSanguineo amostraComp;
	private String codComponente; 
	private Integer codigo;
	private Integer idadeIni; 
	private Integer idadeFim;
	private String voltarPara;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	
	public void iniciar(){
	 

	 

		if(codComponente != null){
			amostraComp = getBancoDeSangueFacade().obterComponeteSanguineoPorCodigo(codComponente);
			dataModel.reiniciarPaginator();
		}
	
	}
	
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
		amostraComp = getBancoDeSangueFacade().obterComponeteSanguineoPorCodigo(codComponente);
	}
	
	public void limparPesquisa(){
		dataModel.limparPesquisa();
		codigo 	 = null;
		idadeIni = null; 
		idadeFim = null;
	}
	
	@Override
	public List<AbsValidAmostrasComponentes> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return getBancoDeSangueFacade().obterListaValidadeAmostrasPorCodigo(firstResult, maxResult, orderProperty, asc, codComponente, codigo, idadeIni, idadeFim);
	}

	@Override
	public Long recuperarCount() {
		return getBancoDeSangueFacade().obterValidadeAmostrasPorCodigoCount(codComponente, codigo, idadeIni, idadeFim);
	}
	
	public String inserir(){
		return MANTER_VALIDADE_AMOSTRA;
	}
	
	public String editar(){
		return MANTER_VALIDADE_AMOSTRA;
	}
	
	public String voltar(){
		limparPesquisa();
		
		if(voltarPara != null){
			return voltarPara;
			
		} else {
			return MANTER_COMPONENTES_SANGUINEOS;
		}
	}

	public String getCodComponente() {
		return codComponente;
	}

	public void setCodComponente(String codComponente) {
		this.codComponente = codComponente;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public Integer getIdadeIni() {
		return idadeIni;
	}

	public void setIdadeIni(Integer idadeIni) {
		this.idadeIni = idadeIni;
	}

	public Integer getIdadeFim() {
		return idadeFim;
	}

	public void setIdadeFim(Integer idadeFim) {
		this.idadeFim = idadeFim;
	}
	
	public IBancoDeSangueFacade getBancoDeSangueFacade() {
		return bancoDeSangueFacade;
	}

	public AbsComponenteSanguineo getAmostraComp() {
		return amostraComp;
	}

	public void setAmostraComp(AbsComponenteSanguineo amostraComp) {
		this.amostraComp = amostraComp;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public DynamicDataModel<AbsValidAmostrasComponentes> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<AbsValidAmostrasComponentes> dataModel) {
		this.dataModel = dataModel;
	}


	public String getVoltarPara() {
		return voltarPara;
	}


	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
}