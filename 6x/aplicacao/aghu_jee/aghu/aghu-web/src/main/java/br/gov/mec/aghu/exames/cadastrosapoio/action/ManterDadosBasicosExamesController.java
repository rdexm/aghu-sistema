package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.faturamento.action.RelacionarPHISSMController;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

@SuppressWarnings("PMD.AghuTooManyMethods")
public class ManterDadosBasicosExamesController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = 4202837711360328052L;

	private static final String PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_PESQUISA = "exames-manterDadosBasicosExamesPesquisa";

	private static final String  PAGE_VINCULAR_PROCEDIMENTO_SUS= "faturamento-vincularProcedimentoSus";

	private static final Log LOG = LogFactory.getLog(ManterDadosBasicosExamesController.class);

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@Inject
	private RelacionarPHISSMController relacionarPHISSMController;
	
	// Variáveis para controle de edição
	private AelExames exame;
	private String sigla;
	private Integer manSeq;
	private boolean desabilitarCodigo = false;
	private List<AelExamesMaterialAnalise> listaMateriaisExame;
	private String origem;

	private AelExamesMaterialAnalise examesMaterialAnaliseExclusao;
	private AelExamesMaterialAnalise examesMaterialAnaliseSelecionado;

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public String iniciar() {
		if (this.sigla != null && this.examesFacade.obterAelExamesPeloId(this.sigla) == null) {
			apresentarMsgNegocio(Severity.INFO, "REGISTRO_NULO_EXCLUSAO");
			return this.cancelar();
		}

		if (StringUtils.isNotBlank(this.sigla)) {
			this.exame = this.examesFacade.obterAelExamesPeloId(this.sigla);
			this.desabilitarCodigo = true;
			montaListaMateriais();
		}

		if (this.exame == null) {
			this.exame = new AelExames();
			this.exame.setIndSituacao(DominioSituacao.A); // O valor padrão para uma situação de exame é ativo
			this.exame.setIndConsisteInterface(true); // O valor padrão do interfaceamento de um exame é verdadeiro
			this.desabilitarCodigo = false;
			this.sigla = null;
		} else {
			this.sigla = this.exame.getSigla();
			montaListaMateriais();
		}

		return null;
	}

	public void selecionarMaterialAnalise(){
		if (examesMaterialAnaliseSelecionado != null){
			sigla = examesMaterialAnaliseSelecionado.getId().getExaSigla();
			manSeq = examesMaterialAnaliseSelecionado.getId().getManSeq();
		}
	}
	
	public Boolean habilitaVinculacaoAPAC() {
		return recuperaMaterialExameSelecionado() != null;
	}

	public String cadastraPHI() throws ApplicationBusinessException {
		if (!cadastrosApoioExamesFacade.phiJaCriado(this.exame, recuperaMaterialExameSelecionado())) {
			cadastraPHIInexistente();
		} else {
			setaPHIVinculacaoProcedimentoSUS();
		}
		return PAGE_VINCULAR_PROCEDIMENTO_SUS;
	}

	private void setaPHIVinculacaoProcedimentoSUS() {
		FatProcedHospInternos phi = cadastrosApoioExamesFacade.pesquisaPHI(this.exame, recuperaMaterialExameSelecionado());
		setaFiltroPesquisaPHIProximaTela(phi);
	}

	private void cadastraPHIInexistente() throws ApplicationBusinessException {
		try {
			setaFiltroPesquisaPHIProximaTela(cadastraPHIExameSelecionado());
		} catch (ApplicationBusinessException e) {
			adicionaMensagemErroCriarPHI(e);
		}
	}

	private FatProcedHospInternos cadastraPHIExameSelecionado() throws ApplicationBusinessException {
		AelExamesMaterialAnalise materialExameSelecionado = recuperaMaterialExameSelecionado();
		FatProcedHospInternos phiInserido = this.cadastrosApoioExamesFacade.insereProcedimentoHospitalarInterno(materialExameSelecionado);
		return phiInserido;
	}

	private void setaFiltroPesquisaPHIProximaTela(FatProcedHospInternos phiInserido) {
		this.relacionarPHISSMController.setProcedimentoInterno(phiInserido);
		this.relacionarPHISSMController.setVincularApac(Boolean.TRUE);
	}

	private void adicionaMensagemErroCriarPHI(ApplicationBusinessException e) throws ApplicationBusinessException {
		LOG.error("Ocorreu um erro ao tentar criar um PHI baseado no nome do exame", e);
		this.apresentarMsgNegocio(Severity.ERROR,"MSG_ERRO_CRIAR_PHI");
	}

	private AelExamesMaterialAnalise recuperaMaterialExameSelecionado() {
		return this.examesFacade.buscarAelExamesMaterialAnalisePorId(this.sigla, this.manSeq);
	}
	
	/**
	 * Testa campos obrigatórios em branco
	 * 
	 * @return
	 */
	private boolean isValidaCamposRequeridosEmBranco() {
		boolean retorno = true;
		if (this.exame != null) {
			if (StringUtils.isBlank(getSigla())) {
				retorno = false;
				this.sigla = null;
				this.apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Sigla");
			}
			if (StringUtils.isBlank(this.exame.getDescricao())) {
				retorno = false;
				this.exame.setDescricao(null);
				this.apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Nome");
			}
			if (StringUtils.isBlank(this.exame.getDescricaoUsual())) {
				retorno = false;
				this.exame.setDescricaoUsual(null);
				this.apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Nome Usual");
			}

		}
		return retorno;
	}

	/**
	 * Transforma textos em maiúsculo
	 */
	private void transformaCamposDescritivosParaMaiusculo() {
		this.sigla = StringUtils.upperCase(StringUtils.trim(this.sigla));
		this.exame.setDescricao(StringUtils.upperCase(StringUtils.trim(this.exame.getDescricao())));
		this.exame.setDescricaoUsual(StringUtils.upperCase(StringUtils.trim(this.exame.getDescricaoUsual())));
	}

	public String confirmar() {

		// Determina o tipo de mensagem de confirmação
		final boolean isInclusao = StringUtils.isBlank(this.exame.getSigla());

		// Para edição
		if (!isInclusao && this.examesFacade.obterAelExamesPeloId(this.exame.getSigla()) == null) {
			apresentarMsgNegocio(Severity.INFO, "REGISTRO_NULO_EXCLUSAO");
			this.limparParametros();
			return PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_PESQUISA;
		}

		try {
			if (isValidaCamposRequeridosEmBranco()) {

				this.transformaCamposDescritivosParaMaiusculo();

				this.cadastrosApoioExamesFacade.persistirAelExames(this.exame, this.sigla);

				// Determina o tipo de mensagem de confirmação
				if (isInclusao) {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_DADOS_BASICOS_EXAMES", this.exame.getDescricao());
				} else {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_DADOS_BASICOS_EXAMES", this.exame.getDescricao());
				}

				this.exame = null;
				this.sigla = null;

			} else {
				return null;
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			if (isInclusao) {
				this.exame.setSigla(null);
			}
			return null;
		}
		this.limparParametros();
		return PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_PESQUISA;
	}

	/**
	 * Excluir
	 */
	public void excluir() {

		try {

			if (this.examesMaterialAnaliseExclusao != null) {

				String descricao = this.examesMaterialAnaliseExclusao.getAelMateriaisAnalises().getDescricao();
				this.cadastrosApoioExamesFacade.removerAelExamesMaterialAnalise(this.examesMaterialAnaliseExclusao);
				montaListaMateriais();
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_MATERIAL_ANALISE_EXAMES", descricao);

			} else {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_REMOCAO_MATERIAL_ANALISE_EXAMES");
			}

		} catch (BaseListException e) {
			for (Iterator<BaseException> errors = e.iterator(); errors.hasNext();) {
				BaseException aghuNegocioException = (BaseException) errors.next();
				super.apresentarExcecaoNegocio(aghuNegocioException);
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} finally {
			this.examesMaterialAnaliseExclusao = null;
		}
	}

	public void montaListaMateriais() {
		listaMateriaisExame = this.examesFacade.buscarAelExamesMaterialAnalisePorAelExames(this.exame);

		if (listaMateriaisExame == null) {
			listaMateriaisExame = new ArrayList<AelExamesMaterialAnalise>();
		}
	}

	public List<AelExamesMaterialAnalise> getListaMateriaisExame() {
		return listaMateriaisExame;
	}

	/**
	 * Método que realiza a ação do botão cancelar
	 */
	public String cancelar() {

		String retorno = this.origem;

		limparParametros();

		if (StringUtils.isEmpty(retorno)) {
			return PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_PESQUISA;
		}

		return retorno;
	}

	private void limparParametros() {
		this.exame = null;
		this.sigla = null;
		this.manSeq = null;
		this.desabilitarCodigo = false;
		this.listaMateriaisExame = null;
		this.origem = null;
		this.examesMaterialAnaliseExclusao = null;
	}

	/*
	 * Getters and Setters abaixo...
	 */

	public AelExames getExame() {
		return exame;
	}

	public void setExame(AelExames exame) {
		this.exame = exame;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public boolean isDesabilitarCodigo() {
		return desabilitarCodigo;
	}

	public void setDesabilitarCodigo(boolean desabilitarCodigo) {
		this.desabilitarCodigo = desabilitarCodigo;
	}

	public boolean isDisableButtonMaterial() {

		if (this.manSeq != null && (this.sigla != null || exame != null)) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isDisableButtonInfoComplementares() {

		if (this.manSeq != null && (this.sigla != null || exame != null)) {
			return false;
		} else {
			return true;
		}
	}

	public Integer getManSeq() {
		return manSeq;
	}

	public void setManSeq(Integer manSeq) {
		this.manSeq = manSeq;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public AelExamesMaterialAnalise getExamesMaterialAnaliseExclusao() {
		return examesMaterialAnaliseExclusao;
	}

	public void setExamesMaterialAnaliseExclusao(AelExamesMaterialAnalise examesMaterialAnaliseExclusao) {
		this.examesMaterialAnaliseExclusao = examesMaterialAnaliseExclusao;
	}

	public AelExamesMaterialAnalise getExamesMaterialAnaliseSelecionado() {
		return examesMaterialAnaliseSelecionado;
	}

	public void setExamesMaterialAnaliseSelecionado(
			AelExamesMaterialAnalise examesMaterialAnaliseSelecionado) {
		this.examesMaterialAnaliseSelecionado = examesMaterialAnaliseSelecionado;
	}

}
