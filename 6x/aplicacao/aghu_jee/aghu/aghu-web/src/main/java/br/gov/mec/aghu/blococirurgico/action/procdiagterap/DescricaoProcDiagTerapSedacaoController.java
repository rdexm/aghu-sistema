package br.gov.mec.aghu.blococirurgico.action.procdiagterap;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoProcDiagTerapVO;
import br.gov.mec.aghu.blococirurgico.vo.ProfDescricaoCirurgicaVO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoAtuacao;
import br.gov.mec.aghu.model.PdtAvalPreSedacao;
import br.gov.mec.aghu.model.PdtDadoDesc;
import br.gov.mec.aghu.model.PdtDescricao;
import br.gov.mec.aghu.model.PdtProf;
import br.gov.mec.aghu.model.PdtViaAereas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.action.RelatorioListarCirurgiasPdtDescProcCirurgiaController;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AvaliacaoPreSedacaoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 *
 * Controller para a aba Sedacao (Aval Pré Sedação).
 * 
 * @author rpanassolo
 *
 */

public class DescricaoProcDiagTerapSedacaoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(DescricaoProcDiagTerapSedacaoController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 928717147110522092L;
	
	
	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;	
	
	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private DescricaoProcDiagTerapController descricaoProcDiagTerapController;

	private DescricaoProcDiagTerapVO descricaoProcDiagTerapVO;

	private RapServidores servidorLogado;
	
	private PdtDadoDesc dadoDesc;
	
	@Inject
	private RelatorioListarCirurgiasPdtDescProcCirurgiaController relatorioListarCirurgiasPdtDescProcCirurgiaController;
	
	
	// Atributos para Equipe Cirurgia 
	private ProfDescricaoCirurgicaVO executorSedacao;
	private Short unfSeq;
	private List<String> listaSiglaConselho;
	private List<PdtProf> listaProf;
	
	
	
	// Atributos para a AvalPreSedação
	private PdtAvalPreSedacao avalPreSedacao;
	private PdtDescricao pdtDescricao;
	private List<PdtViaAereas> viasAereas;
	private RapServidores rapServidores;
	private Integer ddtSeq;
	private String descAvaliacaoClinica;
	private AvaliacaoPreSedacaoVO avaliacaoPreSedacaoVO;
	
	
		
	public void iniciar(DescricaoProcDiagTerapVO descricaoProcDiagTerapVO) {
		
		this.descricaoProcDiagTerapVO = descricaoProcDiagTerapVO;
		try {
			servidorLogado = registroColaboradorFacade.obterServidorPorUsuario(obterLoginUsuarioLogado());
			String strListaSiglaConselho = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LISTA_CONSELHOS_PROF_MBC);
			listaSiglaConselho = Arrays.asList(strListaSiglaConselho.split(","));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 
		
		ddtSeq = descricaoProcDiagTerapVO.getDdtSeq();
		dadoDesc = blocoCirurgicoProcDiagTerapFacade.obterDadoDescPorChavePrimaria(ddtSeq);
		unfSeq = descricaoProcDiagTerapVO.getUnidadeFuncional().getSeq();
		viasAereas = obterViasAereas();
		listaProf = blocoCirurgicoProcDiagTerapFacade.pesquisarProfPorDdtSeq(ddtSeq);
		executorSedacao = null;
		
		
		DominioTipoAtuacao tipoAtuacao = DominioTipoAtuacao.ESE;
		carregarValoresSalvos(blocoCirurgicoProcDiagTerapFacade.pesquisarProfPorDdtSeqETipoAtuacao(ddtSeq, tipoAtuacao));
		populaSedacao();
	}
	
	
	// *************************************************************************************************
	// Vias Aereas

	public List<PdtViaAereas> obterViasAereas() {
		return  blocoCirurgicoProcDiagTerapFacade.obterViasAereas();
	}
	
	/**
	 * Pesquisa para suggestion "Executor Sedação" (Anestesia e Sedação). 
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<ProfDescricaoCirurgicaVO> pesquisarExecutorSedacao(String objPesquisa) {
		return this.returnSGWithCount(blocoCirurgicoFacade.pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEFuncao(0, 100, objPesquisa,unfSeq, DominioFuncaoProfissional.ESE, listaSiglaConselho, DominioSituacao.A), 
				blocoCirurgicoFacade.pesquisarSuggestionProfAtuaUnidCirgConselhoPorServidorUnfSeqEFuncaoCount(objPesquisa,
						unfSeq, DominioFuncaoProfissional.ESE, listaSiglaConselho, DominioSituacao.A));
	}
	
	public void gravarExecutorSedacao() {
		DominioTipoAtuacao tipoAtuacao = DominioTipoAtuacao.ESE;
		executorSedacao.setTipoAtuacao(tipoAtuacao);
		if(verificarEquipeSedacao()){
			try {
				blocoCirurgicoProcDiagTerapFacade.inserirProf(ddtSeq, executorSedacao);
				executorSedacao = null;
				carregarValoresSalvos(blocoCirurgicoProcDiagTerapFacade.pesquisarProfPorDdtSeqETipoAtuacao(ddtSeq, tipoAtuacao));
				//this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_PROFISSIONAL_DESCRICAO_CIRURGICA_INSERIDO_COM_SUCESSO");
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				LOG.error(e.getMessage(), e);
			}
		}
		this.gravarAvalPreSedacao();
	}

	public Boolean validarCamposObrigatorios(){
		if(avalPreSedacao.getViaAereas()==null || avalPreSedacao.getAsa()==null || executorSedacao==null){
			this.apresentarMsgNegocio(Severity.ERROR, 
					"MENSAGEM_CAMPOS_OBRIGATORIOS");
			return true;
		}
		return false;
	}
	
	private Boolean verificarEquipeSedacao(){
		List<ProfDescricaoCirurgicaVO> listaProfAtuaUnidCirgConselhoVO = null;
		listaProf = blocoCirurgicoProcDiagTerapFacade.pesquisarProfPorDdtSeq(ddtSeq);
		for (PdtProf prof : listaProf) {
			//#53595 - responsável pela equipe também pode ser o executor da sedação
			if(!DominioTipoAtuacao.RESP.equals(prof.getTipoAtuacao())) {
				RapServidores servidorProf = prof.getServidorPrf();
				if(prof.getServidorPrf() != null ) {
					listaProfAtuaUnidCirgConselhoVO = pesquisarProfAtuaUnidCirgConselho(servidorProf);
					if (listaProfAtuaUnidCirgConselhoVO != null && listaProfAtuaUnidCirgConselhoVO.size() > 0) {
						ProfDescricaoCirurgicaVO equipe = listaProfAtuaUnidCirgConselhoVO.get(0);
						if(equipe.getNroRegConselho() != null && equipe.getNroRegConselho().equals(executorSedacao.getNroRegConselho())){
							executorSedacao=null;
							this.apresentarMsgNegocio(Severity.ERROR, 
									"MENSAGEM_PROFISSIONAL_MESMA_EQUIPE_CIRURGICA");
							return false;
						}
					}
				}
			}

		} 
		return true;
	}
	
	public Boolean verificarAnestesia() {

		if ((descricaoProcDiagTerapController.getTipoAnestesia() != null)) {
			String tipoSedacao = null;
			try {
				tipoSedacao = parametroFacade
						.buscarValorTexto(AghuParametrosEnum.P_AGHU_TIPO_ANESTESIA_SEDACAO);

			} catch (ApplicationBusinessException e) {

				apresentarExcecaoNegocio(e);
			}
			if (tipoSedacao != null
					&& tipoSedacao
							.equalsIgnoreCase(descricaoProcDiagTerapController
									.getTipoAnestesia().getDescricao())) {
				return true;
			} else {
				return false;
			}
		} else {
			return null;
		}
	}
	
	public void removerExecutorSedacao() {
		removerProfDescricaoPorTipoAtuacao(DominioTipoAtuacao.ESE);
		this.gravarAvalPreSedacao();
	}
	
	private void removerProfDescricaoPorTipoAtuacao(DominioTipoAtuacao tipoAtuacao) {
		List<PdtProf> listaProf = 
				blocoCirurgicoProcDiagTerapFacade.pesquisarProfPorDdtSeqETipoAtuacao(ddtSeq, tipoAtuacao);
		if (!listaProf.isEmpty()) {
			blocoCirurgicoProcDiagTerapFacade.removerProf(listaProf.get(0));
		//	exibirMensagemSucessoExclusao();
			
		}
	}
	
	
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
	
	private List<ProfDescricaoCirurgicaVO> pesquisarProfAtuaUnidCirgConselho(
			RapServidores servidorProf) {
		
		return blocoCirurgicoFacade
				.pesquisarProfAtuaUnidCirgConselhoPorServidorUnfSeq(
						servidorProf.getId().getMatricula(), 
						servidorProf.getId().getVinCodigo(),
						unfSeq, 
						listaSiglaConselho, 
						DominioSituacao.A);
	}	
	
	
	/*private void exibirMensagemSucessoExclusao() {
		this.apresentarMsgNegocio(Severity.INFO, 
				"MENSAGEM_PROFISSIONAL_DESCRICAO_CIRURGICA_EXCLUIDO_COM_SUCESSO");
	}*/	

	public void populaSedacao(){
		this.avaliacaoPreSedacaoVO = new AvaliacaoPreSedacaoVO();
		avalPreSedacao = blocoCirurgicoProcDiagTerapFacade.pesquisarPdtAvalPreSedacaoPorDdtSeq(ddtSeq);
		if(avalPreSedacao == null){
			avalPreSedacao = new PdtAvalPreSedacao();
			avalPreSedacao.setDdtSeq(ddtSeq);
			avalPreSedacao.setIndParticAvalCli(Boolean.TRUE);
		}
		
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
	}
		this.relatorioListarCirurgiasPdtDescProcCirurgiaController.setAvaliacaoPreSedacaoVO(avaliacaoPreSedacaoVO);
}
	
	
	public void limparAvaliacaoClinica(){
		if(avalPreSedacao.getIndParticAvalCli()){
			avalPreSedacao.setAvaliacaoClinica(null);
		}
		this.gravarAvalPreSedacao();
	}
	
	public void validarTempoJejum(){
		if(avalPreSedacao.getTempoJejum()!=null){
			if(!(avalPreSedacao.getTempoJejum()>0)){
				avalPreSedacao.setTempoJejum(null);
				this.apresentarMsgNegocio(Severity.ERROR, 
						"MENSAGEM_TEMPO_JEJUM_MAIOR_ZERO");
			}
		}
		this.gravarAvalPreSedacao();
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
	
	public void gravarViaAerea(){
		if(avalPreSedacao.getViaAereas() == null){
			limparAvalPreSedacao();
		}else{
			gravarAvalPreSedacao();
		}
	}
	
	public void limparAvalPreSedacao(){
		avalPreSedacao.setAsa(null);
		avalPreSedacao.setAvaliacaoClinica(null);
		avalPreSedacao.setComorbidades(null);
		avalPreSedacao.setExameFisico(null);
		avalPreSedacao.setTempoJejum(null);
		executorSedacao = null;
	}
	
	public String gravarAvalPreSedacao(){
		try {
			ajustarQuebras();
			avalPreSedacao.setPdtDescricao(dadoDesc.getPdtDescricao());
			if(avalPreSedacao.getIndParticAvalCli() == null){
				avalPreSedacao.setIndParticAvalCli(Boolean.FALSE);
			}
			blocoCirurgicoProcDiagTerapFacade.persistirPdtAvalPreSedacao(avalPreSedacao);
			
			//this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AVALIACAO_PRE_SEDACAO_INSERIDA_SUCESSO");				
			carregarValoresSalvos(blocoCirurgicoProcDiagTerapFacade.pesquisarProfPorDdtSeqETipoAtuacao(ddtSeq, DominioTipoAtuacao.ESE));
			populaSedacao();
			relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public void atualizarPdtDadoDesc(PdtDadoDesc dadoDesc){
		this.dadoDesc = dadoDesc;
		atualizarPdtDadoDesc();
	}
	
	public void atualizarPdtDadoDesc(){
		try {
			
			blocoCirurgicoProcDiagTerapFacade.atualizarDadoDesc(dadoDesc);
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public DescricaoProcDiagTerapVO getDescricaoProcDiagTerapVO() {
		return descricaoProcDiagTerapVO;
	}


	public void setDescricaoProcDiagTerapVO(
			DescricaoProcDiagTerapVO descricaoProcDiagTerapVO) {
		this.descricaoProcDiagTerapVO = descricaoProcDiagTerapVO;
	}


	public RapServidores getServidorLogado() {
		return servidorLogado;
	}


	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}

	

	public PdtDadoDesc getDadoDesc() {
		return dadoDesc;
	}


	public void setDadoDesc(PdtDadoDesc dadoDesc) {
		this.dadoDesc = dadoDesc;
	}


	public IBlocoCirurgicoProcDiagTerapFacade getBlocoCirurgicoProcDiagTerapFacade() {
		return blocoCirurgicoProcDiagTerapFacade;
	}


	public void setBlocoCirurgicoProcDiagTerapFacade(
			IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade) {
		this.blocoCirurgicoProcDiagTerapFacade = blocoCirurgicoProcDiagTerapFacade;
	}


	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}


	public void setRegistroColaboradorFacade(
			IRegistroColaboradorFacade registroColaboradorFacade) {
		this.registroColaboradorFacade = registroColaboradorFacade;
	}


	public PdtAvalPreSedacao getAvalPreSedacao() {
		return avalPreSedacao;
	}


	public void setAvalPreSedacao(PdtAvalPreSedacao avalPreSedacao) {
		this.avalPreSedacao = avalPreSedacao;
	}


	public PdtDescricao getPdtDescricao() {
		return pdtDescricao;
	}


	public void setPdtDescricao(PdtDescricao pdtDescricao) {
		this.pdtDescricao = pdtDescricao;
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


	public Integer getDdtSeq() {
		return ddtSeq;
	}


	public void setDdtSeq(Integer ddtSeq) {
		this.ddtSeq = ddtSeq;
	}

	public List<PdtProf> getListaProf() {
		return listaProf;
	}


	public void setListaProf(List<PdtProf> listaProf) {
		this.listaProf = listaProf;
	}

	public String getDescAvaliacaoClinica() {
		return descAvaliacaoClinica;
	}

	public void setDescAvaliacaoClinica(String descAvaliacaoClinica) {
		this.descAvaliacaoClinica = descAvaliacaoClinica;
	}


	public AvaliacaoPreSedacaoVO getAvaliacaoPreSedacaoVO() {
		return avaliacaoPreSedacaoVO;
	}

	public void setAvaliacaoPreSedacaoVO(AvaliacaoPreSedacaoVO avaliacaoPreSedacaoVO) {
		this.avaliacaoPreSedacaoVO = avaliacaoPreSedacaoVO;
	}


	public RelatorioListarCirurgiasPdtDescProcCirurgiaController getRelatorioListarCirurgiasPdtDescProcCirurgiaController() {
		return relatorioListarCirurgiasPdtDescProcCirurgiaController;
	}

	public void setRelatorioListarCirurgiasPdtDescProcCirurgiaController(
			RelatorioListarCirurgiasPdtDescProcCirurgiaController relatorioListarCirurgiasPdtDescProcCirurgiaController) {
		this.relatorioListarCirurgiasPdtDescProcCirurgiaController = relatorioListarCirurgiasPdtDescProcCirurgiaController;
	}
	
	
			
}