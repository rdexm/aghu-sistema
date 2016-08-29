package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AghMedicoExterno;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

public class MedicoAtendimentoExternoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -8574056820830522455L;

	private static final String MEDICO_ATENDIMENTO_EXTERNO_CRUD = "medicoAtendimentoExternoCRUD";

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	//campos filtro
	private Integer codigo;
	private String crm;
	private String nome;
	private Long cpf;
	private Map<Object, Object> filtersMap = new HashMap<Object, Object>();
	
	@Inject @Paginator
	private DynamicDataModel<AghMedicoExterno> dataModel;
	
	private AghMedicoExterno selecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void excluir() {
		try {
			cadastrosApoioExamesFacade.removerMedicoAtendimentoExterno(selecionado.getSeq());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_MEDICO_EXTERNO", selecionado.getNome());
	
		} catch (BaseListException ex) {
			apresentarExcecaoNegocio(ex);		
		
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		
		} catch(PersistenceException e) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_EXCLUSAO_MEDICO_EXTERNO");
		}
	}
	
	public String inserir(){
		return MEDICO_ATENDIMENTO_EXTERNO_CRUD;
	}
	
	public String editar(){
		return MEDICO_ATENDIMENTO_EXTERNO_CRUD;
	}
	
	@Override
	public List<AghMedicoExterno> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		prepareFilter();
		
		if(orderProperty == null){
			orderProperty = AghMedicoExterno.Fields.NOME.toString();
			asc = true;
		}
		
		return this.cadastrosApoioExamesFacade.pesquisarMedicoExternoPaginado(filtersMap, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Long recuperarCount() {
		prepareFilter();
		return this.cadastrosApoioExamesFacade.countMedicoAtendimentoExterno(filtersMap);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		dataModel.limparPesquisa();
		this.codigo = null;
		this.crm = null;
		this.nome = null;
		this.cpf = null;
	}
	

	/**
	 * Método interno que prepara os filtros para a pesquisa.
	 */
	private void prepareFilter() {
		this.filtersMap.put(AghMedicoExterno.Fields.SEQ, this.codigo);
		this.filtersMap.put(AghMedicoExterno.Fields.CRM, this.crm);
		this.filtersMap.put(AghMedicoExterno.Fields.NOME, this.nome);
		this.filtersMap.put(AghMedicoExterno.Fields.CPF, this.cpf);
	}
	
	// get's and set's
	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getCrm() {
		return crm;
	}

	public void setCrm(String crm) {
		this.crm = crm;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public DynamicDataModel<AghMedicoExterno> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AghMedicoExterno> dataModel) {
		this.dataModel = dataModel;
	}

	public AghMedicoExterno getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AghMedicoExterno selecionado) {
		this.selecionado = selecionado;
	}
}
