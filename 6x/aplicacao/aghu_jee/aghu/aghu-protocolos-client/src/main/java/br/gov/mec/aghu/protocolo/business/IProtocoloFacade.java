package br.gov.mec.aghu.protocolo.business;

import java.io.Serializable;
import java.util.List;

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
import br.gov.mec.aghu.protocolos.vo.CadIntervaloTempoVO;

public interface IProtocoloFacade extends Serializable {

	List<MpaCadOrdItemHemoter> pesquisarMpaCadOrdItemHemoterPorCodigoProcedimentoHemoterapico(
			String codigoProcedimentoHemoterapico);

	List<MpaUsoOrdItemHemoters> pesquisarMpaUsoOrdItemHemotersPorPheCodigo(
			String codigoProcedimentoHemoterapico);

	List<MpaPops> pesquisarProtocoloLiberado(AghEspecialidades especialidade,
			AelExames exame);
	

	/**
	 * Método que pesquisa usoOrdProcedimentos pelo medicamento da prescrição
	 * @param seq
	 * @param atdSeq
	 * @return
	 */
	public List<MpaUsoOrdProcedimento> pesquisarUsoOrdProcedimentosPorPrescricaoMedicamento(Long seq, Integer atdSeq);
	
	/**
	 * Método que pesquisa UsosOrdMdtos por medicamento
	 * @param atdSeq
	 * @param seq
	 * @return
	 */
	public List<MpaUsoOrdMdto> pesquisarUsosOrdMdtos(Integer atdSeq, Long seq);
	
	public List<MpaUsoOrdCuidado> pesquisarUsoOrdCuidadosPorPrescricaoCuidado(Long pcuSeqAnt, Integer pmeAtdSeq);
	
	public List<MpaUsoOrdNutricao> pesquisarUsoOrdNutricoesPorPrescricaoDieta(Long pdtSeqAnt, Integer pmeAtdSeq);

	public Long consultarSessoesQuimioterapiaCount(Integer seqTratamentoTerQuimio);

	public List<MpaProtocoloAssistencial> consultarSessoesQuimioterapia(
			Integer seqTratamentoTerQuimio, Integer firstResult,
			Integer maxResult);
	
	List<MpaVersaoProtAssistencial> listarProtocolosAssistenciais(String param);
	
	Long listarProtocolosAssistenciaisCount(String param);
	
	List<CadIntervaloTempoVO> listarIntervalosTempo(Short vpaSeqp, Short vpaPtaSeq);
	
	MpaVersaoProtAssistencial obterMpaVersaoProtAssistencialPorId(MpaVersaoProtAssistencialId id);
}