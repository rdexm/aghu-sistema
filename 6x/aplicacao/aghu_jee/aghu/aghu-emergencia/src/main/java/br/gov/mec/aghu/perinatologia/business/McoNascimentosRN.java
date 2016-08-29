package br.gov.mec.aghu.perinatologia.business;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.dominio.DominioRNClassificacaoNascimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.McoNascimentoJn;
import br.gov.mec.aghu.model.McoNascimentos;
import br.gov.mec.aghu.model.McoNascimentosId;
import br.gov.mec.aghu.model.McoPlacar;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.perinatologia.dao.McoNascimentoJnDAO;
import br.gov.mec.aghu.perinatologia.dao.McoNascimentosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoPlacarDAO;
import br.gov.mec.aghu.perinatologia.dao.McoRecemNascidosDAO;
import br.gov.mec.aghu.perinatologia.vo.DadosNascimentoVO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.util.EmergenciaParametrosColunas;
import br.gov.mec.aghu.util.EmergenciaParametrosEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.utils.DateUtil;
/**
 * @author israel.haas
 */
@Stateless
public class McoNascimentosRN extends BaseBusiness {

	private static final long serialVersionUID = -3423984755101821178L;

	@Inject @QualificadorUsuario
	private Usuario usuario;
	
	@Inject
	private McoNascimentosDAO mcoNascimentosDAO;
	
	@Inject
	private McoNascimentoJnDAO mcoNascimentoJnDAO;
	
	@Inject
	private McoPlacarDAO mcoPlacarDAO;
	
	@Inject
	private McoRecemNascidosDAO mcoRecemNascidosDAO;
	
	@Inject
	private McoPlacarRN mcoPlacarRN;
	
	@EJB
	McoRecemNascidosRN mcoRecemNascidosRN;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Override
	protected Log getLogger() {
		return null;
	}
	
	private enum McoNascimentosRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SERVICO_INDISPONIVEL, ERRO_CLASSIFICACAO_OBRIGATORIA, ERRO_PESONATIMORTO_MAIOR_ZERO, ERRO_PESOABORTO_MAIOR_ZERO,
		MENSAGEM_ERRO_OBTER_PARAMETRO, ERRO_DATA_NASCIMENTO_MAIOR_X_DIAS_ATRAS
	}
	
	public DadosNascimentoVO inserirNascimentos(DadosNascimentoVO nascimentoVO, Integer pacCodigo, Short gsoSeqp,
			Integer seqpNascimento, Short seqAnestesia) throws ApplicationBusinessException {
		
		McoNascimentos nascimento = this.preInserirMcoNascimentos(nascimentoVO, pacCodigo);
		
		McoNascimentosId id = new McoNascimentosId(pacCodigo, gsoSeqp, seqpNascimento);
		nascimento.setId(id);
		
		nascimento.setTipo(nascimentoVO.getTipoNascimento());
		nascimento.setModo(nascimentoVO.getModoNascimento());
		nascimento.setIndEpisotomia(null);
		nascimento.setPeriodoDilatacao(null);
		nascimento.setPeriodoExpulsivo(null);
		nascimento.setPesoPlacenta(nascimentoVO.getPesoPlacenta());
		nascimento.setCompCordao(nascimentoVO.getCordao());
		nascimento.setTanSeq(seqAnestesia);
		nascimento.setEqpSeq(null);
		nascimento.setRnClassificacao(nascimentoVO.getClassificacao());
		nascimento.setApresentacao(nascimentoVO.getApresentacao());
		nascimento.setPesoNamAbo(nascimentoVO.getPesoAborto());
		nascimento.setObservacao(null);
		nascimento.setIndAcompanhante(null);
		
		
		this.mcoNascimentosDAO.persistir(nascimento);
		
		nascimentoVO.setSeqp(seqpNascimento);
		
		this.posInclusaoNascimentos(nascimento, pacCodigo, gsoSeqp);
		
		return nascimentoVO;
	}
	
	/**
	 * @ORADB MCO_NASCIMENTOS.MCOT_NAS_BRI
	 * @param nascimentoVO
	 * @param pacCodigo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public McoNascimentos preInserirMcoNascimentos(DadosNascimentoVO nascimentoVO, Integer pacCodigo) throws ApplicationBusinessException {
		
		McoNascimentos nascimento = new McoNascimentos();
		nascimento.setCriadoEm(new Date());
		nascimento.setSerMatricula(usuario.getMatricula());
		nascimento.setSerVinCodigo(usuario.getVinculo());
		
		this.verificarGestante(pacCodigo);
		this.verificarAborto(nascimentoVO);
		nascimento.setDthrNascimento(nascimentoVO.getDtHrNascimento());
		validarDataNascimento(nascimento);
		
		
		return nascimento;
	}
	
	/**
	 * @ORADB MCOK_NAS_RN.RN_NASP_ATU_PLACAR
	 * @param pacCodigo
	 * @throws ApplicationBusinessException 
	 */
	public void verificarGestante(Integer pacCodigo) throws ApplicationBusinessException {
		McoPlacar placar = this.mcoPlacarDAO.buscarPlacar(pacCodigo);
		if (placar != null) {
			McoPlacar placarOriginal = this.mcoPlacarDAO.obterOriginal(placar.getSeq());
			placar.setIndSituacao(DominioSituacao.I);
			this.mcoPlacarRN.atualizarPlacar(placar, placarOriginal);
		}
	}
	
	/**
	 * @ORADB MCOK_NAS_RN.RN_NASP_VER_ABORTO
	 * @param nascimentoVO
	 * @throws ApplicationBusinessException
	 */
	public void verificarAborto(DadosNascimentoVO nascimentoVO) throws ApplicationBusinessException {
		if (nascimentoVO.getClassificacao() == null) {
			throw new ApplicationBusinessException(McoNascimentosRNExceptionCode.ERRO_CLASSIFICACAO_OBRIGATORIA);
			
		} else if (nascimentoVO.getClassificacao().equals(DominioRNClassificacaoNascimento.NAM)) {
			if (nascimentoVO.getPesoAborto() == null || nascimentoVO.getPesoAborto().intValue() == 0) {
				throw new ApplicationBusinessException(McoNascimentosRNExceptionCode.ERRO_PESONATIMORTO_MAIOR_ZERO);
			}
		} else if (nascimentoVO.getClassificacao().equals(DominioRNClassificacaoNascimento.ABO)) {
			if (nascimentoVO.getPesoAborto() == null || nascimentoVO.getPesoAborto().intValue() == 0) {
				throw new ApplicationBusinessException(McoNascimentosRNExceptionCode.ERRO_PESOABORTO_MAIOR_ZERO);
			}
		}
	}
	
	public void atualizarMcoNascimento(McoNascimentos mcoNascimentos, McoNascimentos mcoNascimentosOriginal) {
		// @ORADB MCO_NASCIMENTOS.MCOT_NAS_BRU
		mcoNascimentos.setSerVinCodigo(usuario.getVinculo());
		mcoNascimentos.setSerMatricula(usuario.getMatricula());
		
		if (isMcoNascimentosAlterado(mcoNascimentos, mcoNascimentosOriginal)) {
			this.inserirJournalMcoNascimentos(mcoNascimentosOriginal, DominioOperacoesJournal.UPD);
		}
		
		this.mcoNascimentosDAO.merge(mcoNascimentos);
		
	}
	
	public void atualizarNascimentos(DadosNascimentoVO nascimentoVO, Integer pacCodigo, Short gsoSeqp,
			Short seqAnestesia) throws ApplicationBusinessException {
		
		McoNascimentosId id = new McoNascimentosId(pacCodigo, gsoSeqp, nascimentoVO.getSeqp());
		McoNascimentos nascimentoOriginal = this.mcoNascimentosDAO.obterOriginal(id);
		
		McoNascimentos nascimento = this.preAtualizarNascimentos(nascimentoVO, pacCodigo, gsoSeqp);
		
		nascimento.setDthrNascimento(nascimentoVO.getDtHrNascimento());
		nascimento.setTipo(nascimentoVO.getTipoNascimento());
		nascimento.setModo(nascimentoVO.getModoNascimento());
		nascimento.setTanSeq(seqAnestesia);
		nascimento.setRnClassificacao(nascimentoVO.getClassificacao());
		nascimento.setApresentacao(nascimentoVO.getApresentacao());
		nascimento.setPesoPlacenta(nascimentoVO.getPesoPlacenta());
		nascimento.setCompCordao(nascimentoVO.getCordao());
		nascimento.setPesoNamAbo(nascimentoVO.getPesoAborto());
		
		if (isMcoNascimentosAlterado(nascimento, nascimentoOriginal)) {
			this.inserirJournalMcoNascimentos(nascimentoOriginal, DominioOperacoesJournal.UPD);
		}
		
		this.mcoNascimentosDAO.merge(nascimento);
		
		this.posAtualizarNascimentos(nascimento, nascimentoOriginal, pacCodigo, gsoSeqp);
	}
	
	/**
	 * @ORADB MCO_NASCIMENTOS.MCOT_NAS_BRU
	 * @param nascimentoVO
	 * @param pacCodigo
	 * @param gsoSeqp
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public McoNascimentos preAtualizarNascimentos(DadosNascimentoVO nascimentoVO, Integer pacCodigo,
			Short gsoSeqp) throws ApplicationBusinessException {
		
		McoNascimentosId id = new McoNascimentosId(pacCodigo, gsoSeqp, nascimentoVO.getSeqp());
		McoNascimentos nascimento = this.mcoNascimentosDAO.obterPorChavePrimaria(id);
		
		nascimento.setSerVinCodigo(usuario.getVinculo());
		nascimento.setSerMatricula(usuario.getMatricula());
		this.verificarAborto(nascimentoVO);
		//this.verificarGestante(pacCodigo);
		return nascimento;
	}
	
	/**
	 * @ORADB MCO_NASCIMENTOS.MCOT_NAS_ASU
	 * @ORADB MCOP_ENFORCE_NAS_RULES
	 * @ORADB MCOK_NAS_RN.RN_NASP_VER_DT_NASC
	 * @param nascimento
	 * @param nascimentoOriginal
	 * @param pacCodigo
	 * @param gsoSeqp
	 * @throws ApplicationBusinessException
	 */
	public void posAtualizarNascimentos(McoNascimentos nascimento, McoNascimentos nascimentoOriginal,
			Integer pacCodigo, Short gsoSeqp) throws ApplicationBusinessException {
		
		if (CoreUtil.modificados(nascimento.getDthrNascimento(), nascimentoOriginal.getDthrNascimento())) {
			validarDataNascimento(nascimento);

			McoRecemNascidos recemNascido = this.mcoRecemNascidosDAO.obterMcoRecemNascidosPorId(pacCodigo, gsoSeqp, nascimento.getId().getSeqp());
			if (recemNascido != null && !DateUtil.isDatasIguais(recemNascido.getDthrNascimento(), nascimento.getDthrNascimento())) {
				McoRecemNascidos recemNascidoOriginal = this.mcoRecemNascidosDAO.obterOriginal(recemNascido);
				recemNascido.setDthrNascimento(nascimento.getDthrNascimento());
				mcoRecemNascidosRN.atualizarRecemNascido(recemNascido, recemNascidoOriginal, true);
			}
			
		}
	}
	
	/**
	 * @ORADB MCO_NASCIMENTOS.MCOT_NAS_ASU
	 * @ORADB MCOP_ENFORCE_NAS_RULES
	 * @ORADB MCOK_NAS_RN.RN_NASP_VER_DT_NASC
	 * @param nascimento
	 * @param pacCodigo
	 * @param gsoSeqp
	 * @throws ApplicationBusinessException
	 */
	public void posInclusaoNascimentos(McoNascimentos nascimento, Integer pacCodigo, Short gsoSeqp) throws ApplicationBusinessException {
		McoRecemNascidos recemNascido = this.mcoRecemNascidosDAO.obterMcoRecemNascidosPorId(pacCodigo, gsoSeqp, nascimento.getId().getSeqp());
		if (recemNascido != null && !DateUtil.isDatasIguais(recemNascido.getDthrNascimento(), nascimento.getDthrNascimento())) {
			McoRecemNascidos recemNascidoOriginal = this.mcoRecemNascidosDAO.obterOriginal(recemNascido);
			recemNascido.setDthrNascimento(nascimento.getDthrNascimento());
			mcoRecemNascidosRN.atualizarRecemNascido(recemNascido, recemNascidoOriginal, true);
		}
	}

	private void validarDataNascimento(McoNascimentos nascimento)
			throws ApplicationBusinessException {
		Object pDiasConsisteData = this.buscarParametroPorNome(EmergenciaParametrosEnum.P_DIAS_CONSISTE_DATA.toString(), EmergenciaParametrosColunas.VLR_NUMERICO.toString());
		Integer diasConsisteData = null;
		if (pDiasConsisteData != null) {
			diasConsisteData = ((BigDecimal) pDiasConsisteData).intValue();
		}
		
		Date dataCalculada = DateUtil.adicionaDias(new Date(), -diasConsisteData);

		// Colocar #TODO para quando for chamada pela procedure “@ORADB AIPP_SUBS_PAC_GESTA” incluir o seguinte teste:
		// Incluir no #TODO: Se a origem da chamada da rotina for diferente da procedure “@ORADB AIPP_SUBS_PAC_GESTA” efetuar o seguinte
		// teste:

		// TODO #OBS na trigger: Quando vem da procedure aipp_subs_pac_gesta não deve testar esta regra
		if (DateUtil.validaDataMaior(nascimento.getDthrNascimento(), new Date()) || DateUtil.validaDataMenor(nascimento.getDthrNascimento(), dataCalculada)) {
			throw new ApplicationBusinessException(McoNascimentosRNExceptionCode.ERRO_DATA_NASCIMENTO_MAIOR_X_DIAS_ATRAS, diasConsisteData);
		}
	}
	
	public void excluirNascimentos(DadosNascimentoVO nascimentoVO, Integer pacCodigo,
			Short gsoSeqp) throws ApplicationBusinessException {
		
		McoNascimentosId id = new McoNascimentosId(pacCodigo, gsoSeqp, nascimentoVO.getSeqp());
		McoNascimentos nascimentoOriginal = this.mcoNascimentosDAO.obterOriginal(id);
		McoNascimentos nascimento = this.mcoNascimentosDAO.obterPorChavePrimaria(id);
		
		this.inserirJournalMcoNascimentos(nascimentoOriginal, DominioOperacoesJournal.DEL);
		
		this.mcoNascimentosDAO.remover(nascimento);
	}
	
	/**
	 * @ORADB MCO_NASCIMENTOS.MCOT_NAS_ARU
	 * @ORADB MCO_NASCIMENTOS.MCOT_NAS_ARD
	 * @param nascimentoOriginal
	 * @param operacao
	 */
	public void inserirJournalMcoNascimentos(McoNascimentos nascimentoOriginal, DominioOperacoesJournal operacao) {
		
		McoNascimentoJn mcoNascimentoJn = BaseJournalFactory.getBaseJournal(operacao, McoNascimentoJn.class, usuario.getLogin());
		
		mcoNascimentoJn.setCriadoEm(nascimentoOriginal.getCriadoEm());
		mcoNascimentoJn.setSerMatricula(nascimentoOriginal.getSerMatricula());
		mcoNascimentoJn.setSerVinCodigo(nascimentoOriginal.getSerVinCodigo());
		
		mcoNascimentoJn.setSeqp(nascimentoOriginal.getId().getSeqp());
		
		
		mcoNascimentoJn.setTipo(nascimentoOriginal.getTipo());
		mcoNascimentoJn.setModo(nascimentoOriginal.getModo());
		mcoNascimentoJn.setIndEpisotomia(nascimentoOriginal.getIndEpisotomia());
		mcoNascimentoJn.setPeriodoDilatacao(nascimentoOriginal.getPeriodoDilatacao());
		mcoNascimentoJn.setPeriodoExpulsivo(nascimentoOriginal.getPeriodoExpulsivo());
		mcoNascimentoJn.setPesoPlacenta(nascimentoOriginal.getPesoPlacenta());
		mcoNascimentoJn.setCompCordao(nascimentoOriginal.getCompCordao());
		mcoNascimentoJn.setDthrNascimento(nascimentoOriginal.getDthrNascimento());
		mcoNascimentoJn.setGsoSeqp(nascimentoOriginal.getId().getGsoSeqp());
		mcoNascimentoJn.setGsoPacCodigo(nascimentoOriginal.getId().getGsoPacCodigo());
		mcoNascimentoJn.setTanSeq(nascimentoOriginal.getTanSeq());
		mcoNascimentoJn.setEqpSeq(nascimentoOriginal.getEqpSeq());
		mcoNascimentoJn.setRnClassificacao(nascimentoOriginal.getRnClassificacao());
		mcoNascimentoJn.setApresentacao(nascimentoOriginal.getApresentacao());
		mcoNascimentoJn.setPesoNamAbo(nascimentoOriginal.getPesoNamAbo());
		mcoNascimentoJn.setObservacao(nascimentoOriginal.getObservacao());
		mcoNascimentoJn.setIndAcompanhante(nascimentoOriginal.getIndAcompanhante());
		
		this.mcoNascimentoJnDAO.persistir(mcoNascimentoJn);
	}
	
	public boolean isMcoNascimentosAlterado(McoNascimentos nascimento, McoNascimentos nascimentoOriginal) {
		return CoreUtil.modificados(nascimento.getDthrNascimento(), nascimentoOriginal.getDthrNascimento()) ||
				CoreUtil.modificados(nascimento.getTipo(), nascimentoOriginal.getTipo()) ||
				CoreUtil.modificados(nascimento.getModo(), nascimentoOriginal.getModo()) ||
				CoreUtil.modificados(nascimento.getTanSeq(), nascimentoOriginal.getTanSeq()) ||
				CoreUtil.modificados(nascimento.getRnClassificacao(), nascimentoOriginal.getRnClassificacao()) ||
				CoreUtil.modificados(nascimento.getApresentacao(), nascimentoOriginal.getApresentacao()) ||
				CoreUtil.modificados(nascimento.getPesoPlacenta(), nascimentoOriginal.getPesoPlacenta()) ||
				CoreUtil.modificados(nascimento.getCompCordao(), nascimentoOriginal.getCompCordao()) ||
				CoreUtil.modificados(nascimento.getPesoNamAbo(), nascimentoOriginal.getPesoNamAbo()) ||
				CoreUtil.modificados(nascimento.getSerVinCodigo(), nascimentoOriginal.getSerVinCodigo()) ||
				CoreUtil.modificados(nascimento.getSerMatricula(), nascimentoOriginal.getSerMatricula()) ||
				CoreUtil.modificados(nascimento.getIndAcompanhante(), nascimentoOriginal.getIndAcompanhante()) ||
				CoreUtil.modificados(nascimento.getObservacao(), nascimentoOriginal.getObservacao()) ||
				CoreUtil.modificados(nascimento.getEqpSeq(), nascimentoOriginal.getEqpSeq()) ||
				CoreUtil.modificados(nascimento.getSciUnfSeq(), nascimentoOriginal.getSciUnfSeq()) ||
				CoreUtil.modificados(nascimento.getSciSeqp(), nascimentoOriginal.getSciSeqp());
				
	}
	
	private Object buscarParametroPorNome(String nome, String coluna) throws ApplicationBusinessException {
		Object retorno = null;
		try {
			retorno = parametroFacade.obterAghParametroPorNome(nome, coluna);
			if (retorno == null) {
				throw new ApplicationBusinessException(McoNascimentosRNExceptionCode.MENSAGEM_ERRO_OBTER_PARAMETRO, nome);
			}
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(McoNascimentosRNExceptionCode.MENSAGEM_ERRO_OBTER_PARAMETRO, nome);
		}
		return retorno;
	}
}
