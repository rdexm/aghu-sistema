package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioConvenioExameSituacao;
import br.gov.mec.aghu.dominio.DominioFuncaoPatologista;
import br.gov.mec.aghu.dominio.DominioSecaoConfiguravel;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSimNaoNaoAplicavel;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.exames.dao.AelAnatomoPatologicoDAO;
import br.gov.mec.aghu.exames.dao.AelApXPatologistaDAO;
import br.gov.mec.aghu.exames.dao.AelCadGuicheDAO;
import br.gov.mec.aghu.exames.dao.AelCestoPatologiaDAO;
import br.gov.mec.aghu.exames.dao.AelConfigExLaudoUnicoDAO;
import br.gov.mec.aghu.exames.dao.AelDescMaterialApsDAO;
import br.gov.mec.aghu.exames.dao.AelDiagnosticoApsDAO;
import br.gov.mec.aghu.exames.dao.AelDiagnosticoLaudosDAO;
import br.gov.mec.aghu.exames.dao.AelExameApDAO;
import br.gov.mec.aghu.exames.dao.AelExameApItemSolicDAO;
import br.gov.mec.aghu.exames.dao.AelExamesMaterialAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoExameApDAO;
import br.gov.mec.aghu.exames.dao.AelGrpDescMatsLacunaDAO;
import br.gov.mec.aghu.exames.dao.AelGrpDiagLacunasDAO;
import br.gov.mec.aghu.exames.dao.AelGrpMacroLacunaDAO;
import br.gov.mec.aghu.exames.dao.AelGrpMicroLacunaDAO;
import br.gov.mec.aghu.exames.dao.AelGrpTxtDescMatsDAO;
import br.gov.mec.aghu.exames.dao.AelGrpTxtPadraoDiagsDAO;
import br.gov.mec.aghu.exames.dao.AelGrpTxtPadraoMacroDAO;
import br.gov.mec.aghu.exames.dao.AelGrpTxtPadraoMicroDAO;
import br.gov.mec.aghu.exames.dao.AelIndiceBlocoApDAO;
import br.gov.mec.aghu.exames.dao.AelInformacaoClinicaApDAO;
import br.gov.mec.aghu.exames.dao.AelItemConfigExameDAO;
import br.gov.mec.aghu.exames.dao.AelKitIndiceBlocoDAO;
import br.gov.mec.aghu.exames.dao.AelKitItemIndiceBlocoDAO;
import br.gov.mec.aghu.exames.dao.AelKitItemMatPatologiaDAO;
import br.gov.mec.aghu.exames.dao.AelKitMatPatologiaDAO;
import br.gov.mec.aghu.exames.dao.AelLaminaApsDAO;
import br.gov.mec.aghu.exames.dao.AelMacroscopiaApsDAO;
import br.gov.mec.aghu.exames.dao.AelMarcadorDAO;
import br.gov.mec.aghu.exames.dao.AelMaterialApDAO;
import br.gov.mec.aghu.exames.dao.AelNomenclaturaApsDAO;
import br.gov.mec.aghu.exames.dao.AelNomenclaturaEspecsDAO;
import br.gov.mec.aghu.exames.dao.AelNomenclaturaGenericsDAO;
import br.gov.mec.aghu.exames.dao.AelNotaAdicionalApDAO;
import br.gov.mec.aghu.exames.dao.AelOcorrenciaExameApDAO;
import br.gov.mec.aghu.exames.dao.AelPatologistaApDAO;
import br.gov.mec.aghu.exames.dao.AelPatologistaDAO;
import br.gov.mec.aghu.exames.dao.AelSecaoConfExamesDAO;
import br.gov.mec.aghu.exames.dao.AelTextoDescMatsDAO;
import br.gov.mec.aghu.exames.dao.AelTextoPadraoColoracsDAO;
import br.gov.mec.aghu.exames.dao.AelTextoPadraoDiagsDAO;
import br.gov.mec.aghu.exames.dao.AelTextoPadraoMacroDAO;
import br.gov.mec.aghu.exames.dao.AelTextoPadraoMicroDAO;
import br.gov.mec.aghu.exames.dao.AelTopografiaAparelhosDAO;
import br.gov.mec.aghu.exames.dao.AelTopografiaApsDAO;
import br.gov.mec.aghu.exames.dao.AelTopografiaCidOsDAO;
import br.gov.mec.aghu.exames.dao.AelTopografiaGruposCidOsDAO;
import br.gov.mec.aghu.exames.dao.AelTopografiaLaudosDAO;
import br.gov.mec.aghu.exames.dao.AelTopografiaSistemasDAO;
import br.gov.mec.aghu.exames.dao.AelTxtDescMatsLacunaDAO;
import br.gov.mec.aghu.exames.dao.AelTxtDiagLacunasDAO;
import br.gov.mec.aghu.exames.dao.AelTxtMacroLacunaDAO;
import br.gov.mec.aghu.exames.dao.AelTxtMicroLacunaDAO;
import br.gov.mec.aghu.exames.dao.VAelApXPatologiaAghuDAO;
import br.gov.mec.aghu.exames.patologia.vo.AelItemSolicitacaoExameLaudoUnicoVO;
import br.gov.mec.aghu.exames.patologia.vo.AelKitMatPatologiaVO;
import br.gov.mec.aghu.exames.patologia.vo.AelPatologistaLaudoVO;
import br.gov.mec.aghu.exames.patologia.vo.ConsultaConfigExamesVO;
import br.gov.mec.aghu.exames.patologia.vo.ConsultaItensPatologiaVO;
import br.gov.mec.aghu.exames.patologia.vo.TelaLaudoUnicoVO;
import br.gov.mec.aghu.exames.vo.AelIdentificarGuicheVO;
import br.gov.mec.aghu.exames.vo.AelpCabecalhoLaudoVO;
import br.gov.mec.aghu.exames.vo.MedicoSolicitanteVO;
import br.gov.mec.aghu.exames.vo.RelatorioLaudoUnicoVO;
import br.gov.mec.aghu.exames.vo.RelatorioMapaLaminasVO;
import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelApXPatologista;
import br.gov.mec.aghu.model.AelCadGuiche;
import br.gov.mec.aghu.model.AelCestoPatologia;
import br.gov.mec.aghu.model.AelCidos;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelDescMatLacunas;
import br.gov.mec.aghu.model.AelDescMatLacunasId;
import br.gov.mec.aghu.model.AelDescMaterialAps;
import br.gov.mec.aghu.model.AelDiagnosticoAps;
import br.gov.mec.aghu.model.AelDiagnosticoLaudos;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExtratoExameAp;
import br.gov.mec.aghu.model.AelExtratoExameApId;
import br.gov.mec.aghu.model.AelGrpDescMatLacunas;
import br.gov.mec.aghu.model.AelGrpDescMatLacunasId;
import br.gov.mec.aghu.model.AelGrpDiagLacunas;
import br.gov.mec.aghu.model.AelGrpDiagLacunasId;
import br.gov.mec.aghu.model.AelGrpMacroLacuna;
import br.gov.mec.aghu.model.AelGrpMacroLacunaId;
import br.gov.mec.aghu.model.AelGrpMicroLacuna;
import br.gov.mec.aghu.model.AelGrpMicroLacunaId;
import br.gov.mec.aghu.model.AelGrpTxtDescMats;
import br.gov.mec.aghu.model.AelGrpTxtPadraoDiags;
import br.gov.mec.aghu.model.AelGrpTxtPadraoMacro;
import br.gov.mec.aghu.model.AelGrpTxtPadraoMicro;
import br.gov.mec.aghu.model.AelIndiceBlocoAp;
import br.gov.mec.aghu.model.AelInformacaoClinicaAP;
import br.gov.mec.aghu.model.AelItemConfigExame;
import br.gov.mec.aghu.model.AelItemConfigExameId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelKitIndiceBloco;
import br.gov.mec.aghu.model.AelKitItemIndiceBloco;
import br.gov.mec.aghu.model.AelKitItemIndiceBlocoId;
import br.gov.mec.aghu.model.AelKitItemMatPatologia;
import br.gov.mec.aghu.model.AelKitItemMatPatologiaId;
import br.gov.mec.aghu.model.AelKitMatPatologia;
import br.gov.mec.aghu.model.AelLaminaAps;
import br.gov.mec.aghu.model.AelMacroscopiaAps;
import br.gov.mec.aghu.model.AelMarcador;
import br.gov.mec.aghu.model.AelMaterialAp;
import br.gov.mec.aghu.model.AelMovimentoGuiche;
import br.gov.mec.aghu.model.AelNomenclaturaAps;
import br.gov.mec.aghu.model.AelNomenclaturaEspecs;
import br.gov.mec.aghu.model.AelNomenclaturaEspecsId;
import br.gov.mec.aghu.model.AelNomenclaturaGenerics;
import br.gov.mec.aghu.model.AelNotaAdicionalAp;
import br.gov.mec.aghu.model.AelOcorrenciaExameAp;
import br.gov.mec.aghu.model.AelPatologista;
import br.gov.mec.aghu.model.AelPatologistaAps;
import br.gov.mec.aghu.model.AelTextoPadraoColoracs;
import br.gov.mec.aghu.model.AelTextoPadraoDiags;
import br.gov.mec.aghu.model.AelTextoPadraoDiagsId;
import br.gov.mec.aghu.model.AelTextoPadraoMacro;
import br.gov.mec.aghu.model.AelTextoPadraoMacroId;
import br.gov.mec.aghu.model.AelTextoPadraoMicro;
import br.gov.mec.aghu.model.AelTextoPadraoMicroId;
import br.gov.mec.aghu.model.AelTopografiaAparelhos;
import br.gov.mec.aghu.model.AelTopografiaAparelhosId;
import br.gov.mec.aghu.model.AelTopografiaAps;
import br.gov.mec.aghu.model.AelTopografiaCidOs;
import br.gov.mec.aghu.model.AelTopografiaGrupoCidOs;
import br.gov.mec.aghu.model.AelTopografiaLaudos;
import br.gov.mec.aghu.model.AelTopografiaSistemas;
import br.gov.mec.aghu.model.AelTxtDescMats;
import br.gov.mec.aghu.model.AelTxtDescMatsId;
import br.gov.mec.aghu.model.AelTxtDiagLacunas;
import br.gov.mec.aghu.model.AelTxtDiagLacunasId;
import br.gov.mec.aghu.model.AelTxtMacroLacuna;
import br.gov.mec.aghu.model.AelTxtMacroLacunaId;
import br.gov.mec.aghu.model.AelTxtMicroLacuna;
import br.gov.mec.aghu.model.AelTxtMicroLacunaId;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.VAelApXPatologiaAghu;
import br.gov.mec.aghu.paciente.vo.ConvenioExamesLaudosVO;

@Modulo(ModuloEnum.EXAMES_LAUDOS)
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects" })
@Stateless
public class ExamesPatologiaFacade extends BaseFacade implements IExamesPatologiaFacade {

	@EJB
	private AelDiagnosticoApsRN aelDiagnosticoApsRN;

	@EJB
	private AelPatologistaApON aelPatologistaApON;

	@EJB
	private AelMacroscopiaApsON aelMacroscopiaApsON;

	@EJB
	private AelNomenclaturaGenericsRN aelNomenclaturaGenericsRN;

	@EJB
	private AelNotaAdicionalApRN aelNotaAdicionalApRN;

	@EJB
	private AelTopografiaLaudosRN aelTopografiaLaudosRN;

	@EJB
	private IdentificaGuicheON identificaGuicheON;

	@EJB
	private AelDiagnosticoLaudosRN aelDiagnosticoLaudosRN;

	@EJB
	private AelIndiceBlocoApRN aelIndiceBlocoApRN;

	@EJB
	private AelKitItemMatPatologiaRN aelKitItemMatPatologiaRN;

	@EJB
	private LaudoUnicoON laudoUnicoON;

	@EJB
	private AelTextoPadraoColoracsRN aelTextoPadraoColoracsRN;

	@EJB
	private AelTopografiaSistemasRN aelTopografiaSistemasRN;

	@EJB
	private AelConfigExLaudoUnicoRN aelConfigExLaudoUnicoRN;

	@EJB
	private AelKitMatPatologiaRN aelKitMatPatologiaRN;

	@EJB
	private AelNomenclaturaEspecsRN aelNomenclaturaEspecsRN;

	@EJB
	private AelCadastroGuicheON aelCadastroGuicheON;

	@EJB
	private VAelApXPatologiaAghuON vAelApXPatologiaAghuON;

	@EJB
	private AelTopografiaAparelhosRN aelTopografiaAparelhosRN;

	@EJB
	private AelMovimentoGuicheRN aelMovimentoGuicheRN;

	@EJB
	private AelPatologistaApRN aelPatologistaApRN;

	@EJB
	private TextoPadraoMicroscopiaON textoPadraoMicroscopiaON;

	@EJB
	private TextoPadraoMacroscopiaON textoPadraoMacroscopiaON;

	@EJB
	private TextoPadraoDescMatsON textoPadraoDescMatsON;

	@EJB
	private AelTopografiaApsRN aelTopografiaApsRN;

	@EJB
	private AelDiagnosticoApsON aelDiagnosticoApsON;

	@EJB
	private AelApXPatologistaON aelApXPatologistaON;

	@EJB
	private AelInformacaoClinicaAPRN aelInformacaoClinicaAPRN;

	@EJB
	private AelLaminaApsON aelLaminaApsON;

	@EJB
	private AelDescMaterialApsON aelDescMaterialApsON;

	@EJB
	private AelDescMaterialApsRN aelDescMaterialApsRN;

	@EJB
	private AelNotaAdicionalApON aelNotaAdicionalApON;

	@EJB
	private AelKitIndiceBlocoRN aelKitIndiceBlocoRN;

	@EJB
	private TextoPadraoDiagnosticoON textoPadraoDiagnosticoON;

	@EJB
	private ConfiguracaoExamesRN configuracaoExamesRN;

	@EJB
	private AelCestoPatologiaRN aelCestoPatologiaRN;

	@EJB
	private AelMaterialApRN aelMaterialApRN;

	@EJB
	private AelPatologistaRN aelPatologistaRN;

	@EJB
	private AelNomenclaturaApsRN aelNomenclaturaApsRN;

	@EJB
	private RelatorioLaudoUnicoON relatorioLaudoUnicoON;

	@EJB
	private AelExameApON aelExameApON;

	@EJB
	private AelMacroscopiaApsRN aelMacroscopiaApsRN;

	@EJB
	private AelMarcadorRN aelMarcadorRN;

	@EJB
	private AelMarcadorON aelMarcadorON;

	@EJB
	private AelKitItemIndiceBlocoRN aelKitItemIndiceBlocoRN;

	@EJB
	private AelItemConfigExameRN aelItemConfigExameRN;

	@EJB
	private AelCadastroGuicheRN aelCadastroGuicheRN;
	
	@EJB
	private AelOcorrenciaExameApRN aelOcorrenciaExameApRN;
	
	@EJB
	private AelExameApRN aelExameApRN;
	
	@EJB
	private AelApXPatologistaRN aelApXPatologistaRN;
	
	@EJB
	private AelAnatomoPatologicoRN aelAnatomoPatologicoRN;

	@Inject
	private AelMaterialApDAO aelMaterialApDAO;

	@Inject
	private AelGrpDiagLacunasDAO aelGrpDiagLacunasDAO;

	@Inject
	private AelKitItemIndiceBlocoDAO aelKitItemIndiceBlocoDAO;

	@Inject
	private AelGrpTxtPadraoMicroDAO aelGrpTxtPadraoMicroDAO;

	@Inject
	private AelTextoPadraoMicroDAO aelTextoPadraoMicroDAO;

	@Inject
	private AelGrpMicroLacunaDAO aelGrpMicroLacunaDAO;

	@Inject
	private AelMarcadorDAO aelMarcadorDAO;

	@Inject
	private AelNomenclaturaEspecsDAO aelNomenclaturaEspecsDAO;

	@Inject
	private AelTxtMacroLacunaDAO aelTxtMacroLacunaDAO;

	@Inject
	private AelGrpTxtDescMatsDAO aelGrpTxtDescMatsDAO;

	@Inject
	private AelTextoDescMatsDAO aelTextoDescMatsDAO;

	@Inject
	private AelGrpDescMatsLacunaDAO aelGrpDescMatsLacunaDAO;

	@Inject
	private AelTxtDescMatsLacunaDAO aelTxtDescMatsLacunaDAO;

	@Inject
	private AelPatologistaApDAO aelPatologistaApDAO;

	@Inject
	private AelKitItemMatPatologiaDAO aelKitItemMatPatologiaDAO;

	@Inject
	private AelAnatomoPatologicoDAO aelAnatomoPatologicoDAO;

	@Inject
	private AelTopografiaLaudosDAO aelTopografiaLaudosDAO;

	@Inject
	private AelTxtDiagLacunasDAO aelTxtDiagLacunasDAO;

	@Inject
	private AelGrpMacroLacunaDAO aelGrpMacroLacunaDAO;

	@Inject
	private AelTopografiaCidOsDAO aelTopografiaCidOsDAO;

	@Inject
	private AelTopografiaSistemasDAO aelTopografiaSistemasDAO;

	@Inject
	private AelGrpTxtPadraoDiagsDAO aelGrpTxtPadraoDiagsDAO;

	@Inject
	private AelDiagnosticoApsDAO aelDiagnosticoApsDAO;

	@Inject
	private AelTxtMicroLacunaDAO aelTxtMicroLacunaDAO;

	@Inject
	private AelNotaAdicionalApDAO aelNotaAdicionalApDAO;

	@Inject
	private AelKitMatPatologiaDAO aelKitMatPatologiaDAO;

	@Inject
	private AelExtratoExameApDAO aelExtratoExameApDAO;

	@Inject
	private AelExamesMaterialAnaliseDAO aelExamesMaterialAnaliseDAO;

	@Inject
	private AelCestoPatologiaDAO aelCestoPatologiaDAO;

	@Inject
	private AelPatologistaDAO aelPatologistaDAO;

	@Inject
	private AelTextoPadraoMacroDAO aelTextoPadraoMacroDAO;

	@Inject
	private AelTextoPadraoDiagsDAO aelTextoPadraoDiagsDAO;

	@Inject
	private AelGrpTxtPadraoMacroDAO aelGrpTxtPadraoMacroDAO;

	@Inject
	private AelDiagnosticoLaudosDAO aelDiagnosticoLaudosDAO;

	@Inject
	private AelLaminaApsDAO aelLaminaApsDAO;

	@Inject
	private AelItemConfigExameDAO aelItemConfigExameDAO;

	@Inject
	private AelIndiceBlocoApDAO aelIndiceBlocoApDAO;

	@Inject
	private AelTopografiaGruposCidOsDAO aelTopografiaGruposCidOsDAO;

	@Inject
	private AelNomenclaturaGenericsDAO aelNomenclaturaGenericsDAO;

	@Inject
	private AelTextoPadraoColoracsDAO aelTextoPadraoColoracsDAO;

	@Inject
	private AelApXPatologistaDAO aelApXPatologistaDAO;

	@Inject
	private AelTopografiaApsDAO aelTopografiaApsDAO;

	@Inject
	private AelConfigExLaudoUnicoDAO aelConfigExLaudoUnicoDAO;

	@Inject
	private AelCadGuicheDAO aelCadGuicheDAO;

	@Inject
	private AelNomenclaturaApsDAO aelNomenclaturaApsDAO;

	@Inject
	private AelExameApDAO aelExameApDAO;

	@Inject
	private AelMacroscopiaApsDAO aelMacroscopiaApsDAO;

	@Inject
	private AelKitIndiceBlocoDAO aelKitIndiceBlocoDAO;

	@Inject
	private VAelApXPatologiaAghuDAO vAelApXPatologiaAghuDAO;

	@Inject
	private AelTopografiaAparelhosDAO aelTopografiaAparelhosDAO;

	@Inject
	private AelInformacaoClinicaApDAO aelInformacaoClinicaApDAO;

	@Inject
	private AelOcorrenciaExameApDAO aelOcorrenciaExameApDAO;

	@Inject
	private AelSecaoConfExamesDAO aelSecaoConfExamesDAO;

	@Inject
	private AelDescMaterialApsDAO aelDescMaterialApsDAO;
	
	@Inject
	private AelExameApItemSolicDAO aelExameApItemSolicDAO;

	private static final long serialVersionUID = 5983082470283664413L;

	@Override
	public AelTextoPadraoColoracs obterAelTextoPadraoColoracs(final Integer seq) {
		return getAelTextoPadraoColoracsDao().obterPorChavePrimaria(seq);
	}

	@Override
	public void persistirAelExameAp(final AelExameAp aelExameAp) throws BaseException {
		this.getAelExameApON().persistirAelExameAp(aelExameAp);
	}
	
	@Override
	public void persistirAelExameAp(final AelExameAp aelExameAp, final AelExameAp aelExameApOld) throws BaseException {
		this.getAelExameApON().persistirAelExameAp(aelExameAp, aelExameApOld);
	}

	@Override
	public List<AelTextoPadraoColoracs> pesquisarAelTextoPadraoColoracs(final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc, final AelTextoPadraoColoracs aelTextoPadraoColoracs) {
		return getAelTextoPadraoColoracsDao().pesquisarAelTextoPadraoColoracs(firstResult, maxResults, orderProperty, asc,
				aelTextoPadraoColoracs);
	}

	@Override
	public Long pesquisarAelTextoPadraoColoracsCount(final AelTextoPadraoColoracs aelTextoPadraoColoracs) {
		return getAelTextoPadraoColoracsDao().pesquisarAelTextoPadraoColoracsCount(aelTextoPadraoColoracs);
	}

	@Override
	public List<AelTextoPadraoColoracs> pesquisarAelTextoPadraoColoracs(final String filtro, final DominioSituacao situacao) {
		return getAelTextoPadraoColoracsDao().pesquisarAelTextoPadraoColoracs(filtro, situacao);
	}

	@Override
	public Long pesquisarAelTextoPadraoColoracsCount(final String filtro, final DominioSituacao situacao) {
		return getAelTextoPadraoColoracsDao().pesquisarAelTextoPadraoColoracsCount(filtro, situacao);
	}

	@Override
	public List<AelTextoPadraoColoracs> listarAelTextoPadraoColoracs(final DominioSituacao situacao) {
		return getAelTextoPadraoColoracsDao().listarAelTextoPadraoColoracs(situacao);
	}

	@Override
	public AelTextoPadraoColoracs obterTextoPadraoColoracsPelaDescricaoExata(String filtro) {
		return getAelTextoPadraoColoracsDao().obterTextoPadraoColoracsPelaDescricaoExata(filtro);
	}

	@Override
	public void inserirAelTextoPadraoColoracs(final AelTextoPadraoColoracs aelTextoPadraoColoracs) throws ApplicationBusinessException {
		getAelTextoPadraoColoracsRN().inserir(aelTextoPadraoColoracs);
	}

	@Override
	public void alterarAelTextoPadraoColoracs(final AelTextoPadraoColoracs aelTextoPadraoColoracs) throws BaseException {
		getAelTextoPadraoColoracsRN().alterar(aelTextoPadraoColoracs);
	}

	@Override
	public void excluirAelTextoPadraoColoracs(final Integer seq) throws ApplicationBusinessException {
		getAelTextoPadraoColoracsRN().excluir(seq);
	}

	@Override
	public Long pesquisarAelCadGuicheCount(final AelCadGuiche aelCadGuiche) {
		return getAelCadGuicheDao().pesquisarAelCadGuicheCount(aelCadGuiche);
	}

	@Override
	public List<AelIdentificarGuicheVO> pesquisarAelCadGuiche(final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc, final AelCadGuiche aelCadGuiche) {
		return getAelCadastroGuicheON().pesquisarAelCadGuiche(firstResult, maxResults, orderProperty, asc, aelCadGuiche);
	}

	@Override
	public AelCadGuiche obterAelCadGuiche(final Short seq) {
		return getAelCadGuicheDao().obterPorChavePrimaria(seq);
	}
	
	@Override
	public AelCadGuiche obterAelGuiche(final Short seq) {
		return getAelCadGuicheDao().obterAelCadGuiche(seq);
	}	

	@Override
	public AelCadGuiche obterAelCadGuichePorUsuarioUnidadeExecutora(final AghUnidadesFuncionais unidade, final String usuario,
			final DominioSituacao situacao, Short seqGuiche) {
		return getAelCadGuicheDao().obterAelCadGuichePorUsuarioUnidadeExecutora(unidade, usuario, situacao, seqGuiche);
	}

	@Override
	public void inserirAelCadGuiche(final AelCadGuiche aelCadGuiche) throws ApplicationBusinessException, ApplicationBusinessException {
		getAelCadastroGuicheRN().inserir(aelCadGuiche);
	}

	@Override
	public void alterarAelCadGuiche(final AelCadGuiche aelCadGuiche, String nomeMicrocomputador) throws ApplicationBusinessException,
			ApplicationBusinessException {
		getAelCadastroGuicheRN().alterar(aelCadGuiche, nomeMicrocomputador);
	}

	@Override
	public void excluirAelCadGuiche(final Short aelCadGuiche) throws ApplicationBusinessException {
		getAelCadastroGuicheRN().excluir(aelCadGuiche);
	}

	@Override
	public AelMovimentoGuiche criarAelMovimentoGuiche(final Short cguSeq, String nomeMicrocomputador) throws ApplicationBusinessException {
		return getAelMovimentoGuicheRN().criarAelMovimentoGuiche(cguSeq, nomeMicrocomputador);
	}

	@Override
	public List<AelGrpTxtPadraoDiags> pesquisarAelGrpTxtPadraoDiags(final Short seq, final String descricao, final DominioSituacao situacao) {
		return getAelGrpTxtPadraoDiagsDAO().pesquisarAelGrpTxtPadraoDiags(seq, descricao, situacao);
	}

	@Override
	public List<AelGrpTxtPadraoDiags> pesquisarAelGrpTxtPadraoDiags(final String filtro, final DominioSituacao situacao) {
		return getAelGrpTxtPadraoDiagsDAO().pesquisarAelGrpTxtPadraoDiags(filtro, situacao);
	}

	@Override
	public Long pesquisarAelGrpTxtPadraoDiagsCount(final String filtro, final DominioSituacao situacao) {
		return getAelGrpTxtPadraoDiagsDAO().pesquisarAelGrpTxtPadraoDiagsCount(filtro, situacao);
	}

	@Override
	public AelGrpTxtPadraoDiags obterAelGrpTxtPadraoDiags(final Short seq) {
		return getAelGrpTxtPadraoDiagsDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public AelTextoPadraoDiags obterAelTextoPadraoDiags(final AelTextoPadraoDiagsId aelTextoPadraoDiagsId) {
		return getAelTextoPadraoDiagsDAO().obterPorChavePrimaria(aelTextoPadraoDiagsId);
	}

	@Override
	public AelGrpDiagLacunas obterAelGrpDiagLacunas(final AelGrpDiagLacunasId aelGrpDiagLacunasId) {
		return getAelGrpDiagLacunasDAO().obterPorChavePrimaria(aelGrpDiagLacunasId);
	}

	@Override
	public AelTxtDiagLacunas obterAelTxtDiagLacunas(final AelTxtDiagLacunasId aelTxtDiagLacunasId) {
		return getAelTxtDiagLacunasDAO().obterPorChavePrimaria(aelTxtDiagLacunasId, true, AelTxtDiagLacunas.Fields.AEL_GRP_DIAG_LACUNAS, AelTxtDiagLacunas.Fields.SERVIDOR);
	}

	@Override
	public List<AelGrpTxtPadraoMacro> pesquisarGrupoTextoPadraoMacro(final Short codigo, final String descricao,
			final DominioSituacao situacao) {
		return getAelGrpTxtPadraoMacroDAO().pesquisarGrupoTextoPadraoMacro(codigo, descricao, situacao);
	}

	@Override
	public List<AelGrpTxtPadraoMacro> pesquisarGrupoTextoPadraoMacro(final String filtro, final DominioSituacao situacao) {
		return getAelGrpTxtPadraoMacroDAO().pesquisarGrupoTextoPadraoMacro(filtro, situacao);
	}

	@Override
	public Long pesquisarGrupoTextoPadraoMacroCount(final String filtro, final DominioSituacao situacao) {
		return getAelGrpTxtPadraoMacroDAO().pesquisarGrupoTextoPadraoMacroCount(filtro, situacao);
	}

	@Override
	public AelGrpTxtPadraoMacro obterAelGrpTxtPadraoMacro(final Short seq) {
		return getAelGrpTxtPadraoMacroDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public void alterarAelGrpTxtPadraoMacro(final AelGrpTxtPadraoMacro aelGrpTxtPadraoMacro) throws BaseException {
		getTextoPadraoMacroscopiaON().persistirAelGrpTxtPadraoMacro(aelGrpTxtPadraoMacro);
	}

	@Override
	public void excluirAelGrpTxtPadraoMacro(final Short seq) throws BaseException {
		getTextoPadraoMacroscopiaON().removerAelGrpTxtPadraoMacro(seq);
	}

	@Override
	public void inserirAelGrpTxtPadraoMacro(final AelGrpTxtPadraoMacro aelGrpTxtPadraoMacro) throws BaseException {
		getTextoPadraoMacroscopiaON().persistirAelGrpTxtPadraoMacro(aelGrpTxtPadraoMacro);
	}

	@Override
	public List<AelTextoPadraoMacro> pesquisarTextoPadraoMacroPorAelGrpTxtPadraoMacro(final Short seqAelGrpTxtPadraoMacro) {
		return getAelTextoPadraoMacroDAO().pesquisarTextoPadraoMacroPorAelGrpTxtPadraoMacro(seqAelGrpTxtPadraoMacro);
	}

	@Override
	public List<AelTextoPadraoMacro> pesquisarTextoPadraoMacroscopia(final AelGrpTxtPadraoMacro aelGrpTxtPadraoMacros, final String filtro,
			final DominioSituacao indSituacao) {
		return getAelTextoPadraoMacroDAO().pesquisarTextoPadraoMacroscopia(aelGrpTxtPadraoMacros, filtro, indSituacao);
	}

	@Override
	public Long pesquisarTextoPadraoMacroscopiaCount(final AelGrpTxtPadraoMacro aelGrpTxtPadraoMacros, final String filtro,
			final DominioSituacao indSituacao) {
		return getAelTextoPadraoMacroDAO().pesquisarTextoPadraoMacroscopiaCount(aelGrpTxtPadraoMacros, filtro, indSituacao);
	}

	@Override
	public void alterarAelTextoPadraoMacro(final AelTextoPadraoMacro aelTextoPadraoMacro) throws BaseException {
		getTextoPadraoMacroscopiaON().persistirAelTextoPadraoMacro(aelTextoPadraoMacro);
	}

	@Override
	public void inserirAelTextoPadraoMacro(final AelTextoPadraoMacro aelTextoPadraoMacro) throws BaseException {
		getTextoPadraoMacroscopiaON().persistirAelTextoPadraoMacro(aelTextoPadraoMacro);
	}

	@Override
	public void excluirAelTextoPadraoMacro(final AelTextoPadraoMacroId id) throws BaseException {
		getTextoPadraoMacroscopiaON().removerAelTextoPadraoMacro(id);
	}

	@Override
	public AelTextoPadraoMacro obterAelTextoPadraoMacro(final AelTextoPadraoMacroId aelTextoPadraoMacroId) {
		return getAelTextoPadraoMacroDAO().obterPorChavePrimaria(aelTextoPadraoMacroId);
	}

	@Override
	@Secure("#{s:hasPermission('manterTextoPadraoMacroscopia','pesquisar')}")
	public List<AelGrpMacroLacuna> pesquisarAelGrpMacroLacunaPorTextoPadraoMacro(final Short aelTextoPadraoMacroLubSeq,
			final Short aelTextoPadraoMacroSeqp, final DominioSituacao indSituacao) {
		return getAelGrpMacroLacunaDAO().pesquisarAelGrpMacroLacunaPorTextoPadraoMacro(aelTextoPadraoMacroLubSeq, aelTextoPadraoMacroSeqp, indSituacao);
	}

	@Override
	public void alterarAelGrpMacroLacuna(final AelGrpMacroLacuna aelGrpMacroLacuna) throws BaseException {
		getTextoPadraoMacroscopiaON().persistirAelGrpMacroLacuna(aelGrpMacroLacuna);
	}

	@Override
	public void inserirAelGrpMacroLacuna(final AelGrpMacroLacuna aelGrpMacroLacuna) throws BaseException {
		getTextoPadraoMacroscopiaON().persistirAelGrpMacroLacuna(aelGrpMacroLacuna);
	}

	@Override
	public void excluirAelGrpMacroLacuna(final AelGrpMacroLacunaId id) throws BaseException {
		getTextoPadraoMacroscopiaON().removerAelGrpMacroLacuna(id);
	}

	@Override
	public AelGrpMacroLacuna obterAelGrpMacroLacuna(final AelGrpMacroLacunaId aelGrpMacroLacunaId) {
		return getAelGrpMacroLacunaDAO().obterPorChavePrimaria(aelGrpMacroLacunaId);
	}

	@Override
	@Secure("#{s:hasPermission('manterTextoPadraoMacroscopia','pesquisar')}")
	public List<AelTxtMacroLacuna> pesquisarAelTxtMacroLacunaPorAelGrpMacroLacuna(final AelGrpMacroLacuna aelGrpMacroLacuna, final DominioSituacao indSituacao) {
		return getAelTxtMacroLacunaDAO().pesquisarAelTxtMacroLacunaPorAelGrpMacroLacuna(aelGrpMacroLacuna, indSituacao);
	}

	@Override
	public void alterarAelTxtMacroLacuna(final AelTxtMacroLacuna aelTxtMacroLacuna) throws BaseException {
		getTextoPadraoMacroscopiaON().persistirAelTxtMacroLacuna(aelTxtMacroLacuna);
	}

	@Override
	public void inserirAelTxtMacroLacuna(final AelTxtMacroLacuna aelTxtMacroLacuna) throws BaseException {
		getTextoPadraoMacroscopiaON().persistirAelTxtMacroLacuna(aelTxtMacroLacuna);
	}

	@Override
	public AelTxtMacroLacuna obterAelTxtMacroLacuna(final AelTxtMacroLacunaId aelTxtMacroLacunaId) {
		return getAelTxtMacroLacunaDAO().obterPorChavePrimaria(aelTxtMacroLacunaId);
	}

	@Override
	public void excluirAelTxtMacroLacuna(final AelTxtMacroLacunaId id) throws BaseException {
		getTextoPadraoMacroscopiaON().removerAelTxtMacroLacuna(id);
	}

	@Override
	public List<AelTextoPadraoDiags> pesquisarTextoPadraoDiaguinosticoPorAelGrpTxtPadraoDiags(final Short seqAelGrpTxtPadraoDiag) {
		return getAelTextoPadraoDiagsDAO().pesquisarTextoPadraoDiaguinosticoPorAelGrpTxtPadraoDiags(seqAelGrpTxtPadraoDiag);
	}

	@Override
	public List<AelTextoPadraoDiags> pesquisarTextoPadraoDiaguinosticoPorAelGrpTxtPadraoDiags(final Short seqAelGrpTxtPadraoDiag,
			final String filtro, final DominioSituacao situacao) {
		return getAelTextoPadraoDiagsDAO().pesquisarTextoPadraoDiaguinosticoPorAelGrpTxtPadraoDiags(seqAelGrpTxtPadraoDiag, filtro,
				situacao);
	}

	@Override
	public Long pesquisarTextoPadraoDiaguinosticoPorAelGrpTxtPadraoDiagsCount(final Short seqAelGrpTxtPadraoDiag, final String filtro,
			final DominioSituacao situacao) {
		return getAelTextoPadraoDiagsDAO().pesquisarTextoPadraoDiaguinosticoPorAelGrpTxtPadraoDiagsCount(seqAelGrpTxtPadraoDiag, filtro,
				situacao);
	}

	@Override
	public void persistirAelGrpTxtPadraoDiags(final AelGrpTxtPadraoDiags aelGrpTxtPadraoDiags) throws BaseException {
		this.getTextoPadraoDiagnosticoON().persistirAelGrpTxtPadraoDiags(aelGrpTxtPadraoDiags);
	}

	@Override
	public void excluirAelGrpTxtPadraoDiags(final Short seq) throws BaseException {
		this.getTextoPadraoDiagnosticoON().removerAelGrpTxtPadraoDiags(seq);
	}

	@Override
	public List<AelGrpTxtPadraoMicro> pesquisarGrupoTextoPadraoMicro(final Short seq, final String descricao, final DominioSituacao situacao) {
		return getAelGrpTxtPadraoMicroDAO().pesquisarGrupoTextoPadraoMicro(seq, descricao, situacao);
	}

	@Override
	public List<AelTopografiaGrupoCidOs> listarTopografiaGrupoCidOsNodosRaiz() {
		return getAelTopografiaGruposCidOsDAO().listarTopografiaGrupoCidOsNodosRaiz();
	}

	@Override
	public AelGrpTxtPadraoMicro obterAelGrpTxtPadraoMicro(final Short seq) {
		return getAelGrpTxtPadraoMicroDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public void inserirAelGrpTxtPadraoMicro(final AelGrpTxtPadraoMicro aelGrpTxtPadraoMicro) throws BaseException {
		getTextoPadraoMicroscopiaON().persistirAelGrpTxtPadraoMicro(aelGrpTxtPadraoMicro);
	}

	@Override
	public void excluirAelGrpTxtPadraoMicro(final Short seq) throws BaseException {
		getTextoPadraoMicroscopiaON().removerAelGrpTxtPadraoMicro(seq);
	}

	@Override
	public void alterarAelGrpTxtPadraoMicro(final AelGrpTxtPadraoMicro aelGrpTxtPadraoMicro) throws BaseException {
		getTextoPadraoMicroscopiaON().persistirAelGrpTxtPadraoMicro(aelGrpTxtPadraoMicro);
	}

	@Override
	public List<AelTextoPadraoMicro> pesquisarTextoPadraoMicroPorAelGrpTxtPadraoMicro(final Short seqAelGrpTxtPadraoMicro) {
		return getAelTextoPadraoMicroDAO().pesquisarTextoPadraoMicroPorAelGrpTxtPadraoMicro(seqAelGrpTxtPadraoMicro);
	}

	@Override
	public AelTextoPadraoMicro obterAelTextoPadraoMicro(final AelTextoPadraoMicroId aelTextoPadraoMicroId) {
		return getAelTextoPadraoMicroDAO().obterPorChavePrimaria(aelTextoPadraoMicroId);
	}

	@Override
	public void alterarAelTextoPadraoMicro(final AelTextoPadraoMicro aelTextoPadraoMicro) throws BaseException {
		getTextoPadraoMicroscopiaON().persistirAelTextoPadraoMicro(aelTextoPadraoMicro);
	}

	@Override
	public void inserirAelTextoPadraoMicro(final AelTextoPadraoMicro aelTextoPadraoMicro) throws BaseException {
		getTextoPadraoMicroscopiaON().persistirAelTextoPadraoMicro(aelTextoPadraoMicro);
	}

	@Override
	public void excluirAelTextoPadraoMicro(final AelTextoPadraoMicroId id) throws BaseException {
		getTextoPadraoMicroscopiaON().removerAelTextoPadraoMicro(id);
	}

	@Override
	public List<AelGrpMicroLacuna> pesquisarAelGrpMicroLacunaPorTextoPadraoMicro(final Short aelTextoPadraoMicroLubSeq,
			final Short aelTextoPadraoMicroSeqp) {
		return getAelGrpMicroLacunaDAO().pesquisarAelGrpMicroLacunaPorTextoPadraoMicro(aelTextoPadraoMicroLubSeq, aelTextoPadraoMicroSeqp);
	}

	@Override
	public AelGrpMicroLacuna obterAelGrpMicroLacuna(final AelGrpMicroLacunaId aelGrpMicroLacunaId) {
		return getAelGrpMicroLacunaDAO().obterPorChavePrimaria(aelGrpMicroLacunaId);
	}

	@Override
	public void alterarAelGrpMicroLacuna(final AelGrpMicroLacuna aelGrpMicroLacuna) throws BaseException {
		getTextoPadraoMicroscopiaON().persistirAelGrpMicroLacuna(aelGrpMicroLacuna);
	}

	@Override
	public void inserirAelGrpMicroLacuna(final AelGrpMicroLacuna aelGrpMicroLacuna) throws BaseException {
		getTextoPadraoMicroscopiaON().persistirAelGrpMicroLacuna(aelGrpMicroLacuna);
	}

	@Override
	public void excluirAelGrpMicroLacuna(final AelGrpMicroLacunaId id) throws BaseException {
		getTextoPadraoMicroscopiaON().removerAelGrpMicroLacuna(id);
	}

	@Override
	public List<AelTxtMicroLacuna> pesquisarAelTxtMicroLacunaPorAelGrpMicroLacuna(final AelGrpMicroLacuna aelGrpMicroLacuna) {
		return getAelTxtMicroLacunaDAO().pesquisarAelTxtMicroLacunaPorAelGrpMicroLacuna(aelGrpMicroLacuna);
	}

	@Override
	public AelTxtMicroLacuna obterAelTxtMicroLacuna(final AelTxtMicroLacunaId aelTxtMicroLacunaId) {
		return getAelTxtMicroLacunaDAO().obterPorChavePrimaria(aelTxtMicroLacunaId);
	}

	@Override
	public void alterarAelTxtMicroLacuna(final AelTxtMicroLacuna aelTxtMicroLacuna) throws BaseException {
		getTextoPadraoMicroscopiaON().persistirAelTxtMicroLacuna(aelTxtMicroLacuna);
	}

	@Override
	public void inserirAelTxtMicroLacuna(final AelTxtMicroLacuna aelTxtMicroLacuna) throws BaseException {
		getTextoPadraoMicroscopiaON().persistirAelTxtMicroLacuna(aelTxtMicroLacuna);
	}

	@Override
	public void excluirAelTxtMicroLacuna(final AelTxtMicroLacuna aelTxtMicroLacuna) throws BaseException {
		getTextoPadraoMicroscopiaON().removerAelTxtMicroLacuna(aelTxtMicroLacuna);
	}

	@Override
	public List<AelPatologista> listarPatologistas(final Integer firstResult, final Integer maxResult, final Integer seq,
			final String nome, final DominioFuncaoPatologista funcao, final Boolean permiteLibLaudo, final DominioSituacao situacao,
			final RapServidoresId servidor, final String nomeParaLaudo) {
		return getAelPatologistaDAO().listarPatologistas(firstResult, maxResult, seq, nome, funcao, permiteLibLaudo, situacao, servidor,
				nomeParaLaudo);
	}

	@Override
	public void persistirIdentificacaoGuiche(final AelCadGuiche guiche, final AghUnidadesFuncionais unidade,
			final DominioSituacao situacao, String nomeMicrocomputador) throws BaseException {
		getIdentificaGuicheON().persistirIdentificacaoGuiche(guiche, unidade, situacao, nomeMicrocomputador);
	}

	@Override
	public Long listarPatologistasCount(final Integer seq, final String nome, final DominioFuncaoPatologista funcao,
			final Boolean permiteLibLaudo, final DominioSituacao situacao, final RapServidoresId servidor, final String nomeParaLaudo) {
		return getAelPatologistaDAO().listarPatologistasCount(seq, nome, funcao, permiteLibLaudo, situacao, servidor, nomeParaLaudo);
	}

	@Override
	public AelPatologista obterPatologistaPorChavePrimaria(final Integer seq) {
		return getAelPatologistaDAO().obterPorChavePrimaria(seq);
	}
	
	@Override
	public AelPatologista obterPatologista(final Integer seq) {
		return getAelPatologistaDAO().obterPatologista(seq);
	}

	@Override
	public AelPatologista obterAelPatologistaAtivoPorServidorEFuncao(final RapServidores servidor, final DominioFuncaoPatologista... funcao) {
		return getAelPatologistaDAO().obterAelPatologistaAtivoPorServidorEFuncao(servidor, funcao);
	}

	@Override
	public List<AelPatologista> listarPatologistasPorCodigoNomeFuncao(final String filtro, final DominioFuncaoPatologista... funcao) {
		return getAelPatologistaDAO().listarPatologistasPorCodigoNomeFuncao(filtro, funcao);
	}

	@Override
	public Long listarPatologistasPorCodigoNomeFuncaoCount(final String filtro, final DominioFuncaoPatologista... funcao) {
		return getAelPatologistaDAO().listarPatologistasPorCodigoNomeFuncaoCount(filtro, funcao);
	}

	@Override
	public List<AelPatologista> listarPatologistasAtivosPorCodigoNomeFuncao(final String filtro, final DominioFuncaoPatologista... funcao) {
		return getAelPatologistaDAO().listarPatologistasAtivosPorCodigoNomeFuncao(filtro, funcao);
	}

	@Override
	public Long listarPatologistasAtivosPorCodigoNomeFuncaoCount(final String filtro, final DominioFuncaoPatologista... funcao) {
		return getAelPatologistaDAO().listarPatologistasAtivosPorCodigoNomeFuncaoCount(filtro, funcao);
	}

	@Override
	public void persistirPatologista(final AelPatologista patologista) throws BaseException {
		getAelPatologistaRN().persistir(patologista);
	}

	@Override
	public void excluirPatologista(final Integer seq) throws BaseException {
		getAelPatologistaRN().excluir(seq);
	}

	@Override
	public AelPatologista clonarPatologista(final AelPatologista patologista) throws BaseException {
		return getAelPatologistaRN().clonarPatologista(patologista);
	}

	@Override
	public List<AelConfigExLaudoUnico> listarConfigExames(ConsultaConfigExamesVO consulta) {
		return getAelConfigExLaudoUnicoDAO().listarConfigExames(consulta);
	}

	@Override
	public Long listarConfigExamesCount(ConsultaConfigExamesVO consulta) {
		return getAelConfigExLaudoUnicoDAO().listarConfigExamesCount(consulta);
	}

	@Override
	public List<AelItemConfigExame> listarItemConfigExames(final Integer firstResult, final Integer maxResult, final Integer lu2Seq) {
		return getAelItemConfigExameDAO().listarItemConfigExames(firstResult, maxResult, lu2Seq);
	}

	@Override
	public Long listarItemConfigExamesCount(final Integer lu2Seq) {
		return getAelItemConfigExameDAO().listarItemConfigExamesCount(lu2Seq);
	}

	@Override
	public List<AelExamesMaterialAnalise> listarExamesMaterialAnaliseUnfExecExames(final Object objPesquisa, final Short unfSeq) {
		return getAelExamesMaterialAnaliseDAO().listarExamesMaterialAnaliseUnfExecExames(objPesquisa, unfSeq);
	}

	@Override
	public AelConfigExLaudoUnico obterConfigExameLaudoUncioPorChavePrimaria(final Integer seq) {
		return getAelConfigExLaudoUnicoDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public AelConfigExLaudoUnico persistirConfigLaudoUnico(AelConfigExLaudoUnico config) throws BaseException {
		return getAelConfigExLaudoUnicoRN().persistir(config);
	}

	@Override
	public void excluirConfigLaudo(final Integer config) throws BaseException {
		getAelConfigExLaudoUnicoRN().excluir(config);
	}

	@Override
	public AelItemConfigExame clonarItemConfigExame(final AelItemConfigExame item) throws BaseException {
		return getAelItemConfigExameRN().clonarItemConfigExame(item);
	}

	@Override
	public void persistirItemConfigExame(final AelItemConfigExame item, final AelItemConfigExame itemOld, final Boolean inclusao)
			throws BaseException {
		getAelItemConfigExameRN().persistir(item, itemOld, inclusao);
	}

	@Override
	public void excluirItemConfigExame(final AelItemConfigExame item, final AelItemConfigExame itemOld) throws BaseException {
		getAelItemConfigExameRN().excluir(item, itemOld);
	}

	@Override
	public AelItemConfigExame obterItemConfigExame(final AelItemConfigExameId id) {
		return getAelItemConfigExameDAO().obterPorChavePrimaria(id);
	}

	@Override
	public void persistirAelTextoPadraoDiags(final AelTextoPadraoDiags aelTextoPadraoDiags) throws BaseException {
		this.getTextoPadraoDiagnosticoON().persistirAelTextoPadraoDiags(aelTextoPadraoDiags);
	}

	@Override
	public void excluirAelTextoPadraoDiags(final AelTextoPadraoDiagsId id) throws BaseException {
		this.getTextoPadraoDiagnosticoON().removerAelTextoPadraoDiags(id);
	}

	@Override
	public void persistirAelGrpDiagLacunas(final AelGrpDiagLacunas aelGrpDiagLacunas) throws BaseException {
		this.getTextoPadraoDiagnosticoON().persistirAelGrpDiagsLacuna(aelGrpDiagLacunas);
	}

	@Override
	public void excluirAelGrpDiagLacunas(final AelGrpDiagLacunasId id) throws BaseException {
		this.getTextoPadraoDiagnosticoON().removerAelGrpDiagsLacuna(id);
	}

	@Override
	@Secure("#{s:hasPermission('manterTextoPadraoDiagnostico','pesquisar')}")
	public List<AelGrpDiagLacunas> pesquisarAelGrpDiagLacunasPorTextoPadraoDiags(final Short aelTextoPadraoDiagsLuhSeq,
			final Short aelTextoPadraoDiagsSeqp, final DominioSituacao indSituacao) {
		return getAelGrpDiagLacunasDAO().pesquisarAelGrpDiagLacunasPorTextoPadraoDiags(aelTextoPadraoDiagsLuhSeq, aelTextoPadraoDiagsSeqp, indSituacao);
	}

	@Override
	public void persistirAelTxtDiagLacunas(final AelTxtDiagLacunas aelTxtDiagLacunas) throws BaseException {
		getTextoPadraoDiagnosticoON().persistirAelTxtDiagLacunas(aelTxtDiagLacunas);
	}

	@Override
	@Secure("#{s:hasPermission('manterTextoPadraoDiagnostico','pesquisar')}")
	public List<AelTxtDiagLacunas> pesquisarAelTxtDiagLacunasPorAelGrpDiagLacunas(final AelGrpDiagLacunas aelGrpDiagLacunas, final DominioSituacao indSituacao) {
		return getAelTxtDiagLacunasDAO().pesquisarAelTxtDiagLacunasPorAelGrpDiagLacunas(aelGrpDiagLacunas, indSituacao);
	}

	@Override
	public void excluirAelTxtDiagLacunas(final AelTxtDiagLacunas aelTxtDiagLacunas) throws BaseException {
		this.getTextoPadraoDiagnosticoON().removerAelTxtDiagsLacuna(aelTxtDiagLacunas);
	}

	@Override
	public List<AelPatologista> pesquisarPatologistas(final Object valor) {
		return getAelPatologistaDAO().pesquisarPatologistas(valor);
	}

	@Override
	public List<AelPatologista> pesquisarPatologistasPorFuncao(final Object valor, final DominioFuncaoPatologista[] funcao) {
		return getAelPatologistaDAO().pesquisarPatologistasPorFuncao(valor, funcao);
	}

	@Override
	public List<AelConfigExLaudoUnico> pesquisarAelConfigExLaudoUnico(final String orderProperty, final String filtro) {
		return getAelConfigExLaudoUnicoDAO().pesquisarAelConfigExLaudoUnico(orderProperty, filtro);
	}

	@Override
	public Long pesquisarAelConfigExLaudoUnicoCount(final String orderProperty, final String filtro) {
		return getAelConfigExLaudoUnicoDAO().pesquisarAelConfigExLaudoUnicoCount(orderProperty, filtro);
	}
	
	@Override
	public Long pesquisarAelConfigExLaudoUnicoCount(final String filtro) {
		return getAelConfigExLaudoUnicoDAO().pesquisarAelConfigExLaudoUnicoCount(filtro);
	}

	@Override
	public List<AelPatologista> pesquisarPatologistasPorNomeESituacao(final Object parametro, final DominioSituacao indSituacao,
			final Integer max) {
		return getAelPatologistaDAO().pesquisarPatologistasPorNomeESituacao(parametro, indSituacao, max);
	}

	@Override
	public List<VAelApXPatologiaAghu> pesquisarVAelApXPatologiaAghu(final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc, final AelPatologista residenteResp,
			final DominioSituacaoExamePatologia situacaoExmAnd, final Date dtDe, final Date dtAte, final AelPatologista patologistaResp,
			final AelConfigExLaudoUnico exame, final Long numeroAp, final MedicoSolicitanteVO medicoSolic, final AelExameAp material,
			final DominioConvenioExameSituacao convenio, final DominioSimNao laudoImpresso) throws BaseException {

		return getVAelApXPatologiaAghuON().pesquisarVAelApXPatologiaAghu(firstResult, maxResults, orderProperty, asc, residenteResp,
				situacaoExmAnd, dtDe, dtAte, patologistaResp, exame, numeroAp, medicoSolic, material, convenio, laudoImpresso, null, null,
				null, null, null, null, null, false);
	}

	@Override
	public List<VAelApXPatologiaAghu> pesquisarVAelApXPatologiaAghu(final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc, final AelPatologista residenteResp,
			final DominioSituacaoExamePatologia situacaoExmAnd, final Date dtDe, final Date dtAte, final AelPatologista patologistaResp,
			final AelConfigExLaudoUnico exame, final Long numeroAp) throws BaseException {

		return getVAelApXPatologiaAghuON().pesquisarVAelApXPatologiaAghu(firstResult, maxResults, orderProperty, asc, residenteResp,
				situacaoExmAnd, dtDe, dtAte, patologistaResp, exame, numeroAp, null, null, null, null, null, null, null, null, null, null, null,
				false);
	}

	@Override
	public List<VAelApXPatologiaAghu> pesquisarVAelApXPatologiaAghu(final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, final AelNomenclaturaGenerics nomenclaturaGenerica,
			final AelNomenclaturaEspecs nomenclaturaEspecifica, final AelTopografiaSistemas topografiaSistema,
			final AelTopografiaAparelhos topografiaAparelho, final AelPatologista residenteResp, final AelPatologista patologistaResp,
			final DominioSimNao neoplasiaMaligna, final DominioSimNaoNaoAplicavel margemComprometida, final DominioSimNao biopsia, 
			final Date dtDe, final Date dtAte,  final AelConfigExLaudoUnico exame) throws BaseException {

		return getVAelApXPatologiaAghuON().pesquisarVAelApXPatologiaAghu(firstResult, maxResult, orderProperty, asc, residenteResp, null,
				dtDe, dtAte, patologistaResp, exame, null, null, null, null, null, nomenclaturaGenerica, nomenclaturaEspecifica,
				topografiaSistema, topografiaAparelho, neoplasiaMaligna, margemComprometida, biopsia, false);
	}


	@Override
	public Integer pesquisarVAelApXPatologiaAghuCount(final AelPatologista residenteResp,
			final DominioSituacaoExamePatologia situacaoExmAnd, final Date dtDe, final Date dtAte, final AelPatologista patologistaResp,
			final AelConfigExLaudoUnico exame, final Long numeroAp, final MedicoSolicitanteVO medicoSolic, final AelExameAp material,
			final DominioConvenioExameSituacao convenio, final DominioSimNao laudoImpresso) {

		return getVAelApXPatologiaAghuON().pesquisarVAelApXPatologiaAghuCount(residenteResp, situacaoExmAnd, dtDe, dtAte, patologistaResp,
				exame, numeroAp, medicoSolic, material, convenio, laudoImpresso, null, null, null, null, null, null, null);
	}

	@Override
	public Integer pesquisarVAelApXPatologiaAghuCount(final AelPatologista residenteResp,
			final DominioSituacaoExamePatologia situacaoExmAnd, final Date dtDe, final Date dtAte, final AelPatologista patologistaResp,
			final AelConfigExLaudoUnico exame, final Long numeroAp) {

		return getVAelApXPatologiaAghuON().pesquisarVAelApXPatologiaAghuCount(residenteResp, situacaoExmAnd, dtDe, dtAte, patologistaResp,
				exame, numeroAp, null, null, null, null, null, null, null, null, null, null, null);
	}

	@Override
	public Integer pesquisarVAelApXPatologiaAghuCount(final AelNomenclaturaGenerics nomenclaturaGenerica,
			final AelNomenclaturaEspecs nomenclaturaEspecifica, final AelTopografiaSistemas topografiaSistema,
			final AelTopografiaAparelhos topografiaAparelho, final AelPatologista residenteResp, final AelPatologista patologistaResp,
			final DominioSimNao neoplasiaMaligna, final DominioSimNaoNaoAplicavel margemComprometida, final DominioSimNao biopsia, 
			final Date dtDe, final Date dtAte, final AelConfigExLaudoUnico exame) {

		return getVAelApXPatologiaAghuON().pesquisarVAelApXPatologiaAghuCount(residenteResp, null, dtDe, dtAte, patologistaResp, exame,
				null, null, null, null, null, nomenclaturaGenerica, nomenclaturaEspecifica, topografiaSistema, topografiaAparelho,
				neoplasiaMaligna, margemComprometida, biopsia);
	}
	
	@Override
	public List<MedicoSolicitanteVO> pesquisarMedicosSolicitantesVO(final String filtro, final String siglaConselhoProfissional) {
		return getVAelApXPatologiaAghuDAO().pesquisarMedicosSolicitantesVO(filtro, siglaConselhoProfissional);
	}

	@Override
	public Integer pesquisarMedicosSolicitantesVOCount(final String filtro, final String siglaConselhoProfissional) {
		return getVAelApXPatologiaAghuDAO().pesquisarMedicosSolicitantesVOCount(filtro, siglaConselhoProfissional);
	}

	@Override
	public List<AelExameAp> pesquisarAelExameApPorMateriais(final String filtro) {
		return getAelExameApDAO().pesquisarAelExameApPorMateriais(filtro);
	}

	@Override
	public Long pesquisarAelExameApPorMateriaisCount(final String filtro) {
		return getAelExameApDAO().pesquisarAelExameApPorMateriaisCount(filtro);
	}

	@Override
	public AelAnatomoPatologico obterAelAnatomoPatologico(final Long seq) {
		return getAelAnatomoPatologicoDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public AelApXPatologista obterAelApXPatologistaPorSeqAnatoPatologicoMatriculaEFuncao(final Long lumSeq, final Integer matricula,
			final DominioFuncaoPatologista[] funcao, final DominioSituacao situacao) {
		return getAelApXPatologistaDAO().obterAelApXPatologistaPorSeqAnatoPatologicoMatriculaEFuncao(lumSeq, matricula, funcao, situacao);
	}

	@Override
	//@Secure("#{s:hasPermission('trocarResidentePatologistaRealzExame','gravar')}")
	public void persistirAelApXPatologista(final AelApXPatologista apXPatologista) throws BaseException {
		this.getAelApXPatologistaON().persistirAelApXPatologista(apXPatologista);
	}

	@Override
	//@Secure("#{s:hasPermission('trocarResidentePatologistaRealzExame','excluir')}")
	public void excluirAelApXPatologistaPorPatologista(final Integer seqExcluir, final Long seq) throws ApplicationBusinessException {
		this.getAelApXPatologistaON().excluirAelApXPatologistaPorPatologista(seqExcluir, seq);
	}

	@Override
	public ConvenioExamesLaudosVO aelcBuscaConvLaud(final Integer atdSeq, final Integer atvSeq) {
		return getAelPatologistaRN().aelcBuscaConvLaud(atdSeq, atvSeq);
	}

	@Override
	public DominioSimNao aelcBuscaConvGrp(final Integer atdSeq, final Integer atvSeq) {
		return getAelPatologistaRN().aelcBuscaConvGrp(atdSeq, atvSeq);
	}

	@Override
	public List<AelNomenclaturaGenerics> pesquisarAelNomenclaturaGenerics(final String filtro, final DominioSituacao situacao) {
		return getAelNomenclaturaGenericsDAO().pesquisarAelNomenclaturaGenerics(filtro, situacao);
	}

	@Override
	public Long pesquisarAelNomenclaturaGenericsCount(final String filtro, final DominioSituacao situacao) {
		return getAelNomenclaturaGenericsDAO().pesquisarAelNomenclaturaGenericsCount(filtro, situacao);
	}

	@Override
	public List<AelNomenclaturaGenerics> pesquisarAelNomenclaturaGenerics(final Integer seq, final String filtro,
			final DominioSituacao situacao) {
		return getAelNomenclaturaGenericsDAO().pesquisarAelNomenclaturaGenerics(seq, filtro, situacao);
	}

	@Override
	public AelNomenclaturaGenerics obterAelNomenclaturaGenericsPorChavePrimaria(final Integer seq) {
		return getAelNomenclaturaGenericsDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public void alterarAelNomenclaturaGenerics(final AelNomenclaturaGenerics aelNomenclaturaGenerics) throws BaseException {
		getAelNomenclaturaGenericsRN().alterar(aelNomenclaturaGenerics);
	}

	@Override
	public void excluirAelNomenclaturaGenerics(final AelNomenclaturaGenerics aelNomenclaturaGenerics) throws BaseException {
		getAelNomenclaturaGenericsRN().excluir(aelNomenclaturaGenerics);
	}

	@Override
	public void inserirAelNomenclaturaGenerics(final AelNomenclaturaGenerics aelNomenclaturaGenerics) throws BaseException {
		getAelNomenclaturaGenericsRN().inserir(aelNomenclaturaGenerics);
	}

	@Override
	public List<AelNomenclaturaEspecs> pesquisarAelNomenclaturaEspecs(final String filtro, final DominioSituacao situacao,
			final AelNomenclaturaGenerics aelNomenclaturaGenerics) {
		return getAelNomenclaturaEspecsDAO().pesquisarAelNomenclaturaEspecs(filtro, situacao, aelNomenclaturaGenerics);
	}

	@Override
	public List<AelNomenclaturaEspecs> pesquisarAelNomenclaturaEspecsPorAelNomenclaturaGenerics(
			final AelNomenclaturaGenerics aelNomenclaturaGenerics) {
		return getAelNomenclaturaEspecsDAO().pesquisarAelNomenclaturaEspecsPorAelNomenclaturaGenerics(aelNomenclaturaGenerics);
	}

	@Override
	public AelNomenclaturaEspecs obterAelNomenclaturaEspecsPorChavePrimaria(final AelNomenclaturaEspecsId aelNomenclaturaEspecsId) {
		return getAelNomenclaturaEspecsDAO().obterPorChavePrimaria(aelNomenclaturaEspecsId);
	}

	@Override
	public Long pesquisarAelNomenclaturaEspecsCount(final String filtro, final DominioSituacao situacao,
			final AelNomenclaturaGenerics aelNomenclaturaGenerics) {
		return getAelNomenclaturaEspecsDAO().pesquisarAelNomenclaturaEspecsCount(filtro, situacao, aelNomenclaturaGenerics);
	}

	@Override
	public void alterarAelNomenclaturaEspecs(final AelNomenclaturaEspecs aelNomenclaturaEspecs) throws BaseException {
		getAelNomenclaturaEspecsRN().alterar(aelNomenclaturaEspecs);
	}

	@Override
	public void excluirAelNomenclaturaEspecs(final AelNomenclaturaEspecs aelNomenclaturaEspecs) throws BaseException {
		getAelNomenclaturaEspecsRN().excluir(aelNomenclaturaEspecs);
	}

	@Override
	public void inserirAelNomenclaturaEspecs(final AelNomenclaturaEspecs aelNomenclaturaEspecs) throws BaseException {
		getAelNomenclaturaEspecsRN().inserir(aelNomenclaturaEspecs);
	}

	@Override
	public List<AelTopografiaSistemas> pesquisarAelTopografiaSistemas(final String filtro, final DominioSituacao situacao) {
		return getAelTopografiaSistemasDAO().pesquisarAelTopografiaSistemas(filtro, situacao);
	}

	@Override
	public Long pesquisarAelTopografiaSistemasCount(final String filtro, final DominioSituacao situacao) {
		return getAelTopografiaSistemasDAO().pesquisarAelTopografiaSistemasCount(null, filtro, situacao);
	}

	@Override
	public List<AelTopografiaSistemas> pesquisarAelTopografiaSistemas(final Integer seq, final String filtro, final DominioSituacao situacao) {
		return getAelTopografiaSistemasDAO().pesquisarAelTopografiaSistemas(seq, filtro, situacao);
	}

	@Override
	public AelTopografiaSistemas obterAelTopografiaSistemasPorChavePrimaria(final Integer seq) {
		return getAelTopografiaSistemasDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public void alterarAelTopografiaSistemas(final AelTopografiaSistemas aelTopografiaSistemas) throws BaseException {
		getAelTopografiaSistemasRN().alterar(aelTopografiaSistemas);
	}

	@Override
	public void excluirAelTopografiaSistemas(final Integer seq) throws BaseException {
		getAelTopografiaSistemasRN().excluir(seq);
	}

	@Override
	public void inserirAelTopografiaSistemas(final AelTopografiaSistemas aelTopografiaSistemas) throws BaseException {
		getAelTopografiaSistemasRN().inserir(aelTopografiaSistemas);
	}

	@Override
	public List<AelTopografiaAparelhos> pesquisarAelTopografiaAparelhos(final String filtro, final DominioSituacao situacao,
			final AelTopografiaSistemas aelTopografiaSistemas) {
		return getAelTopografiaAparelhosDAO().pesquisarAelTopografiaAparelhos(filtro, situacao, aelTopografiaSistemas);
	}

	@Override
	public Long pesquisarAelTopografiaAparelhosCount(final String filtro, final DominioSituacao situacao,
			final AelTopografiaSistemas aelTopografiaSistemas) {
		return getAelTopografiaAparelhosDAO().pesquisarAelTopografiaAparelhosCount(filtro, situacao, aelTopografiaSistemas);
	}

	@Override
	public List<AelTopografiaAparelhos> pesquisarAelTopografiaAparelhos(final AelTopografiaSistemas aelTopografiaSistemas) {
		return getAelTopografiaAparelhosDAO().pesquisarAelTopografiaAparelhos(aelTopografiaSistemas);
	}

	@Override
	public AelTopografiaAparelhos obterAelTopografiaAparelhosPorChavePrimaria(final AelTopografiaAparelhosId id) {
		return getAelTopografiaAparelhosDAO().obterPorChavePrimaria(id);
	}

	@Override
	public void alterarAelTopografiaAparelhos(final AelTopografiaAparelhos aelTopografiaAparelhos) throws BaseException {
		getAelTopografiaAparelhosRN().alterar(aelTopografiaAparelhos);
	}

	@Override
	public void excluirAelTopografiaAparelhos(final AelTopografiaAparelhosId id) throws BaseException {
		getAelTopografiaAparelhosRN().excluir(id);
	}

	@Override
	public void inserirAelTopografiaAparelhos(final AelTopografiaAparelhos aelTopografiaAparelhos) throws BaseException {
		getAelTopografiaAparelhosRN().inserir(aelTopografiaAparelhos);
	}

	@Override
	public List<AelKitIndiceBloco> pesquisarAelKitIndiceBloco(final Integer seq, final String descricao, final DominioSituacao situacao) {
		return getAelKitIndiceBlocoDAO().pesquisarAelKitIndiceBloco(seq, descricao, situacao);
	}

	@Override
	public Long pesquisarAelKitIndiceBlocoCount(final Integer seq, final String filtro, final DominioSituacao situacao) {
		return getAelKitIndiceBlocoDAO().pesquisarAelKitIndiceBlocoCount(seq, filtro, situacao);
	}

	@Override
	public AelKitIndiceBloco obterAelKitIndiceBlocoPorChavePrimaria(final Integer seq) {
		return getAelKitIndiceBlocoDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public void alterarAelKitIndiceBloco(final AelKitIndiceBloco aelKitIndiceBloco) throws BaseException {
		getAelKitIndiceBlocoRN().alterar(aelKitIndiceBloco);
	}

	@Override
	public void excluirAelKitIndiceBloco(final Integer aelKitIndiceBloco) throws BaseException {
		getAelKitIndiceBlocoRN().excluir(aelKitIndiceBloco);
	}

	@Override
	public void inserirAelKitIndiceBloco(final AelKitIndiceBloco aelKitItemIndiceBloco) throws BaseException {
		getAelKitIndiceBlocoRN().inserir(aelKitItemIndiceBloco);
	}

	@Override
	public List<AelKitItemIndiceBloco> pesquisarAelKitItemIndiceBloco(final AelKitIndiceBloco aelKitIndiceBloco) {
		return getAelKitItemIndiceBlocoDAO().pesquisarAelKitItemIndiceBloco(aelKitIndiceBloco);
	}

	@Override
	public AelKitItemIndiceBloco obterAelKitItemIndiceBlocoPorChavePrimaria(final AelKitItemIndiceBlocoId id) {
		return getAelKitItemIndiceBlocoDAO().obterPorChavePrimaria(id);
	}

	@Override
	public void alterarAelKitItemIndiceBloco(final AelKitItemIndiceBloco aelKitItemIndiceBloco) throws BaseException {
		getAelKitItemIndiceBlocoRN().alterar(aelKitItemIndiceBloco);
	}

	@Override
	public void excluirAelKitItemIndiceBloco(final AelKitItemIndiceBlocoId id) throws BaseException {
		getAelKitItemIndiceBlocoRN().excluir(id);
	}

	@Override
	public void inserirAelKitItemIndiceBloco(final AelKitItemIndiceBloco aelKitItemIndiceBloco) throws BaseException {
		getAelKitItemIndiceBlocoRN().inserir(aelKitItemIndiceBloco);
	}

	@Override
	public List<AelKitMatPatologia> pesquisarAelKitMatPatologia(final Integer seq, final String descricao, final DominioSituacao situacao) {
		return getAelKitMatPatologiaDAO().pesquisarAelKitMatPatologia(seq, descricao, situacao);
	}

	@Override
	public AelKitMatPatologia obterAelKitMatPatologiaPorChavePrimaria(final Integer seq) {
		return getAelKitMatPatologiaDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public void alterarAelKitMatPatologia(final AelKitMatPatologia aelKitMatPatologia) throws BaseException {
		getAelKitMatPatologiaRN().alterar(aelKitMatPatologia);
	}

	@Override
	public void excluirAelKitMatPatologia(final Integer seq) throws BaseException {
		getAelKitMatPatologiaRN().excluir(seq);
	}

	@Override
	public void inserirAelKitMatPatologia(final AelKitMatPatologia aelKitMatPatologia) throws BaseException {
		getAelKitMatPatologiaRN().inserir(aelKitMatPatologia);
	}

	@Override
	public List<AelKitItemMatPatologia> pesquisarAelKitItemMatPatologia(final AelKitMatPatologia aelKitMatPatologia) {
		return getAelKitItemMatPatologiaDAO().pesquisarAelKitItemMatPatologia(aelKitMatPatologia);
	}

	@Override
	public AelKitItemMatPatologia obterAelKitItemMatPatologiaPorChavePrimaria(final AelKitItemMatPatologiaId id) {
		return getAelKitItemMatPatologiaDAO().obterPorChavePrimaria(id);
	}

	@Override
	public void alterarAelKitItemMatPatologia(final AelKitItemMatPatologia aelKitItemMatPatologia) throws BaseException {
		getAelKitItemMatPatologiaRN().alterar(aelKitItemMatPatologia);
	}

	@Override
	public void excluirAelKitItemMatPatologia(final AelKitItemMatPatologiaId id) throws BaseException {
		getAelKitItemMatPatologiaRN().excluir(id);
	}

	@Override
	public void inserirAelKitItemMatPatologia(final AelKitItemMatPatologia aelKitItemMatPatologia) throws BaseException {
		getAelKitItemMatPatologiaRN().inserir(aelKitItemMatPatologia);
	}

	@Override
	public AelExameAp obterAelExameApPorChavePrimaria(final Long luxSeq) {
		return getAelExameApDAO().obterPorChavePrimaria(luxSeq);
	}
	
	@Override
	public AelExameAp obterAelExameApPorSeq(final Long luxSeq) {
		return getAelExameApDAO().obterAelExameApPorSeq(luxSeq);
	}

	@Override
	public AelExameAp obterAelExameApPorAelAnatomoPatologicos(final AelAnatomoPatologico aelAP) {
		return getAelExameApDAO().obterAelExameApPorAelAnatomoPatologicos(aelAP);
	}

	@Override
	public List<AelCestoPatologia> pesquisarAelCestoPatologia(final Integer seq, final String descricao, final String sigla,
			final DominioSituacao situacao) {
		return getAelCestoPatologiaDAO().pesquisarAelCestoPatologia(seq, descricao, sigla, situacao);
	}

	@Override
	public List<AelCestoPatologia> pesquisarAelCestoPatologia(final String filtro, final DominioSituacao situacao) {
		return getAelCestoPatologiaDAO().pesquisarAelCestoPatologia(filtro, situacao);
	}

	@Override
	public Long pesquisarAelCestoPatologiaCount(final String filtro, final DominioSituacao situacao) {
		return getAelCestoPatologiaDAO().pesquisarAelCestoPatologiaCount(filtro, situacao);
	}

	@Override
	public AelCestoPatologia obterAelCestoPatologiaPorChavePrimaria(final Integer seq) {
		return getAelCestoPatologiaDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public void alterarAelCestoPatologia(final AelCestoPatologia aelCestoPatologia) throws BaseException {
		getAelCestoPatologiaRN().alterar(aelCestoPatologia);
	}

	@Override
	public void inserirAelCestoPatologia(final AelCestoPatologia aelCestoPatologia) throws BaseException {
		getAelCestoPatologiaRN().inserir(aelCestoPatologia);
	}

	@Override
	public void persistirAelMaterialAp(final AelMaterialAp aelMaterialAp) throws BaseException {
		getAelMaterialApRN().persistirAelMaterialAp(aelMaterialAp);
	}
	
	@Override
	public void persistirAelMaterialAp(final AelMaterialAp aelMaterialAp, final AelMaterialAp aelMaterialApOld) throws BaseException {
		getAelMaterialApRN().persistirAelMaterialAp(aelMaterialAp, aelMaterialApOld);
	}

	@Override
	public void excluirAelMaterialAp(final AelMaterialAp aelMaterialAp) throws BaseException {
		getAelMaterialApRN().excluirAelMaterialAp(aelMaterialAp);
	}

	@Override
	public List<RelatorioMapaLaminasVO> pesquisarRelatorioMapaLaminasVO(final Date dtRelatorio, final AelCestoPatologia cesto) {
		return getAelLaminaApsON().pesquisarRelatorioMapaLaminasVO(dtRelatorio, cesto);
	}

	@Override
	public Date obterMaxCriadoEmPorLuxSeqEEtapasLaudo(final Long luxSeq, final DominioSituacaoExamePatologia etapasLaudo) {
		return getAelExtratoExameApDAO().obterMaxCriadoEmPorLuxSeqEEtapasLaudo(luxSeq, etapasLaudo);
	}

	@Override
	public List<AelExtratoExameAp> listarAelExtratoExameApPorLuxSeq(final Long luxSeq) {
		return getAelExtratoExameApDAO().listarAelExtratoExameApPorLuxSeq(luxSeq);
	}

	@Override
	@Secure("#{s:hasPermission('concluirReabrirMacroscopia','executar')}")
	public void concluirMacroscopiaAps(final AelExameAp exameAp, final AelMacroscopiaAps macroscopia) throws BaseException {
		getAelMacroscopiaApsON().concluirMacroscopiaAps(exameAp, macroscopia);
	}

	@Override
	public void validarMacroscopiaPreenchida(final AelMacroscopiaAps macroscopia) throws ApplicationBusinessException {
		getAelMacroscopiaApsON().validarMacroscopiaPreenchida(macroscopia);
	}

	@Override
	public void persistirAelMacroscopiaAps(final AelMacroscopiaAps macroscopia) throws BaseException {
		getAelMacroscopiaApsRN().persistir(macroscopia);
	}

	@Override
	public AelMacroscopiaAps obterAelMacroscopiaApsPorAelExameAps(final Long luxSeq) {
		return getAelMacroscopiaApsDAO().obterAelMacroscopiaApsPorAelExameAps(luxSeq);
	}

	@Override
	public void persistirAelDiagnosticoAps(final AelDiagnosticoAps diagnosticoAps) throws BaseException {
		getAelDiagnosticoApsRN().persistir(diagnosticoAps);
	}

	@Override
	@Secure("#{s:hasPermission('concluirReabrirDiagnostico','executar')}")
	public void concluirDiagnosticoAps(final AelExameAp exameAp, final AelDiagnosticoAps diagnostico,
			final List<AelTopografiaAps> listaTopografiaAp, final List<AelNomenclaturaAps> listaNomenclaturaAp,
			final List<AelLaminaAps> listaLaminaAp) throws BaseException {
		getAelDiagnosticoApsON().concluirDiagnosticoAps(exameAp, diagnostico, listaTopografiaAp, listaNomenclaturaAp, listaLaminaAp);
	}

	// @Override
	// public void validarDiagnosticoPreenchido(final AelDiagnosticoAps
	// diagnostico) throws ApplicationBusinessException {
	// getAelDiagnosticoApsON().validarDiagnosticoPreenchido(diagnostico);
	// }

	@Override
	public AelDiagnosticoAps obterAelDiagnosticoApsPorAelExameAps(final Long luxSeq) {

		// List<AelDiagnosticoAps> temp =
		// getAelDiagnosticoApsDAO().pesquisarAelDiagnosticoAps(3); //NOPMD

		return getAelDiagnosticoApsDAO().obterAelDiagnosticoApsPorAelExameAps(luxSeq);
	}

	@Override
	@Secure("#{s:hasPermission('imprimirRelatorioLaudoUnicoAP','imprimir')}")
	public List<RelatorioLaudoUnicoVO> obterRelatorioLaudoUnicoVO(final String nroAps, final AelConfigExLaudoUnico aelConfigExLaudoUnico)
			throws BaseException {
		return getRelatorioLaudoUnicoON().obterRelatorioLaudoUnicoVO(nroAps, aelConfigExLaudoUnico);
	}

	@BypassInactiveModule
	public AelpCabecalhoLaudoVO obterAelpCabecalhoLaudo(final Short unfSeq) throws ApplicationBusinessException {
		return getRelatorioLaudoUnicoON().obterAelpCabecalhoLaudo(unfSeq);
	}

	@Override
	public List<AelMaterialAp> obterAelMaterialApPorAelExameAps(final Long luxSeq) {
		return getAelMaterialApDAO().obterAelMaterialApPorAelExameAps(luxSeq);
	}

	@Override
	public AelDiagnosticoAps obterAelDiagnosticoApPorChavePrimaria(Long chavePrimaria) {
		return getAelDiagnosticoApsDAO().obterPorChavePrimaria(chavePrimaria);
	}

	@Override
	public AelMacroscopiaAps obterAelMacroscopiaApPorChavePrimaria(Long chavePrimaria) {
		return getAelMacroscopiaApsDAO().obterPorChavePrimaria(chavePrimaria);
	}

	@Override
	public void aelpAtualizaTela(final TelaLaudoUnicoVO telaVo) throws BaseException {
		getLaudoUnicoON().aelpAtualizaTela(telaVo);
	}

	@Override
	public AelItemSolicitacaoExameLaudoUnicoVO obterAelItemSolicitacaoExameLaudoUnicoVO(final AelExameAp aelExameAp,
			final boolean isPesqSolic) {
		return getLaudoUnicoON().obterAelItemSolicitacaoExameLaudoUnicoVO(aelExameAp, isPesqSolic);
	}

	@Override
	public List<AelKitMatPatologiaVO> listaMateriais(final Long luxSeq, DominioSituacaoExamePatologia etapaLaudo) {
		return getLaudoUnicoON().listaMateriais(luxSeq, etapaLaudo);
	}

	@Override
	public void atualizaMateriaisVO(List<AelKitMatPatologiaVO> listaVO) {
		getLaudoUnicoON().atualizaMateriaisVO(listaVO);
	}

	@Override
	public List<AelMaterialAp> listarAelMaterialApPorLuxSeqEOrdem(final Long luxSeq, Short ordem) {
		return getAelMaterialApDAO().listarAelMaterialApPorLuxSeqEOrdem(luxSeq, ordem);
	}

	@Override
	public List<AelMaterialAp> listarAelMaterialApPorLuxSeqEOrdemMaior(final Long luxSeq, Short ordem) {
		return getAelMaterialApDAO().listarAelMaterialApPorLuxSeqEOrdemMaior(luxSeq, ordem);
	}

	@Override
	public List<AelNotaAdicionalAp> obterListaNotasAdicionaisPeloExameApSeq(Long luxSeq) {
		return getAelNotaAdicionalApDAO().obterListaNotasAdicionaisPeloExameApSeq(luxSeq);
	}

	@Override
	public void persistirAelNotaAdicionalAp(final AelNotaAdicionalAp aelNotaAdicionalAp) throws BaseException {
		getAelNotaAdicionalApRN().persistirAelNotaAdicionalAp(aelNotaAdicionalAp);
	}

	@Override
	public void gravarNotasAdicionais(String notas, Long luxSeq) throws BaseException {
		this.getAelNotaAdicionalApON().gravarNotasAdicionais(notas, luxSeq);
	}

	@Override
	public List<AelTopografiaAps> listarTopografiaApPorLuxSeq(Long luxSeq) {
		return getAelTopografiaApsDAO().listarTopografiaApPorLuxSeq(luxSeq);
	}

	@Override
	public List<AelNomenclaturaAps> listarNomenclaturaApPorLuxSeq(Long luxSeq) {
		return getAelNomenclaturaApsDAO().listarNomenclaturaApPorLuxSeq(luxSeq);
	}

	@Override
	public List<AelPatologistaLaudoVO> listarPatologistaLaudo(Long luxSeq) {
		return getAelPatologistaApON().listarPatologistaLaudo(luxSeq);
	}

	@Override
	public List<AelPatologistaAps> listarPatologistaLaudoPorLuxSeqEOrdemMaior(final Long luxSeq, Short ordem) {
		return getAelPatologistaApDAO().listarPatologistaLaudoPorLuxSeqEOrdemMaior(luxSeq, ordem);
	}

	@Override
	public List<AelPatologistaAps> listarPatologistaLaudoPorLuxSeqEOrdem(final Long luxSeq, Short ordem) {
		return getAelPatologistaApDAO().listarPatologistaLaudoPorLuxSeqEOrdem(luxSeq, ordem);
	}
	
	public AelPatologista obterPatologistaPorServidor(final RapServidores servidor){
		return getAelPatologistaDAO().obterPatologistaAtivoPorServidor(servidor);
	}	

	@Override
	public void persistirAelPatologistaAps(AelPatologistaAps aelPatologistaAp) throws BaseException {
		getAelPatologistaApRN().persistirAelPatologistaAps(aelPatologistaAp);
	}

	@Override
	public void persistir(AelTopografiaAps topografiaAps) throws BaseException {
		getAelTopografiaApsRN().persistir(topografiaAps);
	}

	@Override
	public void excluir(AelTopografiaAps topografiaAps) throws BaseException {
		getAelTopografiaApsRN().excluir(topografiaAps);
	}

	@Override
	public void persistir(AelNomenclaturaAps nomenclaturaAps) throws BaseException {
		getAelNomenclaturaApsRN().persistir(nomenclaturaAps);
	}

	@Override
	public void excluir(AelNomenclaturaAps nomenclaturaAps) throws BaseException {
		getAelNomenclaturaApsRN().excluir(nomenclaturaAps);
	}

	@Override
	public AelTopografiaAps obterAelTopografiaApsPorChavePrimaria(Long seq) {
		return getAelTopografiaApsDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public AelNomenclaturaAps obterAelNomenclaturaApsPorChavePrimaria(Long seq) {
		return getAelNomenclaturaApsDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public void carregaPatologistas(TelaLaudoUnicoVO telaVo) throws BaseException {
		getLaudoUnicoON().carregaPatologistas(telaVo);
	}

	@Override
	public void atualizaMateriais(AelExameAp aelExameAp) throws BaseException {
		getLaudoUnicoON().atualizaMateriais(aelExameAp);
	}

	@Override
	public void atualizaInformacoesClinicas(TelaLaudoUnicoVO telaVo) throws BaseException {
		getLaudoUnicoON().atualizaInformacoesClinicas(telaVo);
	}

	@Override
	@Secure("#{s:hasPermission('assinarLaudoUnico','executar')}")
	public Boolean assinarReabrirLaudo(Long numeroAp, Long luxSeq, Integer lu2Seq, DominioSituacaoExamePatologia etapasLaudo,
			AelDiagnosticoAps diagnostico, List<AelKitMatPatologiaVO> listaMateriais, String nomeMicrocomputador) throws BaseException {
		return getLaudoUnicoON().assinarReabrirLaudo(numeroAp, luxSeq, lu2Seq, etapasLaudo, diagnostico, listaMateriais,
				nomeMicrocomputador);
	}

	@Override
	public void aelpAtualizaAel(Integer lu2Seq, Long luxSeq, String nomeMicrocomputador) throws BaseException {
		getLaudoUnicoON().aelpAtualizaAel(lu2Seq, luxSeq, nomeMicrocomputador);
	}

	@Override
	@Secure("#{s:hasPermission('assinarLaudoUnico','executar')}")
	public Boolean assinarLaudo(Long numeroAp, Long luxSeq, Integer lu2Seq, AelDiagnosticoAps diagnostico,
			List<AelKitMatPatologiaVO> listaMateriais, String nomeMicrocomputador) throws BaseException {
		return getLaudoUnicoON().assinarLaudo(numeroAp, luxSeq, lu2Seq, diagnostico, listaMateriais, nomeMicrocomputador);
	}

	@Override
	@Secure("#{s:hasPermission('assinarLaudoUnico','executar')}")
	public Boolean reabrirLaudo(Long numeroAp, Long luxSeq, Integer lu2Seq, DominioSituacaoExamePatologia etapasLaudo,
			AelDiagnosticoAps diagnostico, List<AelKitMatPatologiaVO> listaMateriais, String nomeMicrocomputador) throws BaseException {
		return getLaudoUnicoON().reabrirLaudo(numeroAp, luxSeq, lu2Seq, etapasLaudo, diagnostico, listaMateriais, nomeMicrocomputador);
	}

	@Override
	public AelPatologistaAps obterAelPatologistaApsPorChavePrimaria(Integer seq) {
		return getAelPatologistaApDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public void excluir(AelPatologistaAps patologistaAps) throws BaseException {
		getAelPatologistaApRN().excluirAelPatologistaAps(patologistaAps);
	}

	@Override
	public AelInformacaoClinicaAP obterAelInformacaoClinicaApPorAelExameAps(Long luxSeq) {
		return getAelInformacaoClinicaAPDAO().obterAelInformacaoClinicaApPorAelExameAps(luxSeq);
	}

	@Override
	public void persistirAelInformacaoClinicaAP(final AelInformacaoClinicaAP aelInformacaoClinicaAP) throws BaseException {
		getAelInformacaoClinicaAPRN().persistir(aelInformacaoClinicaAP);
	}

	@Override
	public void persistir(AelIndiceBlocoAp aelIndiceBlocoAp) throws BaseException {
		getAelIndiceBlocoApRN().persistir(aelIndiceBlocoAp);
	}

	@Override
	public List<AelIndiceBlocoAp> listarAelIndiceBlocoApPorAelExameAps(Long luxSeq) {
		return getAelIndiceBlocoApDAO().listarAelIndiceBlocoApPorAelExameAps(luxSeq);
	}

	@Override
	public AelIndiceBlocoAp obterAelIndiceBlocoApPorChavePrimaria(Long seq) {
		return getAelIndiceBlocoApDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public void excluir(AelIndiceBlocoAp aelIndiceBlocoAp) throws BaseException {
		getAelIndiceBlocoApRN().excluir(aelIndiceBlocoAp);
	}

	@Override
	public List<AelLaminaAps> obterListaLaminasPeloExameApSeq(Long luxSeq) {
		return getAelLaminaApsDAO().obterListaLaminasPeloExameApSeq(luxSeq);
	}

	@Override
	public AelLaminaAps obterLaminasPelaChavePrimaria(Long seq) {
		return getAelLaminaApsDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public void persistirAelLaminaAps(final AelLaminaAps aelLaminaAps) throws BaseException {
		getAelLaminaApsON().persistirAelLaminaAps(aelLaminaAps);
	}

	@Override
	public void excluirAelLaminaAps(final AelLaminaAps aelLaminaAps) throws BaseException {
		getAelLaminaApsON().excluirAelLaminaAps(aelLaminaAps);
	}

	@Override
	@Secure("#{s:hasPermission('cancelarImunohistoquimico','executar') and s:hasPermission('estornarImunohistoquimico','executar')}")
	public void ativarInativarImunoHistoquimica(List<AelKitMatPatologiaVO> listaMateriais, String nomeMicrocomputador) throws BaseException {
		getLaudoUnicoON().ativarInativarImunoHistoquimica(listaMateriais, nomeMicrocomputador);
	}

	@Override
	public List<AelOcorrenciaExameAp> buscarAelOcorrenciaExameApPorSeqExameAp(final Long luxSeq) {
		return this.getAelOcorrenciaExameApDAO().buscarAelOcorrenciaExameApPorSeqExameAp(luxSeq);
	}

	@Override
	public void atualizarImpressao(final Long luxSeq) throws BaseException {
		this.getAelExameApON().atualizarImpressao(luxSeq);
	}

	@Override
	public void validarPatologistaExcluir(RapServidores servidorPatologista, RapServidores patologistaApExcluir)
			throws ApplicationBusinessException {
		getAelPatologistaApON().validarPatologistaExcluir(servidorPatologista, patologistaApExcluir);
	}

	@Override
	public String replaceSustenidoLaudoUnico(String str, String oldValue, String newValue) {
		return getAelPatologistaApON().replaceSustenidoLaudoUnico(str, oldValue, newValue);

	}

	@Override
	public List<AelTopografiaCidOs> listarTopografiaCidOs(Object pesquisa) {
		return getAelTopografiaCidOsDAO().listarTopografiaCidOs(pesquisa);
	}

	@Override
	public Long listarTopografiaCidOsCount(Object pesquisa) {
		return getAelTopografiaCidOsDAO().listarTopografiaCidOsCount(pesquisa);
	}

	@Override
	public AelTopografiaLaudos obterAelTopografiaLaudosPorChavePrimaria(final Long seq) {
		return getAelTopografiaLaudosDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public List<AelTopografiaLaudos> listarTopografiaLaudosPorSeqExame(Long seqExame) {
		return getAelTopografiaLaudosDAO().listarTopografiaLaudosPorSeqExame(seqExame);
	}

	@Override
	public void persistirTopografiaLaudos(final AelTopografiaLaudos topografiaLaudos) throws BaseException {
		getAelTopografiaLaudosRN().persistir(topografiaLaudos);
	}

	@Override
	public void excluirTopografiaLaudos(final AelTopografiaLaudos topografiaLaudos) throws BaseException {
		getAelTopografiaLaudosRN().excluir(topografiaLaudos);
	}

	@Override
	public AelDiagnosticoLaudos obterAelDiagnosticoLaudosPorChavePrimaria(final Long seq) {
		return getAelDiagnosticoLaudosDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public List<AelDiagnosticoLaudos> listarDiagnosticoLaudosPorSeqExame(Long seqExame) {
		return getAelDiagnosticoLaudosDAO().listarDiagnosticoLaudosPorSeqExame(seqExame);
	}

	@Override
	public void persistirDiagnosticoLaudos(final AelDiagnosticoLaudos diagnosticoLaudos) throws BaseException {
		getAelDiagnosticoLaudosRN().persistir(diagnosticoLaudos);
	}

	@Override
	public void excluirDiagnosticoLaudos(final AelDiagnosticoLaudos diagnosticoLaudos) throws BaseException {
		getAelDiagnosticoLaudosRN().excluir(diagnosticoLaudos);
	}

	@Override
	public AelTopografiaLaudos obterTopografiaLaudos(Long seqExame, String codigo) {
		return getAelTopografiaLaudosDAO().obterTopografiaLaudos(seqExame, codigo);
	}

	/** {@inheritDoc} */
	@Override
	public AelAnatomoPatologico obterAelAnatomoPatologicoByNumeroAp(final Long numeroAp, Integer configExame) {
		return getAelAnatomoPatologicoDAO().obterAelAnatomoPatologicoByNumeroAp(numeroAp, configExame);
	}

	@Override
	public AelAnatomoPatologico obterAelAnatomoPatologicoPorItemSolic(Integer soeSeq, Short seqp) {
		return getAelAnatomoPatologicoDAO().obterAelAnatomoPatologicoPorItemSolic(soeSeq, seqp);
	}

	@Override
	public List<AelCidos> listarAelCidos(Object param) {
		return getAelDiagnosticoLaudosDAO().listarAelCidos(param);
	}

	@Override
	public Long listarAelCidosCount(Object param) {
		return getAelDiagnosticoLaudosDAO().listarAelCidosCount(param);
	}

	// Manter Texto Padrão de Descrição de Materiais

	@Override
	public List<AelGrpTxtDescMats> pesquisarGrupoTextoPadraoDescMats(final Short codigo, final String descricao,
			final DominioSituacao situacao) {
		return getAelGrpTxtDescMatsDAO().pesquisarGrupoTextoPadraoDescMats(codigo, descricao, situacao);
	}

	@Override
	public List<AelGrpTxtDescMats> pesquisarGrupoTextoPadraoDescMats(final String filtro, final DominioSituacao situacao) {
		return getAelGrpTxtDescMatsDAO().pesquisarGrupoTextoPadraoDescMats(filtro, situacao);
	}

	@Override
	public Long pesquisarGrupoTextoPadraoDescMatsCount(final String filtro, final DominioSituacao situacao) {
		return getAelGrpTxtDescMatsDAO().pesquisarGrupoTextoPadraoDescMatsCount(filtro, situacao);
	}

	@Override
	public AelGrpTxtDescMats obterAelGrpTxtDescMats(final Short seq) {
		return getAelGrpTxtDescMatsDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public void alterarAelGrpTxtDescMats(final AelGrpTxtDescMats AelGrpTxtDescMats) throws BaseException {
		getTextoPadraoDescMatsON().persistirAelGrpTxtDescMats(AelGrpTxtDescMats);
	}

	@Override
	public void excluirAelGrpTxtDescMats(final AelGrpTxtDescMats AelGrpTxtDescMats) throws BaseException {
		getTextoPadraoDescMatsON().removerAelGrpTxtDescMats(AelGrpTxtDescMats);
	}

	@Override
	public void inserirAelGrpTxtDescMats(final AelGrpTxtDescMats AelGrpTxtDescMats) throws BaseException {
		getTextoPadraoDescMatsON().persistirAelGrpTxtDescMats(AelGrpTxtDescMats);
	}

	@Override
	public List<AelTxtDescMats> pesquisarTextoPadraoDescMatsPorAelGrpTxtDescMats(final Short seqAelGrpTxtDescMats) {
		return getAelTextoDescMatsDAO().pesquisarTextoPadraoDescMatsPorAelGrpTxtPadraoDescMats(seqAelGrpTxtDescMats);
	}

	@Override
	public List<AelTxtDescMats> pesquisarTextoPadraoDescMats(final AelGrpTxtDescMats AelGrpTxtDescMats, final String filtro,
			final DominioSituacao indSituacao) {
		return getAelTextoDescMatsDAO().pesquisarTextoPadraoDescMats(AelGrpTxtDescMats, filtro, indSituacao);
	}
	
	@Override
	public List<AelTopografiaCidOs> listarTopografiaCidOs() {
		return getAelTopografiaCidOsDAO().listarTopografiaCidOs();
	}

	@Override
	public List<AelTopografiaCidOs> listarTopografiaCidOsPorGrupo(Long seqGrupo) {
		return getAelTopografiaCidOsDAO().listarTopografiaCidOsPorGrupo(seqGrupo);
	}

	@Override
	public List<AelTopografiaGrupoCidOs> listarTopografiaGrupoCidOsPorGrupo(Long seqGrupo) {
		return getAelTopografiaGruposCidOsDAO().listarTopografiaGrupoCidOsPorGrupo(seqGrupo);
	}

	@Override
	public AelTopografiaCidOs obterCidOPorChavePrimaria(Long cidOSeq) {
		return getAelTopografiaCidOsDAO().obterPorChavePrimaria(cidOSeq);
	}


	@Override
	public Long pesquisarTextoPadraoDescMatsCount(final AelGrpTxtDescMats AelGrpTxtDescMats, final String filtro,
			final DominioSituacao indSituacao) {
		return getAelTextoDescMatsDAO().pesquisarTextoPadraoDescMatsCount(AelGrpTxtDescMats, filtro, indSituacao);
	}

	@Override
	public void alterarAelTxtDescMats(final AelTxtDescMats AelTxtDescMats) throws BaseException {
		getTextoPadraoDescMatsON().persistirAelTxtDescMats(AelTxtDescMats);
	}

	@Override
	public void inserirAelTxtDescMats(final AelTxtDescMats AelTxtDescMats) throws BaseException {
		getTextoPadraoDescMatsON().persistirAelTxtDescMats(AelTxtDescMats);
	}

	@Override
	public void excluirAelTxtDescMats(final AelTxtDescMats AelTxtDescMats) throws BaseException {
		getTextoPadraoDescMatsON().removerAelTxtDescMats(AelTxtDescMats);
	}

	@Override
	public AelTxtDescMats obterAelTxtDescMats(final AelTxtDescMatsId AelTxtDescMatsId) {
		return getAelTextoDescMatsDAO().obterPorChavePrimaria(AelTxtDescMatsId);
	}

	@Override
	public List<AelGrpDescMatLacunas> pesquisarAelGrpDescMatLacunasPorTextoPadraoDescMats(Short AelTxtDescMatsGtmSeq, Short AelTxtDescMatsSeqp, final DominioSituacao indSituacao) {
		return getAelGrpDescMatsLacunaDAO().pesquisarAelGrpDescMatLacunasPorTextoPadraoDescMats(AelTxtDescMatsGtmSeq, AelTxtDescMatsSeqp, indSituacao);
	}

	@Override
	public void alterarAelGrpDescMatLacunas(final AelGrpDescMatLacunas AelGrpDescMatLacunas) throws BaseException {
		getTextoPadraoDescMatsON().persistirAelGrpDescMatLacunas(AelGrpDescMatLacunas);
	}

	@Override
	public void inserirAelGrpDescMatLacunas(final AelGrpDescMatLacunas AelGrpDescMatLacunas) throws BaseException {
		getTextoPadraoDescMatsON().persistirAelGrpDescMatLacunas(AelGrpDescMatLacunas);
	}

	@Override
	public void excluirAelGrpDescMatLacunas(final AelGrpDescMatLacunas AelGrpDescMatLacunas) throws BaseException {
		getTextoPadraoDescMatsON().removerAelGrpDescMatLacunas(AelGrpDescMatLacunas);
	}

	@Override
	public AelGrpDescMatLacunas obterAelGrpDescMatLacunas(final AelGrpDescMatLacunasId AelGrpDescMatLacunasId) {
		return getAelGrpDescMatsLacunaDAO().obterPorChavePrimaria(AelGrpDescMatLacunasId);
	}

	@Override
	public List<AelDescMatLacunas> pesquisarAelDescMatLacunasPorAelGrpDescMatLacunas(AelGrpDescMatLacunas AelGrpDescMatLacunas, final DominioSituacao indSituacao) {
		return getAelTxtDescMatsLacunaDAO().pesquisarAelDescMatLacunasPorAelGrpDescMatsLacuna(AelGrpDescMatLacunas, indSituacao);
	}

	@Override
	public void alterarAelDescMatLacunas(final AelDescMatLacunas AelDescMatLacunas) throws BaseException {
		getTextoPadraoDescMatsON().persistirAelDescMatLacunas(AelDescMatLacunas);
	}

	@Override
	public void inserirAelDescMatLacunas(final AelDescMatLacunas AelDescMatLacunas) throws BaseException {
		getTextoPadraoDescMatsON().persistirAelDescMatLacunas(AelDescMatLacunas);
	}

	@Override
	public AelDescMatLacunas obterAelDescMatLacunas(final AelDescMatLacunasId AelDescMatLacunasId) {
		return getAelTxtDescMatsLacunaDAO().obterPorChavePrimaria(AelDescMatLacunasId);
	}

	@Override
	public void excluirAelDescMatLacunas(final AelDescMatLacunas AelDescMatLacunas) throws BaseException {
		getTextoPadraoDescMatsON().removerAelDescMatLacunas(AelDescMatLacunas);
	}

	// Preencher texto Padrão de Descrição do Material

	@Override
	@Secure("#{s:hasPermission('concluirReabrirDescMaterial','executar')}")
	public void concluirDescMaterialAps(final AelExameAp exameAp, final AelDescMaterialAps macroscopia) throws BaseException {
		getAelDescMaterialApsON().concluirDescMaterialAps(exameAp, macroscopia);
	}

	@Override
	public void validarDescMaterialPreenchida(final AelDescMaterialAps macroscopia) throws ApplicationBusinessException {
		getAelDescMaterialApsON().validarDescMaterialPreenchida(macroscopia);
	}

	@Override
	public void persistirAelDescMaterialAps(final AelDescMaterialAps macroscopia) throws BaseException {
		getAelDescMaterialApsRN().persistir(macroscopia);
	}

	@Override
	public AelDescMaterialAps obterAelDescMaterialApsPorAelExameAps(final Long luxSeq) {
		return getAelDescMaterialApsDAO().obterAelDescMaterialApsPorAelExameAps(luxSeq);
	}

	@Override
	public AelDescMaterialAps obterAelDescMaterialApPorChavePrimaria(Long chavePrimaria) {
		return getAelDescMaterialApsDAO().obterPorChavePrimaria(chavePrimaria);
	}

	// TODO #21881
	@Override
	public AelMarcador obterAelMarcadorPorChavePrimaria(final Integer seq) {
		return getAelMarcadorDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public List<AelMarcador> pesquisarAelMarcador(final Integer firstResult, final Integer maxResults, final String orderProperty,
			final boolean asc, final AelMarcador aelMarcador) {
		return getAelMarcadorDAO().pesquisarAelMarcador(firstResult, maxResults, orderProperty, asc, aelMarcador);
	}

	@Override
	public Long pesquisarAelMarcadorCount(final AelMarcador aelMarcador) {
		return getAelMarcadorDAO().pesquisarAelMarcadorCount(aelMarcador);
	}

	@Override
	public void inserirAelMarcador(final AelMarcador aelMarcador) throws BaseException {
		getAelMarcadorRN().inserir(aelMarcador);
	}

	@Override
	public void alterarAelMarcador(final AelMarcador aelMarcador) throws BaseException {
		getAelMarcadorRN().alterar(aelMarcador);
	}

	@Override
	//@Secure("#{s:hasPermission('manterMarcadores','executar')}")
	public String ativarInativarAelMarcador(AelMarcador aelMarcadorEdicao) {
		return getAelMarcadorON().ativarInativarAelMarcador(aelMarcadorEdicao);
	}

	@Override
	public Boolean buscarAtivoPorDescVersaoConfLu2Seq(DominioSecaoConfiguravel secao, Integer versaoConf, Integer lu2Seq) {
		return getAelSecaoConfExamesDAO().buscarAtivoPorDescVersaoConfLu2Seq(secao, versaoConf, lu2Seq);
	}

	@Override
	public void validaSigla(String sigla) throws ApplicationBusinessException {
		getConfiguracaoExamesRN().validaSigla(sigla);
	}

	@Override
	public void persisteSecaoConfigurcaoExames(AelConfigExLaudoUnico configExame) {
		getConfiguracaoExamesRN().persisteSecaoConfigurcaoExames(configExame);
	}

	@Override
	public Short obterMaxSeqAelExtratoExameAp(final Long luxSeq, final DominioSituacaoExamePatologia etapasLaudo) {
		return getAelExtratoExameApDAO().obterMaxSeqAelExtratoExameAp(luxSeq, etapasLaudo);
	}

	@Override
	public AelExtratoExameAp obterAelExtratoExameApPorChavePrimaria(final AelExtratoExameApId id) {
		return getAelExtratoExameApDAO().obterPorChavePrimaria(id);
	}

	@Override
	public void atualizaMateriais(final AelExameAp aelExameAp, final AelItemSolicitacaoExamesId itemSolicitacao) throws BaseException {
		this.getLaudoUnicoON().atualizaMateriais(aelExameAp, itemSolicitacao);
	}

	@Override
	public List<AelMaterialAp> pesquisaMateriaisCapsula(Object material, Long luxSeq) {
		return getAelMaterialApDAO().pesquisaMateriaisCapsula(material, luxSeq);
	}

	@Override
	public Long pesquisaMateriaisCapsulaCount(Object material, Long luxSeq) {
		return getAelMaterialApDAO().pesquisaMateriaisCapsulaCount(material, luxSeq);
	}

	@Override
	public void gravarLaminas(List<AelLaminaAps> laminasEmMemoria, List<AelLaminaAps> laminasExcluir, AelExameAp exame)
			throws BaseException {
		getAelLaminaApsON().gravarLaminas(laminasEmMemoria, laminasExcluir, exame);
	}
	
	@Override
	public AelConfigExLaudoUnico obterPorSigla(String sigla) {
		return getAelConfigExLaudoUnicoDAO().obterPorSigla(sigla);
	}

	@Override
	public List<ConsultaItensPatologiaVO> listaExamesComVersaoLaudo(Long luxSeq, Integer calcSeq, String[] sitCodigo) {
		return getAelExameApItemSolicDAO().listaExamesComVersaoLaudo(luxSeq, calcSeq, sitCodigo);
	}

	@Override
	public void excluir(AelOcorrenciaExameAp aelOcorrenciaExameAp, String usuarioLogado) throws BaseException {
		getAelOcorrenciaExameApRN().excluir(aelOcorrenciaExameAp, usuarioLogado);
	}

	@Override
	public void excluir(AelInformacaoClinicaAP aelInformacaoClinicaAP, String usuarioLogado) throws BaseException {
		getAelInformacaoClinicaAPRN().excluir(aelInformacaoClinicaAP, usuarioLogado);
	}

	@Override
	public void excluir(AelExameAp aelExameAp, String usuarioLogado)
			throws BaseException {
		getAelExameApRN().excluir(aelExameAp, usuarioLogado);
	}

	@Override
	public void excluirAelApXPatologista(AelApXPatologista aelApXPatologista) throws ApplicationBusinessException {
		getAelApXPatologistaRN().excluirAelApXPatologista(aelApXPatologista);
	}

	@Override
	public void excluir(AelAnatomoPatologico aelAnatomoPatologico, String usuarioLogado) throws BaseException {
		getAelAnatomoPatologicoRN().excluir(aelAnatomoPatologico, usuarioLogado);
	}

	@Override
	public AelConfigExLaudoUnico obterConfigExLaudoUnico(AelItemSolicitacaoExames item) {
		return getAelConfigExLaudoUnicoDAO().obterConfigExLaudoUnico(item);
	}

	@Override
	public void removeAdicionaPatologistaLaudo(
			AelAnatomoPatologico anatomoPatologico,
			AelPatologista antigoPatologistaResp,
			AelPatologista novoPatologistaResp, RapServidores servidorLogado)
			throws BaseException {
		getAelPatologistaApON().removeAdicionaPatologistaLaudo(anatomoPatologico, antigoPatologistaResp, novoPatologistaResp, servidorLogado);
	}

	// DAO's
	// ------------------------------------------------------------------------------

	private AelOcorrenciaExameApDAO getAelOcorrenciaExameApDAO() {
		return aelOcorrenciaExameApDAO;
	}

	protected AelIndiceBlocoApDAO getAelIndiceBlocoApDAO() {
		return aelIndiceBlocoApDAO;
	}

	protected AelIndiceBlocoApRN getAelIndiceBlocoApRN() {
		return aelIndiceBlocoApRN;
	}

	protected AelApXPatologistaDAO getAelApXPatologistaDAO() {
		return aelApXPatologistaDAO;
	}

	protected AelAnatomoPatologicoDAO getAelAnatomoPatologicoDAO() {
		return aelAnatomoPatologicoDAO;
	}

	private AelTextoPadraoColoracsDAO getAelTextoPadraoColoracsDao() {
		return aelTextoPadraoColoracsDAO;
	}

	private AelCadGuicheDAO getAelCadGuicheDao() {
		return aelCadGuicheDAO;
	}

	private AelGrpTxtPadraoDiagsDAO getAelGrpTxtPadraoDiagsDAO() {
		return aelGrpTxtPadraoDiagsDAO;
	}

	private AelTextoPadraoDiagsDAO getAelTextoPadraoDiagsDAO() {
		return aelTextoPadraoDiagsDAO;
	}

	private AelGrpDiagLacunasDAO getAelGrpDiagLacunasDAO() {
		return aelGrpDiagLacunasDAO;
	}

	private AelTxtDiagLacunasDAO getAelTxtDiagLacunasDAO() {
		return aelTxtDiagLacunasDAO;
	}

	private AelGrpTxtPadraoMacroDAO getAelGrpTxtPadraoMacroDAO() {
		return aelGrpTxtPadraoMacroDAO;
	}

	private AelTextoPadraoMacroDAO getAelTextoPadraoMacroDAO() {
		return aelTextoPadraoMacroDAO;
	}

	private AelGrpMacroLacunaDAO getAelGrpMacroLacunaDAO() {
		return aelGrpMacroLacunaDAO;
	}

	private AelTxtMacroLacunaDAO getAelTxtMacroLacunaDAO() {
		return aelTxtMacroLacunaDAO;
	}

	private AelGrpTxtDescMatsDAO getAelGrpTxtDescMatsDAO() {
		return aelGrpTxtDescMatsDAO;
	}

	private AelTextoDescMatsDAO getAelTextoDescMatsDAO() {
		return aelTextoDescMatsDAO;
	}

	private AelGrpDescMatsLacunaDAO getAelGrpDescMatsLacunaDAO() {
		return aelGrpDescMatsLacunaDAO;
	}

	private AelTxtDescMatsLacunaDAO getAelTxtDescMatsLacunaDAO() {
		return aelTxtDescMatsLacunaDAO;
	}

	private AelGrpTxtPadraoMicroDAO getAelGrpTxtPadraoMicroDAO() {
		return aelGrpTxtPadraoMicroDAO;
	}

	private AelTextoPadraoMicroDAO getAelTextoPadraoMicroDAO() {
		return aelTextoPadraoMicroDAO;
	}

	private AelGrpMicroLacunaDAO getAelGrpMicroLacunaDAO() {
		return aelGrpMicroLacunaDAO;
	}

	private AelTxtMicroLacunaDAO getAelTxtMicroLacunaDAO() {
		return aelTxtMicroLacunaDAO;
	}

	protected AelPatologistaDAO getAelPatologistaDAO() {
		return aelPatologistaDAO;
	}

	private AelConfigExLaudoUnicoDAO getAelConfigExLaudoUnicoDAO() {
		return aelConfigExLaudoUnicoDAO;
	}

	protected AelItemConfigExameDAO getAelItemConfigExameDAO() {
		return aelItemConfigExameDAO;
	}

	private VAelApXPatologiaAghuDAO getVAelApXPatologiaAghuDAO() {
		return vAelApXPatologiaAghuDAO;
	}

	private AelExameApDAO getAelExameApDAO() {
		return aelExameApDAO;
	}

	protected AelExamesMaterialAnaliseDAO getAelExamesMaterialAnaliseDAO() {
		return aelExamesMaterialAnaliseDAO;
	}

	private AelNomenclaturaGenericsDAO getAelNomenclaturaGenericsDAO() {
		return aelNomenclaturaGenericsDAO;
	}

	private AelNomenclaturaEspecsDAO getAelNomenclaturaEspecsDAO() {
		return aelNomenclaturaEspecsDAO;
	}

	private AelTopografiaSistemasDAO getAelTopografiaSistemasDAO() {
		return aelTopografiaSistemasDAO;
	}

	private AelTopografiaAparelhosDAO getAelTopografiaAparelhosDAO() {
		return aelTopografiaAparelhosDAO;
	}

	private AelKitIndiceBlocoDAO getAelKitIndiceBlocoDAO() {
		return aelKitIndiceBlocoDAO;
	}

	private AelKitItemIndiceBlocoDAO getAelKitItemIndiceBlocoDAO() {
		return aelKitItemIndiceBlocoDAO;
	}

	private AelKitMatPatologiaDAO getAelKitMatPatologiaDAO() {
		return aelKitMatPatologiaDAO;
	}

	private AelKitItemMatPatologiaDAO getAelKitItemMatPatologiaDAO() {
		return aelKitItemMatPatologiaDAO;
	}

	private AelCestoPatologiaDAO getAelCestoPatologiaDAO() {
		return aelCestoPatologiaDAO;
	}

	private AelExtratoExameApDAO getAelExtratoExameApDAO() {
		return aelExtratoExameApDAO;
	}

	private AelMacroscopiaApsDAO getAelMacroscopiaApsDAO() {
		return aelMacroscopiaApsDAO;
	}

	private AelDiagnosticoApsDAO getAelDiagnosticoApsDAO() {
		return aelDiagnosticoApsDAO;
	}

	private AelMaterialApDAO getAelMaterialApDAO() {
		return aelMaterialApDAO;
	}

	private AelNotaAdicionalApDAO getAelNotaAdicionalApDAO() {
		return aelNotaAdicionalApDAO;
	}

	private AelTopografiaApsDAO getAelTopografiaApsDAO() {
		return aelTopografiaApsDAO;
	}

	private AelNomenclaturaApsDAO getAelNomenclaturaApsDAO() {
		return aelNomenclaturaApsDAO;
	}

	private AelPatologistaApDAO getAelPatologistaApDAO() {
		return aelPatologistaApDAO;
	}

	private AelInformacaoClinicaApDAO getAelInformacaoClinicaAPDAO() {
		return aelInformacaoClinicaApDAO;
	}
	
	public AelExameApItemSolicDAO getAelExameApItemSolicDAO() {
		return aelExameApItemSolicDAO;
	}

	// RN's
	// ------------------------------------------------------------------------------
	private AelTextoPadraoColoracsRN getAelTextoPadraoColoracsRN() {
		return aelTextoPadraoColoracsRN;
	}

	private AelCadastroGuicheRN getAelCadastroGuicheRN() {
		return aelCadastroGuicheRN;
	}

	private AelExameApON getAelExameApON() {
		return aelExameApON;
	}

	private AelMovimentoGuicheRN getAelMovimentoGuicheRN() {
		return aelMovimentoGuicheRN;
	}

	protected AelPatologistaRN getAelPatologistaRN() {
		return aelPatologistaRN;
	}

	protected AelConfigExLaudoUnicoRN getAelConfigExLaudoUnicoRN() {
		return aelConfigExLaudoUnicoRN;
	}

	protected AelItemConfigExameRN getAelItemConfigExameRN() {
		return aelItemConfigExameRN;
	}

	private AelNomenclaturaGenericsRN getAelNomenclaturaGenericsRN() {
		return aelNomenclaturaGenericsRN;
	}

	private AelNomenclaturaEspecsRN getAelNomenclaturaEspecsRN() {
		return aelNomenclaturaEspecsRN;
	}

	private AelTopografiaSistemasRN getAelTopografiaSistemasRN() {
		return aelTopografiaSistemasRN;
	}

	private AelTopografiaAparelhosRN getAelTopografiaAparelhosRN() {
		return aelTopografiaAparelhosRN;
	}

	private AelKitIndiceBlocoRN getAelKitIndiceBlocoRN() {
		return aelKitIndiceBlocoRN;
	}

	private AelKitItemIndiceBlocoRN getAelKitItemIndiceBlocoRN() {
		return aelKitItemIndiceBlocoRN;
	}

	private AelKitMatPatologiaRN getAelKitMatPatologiaRN() {
		return aelKitMatPatologiaRN;
	}

	private AelKitItemMatPatologiaRN getAelKitItemMatPatologiaRN() {
		return aelKitItemMatPatologiaRN;
	}

	private AelCestoPatologiaRN getAelCestoPatologiaRN() {
		return aelCestoPatologiaRN;
	}

	protected AelMaterialApRN getAelMaterialApRN() {
		return aelMaterialApRN;
	}

	protected AelNotaAdicionalApRN getAelNotaAdicionalApRN() {
		return aelNotaAdicionalApRN;
	}

	protected AelPatologistaApRN getAelPatologistaApRN() {
		return aelPatologistaApRN;
	}

	protected AelTopografiaApsRN getAelTopografiaApsRN() {
		return aelTopografiaApsRN;
	}

	protected AelNomenclaturaApsRN getAelNomenclaturaApsRN() {
		return aelNomenclaturaApsRN;
	}

	private AelDiagnosticoApsRN getAelDiagnosticoApsRN() {
		return aelDiagnosticoApsRN;
	}

	private AelMacroscopiaApsRN getAelMacroscopiaApsRN() {
		return aelMacroscopiaApsRN;
	}

	private AelInformacaoClinicaAPRN getAelInformacaoClinicaAPRN() {
		return aelInformacaoClinicaAPRN;
	}

	private AelLaminaApsDAO getAelLaminaApsDAO() {
		return aelLaminaApsDAO;
	}

	private AelOcorrenciaExameApRN getAelOcorrenciaExameApRN() {
		return aelOcorrenciaExameApRN;
	}
	
	public AelExameApRN getAelExameApRN() {
		return aelExameApRN;
	}

	public AelApXPatologistaRN getAelApXPatologistaRN() {
		return aelApXPatologistaRN;
	}

	public AelAnatomoPatologicoRN getAelAnatomoPatologicoRN() {
		return aelAnatomoPatologicoRN;
	}	

	// ON's
	// ------------------------------------------------------------------------------
	private TextoPadraoMacroscopiaON getTextoPadraoMacroscopiaON() {
		return textoPadraoMacroscopiaON;
	}

	private TextoPadraoDescMatsON getTextoPadraoDescMatsON() {
		return textoPadraoDescMatsON;
	}

	private TextoPadraoDiagnosticoON getTextoPadraoDiagnosticoON() {
		return textoPadraoDiagnosticoON;
	}

	private TextoPadraoMicroscopiaON getTextoPadraoMicroscopiaON() {
		return textoPadraoMicroscopiaON;
	}

	private IdentificaGuicheON getIdentificaGuicheON() {
		return identificaGuicheON;
	}

	private AelApXPatologistaON getAelApXPatologistaON() {
		return aelApXPatologistaON;
	}

	private VAelApXPatologiaAghuON getVAelApXPatologiaAghuON() {
		return vAelApXPatologiaAghuON;
	}

	protected AelCadastroGuicheON getAelCadastroGuicheON() {
		return aelCadastroGuicheON;
	}

	private AelLaminaApsON getAelLaminaApsON() {
		return aelLaminaApsON;
	}

	private RelatorioLaudoUnicoON getRelatorioLaudoUnicoON() {
		return relatorioLaudoUnicoON;
	}

	protected LaudoUnicoON getLaudoUnicoON() {
		return laudoUnicoON;
	}

	private AelDiagnosticoApsON getAelDiagnosticoApsON() {
		return aelDiagnosticoApsON;
	}

	protected AelPatologistaApON getAelPatologistaApON() {
		return aelPatologistaApON;
	}

	protected AelMacroscopiaApsON getAelMacroscopiaApsON() {
		return aelMacroscopiaApsON;
	}

	protected AelNotaAdicionalApON getAelNotaAdicionalApON() {
		return aelNotaAdicionalApON;
	}

	private AelTopografiaCidOsDAO getAelTopografiaCidOsDAO() {
		return aelTopografiaCidOsDAO;
	}

	private AelTopografiaGruposCidOsDAO getAelTopografiaGruposCidOsDAO() {
		return aelTopografiaGruposCidOsDAO;
	}

	private AelTopografiaLaudosDAO getAelTopografiaLaudosDAO() {
		return aelTopografiaLaudosDAO;
	}

	private AelDiagnosticoLaudosDAO getAelDiagnosticoLaudosDAO() {
		return aelDiagnosticoLaudosDAO;
	}

	protected AelTopografiaLaudosRN getAelTopografiaLaudosRN() {
		return aelTopografiaLaudosRN;
	}

	protected AelDiagnosticoLaudosRN getAelDiagnosticoLaudosRN() {
		return aelDiagnosticoLaudosRN;
	}

	protected AelSecaoConfExamesDAO getAelSecaoConfExamesDAO() {
		return aelSecaoConfExamesDAO;
	}

	protected AelDescMaterialApsDAO getAelDescMaterialApsDAO() {
		return aelDescMaterialApsDAO;
	}

	protected AelMarcadorDAO getAelMarcadorDAO() {
		return aelMarcadorDAO;
	}

	protected AelMarcadorRN getAelMarcadorRN() {
		return aelMarcadorRN;
	}

	protected AelMarcadorON getAelMarcadorON() {
		return aelMarcadorON;
	}

	protected AelDescMaterialApsON getAelDescMaterialApsON() {
		return aelDescMaterialApsON;
	}

	protected AelDescMaterialApsRN getAelDescMaterialApsRN() {
		return aelDescMaterialApsRN;
	}

	protected ConfiguracaoExamesRN getConfiguracaoExamesRN() {
		return configuracaoExamesRN;
	}

	@Override
	public AelMaterialAp obterAelMaterialApPorSeq(Long luxSeq) {
		return this.getAelMaterialApDAO().obterPorChavePrimaria(luxSeq);
	}

	@Override
	public void persistirAelMaterialAp(AelMaterialAp materialAp,
			AelMaterialAp materialAPOld, Boolean isInsert) throws BaseException {
		getAelMaterialApRN().persistirAelMaterialAp(materialAp, materialAPOld, isInsert);
	}
	
	@Override
	public AelAnatomoPatologico obterAelAnatomoPatologicoPorId(Long seq) {
		return getAelAnatomoPatologicoDAO().obterAelAnatomoPatologicoPorId(seq);
	}
	
}