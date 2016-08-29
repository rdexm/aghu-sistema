package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghInstituicoesHospitalares;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;



public class InstituicaoHospitalarPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 8142768388651008853L;

	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;

	
	private Integer codigoPesquisaInstituicao;

	private String descricaoPesquisaInstituicao;

	private Integer codigoPesquisaCidade;
	
	@Inject @Paginator
	private DynamicDataModel<Perfil> dataModel;	
	
	private final String PAGE_CAD_INST_HOSP = "instituicaoHospitalarCRUD";

	/**
	 * Atributos adicionais para LOVs de pesquisa
	 */

	private AipCidades cidadesPesq = new AipCidades();
	private List<AipCidades> listaCidadesPesq;
	private String descricaoCidadesBuscaLov;
	private AghInstituicoesHospitalares instituicao = new AghInstituicoesHospitalares();

	// ############### Métodos de Busca LOV ####################

	public void buscaCidadeLov() {
		if (codigoPesquisaCidade != null) {
			instituicao.setCddCodigo(cadastrosBasicosPacienteFacade.obterCidadePorCodigo(this.codigoPesquisaCidade));
		} else {
			instituicao = new AghInstituicoesHospitalares();
		}
	}

	public void pesquisaCidadeLov() {
		this.setListaCidadesPesq(cadastrosBasicosPacienteFacade.pesquisarCidades(descricaoCidadesBuscaLov));
	}

	// ################Métodos do Controller ####################
	
	@PostConstruct
	public void init() {
		begin(conversation, true);	
		instituicao = new AghInstituicoesHospitalares();
	}

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		this.codigoPesquisaInstituicao = null;
		this.descricaoPesquisaInstituicao = null;	
		this.instituicao = new AghInstituicoesHospitalares();
		this.dataModel.limparPesquisa();
	}

	@Override
	public Long recuperarCount() {

		return aghuFacade
				.obterCountInstituicaoCodigoDescricao(
						this.codigoPesquisaInstituicao,
						this.descricaoPesquisaInstituicao,
						this.codigoPesquisaCidade);
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AghInstituicoesHospitalares> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return this.aghuFacade
				.pesquisarInstituicaoHospitalar(firstResult, maxResult,
						AghInstituicoesHospitalares.Fields.SEQ.toString(),
						true, this.codigoPesquisaInstituicao,
						this.descricaoPesquisaInstituicao,
						this.codigoPesquisaCidade);

	}

	public void excluir() {
		
		try {
			if (this.instituicao!= null) {				
				this.cadastrosBasicosInternacaoFacade.removerInstituicaoHospitalar(this.instituicao);
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOCAO_INSTITUICAO", instituicao.getNome());
			} else {
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_ERRO_REMOCAO_INSTITUCAO_INVALIDA");
			}

		
			this.dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {			
			apresentarMsgNegocio(Severity.FATAL, "MENSAGEM_ERRO_REMOCAO_INSTITUCAO_INVALIDA");
		}
	} 
	
	public String novo(){		
		this.setInstituicao(null);
		return PAGE_CAD_INST_HOSP;
	}
	public String editar(){		
		return PAGE_CAD_INST_HOSP;
	}

	
	public Integer getCodigoPesquisaInstituicao() {
		return codigoPesquisaInstituicao;
	}

	public void setCodigoPesquisaInstituicao(Integer codigoPesquisaInstituicao) {
		this.codigoPesquisaInstituicao = codigoPesquisaInstituicao;
	}

	public String getDescricaoPesquisaInstituicao() {
		return descricaoPesquisaInstituicao;
	}

	public void setDescricaoPesquisaInstituicao(
			String descricaoPesquisaInstituicao) {
		this.descricaoPesquisaInstituicao = descricaoPesquisaInstituicao;
	}



	public Integer getCodigoPesquisaCidade() {
		return codigoPesquisaCidade;
	}

	public void setCodigoPesquisaCidade(Integer codigoPesquisaCidade) {
		this.codigoPesquisaCidade = codigoPesquisaCidade;
	}

	public void setListaCidadesPesq(List<AipCidades> listaCidadesPesq) {
		this.listaCidadesPesq = listaCidadesPesq;
	}

	public List<AipCidades> getListaCidadesPesq() {
		return listaCidadesPesq;
	}

	public void setCidadesPesq(AipCidades cidadesPesq) {
		this.cidadesPesq = cidadesPesq;
	}

	public AipCidades getCidadesPesq() {
		return cidadesPesq;
	}

	public String getDescricaoCidadesBuscaLov() {
		return descricaoCidadesBuscaLov;
	}

	public void setDescricaoCidadesBuscaLov(String descricaoCidadesBuscaLov) {
		this.descricaoCidadesBuscaLov = descricaoCidadesBuscaLov;
	}

	public AghInstituicoesHospitalares getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(AghInstituicoesHospitalares instituicao) {
		this.instituicao = instituicao;
	}

	public DynamicDataModel<Perfil> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<Perfil> dataModel) {
		this.dataModel = dataModel;
	}

}
