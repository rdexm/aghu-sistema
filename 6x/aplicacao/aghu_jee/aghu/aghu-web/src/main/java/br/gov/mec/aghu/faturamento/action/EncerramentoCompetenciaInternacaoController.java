package br.gov.mec.aghu.faturamento.action;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.weld.context.http.HttpConversationContext;
import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoCompetencia;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.ArquivoURINomeQtdVO;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class EncerramentoCompetenciaInternacaoController extends ActionController {

	private static final String EXCECAO_CAPTURADA = "Exceção capturada: ";
	
	private static final String ENCERRAMENTO_CONTAS_HOSPITALARES_ERRO = "ENCERRAMENTO_CONTAS_HOSPITALARES_ERRO";

	@PostConstruct
	protected void inicializar() {
		try {
			ajustarTempoSessao();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(EncerramentoCompetenciaInternacaoController.class);

	public enum EncerramentoCompetenciaInternacaoControllerExceptionCode implements BusinessExceptionCode {
		ARQ_ERRO_FISICO, ERRO_AO_ATUALIZAR

	}

	private static final long serialVersionUID = -2238179898437283363L;

	private FatCompetencia competencia;

	private Date dtInicialArqParcial;

	private Date dtFinalArqParcial;

	private Date dtInicialCompetencia;

	private Date dtFinalCompetencia;

	private Long procedimentoSUS;

	private ArquivoURINomeQtdVO arqVo;

	private List<String> cthsEncerradas;

	private List<String> cthsNaoEncerradas;

	private Boolean encerrando = false;

	private Boolean reabrindo = false;
	
	private Boolean encerrandoCompetencia = false;

	private List<Integer> cths;

	private Integer indexCth;

	private List<Integer> cthsReabertas;

	private List<Integer> cthsNaoReabertas;

	private Integer totalValue;

	//private Integer currentValue;

	private Date dataInicioEncerramento;

	//private Integer porcentagemProgressoReabertura;
	//private Integer porcentagemProgresso;
	
	private ICascaFacade cascaFacade = ServiceLocator.getBean(
			ICascaFacade.class, "aghu-casca");

	@Inject
    private HttpConversationContext conversationContext;	

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	private static final String MAGIC_MIME_TYPE_EQ_APPLICATION_ZIP = "application/zip";

	private static final NumberFormat FORMATTER = new DecimalFormat("######0.##");

	private boolean gerouArquivo = false;
	
	private int tempoDecorrido;
	
	public void inicio() {
	 
		this.gerouArquivo = false;

		cths = null;
		cthsEncerradas = cthsNaoEncerradas = null;
		reabrindo = encerrando = false;
		encerrandoCompetencia = Boolean.FALSE;
		cthsReabertas = cthsNaoReabertas = null;
		indexCth = -1;
		//porcentagemProgressoReabertura = -1;
		//porcentagemProgresso = -1;
		
		// Irá obter a última competência do módulo de internação, que estará em
		// situação aberta ou em manutenção
		final List<FatCompetencia> competencias = faturamentoFacade.obterCompetenciasPorModuloESituacoes(DominioModuloCompetencia.INT,
				DominioSituacaoCompetencia.A, DominioSituacaoCompetencia.M);
		
		obterUltimaCompetenciaModuloInternacao();

		if (competencias != null && !competencias.isEmpty()) {
			competencia = competencias.get(0);
		}

		dtInicialArqParcial = null;
		dtFinalArqParcial = null;
		dtInicialCompetencia = null;
		dtFinalCompetencia = null;
		procedimentoSUS = null;
		this.arqVo = null;
	
	}
	
	private void obterUltimaCompetenciaModuloInternacao() {
		// Irá obter a última competência do módulo de internação, que estará em situação aberta ou em manutenção
		competencia = faturamentoFacade.obterUltimaCompetenciaModInternacao();
	}

	/**
	 * Encerramento Automático de Contas
	 * 
	 */
	public void encerrarContas() throws BaseException {
		if (DominioSituacaoCompetencia.A.equals(competencia.getIndSituacao())) {
			if (!encerrando) {
				cths = faturamentoFacade.getContasEncerramentoEmLote();
				dataInicioEncerramento = new Date();
				cthsEncerradas = new ArrayList<String>();
				cthsNaoEncerradas = new ArrayList<String>();
				indexCth = -1;

				if (cths != null && !cths.isEmpty()) { 
					totalValue = cths.size();
					//currentValue = 0;
					encerrando = true;
					//this.porcentagemProgresso = -1;
					//executaSomenteProgressBar = true;
					//faturamentoFacade.enviaEmailInicioEncerramentoCTHs(cths.size(), dataInicioEncerramento);
					//RequestContext.getCurrentInstance().execute("PF('progressEncerramentoWG').start();");
				} else {
					totalValue = null;
					//currentValue = null;
					encerrando = false;
					this.closeDialog("loadModalDialogWG");
					this.closeDialog("gerar_ec_cth_LoadingModalBoxWG");
					//RequestContext.getCurrentInstance().execute("PF('pollEncerrarContasNG').cancel();");
				}
			}
		} else {
			apresentarMsgNegocio(Severity.ERROR, "ENCERRAMENTO_CONTAS_HOSPITALARES_SITUACAO_COMPETENCIA_INVALIDA");
		}
	}

	/*public void setPorcentagemProgresso(Integer porcentagemProgresso) {
		this.porcentagemProgresso = porcentagemProgresso;
	}*/

	private void ajustarTempoSessao() throws ApplicationBusinessException {
		Usuario usuario = cascaFacade.recuperarUsuario(obterLoginUsuarioLogado());

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

		session.setMaxInactiveInterval(usuario.getTempoSessaoMinutos() * 180);
		
		int sessionTime = session.getMaxInactiveInterval()*1000;  //pega tempo de sessão e passa para milisegundos
        if (conversationContext != null) {
            if (conversationContext.getDefaultTimeout() < sessionTime){
                conversationContext.setDefaultTimeout(sessionTime);
            }
        }

	}

	public void getPorcentagemProgresso() {
		//this.closeDialog("waitDialogWG");
		//this.closeDialog("loadDialogWG");		

        if (encerrando) {
			// super.renovarSessao();//

			try {
				conversationContext.setConcurrentAccessTimeout(conversationContext.getConcurrentAccessTimeout()*100);
				// Processou última conta
				if (cths.size() == ++indexCth) {

					encerrando = false;
					cths = null;
					totalValue = null;
					//currentValue = null;
					indexCth = -1;
					//this.porcentagemProgresso = 100;
					concluirEncerramentoConta();
					//return this.porcentagemProgresso;
				// Processa próxima conta
				} else {
					//currentValue = indexCth;

					String nomeMicrocomputador = null;
					try {
						nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
					} catch (UnknownHostException e) {
						LOG.error("Exceção caputada:", e);
					}

					final Date dataFimVinculoServidor = new Date();
					if (faturamentoFacade.encerrarContasHospitalares(cths.get(indexCth), nomeMicrocomputador, dataFimVinculoServidor, null)) {
						apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ENCERRAMENTO_CONTA_EM_LOTE", cths.get(indexCth).toString());
						cthsEncerradas.add("Conta " + cths.get(indexCth).toString() + " encerrada com sucesso.<br />");
					} else {
						apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_ENCERRAMENTO_CONTA_EM_LOTE", cths.get(indexCth).toString());
						cthsNaoEncerradas.add("Conta " + cths.get(indexCth).toString() + " não encerrada por conter erros.<br />");
					}
					
					//this.porcentagemProgresso = (int) (((double) indexCth.longValue() / cths.size()) * 100);
					//RequestContext.getCurrentInstance().execute("PF('progressEncerramentoWG').start();");
					//return this.porcentagemProgresso;
				}

			} catch (BaseException e) {
				LOG.error(EXCECAO_CAPTURADA, e);
				apresentarMsgNegocio(Severity.ERROR, ENCERRAMENTO_CONTAS_HOSPITALARES_ERRO, e);

			} catch (Exception e) {
				LOG.error(EXCECAO_CAPTURADA, e);
				apresentarMsgNegocio(Severity.ERROR, ENCERRAMENTO_CONTAS_HOSPITALARES_ERRO, e);
			}
			RequestContext.getCurrentInstance().execute("atualizarMensagens();");
        }
	}
	
	public void concluirEncerramentoConta(){
		try {
			faturamentoFacade.enviaEmailResultadoEncerramentoCTHs(cthsEncerradas, cthsNaoEncerradas, dataInicioEncerramento);
			this.closeDialog("gerar_ec_cth_LoadingModalBoxWG");
	
			apresentarMsgNegocio(Severity.INFO, "ENCERRAMENTO_CONTAS_HOSPITALARES_SUCESSO");
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CONTAS_ENCERRADAS", cthsEncerradas.size());
			apresentarMsgNegocio(cthsNaoEncerradas.size() == 0 ? Severity.INFO : Severity.ERROR, "MENSAGEM_CONTAS_N_ENCERRADAS",
					cthsNaoEncerradas.size());
			encerrando = false;
			cths = null;
			totalValue = null;
			//currentValue = null;
			indexCth = -1;
			cthsEncerradas = null;
			cthsNaoEncerradas = null;
			//this.porcentagemProgresso = 0;
			//RequestContext.getCurrentInstance().execute("PF('pollEncerrarContasNG').cancel();");
		} catch (BaseException e) {
			LOG.error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR, ENCERRAMENTO_CONTAS_HOSPITALARES_ERRO, e);

		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR, ENCERRAMENTO_CONTAS_HOSPITALARES_ERRO, e);
		}
	}
	
	public void estornarCompetencia(){
		try {
			faturamentoFacade.estornarCompetenciaInternacao(competencia);
			this.inicio();
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_ESTORNO_COMPETENCIA_SUCESSO");
		} catch (BaseException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			apresentarExcecaoNegocio(e);
		}
	}

	public String getLabelProgresso() {
		StringBuffer sb = new StringBuffer();

		if (totalValue != null && (indexCth+1) < totalValue) {
			sb.append(FORMATTER.format(((double) (indexCth+2) / totalValue) * 100)).append("% ");
			sb.append(" (").append(indexCth+2).append('/').append(totalValue).append(") Processando Conta: ").append(cths.get(indexCth+1));
		} 
		
		return sb.toString();
	}

	public void dispararDownloadArquivoSus() throws ApplicationBusinessException {

		if (this.arqVo != null) {
			try {
				this.download(new File(this.arqVo.getUri()), MAGIC_MIME_TYPE_EQ_APPLICATION_ZIP);
				this.gerouArquivo = false;
				inicio();
			} catch (IOException e) {
				LOG.error("Erro disparando download de arquivo", e);
				throw new ApplicationBusinessException(EncerramentoCompetenciaInternacaoControllerExceptionCode.ARQ_ERRO_FISICO);
			}
		}
	}

	/**
	 * Gerar Arquivo Parcial
	 * 
	 * 
	 */
	public void gerarArquivoParcial() {
		

		try {
            conversationContext.setConcurrentAccessTimeout(conversationContext.getConcurrentAccessTimeout()*100);
			arqVo = faturamentoFacade.gerarArquivoFaturamentoParcialSUSNew(competencia, dtInicialArqParcial, dtFinalArqParcial);
			this.gerouArquivo = true;
		} catch (IOException e) {
			// Rever esta exceção
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e,
					e.getLocalizedMessage()));
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		} 

	}

	/**
	 * Encerrar e Abrir Nova Competência
	 * 
	 */
	public void encerrarCompetenciaAtualAbrirNovaCompetencia() {
		FatCompetencia compOld = null;
		String nomeMicrocomputador = null;
		final Date dataFimVinculoServidor = new Date();
		
		encerrandoCompetencia = Boolean.TRUE;
		
		try {
			conversationContext.setConcurrentAccessTimeout(conversationContext.getConcurrentAccessTimeout()*100);

			try {
				compOld = faturamentoFacade.clonarFatCompetencia(competencia);
			} catch (Exception e) {
				LOG.error(EXCECAO_CAPTURADA, e);
				apresentarExcecaoNegocio(new BaseException(EncerramentoCompetenciaInternacaoControllerExceptionCode.ERRO_AO_ATUALIZAR));
			}
			
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}

			this.faturamentoFacade.gerarCompetenciaEmManutencao(competencia, nomeMicrocomputador, dataFimVinculoServidor);

			//obtem a competencia em manutenção
			competencia = faturamentoFacade.obterCompetenciaModuloMesAno(competencia.getId().getModulo(), competencia.getId().getMes(), competencia.getId().getAno());

			try {
				compOld = faturamentoFacade.clonarFatCompetencia(competencia);
			} catch (Exception e) {
				LOG.error(EXCECAO_CAPTURADA, e);
				apresentarExcecaoNegocio(new BaseException(EncerramentoCompetenciaInternacaoControllerExceptionCode.ERRO_AO_ATUALIZAR));
			}
			
			encerrar(competencia, nomeMicrocomputador, dataFimVinculoServidor);

		} catch (ApplicationBusinessException e) {
			competencia = compOld;
			apresentarExcecaoNegocio(e);
			encerrandoCompetencia = Boolean.FALSE;
		} catch (BaseException e) {
			competencia = compOld;
			apresentarExcecaoNegocio(e);
			encerrandoCompetencia = Boolean.FALSE;
		}
	}

	public void encerrar(FatCompetencia competencia, String nomeMicrocomputador, Date dataFimVinculoServidor) throws BaseException {
		if (faturamentoFacade.encerrarCompetenciaAtualEAbreNovaCompetencia(competencia, nomeMicrocomputador, dataFimVinculoServidor)) {
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ENCERRAMENTO_COMPETENCIA_SUCESSO");
			inicio();

		} else {
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ENCERRAMENTO_COMPETENCIA_NAO_ENCONTRADA");
		}
	}	
	
	/**
	 * Reabrir Contas Encerradas
	 * 
	 */
	public void reabrirContasEncerradas() {
		try {

			if (!reabrindo) {
				cths = faturamentoFacade.getContasParaReaberturaEmLote(competencia, dtInicialCompetencia, dtFinalCompetencia,
						procedimentoSUS);

				cthsReabertas = new ArrayList<Integer>();
				cthsNaoReabertas = new ArrayList<Integer>();
				indexCth = -1;

				if (cths != null && !cths.isEmpty()) {
					totalValue = cths.size();
					//currentValue = 0;
					reabrindo = true;
					//porcentagemProgressoReabertura = -1;
					//this.openDialog("reabrir_cths_LoadingModalBoxWG");
					//RequestContext.getCurrentInstance().execute("PF('progressReaberturaWG').start();");
				} else {
					totalValue = null;
					//currentValue = null;
					reabrindo = false;
					// Nenhuma conta encerrada foi selecionada para o
					// período/procedimento informado
					apresentarMsgNegocio(Severity.WARN, "MENSAGEM_ENCERRAMENTO_COMPETENCIA_NAO_SELECIONADA");
					this.closeDialog("loadModalDialogWG");
					RequestContext.getCurrentInstance().execute("PF('reabrir_cths_LoadingModalBoxWG').hide();");
					RequestContext.getCurrentInstance().execute("PF('pollReaberturaContaNG').cancel();");
				}
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/*public void setPorcentagemProgressoReabertura(Integer porcentagemProgressoReabertura) {
		this.porcentagemProgressoReabertura = porcentagemProgressoReabertura;
	}*/

	public void getPorcentagemProgressoReabertura() {
		//this.closeDialog("waitDialogWG");
		//this.closeDialog("loadDialogWG");
		if (reabrindo) {
			try {
				conversationContext.setConcurrentAccessTimeout(conversationContext.getConcurrentAccessTimeout()*100);
				// Processou última conta
				if (cths.size() == ++indexCth) {
					cths = null;
					totalValue = null;
					//currentValue = null;
					reabrindo = false;
					indexCth = -1;
					//porcentagemProgressoReabertura = 100;
					concluirReaberturaContas();
					//return porcentagemProgressoReabertura;

					// Processa próxima conta
				} else {
					//currentValue = indexCth;

					String nomeMicrocomputador = null;
					try {
						nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
					} catch (UnknownHostException e) {
						LOG.error("Exceção caputada:", e);
					}

					final Date dataFimVinculoServidor = new Date();
					if (faturamentoFacade.reabrirContasHospitalares(cths.get(indexCth), nomeMicrocomputador, dataFimVinculoServidor)) {
						cthsReabertas.add(cths.get(indexCth));
						apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REABERTURA_CONTA_EM_LOTE", cths.get(indexCth).toString());
						/*
						 * // 86400 == 24horas faturamentoFacade.commit(86400);
						 * faturamentoFacade.flush();
						 */
					} else {
						apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_REABERTURA_CONTA_EM_LOTE", cths.get(indexCth).toString());
						cthsNaoReabertas.add(cths.get(indexCth));
					}
					
					//porcentagemProgressoReabertura = (int) (((double) indexCth.longValue() / cths.size()) * 100);
					//return porcentagemProgressoReabertura;
				}

			} catch (BaseException e) {
				LOG.error(EXCECAO_CAPTURADA, e);
				apresentarMsgNegocio(Severity.ERROR, ENCERRAMENTO_CONTAS_HOSPITALARES_ERRO, e);
			}
		}

		//return -1L;
	}

	public void concluirReaberturaContas(){
		this.closeDialog("reabrir_cths_LoadingModalBoxWG");
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CONCLUSAO_REABERTURA");
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CONTAS_REABERTAS", cthsReabertas.size());
		apresentarMsgNegocio(cthsNaoReabertas.size() == 0 ? Severity.INFO : Severity.ERROR, "MENSAGEM_CONTAS_N_REABERTAS",
				cthsNaoReabertas.size());
		cthsReabertas = null;
		cthsNaoReabertas = null;
		//this.porcentagemProgressoReabertura = -1;
		reabrindo = false;
		cths = null;
		totalValue = null;
		//currentValue = null;
		indexCth = -1;
		//RequestContext.getCurrentInstance().execute("PF('pollReaberturaContaNG').cancel();");
	}

	public String voltar() {
		return "voltar";
	}
	
	public void incrementa(){
		this.tempoDecorrido++;
	}

	public FatCompetencia getCompetencia() {
		return competencia;
	}

	public void setCompetencia(FatCompetencia competencia) {
		this.competencia = competencia;
	}

	public Date getDtInicialArqParcial() {
		return dtInicialArqParcial;
	}

	public void setDtInicialArqParcial(Date dtInicialArqParcial) {
		this.dtInicialArqParcial = dtInicialArqParcial;
	}

	public Date getDtFinalArqParcial() {
		return dtFinalArqParcial;
	}

	public void setDtFinalArqParcial(Date dtFinalArqParcial) {
		this.dtFinalArqParcial = dtFinalArqParcial;
	}

	public Date getDtInicialCompetencia() {
		return dtInicialCompetencia;
	}

	public void setDtInicialCompetencia(Date dtInicialCompetencia) {
		this.dtInicialCompetencia = dtInicialCompetencia;
	}

	public Date getDtFinalCompetencia() {
		return dtFinalCompetencia;
	}

	public void setDtFinalCompetencia(Date dtFinalCompetencia) {
		this.dtFinalCompetencia = dtFinalCompetencia;
	}

	public Long getProcedimentoSUS() {
		return procedimentoSUS;
	}

	public void setProcedimentoSUS(Long procedimentoSUS) {
		this.procedimentoSUS = procedimentoSUS;
	}

	public ArquivoURINomeQtdVO getArqVo() {
		return arqVo;
	}

	public void setArqVo(ArquivoURINomeQtdVO arqVo) {
		this.arqVo = arqVo;
	}

	public Boolean getEncerrando() {
		return encerrando;
	}

	public void setEncerrando(Boolean encerrando) {
		this.encerrando = encerrando;
	}

	public List<Integer> getCths() {
		return cths;
	}

	public void setCths(List<Integer> cths) {
		this.cths = cths;
	}

	public Integer getIndexCth() {
		return indexCth;
	}

	public void setIndexCth(Integer indexCth) {
		this.indexCth = indexCth;
	}

	public Boolean getReabrindo() {
		return reabrindo;
	}

	public void setReabrindo(Boolean reabrindo) {
		this.reabrindo = reabrindo;
	}

	public Date getDataInicioEncerramento() {
		return dataInicioEncerramento;
	}

	public void setDataInicioEncerramento(Date dataInicioEncerramento) {
		this.dataInicioEncerramento = dataInicioEncerramento;
	}
	
	public Boolean getEncerrandoCompetencia() {
		return encerrandoCompetencia;
	}

	public void setEncerrandoCompetencia(Boolean encerrandoCompetencia) {
		this.encerrandoCompetencia = encerrandoCompetencia;
	}

	public boolean isGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public int getTempoDecorrido() {
		return tempoDecorrido;
	}

	public void setTempoDecorrido(int tempoDecorrido) {
		this.tempoDecorrido = tempoDecorrido;
	}

}