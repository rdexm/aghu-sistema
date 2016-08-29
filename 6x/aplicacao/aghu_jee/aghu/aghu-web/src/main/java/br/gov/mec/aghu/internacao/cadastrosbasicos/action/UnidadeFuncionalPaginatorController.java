package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações da listagem de Unidades Funcionais
 */


public class UnidadeFuncionalPaginatorController extends ActionController implements ActionPaginator  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1377330504027968673L;
	private final String PAGE_CAD_UNIDADE_FUNCIONAL = "unidadeFuncionalCRUD";
	private final String PAGE_CAD_IMP_UNIDADE_FUNCIONAL = "cadastroImpressoraPadraoUnidadesFuncionaisCRUD";

	private static final Log LOG = LogFactory.getLog(UnidadeFuncionalPaginatorController.class);

	@EJB
	protected IAghuFacade aghuFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@Inject @Paginator
	private DynamicDataModel<AghUnidadesFuncionais> dataModel;	
	

	AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
	
	private Integer codigo;
	
	@Inject
	private UnidadeFuncionalController unidadeFuncionalController;
		
	/**
	 * Atributo referente aos campos de filtro 
	 */
	private Integer codigoPesquisa;
	private String descricaoPesquisa;
	private String sigla;
	private FccCentroCustos centroCustoPesquisa;
	
	private DominioSituacao situacaoPesquisa;
	private String andar;
	private AghAla ala;
	private AghClinicas clinicaPesquisa;
	private AghUnidadesFuncionais unidadeFuncionalPaiPesquisa;
	
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}
	
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}
	
	public AghAla[] getDominioAlas() {
		List<AghAla> alas = aghuFacade.buscarTodasAlas();
		
		return alas.toArray(new AghAla[alas.size()]);
	}
	
	public void limparPesquisa() {
		this.codigoPesquisa = null;
		this.descricaoPesquisa = null;
		this.sigla = null;
		this.clinicaPesquisa = null;
		this.centroCustoPesquisa = null;
		this.unidadeFuncionalPaiPesquisa = null;
		this.situacaoPesquisa = null;		
		this.andar = null;
		this.ala = null;
		this.dataModel.limparPesquisa();
	}
	
	public String novo() {
		unidadeFuncionalController.setUnidadeFuncional(new AghUnidadesFuncionais());
		unidadeFuncionalController.inicializaCaracteristicas();
		return PAGE_CAD_UNIDADE_FUNCIONAL;
	}	

	private static final Enum[] FIELDS_LEFT = { AghUnidadesFuncionais.Fields.ALMOXARIFADO, AghUnidadesFuncionais.Fields.CLINICA, 
			 								    AghUnidadesFuncionais.Fields.CENTRO_CUSTO, AghUnidadesFuncionais.Fields.UNF_SEQ, 
			 								    AghUnidadesFuncionais.Fields.RAP_SERVIDOR_CHEFIA, 
			 								    AghUnidadesFuncionais.Fields.RAP_SERVIDOR_CHEFIA_PESSOA_FISICA};

	private static final Enum[] FIELDS_INNER = { AghUnidadesFuncionais.Fields.TIPOS_UNIDADE_FUNCIONAL};
	
	public String editar() {
		
		unidade = aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(unidade.getSeq(), true, FIELDS_INNER, FIELDS_LEFT);
		
		unidadeFuncionalController.setUnidadeFuncional(unidade);
		
		unidadeFuncionalController.inicializaCaracteristicas();
		return PAGE_CAD_UNIDADE_FUNCIONAL;
	}
	
	public String impressora() {
		return PAGE_CAD_IMP_UNIDADE_FUNCIONAL;
	}
	
	// ### Paginação ###

	@Override
	public Long recuperarCount() {
		
		Long count = this.aghuFacade.pesquisaAghUnidadesFuncionaisCount(
			getCodigoPesquisaShort(),
			this.descricaoPesquisa,
			this.sigla,
			this.clinicaPesquisa,
			this.centroCustoPesquisa,
			this.unidadeFuncionalPaiPesquisa,
			this.situacaoPesquisa,
			this.andar,
			this.ala
		);

		return count;
	}

	@Override
	public List<AghUnidadesFuncionais> recuperarListaPaginada(Integer firstResult,	Integer maxResult, String orderProperty, boolean asc) {

		return aghuFacade.pesquisaAghUnidadesFuncionais(firstResult, maxResult, 
				AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), true,
				getCodigoPesquisaShort(), this.descricaoPesquisa,
				this.sigla, this.clinicaPesquisa,
				this.centroCustoPesquisa, this.unidadeFuncionalPaiPesquisa,
				this.situacaoPesquisa, this.andar,
				this.ala);
	}

	
	/**
	 * Método que realiza a ação do botão excluir na tela de Pesquisa de
	 * Manter Unidades Funcionais.
	 */
	public void excluir() {		
		try {			
			cadastrosBasicosInternacaoFacade.excluirUnidade(unidade.getSeq());
			apresentarMsgNegocio("MENSAGEM_SUCESSO_EXCLUSAO_UNIDADE_FUNCIONAL");			
		} catch (ApplicationBusinessException e) {
			apresentarMsgNegocio(Severity.WARN, "MENSAGEM_ERRO_EXCLUSAO_UNIDADE_FUNCIONAL");
			LOG.error("Erro ao tentar excluir uma unidade fucional:", e);
		}
		
		unidade = null;
		
	}
	
	
	// #20927 - novos filtros de pesquisa 
	public List<AghClinicas> pesquisaClinicaSB(String strPesquisa) {
		return aghuFacade.listarClinicasPorNomeOuCodigo(strPesquisa);
	}
	
	public List<AghUnidadesFuncionais> pesquisaUnidades(String strPesquisa){
		return aghuFacade.pesquisarUnidadesPorCodigoDescricao(strPesquisa, false);
	}

	public AghAla[] getAlaItens() {
		List<AghAla> alas = aghuFacade.buscarTodasAlas();
		
		return alas.toArray(new AghAla[alas.size()]);
	}
	
	// ### GETs e SETs ###

	public Integer getCodigoPesquisa() {
		return codigoPesquisa;
	}
	
	public Short getCodigoPesquisaShort() {
		return this.codigoPesquisa == null ? null : Short.valueOf(this.codigoPesquisa.shortValue());
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

	public AghClinicas getClinicaPesquisa() {
		return clinicaPesquisa;
	}

	public void setClinicaPesquisa(AghClinicas clinicaPesquisa) {
		this.clinicaPesquisa = clinicaPesquisa;
	}

	public FccCentroCustos getCentroCustoPesquisa() {
		return centroCustoPesquisa;
	}

	public void setCentroCustoPesquisa(FccCentroCustos centroCustoPesquisa) {
		this.centroCustoPesquisa = centroCustoPesquisa;
	}

	public AghUnidadesFuncionais getUnidadeFuncionalPaiPesquisa() {
		return unidadeFuncionalPaiPesquisa;
	}

	public void setUnidadeFuncionalPaiPesquisa(
			AghUnidadesFuncionais unidadeFuncionalPaiPesquisa) {
		this.unidadeFuncionalPaiPesquisa = unidadeFuncionalPaiPesquisa;
	}

	public DominioSituacao getSituacaoPesquisa() {
		return situacaoPesquisa;
	}

	public void setSituacaoPesquisa(DominioSituacao situacaoPesquisa) {
		this.situacaoPesquisa = situacaoPesquisa;
	}

	public AghUnidadesFuncionais getUnidade() {
		return unidade;
	}

	public String getAndar() {
		return andar;
	}

	public void setAndar(String andar) {
		this.andar = andar;
	}

	public void setUnidade(AghUnidadesFuncionais unidade) {
		this.unidade = unidade;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public AghAla getAla() {
		return ala;
	}

	public void setAla(AghAla ala) {
		this.ala = ala;
	}
	
	public DynamicDataModel<AghUnidadesFuncionais> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AghUnidadesFuncionais> dataModel) {
		this.dataModel = dataModel;
	}

	public void reiniciarPaginator() {
		dataModel.reiniciarPaginator();		
	}
	
	
}
