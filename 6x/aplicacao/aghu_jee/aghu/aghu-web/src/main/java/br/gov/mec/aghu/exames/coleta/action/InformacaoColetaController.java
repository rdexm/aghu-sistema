package br.gov.mec.aghu.exames.coleta.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioCumpriuJejumColeta;
import br.gov.mec.aghu.dominio.DominioLocalColetaAmostra;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoAcessoColeta;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.coleta.business.IColetaExamesFacade;
import br.gov.mec.aghu.model.AelInformacaoColeta;
import br.gov.mec.aghu.model.AelInformacaoMdtoColeta;
import br.gov.mec.aghu.model.AelInformacaoMdtoColetaId;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller para a aba Informações da coleta, localizada na tela de registro
 * de informações de amostra e coleta.
 * 
 * @author diego.pacheco
 *
 */

public class InformacaoColetaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(InformacaoColetaController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -217029579054517035L;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IColetaExamesFacade coletaExamesFacade;
	
	private String labelPaciente;
	
	// Informação coleta
	private DominioCumpriuJejumColeta pacienteCumpriuJejum;
	private String motivoJejumNaoRealizado;
	private DominioSimNao pacienteApresentouRgComFoto;
	private DominioTipoAcessoColeta tipoAcessoColeta;
	private Date dtUltimaMenstruacao;
	private Boolean pacienteNaoSoubeInfoMenstruacao;
	private DominioLocalColetaAmostra localColeta;
	private String informacoesAdicionais;
	
	// Informação coleta do medicamento
	private Boolean pacienteNaoSoubeInfoMedicamento;
	private String medicamento;
	private Date dthrIngeriu;
	private Date dthrColetou;
	
	// Parâmetros
	private Short hedGaeUnfSeq;
	private Integer hedGaeSeqp;
	private Date hedDthrAgenda;
	
	private List<AelSolicitacaoExames> listaSolicitacaoExames;
	private AelInformacaoColeta informacaoColeta;
	private List<AelInformacaoMdtoColeta> listaInformacaoMdtoColeta;
	private AelInformacaoMdtoColeta mdtoColetaEdit;
	
	private Integer soeSeqSelecionado;
	private String labelSolicitacao;
	private Boolean origemMenu;
//	private Boolean cameFrom;
	private Boolean salvouInfoColeta;
	
	private void inicializarValoresDefault() {
		inicializarFieldsColeta();
		inicializarFieldsMedicamento();
		this.salvouInfoColeta = false;
	}
	
	private void inicializarFieldsColeta() {
		pacienteCumpriuJejum = DominioCumpriuJejumColeta.S;
		motivoJejumNaoRealizado = null;
		pacienteApresentouRgComFoto = DominioSimNao.S;
		tipoAcessoColeta = DominioTipoAcessoColeta.V;
		dtUltimaMenstruacao = null;
		pacienteNaoSoubeInfoMenstruacao = Boolean.FALSE;
		localColeta = null;
		informacoesAdicionais = null;
	}
	
	private void inicializarFieldsMedicamento() {
		pacienteNaoSoubeInfoMedicamento = Boolean.FALSE;
		medicamento = null;
		Calendar cal = Calendar.getInstance();
		dthrColetou = cal.getTime();
		Calendar calCopia = (Calendar) cal.clone();
		calCopia.add(Calendar.DAY_OF_MONTH, -1);
		dthrIngeriu = calCopia.getTime();
	}
	
	public void iniciar() {
//		if ("pesquisaAgendaExamesHorarios".equals(this.getCameFrom())) {
//			this.origemMenu = false;
//		} else {
//			this.origemMenu = true;
//		}

		inicializarValoresDefault();
		informacaoColeta = new AelInformacaoColeta();
		listaInformacaoMdtoColeta = new ArrayList<AelInformacaoMdtoColeta>();
		if (hedDthrAgenda != null) {
			listaSolicitacaoExames = examesFacade
					.pesquisarSolicitacaoExamePorGaeUnfSeqGaeSeqpDthrAgenda(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda);
			montarLabelPaciente();
			// Carrega informacoes da coleta caso exista
			if (!listaSolicitacaoExames.isEmpty()) {
				carregarColeta();
			}
		}
	}
	
	private void montarLabelSolicitacao() {
		StringBuilder sb = new StringBuilder(40);
		sb.append("</br>")
		
		.append(WebUtil.initLocalizedMessage("LABEL_SOLICITACAO", null, (Object[])null) ).append( ": ")
		.append("<b>").append(soeSeqSelecionado.toString()).append("</b>");
		labelSolicitacao = sb.toString();
	}
	
	private void montarLabelPaciente() {
		StringBuilder sb = new StringBuilder(30);
		sb.append(WebUtil.initLocalizedMessage("LABEL_PACIENTE", null, (Object[])null) ).append( ": ");
		if(listaSolicitacaoExames.get(0).getAtendimento() != null){
			sb.append("<b>").append(listaSolicitacaoExames.get(0).getAtendimento().getPaciente().getNome()).append("</b>");
		}else{
			sb.append("<b>").append(listaSolicitacaoExames.get(0).getAtendimentoDiverso().getAipPaciente().getNome()).append("</b>");
		}
		labelPaciente = sb.toString();
	}
	
	private void carregarColeta() {
		AelSolicitacaoExames solicitacaoExame = listaSolicitacaoExames.get(0);
		AelInformacaoColeta infoColeta = coletaExamesFacade.obterInformacaoColeta(solicitacaoExame.getSeq());
		if (infoColeta != null) {
			this.salvouInfoColeta = true;
			informacaoColeta = infoColeta;
			pacienteCumpriuJejum = informacaoColeta.getCumpriuJejum();
			motivoJejumNaoRealizado = informacaoColeta.getJejumRealizado();
			pacienteApresentouRgComFoto = DominioSimNao.getInstance(informacaoColeta.getDocumento());
			tipoAcessoColeta = informacaoColeta.getTipoAcesso();
			dtUltimaMenstruacao = informacaoColeta.getDtUltMenstruacao();
			pacienteNaoSoubeInfoMenstruacao = informacaoColeta.getInfMenstruacao();
			pacienteNaoSoubeInfoMedicamento = informacaoColeta.getInfMedicacao();
			localColeta = informacaoColeta.getLocalColeta();
			informacoesAdicionais = informacaoColeta.getInformacoesAdicionais();
			for (AelInformacaoMdtoColeta informacaoMdtoColeta : informacaoColeta.getInformacaoMdtoColetaes()) {
				listaInformacaoMdtoColeta.add(informacaoMdtoColeta);
			}
		}
	}
	
	public void carregarInformacoesColetaPorSolicitacao() {
		if (this.origemMenu) {
			AelSolicitacaoExames solicitacaoExame = this.examesFacade.obterAelSolicitacaoExamesPeloId(this.getSoeSeqSelecionado());
			listaSolicitacaoExames = null;
			listaSolicitacaoExames = new ArrayList<AelSolicitacaoExames>();
			listaSolicitacaoExames.add(solicitacaoExame);
			informacaoColeta = coletaExamesFacade.obterInformacaoColeta(solicitacaoExame.getSeq());
			if (informacaoColeta != null) {
				this.salvouInfoColeta = true;
				pacienteCumpriuJejum = informacaoColeta.getCumpriuJejum();
				motivoJejumNaoRealizado = informacaoColeta.getJejumRealizado();
				pacienteApresentouRgComFoto = DominioSimNao.getInstance(informacaoColeta.getDocumento());
				tipoAcessoColeta = informacaoColeta.getTipoAcesso();
				dtUltimaMenstruacao = informacaoColeta.getDtUltMenstruacao();
				pacienteNaoSoubeInfoMenstruacao = informacaoColeta.getInfMenstruacao();
				pacienteNaoSoubeInfoMedicamento = informacaoColeta.getInfMedicacao();
				localColeta = informacaoColeta.getLocalColeta();
				informacoesAdicionais = informacaoColeta.getInformacoesAdicionais();
				for (AelInformacaoMdtoColeta informacaoMdtoColeta : informacaoColeta.getInformacaoMdtoColetaes()) {
					listaInformacaoMdtoColeta.add(informacaoMdtoColeta);
				}
			}	
			montarLabelPaciente();
			montarLabelSolicitacao();
		}
	}
	
	public void limparPaciente() {
		this.labelPaciente = null;
		this.labelSolicitacao = null;
	}
	
	public void carregarInformacoesColeta() {
		if (this.getSoeSeqSelecionado() != null) {
			final AelSolicitacaoExames solicitacaoExame = this.examesFacade.obterAelSolicitacaoExamesPeloId(this.getSoeSeqSelecionado());
			if (solicitacaoExame != null) {
				listaSolicitacaoExames = new ArrayList<AelSolicitacaoExames>(1);
				listaSolicitacaoExames.add(solicitacaoExame);
				informacaoColeta = coletaExamesFacade.obterInformacaoColeta(solicitacaoExame.getSeq());
				if (informacaoColeta != null) {
					this.salvouInfoColeta = true;
					
					pacienteCumpriuJejum = informacaoColeta.getCumpriuJejum();
					motivoJejumNaoRealizado = informacaoColeta.getJejumRealizado();
					pacienteApresentouRgComFoto = DominioSimNao.getInstance(informacaoColeta.getDocumento());
					tipoAcessoColeta = informacaoColeta.getTipoAcesso();
					dtUltimaMenstruacao = informacaoColeta.getDtUltMenstruacao();
					pacienteNaoSoubeInfoMenstruacao = informacaoColeta.getInfMenstruacao();
					pacienteNaoSoubeInfoMedicamento = informacaoColeta.getInfMedicacao();
					if (pacienteNaoSoubeInfoMedicamento) {
						dthrIngeriu = null;
					}
					localColeta = informacaoColeta.getLocalColeta();
					informacoesAdicionais = informacaoColeta.getInformacoesAdicionais();
					if (informacaoColeta.getInformacaoMdtoColetaes() == null || informacaoColeta.getInformacaoMdtoColetaes().isEmpty()) {
						listaInformacaoMdtoColeta = new ArrayList<AelInformacaoMdtoColeta>(0);
					} else {
						listaInformacaoMdtoColeta = coletaExamesFacade.buscarAelInformacaoMdtoColetaByAelInformacaoColeta(informacaoColeta.getId().getSeqp(),
								informacaoColeta.getId().getSoeSeq());
						// listaInformacaoMdtoColeta = new
						// ArrayList<AelInformacaoMdtoColeta>(informacaoColeta.getInformacaoMdtoColetaes().size());
						//
						// for (AelInformacaoMdtoColeta informacaoMdtoColeta :
						// informacaoColeta.getInformacaoMdtoColetaes()) {
						// coletaExamesFacade.refresh(informacaoMdtoColeta);
						// informacaoMdtoColeta.setEmEdicao(false);
						// listaInformacaoMdtoColeta.add(informacaoMdtoColeta);
						// }
					}
				} else {
					inicializarValoresDefault();
					informacaoColeta = new AelInformacaoColeta();
					listaInformacaoMdtoColeta = new ArrayList<AelInformacaoMdtoColeta>();
				}
				montarLabelPaciente();
				montarLabelSolicitacao();
			}
		}
	}
	
	public void alterarInformacaoMdtoColeta() {
		Boolean possuiInformacaoMdtoColetaEmEdicao = getPossuiInformacaoMdtoColetaEmEdicao();

		if (StringUtils.isEmpty(medicamento) || dthrColetou == null) {
			this.apresentarMsgNegocio(Severity.ERROR,"ERRO_REGISTRAR_INFORMACOES_COLETA_EXAME_CAMPOS_MEDICAMENTO_NAO_INFORMADOS");
		
			return;

		}

		if (possuiInformacaoMdtoColetaEmEdicao) { // Alterar
			for (AelInformacaoMdtoColeta infoMdtoColeta : listaInformacaoMdtoColeta) {
				if (infoMdtoColeta.getEmEdicao() != null
						&& infoMdtoColeta.getEmEdicao()) {
					infoMdtoColeta.setMedicamento(medicamento);
					infoMdtoColeta.setDthrIngeriu(dthrIngeriu);
					infoMdtoColeta.setDthrColetou(dthrColetou);
					infoMdtoColeta.setEmEdicao(Boolean.FALSE);

					break;
				}
			}
		} else { // Adicionar
			// Seqp é setado antes para permitir usar o equals para remoção
			if (listaSolicitacaoExames != null && !listaSolicitacaoExames.isEmpty()) { //#25917
				AelInformacaoMdtoColetaId informacaoMdtoColetaId = new AelInformacaoMdtoColetaId();
				informacaoMdtoColetaId
						.setSeqp(obterMaxSeqpLista(listaInformacaoMdtoColeta));
				informacaoMdtoColetaId.setIclSoeSeq(listaSolicitacaoExames.get(0)
						.getSeq());
				informacaoMdtoColetaId.setIclSeqp((short) 1);
				AelInformacaoMdtoColeta informacaoMdtoColeta = new AelInformacaoMdtoColeta();
				informacaoMdtoColeta.setId(informacaoMdtoColetaId);
				informacaoMdtoColeta.setMedicamento(medicamento);
				informacaoMdtoColeta.setDthrIngeriu(dthrIngeriu);
				informacaoMdtoColeta.setDthrColetou(dthrColetou);
				informacaoMdtoColeta.setEmEdicao(Boolean.FALSE);
		
				listaInformacaoMdtoColeta.add(informacaoMdtoColeta);
			}
		}

		inicializarFieldsMedicamento();

	}
	
	
	private Integer obterMaxSeqpLista(List<AelInformacaoMdtoColeta> listaInformacaoMdtoColeta) {
		int seqp = 0;
		for (AelInformacaoMdtoColeta aelInformacaoMdtoColeta : listaInformacaoMdtoColeta) {
			if (aelInformacaoMdtoColeta.getId().getSeqp() > seqp) {
				seqp = aelInformacaoMdtoColeta.getId().getSeqp();
			}
		}
		return seqp + 1;
	}
	
	
	public void cancelarEdicaoInformacaoMdtoColeta() {
		inicializarFieldsMedicamento();
		for (AelInformacaoMdtoColeta infoMdtoColeta : listaInformacaoMdtoColeta) {
			infoMdtoColeta.setEmEdicao(Boolean.FALSE);
		}
	}
	
	public void editarInformacaoMdtoColeta() {
		AelInformacaoMdtoColeta informacaoMdtoColeta = mdtoColetaEdit;
		medicamento = informacaoMdtoColeta.getMedicamento();
		dthrIngeriu = informacaoMdtoColeta.getDthrIngeriu();
		dthrColetou = informacaoMdtoColeta.getDthrColetou();
		if (dthrIngeriu == null) {
			pacienteNaoSoubeInfoMedicamento = Boolean.TRUE;
		}
		informacaoMdtoColeta.setEmEdicao(Boolean.TRUE);		
	}
	
	public void removerInformacaoMdtoColeta() {
		AelInformacaoMdtoColeta informacaoMdtoColeta = mdtoColetaEdit;
		try {
			coletaExamesFacade.removerInformacaoMdtoColeta(informacaoMdtoColeta);
			listaInformacaoMdtoColeta.remove(informacaoMdtoColeta);
			mdtoColetaEdit = null;
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
		}

	}

	public Boolean getPossuiInformacaoMdtoColetaEmEdicao() {
		if (listaInformacaoMdtoColeta != null && !listaInformacaoMdtoColeta.isEmpty()) {
			for (AelInformacaoMdtoColeta infoMdtoColeta : listaInformacaoMdtoColeta) {
				if (Boolean.TRUE.equals(infoMdtoColeta.getEmEdicao())) {
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}
	
	public Boolean getDesabilitaPacienteCumpriuJejum() {
		if (DominioCumpriuJejumColeta.N.equals(pacienteCumpriuJejum)) {
			return Boolean.FALSE;
		} else {
			return Boolean.TRUE;
		}
	}
	public void selecionaDeselecionaPacienteNaoSoubeInformar() {
		if (pacienteNaoSoubeInfoMedicamento) {
			dthrIngeriu = null;
		} else {
			Calendar calCopia = Calendar.getInstance();
			calCopia.add(Calendar.DAY_OF_MONTH, -1);
			dthrIngeriu = calCopia.getTime();
		}
	}
	
	public void gravar() {
		try {			
			informacaoColeta.setCumpriuJejum(pacienteCumpriuJejum);
			informacaoColeta.setJejumRealizado(motivoJejumNaoRealizado);
			informacaoColeta.setDocumento(pacienteApresentouRgComFoto.isSim());
			informacaoColeta.setTipoAcesso(tipoAcessoColeta);
			informacaoColeta.setDtUltMenstruacao(dtUltimaMenstruacao);
			informacaoColeta.setInfMenstruacao(pacienteNaoSoubeInfoMenstruacao);
			informacaoColeta.setInfMedicacao(pacienteNaoSoubeInfoMedicamento);
			informacaoColeta.setLocalColeta(localColeta);
			informacaoColeta.setInformacoesAdicionais(informacoesAdicionais);
			informacaoColeta = coletaExamesFacade.persistirInformacaoColeta(informacaoColeta, listaInformacaoMdtoColeta, listaSolicitacaoExames);
			apresentarMsgNegocio(Severity.INFO, "LABEL_REGISTRAR_INFORMACOES_COLETA_EXAME_SALVO_SUCESSO");
			this.carregarInformacoesColeta();
			this.atualizarInformacaoMdtoColeta();
			this.salvouInfoColeta = true;
			//this.informacaoColeta.setId(new AelInformacaoColetaId()); //para indicar que gravou.
			// this.inicializarFieldsMedicamento();
		} catch (final BaseException e) {
//			this.salvouInfoColeta = false;
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}
	}
	
	private void atualizarInformacaoMdtoColeta(){
		listaInformacaoMdtoColeta = null;
		listaInformacaoMdtoColeta = coletaExamesFacade.buscarAelInformacaoMdtoColetaByAelInformacaoColeta(informacaoColeta.getId().getSeqp(),
				informacaoColeta.getId().getSoeSeq());

	}
	
	public String voltar() {
		if (!this.salvouInfoColeta) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_REGISTRAR_INFORMACOES_COLETA_EXAME_INFO_COLETA_NAO_SALVA");
			return null;
		} else {
			//informacaoColeta.setId(null);
			return "pesquisaAgendaExamesHorarios";
		}
	}

	public DominioCumpriuJejumColeta getPacienteCumpriuJejum() {
		return pacienteCumpriuJejum;
	}

	public void setPacienteCumpriuJejum(
			DominioCumpriuJejumColeta pacienteCumpriuJejum) {
		this.pacienteCumpriuJejum = pacienteCumpriuJejum;
	}

	public String getMotivoJejumNaoRealizado() {
		return motivoJejumNaoRealizado;
	}

	public void setMotivoJejumNaoRealizado(String motivoJejumNaoRealizado) {
		this.motivoJejumNaoRealizado = motivoJejumNaoRealizado;
	}

	public DominioSimNao getPacienteApresentouRgComFoto() {
		return pacienteApresentouRgComFoto;
	}

	public void setPacienteApresentouRgComFoto(
			DominioSimNao pacienteApresentouRgComFoto) {
		this.pacienteApresentouRgComFoto = pacienteApresentouRgComFoto;
	}

	public DominioTipoAcessoColeta getTipoAcessoColeta() {
		return tipoAcessoColeta;
	}

	public void setTipoAcessoColeta(DominioTipoAcessoColeta tipoAcessoColeta) {
		this.tipoAcessoColeta = tipoAcessoColeta;
	}

	public Date getDtUltimaMenstruacao() {
		return dtUltimaMenstruacao;
	}

	public void setDtUltimaMenstruacao(Date dtUltimaMenstruacao) {
		this.dtUltimaMenstruacao = dtUltimaMenstruacao;
	}

	public Boolean getPacienteNaoSoubeInfoMenstruacao() {
		return pacienteNaoSoubeInfoMenstruacao;
	}

	public void setPacienteNaoSoubeInfoMenstruacao(
			Boolean pacienteNaoSoubeInfoMenstruacao) {
		this.pacienteNaoSoubeInfoMenstruacao = pacienteNaoSoubeInfoMenstruacao;
	}

	public DominioLocalColetaAmostra getLocalColeta() {
		return localColeta;
	}

	public void setLocalColeta(DominioLocalColetaAmostra localColeta) {
		this.localColeta = localColeta;
	}

	public String getInformacoesAdicionais() {
		return informacoesAdicionais;
	}

	public void setInformacoesAdicionais(String informacoesAdicionais) {
		this.informacoesAdicionais = informacoesAdicionais;
	}

	public Boolean getPacienteNaoSoubeInfoMedicamento() {
		return pacienteNaoSoubeInfoMedicamento;
	}

	public void setPacienteNaoSoubeInfoMedicamento(
			Boolean pacienteNaoSoubeInfoMedicamento) {
		this.pacienteNaoSoubeInfoMedicamento = pacienteNaoSoubeInfoMedicamento;
	}

	public String getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(String medicamento) {
		this.medicamento = medicamento;
	}

	public Date getDthrIngeriu() {
		return dthrIngeriu;
	}

	public void setDthrIngeriu(Date dthrIngeriu) {
		this.dthrIngeriu = dthrIngeriu;
	}

	public Date getDthrColetou() {
		return dthrColetou;
	}

	public void setDthrColetou(Date dthrColetou) {
		this.dthrColetou = dthrColetou;
	}

	public IExamesFacade getExamesFacade() {
		return examesFacade;
	}

	public void setExamesFacade(IExamesFacade examesFacade) {
		this.examesFacade = examesFacade;
	}

	public String getLabelPaciente() {
		return labelPaciente;
	}

	public void setLabelPaciente(String labelPaciente) {
		this.labelPaciente = labelPaciente;
	}

	public Short getHedGaeUnfSeq() {
		return hedGaeUnfSeq;
	}

	public void setHedGaeUnfSeq(Short hedGaeUnfSeq) {
		this.hedGaeUnfSeq = hedGaeUnfSeq;
	}

	public Integer getHedGaeSeqp() {
		return hedGaeSeqp;
	}

	public void setHedGaeSeqp(Integer hedGaeSeqp) {
		this.hedGaeSeqp = hedGaeSeqp;
	}

	

	public List<AelInformacaoMdtoColeta> getListaInformacaoMdtoColeta() {
		return listaInformacaoMdtoColeta;
	}

	public void setListaInformacaoMdtoColeta(
			List<AelInformacaoMdtoColeta> listaInformacaoMdtoColeta) {
		this.listaInformacaoMdtoColeta = listaInformacaoMdtoColeta;
	}

	public IColetaExamesFacade getColetaExamesFacade() {
		return coletaExamesFacade;
	}

	public void setColetaExamesFacade(IColetaExamesFacade coletaExamesFacade) {
		this.coletaExamesFacade = coletaExamesFacade;
	}

	public List<AelSolicitacaoExames> getListaSolicitacaoExames() {
		return listaSolicitacaoExames;
	}

	public void setListaSolicitacaoExames(
			List<AelSolicitacaoExames> listaSolicitacaoExames) {
		this.listaSolicitacaoExames = listaSolicitacaoExames;
	}

	public AelInformacaoColeta getInformacaoColeta() {
		return informacaoColeta;
	}

	public void setInformacaoColeta(AelInformacaoColeta informacaoColeta) {
		this.informacaoColeta = informacaoColeta;
	}

	public Integer getSoeSeqSelecionado() {
		return soeSeqSelecionado;
	}

	public void setSoeSeqSelecionado(Integer soeSeqSelecionado) {
		this.soeSeqSelecionado = soeSeqSelecionado;
	}

	public String getLabelSolicitacao() {
		return labelSolicitacao;
	}

	public void setLabelSolicitacao(String labelSolicitacao) {
		this.labelSolicitacao = labelSolicitacao;
	}

	public Boolean getOrigemMenu() {
		return origemMenu;
	}

	public void setOrigemMenu(Boolean origemMenu) {
		this.origemMenu = origemMenu;
	}

//	public Boolean getCameFrom() {
//		return cameFrom;
//	}
//
//	public void setCameFrom(Boolean cameFrom) {
//		this.cameFrom = cameFrom;
//	}

	public Boolean getSalvouInfoColeta() {
		return salvouInfoColeta;
	}

	public void setSalvouInfoColeta(Boolean salvouInfoColeta) {
		this.salvouInfoColeta = salvouInfoColeta;
	}

	public Date getHedDthrAgenda() {
		return hedDthrAgenda;
	}

	public void setHedDthrAgenda(Date hedDthrAgenda) {
		this.hedDthrAgenda = hedDthrAgenda;
	}

	public AelInformacaoMdtoColeta getMdtoColetaEdit() {
		return mdtoColetaEdit;
	}

	public void setMdtoColetaEdit(AelInformacaoMdtoColeta mdtoColetaEdit) {
		this.mdtoColetaEdit = mdtoColetaEdit;
	}

}
