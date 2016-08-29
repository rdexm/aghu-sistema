package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioUnidadeMedidaTempo;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelIntervaloColeta;
import br.gov.mec.aghu.model.AelRefCode;
import br.gov.mec.aghu.model.AelTmpIntervaloColeta;
import br.gov.mec.aghu.model.AelTmpIntervaloColetaId;

@SuppressWarnings("PMD.AghuTooManyMethods")
public class ManterIntervaloColetaController extends ActionController {

	private static final String CAMPO_OBRIGATORIO = "CAMPO_OBRIGATORIO";

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final String PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD = "exames-manterDadosBasicosExamesCRUD";

	private static final long serialVersionUID = 7968680874971395066L;
	private static final String UNIDADE_MEDIDA_VOLUME_DOMINIO = "UNID MED INGERIDA";
	private static final String TIPO_SUBSTANCIA_DOMINIO = "TIPO SUBSTANCIA";

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	// Variáveis para controle de edição
	private String siglaExame;
	private Integer codigoMaterial;
	private AelExamesMaterialAnalise exameMaterialAnalise;
	private AelIntervaloColeta intervalo;
	private AelTmpIntervaloColeta tempo;

	private short maxSeqTempo = 0;

	private List<AelIntervaloColeta> intervalosColeta;
	private List<AelTmpIntervaloColeta> temposColeta;

	private Short intervaloEmEdicaoId;
	private Short tempoEmEdicaoId;

	private String origemModal;
	private boolean exibirModalAlteracoesPendentes;
	
	private Boolean intervaloColetaEmEdicao = Boolean.FALSE;

	/**
	 * Método chamado ao carregar a tela de cadastro de intervalos de coleta
	 */
	public void iniciar() {
	 

		if (siglaExame != null && codigoMaterial != null) {
			exameMaterialAnalise = examesFacade.buscarAelExamesMaterialAnalisePorId(siglaExame, codigoMaterial);
			intervalosColeta = examesFacade.listarIntervalosColetaPorExameMaterial(siglaExame, codigoMaterial);

			resetarIntervalo();
			resetarTempo();
		}
		//this.//setIgnoreInitPageConfig(true);
	
	}

	public boolean exibirModalAlteracoesPendentes() {
		return exibirModalAlteracoesPendentes;
	}

	/**
	 * Método que realiza a ação do botão voltar na tela de cadastro de intervalos de coleta
	 */
	public String voltar() {
		this.limparParametros();
		return PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD;
	}

	private void limparParametros() {
		this.siglaExame = null;
		this.codigoMaterial = null;
		this.exameMaterialAnalise = null;
		this.intervalo = null;
		this.tempo = null;
		this.maxSeqTempo = 0;
		this.intervalosColeta = null;
		this.temposColeta = null;
		this.intervaloEmEdicaoId = null;
		this.tempoEmEdicaoId = null;
		this.origemModal = null;
		this.exibirModalAlteracoesPendentes = false;
		this.intervaloColetaEmEdicao = Boolean.FALSE;
	}

	public String gravar() {
		origemModal = "gravar";
		if (tempo.getTempo() != null) {
			exibirModalAlteracoesPendentes = true;
			return null;
		} else {
			gravarIntervaloColeta();
			return null;
		}
	}

	public String confirmarModal() {
		if ("voltar".equals(origemModal)) {
			this.limparParametros();
			return PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD;
		} else {
			exibirModalAlteracoesPendentes = false;
			gravarIntervaloColeta();
			return null;
		}
	}

	public void cancelarModal() {
		exibirModalAlteracoesPendentes = false;
	}

	// ------------------------------------------------------
	// Métodos relacionados a operações sobre Intervalos de Coleta

	/**
	 * Método que realiza a ação do botão gravar na tela de cadastro de intervalos de coleta
	 */
	public void gravarIntervaloColeta() {
		// Verifica se a ação é de criação ou edição
		boolean criando = intervalo.getSeq() == null;

		if (validarCamposObrigatoriosIntervaloColeta()) {
			try {
				if (criando) {
					intervalo.setEmaExaSigla(getSiglaExame());
					intervalo.setEmaManSeq(getCodigoMaterial());
				}

				cadastrosApoioExamesFacade.persistirIntervaloColeta(intervalo);

				// cadastrosApoioExamesFacade.flush();

				if (criando) {
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_INTERVALO_COLETA", intervalo.getDescricao());
				} else {
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_INTERVALO_COLETA", intervalo.getDescricao());
				}

				intervalosColeta = examesFacade.listarIntervalosColetaPorExameMaterial(siglaExame, codigoMaterial);

				resetarIntervalo();
			} catch (BaseException e) {
				intervalo.setSeq(null);
				apresentarExcecaoNegocio(e);
			} catch (PersistenceException e) {
				if (criando) {
					intervalo.setSeq(null);
					apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_CRIACAO_INTERVALO_COLETA", intervalo.getDescricao());
				} else {
					apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_EDICAO_INTERVALO_COLETA", intervalo.getDescricao());
				}
			}
		}
	}

	private boolean validarCamposObrigatoriosIntervaloColeta() {
		boolean camposPreenchidos = true;

		if (StringUtils.isBlank(this.intervalo.getDescricao())) {
			apresentarMsgNegocio("descricaoIntervalo", Severity.ERROR, CAMPO_OBRIGATORIO, "Descrição");
			camposPreenchidos = false;
		}
		if (intervalo.getUnidMedidaTempo() == null) {
			apresentarMsgNegocio("unidadeMedidaTempo", Severity.ERROR, CAMPO_OBRIGATORIO, "Unid Medida Tempo");
			camposPreenchidos = false;
		}
		if (intervalo.getIndSituacao() == null) {
			apresentarMsgNegocio("situacao", Severity.ERROR, CAMPO_OBRIGATORIO, "Situação");
			camposPreenchidos = false;
		}
		
		if(intervalo.getDescricao() != null){
			intervalo.setDescricao(intervalo.getDescricao().trim().toUpperCase());
		}

		return camposPreenchidos;
	}

	public void excluirIntervaloColeta(AelIntervaloColeta intervaloColeta) {
		try {
			cadastrosApoioExamesFacade.removerIntervaloColeta(intervaloColeta.getSeq());

			// cadastrosApoioExamesFacade.flush();
			if (intervaloEmEdicaoId != null && intervaloEmEdicaoId == intervaloColeta.getSeq()) {
				resetarIntervalo();
				resetarTempo();
			}

			intervalosColeta = examesFacade.listarIntervalosColetaPorExameMaterial(siglaExame, codigoMaterial);

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_INTERVALO_COLETA", intervaloColeta.getDescricao());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (PersistenceException e) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_EXCLUSAO_IvNTERVALO_COLETA", intervaloColeta.getDescricao());
		}
	}

	public List<AelRefCode> getUnidadesMedidaVolume() {
		return examesFacade.obterCodigosPorDominio(UNIDADE_MEDIDA_VOLUME_DOMINIO);
	}

	public List<AelRefCode> getTiposSubstancia() {
		return examesFacade.obterCodigosPorDominio(TIPO_SUBSTANCIA_DOMINIO);
	}

	public void iniciarEdicaoIntervaloColeta(AelIntervaloColeta intervaloColeta) {
		resetarTempo();

		intervaloEmEdicaoId = intervaloColeta.getSeq();
		List<AelTmpIntervaloColeta> temposCopia = new ArrayList<AelTmpIntervaloColeta>();

		// final List<AelTmpIntervaloColeta> temposColeta = intervaloColeta.getTemposColeta();
		final List<AelTmpIntervaloColeta> temposItemSelecionado = this.examesFacade.pesquisarTempoPorIntervaloColeta(intervaloColeta.getSeq());
		for (AelTmpIntervaloColeta tempovar : temposItemSelecionado) {
			temposCopia.add(new AelTmpIntervaloColeta(tempovar.getId(), tempovar.getTempo(), tempovar.getCriadoEm(), tempovar.getServidor()));

			if (tempovar.getId().getSeqp() > maxSeqTempo) {
				maxSeqTempo = tempovar.getId().getSeqp();
			}
		}

		intervalo = new AelIntervaloColeta(intervaloColeta.getSeq(), intervaloColeta.getEmaExaSigla(), intervaloColeta.getEmaManSeq(), intervaloColeta.getDescricao(), intervaloColeta.getNroColetas(),
				intervaloColeta.getTempo(), intervaloColeta.getUnidMedidaTempo(), intervaloColeta.getIndSituacao(), intervaloColeta.getVolumeIngerido(), intervaloColeta.getUnidMedidaVolume(),
				intervaloColeta.getTipoSubstancia(), temposCopia);
		temposColeta = temposCopia;

	}

	public boolean intervaloColetaEmEdicao(AelIntervaloColeta intervaloColeta) {
		if (intervaloEmEdicaoId != null && intervaloColeta != null && intervaloColeta.getSeq() != null) {
			this.intervaloColetaEmEdicao = Boolean.TRUE;
			return intervaloEmEdicaoId.equals(intervaloColeta.getSeq());
		} else {
			this.intervaloColetaEmEdicao = Boolean.FALSE;
			return false;
		}
	}

	public boolean emEdicaoIntervaloColeta() {
		return intervaloEmEdicaoId != null;
	}

	public void cancelarEdicaoIntervaloColeta() {
		resetarIntervalo();

		resetarTempo();
	}

	private void resetarIntervalo() {
		intervalo = new AelIntervaloColeta();
		intervalo.setIndSituacao(DominioSituacao.A);
		intervalo.setUnidMedidaTempo(DominioUnidadeMedidaTempo.D);
		intervaloEmEdicaoId = null;

		temposColeta = null;
		maxSeqTempo = 0;
	}

	// ------------------------------------------------------
	// Métodos relacionados a operações sobre Tempos de Coleta

	public Integer getValorTempo() {
		if (tempo != null) {
			if (tempo.getTempo() != null) {
				return tempo.getTempo().intValue();
			}
		}

		return null;
	}

	public void setValorTempo(Integer valor) {
		if (tempo != null) {
			if (valor != null) {
				tempo.setTempo(valor.shortValue());
			} else {
				tempo.setTempo(null);
			}
		}
	}

	public void adicionarTempoColeta() {
		if (validarCamposObrigatoriosIntervaloColeta()) {
			if (tempo.getTempo() == null) {
				apresentarMsgNegocio("tempo", Severity.ERROR, CAMPO_OBRIGATORIO, "Tempo");
			} else {
				AelTmpIntervaloColetaId id = new AelTmpIntervaloColetaId();
				if (intervalo.getSeq() != null) {
					id.setIcoSeq(intervalo.getSeq());
				}
				id.setSeqp(++maxSeqTempo);
				tempo.setId(id);
				intervalo.getTemposColeta().add(tempo);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ADICAO_INTERVALO_COLETA_TEMPO", tempo.getTempo(), intervalo.getDescricao());
				resetarTempo();
			}
		}
	}

	public void atualizarTempoColeta() {
		if (tempo.getTempo() == null) {
			apresentarMsgNegocio("tempo", Severity.ERROR, CAMPO_OBRIGATORIO, "Tempo");
		} else {
			for (AelTmpIntervaloColeta tempoVar : intervalo.getTemposColeta()) {
				if (tempoVar.getId().getSeqp() == tempoEmEdicaoId) {
					tempoVar.setTempo(tempo.getTempo());
				}
			}

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_INTERVALO_COLETA_TEMPO", tempo.getTempo(), intervalo.getDescricao());
			resetarTempo();
		}
	}

	public void iniciarEdicaoTempoColeta(AelTmpIntervaloColeta tempoColeta) {
		tempoEmEdicaoId = tempoColeta.getId().getSeqp();
		tempo = new AelTmpIntervaloColeta(tempoColeta.getId(), tempoColeta.getTempo(), tempoColeta.getCriadoEm(), tempoColeta.getServidor());
	}

	public boolean tempoColetaEmEdicao(AelTmpIntervaloColeta tempoColeta) {
		if (tempoEmEdicaoId != null && tempoColeta != null && tempoColeta.getId() != null) {
			return tempoEmEdicaoId.equals(tempoColeta.getId().getSeqp());
		} else {
			return false;
		}
	}

	public boolean emEdicaoTempoColeta() {
		return tempoEmEdicaoId != null;
	}

	public void cancelarEdicaoTempoColeta() {
		resetarTempo();
	}

	public void excluirTempoColeta(AelTmpIntervaloColeta tempoColeta) {
		intervalo.getTemposColeta().remove(tempoColeta);
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_INTERVALO_COLETA_TEMPO", tempoColeta.getTempo(), intervalo.getDescricao());
	}

	private void resetarTempo() {
		tempo = new AelTmpIntervaloColeta();
		tempoEmEdicaoId = null;
	}

	// ------------------------------------------------------
	// Getters e setters

	public String getSiglaExame() {
		return siglaExame;
	}

	public void setSiglaExame(String siglaExame) {
		this.siglaExame = siglaExame;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public AelExamesMaterialAnalise getExameMaterialAnalise() {
		return exameMaterialAnalise;
	}

	public void setExameMaterialAnalise(AelExamesMaterialAnalise exameMaterialAnalise) {
		this.exameMaterialAnalise = exameMaterialAnalise;
	}

	public AelIntervaloColeta getIntervalo() {
		return intervalo;
	}

	public void setIntervalo(AelIntervaloColeta intervalo) {
		this.intervalo = intervalo;
	}

	public AelTmpIntervaloColeta getTempo() {
		return tempo;
	}

	public void setTempo(AelTmpIntervaloColeta tempo) {
		this.tempo = tempo;
	}

	public List<AelIntervaloColeta> getIntervalosColeta() {
		return intervalosColeta;
	}

	public void setIntervalosColeta(List<AelIntervaloColeta> intervalosColeta) {
		this.intervalosColeta = intervalosColeta;
	}

	public List<AelTmpIntervaloColeta> getTemposColeta() {
		return temposColeta;
	}

	public void setTemposColeta(List<AelTmpIntervaloColeta> temposColeta) {
		this.temposColeta = temposColeta;
	}

	public String getOrigemModal() {
		return origemModal;
	}

	public void setOrigemModal(String origemModal) {
		this.origemModal = origemModal;
	}

	public Boolean getIntervaloColetaEmEdicao() {
		return intervaloColetaEmEdicao;
	}

	public void setIntervaloColetaEmEdicao(Boolean intervaloColetaEmEdicao) {
		this.intervaloColetaEmEdicao = intervaloColetaEmEdicao;
	}

}
