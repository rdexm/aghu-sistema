package br.gov.mec.aghu.protocolo.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MpaCadOrdItemHemoter;
import br.gov.mec.aghu.model.MpaPops;
import br.gov.mec.aghu.model.MpaProtocoloAssistencial;
import br.gov.mec.aghu.model.MpaUsoOrdCuidado;
import br.gov.mec.aghu.model.MpaUsoOrdItemHemoters;
import br.gov.mec.aghu.model.MpaUsoOrdMdto;
import br.gov.mec.aghu.model.MpaUsoOrdNutricao;
import br.gov.mec.aghu.model.MpaUsoOrdProcedimento;
import br.gov.mec.aghu.model.MpaVersaoProtAssistencial;
import br.gov.mec.aghu.model.MpaVersaoProtAssistencialId;
import br.gov.mec.aghu.protocolos.dao.MpaCadOrdItemHemoterDao;
import br.gov.mec.aghu.protocolos.dao.MpaPopsDAO;
import br.gov.mec.aghu.protocolos.dao.MpaProtocoloAssistencialDAO;
import br.gov.mec.aghu.protocolos.dao.MpaUsoOrdCuidadoDAO;
import br.gov.mec.aghu.protocolos.dao.MpaUsoOrdItemHemotersDao;
import br.gov.mec.aghu.protocolos.dao.MpaUsoOrdMdtoDAO;
import br.gov.mec.aghu.protocolos.dao.MpaUsoOrdModoProcedDAO;
import br.gov.mec.aghu.protocolos.dao.MpaUsoOrdNutricaoDAO;
import br.gov.mec.aghu.protocolos.dao.MpaUsoOrdProcedimentoDAO;
import br.gov.mec.aghu.protocolos.dao.MpaVersaoProtAssistencialDAO;
import br.gov.mec.aghu.protocolos.vo.CadIntervaloTempoVO;

/**
 * Classe de fachada que disponibiliza uma interface para as funcionalidades do
 * módulo MPA - Sistema de Protocolos.
 */


@Modulo(ModuloEnum.PROTOCOLO)
@Stateless
public class ProtocoloFacade extends BaseFacade implements IProtocoloFacade {

@Inject
private MpaUsoOrdMdtoDAO mpaUsoOrdMdtoDAO;

@Inject
private MpaUsoOrdNutricaoDAO mpaUsoOrdNutricaoDAO;

@Inject
private MpaPopsDAO mpaPopsDAO;

@Inject
private MpaUsoOrdModoProcedDAO mpaUsoOrdModoProcedDAO;

@Inject
private MpaCadOrdItemHemoterDao mpaCadOrdItemHemoterDao;

@Inject
private MpaUsoOrdCuidadoDAO mpaUsoOrdCuidadoDAO;

@Inject
private MpaUsoOrdItemHemotersDao mpaUsoOrdItemHemotersDao;

@Inject
private MpaProtocoloAssistencialDAO mpaProtocoloAssistencialDAO;

@Inject
private MpaUsoOrdProcedimentoDAO mpaUsoOrdProcedimentoDAO;

@Inject
private MpaVersaoProtAssistencialDAO mpaVersaoProtAssistencialDAO;

@Inject
private MpaCadIntervaloTempoON mpaCadIntervaloTempoON;


	private static final long serialVersionUID = -4653902507829290350L;

	protected MpaCadOrdItemHemoterDao getMpaCadOrdItemHemoterDao() {
		return mpaCadOrdItemHemoterDao;
	}

	protected MpaUsoOrdItemHemotersDao getMpaUsoOrdItemHemotersDao() {
		return mpaUsoOrdItemHemotersDao;
	}
	
	protected MpaPopsDAO getMpaPopsDAO() {
		return mpaPopsDAO;
	}

	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.protocolo.business.IProtocoloFacade#pesquisarMpaCadOrdItemHemoterPorCodigoProcedimentoHemoterapico(java.lang.String)
	 */
	@Override
	public List<MpaCadOrdItemHemoter> pesquisarMpaCadOrdItemHemoterPorCodigoProcedimentoHemoterapico(String codigoProcedimentoHemoterapico) {
		return getMpaCadOrdItemHemoterDao().pesquisarMpaCadOrdItemHemoterPorCodigoProcedimentoHemoterapico(codigoProcedimentoHemoterapico);		
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.protocolo.business.IProtocoloFacade#pesquisarMpaUsoOrdItemHemotersPorPheCodigo(java.lang.String)
	 */
	@Override
	public List<MpaUsoOrdItemHemoters> pesquisarMpaUsoOrdItemHemotersPorPheCodigo(String codigoProcedimentoHemoterapico) {
		return getMpaUsoOrdItemHemotersDao().pesquisarMpaUsoOrdItemHemotersPorPheCodigo(codigoProcedimentoHemoterapico);
	}

	@Override
	@BypassInactiveModule
	public List<MpaPops> pesquisarProtocoloLiberado(AghEspecialidades especialidade, AelExames exame) {
		return getMpaPopsDAO().pesquisarProtocoloLiberado(especialidade, exame);
	}
	
	protected MpaUsoOrdProcedimentoDAO getMpaUsoOrdProcedimentoDAO() {
		return mpaUsoOrdProcedimentoDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MpaUsoOrdProcedimento> pesquisarUsoOrdProcedimentosPorPrescricaoMedicamento(
			Long seq, Integer atdSeq) {
		return getMpaUsoOrdProcedimentoDAO().pesquisarUsoOrdProcedimentosPorPrescricaoMedicamento(seq, atdSeq);
	}

	protected MpaUsoOrdModoProcedDAO getMpaUsoOrdModoProcedDAO() {
		return mpaUsoOrdModoProcedDAO;
	}

	protected MpaUsoOrdMdtoDAO getMpaUsoOrdMdtoDAO() {
		return mpaUsoOrdMdtoDAO;
	}

	protected MpaUsoOrdCuidadoDAO getMpaUsoOrdCuidadoDAO(){
		return mpaUsoOrdCuidadoDAO;
	}
	
	protected MpaUsoOrdNutricaoDAO getMpaUsoOrdNutricaoDAO(){
		return mpaUsoOrdNutricaoDAO;
	}
	
	protected MpaProtocoloAssistencialDAO getMpaProtocoloAssistencialDAO(){
		return mpaProtocoloAssistencialDAO;
	}

	@Override
	@BypassInactiveModule
	public List<MpaUsoOrdMdto> pesquisarUsosOrdMdtos(Integer atdSeq, Long seq) {
		return getMpaUsoOrdMdtoDAO().pesquisarUsosOrdMdtos(atdSeq, seq);
	}

	@Override
	@BypassInactiveModule
	public List<MpaUsoOrdCuidado> pesquisarUsoOrdCuidadosPorPrescricaoCuidado(
			Long pcuSeqAnt, Integer pmeAtdSeq) {		
		return getMpaUsoOrdCuidadoDAO().pesquisarUsoOrdCuidadosPorPrescricaoCuidado(pcuSeqAnt, pmeAtdSeq);
	}

	@Override
	@BypassInactiveModule
	public List<MpaUsoOrdNutricao> pesquisarUsoOrdNutricoesPorPrescricaoDieta(
			Long pdtSeqAnt, Integer pmeAtdSeq) {
		return getMpaUsoOrdNutricaoDAO().pesquisarUsoOrdNutricoesPorPrescricaoDieta(pdtSeqAnt, pmeAtdSeq);
	}
	
	@Override
	@BypassInactiveModule
	public Long consultarSessoesQuimioterapiaCount(Integer seqTratamentoTerQuimio) {
		return getMpaProtocoloAssistencialDAO().pesquisarMpaProtocoloAssistencialCount(seqTratamentoTerQuimio);
	}

	@Override
	@BypassInactiveModule
	public List<MpaProtocoloAssistencial> consultarSessoesQuimioterapia(
			Integer seqTratamentoTerQuimio, Integer firstResult,
			Integer maxResult) {
		return getMpaProtocoloAssistencialDAO().pesquisarMpaProtocoloAssistencial(seqTratamentoTerQuimio, firstResult, maxResult);
	}
	
	@Override
	public List<MpaVersaoProtAssistencial> listarProtocolosAssistenciais(String param) {
		return this.mpaVersaoProtAssistencialDAO.listarProtocolosAssistenciais(param);
	}
	
	@Override
	public Long listarProtocolosAssistenciaisCount(String param) {
		return this.mpaVersaoProtAssistencialDAO.listarProtocolosAssistenciaisCount(param);
	}
	
	@Override
	public List<CadIntervaloTempoVO> listarIntervalosTempo(Short vpaSeqp, Short vpaPtaSeq) {
		return this.mpaCadIntervaloTempoON.listarIntervalosTempo(vpaSeqp, vpaPtaSeq);
	}
	
	@Override
	public MpaVersaoProtAssistencial obterMpaVersaoProtAssistencialPorId(MpaVersaoProtAssistencialId id) {
		return this.mpaVersaoProtAssistencialDAO.obterPorChavePrimaria(id);
	}
	
}
