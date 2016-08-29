package br.gov.mec.aghu.faturamento.action;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatContaSugestaoDesdobr;
import br.gov.mec.aghu.model.FatContaSugestaoDesdobrId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisarSugestoesDesdobramentoPaginatorController extends
		ActionController implements ActionPaginator {

	private static final long serialVersionUID = 8598336501091755132L;

	private static final Log LOG = LogFactory.getLog(PesquisarSugestoesDesdobramentoPaginatorController.class);

	private Log getLog() {
		return LOG;
	}

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	private Date dataHoraSugestao;
	private String origem;
	private String leito;
	private Integer prontuario;
	private DominioSimNao considera;
	private FatContaSugestaoDesdobrId fatContaSugestaoDesdobrId;
	private boolean gerandoSugestao;
	private boolean finalizouSugestao;
	private Long totalValue;
	private Long currentValue;
	private Integer posLCA;
	private List<Integer> cths;
	private boolean stopPoll;
	private List<Integer> contasProcessadas;

	@Inject @Paginator
	private DynamicDataModel<FatContaSugestaoDesdobr> dataModel;

	private static final NumberFormat FORMATTER = new DecimalFormat(
			"######0.##");

	@PostConstruct
	protected void init() {
		begin(conversation);
		contasProcessadas = new ArrayList<Integer>();
	}

	@Override
	public Long recuperarCount() {
		return faturamentoFacade.pesquisarSugestoesDesdobramentoCount(
				dataHoraSugestao, origem, leito, prontuario,
				(considera != null ? DominioSimNao.S.equals(considera) : null),
				new DominioSituacaoConta[] { DominioSituacaoConta.A,
						DominioSituacaoConta.F });
	}

	@Override
	public List<FatContaSugestaoDesdobr> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return faturamentoFacade.pesquisarSugestoesDesdobramento(
				dataHoraSugestao, origem, leito, prontuario,
				(considera != null ? DominioSimNao.S.equals(considera) : null),
				new DominioSituacaoConta[] { DominioSituacaoConta.A,
						DominioSituacaoConta.F }, firstResult, maxResult,
				orderProperty, asc);
	}

	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		dataHoraSugestao = null;
		origem = null;
		leito = null;
		prontuario = null;
		considera = null;
		dataModel.setPesquisaAtiva(false);
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void gerarSugestoesDesdobramento() {
		if(!gerandoSugestao){
			totalValue = this.faturamentoFacade.geracaoSugestoesDesdobramentoCount();
			contasProcessadas = new ArrayList<Integer>();
			if(totalValue != null && totalValue.intValue() > 0){
				currentValue = 0L;
				try {
					cths = this.faturamentoFacade.gerarSugestoesDesdobramento();
				} catch (BaseException e) {
					apresentarExcecaoNegocio(e);
				}
				gerandoSugestao = true;
			}
		}
	}

	public Long getPorcentagemProgresso() {
		Integer zero = 0;
		if (gerandoSugestao){
			if(!finalizouSugestao){
				
				// Já processou as contas...
				if(zero.equals(cths.size())){
					try {
						// Executa sugestao com base na agenda do bloco cirurgico
						faturamentoFacade.geraSugestaoDesdobramentoParaCirAgendada();
						finalizouSugestao = true;
						pesquisar();
						contasProcessadas = new ArrayList<Integer>();
					} catch (BaseException e) {
						finalizouSugestao = gerandoSugestao = false;
						totalValue = null;
						currentValue = -1L;
						apresentarExcecaoNegocio(e);
						return null;
					}
				} else {
					try {
						posLCA = 0;
						List<Integer> contas = new ArrayList<Integer>();
						
						for (int i = 0; i < 25; i++) {
							if(i < cths.size()){
								contas.add(cths.get(i));
								contasProcessadas.add(cths.get(i));
							}	
						}
						cths.removeAll(contas);
						for (Integer item : contas) {
							faturamentoFacade.geraSugestaoDesdobramentoContaHospitalar(item, null);
						}
						currentValue = Long.valueOf(contasProcessadas.size());
						pesquisar();
					} catch (BaseException e) {
						finalizouSugestao = gerandoSugestao = false;
						totalValue = null;
						currentValue = -1L;
						apresentarExcecaoNegocio(e);
						return null;
					}
				}
			}
			
			if (finalizouSugestao) {
				contasProcessadas = new ArrayList<Integer>();
				this.closeDialog("bt_gerar_sug_desdLoadingModalBoxWG");
				this.closeDialog("waitDialogWG");
				//RequestContext.getCurrentInstance().execute("pollDesdobrarContas.stop()");
				RequestContext.getCurrentInstance().execute("bt_gerar_sug_desdLoadingModalBoxWG.hide()");
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GERACAO_SUGESTOES_DESDOBRAMENTO");
				totalValue = null;
				currentValue = -1L;
				finalizouSugestao = gerandoSugestao = false;
				
				
				//RequestContext.getCurrentInstance().execute("");
				return null;
			} else {
				return (long) (((double)currentValue / totalValue) * 100);
			}
		} else {
			return -1L;
		}
	}
	
	public void gerarSugestoes(){
		cths = new ArrayList<Integer>();
		try {
			cths = this.faturamentoFacade.gerarSugestoesDesdobramento();
		} catch (BaseException e1) {
			apresentarExcecaoNegocio(e1);
		}
		
		for (Integer conta : cths) {
			try {
				faturamentoFacade.geraSugestaoDesdobramentoContaHospitalar(conta, null);
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		try {
			faturamentoFacade.geraSugestaoDesdobramentoParaCirAgendada();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		super.closeDialog("modalConfirmacaoGerarSugestoesDesdobramentoWG");
		pesquisar();
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GERACAO_SUGESTOES_DESDOBRAMENTO");
		
	}

	public String getLabelProgresso() {
		
		StringBuilder sb = new StringBuilder();

		if (gerandoSugestao) {
			sb.append(FORMATTER.format(((double) currentValue / totalValue) * 100)).append("% ");
			//sb.append(" (").append(currentValue).append('/').append(totalValue).append(')');
		}

		return sb.toString();
	}

	public void excluir() {
		try {
			if (fatContaSugestaoDesdobrId != null) {
				faturamentoFacade.removerFatContaSugestaoDesdobrPorId(
						fatContaSugestaoDesdobrId, true);
				apresentarMsgNegocio(
						Severity.INFO,
						"MENSAGEM_SUCESSO_REMOCAO_CONTA_SUGESTAO_DESDOBRAMENTO",
						String.valueOf(fatContaSugestaoDesdobrId.getCthSeq()));
				return;
			}
		} catch (Exception e) {
			getLog().error("Execeção capturada: ", e);
		}
		apresentarMsgNegocio(Severity.ERROR,
				"MENSAGEM_ERRO_REMOCAO_CONTA_SUGESTAO_DESDOBRAMENTO_INVALIDA");
	}

	public void considerarDesconsiderarSugestaoDesdobramento() {
		try {
			if (fatContaSugestaoDesdobrId != null) {
				FatContaSugestaoDesdobr contaSugestaoDesdobr = faturamentoFacade
						.obterFatContaSugestaoDesdobrPorChavePrimaria(fatContaSugestaoDesdobrId);
				contaSugestaoDesdobr.setConsidera(!contaSugestaoDesdobr
						.getConsidera());

				if (contaSugestaoDesdobr != null) {
					faturamentoFacade.atualizarFatContaSugestaoDesdobr(
							contaSugestaoDesdobr, true);
					apresentarMsgNegocio(
							Severity.INFO,
							"MENSAGEM_SUCESSO_CONSIDERAR_DESCONSIDERAR_CONTA_SUGESTAO_DESDOBRAMENTO",
							String.valueOf(fatContaSugestaoDesdobrId
									.getCthSeq()), (contaSugestaoDesdobr
									.getConsidera() ? "Considerada"
									: "Desconsiderada"));
					return;
				}
			}

			apresentarMsgNegocio(
					Severity.ERROR,
					"MENSAGEM_ERRO_CONSIDERAR_DESCONSIDERAR_CONTA_SUGESTAO_DESDOBRAMENTO_INVALIDA",
					String.valueOf(fatContaSugestaoDesdobrId.getCthSeq()));
		} catch (BaseException e) {
			getLog().error("Exceção capturada: ", e);
			apresentarExcecaoNegocio(e);
		}
	}

	public String desdobrarContaHospitalar() {
		return "desdobramentoContaHospitalar";
	}

	public Date getDataHoraSugestao() {
		return dataHoraSugestao;
	}

	public void setDataHoraSugestao(Date dataHoraSugestao) {
		this.dataHoraSugestao = dataHoraSugestao;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public String getLeito() {
		return leito;
	}

	public boolean isGerandoSugestao() {
		return gerandoSugestao;
	}

	public void setGerandoSugestao(boolean gerandoSugestao) {
		this.gerandoSugestao = gerandoSugestao;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public DominioSimNao getConsidera() {
		return considera;
	}

	public void setConsidera(DominioSimNao considera) {
		this.considera = considera;
	}

	public FatContaSugestaoDesdobrId getFatContaSugestaoDesdobrId() {
		return fatContaSugestaoDesdobrId;
	}

	public void setFatContaSugestaoDesdobrId(
			FatContaSugestaoDesdobrId fatContaSugestaoDesdobrId) {
		this.fatContaSugestaoDesdobrId = fatContaSugestaoDesdobrId;
	}

	public DynamicDataModel<FatContaSugestaoDesdobr> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatContaSugestaoDesdobr> dataModel) {
		this.dataModel = dataModel;
	}
	
	public void setTotalValue(Long totalValue) {
		this.totalValue = totalValue;
	}

	public Long getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(Long currentValue) {
		this.currentValue = currentValue;
	}

	public boolean isFinalizouSugestao() {
		return finalizouSugestao;
	}

	public void setFinalizouSugestao(boolean finalizouSugestao) {
		this.finalizouSugestao = finalizouSugestao;
	}

	public List<Integer> getCths() {
		return cths;
	}

	public void setCths(List<Integer> cths) {
		this.cths = cths;
	}

	public boolean isStopPoll() {
		return stopPoll;
	}

	public void setStopPoll(boolean stopPoll) {
		this.stopPoll = stopPoll;
	}

	public Integer getPosLCA() {
		return posLCA;
	}

	public void setPosLCA(Integer posLCA) {
		this.posLCA = posLCA;
	}

	public List<Integer> getContasProcessadas() {
		return contasProcessadas;
	}

	public void setContasProcessadas(List<Integer> contasProcessadas) {
		this.contasProcessadas = contasProcessadas;
	}

	
}
