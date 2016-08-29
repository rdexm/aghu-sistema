package br.gov.mec.aghu.paciente.action;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipProntuariosImpressos;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.business.PacConsultaVo;
import br.gov.mec.aghu.paciente.vo.ZplVo;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.commons.seguranca.AuthorizationException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.exceptioncode.AghuSecureInterceptorExceptionCode;

/**
 * Controller para geração Etiquetas de Movimentacao de Prontuario.
 * 
 * @author RicardoCosta
 */
public class EtiquetasMovimentacaoProntuarioController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3210494217909492541L;
	
	private static final Log LOG = LogFactory.getLog(EtiquetasMovimentacaoProntuarioController.class);

	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@Inject
	private SecurityController securityController;

	/**
	 * Turno de.
	 */
	private Integer nbTurnoDe;

	/**
	 * Turno até.
	 */
	private Integer nbTurnoAte;

	/**
	 * Zona de.
	 */
	private String txZonaDe;

	/**
	 * Zona até.
	 */
	private String txZonaAte;

	/**
	 * Sala de
	 */
	private Integer nbSalaDe;

	/**
	 * Sala até
	 */
	private Integer nbSalaAte;

	/**
	 * Data de Referência.
	 */
	private Date dtReferencia;

	/**
	 * Código ZPL que será enviado à impressora.
	 */
	private String zpl;

	/**
	 * Intervalo inicial.
	 */
	private String vtznslInicial;

	/**
	 * Intervalo final.
	 */
	private String vtznslFinal;

	/**
	 * Indica a unidade funcional SAMIS
	 */
	private AghUnidadesFuncionais unidadeSamis = null;

	/**
	 * Vo com o resultado da ON de Etiquetas.
	 */
	private ZplVo res;

	/**
	 * Hash com Pacientes envolvidos.
	 */
	private Map<Integer, PacConsultaVo> hashProntPacConsultaVo = new HashMap<Integer, PacConsultaVo>();

	/**
	 * Flag que indica se é reimpressão ou não.
	 */
	private Boolean isReprint;

	/**
	 * Método executando na criação do componente de acordo com escopo.
	 */
	@PostConstruct
	public void inicio() {
		this.begin(conversation);
		try {
			// Recupara data de referência.
			AghParametros aghParametro = parametroFacade
					.buscarAghParametro(AghuParametrosEnum.P_DT_REFERENCIA);
			this.dtReferencia = aghParametro.getVlrData();
		} catch (ApplicationBusinessException e) {
			this.dtReferencia = new Date();
		}

		txZonaDe = "";
		txZonaAte = "";

		// busca unidade funcional SAMIS
		AghParametros aghParam = null;
		try {
			aghParam = parametroFacade
					.buscarAghParametro(AghuParametrosEnum.P_UNIDADE_SAMIS);
			unidadeSamis = aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(aghParam.getVlrNumerico().shortValue());
			// a busca pela impressora foi retirada daqui devido a abstração do
			// sistema de impressão. implementações diferentes buscam a
			// impressora de forma diferente. 
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);			
			apresentarMsgNegocio(Severity.ERROR,
					e.getLocalizedMessage());
			return;
		}

	}

	/**
	 * Ação do botão gerar etiquetas.
	 */
	public void gerarEtiquetasImpressao() {
		this.actionGerarEtiquetas(false);
	}

	/**
	 * Ação do botão reimprimir.
	 */
	public void gerarEtiquetasReimpressao() {
		this.actionGerarEtiquetas(true);
	}

	/**
	 * Ação do botão imprimir etiquetas e atualizar.
	 */
	public void imprimirEtiquetas() {
		// envia impressao das etiquetas
		try {
			
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoIPv4HostRemoto().toString();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}	
			
			String componente = "relatorioPaciente";
			String metodo = "etiquetasMovimentacaoProntuario";
			boolean temPermissao = securityController.usuarioTemPermissao(
					componente, metodo);
			if (!temPermissao) {
				throw new AuthorizationException(
						AghuSecureInterceptorExceptionCode.ERRO_PERMISSAO,
						obterLoginUsuarioLogado() ,componente, metodo);
			}
			
			this.sistemaImpressao.imprimir(zpl, unidadeSamis,
					TipoDocumentoImpressao.ETIQUETAS_BARRAS_PRONTUARIO);
			pacienteFacade.atualizaEtiquetasImpressas(res.getListPacZplVo(),
					isReprint, dtReferencia, hashProntPacConsultaVo,
					nomeMicrocomputador, new Date());
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_IMPRESSAO_ETIQUETAS");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			// TODO: colocar em bundle
			LOG.error("erro na impressão de etiquetas zpl", e);
			this.apresentarMsgNegocio(Severity.ERROR,
					"Erro na impressão das etiquetas");
		}
	}

	/**
	 * Método que gera o código ZPL que será usado na etiquetas.
	 * 
	 * @param rePrint
	 *            - Indica se é reimpressão ou não.
	 * @return
	 */
	private void actionGerarEtiquetas(Boolean rePrint) {

		this.isReprint = rePrint;

		// Hash para PacConsultaVo.
		hashProntPacConsultaVo = new HashMap<Integer, PacConsultaVo>();

		// Hash para Prontuarios Impressos por data referência.
		Map<Integer, AipProntuariosImpressos> hashProntImpressos = new HashMap<Integer, AipProntuariosImpressos>(
				0);

		try {
			// Preenche HASH com dados de PacConsultas.
			hashProntPacConsultaVo = pacienteFacade
					.obterPacConsultas(this.dtReferencia, rePrint);

			if (rePrint) {
				// Preenche HASH com dados de PacConsultas.
				hashProntImpressos = pacienteFacade
						.obterProntImpressos(this.dtReferencia);
			}

			// Preenche código ZPL a ser impresso.
			res = pacienteFacade.obterDadosEtiquetas(
					this.dtReferencia, rePrint, this.nbTurnoDe,
					this.nbTurnoAte, this.txZonaDe, this.txZonaAte,
					this.nbSalaDe, this.nbSalaAte, hashProntPacConsultaVo,
					hashProntImpressos);

			zpl = res.getZpl();
			vtznslInicial = res.getVtznslInicial();
			vtznslFinal = res.getVtznslFinal();

			openDialog("modalWG");
			
		} catch (ApplicationBusinessException e) {
			e.getMessage();
			this.apresentarMsgNegocio(Severity.ERROR,
					e.getLocalizedMessage());
		}
	}

	/**
	 * Método invocado ao usuário cancelar impressão.
	 * 
	 * @return String
	 */
	public void actionCancel() {
		try {
			cadastrosBasicosInternacaoFacade.cancelarImpressao();
		} catch (ApplicationBusinessException e) {
			e.getMessage();
			this.apresentarMsgNegocio(Severity.WARN,
					e.getLocalizedMessage());
		}
	}

	/*
	 * GET's e SET's.
	 */
	public String getZpl() {
		return zpl;
	}

	public void setZpl(String zpl) {
		this.zpl = zpl;
	}

	public Date getDtReferencia() {
		return dtReferencia;
	}

	public void setDtReferencia(Date dtReferencia) {
		this.dtReferencia = dtReferencia;
	}

	public String getTxZonaDe() {
		return txZonaDe;
	}

	public void setTxZonaDe(String txZonaDe) {
		this.txZonaDe = txZonaDe;
	}

	public String getTxZonaAte() {
		return txZonaAte;
	}

	public void setTxZonaAte(String txZonaAte) {
		this.txZonaAte = txZonaAte;
	}

	public Integer getNbTurnoDe() {
		return nbTurnoDe;
	}

	public void setNbTurnoDe(Integer nbTurnoDe) {
		this.nbTurnoDe = nbTurnoDe;
	}

	public Integer getNbTurnoAte() {
		return nbTurnoAte;
	}

	public void setNbTurnoAte(Integer nbTurnoAte) {
		this.nbTurnoAte = nbTurnoAte;
	}

	public Integer getNbSalaDe() {
		return nbSalaDe;
	}

	public void setNbSalaDe(Integer nbSalaDe) {
		this.nbSalaDe = nbSalaDe;
	}

	public Integer getNbSalaAte() {
		return nbSalaAte;
	}

	public void setNbSalaAte(Integer nbSalaAte) {
		this.nbSalaAte = nbSalaAte;
	}

	public String getVtznslInicial() {
		return vtznslInicial;
	}

	public void setVtznslInicial(String vtznslInicial) {
		this.vtznslInicial = vtznslInicial;
	}

	public String getVtznslFinal() {
		return vtznslFinal;
	}

	public void setVtznslFinal(String vtznslFinal) {
		this.vtznslFinal = vtznslFinal;
	}
	
	public AghUnidadesFuncionais getUnidadeSamis() {
		return unidadeSamis;
	}

	public void setUnidadeSamis(AghUnidadesFuncionais unidadeSamis) {
		this.unidadeSamis = unidadeSamis;
	}

	public ZplVo getRes() {
		return res;
	}

	public void setRes(ZplVo res) {
		this.res = res;
	}

	public Map<Integer, PacConsultaVo> getHashProntPacConsultaVo() {
		return hashProntPacConsultaVo;
	}

	public void setHashProntPacConsultaVo(
			Map<Integer, PacConsultaVo> hashProntPacConsultaVo) {
		this.hashProntPacConsultaVo = hashProntPacConsultaVo;
	}

	public Boolean getIsReprint() {
		return isReprint;
	}

	public void setIsReprint(Boolean isReprint) {
		this.isReprint = isReprint;
	}
}
