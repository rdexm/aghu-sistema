package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.MamLaudoAihDAO;
import br.gov.mec.aghu.blococirurgico.vo.VFatSsmInternacaoVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.dominio.DominioIndPendenteLaudoAih;
import br.gov.mec.aghu.dominio.DominioIndSituacaoLaudoAih;
import br.gov.mec.aghu.dominio.DominioOrigemLaudoAih;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoVersaoDocumento;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatTipoCaractItens;
import br.gov.mec.aghu.model.MamLaudoAih;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * 
 * @author murillo
 * 
 */
@Stateless
public class LaudoAihON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(LaudoAihON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private ICascaFacade iCascaFacade;

	@EJB
	private IFaturamentoFacade iFaturamentoFacade;

	@Inject
	private MamLaudoAihDAO mamLaudoAihDAO;

	@EJB
	private IPacienteFacade iPacienteFacade;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IAghuFacade iAghuFacade;
	
	@EJB
	private LaudoAihRN laudoAihRN;

	@EJB
	private ICertificacaoDigitalFacade iCertificacaoDigitalFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7590783079819421150L;

	public enum LaudoAihONExceptionCode implements BusinessExceptionCode {
		MCO_00763, MAM_00435, CID_NAO_COMPATIVEL_PROCEDIMENTO;
	}

	private static final String PERMISSAO_ASSINATURA_DIGITAL = "assinaturaDigital";

	private MamLaudoAihDAO getMamLaudoAihDAO() {
		return mamLaudoAihDAO;
	}

	List<MamLaudoAih> obterPorTrgSeq(long trgSeq) {
		return this.getMamLaudoAihDAO().obterPorTrgSeq(trgSeq);
	}

	/**
	 * ON1 - Executada ao incluir um novo registro
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 */
	public Boolean salvar(MamLaudoAih laudoAih, AipPacientes paciente) throws BaseException {
		RapServidores servidorLogado = this.getServidorLogadoFacade().obterServidorLogado();
		
		// ON5
		this.eventoPreInserirLaudo(laudoAih, paciente);

		// RN8
		this.getLaudoAihRN().preInserirLaudo(laudoAih, servidorLogado.getUsuario());

		// Persiste MAM_LAUDO_AIHS
		this.getMamLaudoAihDAO().persistir(laudoAih);
		this.getMamLaudoAihDAO().flush();

		// ON9
		return eventoPreConcluirLaudoON9(laudoAih, servidorLogado, paciente);
	}

	/**
	 * ON2 - Executada ao editar um registro
	 * 
	 * @throws ApplicationBusinessException
	 */
	public Boolean atualizar(MamLaudoAih laudoAih, AipPacientes paciente) throws BaseException {
		RapServidores servidorLogado = this.getServidorLogadoFacade().obterServidorLogado();
		
		// ON3 e ON7
		this.eventoPreAtualizarLaudo(laudoAih);

		// ON8
		this.alterarAutoRelacionamentoLaudo(laudoAih.getSeq(), laudoAih.getIndPendente(), DominioIndPendenteLaudoAih.A, servidorLogado);

		// RN3
		this.getLaudoAihRN().preAtualizarLaudoParte1(laudoAih, servidorLogado);

		// Atualiza MAM_LAUDO_AIHS
		this.getMamLaudoAihDAO().atualizar(laudoAih);
		this.getMamLaudoAihDAO().flush();

		// ON9
		return eventoPreConcluirLaudoON9(laudoAih, servidorLogado, paciente);
	}

	/**
	 * ON4 - PROCEDURE PROTEGE_LIBERA_REGISTRO_LAI
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 */
	public void protegerLiberarRegistroLai(Long laiSeq, DominioIndPendenteLaudoAih indPendente, RapServidores servidorValida) throws ApplicationBusinessException {
		final RapServidores servidorLogado = this.getServidorLogadoFacade().obterServidorLogado();
		if (indPendente.equals(DominioIndPendenteLaudoAih.V)) {
			if (!(servidorLogado.getId().getMatricula().equals(servidorValida.getId().getMatricula()) && servidorLogado.getId().getVinCodigo().equals(servidorValida.getId().getVinCodigo()))) {
				throw new ApplicationBusinessException(LaudoAihONExceptionCode.MCO_00763, Severity.INFO);
			}
		} else if ((indPendente.equals(DominioIndPendenteLaudoAih.P) || indPendente.equals(DominioIndPendenteLaudoAih.R)) && laiSeq != null) {
			MamLaudoAih laudoAih = getMamLaudoAihDAO().obterPorChavePrimaria(laiSeq);
			RapServidores serValidaAnt = laudoAih.getServidorValida();
			if (!(servidorLogado.getId().getMatricula().equals(serValidaAnt.getId().getMatricula()) && servidorLogado.getId().getVinCodigo().equals(serValidaAnt.getId().getVinCodigo()))) {
				throw new ApplicationBusinessException(LaudoAihONExceptionCode.MCO_00763, Severity.INFO);
			}
		}
	}

	private void eventoPreInserirLaudo(MamLaudoAih laudoAih, AipPacientes paciente) throws ApplicationBusinessException {
		laudoAih.setIndPendente(DominioIndPendenteLaudoAih.P);
		laudoAih.setIndImpresso(false);
		laudoAih.setPaciente(paciente);
		// TODO con_numero, trg_seq e rgt_seq não são passados por parâmetro ao acessar o laudo via módulo de Cirurgias.
		if (laudoAih.getTrgSeq() != null) {
			laudoAih.setOrigem(DominioOrigemLaudoAih.E);
			AghParametros paramRevMedicaLaiEmerg = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_REVISAO_MEDICA_LAI_EMERG);

			if (paramRevMedicaLaiEmerg.getVlrTexto().equalsIgnoreCase("S")) {
				laudoAih.setIndSituacao(DominioIndSituacaoLaudoAih.H);
			} else {
				laudoAih.setIndSituacao(DominioIndSituacaoLaudoAih.G);
			}
		} else {
			laudoAih.setOrigem(DominioOrigemLaudoAih.O);
			AghParametros paramRevMedicaLaiOutros = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_REVISAO_MEDICA_LAI_OUTROS);
			if (paramRevMedicaLaiOutros.getVlrTexto().equalsIgnoreCase("S")) {
				laudoAih.setIndSituacao(DominioIndSituacaoLaudoAih.H);
			} else {
				laudoAih.setIndSituacao(DominioIndSituacaoLaudoAih.G);
			}
		}
		if (laudoAih.getAghCid().getCodigo() != null) {
			// Chama ON6
			verificarCompatibilidadeCid(laudoAih);

		} else {
			throw new ApplicationBusinessException(LaudoAihONExceptionCode.MAM_00435);
		}

	}

	/**
	 * ON3 e ON7 - PRE-UPDATE
	 * 
	 * @param laudoAih
	 * @throws ApplicationBusinessException
	 */
	private void eventoPreAtualizarLaudo(MamLaudoAih laudoAih) throws ApplicationBusinessException {
		// ON3
		if (laudoAih.getIndPendente().equals(DominioIndPendenteLaudoAih.R)) {
			laudoAih.setIndPendente(DominioIndPendenteLaudoAih.P);
		}
		if (laudoAih.getTrgSeq() != null) {
			laudoAih.setOrigem(DominioOrigemLaudoAih.E);
		} else {
			if (!laudoAih.getIndSituacao().equals(DominioIndSituacaoLaudoAih.I)) {
				laudoAih.setOrigem(DominioOrigemLaudoAih.O);
			}
		}
		laudoAih.setIndSituacao(DominioIndSituacaoLaudoAih.H);
		// FIM ON3

		// ON7
		if (laudoAih.getAghCid().getCodigo() != null) {
			// Chama ON6
			verificarCompatibilidadeCid(laudoAih);

		} else {
			throw new ApplicationBusinessException(LaudoAihONExceptionCode.MAM_00435);
		}
		// FIM ON7
	}

	/**
	 * ON6 - PROCEDURE CGFK$LKP_LAI_MAM_LAI_CID_FK1
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 */
	private void verificarCompatibilidadeCid(MamLaudoAih laudoAih) throws ApplicationBusinessException {
		Long codTabela = laudoAih.getFatItemProcedHospital().getCodTabela();
		Integer idade = laudoAih.getPaciente().getIdade();
		String cidCodigo = laudoAih.getAghCid().getCodigo();

		DominioSexoDeterminante sexo = converterSexoDeterminante(laudoAih.getPaciente().getSexo());

		Short vlrNumerico = obterParametroTabelaFaturamentoPadrao();
		List<Long> listaCodSsm = getFaturamentoFacade().obterListaCodTabelaFatSsm(codTabela, idade, sexo, vlrNumerico);

		List<AghCid> resultado = getAghuFacade().listarAghCidPorIdadeSexo(listaCodSsm, idade, cidCodigo, sexo);

		if (resultado == null || resultado.isEmpty()) {
			throw new ApplicationBusinessException(LaudoAihONExceptionCode.CID_NAO_COMPATIVEL_PROCEDIMENTO);
		}

	}

	/**
	 * ON8 - PROCEDURE altera_auto_rel_lai
	 * 
	 * @param laiSeq
	 * @param indPendente
	 * @param novoIndPendente
	 * @param servidorLogado
	 */
	private void alterarAutoRelacionamentoLaudo(Long laiSeq, DominioIndPendenteLaudoAih indPendente, DominioIndPendenteLaudoAih novoIndPendente, RapServidores servidorLogado) {

		if ((indPendente.equals(DominioIndPendenteLaudoAih.R) || indPendente.equals(DominioIndPendenteLaudoAih.P)) && laiSeq != null) {

			MamLaudoAih laudoAih = this.getMamLaudoAihDAO().obterPorChavePrimaria(laiSeq);

			laudoAih.setDthrMvto(new Date());
			laudoAih.setServidorMvto(servidorLogado);
			laudoAih.setIndPendente(novoIndPendente);

			this.getMamLaudoAihDAO().atualizar(laudoAih);
			this.getMamLaudoAihDAO().flush();
		}

	}

	/**
	 * ON9 - P_CONCLUIR_LAUDO
	 * 
	 * @throws ApplicationBusinessException
	 */
	private Boolean eventoPreConcluirLaudoON9(MamLaudoAih laudoAih, RapServidores servidorLogado, AipPacientes paciente) throws BaseException {
		// TODO Parâmetros assinar_aih, rgt_seq e rgt_seq não são passados por parâmetro ao acessar o laudo via módulo de Cirurgias.
		if ("assinar_aih".equals(DominioSimNao.S)) {
			// RN1
			getLaudoAihRN().concluirLaudoAih(0, new Date(), servidorLogado, null);

		} else if (laudoAih.getTrgSeq() == null) {
			// ON10
			eventoPreConcluirLaudoON10(new Date(), servidorLogado, laudoAih.getSeq(), paciente.getCodigo());
		}
		final boolean habilitaCertif = getCertificacaoDigitalFacade().verificaAssituraDigitalHabilitada();
		Boolean usuarioQualifAssinaturaDigital = this.getICascaFacade().usuarioTemPermissao(servidorLogado != null ? servidorLogado.getUsuario() : null, PERMISSAO_ASSINATURA_DIGITAL);

		if (habilitaCertif && usuarioQualifAssinaturaDigital.equals(Boolean.TRUE)) {
			return true;
		} else {
			return false;
		}
	}

	private void eventoPreConcluirLaudoON10(Date dataHoraMovto, RapServidores servidorLogado, Long seq, Integer pacCodigo) throws BaseException {

		List<MamLaudoAih> listaLaudos = getMamLaudoAihDAO().obterLaudoPorSeqEPaciente(seq, pacCodigo);

		for (MamLaudoAih laudo : listaLaudos) {
			if (laudo.getIndPendente().equals(DominioIndPendenteLaudoAih.R)) {
				// Reaproveitando o método da RN1, pois faz a mesma coisa.
				getLaudoAihRN().tratarRascunho(laudo.getSeq(), laudo.getMamLaudoAihs() == null ? null : laudo.getMamLaudoAihs().getSeq(), servidorLogado);

			} else if (laudo.getIndPendente().equals(DominioIndPendenteLaudoAih.P)) {
				// Reaproveitando o método da RN1, pois faz a mesma coisa.
				getLaudoAihRN().tratarPendente(laudo.getSeq(), null, dataHoraMovto, servidorLogado, pacCodigo);

			} else if (laudo.getIndPendente().equals(DominioIndPendenteLaudoAih.E)) {
				// Reaproveitando o método da RN1, pois faz a mesma coisa.
				getLaudoAihRN().tratarExclusao(laudo.getSeq(), dataHoraMovto, servidorLogado);
			}
		}
	}

	/**
	 * ON13 - PROCEDURE P_DESBLOQ_DOC_CERTIF
	 * 
	 * @param laiSeq
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void desbloquearDocAssinaturaDigital(Long laiSeq, RapServidores servidorLogado) throws BaseException {

		List<AghVersaoDocumento> listVersoes = this.getCertificacaoDigitalFacade().pesquisarVersoesLaudoAih(laiSeq);

		final boolean habilitaCertif = getCertificacaoDigitalFacade().verificaAssituraDigitalHabilitada();
		Boolean usuarioQualifAssinaturaDigital = this.getICascaFacade().usuarioTemPermissao(servidorLogado != null ? servidorLogado.getUsuario() : null, PERMISSAO_ASSINATURA_DIGITAL);

		if (listVersoes != null && listVersoes.size() > 0) {

			if (habilitaCertif && usuarioQualifAssinaturaDigital.equals(Boolean.TRUE)) {

				for (AghVersaoDocumento aghVersaoDocumento : listVersoes) {

					aghVersaoDocumento.setSituacao(DominioSituacaoVersaoDocumento.I);
					this.getCertificacaoDigitalFacade().atualizarAghVersaoDocumento(aghVersaoDocumento);
				}
			}
		}

	}

	private DominioSexoDeterminante converterSexoDeterminante(DominioSexo sexo) {
		DominioSexoDeterminante sexoDeterminante;

		if (sexo != null) {

			if (sexo.equals(DominioSexo.I)) {
				sexoDeterminante = DominioSexoDeterminante.Q;
			} else if (sexo.equals(DominioSexo.M)) {
				sexoDeterminante = DominioSexoDeterminante.M;
			} else {
				sexoDeterminante = DominioSexoDeterminante.F;
			}
			return sexoDeterminante;
		} else {
			return null;
		}
	}

	public List<VFatSsmInternacaoVO> obterListaVFatSssInternacao(Object param, AipPacientes paciente, Integer cidSeq) throws ApplicationBusinessException {
		Short vlrNumerico = obterParametroTabelaFaturamentoPadrao();
		DominioSexoDeterminante sexo = converterSexoDeterminante(paciente.getSexo());
		Integer caracteristica = obterCodigoFatTipoCatactItens();

		return this.getFaturamentoFacade().obterListaVFatSssInternacao(param, paciente.getIdade(), sexo, vlrNumerico, cidSeq, caracteristica);
	}

	public Long obterListaVFatSssInternacaoCount(Object param, AipPacientes paciente, Integer cidSeq) throws ApplicationBusinessException {
		Short vlrNumerico = obterParametroTabelaFaturamentoPadrao();
		DominioSexoDeterminante sexo = converterSexoDeterminante(paciente.getSexo());
		Integer caracteristica = obterCodigoFatTipoCatactItens();

		return this.getFaturamentoFacade().obterListaVFatSssInternacaoCount(param, paciente.getIdade(), sexo, vlrNumerico, cidSeq, caracteristica);
	}

	public List<AghCid> obterListaCidLaudoAih(Object param, AipPacientes paciente, Long codTabela) throws ApplicationBusinessException {
		Short vlrNumerico = obterParametroTabelaFaturamentoPadrao();
		DominioSexoDeterminante sexo = converterSexoDeterminante(paciente.getSexo());

		return this.getAghuFacade().listarCidPorIdadeSexoProcedimento(param, sexo, paciente.getIdade(), codTabela, vlrNumerico);
	}

	public Long obterListaCidLaudoAihCount(Object param, AipPacientes paciente, Long codTabela) throws ApplicationBusinessException {
		Short vlrNumerico = obterParametroTabelaFaturamentoPadrao();
		DominioSexoDeterminante sexo = converterSexoDeterminante(paciente.getSexo());

		return this.getAghuFacade().listarCidPorIdadeSexoProcedimentoCount(param, sexo, paciente.getIdade(), codTabela, vlrNumerico);
	}

	public List<AghCid> obterListaCidSecundarioLaudoAih(Object param, AipPacientes paciente, String cidCodigo) throws ApplicationBusinessException {
		Short vlrNumerico = obterParametroTabelaFaturamentoPadrao();
		DominioSexoDeterminante sexo = converterSexoDeterminante(paciente.getSexo());
		Integer caracteristica = obterCodigoFatTipoCatactItens();

		return this.getAghuFacade().listarCidSecundarioPorIdadeSexoProcedimento(param, sexo, paciente.getIdade(), cidCodigo, vlrNumerico, caracteristica);
	}

	public Long obterListaCidSecundarioLaudoAihCount(Object param, AipPacientes paciente, String cidCodigo) throws ApplicationBusinessException {
		Short vlrNumerico = obterParametroTabelaFaturamentoPadrao();
		DominioSexoDeterminante sexo = converterSexoDeterminante(paciente.getSexo());
		Integer caracteristica = obterCodigoFatTipoCatactItens();

		return this.getAghuFacade().listarCidSecundarioPorIdadeSexoProcedimentoCount(param, sexo, paciente.getIdade(), cidCodigo, vlrNumerico, caracteristica);

	}

	private Integer obterCodigoFatTipoCatactItens() throws ApplicationBusinessException {
		List<FatTipoCaractItens> listaCaracteristica = this.getFaturamentoFacade().listarTipoCaractItensPorCaracteristica(DominioFatTipoCaractItem.NAO_GERA_SLCT_LAUDO_AIH.getDescricao());
		return listaCaracteristica.get(0).getSeq();
	}

	private Short obterParametroTabelaFaturamentoPadrao() throws ApplicationBusinessException {
		AghParametros paramTabelaFaturPadrao = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
		return paramTabelaFaturPadrao.getVlrNumerico().shortValue();
	}
	
	public void atualizarIndImpressaoLaudoAIH(Long seq){
		MamLaudoAih laudoAih = mamLaudoAihDAO.obterPorChavePrimaria(seq);
		laudoAih.setIndImpresso(Boolean.TRUE);
		mamLaudoAihDAO.atualizar(laudoAih);
	}

	protected ICascaFacade getCascaFacade() {
		return this.iCascaFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return iFaturamentoFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return iAghuFacade;
	}

	protected IPacienteFacade getPacienteFacade() {
		return iPacienteFacade;
	}

	protected ICertificacaoDigitalFacade getCertificacaoDigitalFacade() {
		return this.iCertificacaoDigitalFacade;
	}

	protected ICascaFacade getICascaFacade() {
		return this.iCascaFacade;
	}

	protected LaudoAihRN getLaudoAihRN() {
		return laudoAihRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
}
