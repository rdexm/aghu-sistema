package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.compras.vo.ItensScoDireitoAutorizacaoTempVO.Operacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class ClinicaPaginatorController extends ActionController implements ActionPaginator {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2820320261096997872L;

	private static final String PAGE_CADASTRAR_CLINICA = "clinicaCRUD";

	private static final String PAGE_PESQUISAR_CLINICA = "clinicaList";

	@EJB
	protected IAghuFacade aghuFacade;
	
	private Operacao operacao;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@Inject @Paginator
	private DynamicDataModel<AghClinicas> dataModel;	

	private AghClinicas clinica;

	/**
	 * Atributo referente ao campo de filtro de código.
	 */
	private Integer codigoPesquisa;

	/**
	 * Atributo referente ao campo de filtro de descrição.
	 */
	private String descricaoPesquisa;
	
	/**
	 * Atributo referente ao campo de filtro de código sus.
	 */
	private Integer codigoSUSPesquisa;
	
	private Boolean desativaCampoCodigo;

	
	public enum ClinicaPaginatorControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SUCESSO_CRIACAO_CLINICA, MENSAGEM_SUCESSO_EDICAO_CLINICA, 
		MENSAGEM_SUCESSO_REMOCAO_CLINICA, CODIGO_CLINICA_JA_EXISTENTE ;
	}
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}	

	public void limparPesquisa() {
		this.codigoPesquisa = null;
		this.descricaoPesquisa = null;
		this.codigoSUSPesquisa = null;
		this.dataModel.limparPesquisa();
	}
	
	public String novo() {
		begin(conversation);
		operacao = Operacao.ADD;
		clinica = new AghClinicas();
		
		return PAGE_CADASTRAR_CLINICA;
	}	
	
	public String editar() {
		begin(conversation);
		operacao = Operacao.EDIT;
		return PAGE_CADASTRAR_CLINICA;
	}
	
	/**
	 * Método que realiza a ação do botão salvar na tela de cadastro de
	 * clínica
	 */
	public String salvar() {
		try {
			boolean novo=clinica.getVersion() == null;
			if (novo) {
				// Tarefa 2063 - deixar todos textos das entidades em caixa alta via toUpperCase()
				transformarTextosCaixaAlta();
				this.clinica.setIndSituacao(DominioSituacao.A);
				this.cadastrosBasicosInternacaoFacade.criarClinica(this.clinica);
				apresentarMsgNegocio(Severity.INFO,ClinicaPaginatorControllerExceptionCode.MENSAGEM_SUCESSO_CRIACAO_CLINICA.toString(),
					this.clinica.getDescricao());
			}else{
				// Tarefa 2063 - deixar todos textos das entidades em caixa alta via toUpperCase()
				transformarTextosCaixaAlta();
				this.cadastrosBasicosInternacaoFacade.atualizarClinica(this.clinica);
				apresentarMsgNegocio(Severity.INFO, ClinicaPaginatorControllerExceptionCode.MENSAGEM_SUCESSO_EDICAO_CLINICA.toString(),
					this.clinica.getDescricao());
			}
			clinica = new AghClinicas();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return PAGE_PESQUISAR_CLINICA;	
	}
		
	
	public String cancelar() {
		return PAGE_PESQUISAR_CLINICA;
	}	
	
	/**
	 * Método que realiza a ação do botão excluir na tela de Pesquisa de
	 * Clínicas.
	 */
	public void excluir() {
		
		try {
			this.cadastrosBasicosInternacaoFacade.removerClinica(clinica.getCodigo());
			apresentarMsgNegocio(Severity.INFO, ClinicaPaginatorControllerExceptionCode.MENSAGEM_SUCESSO_REMOCAO_CLINICA.toString(),clinica.getDescricao());
			
			this.dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void validaCodigo() {
		Integer codigo = this.clinica.getCodigo();
		if (this.cadastrosBasicosInternacaoFacade.codigoClinicaExistente(codigo)) {
			apresentarMsgNegocio(Severity.ERROR, ClinicaPaginatorControllerExceptionCode.CODIGO_CLINICA_JA_EXISTENTE.toString());
		}
	}

	// ### Paginação ###

	@Override
	public Long recuperarCount() {
		return aghuFacade.pesquisaClinicaCount(
			this.codigoPesquisa, this.descricaoPesquisa, this.codigoSUSPesquisa
		);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AghClinicas> recuperarListaPaginada (Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		return aghuFacade.pesquisaClinica(firstResult, maxResults, orderProperty, asc,
				this.codigoPesquisa, this.descricaoPesquisa, this.codigoSUSPesquisa);
	}
	
	private void transformarTextosCaixaAlta() {
		this.clinica.setDescricao(this.clinica.getDescricao()== null ? null : this.clinica.getDescricao().toUpperCase());		
	}

	public boolean isCodigoEditavel () {
		return operacao == Operacao.ADD;
	}
	
	public Integer getCodigoPesquisa() {
		return codigoPesquisa;
	}

	public void setCodigoPesquisa(Integer codigoPesquisa) {
		this.codigoPesquisa = codigoPesquisa;
	}

	public String getDescricaoPesquisa() {
		return descricaoPesquisa;
	}

	public void setDescricaoPesquisa(String descricaoPesquisa) {
		this.descricaoPesquisa = descricaoPesquisa;
	}

	public Integer getCodigoSUSPesquisa() {
		return codigoSUSPesquisa;
	}

	public void setCodigoSUSPesquisa(Integer codigoSUSPesquisa) {
		this.codigoSUSPesquisa = codigoSUSPesquisa;
	}

	public AghClinicas getClinica() {
		return clinica;
	}

	public void setClinica(AghClinicas clinica) {
		this.clinica = clinica;
	}

	public DynamicDataModel<AghClinicas> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AghClinicas> dataModel) {
		this.dataModel = dataModel;
	}
	
	public Boolean getDesativaCampoCodigo() {
		return desativaCampoCodigo;
	}

	public void setDesativaCampoCodigo(Boolean desativaCampoCodigo) {
		this.desativaCampoCodigo = desativaCampoCodigo;
	}
	
}
