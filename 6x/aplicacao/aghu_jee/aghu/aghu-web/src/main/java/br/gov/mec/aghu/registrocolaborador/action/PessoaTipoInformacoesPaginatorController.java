package br.gov.mec.aghu.registrocolaborador.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacaoVinculo;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapPessoaTipoInformacoesId;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.RapTipoInformacoes;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.vo.PessoaTipoInformacoesVO;
import br.gov.mec.aghu.vo.RapServidoresVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class PessoaTipoInformacoesPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 3032888093104267373L;

	private static final String CADASTRAR_PESSOA_TIPO_INFORMACOES = "cadastrarPessoaTipoInformacoes";

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	private RapPessoaTipoInformacoesId idExclusao;

	// formulário pesquisa
	private Integer pesCodigo;
	
	private Short tiiSeq;
	private Long seq;

	private Integer pesCodigoExc;
	
	private RapServidores servidorSelecionado;
	
	private Short tiiSeqExc;
	private String situacao;

	// Lista de valores de tipos de informações
	private RapTipoInformacoes rapTipoInformacoes;

	// Lista de valores do servidor.
	private RapServidoresVO servidorVO;
	private Short tipoCBO;

	private RapPessoasFisicas pessoaFisica;
	private RapPessoasFisicas pessoaSuggestion;
	
	private String voltarPara;
	
	@Inject @Paginator
	private DynamicDataModel<PessoaTipoInformacoesVO> dataModel;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar(){
	 

		try {
			if(Boolean.TRUE.equals(dataModel.getPesquisaAtiva())){
				return;
			}
			
			if (servidorSelecionado != null) {
				pesquisarServidor();
				pesquisar();

			} else if(tipoCBO == null){
				final AghParametros paramTipoCBO = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TIPO_INF_CBO);
				tipoCBO = paramTipoCBO.getVlrNumerico().shortValue();
			}
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	
	}
	
	
	public void limpar() {
		this.servidorVO = null;
		this.situacao = null;
		this.pessoaFisica = null;
		this.pessoaSuggestion = null;
		this.dataModel.limparPesquisa();
	}

	public void pesquisar() throws ApplicationBusinessException {
		if (this.servidorVO == null 
				&& this.pessoaSuggestion == null 
					&& this.pessoaFisica == null) {
			apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_INFORMAR_CRITERIO_PESQUISA");
			return;
		} else {
			pesCodigo = this.buscarCodigoPessoa();
		}
			
		if (pesCodigo != null) {
			this.dataModel.reiniciarPaginator();
				
		} else {
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_PESSOA_NAO_ENCONTRADA");
		}
	}
		
	/**
	 * Busca código da pessoa através de vínculo e matricula ou cpf. Com Vinculo
	 * e matricula realiza a busca na RAP_SERVIDORES, com cpf a busca é
	 * realizada em RAP_PESSOAS_FISICAS
	 */
	public Integer buscarCodigoPessoa() {
		Integer codigo = null;

		if (servidorVO != null && servidorVO.getCodigoPessoa() != null) {
			codigo = servidorVO.getCodigoPessoa();

		} else if (pessoaFisica != null) {
			codigo = pessoaFisica.getCodigo();
			
		} else if (pessoaSuggestion != null) {
			codigo = pessoaSuggestion.getCodigo();
			
		} else {
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_ERRO_PREENCHIMENTO_CAMPOS");
		}
		return codigo;
	}


		

	@Override
	public Long recuperarCount() {
		return registroColaboradorFacade.pesquisarPessoaTipoInformacoesCount(pesCodigo);
	}

	@Override
	public List<PessoaTipoInformacoesVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,boolean asc) {
		return registroColaboradorFacade.pesquisarPessoaTipoInformacoes(this.pesCodigo,firstResult, maxResult, orderProperty, asc);
	}
	
	public String iniciarInclusao() {
		this.tiiSeq = null;
		this.rapTipoInformacoes = null;
		return CADASTRAR_PESSOA_TIPO_INFORMACOES;
	}

	public void excluir() {
		try {

			idExclusao = new RapPessoaTipoInformacoesId();
			idExclusao.setPesCodigo(pesCodigoExc);
			idExclusao.setTiiSeq(tiiSeqExc);
			idExclusao.setSeq(seq);
			registroColaboradorFacade.excluirPessoaTipoInformacao(idExclusao);
			this.dataModel.reiniciarPaginator();

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_PESSOA_TIPO_INFORMACAO");

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	

	// Suggestions
	public List<RapServidoresVO> pesquisarServidor(String strPesquisa) {
		try {
			return registroColaboradorFacade.pesquisarServidor(strPesquisa,tipoCBO);
		} catch (BaseException e) {
			return new ArrayList<RapServidoresVO>();
		}
	}
	
	public RapServidoresVO pesquisarServidor() {
		servidorVO = new RapServidoresVO();
		servidorVO.setMatricula(servidorSelecionado.getId().getMatricula());
		servidorVO.setNome(servidorSelecionado.getPessoaFisica().getNome());
		servidorVO.setIndSituacao(servidorSelecionado.getIndSituacao());
		servidorVO.setCodigoPessoa(servidorSelecionado.getPessoaFisica().getCodigo());
		servidorVO.setDtFimVinculo(servidorSelecionado.getDtFimVinculo());
		pessoaFisica = new RapPessoasFisicas();
		pessoaFisica.setNome(servidorSelecionado.getPessoaFisica().getNome());
		pessoaFisica.setCpf(servidorSelecionado.getPessoaFisica().getCpf());
		pessoaSuggestion = new RapPessoasFisicas();
		pessoaSuggestion.setCodigo(servidorSelecionado.getPessoaFisica().getCodigo());
		pessoaSuggestion.setNome(servidorSelecionado.getPessoaFisica().getNome());
		return servidorVO;
	
	}

	public Long pesquisarServidorCount(Object strPesquisa) {
		try {
			return registroColaboradorFacade.pesquisarServidorCount(strPesquisa,tipoCBO);
		} catch (BaseException e) {
			return 0l;
		}
	}

	/**
	 * Busca pessoa fisica pelo código ou pelo nome da pessoa
	 */
	public List<RapPessoasFisicas> buscarSuggestion(String object) {
		return registroColaboradorFacade.pesquisarPessoaFisica(object);
	}
	
	public Long buscarSuggestionCount(String object) {
		return registroColaboradorFacade.pesquisarPessoaFisicaCount(object);
	}
	
	public List<RapPessoasFisicas> suggestionPessoasFisicasPorCPFNome(final String objPesquisa){
		return registroColaboradorFacade.suggestionPessoasFisicasPorCPFNome(objPesquisa);
	}
	
	public Long suggestionPessoasFisicasPorCPFNomeCount(final String objPesquisa){
		return registroColaboradorFacade.suggestionPessoasFisicasPorCPFNomeCount(objPesquisa);
	}

	public void selecionouServidor() {
		final RapServidores servidor = registroColaboradorFacade.obterServidorPessoa(new RapServidores(new RapServidoresId(servidorVO.getMatricula(),servidorVO.getVinculo())));
		pessoaFisica = pessoaSuggestion = servidor.getPessoaFisica();
	}
	
	public void apagouServidor() {
		pessoaFisica = pessoaSuggestion = null;
	}
	
	public void selecionouPessoaFisica() {
		if(servidorVO == null){
			try {
				servidorVO = registroColaboradorFacade.obterServidorVO(pessoaFisica.getCodigo(),
													    null, 
													    null, 
													    tipoCBO);
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		
		pessoaSuggestion = pessoaFisica;
	}
	
	public void apagouPessoaFisica() {
		servidorVO = null;
		pessoaSuggestion =null;
	}
	
	public void selecionouPessoaSuggestion() {
		if(servidorVO == null){
			try {
				servidorVO = registroColaboradorFacade.obterServidorVO(pessoaSuggestion.getCodigo(),
														null, 
														null, 
													    tipoCBO);
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		pessoaFisica = pessoaSuggestion;
	}
	
	public String voltar(){
		limpar();
		return voltarPara;
	}
	
	
	public void apagouPessoaSuggestion() {
		servidorVO = null;
		pessoaFisica = null;
	}

	public RapTipoInformacoes getRapTipoInformacoes() {
		return rapTipoInformacoes;
	}

	public void setRapTipoInformacoes(RapTipoInformacoes rapTipoInformacoes) {
		this.rapTipoInformacoes = rapTipoInformacoes;
	}

	public Integer getPesCodigo() {
		return pesCodigo;
	}

	public void setPesCodigo(Integer pesCodigo) {
		this.pesCodigo = pesCodigo;
	}

	public Short getTiiSeq() {
		return tiiSeq;
	}

	public void setTiiSeq(Short tiiSeq) {
		this.tiiSeq = tiiSeq;
	}

	public RapPessoaTipoInformacoesId getIdExclusao() {
		return idExclusao;
	}

	public void setIdExclusao(RapPessoaTipoInformacoesId idExclusao) {
		this.idExclusao = idExclusao;
	}

	public Integer getPesCodigoExc() {
		return pesCodigoExc;
	}

	public void setPesCodigoExc(Integer pesCodigoExc) {
		this.pesCodigoExc = pesCodigoExc;
	}

	public Short getTiiSeqExc() {
		return tiiSeqExc;
	}

	public void setTiiSeqExc(Short tiiSeqExc) {
		this.tiiSeqExc = tiiSeqExc;
	}

	public String getSituacao() {

		if(servidorVO != null){
			//Verificação de status do servidor
			if (DominioSituacaoVinculo.A.equals(servidorVO.getIndSituacao())) {
				return "Ativo";
				
			} else if (DominioSituacaoVinculo.P.equals(servidorVO.getIndSituacao())
					&& (servidorVO.getDtFimVinculo() != null) 
					&& (servidorVO.getDtFimVinculo().after(Calendar.getInstance().getTime()))) {
				
				return "Ativo";
				
			} else {
				return "Inativo";
			}
		}
		
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public void setPessoaSuggestion(RapPessoasFisicas pessoaSuggestion) {
		this.pessoaSuggestion = pessoaSuggestion;
	}

	public RapPessoasFisicas getPessoaSuggestion() {
		return pessoaSuggestion;
	}

	public void setPessoaFisica(RapPessoasFisicas pessoaFisica) {
		this.pessoaFisica = pessoaFisica;
	}

	public RapPessoasFisicas getPessoaFisica() {
		return pessoaFisica;
	}

	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public RapServidoresVO getServidorVO() {
		return servidorVO;
	}

	public void setServidorVO(RapServidoresVO servidorVO) {
		this.servidorVO = servidorVO;
	}

	public Short getTipoCBO() {
		return tipoCBO;
	}

	public void setTipoCBO(Short tipoCBO) {
		this.tipoCBO = tipoCBO;
	} 


	public DynamicDataModel<PessoaTipoInformacoesVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<PessoaTipoInformacoesVO> dataModel) {
	 this.dataModel = dataModel;
	}


	public RapServidores getServidorSelecionado() {
		return servidorSelecionado;
	}


	public void setServidorSelecionado(RapServidores servidorSelecionado) {
		this.servidorSelecionado = servidorSelecionado;
	}


	public String getVoltarPara() {
		return voltarPara;
	}


	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
}