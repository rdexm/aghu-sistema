package br.gov.mec.aghu.blococirurgico.action;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoCirurgicaVO;
import br.gov.mec.aghu.blococirurgico.vo.ProfDescricaoCirurgicaVO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoAtuacao;
import br.gov.mec.aghu.model.MbcAvalPreSedacao;
import br.gov.mec.aghu.model.MbcAvalPreSedacaoId;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.PdtProf;
import br.gov.mec.aghu.model.PdtViaAereas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.action.RelatorioDescricaoCirurgiaController;
import br.gov.mec.aghu.paciente.prontuarioonline.action.RelatorioListarCirurgiasPdtDescProcCirurgiaController;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AvaliacaoPreSedacaoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class DescricaoCirurgicaAvalPreSedacaoController extends ActionController{
	
	
	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6787684160697144546L;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;	
	
	
	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private DescricaoCirurgicaCirRealizadaController descricaoCirurgicaCirRealizadaController;

	@Inject
	private RelatorioDescricaoCirurgiaController relatorioDescricaoCirurgiaController;
	@Inject
	private RelatorioListarCirurgiasPdtDescProcCirurgiaController relatorioListarCirurgiasPdtDescProcCirurgiaController;
	@Inject
	private DescricaoCirurgicaController descricaoCirurgicaController;
	private DescricaoCirurgicaVO descricaoCirurgicaVO;
	
	private RapServidores servidorLogado;
	
		
	// Atributos para Equipe Cirurgia 
	private ProfDescricaoCirurgicaVO executorSedacao;
	private Short unfSeq;
	private List<String> listaSiglaConselho;
	List<MbcProfAtuaUnidCirgs> listaProf;
	
	
	// Atributos para a AvalPreSedação
	private MbcAvalPreSedacao avalPreSedacao;
	private MbcDescricaoCirurgica mbcDescricaoCirurgica;
	private List<PdtViaAereas> viasAereas;
	private RapServidores rapServidores;
	private MbcAvalPreSedacaoId id;
	private String descAvaliacaoClinica;
	private AvaliacaoPreSedacaoVO avaliacaoPreSedacaoVO;

	
	
	
	public void iniciar(DescricaoCirurgicaVO descricaoCirurgicaVO) {
		
		this.id = new MbcAvalPreSedacaoId(descricaoCirurgicaVO.getDcgCrgSeq(), descricaoCirurgicaVO.getDcgSeqp());
		
		this.avalPreSedacao = blocoCirurgicoFacade.pesquisarMbcAvalPreSedacaoPorDdtSeq(id);
		
		if (avalPreSedacao == null) {
			this.avalPreSedacao = new MbcAvalPreSedacao();			
			avalPreSedacao.setId(id);
			avalPreSedacao.setIndParticAvalCli(Boolean.TRUE);
		}			
		 
		this.descricaoCirurgicaVO = descricaoCirurgicaVO;
		try {
			servidorLogado = registroColaboradorFacade.obterServidorPorUsuario(obterLoginUsuarioLogado());
			String strListaSiglaConselho = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LISTA_CONSELHOS_PROF_MBC);
			listaSiglaConselho = Arrays.asList(strListaSiglaConselho.split(","));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 

		unfSeq = descricaoCirurgicaVO.getUnidadeFuncional().getSeq();
		viasAereas = obterViasAereas();
		
		executorSedacao = blocoCirurgicoFacade.obterProfissionalDescricaoAnestesistaPorDescricaoCirurgica(descricaoCirurgicaVO.getDcgCrgSeq(), descricaoCirurgicaVO.getDcgSeqp());
	
		obterDadosAvalPreSedacao(avalPreSedacao);
	}

	
	// Vias Aereas

	public List<PdtViaAereas> obterViasAereas() {
		return  blocoCirurgicoFacade.obterViasAereasAtivasOrdemDescricao();
				
	}
	
	/**
	 * Pesquisa para suggestion "Executor Sedação" (Anestesia e Sedação). 
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<ProfDescricaoCirurgicaVO> pesquisarExecutorSedacao(String objPesquisa) {
		Integer firstResult=0;
		Integer maxResult=100;
		return this.returnSGWithCount(blocoCirurgicoFacade.pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEFuncao(firstResult,maxResult, objPesquisa,
				unfSeq, DominioFuncaoProfissional.ESE, listaSiglaConselho, DominioSituacao.A), 
				blocoCirurgicoFacade.pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEFuncaoCount(objPesquisa,
						unfSeq, DominioFuncaoProfissional.ESE, listaSiglaConselho, DominioSituacao.A));
	}

	//blocoCirurgicoFacade.pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEFuncao
	
	
	/*private void exibirMensagemSucessoInclusao() {
		this.apresentarMsgNegocio(Severity.INFO, 
				"MENSAGEM_AVALIACAO_PRE_SEDACAO_INSERIDA_SUCESSO");	
	}*/
	
	
	public void carregarValoresSalvos(List<PdtProf> listaProf) {
		List<ProfDescricaoCirurgicaVO> listaProfAtuaUnidCirgConselhoVO = null;
		for (PdtProf prof : listaProf) {
			RapServidores servidorProf = prof.getServidorPrf();
			DominioTipoAtuacao tipoAtuacao = prof.getTipoAtuacao();
			if (DominioTipoAtuacao.ESE.equals(tipoAtuacao)) {
				listaProfAtuaUnidCirgConselhoVO = pesquisarProfAtuaUnidCirgConselho(servidorProf);
				if (!listaProfAtuaUnidCirgConselhoVO.isEmpty()) {
					executorSedacao = listaProfAtuaUnidCirgConselhoVO.get(0);
				}
			}
		}

	}
	
	private List<ProfDescricaoCirurgicaVO> pesquisarProfAtuaUnidCirgConselho(RapServidores servidorProf) {

		return blocoCirurgicoFacade.pesquisarProfAtuaUnidCirgConselhoPorServidorUnfSeq(servidorProf.getId().getMatricula(), servidorProf.getId()
				.getVinCodigo(), unfSeq, listaSiglaConselho, DominioSituacao.A);
	}

	private void populaSedacao(){
		avalPreSedacao = blocoCirurgicoFacade.pesquisarMbcAvalPreSedacaoPorDdtSeq(id);
		if(avalPreSedacao == null){
			avalPreSedacao = new MbcAvalPreSedacao();
			avalPreSedacao.setId(id);
		}
	}
	
	public void validarTempoJejum() {
		if (avalPreSedacao.getTempoJejum() != null && avalPreSedacao.getTempoJejum() <= 0) {
			avalPreSedacao.setTempoJejum(null);
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_TEMPO_JEJUM_MAIOR_ZERO");
		} else {
			this.gravarAvalPreSedacao();
		}
	}
	
	public void ajustarQuebras() {
		if(!StringUtils.isEmpty(avalPreSedacao.getAvaliacaoClinica())) {
			avalPreSedacao.setAvaliacaoClinica(avalPreSedacao.getAvaliacaoClinica().replaceAll("\\r\\n", "\n"));
		}

		if(!StringUtils.isEmpty(avalPreSedacao.getComorbidades())) {
			avalPreSedacao.setComorbidades(avalPreSedacao.getComorbidades().replaceAll("\\r\\n", "\n"));
		}

		if(!StringUtils.isEmpty(avalPreSedacao.getExameFisico())) {
			avalPreSedacao.setExameFisico(avalPreSedacao.getExameFisico().replaceAll("\\r\\n", "\n"));
		}

	}

	public Boolean habilitarAvaliacaoClinica() {

		if (Boolean.TRUE.equals(avalPreSedacao.getIndParticAvalCli())) {
			avalPreSedacao.setAvaliacaoClinica(null);
			return true;
		} else {
			return false;
		}
	}
	
	public Boolean validarCamposObrigatorios() {
		boolean validado = true;

		if (verificarAnestesia() != null && verificarAnestesia()) {
			if (avalPreSedacao == null || avalPreSedacao.getViaAereas() == null) {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_CAMPO_AEREA");
				validado = false;
			}
			if (avalPreSedacao == null || avalPreSedacao.getAsa() == null) {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_CAMPO_ASA");
				validado = false;
			}
			if (executorSedacao == null) {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_CAMPO_SEDACAO");
				validado = false;
			}
		}
		if (habilitarCoMorbidades() != null && habilitarCoMorbidades()) {
			if (avalPreSedacao != null && avalPreSedacao.getComorbidades() == null) {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_CAMPO_CO_MORBIDAES");
				validado = false;
			}
		}
		return validado;
	}

	public Boolean verificarAnestesia() {

		if ((descricaoCirurgicaCirRealizadaController.getTipoAnestesia() != null)) {
			String tipoSedacao = null;
			try {
				tipoSedacao = parametroFacade
						.buscarValorTexto(AghuParametrosEnum.P_AGHU_TIPO_ANESTESIA_SEDACAO);

			} catch (ApplicationBusinessException e) {

				apresentarExcecaoNegocio(e);
			}
			if (tipoSedacao != null
					&& tipoSedacao
							.equalsIgnoreCase(descricaoCirurgicaCirRealizadaController
									.getTipoAnestesia().getDescricao())) {
				return true;
			} else {
				return false;
			}
		} else {
			return null;
		}
	}
	
	public Boolean habilitarCoMorbidades() {

		if (avalPreSedacao != null && avalPreSedacao.getAsa() != null && (avalPreSedacao.getAsa().getCodigo() > Integer.valueOf("1"))) {
			return true;
		} else {
			return false;
		}
	}
	
	public void verificarAltercaoes() {

		if (avalPreSedacao.getViaAereas() != null || avalPreSedacao.getIndParticAvalCli() != null || avalPreSedacao.getAsa() != null
				|| avalPreSedacao.getTempoJejum() != null || avalPreSedacao.getComorbidades() != null || avalPreSedacao.getExameFisico() != null
				|| executorSedacao != null) {
			gravarAvalPreSedacao();
		} 
	}
	

	public void gravarAvalPreSedacao() {

		try {
			if (avalPreSedacao.getViaAereas() != null || avalPreSedacao.getIndParticAvalCli() != null || avalPreSedacao.getAsa() != null
					|| avalPreSedacao.getTempoJejum() != null || avalPreSedacao.getComorbidades() != null || avalPreSedacao.getExameFisico() != null
					|| executorSedacao != null) {
				ajustarQuebras();

				// avalPreSedacao.setMbcDescricaoCirurgica(mbcDescricaoCirurgica);
				if (avalPreSedacao.getIndParticAvalCli() == null) {
					avalPreSedacao.setIndParticAvalCli(Boolean.FALSE);
				}

				blocoCirurgicoFacade.persistirMbcAvalPreSedacao(avalPreSedacao);
				blocoCirurgicoFacade.persistirProfDescricaoExecutorAnestesia(descricaoCirurgicaVO.getDcgCrgSeq(), descricaoCirurgicaVO.getDcgSeqp(), executorSedacao);
			//	exibirMensagemSucessoInclusao();
				populaSedacao();
				relatorioDescricaoCirurgiaController.inicio();
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void obterDadosAvalPreSedacao(MbcAvalPreSedacao avalPreSedacao){
		this.avaliacaoPreSedacaoVO = new AvaliacaoPreSedacaoVO();
		
		if(avalPreSedacao != null){
			
			if(avalPreSedacao.getViaAereas() != null && avalPreSedacao.getViaAereas().getdescricao() != null){
				avaliacaoPreSedacaoVO.setDescViaAereas(avalPreSedacao.getViaAereas().getdescricao());
			}
			if(avalPreSedacao.getAsa() != null){
				avaliacaoPreSedacaoVO.setAsa(avalPreSedacao.getAsa());
			}
			if(avalPreSedacao.getAvaliacaoClinica() != null){
				avaliacaoPreSedacaoVO.setAvaliacaoClinica(avalPreSedacao.getAvaliacaoClinica());
			}
			if(avalPreSedacao.getComorbidades() != null){
				avaliacaoPreSedacaoVO.setComorbidades(avalPreSedacao.getComorbidades());
			}
			if(avalPreSedacao.getExameFisico() != null){
				avaliacaoPreSedacaoVO.setExameFisico(avalPreSedacao.getExameFisico());
			}
		
		if(executorSedacao != null){
			if(executorSedacao.getNome() != null){
					avaliacaoPreSedacaoVO.setNome(executorSedacao.getNome());
				}
			if(executorSedacao.getNroRegConselho() != null){
					avaliacaoPreSedacaoVO.setNroRegConselho(executorSedacao.getNroRegConselho());
				}
			}
			
		if(avalPreSedacao.getIndParticAvalCli()){
				this.setDescAvaliacaoClinica("SEM PARTICULARIDADES");
				avaliacaoPreSedacaoVO.setAvaliacaoClinica(this.descAvaliacaoClinica);
		}
	
		relatorioDescricaoCirurgiaController.setAvaliacaoPreSedacaoVO(avaliacaoPreSedacaoVO);
		relatorioListarCirurgiasPdtDescProcCirurgiaController.setAvaliacaoPreSedacaoVO(avaliacaoPreSedacaoVO);
	}
}
	
	public void obterExecutorSedacao(){
		descricaoCirurgicaController.setExecutorSedacao(this.executorSedacao);
		this.gravarAvalPreSedacao();
	}

	public RapServidores getServidorLogado() {
		return servidorLogado;
	}


	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}


	public void setRegistroColaboradorFacade(
			IRegistroColaboradorFacade registroColaboradorFacade) {
		this.registroColaboradorFacade = registroColaboradorFacade;
	}


	public MbcAvalPreSedacao getAvalPreSedacao() {
		return avalPreSedacao;
	}


	public void setAvalPreSedacao(MbcAvalPreSedacao avalPreSedacao) {
		this.avalPreSedacao = avalPreSedacao;
	}

	public MbcDescricaoCirurgica getMbcDescricaoCirurgica() {
		return mbcDescricaoCirurgica;
	}

	public void setMbcDescricaoCirurgica(MbcDescricaoCirurgica mbcDescricaoCirurgica) {
		this.mbcDescricaoCirurgica = mbcDescricaoCirurgica;
	}

	public RapServidores getRapServidores() {
		return rapServidores;
	}


	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	public List<PdtViaAereas> getViasAereas() {
		return viasAereas;
	}


	public void setViasAereas(List<PdtViaAereas> viasAereas) {
		this.viasAereas = viasAereas;
	}
	
	public DescricaoCirurgicaVO getDescricaoCirurgicaVO() {
		return descricaoCirurgicaVO;
	}

	public void setDescricaoCirurgicaVO(DescricaoCirurgicaVO descricaoCirurgicaVO) {
		this.descricaoCirurgicaVO = descricaoCirurgicaVO;
	}

	public List<String> getListaSiglaConselho() {
		return listaSiglaConselho;
	}


	public void setListaSiglaConselhos(List<String> listaSiglaConselho) {
		this.listaSiglaConselho = listaSiglaConselho;
	}


	public ProfDescricaoCirurgicaVO getExecutorSedacao() {
		return executorSedacao;
	}


	public void setExecutorSedacao(ProfDescricaoCirurgicaVO executorSedacao) {
		this.executorSedacao = executorSedacao;
	}


	public Short getUnfSeq() {
		return unfSeq;
	}


	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}


	public MbcAvalPreSedacaoId getid() {
		return id;
	}


	public void setid(MbcAvalPreSedacaoId id) {
		this.id = id;
	}

	public List<MbcProfAtuaUnidCirgs> getListaProf() {
		return listaProf;
	}


	public void setListaProf(List<MbcProfAtuaUnidCirgs> listaProf) {
		this.listaProf = listaProf;
	}

	public String getDescAvaliacaoClinica() {
		return descAvaliacaoClinica;
	}

	public void setDescAvaliacaoClinica(String descAvaliacaoClinica) {
		this.descAvaliacaoClinica = descAvaliacaoClinica;
	}
	
}
